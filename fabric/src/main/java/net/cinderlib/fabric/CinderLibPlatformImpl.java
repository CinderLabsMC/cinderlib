package net.cinderlib.fabric;

import net.cinderlabsmc.cinderlib.CinderLibPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

/**
 * The Fabric-side implementation of {@link CinderLibPlatform}.
 */
public class CinderLibPlatformImpl {
    public static String getPlatformName() {
        return "Fabric";
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
