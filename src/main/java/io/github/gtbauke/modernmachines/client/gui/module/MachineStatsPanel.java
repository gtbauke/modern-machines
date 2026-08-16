package io.github.gtbauke.modernmachines.client.gui.module;

import java.util.function.IntSupplier;

import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class MachineStatsPanel extends FlexContainer {
    private final IntSupplier energySupplier;
    private final IntSupplier maxEnergySupplier;
    private final IntSupplier usagePerTickSupplier;

    private final LabelWidget energyLabel;
    private final LabelWidget usageLabel;
    private final LabelWidget statusLabel;

    public MachineStatsPanel(IntSupplier energySupplier, IntSupplier maxEnergySupplier, IntSupplier usagePerTickSupplier) {
        super(FlexDirection.COLUMN);
        this.energySupplier = energySupplier;
        this.maxEnergySupplier = maxEnergySupplier;
        this.usagePerTickSupplier = usagePerTickSupplier;

        flexNode.setAlignItems(AlignItems.CENTER);
        flexNode.setGap(4);

        statusLabel = new LabelWidget(Component.literal("Status: Idle").withStyle(ChatFormatting.GREEN));
        statusLabel.setCentered(true);
        this.addChild(statusLabel);

        usageLabel = new LabelWidget(Component.literal("Usage: 0 FE/t").withStyle(ChatFormatting.YELLOW));
        usageLabel.setCentered(true);
        this.addChild(usageLabel);

        energyLabel = new LabelWidget(Component.literal("Stored: 0 FE").withStyle(ChatFormatting.AQUA));
        energyLabel.setCentered(true);
        this.addChild(energyLabel);
    }

    public void updateLiveStats(boolean isActive) {
        statusLabel.setText(Component.literal(isActive ? "Status: Running" : "Status: Idle")
                .withStyle(isActive ? ChatFormatting.GOLD : ChatFormatting.GREEN));
        int usage = isActive ? usagePerTickSupplier.getAsInt() : 0;
        usageLabel.setText(Component.literal("Usage: " + usage + " FE/t").withStyle(ChatFormatting.YELLOW));
        energyLabel.setText(Component.literal("Stored: " + energySupplier.getAsInt() + " FE").withStyle(ChatFormatting.AQUA));
    }
}
