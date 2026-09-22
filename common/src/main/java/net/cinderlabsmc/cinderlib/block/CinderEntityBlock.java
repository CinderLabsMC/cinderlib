package net.cinderlabsmc.cinderlib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * {@link CinderBlock} with a block entity. Interactions, placement and ticking are forwarded to the
 * {@link CinderBlockEntity}, so the block itself never needs to be subclassed for them.
 */
public class CinderEntityBlock extends CinderBlock implements EntityBlock {

    public CinderEntityBlock(@NonNull CinderBlockType<?> type, BlockBehaviour.@NonNull Properties properties) {
        super(type, properties);

        if (type.blockEntity() == null) {
            throw new IllegalStateException("CinderEntityBlock " + type.id() + " has no block entity type");
        }
    }

    private CinderBlockEntityType<?> blockEntity() {
        var blockEntity = type().blockEntity();
        assert blockEntity != null;
        return blockEntity;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return blockEntity().get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> blockEntityType) {
        var blockEntity = blockEntity();
        if (blockEntityType != blockEntity.get()) {
            return null;
        }

        if (level.isClientSide()) {
            return blockEntity.isClientTicking() ? (l, p, s, be) -> ((CinderBlockEntity) be).clientTick() : null;
        }

        return blockEntity.isServerTicking() ? (l, p, s, be) -> ((CinderBlockEntity) be).serverTick() : null;
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof CinderBlockEntity be) {
            return be.onUse(player, hit);
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof CinderBlockEntity be) {
            return be.onUseItem(stack, player, hand, hit);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(@NonNull Level level, @NonNull BlockPos pos, @NonNull BlockState state, @Nullable LivingEntity placer, @NonNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.getBlockEntity(pos) instanceof CinderBlockEntity be) {
            be.onPlaced(placer, stack);
        }
    }

    @Override
    protected boolean triggerEvent(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, int id, int param) {
        var be = level.getBlockEntity(pos);
        return be != null && be.triggerEvent(id, param);
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos) {
        return level.getBlockEntity(pos) instanceof MenuProvider provider ? provider : null;
    }
}
