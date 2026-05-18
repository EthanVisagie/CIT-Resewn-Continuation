package shcm.shsupercm.fabric.citresewn.defaults.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.network.chat.Component;

@Entrypoint("modmenu")
public class CITResewnDefaultsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2"))
            return new ClothConfigOpenImpl().getModConfigScreenFactory();

        return parent -> new AlertScreen(() -> Minecraft.getInstance().setScreenAndShow(parent), Component.literal("CIT Resewn: Defaults"), Component.literal("CIT Resewn requires Cloth Config to be able to show the config."));
    }

    private static class ClothConfigOpenImpl implements ModMenuApi {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return CITResewnDefaultsConfigScreenFactory::create;
        }
    }
}
