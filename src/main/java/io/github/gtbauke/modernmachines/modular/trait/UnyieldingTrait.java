package io.github.gtbauke.modernmachines.modular.trait;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class UnyieldingTrait extends ToolTrait {
    public UnyieldingTrait(Identifier id) {
        super(id);
    }

    @Override
    public float modifyMiningSpeed(ItemStack tool, BlockState state, Player player, float currentSpeed, int level) {
        if (state != null && player != null && state.getDestroySpeed(player.level(), player.blockPosition()) >= 4.0f) {
            return currentSpeed * (1.0f + 0.5f * level);
        }

        return currentSpeed;
    }
}
