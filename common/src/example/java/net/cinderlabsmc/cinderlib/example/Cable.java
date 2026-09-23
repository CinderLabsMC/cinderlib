package net.cinderlabsmc.cinderlib.example;

import java.util.EnumMap;
import java.util.Map;

import org.jspecify.annotations.NonNull;

import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.cinderlabsmc.cinderlib.block.CinderBlockEntityType;
import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.cinderlabsmc.cinderlib.block.geo.CinderBones;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class Cable extends CinderGeoBlockEntity {

  public enum Variant {
    COPPER("copper_cable", 4),
    GOLD("gold_cable", 6);

    final String id;
    final int width;

    Variant(String id, int width) {
      this.id = id;
      this.width = width;
    }
  }

  public static final CinderBlockEntityType<Cable> BLOCK_ENTITY = ExampleMod.REGISTRAR.blockEntity("cable", Cable::new);

  public static final Map<Variant, CinderBlockType<Cable>> VARIANTS = new EnumMap<>(Variant.class);

  static {
    for (var variant : Variant.values()) {
      VARIANTS.put(variant, register(variant));
    }
  }

  private static CinderBlockType<Cable> register(Variant variant) {
    var shapes = createShapes(variant.width);

    return CinderBlock.builder(ExampleMod.REGISTRAR, variant.id)
        .properties(p -> p.strength(0.5f))
        .blockEntity(BLOCK_ENTITY)
        .data(variant)
        .dynamicShape((state, level, pos, context) -> shapes[connectionMask(level, pos)])
        .geo(geo -> geo
            .model(ExampleMod.REGISTRAR.id("block/cable"))
            .animation(ExampleMod.REGISTRAR.id("block/cable")))
        .register();
  }

  public Cable(@NonNull BlockEntityType<?> type, @NonNull BlockPos pos, @NonNull BlockState state) {
    super(type, pos, state);
  }

  public @NonNull Variant variant() {
    return data(Variant.class);
  }

  public static int connectionMask(@NonNull BlockGetter level, @NonNull BlockPos pos) {
    int mask = 0;
    for (var dir : Direction.values()) {
      var neighbor = level.getBlockEntity(pos.relative(dir));
      if (neighbor instanceof Cable || neighbor instanceof Battery || neighbor instanceof Generator) {
        mask |= 1 << dir.ordinal();
      }
    }
    return mask;
  }

  @Override
  public void adjustBones(@NonNull CinderBones bones) {
    int mask = level == null ? 0 : connectionMask(level, worldPosition);
    float scale = variant().width / 4f;

    for (var dir : Direction.values()) {
      bones.visible(dir.getSerializedName(), (mask & (1 << dir.ordinal())) != 0);
    }
    bones.scale("core", scale, scale, scale);
  }

  private static VoxelShape[] createShapes(int width) {
    double min = (16 - width) / 2d;
    double max = min + width;

    var core = Block.box(min, min, min, max, max, max);
    var arms = new VoxelShape[6];
    arms[Direction.DOWN.ordinal()] = Block.box(min, 0, min, max, min, max);
    arms[Direction.UP.ordinal()] = Block.box(min, max, min, max, 16, max);
    arms[Direction.NORTH.ordinal()] = Block.box(min, min, 0, max, max, min);
    arms[Direction.SOUTH.ordinal()] = Block.box(min, min, max, max, max, 16);
    arms[Direction.WEST.ordinal()] = Block.box(0, min, min, min, max, max);
    arms[Direction.EAST.ordinal()] = Block.box(max, min, min, 16, max, max);

    var shapes = new VoxelShape[1 << Direction.values().length];
    for (int mask = 0; mask < shapes.length; mask++) {
      var shape = core;
      for (var dir : Direction.values()) {
        if ((mask & (1 << dir.ordinal())) != 0) {
          shape = Shapes.or(shape, arms[dir.ordinal()]);
        }
      }
      shapes[mask] = shape;
    }
    return shapes;
  }
}
