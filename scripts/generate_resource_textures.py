import os
from PIL import Image

def hex_to_rgb(hex_val):
    if isinstance(hex_val, str):
        hex_val = int(hex_val.replace('#', '').replace('0x', ''), 16)
    r = (hex_val >> 16) & 0xFF
    g = (hex_val >> 8) & 0xFF
    b = hex_val & 0xFF
    return (r, g, b)

def apply_palette_map(ascii_art, palette_map):
    lines = [l for l in ascii_art.strip().splitlines()]
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(16):
        line = lines[y] if y < len(lines) else ""
        for x in range(16):
            char = line[x] if x < len(line) else "."
            if char in palette_map and palette_map[char] is not None:
                color = palette_map[char]
                if len(color) == 3:
                    color = color + (255,)
                img.putpixel((x, y), color)
    return img

def create_tinted_ramp(base_hex, factor_shadow=0.35, factor_mid=0.75, factor_base=1.0, factor_high=1.35, factor_spec=1.7):
    r, g, b = hex_to_rgb(base_hex)
    def scale(f):
        return (
            min(255, max(0, int(r * f))),
            min(255, max(0, int(g * f))),
            min(255, max(0, int(b * f))),
            255
        )
    return {
        '#': scale(factor_shadow),  # Contour / deep shadow
        'd': scale(factor_mid),     # Dark midtone
        'm': scale(factor_base),    # Base metal
        'h': scale(factor_high),    # Bright highlight
        'H': scale(factor_spec),    # Specular glint
        '.': None
    }

# -------------------------------------------------------------
# 1. DUST TEMPLATE (Redstone/Glowstone inspired fine crystalline dust pile)
# -------------------------------------------------------------
DUST_ART = """
................
......H.........
.....Hmh........
....#hmhh#......
...#hmmhmd#..h..
...#dmmmmmh#Hh..
..#hmmmmmhhmd#..
..#mmmmmmmmmd#..
.#hmmhmmmmmmmd#.
.#dmmhmmhmmmmd#.
.#dmmmmmmmmmmd#.
.#ddmmmmmmmmdd#.
.##dmmmmmmmd###.
..##ddddddd##...
....#######.....
................
"""

# -------------------------------------------------------------
# 2. ROD TEMPLATE (Stick-inspired diagonal cylindrical metal rod)
# -------------------------------------------------------------
ROD_ART = """
.............hH#
............hHm#
...........hHm#d
..........hHm#d.
.........hHm#d..
........hHm#d...
.......hHm#d....
......hHm#d.....
.....hHm#d......
....hHm#d.......
...hHm#d........
..hHm#d.........
.hHm#d..........
hHm#d...........
#m#d............
.#d.............
"""

# -------------------------------------------------------------
# 3. WIRE TEMPLATE (Neat industrial coiled loop)
# -------------------------------------------------------------
WIRE_ART = """
.....#####......
...##hHHHh##....
..#hHhhhhhHh#...
.#hH#ddddd#Hh#..
#hH#d.....d#Hh#.
#Hh#.......#hH#m
#hH#.......#hH#H
#Hh#.......#hH#h
#hH#.......#hH#d
#Hh#d.....d#hH##
.#hH#ddddd#hH#..
..#hHhhhhhHh#...
...##hHHHh##....
.....#####......
................
................
"""

# -------------------------------------------------------------
# 4. SCREW TEMPLATE (Realistic threaded mechanical screw)
# -------------------------------------------------------------
SCREW_ART = """
...########.....
..#hHHHHHHh#....
.#hHhhhhhhHh#...
.#Hh#dddd#hH#...
..###hhhh###....
....#hHmh#......
....#Hmh#d......
....#hHmh#......
....#Hmh#d......
....#hHmh#......
....#Hmh#d......
....#hHmh#......
.....#hm#d......
.....#hm#.......
......#h#.......
.......#........
"""

# -------------------------------------------------------------
# 5. PLATE TEMPLATE (Rolled metal sheet with beveled edge & corner rivets)
# -------------------------------------------------------------
PLATE_ART = """
.##############.
#hHHHHHHHHHHHHh#
#Hhhhhhhhhhhhhh#
#Hh#mddddddm#hH#
#HhdmmmmmmmmdhH#
#HhdmmmmmmmmdhH#
#HhdmmmmmmmmdhH#
#HhdmmmmmmmmdhH#
#HhdmmmmmmmmdhH#
#HhdmmmmmmmmdhH#
#HhdmmmmmmmmdhH#
#Hh#mddddddm#hH#
#HhmmmmmmmmmmhH#
#HddddddddddddH#
#hhhhhhhhhhhhhh#
.##############.
"""

# -------------------------------------------------------------
# 6. GEAR TEMPLATE (Precision 8-tooth gear)
# -------------------------------------------------------------
GEAR_ART = """
......####......
.....#hHHh#.....
..##.#hHHh#.##..
.#hH##hHHh##Hh#.
.#HHhmmmmmmhHH#.
.##Hhm####mhH##.
..#Hhm#..#mhH#..
..#Hhm#..#mhH#..
..#Hhm#..#mhH#..
.##Hhm####mhH##.
.#HHhmmmmmmhHH#.
.#hH##hHHh##Hh#.
..##.#hHHh#.##..
.....#hHHh#.....
......####......
................
"""

