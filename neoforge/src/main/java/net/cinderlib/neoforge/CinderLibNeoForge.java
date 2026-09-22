package net.cinderlib.neoforge;

import net.cinderlabsmc.cinderlib.CinderLib;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(CinderLib.MOD_ID)
public class CinderLibNeoForge {
    public CinderLibNeoForge(IEventBus modEventBus) {
        CinderLib.init();
    }
}
