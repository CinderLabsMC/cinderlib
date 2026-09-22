package net.cinderlabsmc.cinderlib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * Generic block driven by a {@link CinderBlockType}. Create one via {@link #builder(CinderRegistrar, String)}.
 *
 * <p>Blocks with a block entity are {@link CinderEntityBlock}s. Custom subclasses can be supplied with
 * {@link CinderBlockBuilder#block(Factory)}.
 */
public class CinderBlock extends Block {

    /**
     * {@link #createBlockStateDefinition} runs inside the {@link Block} constructor, before {@link #type} is assigned,
     * so the type is handed over through this thread-local.
     */
    private static final ThreadLocal<CinderBlockType<?>> PENDING = new ThreadLocal<>();

    private final CinderBlockType<?> type;

    public CinderBlock(@NonNull CinderBlockType<?> type, BlockBehaviour.@NonNull Properties properties) {
        super(stash(type, properties));
        PENDING.remove();
        this.type = type;
        registerDefaultState(initialState(getStateDefinition().any(), type));
    }

    public static @NonNull CinderBlockBuilder<CinderBlockEntity> builder(@NonNull CinderRegistrar registrar, @NonNull String name) {
        return new CinderBlockBuilder<>(registrar, name);
    }

    public @NonNull CinderBlockType<?> type() {
        return type;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        var pending = PENDING.get();
        if (pending == null) {
            throw new IllegalStateException("CinderBlock constructed without a CinderBlockType");
        }

        if (pending.facing().property != null) {
            builder.add(pending.facing().property);
        }
        pending.properties().keySet().forEach(builder::add);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NonNull BlockPlaceContext context) {
        var state = defaultBlockState();

        return switch (type.facing()) {
            case NONE -> state;
            case HORIZONTAL -> state.setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
            case ALL -> state.setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
        };
    }

    @Override
    protected @NonNull BlockState rotate(@NonNull BlockState state, @NonNull Rotation rotation) {
        var property = type.facing().property;
        return property == null ? state : state.setValue(property, rotation.rotate(state.getValue(property)));
    }

    @Override
    protected @NonNull BlockState mirror(@NonNull BlockState state, @NonNull Mirror mirror) {
        var property = type.facing().property;
        return property == null ? state : state.rotate(mirror.getRotation(state.getValue(property)));
    }

    @Override
    protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        var shape = type.shape();
        return shape == null ? super.getShape(state, level, pos, context) : shape.getShape(state, level, pos, context);
    }

    @Override
    protected @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return type.geo() != null ? RenderShape.INVISIBLE : super.getRenderShape(state);
    }

    private static BlockBehaviour.Properties stash(CinderBlockType<?> type, BlockBehaviour.Properties properties) {
        PENDING.set(type);
        return properties;
    }

    private static BlockState initialState(BlockState state, CinderBlockType<?> type) {
        var facing = type.facing().property;
        if (facing != null) {
            state = state.setValue(facing, Direction.NORTH);
        }

        for (Map.Entry<Property<?>, Comparable<?>> entry : type.properties().entrySet()) {
            state = withValue(state, entry.getKey(), entry.getValue());
        }

        return state;
    }

    private static <T extends Comparable<T>> BlockState withValue(BlockState state, Property<T> property, Comparable<?> value) {
        return state.setValue(property, property.getValueClass().cast(value));
    }

    /** Which facing property the block gets and how it is chosen on placement. */
    public enum Facing {
        NONE(null),
        /** {@link BlockStateProperties#HORIZONTAL_FACING}, facing the player. */
        HORIZONTAL(BlockStateProperties.HORIZONTAL_FACING),
        /** {@link BlockStateProperties#FACING}, facing the player (incl. up/down). */
        ALL(BlockStateProperties.FACING);

        final @Nullable EnumProperty<Direction> property;

        Facing(@Nullable EnumProperty<Direction> property) {
            this.property = property;
        }
    }

    /** World-dependent shape, e.g. cables that connect to neighbours. */
    @FunctionalInterface
    public interface ShapeProvider {
        @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context);
    }

    /** Creates a custom {@link CinderBlock} subclass; must be a {@link CinderEntityBlock} if the block has a block entity. */
    @FunctionalInterface
    public interface Factory {
        @NonNull CinderBlock create(@NonNull CinderBlockType<?> type, BlockBehaviour.@NonNull Properties properties);
    }
}
