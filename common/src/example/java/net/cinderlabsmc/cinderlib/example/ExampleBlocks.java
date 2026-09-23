package net.cinderlabsmc.cinderlib.example;

import org.jspecify.annotations.NonNull;

import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.cinderlabsmc.cinderlib.block.CinderBlockEntity;
import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public final class ExampleBlocks {

  public static final CinderBlockType<CinderBlockEntity> MARBLE = CinderBlock.builder(ExampleMod.REGISTRAR, "marble")
      .properties(p -> p.strength(3f).sound(SoundType.STONE).requiresCorrectToolForDrops())
      .register();

  public static final CinderBlockType<CinderBlockEntity> RUBY_ORE = CinderBlock
      .builder(ExampleMod.REGISTRAR, "ruby_ore")
      .properties(p -> p.strength(3f).sound(SoundType.STONE).requiresCorrectToolForDrops())
      .register();

  public static final CinderBlockType<CinderBlockEntity> DEEPSLATE_RUBY_ORE = CinderBlock
      .builder(ExampleMod.REGISTRAR, "deepslate_ruby_ore")
      .properties(p -> p.strength(4.5f).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops())
      .register();

  public static final CinderBlockType<CinderBlockEntity> WALL_LAMP = CinderBlock
      .builder(ExampleMod.REGISTRAR, "wall_lamp")
      .horizontalFacing()
      .property(BlockStateProperties.LIT, false)
      .shape(Block.box(4, 4, 12, 12, 12, 16))
      .properties(p -> p.noOcclusion().lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 15 : 0))
      .block(LampBlock::new)
      .register();

  static void load() {
  }

  private ExampleBlocks() {
  }

  public static final class LampBlock extends CinderBlock {
    public LampBlock(@NonNull CinderBlockType<?> type, @NonNull Properties properties) {
      super(type, properties);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
        @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
      level.setBlock(pos, state.cycle(BlockStateProperties.LIT), Block.UPDATE_ALL);
      return InteractionResult.SUCCESS;
    }
  }
}
