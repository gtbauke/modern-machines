package io.github.gtbauke.modernmachines.modular.trait;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class HeavyTrait extends ToolTrait {
    public HeavyTrait(Identifier id) {
        super(id);
    }

    @Override
    public void onAttack(ItemStack tool, LivingEntity target, LivingEntity attacker, int level) {
        var dx = attacker.getX() - target.getX();
        var dz = attacker.getZ() - target.getZ();
        var dist = Math.sqrt(dx * dx + dz * dz);
        if (dist > 0.001) {
            target.push(-dx / dist * 0.6 * level, 0.15 * level, -dz / dist * 0.6 * level);
            target.hurtMarked = true;
        }

        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40 * level, 1));
    }
}
