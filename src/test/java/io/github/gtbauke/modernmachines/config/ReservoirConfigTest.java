package io.github.gtbauke.modernmachines.config;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.gtbauke.modernmachines.config.reservoir.ReservoirConfig;
import io.github.gtbauke.modernmachines.config.reservoir.ReservoirConfigDeserializer;
import io.github.gtbauke.modernmachines.world.reservoir.ReservoirInstance;

public class ReservoirConfigTest {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ReservoirConfig.class, new ReservoirConfigDeserializer())
            .setPrettyPrinting()
            .create();

    @Test
    public void testReservoirConfigParsing() {
        var json = """
                {
                    "name": "crude_oil",
                    "enabled": true,
                    "fluid": "modernmachines:crude_oil",
                    "barrier_block": "modernmachines:oil_shale",
                    "capstone_block": "modernmachines:capstone",
                    "dimensions": ["minecraft:overworld"],
                    "biome_tags": ["#minecraft:is_overworld"],
                    "min_y": -50,
                    "max_y": 20,
                    "rarity": 32,
                    "radius_xz": 12,
                    "radius_y": 6,
                    "initial_pressure": 8.0,
                    "capacity": 300000,
                    "hazards": {
                        "eruption": true,
                        "eruption_velocity": 1.5,
                        "flammable": true,
                        "ignition_radius": 7,
                        "explosion_strength": 4.0,
                        "toxic": true,
                        "toxic_duration": 45,
                        "toxic_effects": ["minecraft:blindness", "minecraft:nausea"]
                    }
                }
                """;

        var config = GSON.fromJson(json, ReservoirConfig.class);
        assertNotNull(config);
        assertEquals("crude_oil", config.name());
        assertTrue(config.enabled());
        assertEquals("modernmachines:crude_oil", config.fluid());
        assertEquals("modernmachines:oil_shale", config.barrierBlock());
        assertEquals("modernmachines:capstone", config.capstoneBlock());
        assertEquals(List.of("minecraft:overworld"), config.dimensions());
        assertEquals(List.of("#minecraft:is_overworld"), config.biomeTags());
        assertEquals(-50, config.minY());
        assertEquals(20, config.maxY());
        assertEquals(32, config.rarity());
        assertEquals(12, config.radiusXz());
        assertEquals(6, config.radiusY());
        assertEquals(8.0f, config.initialPressure(), 0.001f);
        assertEquals(300_000L, config.capacity());

        assertNotNull(config.hazards());
        assertTrue(config.hazards().eruption());
        assertEquals(1.5f, config.hazards().eruptionVelocity(), 0.001f);
        assertTrue(config.hazards().flammable());
        assertEquals(7, config.hazards().ignitionRadius());
        assertEquals(4.0f, config.hazards().explosionStrength(), 0.001f);
        assertTrue(config.hazards().toxic());
        assertEquals(45, config.hazards().toxicDuration());
        assertEquals(2, config.hazards().toxicEffects().size());
    }

    @Test
    public void testReservoirConfigDefaultsAndClamping() {
        var json = """
                {
                    "enabled": true,
                    "rarity": -5,
                    "radius_xz": 999,
                    "radius_y": 0,
                    "initial_pressure": -2.0,
                    "capacity": -100
                }
                """;

        var config = GSON.fromJson(json, ReservoirConfig.class);
        assertNotNull(config);
        assertTrue(config.enabled());
        assertEquals("minecraft:water", config.fluid());
        assertEquals("minecraft:tuff", config.barrierBlock());
        assertEquals(24, config.rarity());
        assertEquals(24, config.radiusXz());
        assertEquals(4, config.radiusY());
        assertEquals(5.0f, config.initialPressure(), 0.001f);
        assertEquals(100_000L, config.capacity());
    }

    @Test
    public void testReservoirInstanceDrainAndPressure() {
        var instance = ReservoirInstance.createNew(
                "crude_oil",
                "modernmachines:crude_oil",
                0, -30, 0,
                16, -20, 16,
                100_000L,
                10.0f
        );

        assertEquals(100_000L, instance.getCurrentVolume());
        assertEquals(10.0f, instance.getCurrentPressure(), 0.001f);
        assertTrue(instance.contains(8, -25, 8));
        assertFalse(instance.contains(30, -25, 30));

        long drained = instance.drain(50_000L);
        assertEquals(50_000L, drained);
        assertEquals(50_000L, instance.getCurrentVolume());
        assertEquals(5.0f, instance.getCurrentPressure(), 0.001f);

        // Water injection (secondary recovery)
        instance.inject(50_000L);
        assertEquals(10.0f, instance.getCurrentPressure(), 0.001f);
    }
}
