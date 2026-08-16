package io.github.gtbauke.modernmachines.client.gui.tab;

import io.github.gtbauke.modernmachines.client.gui.declarative.Column;
import io.github.gtbauke.modernmachines.client.gui.declarative.Divider;
import io.github.gtbauke.modernmachines.client.gui.declarative.Row;
import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import io.github.gtbauke.modernmachines.client.gui.window.SideTabWidget;
import io.github.gtbauke.modernmachines.machine.menu.AlloySmelterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class UpgradeSideTab extends SideTabWidget {
    private final AlloySmelterMenu menu;
    private final LabelWidget speedLabel;
    private final LabelWidget speedSubLabel;
    private final LabelWidget efficiencyLabel;
    private final LabelWidget efficiencySubLabel;

    public UpgradeSideTab(AlloySmelterMenu menu) {
        super(Component.translatable("gui.modernmachines.upgrades"), 112, 96, false);
        this.menu = menu;
        setExpandedSize(115, 145);

        getContentContainer().getFlexNode().setPadding(FlexInsets.of(4, 4, 4, 4));
        getContentContainer().getFlexNode().setAlignItems(AlignItems.CENTER);
        getContentContainer().getFlexNode().setGap(3);

        // 1. Upgrade Slots 2x2 Grid (Slots 4, 5, 6, 7)
        UiWidget slotsGrid = Column.of(
                Row.of(SlotWidget.of(menu.slots.get(4)), SlotWidget.of(menu.slots.get(5))).gap(2).center(),
                Row.of(SlotWidget.of(menu.slots.get(6)), SlotWidget.of(menu.slots.get(7))).gap(2).center()
        ).gap(2).center();
        getContentContainer().addChild(slotsGrid);

        // 2. Divider
        getContentContainer().addChild(Divider.horizontal());

        // 3. Machine Stats Header
        LabelWidget statsHeader = new LabelWidget(Component.literal("Machine Stats").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        statsHeader.setCentered(true);
        getContentContainer().addChild(statsHeader);

        // 4. Speed Metric Card
        speedLabel = new LabelWidget(Component.literal("Speed: 100%").withStyle(ChatFormatting.GRAY));
        speedLabel.setCentered(true);
        getContentContainer().addChild(speedLabel);

        speedSubLabel = new LabelWidget(Component.literal("~10.0s / operation").withStyle(ChatFormatting.DARK_GRAY));
        speedSubLabel.setCentered(true);
        getContentContainer().addChild(speedSubLabel);

        // 5. Fuel Efficiency Metric Card
        efficiencyLabel = new LabelWidget(Component.literal("Efficiency: 100%").withStyle(ChatFormatting.GRAY));
        efficiencyLabel.setCentered(true);
        getContentContainer().addChild(efficiencyLabel);

        efficiencySubLabel = new LabelWidget(Component.literal("~8.0 ops / coal").withStyle(ChatFormatting.DARK_GRAY));
        efficiencySubLabel.setCentered(true);
        getContentContainer().addChild(efficiencySubLabel);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (isExpanded()) {
            int speedPercent = menu.getSpeedMultiplierPercent();
            int effPercent = menu.getEfficiencyMultiplierPercent();

            double speedMult = speedPercent / 100.0;
            double effMult = effPercent / 100.0;

            ChatFormatting speedColor = speedPercent > 100 ? ChatFormatting.GREEN : (speedPercent < 100 ? ChatFormatting.RED : ChatFormatting.GRAY);
            ChatFormatting effColor = effPercent > 100 ? ChatFormatting.GREEN : (effPercent < 100 ? ChatFormatting.RED : ChatFormatting.GRAY);

            // Time calculation (Base: 10.0s = 200 ticks)
            double secondsPerOp = 10.0 / Math.max(0.1, speedMult);
            String formattedTime = String.format("~%.1fs / op", secondsPerOp);

            // Ops per Coal (Base: 8.0 ops per 1600-tick coal)
            double opsPerCoal = 8.0 * effMult;
            String formattedOps = String.format("~%.1f ops / coal", opsPerCoal);

            speedLabel.setText(Component.literal("⚡ Speed: " + speedPercent + "% (" + String.format("%.2f", speedMult) + "x)").withStyle(speedColor));
            speedSubLabel.setText(Component.literal(formattedTime).withStyle(ChatFormatting.DARK_GRAY));

            efficiencyLabel.setText(Component.literal("🔥 Efficiency: " + effPercent + "% (" + String.format("%.2f", effMult) + "x)").withStyle(effColor));
            efficiencySubLabel.setText(Component.literal(formattedOps).withStyle(ChatFormatting.DARK_GRAY));
        }

        super.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
    }
}
