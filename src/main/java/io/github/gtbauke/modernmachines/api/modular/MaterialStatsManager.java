package io.github.gtbauke.modernmachines.api.modular;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class MaterialStatsManager extends SimpleJsonResourceReloadListener<MaterialToolStats> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Identifier, MaterialToolStats> STATS = new HashMap<>();
    public static final MaterialStatsManager INSTANCE = new MaterialStatsManager();

    public MaterialStatsManager() {
        super(MaterialToolStats.CODEC, FileToIdConverter.json("modernmachines/materials"));
        initDefaults();
    }

    public static void initDefaults() {
        STATS.clear();
        for (Material mat : ModMaterials.getAllMaterials()) {
            Identifier id = mat.getId();
            int durability = Math.max(100, (int) (mat.hardness() * 120));
            float speed = Math.max(2.0f, mat.hardness() * 1.5f);
            float damage = Math.max(1.0f, mat.hardness() * 0.8f);
            String tier = mat.hardness() >= 6.0f ? "diamond" : mat.hardness() >= 4.0f ? "iron" : "stone";

            Item mainItem = mat.getItem(ResourceForm.INGOT);
            if (mainItem == null) mainItem = mat.getItem(ResourceForm.GEM);
            if (mainItem == null) mainItem = mat.getItem(ResourceForm.RAW_ORE);

            Optional<Ingredient> ingredient = mainItem != null
                    ? Optional.of(Ingredient.of(mainItem))
                    : Optional.empty();

            List<MaterialTrait> traits = new java.util.ArrayList<>();
            MaterialToolStats.AttachmentStats attachmentStats;

            if (mat == ModMaterials.LAPIS_LAZULI) {
                attachmentStats = new MaterialToolStats.AttachmentStats(60, 0.5f, 0.0f, Optional.empty());
                traits.add(new MaterialTrait(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "prosperity"), 1, "Increased experience and fortune"));
            } else if (mat == ModMaterials.DIAMOND) {
                attachmentStats = new MaterialToolStats.AttachmentStats(150, 1.0f, 0.0f, Optional.empty());
                traits.add(new MaterialTrait(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "reinforced"), 1, "Chance to not consume durability"));
            } else if (mat == ModMaterials.EMERALD) {
                attachmentStats = new MaterialToolStats.AttachmentStats(100, 0.75f, 0.0f, Optional.empty());
                traits.add(new MaterialTrait(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "bounty"), 1, "Increased mob loot and luck"));
            } else if (mat == ModMaterials.AMETHYST) {
                attachmentStats = new MaterialToolStats.AttachmentStats(80, 0.6f, 0.4f, Optional.empty());
                traits.add(new MaterialTrait(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "resonance"), 1, "Bonus attack and swing speed"));
            } else {
                attachmentStats = new MaterialToolStats.AttachmentStats((int) (durability * 0.1f), damage * 0.3f, 0.2f, Optional.empty());
            }

            MaterialToolStats stats = new MaterialToolStats(
                    id,
                    Optional.of(mat.displayName()),
                    mat.colorHex(),
                    ingredient,
                    Optional.of(new MaterialToolStats.HeadStats(durability, speed, damage, tier)),
                    Optional.of(new MaterialToolStats.HandleStats(1.0f + (mat.hardness() * 0.05f), 1.0f, 0.0f)),
                    Optional.of(new MaterialToolStats.BindingStats((int) (durability * 0.2f))),
                    Optional.of(attachmentStats),
                    traits
            );

            STATS.put(id, stats);
        }
    }

    @Override
    protected void apply(Map<Identifier, MaterialToolStats> map, @NonNull ResourceManager resourceManager, ProfilerFiller profiler) {
        initDefaults();
        STATS.putAll(map);
        LOGGER.info("Loaded {} material definitions (built-in + datapacks)", STATS.size());
    }

    public static void setClientStats(Map<Identifier, MaterialToolStats> map) {
        STATS.clear();
        STATS.putAll(map);
    }

    public static Optional<MaterialToolStats> getStats(Identifier materialId) {
        return Optional.ofNullable(STATS.get(materialId));
    }

    public static Optional<MaterialToolStats> getMaterialForIngredient(ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();

        for (MaterialToolStats stats : STATS.values()) {
            if (stats.ingredient().isPresent() && stats.ingredient().get().test(stack)) {
                return Optional.of(stats);
            }
        }

        return Optional.empty();
    }

    public static Map<Identifier, MaterialToolStats> getAllStats() {
        return Collections.unmodifiableMap(STATS);
    }
}
