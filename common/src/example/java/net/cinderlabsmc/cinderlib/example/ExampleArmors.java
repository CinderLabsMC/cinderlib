package net.cinderlabsmc.cinderlib.example;

import net.cinderlabsmc.cinderlib.armor.CinderArmor;

public final class ExampleArmors {

  public static final PlainArmor PLAIN = CinderArmor.register(ExampleMod.REGISTRAR, new PlainArmor());
  public static final EmberArmor EMBER = CinderArmor.register(ExampleMod.REGISTRAR, new EmberArmor());
  public static final GhostArmor GHOST = CinderArmor.register(ExampleMod.REGISTRAR, new GhostArmor());

  static void load() {
  }

  private ExampleArmors() {
  }
}
