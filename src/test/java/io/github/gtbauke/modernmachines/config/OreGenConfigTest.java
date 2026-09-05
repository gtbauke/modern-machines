package io.github.gtbauke.modernmachines.config;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.gtbauke.modernmachines.config.material.CustomMaterialConfig;
import io.github.gtbauke.modernmachines.config.material.OreGenConfig;
import io.github.gtbauke.modernmachines.config.material.OreGenRule;
import io.github.gtbauke.modernmachines.config.material.OreGenRuleDeserializer;
import io.github.gtbauke.modernmachines.config.material.OreTargetConfig;
import io.github.gtbauke.modernmachines.config.material.OreTargetConfigDeserializer;

public class OreGenConfigTest {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(OreTargetConfig.class, new OreTargetConfigDeserializer())
            .registerTypeAdapter(OreGenRule.class, new OreGenRuleDeserializer())
            .setPrettyPrinting()
            .create();

    @Test
    public void testLegacyFormatParsing() {
        var json = """
                {
                    "name": "copper",
                    "ore_generation": {
                        "enabled": true,
                        "overworld": {
                            "enabled": true,
                            "vein_size": 12,
                            "veins_per_chunk": 16,
                            "min_y": -16,
                            "max_y": 112,
                            "distribution": "triangle"
                        },
                        "nether": {
                            "enabled": false
                        }
                    }
                }
                """;

        var config = GSON.fromJson(json, CustomMaterialConfig.class);
        assertNotNull(config);
        assertNotNull(config.oreGeneration);
        assertTrue(config.oreGeneration.enabled());

        var rules = config.oreGeneration.getResolvedRules(3.0f, true, true, false, false);
        assertEquals(1, rules.size());

        var overworldRule = rules.get(0);
        assertTrue(overworldRule.enabled());
        assertEquals(12, overworldRule.veinSize());
        assertEquals(16, overworldRule.veinsPerChunk());
        assertEquals(-16, overworldRule.minY());
        assertEquals(112, overworldRule.maxY());
        assertEquals("triangle", overworldRule.distribution());
        assertEquals(2, overworldRule.targets().size());
        assertEquals("minecraft:overworld", overworldRule.dimensions().get(0));
    }

    @Test
    public void testRuleBasedFormatParsing() {
        var json = """
                {
                    "name": "titanium",
                    "ore_generation": {
                        "enabled": true,
                        "rules": [
                            {
                                "enabled": true,
                                "dimension": "minecraft:overworld",
                                "biome_tag": "#minecraft:is_mountain",
                                "target": "minecraft:stone_ore_replaceables",
                                "vein_size": 6,
                                "veins_per_chunk": 4,
                                "min_y": 80,
                                "max_y": 256,
                                "distribution": "uniform",
                                "discard_chance_on_air_exposure": 0.75,
                                "required_mod": "some_mod",
                                "adjacent_to_block": "minecraft:lava"
                            }
                        ]
                    }
                }
                """;

        var config = GSON.fromJson(json, CustomMaterialConfig.class);
        assertNotNull(config);
        assertNotNull(config.oreGeneration);

        var rules = config.oreGeneration.getResolvedRules(5.0f, true, true, false, false);
        assertEquals(1, rules.size());

        var rule = rules.get(0);
        assertTrue(rule.enabled());
        assertEquals(List.of("minecraft:overworld"), rule.dimensions());
        assertEquals(List.of("#minecraft:is_mountain"), rule.biomeTags());
        assertEquals(1, rule.targets().size());
        assertEquals("tag_match", rule.targets().get(0).targetType());
        assertEquals("minecraft:stone_ore_replaceables", rule.targets().get(0).target());
        assertEquals(6, rule.veinSize());
        assertEquals(4, rule.veinsPerChunk());
        assertEquals(80, rule.minY());
        assertEquals(256, rule.maxY());
        assertEquals("uniform", rule.distribution());
        assertEquals(0.75f, rule.discardChanceOnAirExposure(), 0.001f);
        assertEquals("some_mod", rule.requiredMod());
        assertEquals("minecraft:lava", rule.adjacentToBlock());
    }

    @Test
    public void testValueClamping() {
        var json = """
                {
                    "enabled": true,
                    "vein_size": 9999,
                    "veins_per_chunk": -50,
                    "discard_chance_on_air_exposure": 5.0
                }
                """;

        var rule = GSON.fromJson(json, OreGenRule.class);
        assertNotNull(rule);
        assertEquals(64, rule.veinSize());
        assertEquals(0, rule.veinsPerChunk());
        assertEquals(1.0f, rule.discardChanceOnAirExposure(), 0.001f);
    }

    @Test
    public void testMultiTargetAndBlacklistParsing() {
        var json = """
                {
                    "enabled": true,
                    "dimensions": ["minecraft:overworld", "aether:the_aether"],
                    "dimension_blacklist": ["minecraft:the_nether"],
                    "biome_tags": ["#minecraft:is_mountain", "#c:is_sandy"],
                    "biome_blacklist": ["minecraft:ocean"],
                    "targets": [
                        {
                            "type": "tag_match",
                            "target": "minecraft:stone_ore_replaceables",
                            "form": "ore"
                        },
                        {
                            "type": "block_match",
                            "target": "aether:holystone",
                            "state": "modernmachines:holystone_titanium_ore"
                        }
                    ],
                    "vein_size": 8,
                    "veins_per_chunk": 5,
                    "distribution": "trapezoid",
                    "min_y": -32,
                    "max_y": 128
                }
                """;

        var rule = GSON.fromJson(json, OreGenRule.class);
        assertNotNull(rule);
        assertEquals(2, rule.dimensions().size());
        assertEquals(1, rule.dimensionBlacklist().size());
        assertEquals(2, rule.biomeTags().size());
        assertEquals(1, rule.biomeBlacklist().size());
        assertEquals(2, rule.targets().size());

        assertEquals("tag_match", rule.targets().get(0).targetType());
        assertEquals("minecraft:stone_ore_replaceables", rule.targets().get(0).target());
        assertEquals("ore", rule.targets().get(0).oreForm());

        assertEquals("block_match", rule.targets().get(1).targetType());
        assertEquals("aether:holystone", rule.targets().get(1).target());
        assertEquals("modernmachines:holystone_titanium_ore", rule.targets().get(1).state());

        assertEquals("trapezoid", rule.distribution());
    }
}
