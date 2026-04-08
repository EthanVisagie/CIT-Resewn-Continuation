package shcm.shsupercm.fabric.citresewn.defaults;

import net.fabricmc.api.ClientModInitializer;

public class CITResewnDefaults implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CITResewnDefaultsCompatAPI.initAll();
    }
}
