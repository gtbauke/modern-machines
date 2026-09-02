package io.github.gtbauke.modernmachines.modular.block;

import io.github.gtbauke.modernmachines.modular.menu.PartBuilderMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

public class PartBuilderBlock extends Block {
    private static final Component CONTAINER_TITLE = Component.translatable("container.modernmachines.part_builder");

    public PartBuilderBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            player.openMenu(state.getMenuProvider(level, pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected MenuProvider getMenuProvider(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos) {
        return new SimpleMenuProvider((containerId, playerInventory, player) ->
                new PartBuilderMenu(containerId, playerInventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
    }
}
