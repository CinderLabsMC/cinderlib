package net.cinderlabsmc.cinderlib.client.gui;

import org.jspecify.annotations.NonNull;

import dev.architectury.registry.client.gui.MenuScreenRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class CinderScreens {

  private CinderScreens() {
  }

  public static <Menu extends AbstractContainerMenu, Type extends MenuType<Menu>, View extends Screen & MenuAccess<Menu>> void register(
      @NonNull RegistrySupplier<Type> type, MenuScreenRegistry.@NonNull ScreenFactory<Menu, View> factory) {
    type.listen(registered -> MenuScreenRegistry.registerScreenFactory(registered, factory));
  }
}
