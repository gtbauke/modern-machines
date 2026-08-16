package io.github.gtbauke.modernmachines.api.machine.side;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum SideIoMode implements StringRepresentable {
    NONE("none", "-", 0xFF3E4450, ChatFormatting.GRAY, false, false),
    INPUT("input", "I", 0xFF3B82F6, ChatFormatting.BLUE, true, false),
    OUTPUT("output", "O", 0xFFF97316, ChatFormatting.GOLD, false, true),
    BOTH("both", "I/O", 0xFFA855F7, ChatFormatting.DARK_PURPLE, true, true);

    private final String name;
    private final String label;
    private final int colorRgb;
    private final ChatFormatting formatting;
    private final boolean allowsInput;
    private final boolean allowsOutput;

    SideIoMode(String name, String label, int colorRgb, ChatFormatting formatting, boolean allowsInput, boolean allowsOutput) {
        this.name = name;
        this.label = label;
        this.colorRgb = colorRgb;
        this.formatting = formatting;
        this.allowsInput = allowsInput;
        this.allowsOutput = allowsOutput;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public String getLabel() {
        return this.label;
    }

    public int getColorRgb() {
        return this.colorRgb;
    }

    public ChatFormatting getFormatting() {
        return this.formatting;
    }

    public boolean allowsInput() {
        return this.allowsInput;
    }

    public boolean allowsOutput() {
        return this.allowsOutput;
    }

    public Component getDisplayName() {
        return Component.literal(this.name.toUpperCase()).withStyle(this.formatting, ChatFormatting.BOLD);
    }

    public SideIoMode next() {
        SideIoMode[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }

    public SideIoMode previous() {
        SideIoMode[] values = values();
        return values[(this.ordinal() - 1 + values.length) % values.length];
    }

    public static SideIoMode fromIndex(int index) {
        SideIoMode[] values = values();
        if (index < 0 || index >= values.length) return NONE;
        return values[index];
    }
}
