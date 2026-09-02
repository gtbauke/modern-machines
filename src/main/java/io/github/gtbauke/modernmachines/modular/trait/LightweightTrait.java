package io.github.gtbauke.modernmachines.modular.trait;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class LightweightTrait extends ToolTrait {
    public LightweightTrait(Identifier id) {
        super(id);
    }

    @Override
    public float modifyMiningSpeed(ItemStack tool, BlockState state, Player player, float currentSpeed, int level) {
        return currentSpeed * (1.0f + 0.15f * level);
    }
}
