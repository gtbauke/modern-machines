package io.github.gtbauke.modernmachines.client.gui.tab;

import io.github.gtbauke.modernmachines.client.gui.declarative.Card;
import io.github.gtbauke.modernmachines.client.gui.declarative.Column;
import io.github.gtbauke.modernmachines.client.gui.declarative.Divider;
import io.github.gtbauke.modernmachines.client.gui.declarative.Row;
import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import io.github.gtbauke.modernmachines.machine.menu.AlloySmelterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class UpgradeContentWidget extends FlexContainer {
    private final AlloySmelterMenu menu;
    private final LabelWidget speedLabel;
    private final LabelWidget speedSubLabel;
    private final LabelWidget efficiencyLabel;
    private final LabelWidget efficiencySubLabel;

    public UpgradeContentWidget(AlloySmelterMenu menu) {
        super(FlexDirection.COLUMN);
        this.menu = menu;

        getFlexNode().setPadding(FlexInsets.of(4, 6, 6, 6));
        getFlexNode().setAlignItems(AlignItems.CENTER);
        getFlexNode().setGap(4);

        // 1. Slots Section Header
        LabelWidget slotsHeader = new LabelWidget(Component.literal("Installed Upgrades").withStyle(ChatFormatting.GRAY));
        slotsHeader.setCentered(true);
        addChild(slotsHeader);

        // 2. Upgrade Slots 2x2 Grid (Slots 4, 5, 6, 7) - Auto-width compact card, centered
        UiWidget slotsGrid = Card.of(
                Column.of(
                        Row.of(SlotWidget.of(menu.slots.get(4)), SlotWidget.of(menu.slots.get(5))).gap(3).center(),
                        Row.of(SlotWidget.of(menu.slots.get(6)), SlotWidget.of(menu.slots.get(7))).gap(3).center()
                ).gap(3).center()
        ).padding(FlexInsets.all(4));
        addChild(slotsGrid);

        // 3. Divider
        addChild(Divider.horizontal());

        // 4. Machine Stats Card (Full width with padding)
        speedLabel = new LabelWidget(Component.literal("Speed: 100%").withStyle(ChatFormatting.GRAY));
        speedLabel.setCentered(true);

        speedSubLabel = new LabelWidget(Component.literal("~10.0s / op").withStyle(ChatFormatting.DARK_GRAY));
        speedSubLabel.setCentered(true);

        efficiencyLabel = new LabelWidget(Component.literal("Efficiency: 100%").withStyle(ChatFormatting.GRAY));
        efficiencyLabel.setCentered(true);

        efficiencySubLabel = new LabelWidget(Component.literal("~8.0 ops / coal").withStyle(ChatFormatting.DARK_GRAY));
        efficiencySubLabel.setCentered(true);

        UiWidget statsCard = Card.of(
                Column.of(
                        LabelWidget.of(Component.literal("Machine Stats").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)).centered(),
                        speedLabel,
                        speedSubLabel,
                        efficiencyLabel,
                        efficiencySubLabel
                ).gap(2).center()
        ).padding(FlexInsets.of(6, 6, 6, 6)).matchParentWidth();
        addChild(statsCard);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;

        int speedPercent = menu.getSpeedMultiplierPercent();
        int effPercent = menu.getEfficiencyMultiplierPercent();

        double speedMult = speedPercent / 100.0;
        double effMult = effPercent / 100.0;

        ChatFormatting speedColor = speedPercent > 100 ? ChatFormatting.GREEN : (speedPercent < 100 ? ChatFormatting.RED : ChatFormatting.GRAY);
        ChatFormatting effColor = effPercent > 100 ? ChatFormatting.GREEN : (effPercent < 100 ? ChatFormatting.RED : ChatFormatting.GRAY);

        double secondsPerOp = 10.0 / Math.max(0.1, speedMult);
        String formattedTime = String.format("~%.1fs / op", secondsPerOp);

        double opsPerCoal = 8.0 * effMult;
        String formattedOps = String.format("~%.1f ops / coal", opsPerCoal);

        speedLabel.setText(Component.literal("⚡ Speed: " + speedPercent + "%").withStyle(speedColor));
        speedSubLabel.setText(Component.literal(formattedTime + " (" + String.format("%.2f", speedMult) + "x)").withStyle(ChatFormatting.GRAY));

        efficiencyLabel.setText(Component.literal("🔥 Efficiency: " + effPercent + "%").withStyle(effColor));
        efficiencySubLabel.setText(Component.literal(formattedOps + " (" + String.format("%.2f", effMult) + "x)").withStyle(ChatFormatting.GRAY));

        super.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
    }
}
