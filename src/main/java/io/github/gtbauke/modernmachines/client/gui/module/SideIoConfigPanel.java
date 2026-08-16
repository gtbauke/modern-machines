package io.github.gtbauke.modernmachines.client.gui.module;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.widget.ButtonWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

public class SideIoConfigPanel extends FlexContainer {
    public enum IoMode {
        NONE("Disabled", 0xFF666666, ChatFormatting.GRAY),
        INPUT("Input", 0xFF38BDF8, ChatFormatting.AQUA),
        OUTPUT("Output", 0xFFFB923C, ChatFormatting.GOLD),
        BOTH("Both", 0xFFA855F7, ChatFormatting.LIGHT_PURPLE);

        private final String label;
        private final int color;
        private final ChatFormatting formatting;

        IoMode(String label, int color, ChatFormatting formatting) {
            this.label = label;
            this.color = color;
            this.formatting = formatting;
        }

        public String getLabel() { return label; }
        public int getColor() { return color; }
        public ChatFormatting getFormatting() { return formatting; }

        public IoMode next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    private final Map<Direction, IoMode> faceModes = new EnumMap<>(Direction.class);
    private boolean autoEject = false;
    private final Consumer<Direction> onFaceToggle;
    private final Consumer<Boolean> onAutoEjectToggle;

    public SideIoConfigPanel(Consumer<Direction> onFaceToggle, Consumer<Boolean> onAutoEjectToggle) {
        super(FlexDirection.COLUMN);
        this.onFaceToggle = onFaceToggle;
        this.onAutoEjectToggle = onAutoEjectToggle;

        for (Direction d : Direction.values()) {
            faceModes.put(d, IoMode.NONE);
        }

        flexNode.setAlignItems(AlignItems.CENTER);
        flexNode.setGap(4);

        // Row 1: Top (UP)
        FlexContainer row1 = new FlexContainer(FlexDirection.ROW);
        row1.getFlexNode().setJustifyContent(JustifyContent.CENTER);
        row1.addChild(createFaceButton(Direction.UP, "Top"));
        this.addChild(row1);

        // Row 2: Left (WEST), Front (NORTH), Right (EAST), Back (SOUTH)
        FlexContainer row2 = new FlexContainer(FlexDirection.ROW);
        row2.getFlexNode().setGap(2);
        row2.getFlexNode().setJustifyContent(JustifyContent.CENTER);
        row2.addChild(createFaceButton(Direction.WEST, "L"));
        row2.addChild(createFaceButton(Direction.NORTH, "F"));
        row2.addChild(createFaceButton(Direction.EAST, "R"));
        row2.addChild(createFaceButton(Direction.SOUTH, "B"));
        this.addChild(row2);

        // Row 3: Bottom (DOWN)
        FlexContainer row3 = new FlexContainer(FlexDirection.ROW);
        row3.getFlexNode().setJustifyContent(JustifyContent.CENTER);
        row3.addChild(createFaceButton(Direction.DOWN, "Bot"));
        this.addChild(row3);

        // Auto-Eject Toggle
        ButtonWidget ejectBtn = new ButtonWidget(Component.literal("Auto-Eject: Off"), b -> {
            this.autoEject = !this.autoEject;
            b.setLabel(Component.literal("Auto-Eject: " + (autoEject ? "On" : "Off")));
            if (onAutoEjectToggle != null) {
                onAutoEjectToggle.accept(autoEject);
            }
        });
        ejectBtn.getFlexNode().setSize(96, 18);
        this.addChild(ejectBtn);
    }

    public void setFaceMode(Direction direction, IoMode mode) {
        faceModes.put(direction, mode);
    }

    public IoMode getFaceMode(Direction direction) {
        return faceModes.getOrDefault(direction, IoMode.NONE);
    }

    private ButtonWidget createFaceButton(Direction direction, String shortName) {
        ButtonWidget btn = new ButtonWidget(Component.literal(shortName));
        btn.getFlexNode().setSize(22, 18);
        btn.setTooltipSupplier(() -> {
            IoMode mode = getFaceMode(direction);
            return List.of(
                    Component.literal(direction.getName().toUpperCase() + " Side").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(mode.getLabel()).withStyle(mode.getFormatting())
            );
        });
        btn.addClickListener(b -> {
            IoMode nextMode = getFaceMode(direction).next();
            setFaceMode(direction, nextMode);
            if (onFaceToggle != null) {
                onFaceToggle.accept(direction);
            }
        });
        return btn;
    }
}
