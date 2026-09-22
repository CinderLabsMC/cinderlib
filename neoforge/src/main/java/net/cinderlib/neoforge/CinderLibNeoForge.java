package net.cinderlib.neoforge;

import net.cinderlabsmc.cinderlib.CinderLib;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(CinderLib.MOD_ID)
public class CinderLibNeoForge {
    private final IApiService apiService;

    public CinderLibNeoForge(IEventBus modEventBus, IApiService apiService) {
        this.apiService = apiService;
        CinderLib.init();
        System.out.println("CinderLib Initializing. API Test: " + apiService.getGreetingMessage("Developer"));
    }
}
