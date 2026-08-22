# Game Balance & Progression Guide (Layer 3 Reference)

This document establishes the mathematical baselines and tier curves for **Modern Machines**.
Every new machine, material, and modular part must adhere to these baselines.

---

## 1. Energy Tiers (Forge Energy / FE)

| Tier | Name | Base Buffer (FE) | Max Transfer (FE/t) | Typical Work Draw (FE/t) | Typical Process Time |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Tier 1** | Steam / Basic | 10,000 – 50,000 | 80 – 160 | 20 – 40 | 200 ticks (10s) |
| **Tier 2** | Advanced / Electric | 100,000 – 500,000 | 400 – 1,000 | 80 – 160 | 100 ticks (5s) |
| **Tier 3** | Industrial / Precision | 1,000,000 – 5,000,000 | 2,000 – 8,000 | 320 – 640 | 50 ticks (2.5s) |
| **Tier 4** | Quantum / Ultimate | 10,000,000+ | 16,000 – 64,000 | 1,280 – 2,560 | 20 ticks (1s) |

---

## 2. Machine Upgrade Scaling (`MachineStats`)

Upgrades scale multiplicatively using base stat calculations:
- **Speed Upgrade**: Each tier/stack multiplies speed by $1.5\times$ and energy consumption by $1.8\times$.
  $$\text{Speed} = \text{Base Speed} \times 1.5^N$$
  $$\text{Energy Per Tick} = \text{Base Energy} \times 1.8^N$$
- **Energy Efficiency Upgrade**: Reduces energy consumption per tick by $20\%$ per upgrade (compounding up to $80\%$ max reduction).
- **Capacity Upgrade**: Multiplies internal FE buffer and fluid storage by $2.0\times$ per level.

---

## 3. Material & Tool Balancing Curves

| Material Class | Hardness / Resistance | Durability Baseline | Mining Speed | Attack Dmg Bonus | Harvest Tier |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Basic Metals** (Copper, Tin, Lead) | 3.0 / 3.0 | 250 – 450 | 5.0 – 6.0 | +1.5 to +2.5 | Stone (`NEEDS_STONE_TOOL`) |
| **Mid Metals** (Bronze, Invar, Aluminum) | 4.0 / 5.0 | 500 – 900 | 6.5 – 8.0 | +2.5 to +3.5 | Iron (`NEEDS_IRON_TOOL`) |
| **Heavy / Precious** (Silver, Electrum, Steel) | 5.0 / 6.0 | 800 – 1,400 | 7.5 – 9.5 | +3.5 to +4.5 | Diamond (`NEEDS_DIAMOND_TOOL`) |
| **Superalloys** (Titanium, Tungsten, Uranium) | 8.0 / 10.0 | 1,600 – 2,500 | 9.0 – 12.0 | +4.5 to +6.0 | Netherite (`NEEDS_NETHERITE_TOOL`) |

---

## 4. Recipe Balance Guidelines
- Raw materials convert 1:1 with standard processing (or 1:2 with crusher + washer chains).
- Alloys require balanced ingredient stoichiometry (e.g., Bronze: 3 Copper + 1 Tin $\rightarrow$ 4 Bronze; Invar: 2 Iron + 1 Nickel $\rightarrow$ 3 Invar).
- Secondary byproduct outputs should have reasonable probability distributions ($10\% - 30\%$).
