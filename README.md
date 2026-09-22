# CinderLib

An [Architectury API](https://github.com/architectury/architectury-api) multi-loader mod for Minecraft `26.3`,
targeting **Fabric** and **NeoForge**.

## Project layout

- `common/` — shared API and logic, using only Minecraft and Architectury API classes.
- `fabric/` — Fabric-specific entrypoint and glue code.
- `neoforge/` — NeoForge-specific entrypoint and glue code.

The core has no required GeckoLib, Cloth Config, JEI, Jade, Mod Menu, or Fabric API
integration. Those can be added by consumers or introduced later as separate optional
integration modules.

The shared API currently provides:

- `CinderLib.isModLoaded(String)` for optional integrations.
- `CinderLib.getConfigDirectory()` for platform-neutral config paths.
- `CinderLibPlatform` as the small extension point for further loader differences.

## CinderBlock

One class per block: the block entity class also declares the block.

```java
public final class ModBlocks {
    public static final CinderRegistrar REGISTRAR = CinderRegistrar.create(MOD_ID);

    public static void init() { // Fabric ModInitializer / NeoForge mod constructor
        REGISTRAR.load(Battery.class, Generator.class, RackShelf.class, Cable.class);
        REGISTRAR.register(); // blocks -> items -> block entity types (+ GeckoLib renderers on the client)
    }
}

public final class RackShelf extends CinderGeoBlockEntity implements ExtendedMenuDataProvider<BlockPos> {
    public static final CinderBlockType<RackShelf> TYPE = CinderBlock.builder(ModBlocks.REGISTRAR, "rack_shelf")
            .horizontalFacing()
            .blockEntity(RackShelf::new)
            .ticking()                   // -> serverTick()
            .geo(geo -> geo.idle("idle")) // GeckoLib block + item, looping "idle"
            .register();

    public RackShelf(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    @Override public void serverTick() { ... }
    @Override public BlockPos getExtraData(ServerPlayer player) { return worldPosition; }
    @Override public StreamCodec<? super RegistryFriendlyByteBuf, BlockPos> getExtraDataCodec() { return BlockPos.STREAM_CODEC; }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) { ... } // opened on use automatically
}
```

- No block entity: `CinderBlock.builder(reg, "name").register()`.
- Shared block entity type for variants: `REGISTRAR.blockEntity("cable", Cable::new)` + `.blockEntity(Cable.BLOCK_ENTITY).data(variant)` per variant, read with `data(Variant.class)`.
- Shapes: `.shape(VoxelShape)` (auto-rotated with facing), `.shape(state -> ...)`, `.dynamicShape((state, level, pos, ctx) -> ...)`.
- Per-instance rendering: override `adjustBones(CinderBones)` (hide/scale bones), `getGeoModel()`/`getGeoTexture()`; call `sync()` after changing data the client needs.
- Contents from `getDroppedContents()` drop on break; menus use the block's name.

GeckoLib assets for `mod:name` (overridable via `.geo(g -> g.model(..).texture(..).animation(..))`):

| File | Path |
|---|---|
| Model | `assets/mod/geckolib/models/block/name.geo.json` |
| Animation | `assets/mod/geckolib/animations/block/name.animation.json` |
| Texture | `assets/mod/textures/block/name.png` |
| Item | `assets/mod/items/name.json` → `{"model":{"type":"minecraft:special","base":"mod:block/name","model":{"type":"geckolib:geckolib"}}}` |
| Particle/display model | `assets/mod/models/block/name.json` (+ blockstate pointing to it) |

GeckoLib must be installed at runtime (`geckolib-fabric` / `geckolib-neoforge`) for `.geo()` blocks; other blocks don't load GeckoLib classes.

## Building

```sh
./gradlew build
```

Loader jars are output to `fabric/build/libs/` and `neoforge/build/libs/`.

## Running in dev

```sh
./gradlew :fabric:runClient
./gradlew :neoforge:runClient
```

## Versions

| Component       | Version               |
|------------------|------------------------|
| Minecraft        | 26.3                   |
| Architectury API | 22.0.2                 |
| Fabric Loader    | 0.19.5                 |
| NeoForge         | 26.3.0.8-beta          |
| GeckoLib         | 5.5.7                  |
| Loom             | 1.18.2                 |
| Java             | 25                      |

> These pin the latest known-compatible releases for Minecraft 26.3 as of this project's creation.
> Bump them in `gradle.properties` / `build.gradle` as newer builds land.
