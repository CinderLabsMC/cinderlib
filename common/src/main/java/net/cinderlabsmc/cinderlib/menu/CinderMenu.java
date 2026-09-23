package net.cinderlabsmc.cinderlib.menu;

import org.jspecify.annotations.NonNull;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class CinderMenu extends AbstractContainerMenu {

  private int machineSlots;

  protected CinderMenu(@NonNull MenuType<?> type, int containerId) {
    super(type, containerId);
  }

  protected @NonNull Slot addMachineSlot(@NonNull Slot slot) {
    if (slots.size() != machineSlots) {
      throw new IllegalStateException("Machine slots must be added before the player inventory");
    }

    machineSlots++;

    return addSlot(slot);
  }

  @Override
  public @NonNull ItemStack quickMoveStack(@NonNull Player player, int index) {
    var slot = slots.get(index);

    if (!slot.hasItem()) {
      return ItemStack.EMPTY;
    }

    var stack = slot.getItem();
    var original = stack.copy();

    if (!move(stack, index)) {
      return ItemStack.EMPTY;
    }

    if (stack.isEmpty()) {
      slot.setByPlayer(ItemStack.EMPTY);
      return original;
    }

    slot.setChanged();

    return original;
  }

  private boolean move(ItemStack stack, int index) {
    if (index < machineSlots) {
      return moveItemStackTo(stack, machineSlots, slots.size(), true);
    }

    return moveItemStackTo(stack, 0, machineSlots, false);
  }
}
