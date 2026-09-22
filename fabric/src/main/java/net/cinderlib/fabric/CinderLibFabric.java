package net.cinderlib.fabric;

import net.cinderlabsmc.cinderlib.CinderLib;
import net.fabricmc.api.ModInitializer;

public class CinderLibFabric implements ModInitializer {
@Override
public void onInitialize() {
    CinderLib.init();
    IApiService apiService = new ApiServiceImpl();
    System.out.println("CinderLib Initializing. API Test: " + apiService.getGreetingMessage("Developer"));
}
}
