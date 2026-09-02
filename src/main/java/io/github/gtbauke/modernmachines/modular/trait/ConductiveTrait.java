package io.github.gtbauke.modernmachines.modular.trait;

import org.jspecify.annotations.Nullable;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class ConductiveTrait extends ToolTrait {
    public ConductiveTrait(Identifier id) {
        super(id);
    }

    @Override
    public void onInventoryTick(ItemStack tool, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot, int traitLevel) {
        if (slot != EquipmentSlot.MAINHAND || !(entity instanceof LivingEntity living)) {
            return;
        }

        if (entity.tickCount % 5 != 0) {
            return;
        }

        var radius = 2.5 + traitLevel * 1.5;
        var box = new AABB(
                living.getX() - radius, living.getY() - radius, living.getZ() - radius,
                living.getX() + radius, living.getY() + radius, living.getZ() + radius
        );

        var items = level.getEntitiesOfClass(ItemEntity.class, box);
        for (var item : items) {
            if (!item.isAlive() || item.hasPickUpDelay()) {
                continue;
            }

            var motion = living.position().subtract(item.position()).normalize().scale(0.35);
            item.setDeltaMovement(motion);
        }
    }

    @Override
    public void onAttack(ItemStack tool, LivingEntity target, LivingEntity attacker, int level) {
        if (target.level().isRainingAt(target.blockPosition())) {
            target.hurt(attacker.damageSources().lightningBolt(), 2.0f * level);
        }
    }
}
