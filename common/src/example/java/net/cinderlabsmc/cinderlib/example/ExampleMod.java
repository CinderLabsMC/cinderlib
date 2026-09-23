package net.cinderlabsmc.cinderlib.example;

import net.cinderlabsmc.cinderlib.block.CinderRegistrar;

public final class ExampleMod {

  public static final String MOD_ID = "cinderexample";
  public static final CinderRegistrar REGISTRAR = CinderRegistrar.create(MOD_ID);

  private static boolean initialized;

  private ExampleMod() {
  }

  public static void init() {
    if (initialized) {
      return;
    }
    initialized = true;

    define();
    REGISTRAR.register();
  }

  /**
   * Creates all example content without registering it, e.g. for
   * {@link ExampleDataGen}.
   */
  public static void define() {
    ExampleBlocks.load();
    ExampleArmors.load();
    ExampleItems.load();
    REGISTRAR.load(Battery.class, Generator.class, RackShelf.class, Cable.class);
  }
}
