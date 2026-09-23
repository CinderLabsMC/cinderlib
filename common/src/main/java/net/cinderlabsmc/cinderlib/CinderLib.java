package net.cinderlabsmc.cinderlib;

import java.nio.file.Path;
import java.util.ServiceLoader;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CinderLib {
  public static final String MOD_ID = "cinderlib";
  public static final Logger LOGGER = LoggerFactory.getLogger("CinderLib");

  private static final CinderLibPlatform PLATFORM = ServiceLoader
      .load(CinderLibPlatform.class, CinderLib.class.getClassLoader())
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("No CinderLibPlatform implementation found"));

  private static boolean initialized;

  private CinderLib() {
  }

  public static void init() {
    if (initialized) {
      return;
    }

    initialized = true;
    LOGGER.info("CinderLib initializing on {}", PLATFORM.getPlatformName());
  }

  public static @NonNull CinderLibPlatform platform() {
    return PLATFORM;
  }

  public static boolean isModLoaded(@NonNull String modId) {
    return PLATFORM.isModLoaded(modId);
  }

  public static @NonNull Path getConfigDirectory() {
    return PLATFORM.getConfigDirectory();
  }
}
