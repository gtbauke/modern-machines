package io.github.gtbauke.modernmachines.api.modular;

import java.util.Locale;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum PartSlot implements StringRepresentable {
    HEAD("head", true),
    HANDLE("handle", true),
    BINDING("binding", true),
    TIP("tip", false),
    GRIP("grip", false);

    public static final Codec<PartSlot> CODEC = StringRepresentable.fromEnum(PartSlot::values);

    private final String name;
    private final boolean required;

    PartSlot(String name, boolean required) {
        this.name = name;
        this.required = required;
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    public @NonNull String getSerializedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}
