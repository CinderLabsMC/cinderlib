package net.cinderlib;

import dev.architectury.injectables.annotations.ExpectPlatform;

/**
 * Demonstrates Architectury's {@link ExpectPlatform} mechanism: this class defines the
 * expectation, and each platform module provides a {@code CinderLibPlatformImpl} in its own
 * {@code .fabric} / {@code .neoforge} sub-package with the real implementation.
 */
public class CinderLibPlatform {
    @ExpectPlatform
    public static String getPlatformName() {
        throw new AssertionError("Platform implementation was not injected");
    }
}
