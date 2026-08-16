package io.github.gtbauke.modernmachines.machine.upgrade;

import java.util.List;

import io.github.gtbauke.modernmachines.api.machine.upgrade.IUpgradeItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public abstract class UpgradeItem extends Item implements IUpgradeItem {
    public static final int MAX_UPGRADE_STACK = 4;

    public UpgradeItem(Properties properties) {
        super(properties.stacksTo(MAX_UPGRADE_STACK));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, java.util.function.Consumer<Component> tooltipOutput, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipOutput, flag);
        for (Component line : getUpgradeTooltips(stack)) {
            tooltipOutput.accept(line);
        }
    }
}
