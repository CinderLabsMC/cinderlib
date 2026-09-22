package net.cinderlib.fabric;

import net.cinderlabsmc.cinderlib.CinderLibPlatform;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;

/**
 * The Fabric-side implementation of {@link CinderLibPlatform}.
 */
public class CinderLibPlatformImpl implements CinderLibPlatform {
    @Override
    public @NonNull String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(@NonNull String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public @NonNull Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
