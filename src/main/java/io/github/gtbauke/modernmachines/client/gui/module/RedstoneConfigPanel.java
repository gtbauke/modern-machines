package io.github.gtbauke.modernmachines.client.gui.module;

import java.util.function.Consumer;

import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.widget.ButtonWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class RedstoneConfigPanel extends FlexContainer {
    public enum RedstoneMode {
        IGNORED("Ignored", "Runs continuously regardless of redstone signal"),
        HIGH("High Active", "Runs only when receiving a redstone signal"),
        LOW("Low Active", "Runs only when NO redstone signal is present"),
        PULSE("Pulse", "Executes 1 process per redstone pulse");

        private final String label;
        private final String description;

        RedstoneMode(String label, String description) {
            this.label = label;
            this.description = description;
        }

        public String getLabel() { return label; }
        public String getDescription() { return description; }

        public RedstoneMode next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    private RedstoneMode currentMode = RedstoneMode.IGNORED;
    private final Consumer<RedstoneMode> onModeChanged;

    public RedstoneConfigPanel(Consumer<RedstoneMode> onModeChanged) {
        super(FlexDirection.COLUMN);
        this.onModeChanged = onModeChanged;

        flexNode.setAlignItems(AlignItems.CENTER);
        flexNode.setGap(6);

        LabelWidget infoLabel = new LabelWidget(Component.literal("Control Mode").withStyle(ChatFormatting.GRAY));
        infoLabel.setCentered(true);
        infoLabel.setShadow(false);
        this.addChild(infoLabel);

        ButtonWidget modeButton = new ButtonWidget(Component.literal(currentMode.getLabel()), null);
        modeButton.getFlexNode().setSize(96, 20);
        modeButton.setTooltip(Component.literal(currentMode.getDescription()).withStyle(ChatFormatting.YELLOW));
        modeButton.addClickListener(b -> {
            currentMode = currentMode.next();
            b.setLabel(Component.literal(currentMode.getLabel()));
            b.setTooltip(Component.literal(currentMode.getDescription()).withStyle(ChatFormatting.YELLOW));
            if (onModeChanged != null) {
                onModeChanged.accept(currentMode);
            }
        });
        this.addChild(modeButton);
    }

    public RedstoneMode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(RedstoneMode mode) {
        this.currentMode = mode;
    }
}
