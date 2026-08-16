package io.github.gtbauke.modernmachines.client.gui.terminal;

import java.util.HashMap;
import java.util.Map;

import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.widget.ButtonWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.window.WindowWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

public class SideIoMatrixSubwindow extends WindowWidget {
    public enum IoMode {
        DISABLED("Disabled", ChatFormatting.DARK_GRAY, 0xFF404040),
        INPUT("Input", ChatFormatting.BLUE, 0xFF1976D2),
        OUTPUT("Output", ChatFormatting.GOLD, 0xFFF57C00),
        BOTH("Both", ChatFormatting.LIGHT_PURPLE, 0xFF7B1FA2);

        public final String name;
        public final ChatFormatting format;
        public final int color;

        IoMode(String name, ChatFormatting format, int color) {
            this.name = name;
            this.format = format;
            this.color = color;
        }

        public IoMode next() {
            IoMode[] vals = values();
            return vals[(ordinal() + 1) % vals.length];
        }
    }

    private final Map<Direction, IoMode> faceModes = new HashMap<>();
    private final Map<Direction, ButtonWidget> faceButtons = new HashMap<>();
    private boolean autoExtract = true;

    public SideIoMatrixSubwindow() {
        super(Component.literal("🔄 Side I/O Matrix"), 210, 180);
        setClosable(true);
        setMinimizable(true);

        // Initial default configuration
        faceModes.put(Direction.UP, IoMode.INPUT);
        faceModes.put(Direction.DOWN, IoMode.OUTPUT);
        faceModes.put(Direction.NORTH, IoMode.DISABLED);
        faceModes.put(Direction.SOUTH, IoMode.DISABLED);
        faceModes.put(Direction.WEST, IoMode.INPUT);
        faceModes.put(Direction.EAST, IoMode.OUTPUT);

        FlexContainer content = getContentContainer();
        content.getFlexNode().setGap(3);
        content.getFlexNode().setPadding(FlexInsets.all(4));
        content.getFlexNode().setAlignItems(AlignItems.CENTER);

        // 1. Title subtitle
        LabelWidget sub = new LabelWidget(Component.literal("Click face to cycle I/O configuration").withStyle(ChatFormatting.DARK_GRAY));
        sub.setShadow(false);
        content.addChild(sub);

        // 2. Unrolled Cube 3x3 Matrix Grid
        // Row 1: [   ] [Up] [   ]
        FlexContainer row1 = new FlexContainer(FlexDirection.ROW);
        row1.getFlexNode().setGap(2);
        row1.getFlexNode().setJustifyContent(JustifyContent.CENTER);
        row1.addChild(createSpacer());
        row1.addChild(createFaceButton(Direction.UP, "UP"));
        row1.addChild(createSpacer());
        content.addChild(row1);

        // Row 2: [West] [North] [East] [South]
        FlexContainer row2 = new FlexContainer(FlexDirection.ROW);
        row2.getFlexNode().setGap(2);
        row2.getFlexNode().setJustifyContent(JustifyContent.CENTER);
        row2.addChild(createFaceButton(Direction.WEST, "W"));
        row2.addChild(createFaceButton(Direction.NORTH, "N"));
        row2.addChild(createFaceButton(Direction.EAST, "E"));
        row2.addChild(createFaceButton(Direction.SOUTH, "S"));
        content.addChild(row2);

        // Row 3: [   ] [Down] [   ]
        FlexContainer row3 = new FlexContainer(FlexDirection.ROW);
        row3.getFlexNode().setGap(2);
        row3.getFlexNode().setJustifyContent(JustifyContent.CENTER);
        row3.addChild(createSpacer());
        row3.addChild(createFaceButton(Direction.DOWN, "DN"));
        row3.addChild(createSpacer());
        content.addChild(row3);

        // 3. Controls Row (Auto-Extract Toggle + Reset)
        FlexContainer ctrlRow = new FlexContainer(FlexDirection.ROW);
        ctrlRow.getFlexNode().setSize(198, 20);
        ctrlRow.getFlexNode().setJustifyContent(JustifyContent.SPACE_BETWEEN);
        ctrlRow.getFlexNode().setAlignItems(AlignItems.CENTER);

        ButtonWidget autoExtractBtn = new ButtonWidget(Component.literal("Auto-Eject: ON").withStyle(ChatFormatting.GREEN));
        autoExtractBtn.getFlexNode().setSize(100, 16);
        autoExtractBtn.addClickListener(b -> {
            autoExtract = !autoExtract;
            b.setLabel(Component.literal(autoExtract ? "Auto-Eject: ON" : "Auto-Eject: OFF")
                    .withStyle(autoExtract ? ChatFormatting.GREEN : ChatFormatting.RED));
        });
        ctrlRow.addChild(autoExtractBtn);

        ButtonWidget resetBtn = new ButtonWidget(Component.literal("Reset"), b -> {
            for (Direction dir : Direction.values()) {
                faceModes.put(dir, IoMode.DISABLED);
                updateFaceButtonLabel(dir);
            }
        });
        resetBtn.getFlexNode().setSize(50, 16);
        ctrlRow.addChild(resetBtn);

        content.addChild(ctrlRow);
    }

    private FlexContainer createSpacer() {
        FlexContainer spacer = new FlexContainer();
        spacer.getFlexNode().setSize(32, 18);
        return spacer;
    }

    private ButtonWidget createFaceButton(Direction dir, String label) {
        ButtonWidget btn = new ButtonWidget(Component.literal(label));
        btn.getFlexNode().setSize(32, 18);
        btn.setTooltipSupplier(() -> {
            IoMode mode = faceModes.get(dir);
            return java.util.List.of(
                    Component.literal(dir.getName().toUpperCase() + " Face").withStyle(ChatFormatting.GOLD),
                    Component.literal("Mode: ").withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(mode.name).withStyle(mode.format))
            );
        });

        btn.addClickListener(b -> {
            IoMode next = faceModes.get(dir).next();
            faceModes.put(dir, next);
            updateFaceButtonLabel(dir);
        });

        faceButtons.put(dir, btn);
        updateFaceButtonLabel(dir);
        return btn;
    }

    private void updateFaceButtonLabel(Direction dir) {
        ButtonWidget btn = faceButtons.get(dir);
        if (btn != null) {
            IoMode mode = faceModes.get(dir);
            String prefix = switch (dir) {
                case UP -> "UP";
                case DOWN -> "DN";
                case NORTH -> "N";
                case SOUTH -> "S";
                case WEST -> "W";
                case EAST -> "E";
            };
            btn.setLabel(Component.literal(prefix + ":" + mode.name.charAt(0)).withStyle(mode.format));
        }
    }
}
