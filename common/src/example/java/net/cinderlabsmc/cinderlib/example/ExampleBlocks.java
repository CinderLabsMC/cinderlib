package net.cinderlabsmc.cinderlib.example;

import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.cinderlabsmc.cinderlib.block.CinderBlockEntity;
import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.cinderlabsmc.cinderlib.block.CinderRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

public final class ExampleBlocks {

    public static final CinderRegistrar REGISTRAR = CinderRegistrar.create("cinderexample");

    public static final CinderBlockType<CinderBlockEntity> MARBLE = CinderBlock.builder(REGISTRAR, "marble")
            .properties(p -> p.strength(3f).sound(SoundType.STONE).requiresCorrectToolForDrops())
            .register();

    public static final CinderBlockType<CinderBlockEntity> WALL_LAMP = CinderBlock.builder(REGISTRAR, "wall_lamp")
            .horizontalFacing()
            .property(BlockStateProperties.LIT, false)
            .shape(Block.box(4, 4, 12, 12, 12, 16))
            .properties(p -> p.noOcclusion().lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 15 : 0))
            .block(LampBlock::new)
            .register();

    public static void init() {
        REGISTRAR.load(Battery.class, Generator.class, RackShelf.class, Cable.class);
        REGISTRAR.register();
    }

    private ExampleBlocks() {
    }

    public static final class LampBlock extends CinderBlock {
        public LampBlock(@NonNull CinderBlockType<?> type, @NonNull Properties properties) {
            super(type, properties);
        }

        @Override
        protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
            level.setBlock(pos, state.cycle(BlockStateProperties.LIT), Block.UPDATE_ALL);
            return InteractionResult.SUCCESS;
        }
    }
}