# -------------------------------------------------------------
# 7. RAW ORE ARCHETYPES (Chunky, Crystal, Spiky, Dense)
# -------------------------------------------------------------
RAW_CHUNKY_ART = """
................
.....####.......
...##hHHh##.....
..#hHhhhhHh#....
.#hHhmmmmhhH#...
#hHhmmmmmmdhH#..
#Hhmm#ddmmddhH#.
#Hhm#..#mmddhH#.
#hHh#..#mmmdhH#.
.#hHh##mmmmdhH#.
..#hHhmmmmddhH#.
...#hHhmmmddhH#.
....#hHhddddhH#.
.....##hHHHHh##.
.......######...
................
"""

RAW_CRYSTAL_ART = """
........#.......
.......#H#......
......#hHh#.....
.....#hHHHh#....
....#hHhmhHh#...
...#hHhm#mhHh#..
..#hHhm#.#mhHh#.
.#hHhm#...#mhHh#
#hHhm#.....#mhHh
#hHh#.......#mhH
.#hH#.......#mh#
..#hH#.....#mh#.
...#hH#...#mh#..
....#hH#.#mh#...
.....#hH#mh#....
......#####.....
"""

RAW_SPIKY_ART = """
.......#........
......#H#...#...
..#..#hHh#.#H#..
.#H##hHHHh#hHh#.
#hHh#hHmhH#hHHh#
#HHh#HhmhH#HHmh#
#hHhhHhmhHHHmhH#
.#hHHHhmhHHmhH#.
..#hHHhmhHmhH#..
...#hHhmhHmh#...
....#hHhmhH#....
.....#hHmhH#....
......#hHh#.....
.......#h#......
........#.......
................
"""

RAW_DENSE_ART = """
................
......#####.....
....##hHHHh##...
...#hHhhhhhHh#..
..#hHhmmmmmhHh#.
.#hHhmmmmmmmdhH#
#hHhmmmmmmmmmdhH
#HhmmmmmmmmmmdhH
#HhmmmmmmmmmmdhH
#hHhmmmmmmmmmdhH
.#hHhmmmmmmmdhH#
..#hHhmmmmmdhH#.
...#hHddddddhH#.
....##hHHHHh##..
......#####.....
................
"""

# -------------------------------------------------------------
# 8. ORE VEIN OVERLAY ARCHETYPES (Chunky, Crystal, Spiky, Dense)
# -------------------------------------------------------------
VEIN_CHUNKY_ART = """
................
..###...........
.#hHH#..........
#hHmhH#....###..
#HmdmhH#..#hHH#.
#hHmdhH#.#hHmhH#
.#hHHh#..#Hmdmh#
..###....#hHmdh#
..........#hHHh#
...###.....###..
..#hHH#.........
.#hHmhH#........
.#HmdmhH#.......
.#hHmdhH#.......
..#hHHh#........
...###..........
"""

VEIN_CRYSTAL_ART = """
................
...#............
..#H#.....#.....
.#hHh#...#H#....
..#hHh#.#hHh#...
...#hHh#hHh#....
....#hHhHh#.....
.....#hHh#......
......#H#.......
.....#hHh#......
....#hHhHh#...#.
...#hHh#hHh#.#H#
..#hHh#.#hHh#hHh
...#H#...#hHhHh#
....#.....#hHh#.
...........#H#..
"""

VEIN_SPIKY_ART = """
................
.#..............
#H#...##........
#hH#.#hH#...#...
.#hH#HmhH#.#H#..
..#hHmdmhH#hHh#.
...#hHmdhH#hHHh#
....#hHHh##Hmdh#
.....###.#hHmdh#
.........#hHHh#.
...##.....###...
..#hH#..........
.#hHmh#...#.....
.#Hmdmh#.#H#....
..#hHmdh#hH#....
...#hHHh##H#....
"""

VEIN_DENSE_ART = """
................
..#.......#.....
.#H#.....#H#....
.#h#......#h#...
......#.........
.....#H#....#...
.....#h#...#H#..
............#h#.
..#.............
.#H#.....#......
.#h#....#H#.....
........#h#...#.
....#........#H#
...#H#.......#h#
...#h#..........
................
"""

MATERIAL_ARCHETYPES = {
    'tin': 'chunky',
    'nickel': 'chunky',
    'invar': 'chunky',
    'lead': 'crystal',
    'zinc': 'crystal',
    'uranium': 'crystal',
    'diamond': 'crystal',
    'emerald': 'crystal',
    'copper': 'spiky',
    'aluminum': 'spiky',
    'titanium': 'spiky',
    'brass': 'spiky',
    'bronze': 'spiky',
    'silver': 'dense',
    'platinum': 'dense',
    'tungsten': 'dense',
    'gold': 'dense',
    'electrum': 'dense',
    'steel': 'dense',
    'iron': 'chunky',
    'netherite': 'dense',
}

