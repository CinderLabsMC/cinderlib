package net.cinderlib.neoforge;

import net.cinderlabsmc.cinderlib.CinderLibPlatform;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;

/**
 * The NeoForge-side implementation of {@link CinderLibPlatform}.
 */
public class CinderLibPlatformImpl implements CinderLibPlatform {
    @Override
    public @NonNull String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(@NonNull String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public @NonNull Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
