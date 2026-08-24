package io.github.gtbauke.modernmachines.api.modular;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record ModularToolData(
        Map<PartSlot, Identifier> parts,
        List<ModifierEntry> modifiers,
        int damage,
        int extraModifierSlots
) {
    public static final ModularToolData EMPTY = new ModularToolData(Collections.emptyMap(), Collections.emptyList(), 0, 0);

    public static final Codec<ModularToolData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(
                            Codec.STRING.xmap(PartSlot::valueOf, PartSlot::name),
                            Identifier.CODEC
                    ).optionalFieldOf("parts", Collections.emptyMap()).forGetter(ModularToolData::parts),
                    ModifierEntry.CODEC.listOf().optionalFieldOf("modifiers", Collections.emptyList()).forGetter(ModularToolData::modifiers),
                    Codec.INT.optionalFieldOf("damage", 0).forGetter(ModularToolData::damage),
                    Codec.INT.optionalFieldOf("extra_modifier_slots", 0).forGetter(ModularToolData::extraModifierSlots)
            ).apply(instance, ModularToolData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ModularToolData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.<RegistryFriendlyByteBuf, PartSlot, Identifier, Map<PartSlot, Identifier>>map(
                    HashMap::new,
                    ByteBufCodecs.fromCodec(Codec.STRING.xmap(PartSlot::valueOf, PartSlot::name)),
                    Identifier.STREAM_CODEC
            ),
            ModularToolData::parts,
            ModifierEntry.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ModularToolData::modifiers,
            ByteBufCodecs.VAR_INT,
            ModularToolData::damage,
            ByteBufCodecs.VAR_INT,
            ModularToolData::extraModifierSlots,
            ModularToolData::new
    );

    public boolean hasPart(PartSlot slot) {
        return parts.containsKey(slot);
    }

    public Identifier getPartMaterial(PartSlot slot) {
        return parts.get(slot);
    }

    public ModularToolData withPart(PartSlot slot, Identifier materialId) {
        var newParts = new EnumMap<PartSlot, Identifier>(PartSlot.class);
        newParts.putAll(this.parts);
        newParts.put(slot, materialId);
        return new ModularToolData(newParts, this.modifiers, this.damage, this.extraModifierSlots);
    }

    public ModularToolData withDamage(int newDamage) {
        return new ModularToolData(this.parts, this.modifiers, newDamage, this.extraModifierSlots);
    }

    public ModularToolData withModifier(Identifier modifierId, int level) {
        var newModifiers = new ArrayList<ModifierEntry>();
        boolean replaced = false;
        for (var entry : this.modifiers) {
            if (entry.id().equals(modifierId)) {
                newModifiers.add(new ModifierEntry(modifierId, level));
                replaced = true;
            } else {
                newModifiers.add(entry);
            }
        }

        if (!replaced) {
            newModifiers.add(new ModifierEntry(modifierId, level));
        }

        return new ModularToolData(this.parts, newModifiers, this.damage, this.extraModifierSlots);
    }

    public int getModifierLevel(Identifier modifierId) {
        for (var entry : modifiers) {
            if (entry.id().equals(modifierId)) {
                return entry.level();
            }
        }

        return 0;
    }

    public int getMaxModifierSlots() {
        return 3 + extraModifierSlots;
    }

    public int getUsedModifierSlots() {
        int total = 0;
        for (var entry : modifiers) {
            total += entry.level();
        }

        return total;
    }
}
