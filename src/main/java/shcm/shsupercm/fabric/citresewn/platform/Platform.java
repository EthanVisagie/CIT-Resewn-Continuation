package shcm.shsupercm.fabric.citresewn.platform;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

public final class Platform {
    private Platform() {
    }

    public static boolean isModLoaded(String modId) {
        ModList modList = ModList.get();
        return modList != null && modList.isLoaded(normalizeModId(modId));
    }

    public static boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.isProduction();
    }

    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static String getModVersion(String modId) {
        ModList modList = ModList.get();
        if (modList == null)
            return "unknown";
        return modList.getModContainerById(normalizeModId(modId))
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("unknown");
    }

    public static Set<String> getLoadedModIds() {
        ModList modList = ModList.get();
        if (modList == null)
            return Set.of();
        Set<String> ids = new HashSet<>();
        for (var mod : modList.getMods())
            ids.add(mod.getModId().replace('-', '_'));
        return ids;
    }

    public static <T> Iterable<T> services(Class<T> type) {
        return ServiceLoader.load(type, Platform.class.getClassLoader());
    }

    public static String normalizeModId(String modId) {
        if (modId.equals("citresewn-defaults"))
            return "citresewn_defaults";
        if (modId.equals("cloth-config2") || modId.equals("cloth-config"))
            return "cloth_config";
        return modId.replace('-', '_');
    }
}