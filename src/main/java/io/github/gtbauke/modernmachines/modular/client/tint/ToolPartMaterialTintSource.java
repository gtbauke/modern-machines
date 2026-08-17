package io.github.gtbauke.modernmachines.modular.client.tint;

import com.mojang.serialization.MapCodec;
import io.github.gtbauke.modernmachines.api.modular.MaterialStatsManager;
import io.github.gtbauke.modernmachines.api.modular.MaterialToolStats;
import io.github.gtbauke.modernmachines.modular.item.ToolPartItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record ToolPartMaterialTintSource() implements ItemTintSource {
    public static final ToolPartMaterialTintSource INSTANCE = new ToolPartMaterialTintSource();
    public static final MapCodec<ToolPartMaterialTintSource> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        Identifier matId = ToolPartItem.getMaterialId(stack);
        if (matId == null) {
            return 0xFFFFFFFF;
        }
        return MaterialStatsManager.getStats(matId)
                .map(MaterialToolStats::color)
                .map(c -> 0xFF000000 | c)
                .orElse(0xFFFFFFFF);
    }

    @Override
    public MapCodec<ToolPartMaterialTintSource> type() {
        return CODEC;
    }
}
