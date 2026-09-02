package io.github.gtbauke.modernmachines.modular.trait;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TemperedTrait extends ToolTrait {
    public TemperedTrait(Identifier id) {
        super(id);
    }

    @Override
    public float modifyMiningSpeed(ItemStack tool, BlockState state, Player player, float currentSpeed, int level) {
        return currentSpeed * (1.0f + 0.10f * level);
    }

    @Override
    public int onDamageTool(ItemStack tool, int amount, LivingEntity user, int level) {
        if (user.getRandom().nextFloat() < 0.12f * level) {
            return 0;
        }

        return amount;
    }
}
