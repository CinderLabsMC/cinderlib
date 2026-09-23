package net.cinderlabsmc.cinderlib.example;

import org.jspecify.annotations.NonNull;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;

import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.cinderlabsmc.cinderlib.block.geo.CinderBones;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Prediction;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;

public final class RackShelf extends CinderGeoBlockEntity {

  public static final CinderBlockType<RackShelf> TYPE = CinderBlock.builder(ExampleMod.REGISTRAR, "rack_shelf")
      .horizontalFacing()
      .shape(Block.box(0, 0, 2, 16, 16, 16))
      .blockEntity(RackShelf::new)
      .geo()
      .register();

  private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
  private static final RawAnimation INSERT = RawAnimation.begin().thenPlay("insert");

  private ItemStack stored = ItemStack.EMPTY;

  public RackShelf(@NonNull BlockEntityType<?> type, @NonNull BlockPos pos, @NonNull BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar controllers) {
    controllers.add(new AnimationController<RackShelf>("main", test -> test.setAndContinue(IDLE))
        .triggerableAnim("insert", INSERT));
  }

  @Override
  public @NonNull InteractionResult onUseItem(@NonNull ItemStack stack, @NonNull Player player,
      @NonNull InteractionHand hand, @NonNull BlockHitResult hit) {
    if (!stored.isEmpty()) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    if (!player.level().isClientSide()) {
      stored = stack.split(1);
      triggerAnim("main", "insert");
      sync();
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public @NonNull InteractionResult onUse(@NonNull Player player, @NonNull BlockHitResult hit) {
    if (stored.isEmpty()) {
      return InteractionResult.PASS;
    }

    if (!player.level().isClientSide()) {
      player.getInventory().placeItemBackInInventory(stored, Prediction.SERVER_ONLY);
      stored = ItemStack.EMPTY;
      sync();
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public void adjustBones(@NonNull CinderBones bones) {
    bones.visible("drive", !stored.isEmpty());
  }

  @Override
  protected @NonNull Container getDroppedContents() {
    return new SimpleContainer(stored);
  }

  @Override
  protected void saveAdditional(@NonNull ValueOutput output) {
    super.saveAdditional(output);
    output.store("stored", ItemStack.OPTIONAL_CODEC, stored);
  }

  @Override
  protected void loadAdditional(@NonNull ValueInput input) {
    super.loadAdditional(input);
    stored = input.read("stored", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
  }
}
