package net.cinderlabsmc.cinderlib.example;

import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.cinderlabsmc.cinderlib.block.CinderBlockEntity;
import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

public final class Battery extends CinderBlockEntity {

    public static final CinderBlockType<Battery> TYPE = CinderBlock.builder(ExampleBlocks.REGISTRAR, "battery")
            .properties(p -> p.strength(2f))
            .blockEntity(Battery::new)
            .register();

    public static final long CAPACITY = 10_000;

    private long energy;

    public Battery(@NonNull BlockEntityType<?> type, @NonNull BlockPos pos, @NonNull BlockState state) {
        super(type, pos, state);
    }

    public long insert(long amount) {
        long accepted = Math.min(amount, CAPACITY - energy);
        if (accepted > 0) {
            energy += accepted;
            setChanged();
        }
        return accepted;
    }

    @Override
    public @NonNull InteractionResult onUse(@NonNull Player player, @NonNull BlockHitResult hit) {
        if (player instanceof ServerPlayer) {
            player.sendSystemMessage(Component.literal(energy + " / " + CAPACITY + " FE"));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putLong("energy", energy);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        energy = input.getLongOr("energy", 0);
    }
}
