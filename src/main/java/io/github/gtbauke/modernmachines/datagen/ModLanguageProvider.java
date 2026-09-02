package io.github.gtbauke.modernmachines.datagen;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.modular.PartSlot;
import io.github.gtbauke.modernmachines.api.modular.ToolPartType;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(PackOutput output) {
        super(output, ModernMachines.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // Creative tabs
        add("itemGroup.modernmachines.blocks", "Modern Machines: Blocks");
        add("itemGroup.modernmachines.materials", "Modern Machines: Materials");
        add("itemGroup.modernmachines.resources", "Modern Machines Resources");
        add("itemGroup.modernmachines.fluids", "Modern Machines: Fluids");

        addBlock(ModBlocks.ADOBE_BRICK, "Adobe Brick");
        addBlock(ModBlocks.COPPER_PIPE, "Copper Pipe");
        addBlock(ModBlocks.STEEL_PIPE, "Steel Pipe");
        addItem(ModItems.ADOBE_BRICKS, "Adobe Bricks");
        addItem(ModItems.ADOBE_MIXTURE, "Adobe Mixture");

        // Workstations
        addBlock(ModBlocks.PART_BUILDER, "Part Builder");
        addBlock(ModBlocks.TINKERING_TABLE, "Tinkering Table");
        addBlock(ModBlocks.BASIC_ALLOY_SMELTER_CONTROLLER, "Basic Alloy Smelter Controller");
        addBlock(ModBlocks.BASIC_ALLOY_SMELTER_HEATER, "Basic Alloy Smelter Heater");
        add("container.modernmachines.part_builder", "Part Builder");
        add("container.modernmachines.tinkering_table", "Tinkering Table");
        add("container.modernmachines.basic_alloy_smelter", "Basic Alloy Smelter");
        add("container.modernmachines.alloy_smelter", "Basic Alloy Smelter");
        add("container.modernmachines.engineers_terminal", "Engineer's Terminal");

        // Upgrades
        addItem(ModItems.SPEED_UPGRADE, "Speed Upgrade");
        addItem(ModItems.ENERGY_EFFICIENCY_UPGRADE, "Energy Efficiency Upgrade");
        add("gui.modernmachines.upgrades", "Upgrades");
        add("stat.modernmachines.speed", "Processing Speed");
        add("stat.modernmachines.energy_efficiency", "Energy Efficiency");
        add("stat.modernmachines.energy_capacity", "Energy Capacity");
        add("tooltip.modernmachines.upgrade.speed_boost", "Speed: %s");
        add("tooltip.modernmachines.upgrade.energy_cost_penalty", "Energy Cost: %s");
        add("tooltip.modernmachines.upgrade.energy_efficiency_boost", "Energy Efficiency: %s");
        add("tooltip.modernmachines.upgrade.max_stack", "Max Upgrades per slot: %d");

        // Crafting Tools
        addItem(ModItems.ENGINEER_HAMMER, "Engineer's Hammer");
        addItem(ModItems.WIRE_CUTTER, "Wire Cutters");

        // Patterns
        addItem(ModItems.BLANK_PATTERN, "Blank Pattern");
        addItem(ModItems.PICKAXE_HEAD_PATTERN, "Pickaxe Head Pattern");
        addItem(ModItems.AXE_HEAD_PATTERN, "Axe Head Pattern");
        addItem(ModItems.SHOVEL_HEAD_PATTERN, "Shovel Head Pattern");
        addItem(ModItems.SWORD_BLADE_PATTERN, "Sword Blade Pattern");
        addItem(ModItems.HOE_HEAD_PATTERN, "Hoe Head Pattern");
        addItem(ModItems.HANDLE_PATTERN, "Handle Pattern");
        addItem(ModItems.BINDING_PATTERN, "Binding Pattern");
        addItem(ModItems.TIP_PATTERN, "Tip Pattern");
        addItem(ModItems.GRIP_PATTERN, "Grip Pattern");
        addItem(ModItems.SWORD_GUARD_PATTERN, "Sword Guard Pattern");
        addItem(ModItems.POMMEL_PATTERN, "Pommel Pattern");

        // Modular Tools
        addItem(ModItems.MODULAR_PICKAXE, "Modular Pickaxe");
        add("item.modernmachines.modular_pickaxe.named", "%s Pickaxe");
        addItem(ModItems.MODULAR_AXE, "Modular Axe");
        add("item.modernmachines.modular_axe.named", "%s Axe");
        addItem(ModItems.MODULAR_SHOVEL, "Modular Shovel");
        add("item.modernmachines.modular_shovel.named", "%s Shovel");
        addItem(ModItems.MODULAR_SWORD, "Modular Sword");
        add("item.modernmachines.modular_sword.named", "%s Sword");
        addItem(ModItems.MODULAR_HOE, "Modular Hoe");
        add("item.modernmachines.modular_hoe.named", "%s Hoe");

        // Tool Parts
        for (Material material : ModMaterials.getAllMaterials()) {
            for (ToolPartType partType : ToolPartType.values()) {
                if (partType == ToolPartType.POMMEL) {
                    if (material != ModMaterials.LAPIS_LAZULI &&
                        material != ModMaterials.DIAMOND &&
                        material != ModMaterials.EMERALD &&
                        material != ModMaterials.AMETHYST) {
                        continue;
                    }
                }

                String regName = partType.getSerializedName() + "_" + material.name();
                String partName = formatPartName(partType, material);

                add("item.modernmachines." + regName, partName);
            }
        }

        // Part Slots & Types
        for (PartSlot slot : PartSlot.values()) {
            add("part_slot.modernmachines." + slot.getSerializedName(), capitalize(slot.getSerializedName()));
        }
        for (ToolPartType type : ToolPartType.values()) {
            add("part_type.modernmachines." + type.getSerializedName(), capitalize(type.getSerializedName().replace("_", " ")));
        }

        // Tooltips & Stats
        add("tooltip.modernmachines.unassembled_tool", "Unassembled Modular Tool");
        add("tooltip.modernmachines.part_material", "Material: %s");
        add("tooltip.modernmachines.stat.durability", "Durability: +%d");
        add("tooltip.modernmachines.stat.durability_ratio", "Durability: %d / %d");
        add("tooltip.modernmachines.stat.mining_speed", "Mining Speed: %s");
        add("tooltip.modernmachines.stat.attack_damage", "Attack Damage: %s");
        add("tooltip.modernmachines.stat.harvest_tier", "Harvest Tier: %s");
        add("tooltip.modernmachines.stat.durability_mult", "Durability Multiplier: %s");
        add("tooltip.modernmachines.stat.speed_mult", "Mining Speed Multiplier: %s");
        add("tooltip.modernmachines.stat.bonus_durability", "Durability Bonus: +%d");
        add("tooltip.modernmachines.stat.attack_bonus", "Attack Damage: %s");
        add("tooltip.modernmachines.pattern_for", "Carves: %s");
        add("tooltip.modernmachines.material_cost", "Material Cost: %d");
        add("tooltip.modernmachines.blank_pattern_desc", "Can be carved into any tool pattern");
        add("tooltip.modernmachines.parts_header", "Tool Parts:");
        add("tooltip.modernmachines.stats_header", "Tool Stats:");
        add("tooltip.modernmachines.traits_header", "Material Traits:");
        add("tooltip.modernmachines.modifiers_header", "Modifiers (%d / %d):");

        // Traits
        add("trait.modernmachines.ecological", "Ecological");
        add("trait.modernmachines.ecological.desc", "Slowly repairs durability over time");
        add("trait.modernmachines.dense", "Dense");
        add("trait.modernmachines.dense.desc", "Knocks back foes when attacking");
        add("trait.modernmachines.reinforced", "Reinforced");
        add("trait.modernmachines.reinforced.desc", "Chance to negate durability damage");
        add("trait.modernmachines.prosperity", "Prosperity");
        add("trait.modernmachines.prosperity.desc", "Yields bonus experience from mining and combat");
        add("trait.modernmachines.keen_edge", "Keen Edge");
        add("trait.modernmachines.keen_edge.desc", "Deals increased critical strike and attack damage");
        add("trait.modernmachines.hellforged", "Hellforged");
        add("trait.modernmachines.hellforged.desc", "Ignites targets and enhances efficiency in the Nether");
        add("trait.modernmachines.conductive", "Conductive");
        add("trait.modernmachines.conductive.desc", "Attracts nearby item drops while held and shocks in rain");
        add("trait.modernmachines.lightweight", "Lightweight");
        add("trait.modernmachines.lightweight.desc", "Increases base mining speed");
        add("trait.modernmachines.sturdy", "Sturdy");
        add("trait.modernmachines.sturdy.desc", "Improves overall durability efficiency");
        add("trait.modernmachines.sharp", "Sharp");
        add("trait.modernmachines.sharp.desc", "Increases base attack damage");
        add("trait.modernmachines.purifying", "Purifying");
        add("trait.modernmachines.purifying.desc", "Deals bonus radiant damage to undead enemies");
        add("trait.modernmachines.heavy", "Heavy");
        add("trait.modernmachines.heavy.desc", "Inflicts massive knockback and slowness on hit");
        add("trait.modernmachines.resilient", "Resilient");
        add("trait.modernmachines.resilient.desc", "Reduces durability loss and cleanses weakness");
        add("trait.modernmachines.tempered", "Tempered");
        add("trait.modernmachines.tempered.desc", "Balanced enhancement to mining speed and durability");
        add("trait.modernmachines.overclocked", "Overclocked");
        add("trait.modernmachines.overclocked.desc", "Significantly increases mining speed");
        add("trait.modernmachines.thermal", "Thermal");
        add("trait.modernmachines.thermal.desc", "Sets attacked targets ablaze");
        add("trait.modernmachines.unyielding", "Unyielding");
        add("trait.modernmachines.unyielding.desc", "Maintains high mining speed on hard materials");
        add("trait.modernmachines.radioactive", "Radioactive");
        add("trait.modernmachines.radioactive.desc", "Inflicts wither and poison upon targets");
        add("trait.modernmachines.lucky", "Lucky");
        add("trait.modernmachines.lucky.desc", "Grants increased loot and bonus experience");

        // Modifiers
        add("modifier.modernmachines.haste", "Haste (Redstone)");
        add("modifier.modernmachines.luck", "Luck (Lapis Lazuli)");
        add("modifier.modernmachines.sharpness", "Sharpness (Quartz)");
        add("modifier.modernmachines.diamond", "Reinforcement (Diamond)");
        add("modifier.modernmachines.reinforced", "Fireproof (Netherite)");

        // Material translation keys
        for (Material material : ModMaterials.getAllMaterials()) {
            add("material.modernmachines." + material.name(), material.displayName());
            for (ResourceForm form : material.supportedForms()) {
                if (material.isRegisteredLocally(form)) {
                    String englishName = form.getEnglishName(material.displayName());

                    if (form.isBlock()) {
                        addBlock(material.getDeferredBlock(form), englishName);
                    } else if (form.isItem()) {
                        addItem(material.getDeferredItem(form), englishName);
                    } else if (form.isFluid()) {
                        add("fluid_type." + ModernMachines.MOD_ID + "." + form.getRegistryName(material.name()), englishName);
                        addBlock(material.getDeferredBlock(form), englishName);
                        addItem(material.getDeferredItem(form), englishName + " Bucket");
                    }
                }
            }
        }
    }

    private String formatPartName(ToolPartType type, Material material) {
        String matName = material.displayName();
        return switch (type) {
            case PICKAXE_HEAD -> matName + " Pickaxe Head";
            case AXE_HEAD -> matName + " Axe Head";
            case SHOVEL_HEAD -> matName + " Shovel Head";
            case SWORD_BLADE -> matName + " Sword Blade";
            case HOE_HEAD -> matName + " Hoe Head";
            case HANDLE -> matName + " Tool Handle";
            case BINDING -> matName + " Tool Binding";
            case TIP -> matName + " Tool Tip";
            case GRIP -> matName + " Tool Grip";
            case SWORD_GUARD -> matName + " Sword Guard";
            case POMMEL -> matName + " Pommel";
        };
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        var words = str.split(" ");
        var sb = new StringBuilder();

        for (var w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1).toLowerCase()).append(" ");
            }
        }

        return sb.toString().trim();
    }
}
