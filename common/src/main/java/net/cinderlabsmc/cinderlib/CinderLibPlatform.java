package net.cinderlabsmc.cinderlib;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public final class CinderLibPlatform {

    private CinderLibPlatform() {
    }

    @ExpectPlatform
    public static String getPlatformName() {
        throw new AssertionError("Platform implementation was not injected");
    }

    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new AssertionError("Platform implementation was not injected");
    }

    @ExpectPlatform
    public static Path getConfigDirectory() {
        throw new AssertionError("Platform implementation was not injected");
    }
}
