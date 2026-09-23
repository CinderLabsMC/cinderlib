package net.cinderlabsmc.cinderlib.block;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import dev.architectury.registry.menu.ExtendedMenuDataProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Base block entity for {@link CinderEntityBlock}s. Override only the hooks you
 * need:
 * <ul>
 * <li>{@link #serverTick()} / {@link #clientTick()} (enable with
 * {@link CinderBlockBuilder#ticking()} /
 * {@link CinderBlockBuilder#clientTicking()})</li>
 * <li>{@link #onUse}, {@link #onUseItem}, {@link #onPlaced}</li>
 * <li>{@link #saveAdditional} / {@link #loadAdditional} — also used for client
 * sync, see {@link #sync()}</li>
 * <li>{@link #getDroppedContents()} — dropped when the block is broken</li>
 * </ul>
 * Implementing {@link MenuProvider} (or architectury's
 * {@link ExtendedMenuDataProvider}) is enough to open the menu on use.
 */
public abstract class CinderBlockEntity extends BlockEntity {

  protected CinderBlockEntity(@NonNull BlockEntityType<?> type, @NonNull BlockPos pos, @NonNull BlockState state) {
    super(type, pos, state);
  }

  /** The {@link CinderBlockType} of the block this entity belongs to. */
  public @NonNull CinderBlockType<?> blockType() {
    return ((CinderBlock) getBlockState().getBlock()).type();
  }

  /**
   * Custom per-block data set with {@link CinderBlockBuilder#data(Object)}, e.g.
   * a cable variant.
   */
  public <D> @NonNull D data(@NonNull Class<D> type) {
    return blockType().data(type);
  }

  public @Nullable CinderGeo geo() {
    return blockType().geo();
  }

  public void serverTick() {
  }

  public void clientTick() {
  }

  /**
   * Right-click with an empty hand (or after {@link #onUseItem} returned
   * {@link InteractionResult#TRY_WITH_EMPTY_HAND}).
   */
  public @NonNull InteractionResult onUse(@NonNull Player player, @NonNull BlockHitResult hit) {
    if (!(this instanceof MenuProvider provider)) {
      return InteractionResult.PASS;
    }

    if (player instanceof ServerPlayer serverPlayer) {
      if (provider instanceof ExtendedMenuDataProvider<?> extended) {
        MenuRegistry.openExtendedMenu(serverPlayer, extended);
      } else {
        MenuRegistry.openMenu(serverPlayer, provider);
      }
    }
    return InteractionResult.SUCCESS;
  }

  public @NonNull InteractionResult onUseItem(@NonNull ItemStack stack, @NonNull Player player,
      @NonNull InteractionHand hand, @NonNull BlockHitResult hit) {
    return InteractionResult.TRY_WITH_EMPTY_HAND;
  }

  public void onPlaced(@Nullable LivingEntity placer, @NonNull ItemStack stack) {
  }

  /**
   * Contents dropped when the block is removed. Defaults to this entity if it is
   * a {@link Container}.
   */
  protected @Nullable Container getDroppedContents() {
    return this instanceof Container container ? container : null;
  }

  /** Default name for menus: the block's translated name. */
  public @NonNull Component getDisplayName() {
    return getBlockState().getBlock().getName();
  }

  /**
   * Marks the entity dirty and sends its saved data ({@link #saveAdditional}) to
   * tracking clients.
   */
  public void sync() {
    setChanged();

    if (level != null && !level.isClientSide()) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
  }

  @Override
  public void preRemoveSideEffects(@NonNull BlockPos pos, @NonNull BlockState state) {
    var contents = getDroppedContents();
    if (contents != null && level != null) {
      Containers.dropContents(level, pos, contents);
    }
  }

  @Override
  public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
    return saveCustomOnly(registries);
  }

  /**
   * Creates a block entity for a registered type; matches a
   * {@code (BlockEntityType, BlockPos, BlockState)} constructor.
   */
  @FunctionalInterface
  public interface Factory<E extends CinderBlockEntity> {
    @NonNull
    E create(@NonNull BlockEntityType<E> type, @NonNull BlockPos pos, @NonNull BlockState state);
  }
}