MATERIALS = [
    ("iron", 0xD8D8D8, True),
    ("gold", 0xFDF55F, True),
    ("copper", 0xE0734D, True),
    ("tin", 0xD3D8EC, False),
    ("lead", 0x4A5364, False),
    ("silver", 0xD7E6EC, False),
    ("nickel", 0xB3B79C, False),
    ("zinc", 0xBAC3B8, False),
    ("aluminum", 0xB8C7D0, False),
    ("bronze", 0xCD7F32, False),
    ("brass", 0xE1C158, False),
    ("invar", 0xA0A7A3, False),
    ("electrum", 0xF2E47E, False),
    ("steel", 0x6E7882, False),
    ("platinum", 0x76BCCC, False),
    ("tungsten", 0x383C45, False),
    ("uranium", 0x54D942, False),
    ("titanium", 0xA3ABB5, False),
    ("diamond", 0x4AEDD9, True),
    ("emerald", 0x17DD62, True),
    ("netherite", 0x4C4143, True)
]

RAW_ARTS = {
    'chunky': RAW_CHUNKY_ART,
    'crystal': RAW_CRYSTAL_ART,
    'spiky': RAW_SPIKY_ART,
    'dense': RAW_DENSE_ART
}

VEIN_ARTS = {
    'chunky': VEIN_CHUNKY_ART,
    'crystal': VEIN_CRYSTAL_ART,
    'spiky': VEIN_SPIKY_ART,
    'dense': VEIN_DENSE_ART
}

# Base background textures from bedrock sample pack
BEDROCK_BLOCKS = r"C:\Users\gusta\dev\bedrock-samples-main\bedrock-samples-main\resource_pack\textures\blocks"
stone_bg = Image.open(f"{BEDROCK_BLOCKS}/stone.png").convert("RGBA")
deepslate_bg = Image.open(f"{BEDROCK_BLOCKS}/deepslate/deepslate.png").convert("RGBA")

items_dir = "src/main/resources/assets/modernmachines/textures/item"
blocks_dir = "src/main/resources/assets/modernmachines/textures/block"
os.makedirs(items_dir, exist_ok=True)
os.makedirs(blocks_dir, exist_ok=True)

for name, hex_col, is_vanilla in MATERIALS:
    palette = create_tinted_ramp(hex_col)
    arch = MATERIAL_ARCHETYPES.get(name, 'chunky')
    raw_art = RAW_ARTS[arch]
    vein_art = VEIN_ARTS[arch]

    # 1. Dust
    dust_img = apply_palette_map(DUST_ART, palette)
    dust_img.save(f"{items_dir}/{name}_dust.png")

    # 2. Plate
    plate_img = apply_palette_map(PLATE_ART, palette)
    plate_img.save(f"{items_dir}/{name}_plate.png")

    # 3. Rod
    rod_img = apply_palette_map(ROD_ART, palette)
    rod_img.save(f"{items_dir}/{name}_rod.png")

    # 4. Screw
    screw_img = apply_palette_map(SCREW_ART, palette)
    screw_img.save(f"{items_dir}/{name}_screw.png")

    # 5. Wire
    wire_img = apply_palette_map(WIRE_ART, palette)
    wire_img.save(f"{items_dir}/{name}_wire.png")

    # 6. Gear
    gear_img = apply_palette_map(GEAR_ART, palette)
    gear_img.save(f"{items_dir}/{name}_gear.png")

    # If mod-registered resource (not vanilla base)
    if not is_vanilla:
        # Ingot
        ingot_palette = create_tinted_ramp(hex_col, factor_shadow=0.3, factor_mid=0.7, factor_base=1.0, factor_high=1.3, factor_spec=1.6)
        ingot_img = apply_palette_map(PLATE_ART, ingot_palette)
        ingot_img.save(f"{items_dir}/{name}_ingot.png")

        # Nugget
        nugget_img = apply_palette_map(RAW_DENSE_ART, ingot_palette)
        nugget_img.save(f"{items_dir}/{name}_nugget.png")

        # Raw Ore Item
        raw_img = apply_palette_map(raw_art, palette)
        raw_img.save(f"{items_dir}/raw_{name}.png")

        # Storage Block
        block_img = apply_palette_map(PLATE_ART, palette)
        block_img.save(f"{blocks_dir}/{name}_block.png")

        # Raw Storage Block
        raw_block_img = apply_palette_map(RAW_CHUNKY_ART, palette)
        raw_block_img.save(f"{blocks_dir}/raw_{name}_block.png")

        # Ore Blocks (Stone & Deepslate)
        vein_overlay = apply_palette_map(vein_art, palette)

        stone_ore = stone_bg.copy()
        stone_ore.alpha_composite(vein_overlay)
        stone_ore.save(f"{blocks_dir}/{name}_ore.png")

        deepslate_ore = deepslate_bg.copy()
        deepslate_ore.alpha_composite(vein_overlay)
        deepslate_ore.save(f"{blocks_dir}/deepslate_{name}_ore.png")

print("Successfully generated all multi-archetype textures for all 21 materials!")
