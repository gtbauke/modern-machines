package io.github.gtbauke.modernmachines.api.modular;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record ModifierEntry(Identifier id, int level) {
    public static final Codec<ModifierEntry> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(ModifierEntry::id),
                    Codec.INT.optionalFieldOf("level", 1).forGetter(ModifierEntry::level)
            ).apply(instance, ModifierEntry::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ModifierEntry> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            ModifierEntry::id,
            ByteBufCodecs.VAR_INT,
            ModifierEntry::level,
            ModifierEntry::new
    );
}
