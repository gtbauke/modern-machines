package io.github.gtbauke.modernmachines.machine.block;

import io.github.gtbauke.modernmachines.client.gui.terminal.TerminalScreenOpener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class EngineersTerminalBlock extends Block {
    public EngineersTerminalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            TerminalScreenOpener.openTerminal();
        }
        return InteractionResult.SUCCESS;
    }
}
