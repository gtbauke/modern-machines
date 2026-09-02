package io.github.gtbauke.modernmachines.data;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.modular.ToolPartType;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.config.material.CustomMaterialLoader;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

public class VirtualResourcePack {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static @Nullable Pack createResourcePack() {
        var packResources = new VirtualPackResources(
                "modernmachines_virtual_resources",
                "Modern Machines Virtual Resource Pack",
                PackType.CLIENT_RESOURCES,
                PackSource.BUILT_IN
        );

        populateClientResources(packResources);

        var selectionConfig = new PackSelectionConfig(true, Pack.Position.TOP, false);
        return Pack.readMetaAndCreate(
                packResources.location(),
                new Pack.ResourcesSupplier() {
                    @Override
                    public @NonNull PackResources openPrimary(@NonNull PackLocationInfo loc) {
                        return packResources;
                    }

                    @Override
                    public @NonNull PackResources openFull(@NonNull PackLocationInfo loc, Pack.@NonNull Metadata metadata) {
                        return packResources;
                    }
                },
                PackType.CLIENT_RESOURCES,
                selectionConfig
        );
    }

    private static void populateClientResources(VirtualPackResources pack) {
        var langEntries = new HashMap<String, String>();

        for (var name : CustomMaterialLoader.getCustomMaterialNames()) {
            var material = ModMaterials.getByName(name);
            if (material == null) {
                continue;
            }

            addMaterialClientResources(pack, material, langEntries);
        }

        if (!langEntries.isEmpty()) {
            var langId = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "lang/en_us.json");
            pack.addResource(langId, GSON.toJson(langEntries));
        }
    }

    private static void addMaterialClientResources(VirtualPackResources pack, Material material, Map<String, String> langEntries) {
        var name = material.name();
        var displayName = material.displayName();
        int tintColor = 0xFF000000 | material.colorHex();
        var overlaySuffix = String.format("%03d", material.overlayIndex());

        langEntries.put("material.modernmachines." + name, displayName);

        for (var form : material.supportedForms()) {
            if (form.isBlock() && material.isRegisteredLocally(form)) {
                var blockName = form.getRegistryName(name);
                var templateModelId = getTemplateBlockModel(form, overlaySuffix);

                var blockstate = Map.of(
                        "variants", Map.of(
                                "", Map.of("model", templateModelId)
                        )
                );
                pack.addResource(
                        Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "blockstates/" + blockName + ".json"),
                        GSON.toJson(blockstate)
                );

                var itemModel = Map.of(
                        "model", Map.of(
                                "type", "minecraft:model",
                                "model", templateModelId,
                                "tints", List.of(
                                        Map.of("type", "minecraft:constant", "value", tintColor)
                                )
                        )
                );
                pack.addResource(
                        Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "items/" + blockName + ".json"),
                        GSON.toJson(itemModel)
                );

                addFormTranslation(form, name, displayName, langEntries);
            } else if (form.isItem() && material.isRegisteredLocally(form)) {
                var itemName = form.getRegistryName(name);
                var formName = form.name().toLowerCase(Locale.ROOT);
                var templateItemModel = ModernMachines.MOD_ID + ":item/template/" + formName;

                var itemModel = Map.of(
                        "model", Map.of(
                                "type", "minecraft:model",
                                "model", templateItemModel,
                                "tints", List.of(
                                        Map.of("type", "minecraft:constant", "value", tintColor)
                                )
                        )
                );
                pack.addResource(
                        Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "items/" + itemName + ".json"),
                        GSON.toJson(itemModel)
                );

                addFormTranslation(form, name, displayName, langEntries);
            } else if (form.isFluid() && material.isRegisteredLocally(form)) {
                var bucketName = form.getRegistryName(name) + "_bucket";
                var itemModel = Map.of(
                        "model", Map.of(
                                "type", "minecraft:model",
                                "model", ModernMachines.MOD_ID + ":item/template/molten_bucket",
                                "tints", List.of(
                                        Map.of("type", "minecraft:constant", "value", tintColor)
                                )
                        )
                );
                pack.addResource(
                        Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "items/" + bucketName + ".json"),
                        GSON.toJson(itemModel)
                );

                langEntries.put("item.modernmachines." + bucketName, "Molten " + displayName + " Bucket");
                langEntries.put("block.modernmachines.molten_" + name, "Molten " + displayName);
                langEntries.put("fluid_type.modernmachines.molten_" + name, "Molten " + displayName);
            }
        }

        for (var partType : ToolPartType.values()) {
            var partName = partType.getSerializedName() + "_" + name;
            var templatePartModel = ModernMachines.MOD_ID + ":item/template/part/" + partType.getSerializedName();

            var partItemModel = Map.of(
                    "model", Map.of(
                            "type", "minecraft:model",
                            "model", templatePartModel,
                            "tints", List.of(
                                    Map.of("type", "minecraft:constant", "value", tintColor)
                            )
                    )
            );
            pack.addResource(
                    Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "items/" + partName + ".json"),
                    GSON.toJson(partItemModel)
            );

            var readablePartName = formatPartName(partType.getSerializedName());
            langEntries.put("item.modernmachines." + partName, displayName + " " + readablePartName);
        }
    }

    private static String getTemplateBlockModel(ResourceForm form, String overlaySuffix) {
        return switch (form) {
            case ORE -> ModernMachines.MOD_ID + ":block/template/stone_ore_" + overlaySuffix;
            case DEEPSLATE_ORE -> ModernMachines.MOD_ID + ":block/template/deepslate_ore_" + overlaySuffix;
            case NETHERRACK_ORE -> ModernMachines.MOD_ID + ":block/template/netherrack_ore_" + overlaySuffix;
            case END_STONE_ORE -> ModernMachines.MOD_ID + ":block/template/end_stone_ore_" + overlaySuffix;
            case RAW_STORAGE_BLOCK -> ModernMachines.MOD_ID + ":block/template/raw_storage_block";
            default -> ModernMachines.MOD_ID + ":block/template/storage_block";
        };
    }

    private static void addFormTranslation(ResourceForm form, String name, String displayName, Map<String, String> lang) {
        var key = form.isBlock() ? "block.modernmachines." + form.getRegistryName(name) : "item.modernmachines." + form.getRegistryName(name);

        String text = switch (form) {
            case ORE -> displayName + " Ore";
            case DEEPSLATE_ORE -> "Deepslate " + displayName + " Ore";
            case NETHERRACK_ORE -> "Netherrack " + displayName + " Ore";
            case END_STONE_ORE -> "End Stone " + displayName + " Ore";
            case STORAGE_BLOCK -> "Block of " + displayName;
            case RAW_STORAGE_BLOCK -> "Block of Raw " + displayName;
            case RAW_ORE -> "Raw " + displayName;
            case INGOT -> displayName + " Ingot";
            case NUGGET -> displayName + " Nugget";
            case DUST -> displayName + " Dust";
            case PLATE -> displayName + " Plate";
            case ROD -> displayName + " Rod";
            case GEAR -> displayName + " Gear";
            case SCREW -> displayName + " Screw";
            case WIRE -> displayName + " Wire";
            default -> displayName + " " + capitalize(form.name().toLowerCase(Locale.ROOT));
        };

        lang.put(key, text);
    }

    private static String formatPartName(String serializedName) {
        var parts = serializedName.split("_");
        var sb = new StringBuilder();
        for (var part : parts) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }

            sb.append(capitalize(part));
        }

        return sb.toString();
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
