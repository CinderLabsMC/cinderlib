package net.cinderlabsmc.cinderlib.example;

import dev.architectury.registry.registries.RegistrySupplier;
import net.cinderlabsmc.cinderlib.item.CinderItem;
import net.cinderlabsmc.cinderlib.item.CinderTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public final class ExampleItems {

  public static final RegistrySupplier<Item> RUBY = CinderItem.builder(ExampleMod.REGISTRAR, "ruby")
      .rarity(Rarity.RARE)
      .register();

  public static final CinderTab TAB = CinderTab.create(ExampleMod.REGISTRAR, "main", RUBY)
      .add(ExampleBlocks.MARBLE::block)
      .add(ExampleBlocks.WALL_LAMP::block);

  static {
    TAB.add(RUBY);
  }

  /** Call from {@link ExampleMod#init()} to load the class. */
  static void load() {
  }

  private ExampleItems() {
  }
}
