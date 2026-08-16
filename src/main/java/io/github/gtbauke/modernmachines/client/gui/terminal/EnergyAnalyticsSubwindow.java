package io.github.gtbauke.modernmachines.client.gui.terminal;

import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.widget.ButtonWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.ProgressBarWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.SparklineGraphWidget;
import io.github.gtbauke.modernmachines.client.gui.window.WindowWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class EnergyAnalyticsSubwindow extends WindowWidget {
    private int selectedMode = 1; // 0 = Eco, 1 = Normal, 2 = Overclock

    public EnergyAnalyticsSubwindow() {
        super(Component.literal("⚡ Energy Analytics"), 220, 180);
        setClosable(true);
        setMinimizable(true);

        FlexContainer content = getContentContainer();
        content.getFlexNode().setGap(4);
        content.getFlexNode().setPadding(FlexInsets.all(4));
        content.getFlexNode().setAlignItems(AlignItems.CENTER);

        // 1. Live Energy Flow Graph
        SparklineGraphWidget graph = new SparklineGraphWidget(208, 48, 28, () -> {
            long t = System.currentTimeMillis();
            double base = selectedMode == 0 ? 60.0 : (selectedMode == 1 ? 120.0 : 180.0);
            return base + Math.sin(t * 0.003) * 25.0 + Math.cos(t * 0.007) * 15.0;
        });
        graph.setMaxValue(220.0);
        graph.setLineColor(0xFF00E5FF);
        graph.setFillColor(0x2200E5FF);
        content.addChild(graph);

        // 2. Stored Energy Bar
        FlexContainer barRow = new FlexContainer(FlexDirection.ROW);
        barRow.getFlexNode().setSize(208, 14);
        barRow.getFlexNode().setAlignItems(AlignItems.CENTER);
        barRow.getFlexNode().setJustifyContent(JustifyContent.SPACE_BETWEEN);

        LabelWidget capLabel = new LabelWidget(Component.literal("16.4k / 20k RF").withStyle(ChatFormatting.AQUA));
        capLabel.setShadow(false);
        barRow.addChild(capLabel);

        ProgressBarWidget bar = new ProgressBarWidget(ProgressBarWidget.ProgressType.LINEAR_HORIZONTAL, () -> 0.82);
        bar.getFlexNode().setSize(110, 8);
        barRow.addChild(bar);
        content.addChild(barRow);

        // 3. Stats row
        FlexContainer statsRow = new FlexContainer(FlexDirection.ROW);
        statsRow.getFlexNode().setSize(208, 14);
        statsRow.getFlexNode().setAlignItems(AlignItems.CENTER);
        statsRow.getFlexNode().setJustifyContent(JustifyContent.SPACE_BETWEEN);

        LabelWidget inLabel = new LabelWidget(Component.literal("In: +120 RF/t").withStyle(ChatFormatting.GREEN));
        inLabel.setShadow(false);
        LabelWidget outLabel = new LabelWidget(Component.literal("Out: -40 RF/t").withStyle(ChatFormatting.RED));
        outLabel.setShadow(false);
        LabelWidget netLabel = new LabelWidget(Component.literal("Net: +80 RF/t").withStyle(ChatFormatting.YELLOW));
        netLabel.setShadow(false);

        statsRow.addChild(inLabel);
        statsRow.addChild(outLabel);
        statsRow.addChild(netLabel);
        content.addChild(statsRow);

        // 4. Power Profile Mode Selector
        FlexContainer modeRow = new FlexContainer(FlexDirection.ROW);
        modeRow.getFlexNode().setSize(208, 22);
        modeRow.getFlexNode().setGap(2);
        modeRow.getFlexNode().setJustifyContent(JustifyContent.SPACE_BETWEEN);

        ButtonWidget ecoBtn = new ButtonWidget(Component.literal("Eco (50%)"), b -> selectedMode = 0);
        ecoBtn.getFlexNode().setSize(66, 18);
        ecoBtn.setTooltip(Component.literal("Power-saving profile: 50% speed and consumption"));

        ButtonWidget normBtn = new ButtonWidget(Component.literal("Standard"), b -> selectedMode = 1);
        normBtn.getFlexNode().setSize(66, 18);
        normBtn.setTooltip(Component.literal("Standard operating profile"));

        ButtonWidget ocBtn = new ButtonWidget(Component.literal("Turbo (150%)"), b -> selectedMode = 2);
        ocBtn.getFlexNode().setSize(66, 18);
        ocBtn.setTooltip(Component.literal("Overclocked profile: 150% throughput"));

        modeRow.addChild(ecoBtn);
        modeRow.addChild(normBtn);
        modeRow.addChild(ocBtn);
        content.addChild(modeRow);
    }
}
