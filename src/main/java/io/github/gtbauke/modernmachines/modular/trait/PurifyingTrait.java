package io.github.gtbauke.modernmachines.modular.trait;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PurifyingTrait extends ToolTrait {
    public PurifyingTrait(Identifier id) {
        super(id);
    }

    @Override
    public float modifyAttackDamage(ItemStack tool, LivingEntity target, LivingEntity attacker, float currentDamage, int level) {
        if (target != null && target.isInvertedHealAndHarm()) {
            return currentDamage + (3.5f * level);
        }

        return currentDamage;
    }
}
