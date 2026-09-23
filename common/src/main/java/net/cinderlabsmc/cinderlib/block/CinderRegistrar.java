package net.cinderlabsmc.cinderlib.block;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jspecify.annotations.NonNull;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.utils.Env;
import net.cinderlabsmc.cinderlib.block.geo.client.CinderGeoClient;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Owns the block, item and block entity type registers of one mod.
 *
 * <pre>{@code
 * public static final CinderRegistrar REGISTRAR = CinderRegistrar.create(MOD_ID);
 *
 * public static void init() {
 *   REGISTRAR.load(Battery.class, Generator.class, RackShelf.class, Cable.class);
 *   REGISTRAR.register();
 * }
 * }</pre>
 * 
 * Call {@link #register()} once from common init (Fabric {@code ModInitializer}
 * / NeoForge mod constructor).
 */
public final class CinderRegistrar {

  private final String modId;
  private final DeferredRegister<Block> blocks;
  private final DeferredRegister<Item> items;
  private final DeferredRegister<BlockEntityType<?>> blockEntityTypes;
  private final DeferredRegister<CreativeModeTab> tabs;
  private final List<CinderBlockType<?>> types = new ArrayList<>();

  private boolean registered;

  private CinderRegistrar(String modId) {
    this.modId = modId;
    this.blocks = DeferredRegister.create(modId, Registries.BLOCK);
    this.items = DeferredRegister.create(modId, Registries.ITEM);
    this.blockEntityTypes = DeferredRegister.create(modId, Registries.BLOCK_ENTITY_TYPE);
    this.tabs = DeferredRegister.create(modId, Registries.CREATIVE_MODE_TAB);
  }

  public static @NonNull CinderRegistrar create(@NonNull String modId) {
    return new CinderRegistrar(modId);
  }

  public @NonNull String modId() {
    return modId;
  }

  public @NonNull Identifier id(@NonNull String path) {
    return Identifier.fromNamespaceAndPath(modId, path);
  }

  public @NonNull DeferredRegister<Block> blocks() {
    return blocks;
  }

  public @NonNull DeferredRegister<Item> items() {
    return items;
  }

  public @NonNull DeferredRegister<BlockEntityType<?>> blockEntityTypes() {
    return blockEntityTypes;
  }

  public @NonNull DeferredRegister<CreativeModeTab> tabs() {
    return tabs;
  }

  public @NonNull List<CinderBlockType<?>> types() {
    return List.copyOf(types);
  }

  /**
   * A block entity type that several blocks can share via
   * {@link CinderBlockBuilder#blockEntity(CinderBlockEntityType)}.
   */
  public <E extends CinderBlockEntity> @NonNull CinderBlockEntityType<E> blockEntity(@NonNull String name,
      CinderBlockEntity.@NonNull Factory<E> factory) {
    checkOpen(name);
    return new CinderBlockEntityType<>(this, id(name), factory);
  }

  /**
   * Initializes the given classes so their static {@link CinderBlockType} fields
   * are created before {@link #register()}.
   */
  public @NonNull CinderRegistrar load(@NonNull Class<?>... classes) {
    for (var clazz : classes) {
      try {
        Class.forName(clazz.getName(), true, clazz.getClassLoader());
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException(e);
      }
    }
    return this;
  }

  /**
   * Registers blocks, then items, block entity types and creative tabs, and on
   * the client the GeckoLib renderers.
   */
  public void register() {
    if (registered) {
      throw new IllegalStateException("CinderRegistrar " + modId + " is already registered");
    }
    registered = true;

    blocks.register();
    items.register();
    blockEntityTypes.register();
    tabs.register();

    if (Platform.getEnvironment() == Env.CLIENT) {
      registerClient();
    }
  }

  private void registerClient() {
    Set<CinderBlockEntityType<?>> geoTypes = new LinkedHashSet<>();
    for (var type : types) {
      if (type.geo() != null && type.blockEntity() != null) {
        geoTypes.add(type.blockEntity());
      }
    }

    for (var type : geoTypes) {
      var geo = type.geo();
      if (geo != null) {
        type.entry().listen(blockEntityType -> CinderGeoClient.registerBlockRenderer(blockEntityType, geo));
      }
    }
  }

  void track(CinderBlockType<?> type) {
    checkOpen(type.id().getPath());
    types.add(type);
  }

  private void checkOpen(String name) {
    if (registered) {
      throw new IllegalStateException("Cannot add " + modId + ":" + name + " after CinderRegistrar.register()");
    }
  }
}
