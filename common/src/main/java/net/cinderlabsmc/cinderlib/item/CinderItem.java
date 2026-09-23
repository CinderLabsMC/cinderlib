package net.cinderlabsmc.cinderlib.item;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import dev.architectury.registry.registries.RegistrySupplier;
import net.cinderlabsmc.cinderlib.block.CinderRegistrar;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * Fluent builder for plain items, obtained from
 * {@link #builder(CinderRegistrar, String)}.
 *
 * <pre>{@code
 * public static final RegistrySupplier<Item> RUBY = CinderItem.builder(REGISTRAR, "ruby")
 *     .rarity(Rarity.RARE)
 *     .tab(MAIN)
 *     .register();
 * }</pre>
 * 
 * Use {@link #factory} for custom item classes, e.g.
 * {@code .factory(MyItem::new)}.
 */
public final class CinderItem<I extends Item> {

  private final CinderRegistrar registrar;
  private final Identifier id;

  private UnaryOperator<Item.Properties> properties = UnaryOperator.identity();
  private Function<Item.Properties, ? extends Item> factory = Item::new;
  private @Nullable CinderTab tab;

  private CinderItem(CinderRegistrar registrar, String name) {
    this.registrar = registrar;
    this.id = registrar.id(name);
  }

  public static @NonNull CinderItem<Item> builder(@NonNull CinderRegistrar registrar, @NonNull String name) {
    return new CinderItem<>(registrar, name);
  }

  /** Adjusts the item properties; the id is set automatically. */
  public @NonNull CinderItem<I> properties(@NonNull UnaryOperator<Item.Properties> properties) {
    var previous = this.properties;
    this.properties = p -> properties.apply(previous.apply(p));
    return this;
  }

  public @NonNull CinderItem<I> stacksTo(int size) {
    return properties(p -> p.stacksTo(size));
  }

  public @NonNull CinderItem<I> rarity(@NonNull Rarity rarity) {
    return properties(p -> p.rarity(rarity));
  }

  public @NonNull CinderItem<I> fireResistant() {
    return properties(Item.Properties::fireResistant);
  }

  /** Custom item class, e.g. {@code MyItem::new}. */
  @SuppressWarnings("unchecked")
  public <N extends Item> @NonNull CinderItem<N> factory(@NonNull Function<Item.Properties, N> factory) {
    this.factory = factory;
    return (CinderItem<N>) this;
  }

  /** Lists the item in a creative tab. */
  public @NonNull CinderItem<I> tab(@NonNull CinderTab tab) {
    this.tab = tab;
    return this;
  }

  @SuppressWarnings("unchecked")
  public @NonNull RegistrySupplier<I> register() {
    var key = ResourceKey.create(Registries.ITEM, id);
    var props = properties;
    var creator = factory;
    var entry = (RegistrySupplier<I>) registrar.items().register(id,
        () -> creator.apply(props.apply(new Item.Properties()).setId(key)));
    if (tab != null) {
      tab.add(entry);
    }
    return entry;
  }
}
