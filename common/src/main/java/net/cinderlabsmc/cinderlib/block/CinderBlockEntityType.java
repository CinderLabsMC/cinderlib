package net.cinderlabsmc.cinderlib.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import dev.architectury.registry.registries.RegistrySupplier;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeo;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A block entity type that can be shared by several {@link CinderBlock}s (e.g.
 * cable variants).
 * Created via
 * {@link CinderRegistrar#blockEntity(String, CinderBlockEntity.Factory)} or
 * implicitly by
 * {@link CinderBlockBuilder#blockEntity(CinderBlockEntity.Factory)}.
 */
public final class CinderBlockEntityType<E extends CinderBlockEntity> implements Supplier<BlockEntityType<E>> {

  private final Identifier id;
  private final CinderBlockEntity.Factory<E> factory;
  private final List<Supplier<? extends Block>> blocks = new ArrayList<>();
  private final RegistrySupplier<BlockEntityType<E>> entry;

  private boolean serverTicking;
  private boolean clientTicking;
  private @Nullable CinderGeo geo;

  CinderBlockEntityType(CinderRegistrar registrar, Identifier id, CinderBlockEntity.Factory<E> factory) {
    this.id = id;
    this.factory = factory;
    this.entry = registrar.blockEntityTypes().register(id, () -> new BlockEntityType<>(this::create, validBlocks()));
  }

  private E create(BlockPos pos, BlockState state) {
    return factory.create(entry.get(), pos, state);
  }

  private Set<Block> validBlocks() {
    return blocks.stream().map(block -> (Block) block.get()).collect(Collectors.toUnmodifiableSet());
  }

  public @NonNull Identifier id() {
    return id;
  }

  @Override
  public @NonNull BlockEntityType<E> get() {
    return entry.get();
  }

  /**
   * The registry entry, e.g. for post-registration hooks via
   * {@link RegistrySupplier#listen}.
   */
  public @NonNull RegistrySupplier<BlockEntityType<E>> entry() {
    return entry;
  }

  /** Calls {@link CinderBlockEntity#serverTick()} every tick. */
  public @NonNull CinderBlockEntityType<E> ticking() {
    serverTicking = true;
    return this;
  }

  /** Calls {@link CinderBlockEntity#clientTick()} every tick. */
  public @NonNull CinderBlockEntityType<E> clientTicking() {
    clientTicking = true;
    return this;
  }

  public boolean isServerTicking() {
    return serverTicking;
  }

  public boolean isClientTicking() {
    return clientTicking;
  }

  /**
   * The geo settings of the first attached GeckoLib block; the renderer is
   * registered once per type.
   */
  public @Nullable CinderGeo geo() {
    return geo;
  }

  void attach(Supplier<? extends Block> block, @Nullable CinderGeo blockGeo) {
    blocks.add(block);

    if (geo == null) {
      geo = blockGeo;
    }
  }
}
