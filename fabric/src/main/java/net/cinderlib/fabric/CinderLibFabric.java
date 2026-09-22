package net.cinderlib.fabric;

import net.cinderlib.CinderLib;
import net.fabricmc.api.ModInitializer;

public class CinderLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CinderLib.init();
    }
}
