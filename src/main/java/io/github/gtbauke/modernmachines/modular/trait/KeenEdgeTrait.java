package io.github.gtbauke.modernmachines.modular.trait;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class KeenEdgeTrait extends ToolTrait {
    public KeenEdgeTrait(Identifier id) {
        super(id);
    }

    @Override
    public float modifyAttackDamage(ItemStack tool, LivingEntity target, LivingEntity attacker, float currentDamage, int level) {
        if (attacker != null && attacker.fallDistance > 0.0f && !attacker.onGround()) {
            return currentDamage * (1.0f + 0.25f * level);
        }

        return currentDamage + (0.5f * level);
    }
}
