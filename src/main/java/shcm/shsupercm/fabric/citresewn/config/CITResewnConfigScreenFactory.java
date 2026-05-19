package shcm.shsupercm.fabric.citresewn.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import shcm.shsupercm.fabric.citresewn.platform.Platform;

import java.util.function.Function;

/**
 * Cloth Config integration to CIT Resewn's config
 * @see CITResewnConfig
 */
public class CITResewnConfigScreenFactory {
    /**
     * Used to get CIT Resewn - Defaults's Cloth Config implementation.
     */
    public static final String DEFAULTS_CONFIG_ENTRYPOINT = "citresewn-defaults:config_screen";

    /**
     * Creates a Cloth Config screen for the current active config instance.
     * @param parent parent to return to from the config screen
     * @return the config screen
     * @throws NoClassDefFoundError if Cloth Config is not present
     */
    public static Screen create(Screen parent) {
        CITResewnConfig currentConfig = CITResewnConfig.INSTANCE, defaultConfig = new CITResewnConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.citresewn.title"))
                .setSavingRunnable(currentConfig::write);

        ConfigCategory category = builder.getOrCreateCategory(Component.empty());
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.citresewn.enabled.title"), currentConfig.enabled)
                .setTooltip(Component.translatable("config.citresewn.enabled.tooltip"))
                .setSaveConsumer(newConfig -> {
                    if (currentConfig.enabled != newConfig) {
                        currentConfig.enabled = newConfig;
                        Minecraft.getInstance().reloadResourcePacks();
                    }
                })
                .setDefaultValue(defaultConfig.enabled)
                .build());

        if (Platform.isModLoaded("citresewn-defaults")) {
            class CurrentScreen { boolean prevToggle = false; } final CurrentScreen currentScreen = new CurrentScreen();
            category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.citresewn-defaults.title"), false)
                    .setTooltip(Component.translatable("config.citresewn-defaults.tooltip"))

                    .setYesNoTextSupplier((b) -> {
                        if (b != currentScreen.prevToggle) {
                            Function<Screen, Screen> defaultsFactory = getDefaultsFactoryReflectively();
                            if (defaultsFactory != null)
                                Minecraft.getInstance().setScreenAndShow((Screen) defaultsFactory.apply(create(parent)));

                            currentScreen.prevToggle = b;
                        }

                        return Component.translatable("config.citresewn.configure");
                    })
                    .build());
        }

        category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.citresewn.mute_errors.title"), currentConfig.mute_errors)
                .setTooltip(Component.translatable("config.citresewn.mute_errors.tooltip"))
                .setSaveConsumer(newConfig -> currentConfig.mute_errors = newConfig)
                .setDefaultValue(defaultConfig.mute_errors)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.citresewn.mute_warns.title"), currentConfig.mute_warns)
                .setTooltip(Component.translatable("config.citresewn.mute_warns.tooltip"))
                .setSaveConsumer(newConfig -> currentConfig.mute_warns = newConfig)
                .setDefaultValue(defaultConfig.mute_warns)
                .build());

        category.addEntry(entryBuilder.startIntSlider(Component.translatable("config.citresewn.cache_ms.title"), currentConfig.cache_ms / 50, 0, 5 * 20)
                .setTooltip(Component.translatable("config.citresewn.cache_ms.tooltip"))
                .setSaveConsumer(newConfig -> currentConfig.cache_ms = newConfig * 50)
                .setDefaultValue(defaultConfig.cache_ms / 50)
                .setTextGetter(ticks -> {
                    if (ticks <= 1)
                        return Component.translatable("config.citresewn.cache_ms.ticks." + ticks).withStyle(ChatFormatting.AQUA);

                    ChatFormatting color = ChatFormatting.DARK_RED;

                    if (ticks <= 40) color = ChatFormatting.RED;
                    if (ticks <= 20) color = ChatFormatting.GOLD;
                    if (ticks <= 10) color = ChatFormatting.DARK_GREEN;
                    if (ticks <= 5) color = ChatFormatting.GREEN;

                    return Component.translatable("config.citresewn.cache_ms.ticks.any", ticks).withStyle(color);
                })
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.citresewn.broken_paths.title"), currentConfig.broken_paths)
                .setTooltip(Component.translatable("config.citresewn.broken_paths.tooltip"))
                .setSaveConsumer(newConfig -> currentConfig.broken_paths = newConfig)
                .setDefaultValue(defaultConfig.broken_paths)
                .requireRestart()
                .build());

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static Function<Screen, Screen> getDefaultsFactoryReflectively() {
        try {
            var method = Class.forName("shcm.shsupercm.fabric.citresewn.defaults.config.CITResewnDefaultsConfigScreenFactory")
                    .getMethod("create", Screen.class);
            return parent -> {
                try {
                    return (Screen) method.invoke(null, parent);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
