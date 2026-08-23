package io.github.gtbauke.modernmachines.api.modular;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;

public record MaterialToolStats(
        Identifier materialId,
        Optional<String> displayName,
        int color,
        Optional<Integer> meltingPoint,
        Optional<Ingredient> ingredient,
        Optional<HeadStats> head,
        Optional<HandleStats> handle,
        Optional<BindingStats> binding,
        Optional<AttachmentStats> attachment,
        List<MaterialTrait> traits
) {
    public static final Codec<MaterialToolStats> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("material").forGetter(MaterialToolStats::materialId),
                    Codec.STRING.optionalFieldOf("display_name").forGetter(MaterialToolStats::displayName),
                    Codec.STRING.xmap(s -> (int) Long.parseLong(s.replace("#", "").replace("0x", ""), 16),
                            i -> String.format("#%06X", (0xFFFFFF & i))).optionalFieldOf("color", 0xFFFFFF).forGetter(MaterialToolStats::color),
                    Codec.INT.optionalFieldOf("melting_point").forGetter(MaterialToolStats::meltingPoint),
                    Ingredient.CODEC.optionalFieldOf("ingredient").forGetter(MaterialToolStats::ingredient),
                    HeadStats.CODEC.optionalFieldOf("head").forGetter(MaterialToolStats::head),
                    HandleStats.CODEC.optionalFieldOf("handle").forGetter(MaterialToolStats::handle),
                    BindingStats.CODEC.optionalFieldOf("binding").forGetter(MaterialToolStats::binding),
                    AttachmentStats.CODEC.optionalFieldOf("attachment").forGetter(MaterialToolStats::attachment),
                    MaterialTrait.CODEC.listOf().optionalFieldOf("traits", Collections.emptyList()).forGetter(MaterialToolStats::traits)
            ).apply(instance, MaterialToolStats::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialToolStats> STREAM_CODEC = StreamCodec.of(
            (buf, stats) -> {
                Identifier.STREAM_CODEC.encode(buf, stats.materialId());
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).encode(buf, stats.displayName());
                ByteBufCodecs.VAR_INT.encode(buf, stats.color());
                ByteBufCodecs.optional(ByteBufCodecs.VAR_INT).encode(buf, stats.meltingPoint());
                ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC).encode(buf, stats.ingredient());
                ByteBufCodecs.optional(HeadStats.STREAM_CODEC).encode(buf, stats.head());
                ByteBufCodecs.optional(HandleStats.STREAM_CODEC).encode(buf, stats.handle());
                ByteBufCodecs.optional(BindingStats.STREAM_CODEC).encode(buf, stats.binding());
                ByteBufCodecs.optional(AttachmentStats.STREAM_CODEC).encode(buf, stats.attachment());
                MaterialTrait.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, stats.traits());
            },
            buf -> new MaterialToolStats(
                    Identifier.STREAM_CODEC.decode(buf),
                    ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.optional(ByteBufCodecs.VAR_INT).decode(buf),
                    ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC).decode(buf),
                    ByteBufCodecs.optional(HeadStats.STREAM_CODEC).decode(buf),
                    ByteBufCodecs.optional(HandleStats.STREAM_CODEC).decode(buf),
                    ByteBufCodecs.optional(BindingStats.STREAM_CODEC).decode(buf),
                    ByteBufCodecs.optional(AttachmentStats.STREAM_CODEC).decode(buf),
                    MaterialTrait.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf)
            )
    );

    public int getEffectiveMeltingPoint(int fallback) {
        return meltingPoint.orElse(fallback);
    }

    public String getEffectiveDisplayName() {
        if (displayName.isPresent() && !displayName.get().isEmpty()) {
            return displayName.get();
        }
        String path = materialId.getPath();
        if (path.isEmpty()) return "Unknown";
        return Character.toUpperCase(path.charAt(0)) + path.substring(1).replace("_", " ");
    }

    public record HeadStats(int durability, float miningSpeed, float attackDamage, String harvestTier) {
        public static final Codec<HeadStats> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("durability").forGetter(HeadStats::durability),
                        Codec.FLOAT.optionalFieldOf("mining_speed", 4.0f).forGetter(HeadStats::miningSpeed),
                        Codec.FLOAT.optionalFieldOf("attack_damage", 2.0f).forGetter(HeadStats::attackDamage),
                        Codec.STRING.optionalFieldOf("harvest_tier", "iron").forGetter(HeadStats::harvestTier)
                ).apply(instance, HeadStats::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HeadStats> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                HeadStats::durability,
                ByteBufCodecs.FLOAT,
                HeadStats::miningSpeed,
                ByteBufCodecs.FLOAT,
                HeadStats::attackDamage,
                ByteBufCodecs.STRING_UTF8,
                HeadStats::harvestTier,
                HeadStats::new
        );
    }

    public record HandleStats(float durabilityMultiplier, float miningSpeedMultiplier, float attackSpeedBonus) {
        public static final Codec<HandleStats> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.FLOAT.optionalFieldOf("durability_multiplier", 1.0f).forGetter(HandleStats::durabilityMultiplier),
                        Codec.FLOAT.optionalFieldOf("mining_speed_multiplier", 1.0f).forGetter(HandleStats::miningSpeedMultiplier),
                        Codec.FLOAT.optionalFieldOf("attack_speed_bonus", 0.0f).forGetter(HandleStats::attackSpeedBonus)
                ).apply(instance, HandleStats::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HandleStats> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT,
                HandleStats::durabilityMultiplier,
                ByteBufCodecs.FLOAT,
                HandleStats::miningSpeedMultiplier,
                ByteBufCodecs.FLOAT,
                HandleStats::attackSpeedBonus,
                HandleStats::new
        );
    }

    public record BindingStats(int bonusDurability) {
        public static final Codec<BindingStats> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.optionalFieldOf("bonus_durability", 50).forGetter(BindingStats::bonusDurability)
                ).apply(instance, BindingStats::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, BindingStats> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                BindingStats::bonusDurability,
                BindingStats::new
        );
    }

    public record AttachmentStats(int bonusDurability, float attackDamageBonus, float speedBonus, Optional<String> tierOverride) {
        public static final Codec<AttachmentStats> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.optionalFieldOf("bonus_durability", 25).forGetter(AttachmentStats::bonusDurability),
                        Codec.FLOAT.optionalFieldOf("attack_damage_bonus", 0.5f).forGetter(AttachmentStats::attackDamageBonus),
                        Codec.FLOAT.optionalFieldOf("speed_bonus", 0.2f).forGetter(AttachmentStats::speedBonus),
                        Codec.STRING.optionalFieldOf("tier_override").forGetter(AttachmentStats::tierOverride)
                ).apply(instance, AttachmentStats::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, AttachmentStats> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                AttachmentStats::bonusDurability,
                ByteBufCodecs.FLOAT,
                AttachmentStats::attackDamageBonus,
                ByteBufCodecs.FLOAT,
                AttachmentStats::speedBonus,
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
                AttachmentStats::tierOverride,
                AttachmentStats::new
        );
    }
}
