package io.github.gtbauke.modernmachines.modular.item;

import java.util.Optional;
import java.util.function.Consumer;

import io.github.gtbauke.modernmachines.api.modular.ToolPartType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class PatternItem extends Item {
    private final Optional<ToolPartType> targetPart;

    public PatternItem(Optional<ToolPartType> targetPart, Properties properties) {
        super(properties);
        this.targetPart = targetPart;
    }

    public Optional<ToolPartType> getTargetPart() {
        return targetPart;
    }

    public boolean isBlank() {
        return targetPart.isEmpty();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        if (targetPart.isPresent()) {
            tooltip.accept(Component.translatable("tooltip.modernmachines.pattern_for",
                    Component.translatable("part_type.modernmachines." + targetPart.get().getSerializedName())).withStyle(ChatFormatting.GOLD));
            tooltip.accept(Component.translatable("tooltip.modernmachines.material_cost", targetPart.get().getMaterialCost()).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.accept(Component.translatable("tooltip.modernmachines.blank_pattern_desc").withStyle(ChatFormatting.DARK_GRAY));
        }
        super.appendHoverText(stack, context, display, tooltip, flag);
    }
}
