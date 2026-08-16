package io.github.gtbauke.modernmachines.api.modular;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record MaterialTrait(Identifier id, int level, String description) {
    public static final Codec<MaterialTrait> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(MaterialTrait::id),
                    Codec.INT.optionalFieldOf("level", 1).forGetter(MaterialTrait::level),
                    Codec.STRING.optionalFieldOf("description", "").forGetter(MaterialTrait::description)
            ).apply(instance, MaterialTrait::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialTrait> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            MaterialTrait::id,
            ByteBufCodecs.VAR_INT,
            MaterialTrait::level,
            ByteBufCodecs.STRING_UTF8,
            MaterialTrait::description,
            MaterialTrait::new
    );

    public Component getDisplayName() {
        String path = id.getPath();
        String name = Character.toUpperCase(path.charAt(0)) + path.substring(1).replace("_", " ");
        if (level > 1) {
            return Component.literal(name + " " + level);
        }
        return Component.literal(name);
    }
}
