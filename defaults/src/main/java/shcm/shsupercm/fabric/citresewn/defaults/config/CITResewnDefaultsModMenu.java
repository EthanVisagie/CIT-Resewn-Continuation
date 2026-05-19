package shcm.shsupercm.fabric.citresewn.defaults.config;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import shcm.shsupercm.fabric.citresewn.platform.Platform;

import java.util.function.Function;

@Entrypoint("modmenu")
public class CITResewnDefaultsModMenu {
    public Function<Screen, Screen> getModConfigScreenFactory() {
        if (Platform.isModLoaded("cloth-config2"))
            return CITResewnDefaultsConfigScreenFactory::create;

        return parent -> new AlertScreen(() -> Minecraft.getInstance().setScreenAndShow(parent), Component.literal("CIT Resewn: Defaults"), Component.literal("CIT Resewn requires Cloth Config to be able to show the config."));
    }
}
