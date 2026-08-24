package io.github.gtbauke.modernmachines.api.modular;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum ToolPartType implements StringRepresentable {
    PICKAXE_HEAD("pickaxe_head", PartSlot.HEAD, 2),
    AXE_HEAD("axe_head", PartSlot.HEAD, 3),
    SHOVEL_HEAD("shovel_head", PartSlot.HEAD, 1),
    SWORD_BLADE("sword_blade", PartSlot.HEAD, 2),
    HOE_HEAD("hoe_head", PartSlot.HEAD, 2),
    HANDLE("handle", PartSlot.HANDLE, 1),
    BINDING("binding", PartSlot.BINDING, 1),
    SWORD_GUARD("sword_guard", PartSlot.BINDING, 1),
    TIP("tip", PartSlot.TIP, 1),
    GRIP("grip", PartSlot.GRIP, 1),
    POMMEL("pommel", PartSlot.POMMEL, 1);

    private final String name;
    private final PartSlot slot;
    private final int materialCost;

    ToolPartType(String name, PartSlot slot, int materialCost) {
        this.name = name;
        this.slot = slot;
        this.materialCost = materialCost;
    }

    public PartSlot getSlot() {
        return slot;
    }

    public int getMaterialCost() {
        return materialCost;
    }

    public String getDisplayName() {
        var words = name.split("_");
        var sb = new StringBuilder();
        for (var word : words) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }

            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }

        return sb.toString();
    }

    @Override
    public @NonNull String getSerializedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}
