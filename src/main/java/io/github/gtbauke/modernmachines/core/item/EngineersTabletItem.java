package io.github.gtbauke.modernmachines.core.item;

import io.github.gtbauke.modernmachines.client.gui.terminal.TerminalScreenOpener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class EngineersTabletItem extends Item {
    public EngineersTabletItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult use(Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        if (level.isClientSide()) {
            TerminalScreenOpener.openTerminal();
        }

        return InteractionResult.SUCCESS;
    }
}
