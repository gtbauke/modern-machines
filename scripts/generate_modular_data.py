import os
import json

MATERIALS_DATA = {
    "wood": {
        "material": "modernmachines:wood",
        "color": "#855C38",
        "head": {"durability": 60, "mining_speed": 2.0, "attack_damage": 1.0, "harvest_tier": "wood"},
        "handle": {"durability_multiplier": 1.0, "mining_speed_multiplier": 1.0, "attack_speed_bonus": 0.0},
        "binding": {"bonus_durability": 20},
        "attachment": {"bonus_durability": 30, "attack_damage_bonus": 0.2},
        "traits": [{"id": "modernmachines:splintering", "level": 1}]
    },
    "stone": {
        "material": "modernmachines:stone",
        "color": "#7F7F7F",
        "head": {"durability": 131, "mining_speed": 4.0, "attack_damage": 1.5, "harvest_tier": "stone"},
        "handle": {"durability_multiplier": 0.8, "mining_speed_multiplier": 0.9, "attack_speed_bonus": -0.1},
        "binding": {"bonus_durability": 40},
        "attachment": {"bonus_durability": 50, "attack_damage_bonus": 0.4},
        "traits": [{"id": "modernmachines:cheap", "level": 1}]
    },
    "flint": {
        "material": "modernmachines:flint",
        "color": "#393939",
        "head": {"durability": 150, "mining_speed": 5.0, "attack_damage": 2.0, "harvest_tier": "stone"},
        "handle": {"durability_multiplier": 0.9, "mining_speed_multiplier": 1.0, "attack_speed_bonus": 0.0},
        "binding": {"bonus_durability": 50},
        "attachment": {"bonus_durability": 60, "attack_damage_bonus": 0.6},
        "traits": [{"id": "modernmachines:jagged", "level": 1}]
    },
    "bone": {
        "material": "modernmachines:bone",
        "color": "#E1DEC3",
        "head": {"durability": 180, "mining_speed": 4.5, "attack_damage": 1.5, "harvest_tier": "stone"},
        "handle": {"durability_multiplier": 1.1, "mining_speed_multiplier": 1.0, "attack_speed_bonus": 0.1},
        "binding": {"bonus_durability": 60},
        "attachment": {"bonus_durability": 70, "attack_damage_bonus": 0.5},
        "traits": [{"id": "modernmachines:fracture", "level": 1}]
    },
    "iron": {
        "material": "modernmachines:iron",
        "color": "#D8D8D8",
        "head": {"durability": 250, "mining_speed": 6.0, "attack_damage": 2.0, "harvest_tier": "iron"},
        "handle": {"durability_multiplier": 1.05, "mining_speed_multiplier": 1.0, "attack_speed_bonus": 0.0},
        "binding": {"bonus_durability": 80},
        "attachment": {"bonus_durability": 100, "attack_damage_bonus": 0.8},
        "traits": [{"id": "modernmachines:magnetic", "level": 1}]
    },
    "gold": {
        "material": "modernmachines:gold",
        "color": "#FDF55F",
        "head": {"durability": 32, "mining_speed": 12.0, "attack_damage": 1.0, "harvest_tier": "wood"},
        "handle": {"durability_multiplier": 0.5, "mining_speed_multiplier": 1.4, "attack_speed_bonus": 0.2},
        "binding": {"bonus_durability": 20},
        "attachment": {"bonus_durability": 30, "attack_damage_bonus": 0.2},
        "traits": [{"id": "modernmachines:lucky", "level": 1}]
    },
    "copper": {
        "material": "modernmachines:copper",
        "color": "#E77C56",
        "head": {"durability": 200, "mining_speed": 5.5, "attack_damage": 1.5, "harvest_tier": "stone"},
        "handle": {"durability_multiplier": 1.0, "mining_speed_multiplier": 1.05, "attack_speed_bonus": 0.0},
        "binding": {"bonus_durability": 70},
        "attachment": {"bonus_durability": 80, "attack_damage_bonus": 0.6},
        "traits": [{"id": "modernmachines:conductive", "level": 1}]
    },
    "diamond": {
        "material": "modernmachines:diamond",
        "color": "#4AEDD9",
        "head": {"durability": 1561, "mining_speed": 8.0, "attack_damage": 3.0, "harvest_tier": "diamond"},
        "handle": {"durability_multiplier": 1.2, "mining_speed_multiplier": 1.1, "attack_speed_bonus": 0.0},
        "binding": {"bonus_durability": 250},
        "attachment": {"bonus_durability": 300, "attack_damage_bonus": 1.2, "tier_override": "diamond"},
        "traits": [{"id": "modernmachines:crystal_sharp", "level": 1}]
    },
    "netherite": {
        "material": "modernmachines:netherite",
        "color": "#4C4447",
        "head": {"durability": 2031, "mining_speed": 9.0, "attack_damage": 4.0, "harvest_tier": "netherite"},
        "handle": {"durability_multiplier": 1.3, "mining_speed_multiplier": 1.15, "attack_speed_bonus": 0.0},
        "binding": {"bonus_durability": 400},
        "attachment": {"bonus_durability": 500, "attack_damage_bonus": 1.5, "tier_override": "netherite"},
        "traits": [{"id": "modernmachines:fireproof", "level": 1}]
    },
    "tin": {
        "material": "modernmachines:tin",
        "color": "#C0D3DD",
        "head": {"durability": 180, "mining_speed": 5.0, "attack_damage": 1.5, "harvest_tier": "stone"},
        "handle": {"durability_multiplier": 1.0, "mining_speed_multiplier": 1.0, "attack_speed_bonus": 0.0},
        "binding": {"bonus_durability": 60},
        "attachment": {"bonus_durability": 70, "attack_damage_bonus": 0.5},
        "traits": [{"id": "modernmachines:lightweight", "level": 1}]
    },
    "lead": {
        "material": "modernmachines:lead",
        "color": "#4D566B",
        "head": {"durability": 220, "mining_speed": 4.5, "attack_damage": 2.0, "harvest_tier": "iron"},
        "handle": {"durability_multiplier": 1.1, "mining_speed_multiplier": 0.9, "attack_speed_bonus": -0.1},
        "binding": {"bonus_durability": 90},
        "attachment": {"bonus_durability": 110, "attack_damage_bonus": 1.0},
        "traits": [{"id": "modernmachines:heavy", "level": 1}]
    },
    "silver": {
        "material": "modernmachines:silver",
        "color": "#DAE3EC",
        "head": {"durability": 190, "mining_speed": 7.0, "attack_damage": 2.0, "harvest_tier": "iron"},
        "handle": {"durability_multiplier": 1.0, "mining_speed_multiplier": 1.1, "attack_speed_bonus": 0.1},
        "binding": {"bonus_durability": 75},
        "attachment": {"bonus_durability": 90, "attack_damage_bonus": 0.8},
        "traits": [{"id": "modernmachines:purifying", "level": 1}]
    },
    "nickel": {
        "material": "modernmachines:nickel",
        "color": "#C5BFA7",
        "head": {"durability": 300, "mining_speed": 6.0, "attack_damage": 2.0, "harvest_tier": "iron"},
        "handle": {"durability_multiplier": 1.15, "mining_speed_multiplier": 1.0, "attack_speed_bonus": 0.0},
        "binding": {"bonus_durability": 100},
        "attachment": {"bonus_durability": 120, "attack_damage_bonus": 0.8},
        "traits": [{"id": "modernmachines:tough", "level": 1}]
    },
    "aluminum": {
        "material": "modernmachines:aluminum",
        "color": "#CBD7DB",
        "head": {"durability": 280, "mining_speed": 7.5, "attack_damage": 1.8, "harvest_tier": "iron"},
        "handle": {"durability_multiplier": 1.1, "mining_speed_multiplier": 1.15, "attack_speed_bonus": 0.1},
        "binding": {"bonus_durability": 85},
        "attachment": {"bonus_durability": 100, "attack_damage_bonus": 0.7},
        "traits": [{"id": "modernmachines:aerodynamic", "level": 1}]
    },
    "uranium": {
        "material": "modernmachines:uranium",
        "color": "#4AE252",
        "head": {"durability": 600, "mining_speed": 6.5, "attack_damage": 2.5, "harvest_tier": "iron"},
        "handle": {"durability_multiplier": 1.1, "mining_speed_multiplier": 1.0, "attack_speed_bonus": 0.0},
        "binding": {"bonus_durability": 150},
        "attachment": {"bonus_durability": 180, "attack_damage_bonus": 1.0},
        "traits": [{"id": "modernmachines:radioactive", "level": 1}]
    },
    "titanium": {
        "material": "modernmachines:titanium",
        "color": "#7D8394",
        "head": {"durability": 1800, "mining_speed": 8.5, "attack_damage": 3.5, "harvest_tier": "diamond"},
        "handle": {"durability_multiplier": 1.35, "mining_speed_multiplier": 1.1, "attack_speed_bonus": 0.0},
        "binding": {"bonus_durability": 350},
        "attachment": {"bonus_durability": 400, "attack_damage_bonus": 1.4, "tier_override": "diamond"},
        "traits": [{"id": "modernmachines:reinforced", "level": 1}]
    },
    "bronze": {
        "material": "modernmachines:bronze",
        "color": "#D18E42",
        "head": {"durability": 450, "mining_speed": 6.5, "attack_damage": 2.2, "harvest_tier": "iron"},
        "handle": {"durability_multiplier": 1.15, "mining_speed_multiplier": 1.05, "attack_speed_bonus": 0.0},
        "binding": {"bonus_durability": 120},
        "attachment": {"bonus_durability": 140, "attack_damage_bonus": 0.9},
        "traits": [{"id": "modernmachines:resilient", "level": 1}]
    },
    "steel": {
        "material": "modernmachines:steel",
        "color": "#636B78",
        "head": {"durability": 800, "mining_speed": 7.5, "attack_damage": 3.0, "harvest_tier": "diamond"},
        "handle": {"durability_multiplier": 1.25, "mining_speed_multiplier": 1.1, "attack_speed_bonus": 0.0},
        "binding": {"bonus_durability": 200},
        "attachment": {"bonus_durability": 250, "attack_damage_bonus": 1.2, "tier_override": "diamond"},
        "traits": [{"id": "modernmachines:sharp", "level": 1}]
    },
    "invar": {
        "material": "modernmachines:invar",
        "color": "#96A19D",
        "head": {"durability": 550, "mining_speed": 6.8, "attack_damage": 2.5, "harvest_tier": "iron"},
        "handle": {"durability_multiplier": 1.2, "mining_speed_multiplier": 1.0, "attack_speed_bonus": 0.0},
        "binding": {"bonus_durability": 160},
        "attachment": {"bonus_durability": 180, "attack_damage_bonus": 1.0},
        "traits": [{"id": "modernmachines:temperproof", "level": 1}]
    },
    "electrum": {
        "material": "modernmachines:electrum",
        "color": "#ECE072",
        "head": {"durability": 120, "mining_speed": 10.0, "attack_damage": 1.5, "harvest_tier": "iron"},
        "handle": {"durability_multiplier": 0.8, "mining_speed_multiplier": 1.3, "attack_speed_bonus": 0.2},
        "binding": {"bonus_durability": 50},
        "attachment": {"bonus_durability": 60, "attack_damage_bonus": 0.5},
        "traits": [{"id": "modernmachines:energized", "level": 1}]
    },
    "constantan": {
        "material": "modernmachines:constantan",
        "color": "#D98A5B",
        "head": {"durability": 400, "mining_speed": 6.2, "attack_damage": 2.0, "harvest_tier": "iron"},
        "handle": {"durability_multiplier": 1.1, "mining_speed_multiplier": 1.05, "attack_speed_bonus": 0.0},
        "binding": {"bonus_durability": 110},
        "attachment": {"bonus_durability": 130, "attack_damage_bonus": 0.8},
        "traits": [{"id": "modernmachines:thermal_resistance", "level": 1}]
    }
}

