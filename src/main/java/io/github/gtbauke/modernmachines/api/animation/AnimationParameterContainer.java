package io.github.gtbauke.modernmachines.api.animation;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

public class AnimationParameterContainer {
    private final Map<String, Float> floatParams = new HashMap<>();
    private final Map<String, Boolean> boolParams = new HashMap<>();
    private final Map<String, Integer> intParams = new HashMap<>();

    private boolean dirty = false;

    public AnimationParameterContainer() {
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        this.dirty = false;
    }

    public void setFloat(@NonNull String key, float value) {
        var current = floatParams.get(key);
        if (current == null || current != value) {
            floatParams.put(key, value);
            this.dirty = true;
        }
    }

    public float getFloat(@NonNull String key, float defaultValue) {
        return floatParams.getOrDefault(key, defaultValue);
    }

    public void setBoolean(@NonNull String key, boolean value) {
        var current = boolParams.get(key);
        if (current == null || current != value) {
            boolParams.put(key, value);
            this.dirty = true;
        }
    }

    public boolean getBoolean(@NonNull String key, boolean defaultValue) {
        return boolParams.getOrDefault(key, defaultValue);
    }

    public void setInt(@NonNull String key, int value) {
        var current = intParams.get(key);
        if (current == null || current != value) {
            intParams.put(key, value);
            this.dirty = true;
        }
    }

    public int getInt(@NonNull String key, int defaultValue) {
        return intParams.getOrDefault(key, defaultValue);
    }

    public boolean isEmpty() {
        return floatParams.isEmpty() && boolParams.isEmpty() && intParams.isEmpty();
    }

    public void save(ValueOutput output) {
        if (isEmpty()) {
            return;
        }

        var animOutput = output.child("AnimationData");

        for (var entry : floatParams.entrySet()) {
            animOutput.putFloat(entry.getKey(), entry.getValue());
        }

        for (var entry : boolParams.entrySet()) {
            animOutput.putBoolean(entry.getKey(), entry.getValue());
        }

        for (var entry : intParams.entrySet()) {
            animOutput.putInt(entry.getKey(), entry.getValue());
        }
    }

    public void load(ValueInput input) {
        input.child("AnimationData").ifPresent(animInput -> {
            // Reserved for component-specific loading if necessary
        });
    }

    public CompoundTag toCompoundTag() {
        var root = new CompoundTag();

        var floatsTag = new CompoundTag();
        for (var entry : floatParams.entrySet()) {
            floatsTag.putFloat(entry.getKey(), entry.getValue());
        }
        root.put("floats", floatsTag);

        var boolsTag = new CompoundTag();
        for (var entry : boolParams.entrySet()) {
            boolsTag.putBoolean(entry.getKey(), entry.getValue());
        }
        root.put("bools", boolsTag);

        var intsTag = new CompoundTag();
        for (var entry : intParams.entrySet()) {
            intsTag.putInt(entry.getKey(), entry.getValue());
        }
        root.put("ints", intsTag);

        return root;
    }

    public void fromCompoundTag(CompoundTag tag) {
        tag.getCompound("floats").ifPresent(floatsTag -> {
            for (var key : floatsTag.keySet()) {
                setFloat(key, floatsTag.getFloatOr(key, 0.0f));
            }
        });

        tag.getCompound("bools").ifPresent(boolsTag -> {
            for (var key : boolsTag.keySet()) {
                setBoolean(key, boolsTag.getBooleanOr(key, false));
            }
        });

        tag.getCompound("ints").ifPresent(intsTag -> {
            for (var key : intsTag.keySet()) {
                setInt(key, intsTag.getIntOr(key, 0));
            }
        });
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(floatParams.size());
        for (var entry : floatParams.entrySet()) {
            buf.writeUtf(entry.getKey());
            buf.writeFloat(entry.getValue());
        }

        buf.writeVarInt(boolParams.size());
        for (var entry : boolParams.entrySet()) {
            buf.writeUtf(entry.getKey());
            buf.writeBoolean(entry.getValue());
        }

        buf.writeVarInt(intParams.size());
        for (var entry : intParams.entrySet()) {
            buf.writeUtf(entry.getKey());
            buf.writeVarInt(entry.getValue());
        }
    }

    public void decode(FriendlyByteBuf buf) {
        var floatCount = buf.readVarInt();
        for (var i = 0; i < floatCount; i++) {
            var key = buf.readUtf();
            var val = buf.readFloat();
            floatParams.put(key, val);
        }

        var boolCount = buf.readVarInt();
        for (var i = 0; i < boolCount; i++) {
            var key = buf.readUtf();
            var val = buf.readBoolean();
            boolParams.put(key, val);
        }

        var intCount = buf.readVarInt();
        for (var i = 0; i < intCount; i++) {
            var key = buf.readUtf();
            var val = buf.readVarInt();
            intParams.put(key, val);
        }
    }
}
