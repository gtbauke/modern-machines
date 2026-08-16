package io.github.gtbauke.modernmachines.client.gui.tab;

import io.github.gtbauke.modernmachines.client.gui.declarative.Card;
import io.github.gtbauke.modernmachines.client.gui.declarative.Divider;
import io.github.gtbauke.modernmachines.client.gui.declarative.GhostIcons;
import io.github.gtbauke.modernmachines.client.gui.declarative.MetricCard;
import io.github.gtbauke.modernmachines.client.gui.declarative.MetricRow;
import io.github.gtbauke.modernmachines.client.gui.declarative.SlotGrid;
import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget;
import io.github.gtbauke.modernmachines.machine.menu.AlloySmelterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class UpgradeContentWidget extends FlexContainer {

    public UpgradeContentWidget(AlloySmelterMenu menu) {
        super(FlexDirection.COLUMN);

        getFlexNode().setPadding(FlexInsets.of(4, 6, 6, 6));
        getFlexNode().setAlignItems(AlignItems.CENTER);
        getFlexNode().setGap(4);

        // 1. Header
        addChild(LabelWidget.of("Installed Upgrades").color(0xFFAAAAAA).centered());

        // 2. 2x2 Upgrade Slots Grid with ghost icons
        addChild(Card.of(
            SlotGrid.of(2, 2, 3,
                SlotWidget.of(menu.slots.get(4)).ghostIcon(GhostIcons.UPGRADE),
                SlotWidget.of(menu.slots.get(5)).ghostIcon(GhostIcons.UPGRADE),
                SlotWidget.of(menu.slots.get(6)).ghostIcon(GhostIcons.UPGRADE),
                SlotWidget.of(menu.slots.get(7)).ghostIcon(GhostIcons.UPGRADE)
            )
        ).padding(FlexInsets.all(4)));

        // 3. Divider
        addChild(Divider.horizontal());

        // 4. Reactive Metric Card (Live bound suppliers)
        addChild(MetricCard.of(
            Component.literal("Machine Stats").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
            MetricRow.of(
                () -> {
                    int speed = menu.getSpeedMultiplierPercent();
                    ChatFormatting color = speed > 100 ? ChatFormatting.GREEN : (speed < 100 ? ChatFormatting.RED : ChatFormatting.GRAY);
                    return Component.literal("⚡ Speed: " + speed + "%").withStyle(color);
                },
                () -> {
                    double speedMult = menu.getSpeedMultiplierPercent() / 100.0;
                    double sec = 10.0 / Math.max(0.1, speedMult);
                    return Component.literal(String.format("~%.1fs / op (%.2fx)", sec, speedMult)).withStyle(ChatFormatting.GRAY);
                }
            ),
            MetricRow.of(
                () -> {
                    int eff = menu.getEfficiencyMultiplierPercent();
                    ChatFormatting color = eff > 100 ? ChatFormatting.GREEN : (eff < 100 ? ChatFormatting.RED : ChatFormatting.GRAY);
                    return Component.literal("🔥 Efficiency: " + eff + "%").withStyle(color);
                },
                () -> {
                    double effMult = menu.getEfficiencyMultiplierPercent() / 100.0;
                    double ops = 8.0 * effMult;
                    return Component.literal(String.format("~%.1f ops / coal (%.2fx)", ops, effMult)).withStyle(ChatFormatting.GRAY);
                }
            )
        ));
    }
}
