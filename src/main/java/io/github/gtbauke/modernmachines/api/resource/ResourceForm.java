package io.github.gtbauke.modernmachines.api.resource;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public enum ResourceForm {
    ORE(FormType.BLOCK, "%s_ore", "ores/%s", "ores", "%s Ore"),
    DEEPSLATE_ORE(FormType.BLOCK, "deepslate_%s_ore", "ores/%s", "ores", "Deepslate %s Ore"),
    NETHERRACK_ORE(FormType.BLOCK, "netherrack_%s_ore", "ores/%s", "ores", "Netherrack %s Ore"),
    END_STONE_ORE(FormType.BLOCK, "end_stone_%s_ore", "ores/%s", "ores", "End Stone %s Ore"),
    STORAGE_BLOCK(FormType.BLOCK, "%s_block", "storage_blocks/%s", "storage_blocks", "Block of %s"),
    RAW_STORAGE_BLOCK(FormType.BLOCK, "raw_%s_block", "storage_blocks/raw_%s", "storage_blocks", "Block of Raw %s"),
    RAW_ORE(FormType.ITEM, "raw_%s", "raw_materials/%s", "raw_materials", "Raw %s"),
    INGOT(FormType.ITEM, "%s_ingot", "ingots/%s", "ingots", "%s Ingot"),
    GEM(FormType.ITEM, "%s", "gems/%s", "gems", "%s"),
    NUGGET(FormType.ITEM, "%s_nugget", "nuggets/%s", "nuggets", "%s Nugget"),
    DUST(FormType.ITEM, "%s_dust", "dusts/%s", "dusts", "%s Dust"),
    PLATE(FormType.ITEM, "%s_plate", "plates/%s", "plates", "%s Plate"),
    ROD(FormType.ITEM, "%s_rod", "rods/%s", "rods", "%s Rod"),
    SCREW(FormType.ITEM, "%s_screw", "screws/%s", "screws", "%s Screw"),
    WIRE(FormType.ITEM, "%s_wire", "wires/%s", "wires", "%s Wire"),
    GEAR(FormType.ITEM, "%s_gear", "gears/%s", "gears", "%s Gear");

    public enum FormType {
        BLOCK,
        ITEM
    }

    private final FormType formType;
    private final String namePattern;
    private final String tagPattern;
    private final String pluralTag;
    private final String englishNamePattern;

    ResourceForm(FormType formType, String namePattern, String tagPattern, String pluralTag, String englishNamePattern) {
        this.formType = formType;
        this.namePattern = namePattern;
        this.tagPattern = tagPattern;
        this.pluralTag = pluralTag;
        this.englishNamePattern = englishNamePattern;
    }

    public boolean isBlock() {
        return this.formType == FormType.BLOCK;
    }

    public boolean isItem() {
        return this.formType == FormType.ITEM;
    }

    public String getRegistryName(String materialName) {
        return String.format(this.namePattern, materialName);
    }

    public String getEnglishName(String materialDisplayName) {
        return String.format(this.englishNamePattern, materialDisplayName);
    }

    public TagKey<Item> getItemTag(String materialName) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", String.format(this.tagPattern, materialName)));
    }

    public TagKey<Block> getBlockTag(String materialName) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", String.format(this.tagPattern, materialName)));
    }

    public TagKey<Item> getPluralItemTag() {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", this.pluralTag));
    }

    public TagKey<Block> getPluralBlockTag() {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", this.pluralTag));
    }
}
