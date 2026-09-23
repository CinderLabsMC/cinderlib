package net.cinderlabsmc.cinderlib.item;

import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.cinderlabsmc.cinderlib.block.CinderRegistrar;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * Creative tab of a mod, translated as {@code itemGroup.<mod>.<name>}. Items
 * join it via
 * {@code CinderItem.builder(...).tab(tab)} or {@link #add(Supplier)}.
 *
 * <pre>{@code
 * public static final CinderTab MAIN = CinderTab.create(REGISTRAR, "main", () -> RUBY.get());
 * }</pre>
 */
public final class CinderTab {

  private final RegistrySupplier<CreativeModeTab> tab;

  private CinderTab(RegistrySupplier<CreativeModeTab> tab) {
    this.tab = tab;
  }

  public static @NonNull CinderTab create(@NonNull CinderRegistrar registrar, @NonNull String name,
      @NonNull Supplier<? extends ItemLike> icon) {
    var title = Component.translatable("itemGroup." + registrar.modId() + "." + name);
    return new CinderTab(
        registrar.tabs().register(name, () -> CreativeTabRegistry.create(title, () -> new ItemStack(icon.get()))));
  }

  public @NonNull RegistrySupplier<CreativeModeTab> entry() {
    return tab;
  }

  /** Adds an item or block to the tab. */
  public @NonNull CinderTab add(@NonNull Supplier<? extends ItemLike> item) {
    CreativeTabRegistry.append(tab, item);
    return this;
  }
}
