package net.cinderlabsmc.cinderlib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public final class CinderLib {

    public static final String MOD_ID = "cinderlib";
    public static final Logger LOGGER = LoggerFactory.getLogger("CinderLib");

    private static boolean initialized;

    private CinderLib() {
    }

    public static void init() {
        if (initialized) {
            return;
        }

        initialized = true;
        LOGGER.info("CinderLib initializing on {}", CinderLibPlatform.getPlatformName());
    }

    public static boolean isModLoaded(String modId) {
        return CinderLibPlatform.isModLoaded(modId);
    }

    public static Path getConfigDirectory() {
        return CinderLibPlatform.getConfigDirectory();
    }
}
