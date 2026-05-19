package shcm.shsupercm.fabric.citresewn;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import shcm.shsupercm.fabric.citresewn.cit.*;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;
import shcm.shsupercm.fabric.citresewn.platform.Platform;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Logic for the /citresewn client command. Only enabled when Fabric API is present.<br>
 * Structure:
 * <pre>
 * /citresewn - General info command
 * /citresewn config - Opens the config gui(only when Cloth Config is present)
 * /citresewn analyze pack &lt;pack&gt; - Displays data for the given loaded cit pack.
 * </pre>
 */
public class CITResewnCommand {
    /**
     * @see shcm.shsupercm.fabric.citresewn.mixin.ChatScreenMixin
     */
    public static boolean openConfig = false;

    /**
     * Registers all of CIT Resewn's commands.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void register(RegisterClientCommandsEvent event) {
        event.getDispatcher().register((LiteralArgumentBuilder) LiteralArgumentBuilder.literal("citresewn").executes(context -> {
                    feedback(Component.literal("CIT Resewn v" + Platform.getModVersion("citresewn") + ":"));
                    feedback(Component.literal("  Registered: " + CITRegistry.TYPES.values().stream().distinct().count() + " types and " + CITRegistry.CONDITIONS.values().stream().distinct().count() + " conditions"));

                    final boolean active = CITResewnConfig.INSTANCE.enabled && ActiveCITs.isActive();
                    feedback(Component.literal("  Active: " + (active ? "yes" : ("no, " + (CITResewnConfig.INSTANCE.enabled ? "no cit packs loaded" : "disabled in config")))));
                    if (active) {
                        feedback(Component.literal("   Loaded: " + ActiveCITs.getActive().cits.values().stream().mapToLong(Collection::size).sum() + " CITs from " + ActiveCITs.getActive().cits.values().stream().flatMap(Collection::stream).map(cit -> cit.packName).distinct().count() + " resourcepacks"));
                    }
                    feedback(Component.literal(""));

                    return 1;
                })
                .then(LiteralArgumentBuilder.literal("config")
                        .executes(context -> { //citresewn config
                            openConfig = true;

                            return 1;
                        }))
                .then(LiteralArgumentBuilder.literal("analyze")
                        .then(LiteralArgumentBuilder.literal("pack")
                                .then(RequiredArgumentBuilder.argument("pack", new LoadedCITPackArgument())
                                        .executes(context -> { //citresewn analyze <pack>
                                            final String pack = context.getArgument("pack", String.class);
                                            if (ActiveCITs.isActive()) {
                                                feedback(Component.literal("Analyzed CIT data of \"" + pack + "\u00a7r\":"));

                                                List<Component> builder = new ArrayList<>();

                                                for (Map.Entry<PropertyKey, Set<PropertyValue>> entry : ActiveCITs.getActive().globalProperties.properties.entrySet())
                                                    for (PropertyValue value : entry.getValue())
                                                        if (value.packName().equals(pack))
                                                            builder.add(Component.literal("  " + entry.getKey().toString() + (value.keyMetadata() == null ? "" : "." + value.keyMetadata()) + " = " + value.value()));
                                                if (!builder.isEmpty()) {
                                                    feedback(Component.literal(" Global Properties:"));
                                                    for (Component text : builder)
                                                        feedback(text);

                                                    builder.clear();
                                                }

                                                for (Map.Entry<Class<? extends CITType>, List<CIT<?>>> entry : ActiveCITs.getActive().cits.entrySet())
                                                    if (!entry.getValue().isEmpty()) {
                                                        long count = entry.getValue().stream().filter(cit -> cit.packName.equals(pack)).count();
                                                        if (count > 0)
                                                            builder.add(Component.literal("  " + CITRegistry.idOfType(entry.getKey()).toString() + " = " + count));
                                                    }
                                                if (!builder.isEmpty()) {
                                                    feedback(Component.literal(" Types:"));
                                                    for (Component text : builder)
                                                        feedback(text);

                                                    builder.clear();
                                                }

                                                List<CITCondition> conditions = ActiveCITs.getActive().cits.values().stream()
                                                        .flatMap(Collection::stream)
                                                        .filter(cit -> cit.packName.equals(pack))
                                                        .flatMap(cit -> Arrays.stream(cit.conditions))
                                                        .toList();
                                                if (!conditions.isEmpty())
                                                    feedback(Component.literal(" Utilizing " + conditions.size() + " conditions(" + conditions.stream().map(Object::getClass).distinct().count() + " unique condition types)"));
                                            } else
                                                feedback(Component.literal("Not active"));

                                            return 1;
                                        })
                                )
                        )
                  )
            );
    }

    private static void feedback(Component message) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gui != null)
            minecraft.gui.getChat().addMessage(message);
    }

    /**
     * Greedy string argument that is limited to cit pack names loaded in {@link shcm.shsupercm.fabric.citresewn.cit.ActiveCITs}.
     */
    private static class LoadedCITPackArgument implements ArgumentType<String> {
        @Override
        public String parse(StringReader reader) throws CommandSyntaxException {
            StringBuilder builder = new StringBuilder();
            while (reader.canRead())
                builder.append(reader.read());

            String pack = builder.toString().trim();

            if (!getPacks().contains(pack)) {
                LiteralMessage message = new LiteralMessage("Could not find CIT pack");
                throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
            }

            return pack;
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CompletableFuture.supplyAsync(() -> {
                for (String pack : getPacks()) {
                    builder.suggest(pack);
                }
                return builder.build();
            });
        }

        private static Set<String> getPacks() {
            if (ActiveCITs.isActive())
                return ActiveCITs.getActive().cits.values().stream()
                        .flatMap(Collection::stream)
                        .map(cit -> cit.packName)
                        .collect(Collectors.toSet());
            else
                return Collections.emptySet();
        }
    }
}
