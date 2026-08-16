package io.github.gtbauke.modernmachines.modular.item;

import java.util.function.Consumer;

import io.github.gtbauke.modernmachines.api.modular.MaterialStatsManager;
import io.github.gtbauke.modernmachines.api.modular.MaterialTrait;
import io.github.gtbauke.modernmachines.api.modular.ToolPartType;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.core.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class ToolPartItem extends Item {
    private final ToolPartType partType;
    private final Material material;

    public ToolPartItem(ToolPartType partType, Material material, Properties properties) {
        super(properties);
        this.partType = partType;
        this.material = material;
    }

    public ToolPartType getPartType() {
        return partType;
    }

    public Material getMaterial() {
        return material;
    }

    public static Identifier getMaterialId(ItemStack stack) {
        if (stack.has(ModDataComponents.MATERIAL_ID.get())) {
            return stack.get(ModDataComponents.MATERIAL_ID.get());
        }
        if (stack.getItem() instanceof ToolPartItem partItem && partItem.getMaterial() != null) {
            return partItem.getMaterial().getId();
        }
        return null;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.has(ModDataComponents.MATERIAL_ID.get())) {
            Identifier matId = stack.get(ModDataComponents.MATERIAL_ID.get());
            String matName = MaterialStatsManager.getStats(matId)
                    .map(s -> s.getEffectiveDisplayName())
                    .orElse(matId.getPath());
            return Component.literal(matName + " " + partType.getDisplayName());
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        Identifier matId = getMaterialId(stack);
        if (matId == null) {
            super.appendHoverText(stack, context, display, tooltip, flag);
            return;
        }

        MaterialStatsManager.getStats(matId).ifPresentOrElse(stats -> {
            tooltip.accept(Component.literal("Material: ").append(Component.literal(stats.getEffectiveDisplayName()).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));

            switch (partType.getSlot()) {
                case HEAD -> stats.head().ifPresent(h -> {
                    tooltip.accept(Component.translatable("tooltip.modernmachines.stat.durability", h.durability()).withStyle(ChatFormatting.DARK_GREEN));
                    tooltip.accept(Component.translatable("tooltip.modernmachines.stat.mining_speed", String.format("%.1f", h.miningSpeed())).withStyle(ChatFormatting.BLUE));
                    tooltip.accept(Component.translatable("tooltip.modernmachines.stat.attack_damage", String.format("%.1f", h.attackDamage())).withStyle(ChatFormatting.RED));
                    tooltip.accept(Component.translatable("tooltip.modernmachines.stat.harvest_tier", h.harvestTier()).withStyle(ChatFormatting.GOLD));
                });
                case HANDLE -> stats.handle().ifPresent(h -> {
                    tooltip.accept(Component.translatable("tooltip.modernmachines.stat.durability_mult", String.format("%.2fx", h.durabilityMultiplier())).withStyle(ChatFormatting.DARK_GREEN));
                    tooltip.accept(Component.translatable("tooltip.modernmachines.stat.speed_mult", String.format("%.2fx", h.miningSpeedMultiplier())).withStyle(ChatFormatting.BLUE));
                });
                case BINDING -> stats.binding().ifPresent(b -> {
                    tooltip.accept(Component.translatable("tooltip.modernmachines.stat.bonus_durability", b.bonusDurability()).withStyle(ChatFormatting.DARK_GREEN));
                });
                case TIP, GRIP -> stats.attachment().ifPresent(a -> {
                    tooltip.accept(Component.translatable("tooltip.modernmachines.stat.bonus_durability", a.bonusDurability()).withStyle(ChatFormatting.DARK_GREEN));
                    tooltip.accept(Component.translatable("tooltip.modernmachines.stat.attack_bonus", String.format("+%.1f", a.attackDamageBonus())).withStyle(ChatFormatting.RED));
                });
            }

            if (!stats.traits().isEmpty()) {
                tooltip.accept(Component.translatable("tooltip.modernmachines.traits_header").withStyle(ChatFormatting.YELLOW));
                for (MaterialTrait trait : stats.traits()) {
                    tooltip.accept(Component.literal(" • ").append(trait.getDisplayName()).withStyle(ChatFormatting.AQUA));
                }
            }
        }, () -> {
            if (material != null) {
                tooltip.accept(Component.translatable("tooltip.modernmachines.part_material",
                        Component.translatable(material.getTranslationKey())).withStyle(ChatFormatting.GRAY));
            }
        });

        super.appendHoverText(stack, context, display, tooltip, flag);
    }
}