# Write material JSONs
materials_dir = "src/main/resources/data/modernmachines/materials"
os.makedirs(materials_dir, exist_ok=True)

for mat_name, data in MATERIALS_DATA.items():
    with open(f"{materials_dir}/{mat_name}.json", "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)
    print(f"Generated material stat JSON: {mat_name}.json")

# Write Datapack Alloy Smelting Recipes
recipes_dir = "src/main/resources/data/modernmachines/recipe"
os.makedirs(recipes_dir, exist_ok=True)

ALLOY_RECIPES = [
    {
        "name": "alloy_bronze",
        "data": {
            "type": "modernmachines:alloy_smelting",
            "inputs": [
                {"count": 3, "ingredient": {"tag": "c:ingots/copper"}},
                {"count": 1, "ingredient": {"tag": "c:ingots/tin"}}
            ],
            "energy": 3000,
            "cooking_time": 200,
            "result": {"count": 4, "id": "modernmachines:bronze_ingot"}
        }
    },
    {
        "name": "alloy_steel",
        "data": {
            "type": "modernmachines:alloy_smelting",
            "inputs": [
                {"count": 1, "ingredient": {"tag": "c:ingots/iron"}},
                {"count": 1, "ingredient": {"tag": "c:dusts/coal"}}
            ],
            "energy": 4000,
            "cooking_time": 240,
            "result": {"count": 1, "id": "modernmachines:steel_ingot"}
        }
    },
    {
        "name": "alloy_invar",
        "data": {
            "type": "modernmachines:alloy_smelting",
            "inputs": [
                {"count": 2, "ingredient": {"tag": "c:ingots/iron"}},
                {"count": 1, "ingredient": {"tag": "c:ingots/nickel"}}
            ],
            "energy": 3200,
            "cooking_time": 200,
            "result": {"count": 3, "id": "modernmachines:invar_ingot"}
        }
    },
    {
        "name": "alloy_electrum",
        "data": {
            "type": "modernmachines:alloy_smelting",
            "inputs": [
                {"count": 1, "ingredient": {"tag": "c:ingots/gold"}},
                {"count": 1, "ingredient": {"tag": "c:ingots/silver"}}
            ],
            "energy": 3000,
            "cooking_time": 180,
            "result": {"count": 2, "id": "modernmachines:electrum_ingot"}
        }
    },
    {
        "name": "alloy_constantan",
        "data": {
            "type": "modernmachines:alloy_smelting",
            "inputs": [
                {"count": 1, "ingredient": {"tag": "c:ingots/copper"}},
                {"count": 1, "ingredient": {"tag": "c:ingots/nickel"}}
            ],
            "energy": 3000,
            "cooking_time": 180,
            "result": {"count": 2, "id": "modernmachines:constantan_ingot"}
        }
    }
]

for recipe in ALLOY_RECIPES:
    with open(f"{recipes_dir}/{recipe['name']}.json", "w", encoding="utf-8") as f:
        json.dump(recipe["data"], f, indent=2)
    print(f"Generated alloy recipe JSON: {recipe['name']}.json")

print("All material stats and alloy recipes generated successfully!")
