package io.github.gtbauke.modernmachines.modular.trait;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HellforgedTrait extends ToolTrait {
    public HellforgedTrait(Identifier id) {
        super(id);
    }

    @Override
    public void onAttack(ItemStack tool, LivingEntity target, LivingEntity attacker, int level) {
        target.igniteForSeconds(3 * level);
    }

    @Override
    public float modifyMiningSpeed(ItemStack tool, BlockState state, Player player, float currentSpeed, int level) {
        if (player != null && player.level().dimension() == Level.NETHER) {
            return currentSpeed * (1.0f + 0.25f * level);
        }

        return currentSpeed;
    }

    @Override
    public float modifyAttackDamage(ItemStack tool, LivingEntity target, LivingEntity attacker, float currentDamage, int level) {
        if (attacker != null && attacker.level().dimension() == Level.NETHER) {
            return currentDamage + (2.0f * level);
        }

        return currentDamage;
    }
}
