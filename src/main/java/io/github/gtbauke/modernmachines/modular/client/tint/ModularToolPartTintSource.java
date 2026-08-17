package io.github.gtbauke.modernmachines.modular.client.tint;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gtbauke.modernmachines.api.modular.MaterialStatsManager;
import io.github.gtbauke.modernmachines.api.modular.MaterialToolStats;
import io.github.gtbauke.modernmachines.api.modular.ModularToolData;
import io.github.gtbauke.modernmachines.api.modular.PartSlot;
import io.github.gtbauke.modernmachines.modular.item.ModularToolItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record ModularToolPartTintSource(PartSlot slot) implements ItemTintSource {
    public static final MapCodec<ModularToolPartTintSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    PartSlot.CODEC.fieldOf("slot").forGetter(ModularToolPartTintSource::slot)
            ).apply(instance, ModularToolPartTintSource::new)
    );

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        ModularToolData data = ModularToolItem.getData(stack);
        Identifier matId = data.getPartMaterial(slot);
        if (matId == null) {
            return slot.isRequired() ? 0xFFFFFFFF : 0x00000000;
        }
        return MaterialStatsManager.getStats(matId)
                .map(MaterialToolStats::color)
                .map(c -> 0xFF000000 | c)
                .orElse(0xFFFFFFFF);
    }

    @Override
    public MapCodec<ModularToolPartTintSource> type() {
        return CODEC;
    }
}
