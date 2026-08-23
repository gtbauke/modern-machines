package io.github.gtbauke.modernmachines.api.machine.capability;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum MachineCapabilityType implements StringRepresentable {
    ITEM("item", "Items", 0xFF60A5FA, ChatFormatting.BLUE, 0, 96),
    ENERGY("energy", "Energy", 0xFFFACC15, ChatFormatting.YELLOW, 0, 96),
    FLUID("fluid", "Fluids", 0xFF38BDF8, ChatFormatting.AQUA, 16, 96);

    private final String name;
    private final String displayName;
    private final int themeColorRgb;
    private final ChatFormatting formatting;
    private final int iconU;
    private final int iconV;

    MachineCapabilityType(String name, String displayName, int themeColorRgb, ChatFormatting formatting, int iconU, int iconV) {
        this.name = name;
        this.displayName = displayName;
        this.themeColorRgb = themeColorRgb;
        this.formatting = formatting;
        this.iconU = iconU;
        this.iconV = iconV;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getThemeColorRgb() {
        return this.themeColorRgb;
    }

    public ChatFormatting getFormatting() {
        return this.formatting;
    }

    public int getIconU() {
        return this.iconU;
    }

    public int getIconV() {
        return this.iconV;
    }

    public Component getFormattedName() {
        return Component.literal(this.displayName).withStyle(this.formatting, ChatFormatting.BOLD);
    }
}
