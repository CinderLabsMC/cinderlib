package net.cinderlib.neoforge;

import net.cinderlabsmc.cinderlib.CinderLibPlatform;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

/**
 * The NeoForge-side implementation of {@link CinderLibPlatform}.
 */
public class CinderLibPlatformImpl {
    public static String getPlatformName() {
        return "NeoForge";
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
