package shcm.shsupercm.fabric.citresewn.defaults.config;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfigScreenFactory;

public class CITResewnDefaultsConfigScreenFactory {
    @Entrypoint(CITResewnConfigScreenFactory.DEFAULTS_CONFIG_ENTRYPOINT)
    public static Screen create(Screen parent) {
        CITResewnDefaultsConfig currentConfig = CITResewnDefaultsConfig.INSTANCE, defaultConfig = new CITResewnDefaultsConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.citresewn-defaults.title"))
                .setSavingRunnable(currentConfig::write);

        ConfigCategory category = builder.getOrCreateCategory(Component.empty());
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        category.addEntry(entryBuilder.startFloatField(Component.translatable("config.citresewn-defaults.type_enchantment_scroll_multiplier.title"), currentConfig.type_enchantment_scroll_multiplier)
                .setTooltip(Component.translatable("config.citresewn-defaults.type_enchantment_scroll_multiplier.tooltip"))
                .setSaveConsumer(newConfig -> currentConfig.type_enchantment_scroll_multiplier = newConfig)
                .setDefaultValue(defaultConfig.type_enchantment_scroll_multiplier)
                .build());

        return builder.build();
    }
}
