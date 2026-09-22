package net.cinderlabsmc.cinderlib.block;

import net.cinderlabsmc.cinderlib.block.geo.CinderGeo;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeoBlockEntity;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeoBlockItem;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Fluent builder for {@link CinderBlock}s, obtained from {@link CinderBlock#builder(CinderRegistrar, String)}.
 *
 * <pre>{@code
 * public static final CinderBlockType<RackShelf> TYPE = CinderBlock.builder(ModBlocks.REGISTRAR, "rack_shelf")
 *         .horizontalFacing()
 *         .blockEntity(RackShelf::new)
 *         .ticking()
 *         .geo()
 *         .register();
 * }</pre>
 */
public final class CinderBlockBuilder<E extends CinderBlockEntity> {

    private final CinderRegistrar registrar;
    private final Identifier id;

    private UnaryOperator<BlockBehaviour.Properties> properties = UnaryOperator.identity();
    private UnaryOperator<Item.Properties> itemProperties = UnaryOperator.identity();
    private @Nullable BiFunction<Block, Item.Properties, ? extends Item> itemFactory;
    private boolean noItem;
    private CinderBlock.@Nullable Factory blockFactory;
    private CinderBlock.Facing facing = CinderBlock.Facing.NONE;
    private final Map<Property<?>, Comparable<?>> stateProperties = new LinkedHashMap<>();
    private CinderBlock.@Nullable ShapeProvider shape;
    private boolean dynamicShape;
    private @Nullable CinderGeo geo;
    private @Nullable CinderBlockEntityType<? extends CinderBlockEntity> blockEntity;
    private boolean serverTicking;
    private boolean clientTicking;
    private @Nullable Object data;

    CinderBlockBuilder(CinderRegistrar registrar, String name) {
        this.registrar = registrar;
        this.id = registrar.id(name);
    }

    /** Adjusts the block properties. Defaults to {@code Properties.of().strength(1.5f)}; the id is set automatically. */
    public @NonNull CinderBlockBuilder<E> properties(@NonNull UnaryOperator<BlockBehaviour.Properties> properties) {
        this.properties = properties;
        return this;
    }

    /** Adds a horizontal facing ({@link BlockStateProperties#HORIZONTAL_FACING}) facing the player on placement. */
    public @NonNull CinderBlockBuilder<E> horizontalFacing() {
        return facing(CinderBlock.Facing.HORIZONTAL);
    }

    public @NonNull CinderBlockBuilder<E> facing(CinderBlock.@NonNull Facing facing) {
        this.facing = facing;
        return this;
    }

    /** Adds a custom block state property with its default value. */
    public <T extends Comparable<T>> @NonNull CinderBlockBuilder<E> property(@NonNull Property<T> property, @NonNull T defaultValue) {
        stateProperties.put(property, defaultValue);
        return this;
    }

    /**
     * Static shape in pixel units (see {@link Block#box}). With {@link #horizontalFacing()} the shape is treated as
     * facing north and rotated automatically.
     */
    public @NonNull CinderBlockBuilder<E> shape(@NonNull VoxelShape shape) {
        if (facing == CinderBlock.Facing.HORIZONTAL) {
            Map<Direction, VoxelShape> rotated = Shapes.rotateHorizontal(shape);
            return shape(state -> rotated.get(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
        }
        return shape(state -> shape);
    }

    /** Shape depending only on the block state (cached by vanilla). */
    public @NonNull CinderBlockBuilder<E> shape(@NonNull Function<BlockState, VoxelShape> shape) {
        this.shape = (state, level, pos, context) -> shape.apply(state);
        this.dynamicShape = false;
        return this;
    }

    /** Shape depending on the world, e.g. neighbours. Marks the block as {@code dynamicShape()}. */
    public @NonNull CinderBlockBuilder<E> dynamicShape(CinderBlock.@NonNull ShapeProvider shape) {
        this.shape = shape;
        this.dynamicShape = true;
        return this;
    }

    /** Uses a custom {@link CinderBlock} subclass. */
    public @NonNull CinderBlockBuilder<E> block(CinderBlock.@NonNull Factory factory) {
        this.blockFactory = factory;
        return this;
    }

    /** Custom item, e.g. {@code MyBlockItem::new}. Defaults to a {@link BlockItem} (or a GeckoLib item for {@link #geo()} blocks). */
    public @NonNull CinderBlockBuilder<E> item(@NonNull BiFunction<Block, Item.Properties, ? extends Item> factory) {
        this.itemFactory = factory;
        return this;
    }

    public @NonNull CinderBlockBuilder<E> itemProperties(@NonNull UnaryOperator<Item.Properties> properties) {
        this.itemProperties = properties;
        return this;
    }

    public @NonNull CinderBlockBuilder<E> noItem() {
        this.noItem = true;
        return this;
    }

    /** Gives the block its own block entity type (named like the block). */
    @SuppressWarnings("unchecked")
    public <N extends E> @NonNull CinderBlockBuilder<N> blockEntity(CinderBlockEntity.@NonNull Factory<N> factory) {
        this.blockEntity = registrar.blockEntity(id.getPath(), factory);
        return (CinderBlockBuilder<N>) this;
    }

    /** Shares an existing block entity type, e.g. between variants of the same block. */
    @SuppressWarnings("unchecked")
    public <N extends E> @NonNull CinderBlockBuilder<N> blockEntity(@NonNull CinderBlockEntityType<N> type) {
        this.blockEntity = type;
        return (CinderBlockBuilder<N>) this;
    }

    /** Calls {@link CinderBlockEntity#serverTick()} every tick. */
    public @NonNull CinderBlockBuilder<E> ticking() {
        this.serverTicking = true;
        return this;
    }

    /** Calls {@link CinderBlockEntity#clientTick()} every tick. */
    public @NonNull CinderBlockBuilder<E> clientTicking() {
        this.clientTicking = true;
        return this;
    }

    /** Renders the block with GeckoLib using the default asset paths, see {@link CinderGeo}. */
    public @NonNull CinderBlockBuilder<E> geo() {
        return geo(geo -> {});
    }

    /** Renders the block with GeckoLib; the consumer can change asset paths, idle animation etc. */
    public @NonNull CinderBlockBuilder<E> geo(@NonNull Consumer<CinderGeo> config) {
        var geo = CinderGeo.defaults(id);
        config.accept(geo);
        this.geo = geo;
        return this;
    }

    /** Custom per-block data, read back with {@link CinderBlockType#data(Class)} / {@link CinderBlockEntity#data(Class)}. */
    public @NonNull CinderBlockBuilder<E> data(@NonNull Object data) {
        this.data = data;
        return this;
    }

    @SuppressWarnings("unchecked")
    public @NonNull CinderBlockType<E> register() {
        if (geo != null && blockEntity == null) {
            blockEntity = registrar.blockEntity(id.getPath(), CinderGeoBlockEntity::new);
        }

        var blockEntityType = (CinderBlockEntityType<E>) blockEntity;
        if (blockEntityType != null) {
            if (serverTicking) blockEntityType.ticking();
            if (clientTicking) blockEntityType.clientTicking();
        } else if (serverTicking || clientTicking) {
            throw new IllegalStateException("CinderBlock " + id + " is ticking but has no block entity");
        }

        var type = new CinderBlockType<>(id, facing, stateProperties, shape, geo, blockEntityType, data);

        CinderBlock.Factory factory = blockFactory != null ? blockFactory : blockEntityType != null ? CinderEntityBlock::new : CinderBlock::new;
        var hasGeo = geo != null;
        var dynamic = dynamicShape;
        var props = properties;

        type.block = registrar.blocks().register(id, () -> {
            var base = BlockBehaviour.Properties.of().strength(1.5f);
            if (hasGeo) base = base.noOcclusion();
            if (dynamic) base = base.dynamicShape();

            var block = factory.create(type, props.apply(base).setId(ResourceKey.create(Registries.BLOCK, id)));
            if (blockEntityType != null && !(block instanceof CinderEntityBlock)) {
                throw new IllegalStateException("CinderBlock " + id + " has a block entity, so its factory must create a CinderEntityBlock");
            }
            return block;
        });

        if (!noItem) {
            var items = itemFactory != null ? itemFactory : defaultItemFactory();
            var itemProps = itemProperties;
            type.item = registrar.items().register(id, () -> items.apply(type.block(),
                    itemProps.apply(new Item.Properties().useBlockDescriptionPrefix()).setId(ResourceKey.create(Registries.ITEM, id))));
        }

        if (blockEntityType != null) {
            blockEntityType.attach(type.blockEntry(), geo);
        }

        registrar.track(type);
        return type;
    }

    private BiFunction<Block, Item.Properties, ? extends Item> defaultItemFactory() {
        var geo = this.geo;
        if (geo != null && geo.hasGeoItem()) {
            return (block, props) -> new CinderGeoBlockItem(block, props, geo);
        }
        return BlockItem::new;
    }
}
