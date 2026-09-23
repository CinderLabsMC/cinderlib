package net.cinderlabsmc.cinderlib.example;

import org.jspecify.annotations.NonNull;

import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.cinderlabsmc.cinderlib.block.CinderBlockEntity;
import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class Generator extends CinderBlockEntity implements MenuProvider {

  public static final CinderBlockType<Generator> TYPE = CinderBlock.builder(ExampleMod.REGISTRAR, "generator")
      .horizontalFacing()
      .properties(p -> p.strength(3.5f).requiresCorrectToolForDrops())
      .blockEntity(Generator::new)
      .ticking()
      .register();

  private static final long CAPACITY = 1_000;
  private static final long PER_TICK = 20;
  private static final int BURN_TICKS_PER_ITEM = 400;

  private final SimpleContainer fuel = new SimpleContainer(HopperMenu.CONTAINER_SIZE) {
    @Override
    public void setChanged() {
      super.setChanged();
      Generator.this.setChanged();
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
      return Container.stillValidBlockEntity(Generator.this, player);
    }
  };

  private long energy;
  private int burnTime;

  public Generator(@NonNull BlockEntityType<?> type, @NonNull BlockPos pos, @NonNull BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void serverTick() {
    if (burnTime <= 0 && energy < CAPACITY) {
      for (int slot = 0; slot < fuel.getContainerSize(); slot++) {
        var stack = fuel.getItem(slot);
        if (stack.is(ItemTags.COALS)) {
          stack.shrink(1);
          burnTime = BURN_TICKS_PER_ITEM;
          break;
        }
      }
    }

    if (burnTime > 0) {
      burnTime--;
      energy = Math.min(CAPACITY, energy + PER_TICK);
    }

    for (var dir : Direction.values()) {
      if (energy > 0 && level.getBlockEntity(worldPosition.relative(dir)) instanceof Battery battery) {
        energy -= battery.insert(energy);
      }
    }

    setChanged();
  }

  @Override
  protected @NonNull Container getDroppedContents() {
    return fuel;
  }

  @Override
  public @NonNull AbstractContainerMenu createMenu(int id, @NonNull Inventory playerInventory, @NonNull Player player) {
    return new HopperMenu(id, playerInventory, fuel);
  }

  @Override
  protected void saveAdditional(@NonNull ValueOutput output) {
    super.saveAdditional(output);
    output.putLong("energy", energy);
    output.putInt("burn_time", burnTime);
    ContainerHelper.saveAllItems(output, fuel.getItems());
  }

  @Override
  protected void loadAdditional(@NonNull ValueInput input) {
    super.loadAdditional(input);
    energy = input.getLongOr("energy", 0);
    burnTime = input.getIntOr("burn_time", 0);
    ContainerHelper.loadAllItems(input, fuel.getItems());
  }
}
