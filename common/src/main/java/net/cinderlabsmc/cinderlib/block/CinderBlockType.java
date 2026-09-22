package net.cinderlabsmc.cinderlib.block;

import dev.architectury.registry.registries.RegistrySupplier;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * Handle of a registered {@link CinderBlock}: its settings plus the registry entries of block, item and block entity type.
 * Returned by {@link CinderBlockBuilder#register()}.
 */
public final class CinderBlockType<E extends CinderBlockEntity> {

    private final Identifier id;
    private final CinderBlock.Facing facing;
    private final Map<Property<?>, Comparable<?>> properties;
    private final CinderBlock.@Nullable ShapeProvider shape;
    private final @Nullable CinderGeo geo;
    private final @Nullable CinderBlockEntityType<E> blockEntity;
    private final @Nullable Object data;

    RegistrySupplier<Block> block;
    @Nullable RegistrySupplier<Item> item;

    CinderBlockType(Identifier id, CinderBlock.Facing facing, Map<Property<?>, Comparable<?>> properties,
                    CinderBlock.@Nullable ShapeProvider shape, @Nullable CinderGeo geo,
                    @Nullable CinderBlockEntityType<E> blockEntity, @Nullable Object data) {
        this.id = id;
        this.facing = facing;
        this.properties = Map.copyOf(properties);
        this.shape = shape;
        this.geo = geo;
        this.blockEntity = blockEntity;
        this.data = data;
    }

    public @NonNull Identifier id() {
        return id;
    }

    public @NonNull Block block() {
        return block.get();
    }

    public @NonNull RegistrySupplier<Block> blockEntry() {
        return block;
    }

    /** The block item, or {@code null} if registered with {@link CinderBlockBuilder#noItem()}. */
    public @Nullable Item item() {
        return item == null ? null : item.get();
    }

    public @Nullable RegistrySupplier<Item> itemEntry() {
        return item;
    }

    public @NonNull BlockEntityType<E> blockEntityType() {
        if (blockEntity == null) {
            throw new IllegalStateException("CinderBlock " + id + " has no block entity");
        }
        return blockEntity.get();
    }

    public @Nullable CinderBlockEntityType<E> blockEntity() {
        return blockEntity;
    }

    public CinderBlock.@NonNull Facing facing() {
        return facing;
    }

    public @NonNull Map<Property<?>, Comparable<?>> properties() {
        return properties;
    }

    public CinderBlock.@Nullable ShapeProvider shape() {
        return shape;
    }

    public @Nullable CinderGeo geo() {
        return geo;
    }

    public <D> @NonNull D data(@NonNull Class<D> type) {
        if (!type.isInstance(data)) {
            throw new IllegalStateException("CinderBlock " + id + " has no data of type " + type.getName());
        }
        return type.cast(data);
    }
}
