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
- `CinderLibPlatform` (via `CinderLib.platform()`) as the small extension point for further loader differences, implemented per loader and loaded with `ServiceLoader`.

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

## CinderArmor

One class per armor set; every setting is an overridable method (only `material()` is required).

```java
public final class EmberArmor extends CinderArmor {
    private static final CinderArmorMaterial MATERIAL = CinderArmorMaterial.create()
            .durability(37).defense(3, 6, 8, 3).enchantability(15).toughness(1f)
            .equipSound(SoundEvents.ARMOR_EQUIP_NETHERITE);

    public EmberArmor() { super("ember"); }

    @Override public CinderArmorMaterial material() { return MATERIAL; }
    @Override public @Nullable String variant(EquipmentSlot slot) { return slot == EquipmentSlot.LEGS ? "legs" : null; }
    @Override public @Nullable String idleAnimation(EquipmentSlot slot) { return "animation.ember.idle"; }
    @Override public float scaleWidth() { return 1.05f; }
}

public static final EmberArmor EMBER = CinderArmor.register(REGISTRAR, new EmberArmor()); // ember_helmet, _chestplate, _leggings, _boots
```

Other hooks: `slots()` (subset of pieces), `model/texture(slot)`, `animation()`, `parts(slot)`, `bone(part)`,
`scaleHeight()`, `translucent()`, `tint(stack)`, `adjustBones(stack, slot, bones)` (per wearer, client),
`properties(slot, props)`, `createItem(type, props)` (custom `CinderArmorItem` subclass with own controllers).

Assets for `mod:name`: `geckolib/models/armor/name.geo.json`, `geckolib/animations/armor/name.animation.json`,
`textures/armor/name.png` (a slot `variant` adds `_variant`), plus `equipment/name.json` for the equipment asset.
Default bones: `armorHead`, `armorBody`, `armorLeftArm`, `armorRightArm`, `armorLeftLeg`, `armorRightLeg`,
`armorLeftBoot`, `armorRightBoot`.

## CinderItem and CinderTab

```java
public static final RegistrySupplier<Item> RUBY = CinderItem.builder(REGISTRAR, "ruby")
        .rarity(Rarity.RARE)   // also stacksTo, fireResistant, properties(..), factory(MyItem::new)
        .register();

public static final CinderTab TAB = CinderTab.create(REGISTRAR, "main", RUBY).add(RUBY); // itemGroup.mod.main
```

`CinderItem.tab(tab)` adds an item to a tab directly when the tab is created first.

## CinderDataGen

Loader independent: collects assets and data in memory and writes plain JSON, without the Fabric or NeoForge
datagen APIs. Output goes to `common/src/generated/resources`, which `common` already includes.

```java
public static void main(String[] args) {
    ExampleMod.define(); // create the content without registering it
    CinderDataGen.create("mymod")
            .blocks(REGISTRAR)                 // state, model, item, loot and name of every plain block
            .item("ruby")                      // generated item model, definition and name
            .lang("itemGroup.mymod.main", "My Mod")
            .shapeless("ruby_from_marble", "mymod:ruby", 2, "mymod:marble")
            .itemTag("gems", "mymod:ruby")
            .apply(ExampleWorldgen::generate)
            .run(Path.of("common/src/generated/resources"));
}
```

Also available: `cubeBlock`, `selfDrop`, `shaped`, `blockTag`, and raw `asset(path, json)` / `data(path, json)`.
Blocks with facing, extra properties or GeckoLib only get a name and a loot table; their models stay hand-written.

## Worldgen

`CinderOre` generates a configured feature, a placed feature and a NeoForge biome modifier:

```java
CinderOre.of("ruby_ore").block("mymod:ruby_ore").deepslate("mymod:deepslate_ruby_ore")
        .size(6).count(4).height(-64, 16).hideFromAir(0.5f).biomes("#minecraft:is_overworld");
```

Fabric has no data driven biome modifiers; add the placed feature there in code with
`BiomeModifications.addFeature(...)`.

## Examples

`common/src/example/` contains a working setup: `ExampleMod` (entry point, `init()` / `define()`), `ExampleBlocks`,
`ExampleItems`, `ExampleArmors` (`PlainArmor`, `EmberArmor`, `GhostArmor`), `ExampleWorldgen` and `ExampleDataGen`.
Call `ExampleMod.init()` from the loader entrypoint to use it. Textures, models and equipment assets are not included.

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
