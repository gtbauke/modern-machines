package io.github.gtbauke.modernmachines.modular.trait;

import org.jspecify.annotations.Nullable;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ResilientTrait extends ToolTrait {
    public ResilientTrait(Identifier id) {
        super(id);
    }

    @Override
    public int onDamageTool(ItemStack tool, int amount, LivingEntity user, int level) {
        if (user.getRandom().nextFloat() < 0.20f * level) {
            return 0;
        }

        return amount;
    }

    @Override
    public void onInventoryTick(ItemStack tool, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot, int traitLevel) {
        if (slot == EquipmentSlot.MAINHAND && entity instanceof LivingEntity living && living.tickCount % 60 == 0) {
            if (living.hasEffect(MobEffects.WEAKNESS)) {
                living.removeEffect(MobEffects.WEAKNESS);
            }
        }
    }
}
