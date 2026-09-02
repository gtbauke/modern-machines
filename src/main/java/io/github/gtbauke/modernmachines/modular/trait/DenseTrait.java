package io.github.gtbauke.modernmachines.modular.trait;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class DenseTrait extends ToolTrait {
    public DenseTrait(Identifier id) {
        super(id);
    }

    @Override
    public void onAttack(ItemStack tool, LivingEntity target, LivingEntity attacker, int level) {
        var dx = attacker.getX() - target.getX();
        var dz = attacker.getZ() - target.getZ();
        var dist = Math.sqrt(dx * dx + dz * dz);
        if (dist > 0.001) {
            target.push(-dx / dist * 0.35 * level, 0.1 * level, -dz / dist * 0.35 * level);
            target.hurtMarked = true;
        }
    }
}
