package net.cinderlabsmc.cinderlib;

import org.jspecify.annotations.NonNull;

import java.nio.file.Path;

/**
 * Loader-specific functionality, implemented once per platform and loaded via {@link java.util.ServiceLoader}
 * ({@code META-INF/services/net.cinderlabsmc.cinderlib.CinderLibPlatform}).
 */
public interface CinderLibPlatform {

    @NonNull String getPlatformName();

    boolean isModLoaded(@NonNull String modId);

    @NonNull Path getConfigDirectory();
}
