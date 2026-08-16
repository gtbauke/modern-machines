package io.github.gtbauke.modernmachines.client.gui.terminal;

import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.widget.ButtonWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.ProgressBarWidget;
import io.github.gtbauke.modernmachines.client.gui.window.WindowWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class HubSubwindow extends WindowWidget {

    public HubSubwindow(
            Runnable openEnergy,
            Runnable openSideIo,
            Runnable openInspector
    ) {
        super(Component.literal("Terminal Hub"), 210, 180);
        setClosable(true);
        setMinimizable(true);

        FlexContainer content = getContentContainer();
        content.getFlexNode().setGap(4);
        content.getFlexNode().setPadding(FlexInsets.all(4));
        content.getFlexNode().setAlignItems(AlignItems.CENTER);

        // 1. Status Pill Row
        FlexContainer statusRow = new FlexContainer(FlexDirection.ROW);
        statusRow.getFlexNode().setSize(198, 18);
        statusRow.getFlexNode().setAlignItems(AlignItems.CENTER);
        statusRow.getFlexNode().setJustifyContent(JustifyContent.SPACE_BETWEEN);

        LabelWidget statusPill = new LabelWidget(Component.literal(" ONLINE ").withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD));
        statusPill.setBackgroundColor(0xFF00E676);
        statusPill.setColor(0xFF003311);
        statusPill.getFlexNode().setSize(54, 12);
        statusPill.setCentered(true);
        statusPill.setShadow(false);
        statusRow.addChild(statusPill);

        LabelWidget latencyLabel = new LabelWidget(Component.literal("Nodes: 3  |  12ms").withStyle(ChatFormatting.GRAY));
        latencyLabel.setShadow(false);
        statusRow.addChild(latencyLabel);
        content.addChild(statusRow);

        // 2. Machine Nodes Overview Panel
        FlexContainer nodesPanel = new FlexContainer(FlexDirection.COLUMN);
        nodesPanel.getFlexNode().setSize(198, 62);
        nodesPanel.getFlexNode().setGap(2);
        nodesPanel.getFlexNode().setPadding(FlexInsets.all(3));

        LabelWidget node1 = new LabelWidget(Component.literal("⚡ Alloy Smelter #1: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("Smelting (100%)").withStyle(ChatFormatting.GREEN)));
        node1.setShadow(false);

        LabelWidget node2 = new LabelWidget(Component.literal("🔨 Part Builder #1: ").withStyle(ChatFormatting.AQUA)
                .append(Component.literal("Ready").withStyle(ChatFormatting.WHITE)));
        node2.setShadow(false);

        LabelWidget node3 = new LabelWidget(Component.literal("⚙️ Tinkering Table: ").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal("Standby").withStyle(ChatFormatting.GRAY)));
        node3.setShadow(false);

        nodesPanel.addChild(node1);
        nodesPanel.addChild(node2);
        nodesPanel.addChild(node3);
        content.addChild(nodesPanel);

        // 3. System Buffer Progress
        FlexContainer buffRow = new FlexContainer(FlexDirection.ROW);
        buffRow.getFlexNode().setSize(198, 14);
        buffRow.getFlexNode().setAlignItems(AlignItems.CENTER);
        buffRow.getFlexNode().setJustifyContent(JustifyContent.SPACE_BETWEEN);

        LabelWidget buffLabel = new LabelWidget(Component.literal("Core Buffer:").withStyle(ChatFormatting.DARK_GRAY));
        buffLabel.setShadow(false);
        buffRow.addChild(buffLabel);

        ProgressBarWidget prog = new ProgressBarWidget(ProgressBarWidget.ProgressType.LINEAR_HORIZONTAL, () -> 0.84);
        prog.getFlexNode().setSize(110, 8);
        buffRow.addChild(prog);
        content.addChild(buffRow);

        // 4. Quick Launch Action Buttons
        FlexContainer btnRow = new FlexContainer(FlexDirection.ROW);
        btnRow.getFlexNode().setSize(198, 22);
        btnRow.getFlexNode().setGap(3);
        btnRow.getFlexNode().setJustifyContent(JustifyContent.SPACE_BETWEEN);

        ButtonWidget energyBtn = new ButtonWidget(Component.literal("⚡ Energy"), b -> openEnergy.run());
        energyBtn.getFlexNode().setSize(62, 18);
        energyBtn.setTooltip(Component.literal("Open Energy Analytics & Flow Graph"));

        ButtonWidget sideIoBtn = new ButtonWidget(Component.literal("🔄 Side I/O"), b -> openSideIo.run());
        sideIoBtn.getFlexNode().setSize(62, 18);
        sideIoBtn.setTooltip(Component.literal("Open Side I/O Configuration Matrix"));

        ButtonWidget codexBtn = new ButtonWidget(Component.literal("📖 Codex"), b -> openInspector.run());
        codexBtn.getFlexNode().setSize(62, 18);
        codexBtn.setTooltip(Component.literal("Open Material & Alloy Codex"));

        btnRow.addChild(energyBtn);
        btnRow.addChild(sideIoBtn);
        btnRow.addChild(codexBtn);
        content.addChild(btnRow);
    }
}
