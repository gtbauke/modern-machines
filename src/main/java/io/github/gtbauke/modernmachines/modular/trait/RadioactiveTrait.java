package io.github.gtbauke.modernmachines.modular.trait;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class RadioactiveTrait extends ToolTrait {
    public RadioactiveTrait(Identifier id) {
        super(id);
    }

    @Override
    public void onAttack(ItemStack tool, LivingEntity target, LivingEntity attacker, int level) {
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60 * level, Math.max(0, level - 1)));
        target.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * level, 0));
    }
}
