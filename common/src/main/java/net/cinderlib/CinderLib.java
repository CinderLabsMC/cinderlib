package net.cinderlib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CinderLib {
    public static final String MOD_ID = "cinderlib";
    public static final Logger LOGGER = LoggerFactory.getLogger("CinderLib");

    public static void init() {
        LOGGER.info("CinderLib initializing on {}", CinderLibPlatform.getPlatformName());
    }
}
