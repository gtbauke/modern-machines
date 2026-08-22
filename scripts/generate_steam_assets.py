import os
from PIL import Image

def hex_to_rgb(hex_val):
    if isinstance(hex_val, str):
        hex_val = int(hex_val.replace('#', '').replace('0x', ''), 16)
    r = (hex_val >> 16) & 0xFF
    g = (hex_val >> 8) & 0xFF
    b = hex_val & 0xFF
    return (r, g, b)

def render_ascii_texture(ascii_art, palette):
    lines = [l for l in ascii_art.strip().splitlines()]
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(16):
        line = lines[y] if y < len(lines) else ""
        for x in range(16):
            char = line[x] if x < len(line) else "."
            if char in palette:
                color = palette[char]
                if color is None:
                    img.putpixel((x, y), (0, 0, 0, 0))
                else:
                    if len(color) == 3:
                        color = color + (255,)
                    img.putpixel((x, y), color)
    return img

PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
BLOCKS_DIR = os.path.join(PROJECT_ROOT, "src", "main", "resources", "assets", "modernmachines", "textures", "block")
ITEMS_DIR = os.path.join(PROJECT_ROOT, "src", "main", "resources", "assets", "modernmachines", "textures", "item")
FLUIDS_DIR = os.path.join(PROJECT_ROOT, "src", "main", "resources", "assets", "modernmachines", "textures", "fluid")

os.makedirs(BLOCKS_DIR, exist_ok=True)
os.makedirs(ITEMS_DIR, exist_ok=True)
os.makedirs(FLUIDS_DIR, exist_ok=True)

# -------------------------------------------------------------
# PALETTES
# -------------------------------------------------------------
BRONZE_PALETTE = {
    '#': (75, 45, 20),     # Deep bronze outline / crevice
    'd': (140, 85, 40),    # Bronze shadow
    'm': (190, 120, 60),   # Bronze midtone
    'h': (230, 160, 95),   # Bronze highlight
    'H': (255, 200, 140),  # Specular glint
    'b': (60, 35, 15),     # Bolt / rivet dark
    'B': (240, 190, 120),  # Bolt rivet highlight
    '.': None
}

CASING_ART = """
#HHHHHHHHHHHHHH#
Hbbhmmmmmmmmhbbd
HBBhmmmmmmmmhBBd
Hhhdmmmmmmmmdhh#
Hmmmmmmmmmmmmmm#
Hmmmmmmmmmmmmmm#
Hmmmmmmmmmmmmmm#
Hmmmmmmmmmmmmmm#
Hmmmmmmmmmmmmmm#
Hmmmmmmmmmmmmmm#
Hmmmmmmmmmmmmmm#
Hhhdmmmmmmmmdhh#
Hbbhmmmmmmmmhbbd
HBBhmmmmmmmmhBBd
#dddddddddddddd#
################
"""

# Boiler Front Off
BOILER_FRONT_OFF_ART = """
#HHHHHHHHHHHHHH#
HBBhmmmmmmmmhBBd
Hhhdmmmmmmmmdhh#
Hmmd########dmm#
Hmm#11111111#mm#
Hmm#1dddddd1#mm#
Hmm#1dggggd1#mm#
Hmm#1dggggd1#mm#
Hmm#1dddddd1#mm#
Hmm#11111111#mm#
Hmmd########dmm#
Hhhdmmmmmmmmdhh#
Hbbhmmmmmmmmhbbd
HBBhmmmmmmmmhBBd
#dddddddddddddd#
################
"""

BOILER_FRONT_ON_ART = """
#HHHHHHHHHHHHHH#
HBBhmmmmmmmmhBBd
Hhhdmmmmmmmmdhh#
Hmmd########dmm#
Hmm#11111111#mm#
Hmm#1ffffff1#mm#
Hmm#1fFFFFf1#mm#
Hmm#1fFFFFf1#mm#
Hmm#1ffffff1#mm#
Hmm#11111111#mm#
Hmmd########dmm#
Hhhdmmmmmmmmdhh#
Hbbhmmmmmmmmhbbd
HBBhmmmmmmmmhBBd
#dddddddddddddd#
################
"""

BOILER_PALETTE = {
    **BRONZE_PALETTE,
    '1': (45, 45, 50),     # Dark iron hatch frame
    'g': (30, 30, 35),     # Cold dark grate interior
    'f': (240, 110, 20),   # Glowing orange fire
    'F': (255, 220, 60),   # Bright yellow flame core
}

# Exhaust Vent Top
EXHAUST_TOP_ART = """
#HHHHHHHHHHHHHH#
HBBhmmmmmmmmhBBd
Hhhd########dhh#
Hmm#11111111#mm#
Hmm#1g1g1g1g#mm#
Hmm#11g1g1g1#mm#
Hmm#1g1g1g1g#mm#
Hmm#11g1g1g1#mm#
Hmm#1g1g1g1g#mm#
Hmm#11g1g1g1#mm#
Hmm#11111111#mm#
Hhhd########dhh#
Hbbhmmmmmmmmhbbd
HBBhmmmmmmmmhBBd
#dddddddddddddd#
################
"""

EXHAUST_PALETTE = {
    **BRONZE_PALETTE,
    '1': (90, 95, 105),    # Steel vent slats
    'g': (25, 25, 30),     # Deep exhaust vent cavity
}

# Turbine Front
TURBINE_FRONT_OFF_ART = """
#HHHHHHHHHHHHHH#
HBBhmmmmmmmmhBBd
Hhhdmmmmmmmmdhh#
Hmmd########dmm#
Hmm#11111111#mm#
Hmm#1#11#111#mm#
Hmm#111##111#mm#
Hmm#11####11#mm#
Hmm#111##111#mm#
Hmm#111#11#1#mm#
Hmm#11111111#mm#
Hmmd########dmm#
Hhhdmmmmmmmmdhh#
Hbbhmmmmmmmmhbbd
#dddddddddddddd#
################
"""

TURBINE_FRONT_ON_ART = """
#HHHHHHHHHHHHHH#
HBBhmmmmmmmmhBBd
Hhhdmmmmmmmmdhh#
Hmmd########dmm#
Hmm#11111111#mm#
Hmm#1r11r111#mm#
Hmm#111rr111#mm#
Hmm#11rccr11#mm#
Hmm#111rr111#mm#
Hmm#111r11r1#mm#
Hmm#11111111#mm#
Hmmd########dmm#
Hhhdmmmmmmmmdhh#
Hbbhmmmmmmmmhbbd
#dddddddddddddd#
################
"""

TURBINE_PALETTE = {
    **BRONZE_PALETTE,
    '1': (50, 55, 65),     # Dark steel casing
    'r': (180, 200, 220),  # Blurred rotating steel blades
    'c': (100, 210, 255),  # Energy rotor glow
}

# Crusher Front
CRUSHER_FRONT_OFF_ART = """
#HHHHHHHHHHHHHH#
HBBhmmmmmmmmhBBd
Hhhdmmmmmmmmdhh#
Hmmd########dmm#
Hmm#11111111#mm#
Hmm#1tt11tt1#mm#
Hmm#1tt11tt1#mm#
Hmm#11111111#mm#
Hmm#11tt11tt#mm#
Hmm#11tt11tt#mm#
Hmm#11111111#mm#
Hmmd########dmm#
Hhhdmmmmmmmmdhh#
Hbbhmmmmmmmmhbbd
#dddddddddddddd#
################
"""

CRUSHER_FRONT_ON_ART = """
#HHHHHHHHHHHHHH#
HBBhmmmmmmmmhBBd
Hhhdmmmmmmmmdhh#
Hmmd########dmm#
Hmm#11111111#mm#
Hmm#111TT111#mm#
Hmm#11TTTT11#mm#
Hmm#1TTffTT1#mm#
Hmm#11TTTT11#mm#
Hmm#111TT111#mm#
Hmm#11111111#mm#
Hmmd########dmm#
Hhhdmmmmmmmmdhh#
Hbbhmmmmmmmmhbbd
#dddddddddddddd#
################
"""

CRUSHER_PALETTE = {
    **BRONZE_PALETTE,
    '1': (40, 42, 48),     # Chamber cavity
    't': (140, 145, 155),  # Steel grinding teeth
    'T': (200, 205, 215),  # Active grinding teeth
    'f': (255, 170, 40),   # Grinding sparks
}

# Alloy Smelter Front
ALLOY_FRONT_OFF_ART = """
#HHHHHHHHHHHHHH#
HBBhmmmmmmmmhBBd
Hhhdmmmmmmmmdhh#
Hmmd########dmm#
Hmm#11111111#mm#
Hmm#1cc11cc1#mm#
Hmm#1cc11cc1#mm#
Hmm#11111111#mm#
Hmm#111dd111#mm#
Hmm#11dddd11#mm#
Hmm#11111111#mm#
Hmmd########dmm#
Hhhdmmmmmmmmdhh#
Hbbhmmmmmmmmhbbd
#dddddddddddddd#
################
"""

ALLOY_FRONT_ON_ART = """
#HHHHHHHHHHHHHH#
HBBhmmmmmmmmhBBd
Hhhdmmmmmmmmdhh#
Hmmd########dmm#
Hmm#11111111#mm#
Hmm#1rr11rr1#mm#
Hmm#1rr11rr1#mm#
Hmm#11111111#mm#
Hmm#111gg111#mm#
Hmm#11gggg11#mm#
Hmm#11111111#mm#
Hmmd########dmm#
Hhhdmmmmmmmmdhh#
Hbbhmmmmmmmmhbbd
#dddddddddddddd#
################
"""

ALLOY_PALETTE = {
    **BRONZE_PALETTE,
    '1': (45, 40, 35),     # Crucible chamber
    'c': (110, 60, 30),    # Cold crucible inputs
    'd': (80, 50, 25),     # Cold mold
    'r': (240, 100, 30),   # Molten red/orange ore
    'g': (255, 210, 60),   # Glowing molten alloy pool
}

# Bronze Fluid Tank
TANK_SIDE_ART = """
#HHHHHHHHHHHHHH#
HBBhmmmmmmmmhBBd
Hhhd########dhh#
Hmm#11111111#mm#
Hmm#1wwggww1#mm#
Hmm#1wwggww1#mm#
Hmm#1wwggww1#mm#
Hmm#1wwggww1#mm#
Hmm#1wwggww1#mm#
Hmm#1wwggww1#mm#
Hmm#11111111#mm#
Hhhd########dhh#
Hbbhmmmmmmmmhbbd
HBBhmmmmmmmmhBBd
#dddddddddddddd#
################
"""

TANK_PALETTE = {
    **BRONZE_PALETTE,
    '1': (65, 40, 20),     # Sight glass bronze bezel
    'w': (180, 215, 235),  # Window glass sheen
    'g': (120, 180, 210),  # Transparent fluid glass
}

# Bronze Pipe
PIPE_ART = """
................
................
....########....
....#HHHHHH#....
....#HbbbbH#....
....#HBBBBH#....
....#HhhdhH#....
....#HmmmmH#....
....#HmmmmH#....
....#HhhdhH#....
....#HBBBBH#....
....#HbbbbH#....
....#HHHHHH#....
....########....
................
................
"""

# Item: Steam Piston
STEAM_PISTON_ART = """
................
.......HHHH.....
.......HbbH.....
.....HHHmmH.....
.....HddddH.....
......#hh#......
......#mm#......
......#mm#......
......#mm#......
.....HHmmHH.....
....HbbhhbbH....
...HBBhhhhBBH...
...HbbmmmmbbH...
....HddddddH....
.....######.....
................
"""

# Item: Pressure Gauge
PRESSURE_GAUGE_ART = """
................
......####......
....##hhhh##....
...#hhHHHHhh#...
..#hhH1111Hhh#..
..#hH11..11Hh#..
.#hH11...r11Hh#.
.#hH1...r.11Hh#.
.#hH1..rr.11Hh#.
.#hH11...111Hh#.
..#hH111111Hh#..
..#hhH1111Hhh#..
...#hhddddhh#...
....##dddd##....
......####......
................
"""

PRESSURE_GAUGE_PALETTE = {
    '#': (70, 45, 20),
    'h': (230, 165, 95),
    'H': (255, 210, 140),
    'd': (130, 80, 35),
    '1': (240, 240, 235),  # Gauge white face
    'r': (210, 30, 30),    # Red gauge needle
    '.': None
}

# Item: Bronze Valve
BRONZE_VALVE_ART = """
................
....########....
...#HHHHHHHH#...
..#HbbbbbbbbH#..
..#HBB#dd#BBH#..
...#H#dmmd#H#...
....##dmmd##....
......#mm#......
......#mm#......
....##dmmd##....
...#H#dmmd#H#...
..#HBB#dd#BBH#..
..#HbbbbbbbbH#..
...#HHHHHHHH#...
....########....
................
"""

# Fluid: Steam
STEAM_FLUID_ART = """
2222112222221122
2211111222111112
2111111121111111
1111111111111111
1111111111111111
1111111111111111
2111111121111111
2211111222111112
2222112222221122
2222222222222222
2222112222221122
2211111222111112
2111111121111111
1111111111111111
2111111121111111
2222222222222222
"""

STEAM_FLUID_PALETTE = {
    '1': (240, 248, 255, 180), # Billowing white steam
    '2': (215, 235, 245, 140), # Soft translucent cyan/white edge
    '.': None
}

def generate():
    print("Generating Steam Era Textures...")
    
    # 1. Bronze Casing & Common Textures
    casing_img = render_ascii_texture(CASING_ART, BRONZE_PALETTE)
    casing_img.save(os.path.join(BLOCKS_DIR, "bronze_casing.png"))
    
    exhaust_img = render_ascii_texture(EXHAUST_TOP_ART, EXHAUST_PALETTE)
    
    # 2. Solid Fuel Boiler
    render_ascii_texture(BOILER_FRONT_OFF_ART, BOILER_PALETTE).save(os.path.join(BLOCKS_DIR, "solid_fuel_boiler_front.png"))
    render_ascii_texture(BOILER_FRONT_ON_ART, BOILER_PALETTE).save(os.path.join(BLOCKS_DIR, "solid_fuel_boiler_front_on.png"))
    casing_img.save(os.path.join(BLOCKS_DIR, "solid_fuel_boiler_side.png"))
    casing_img.save(os.path.join(BLOCKS_DIR, "solid_fuel_boiler_top.png"))
    casing_img.save(os.path.join(BLOCKS_DIR, "solid_fuel_boiler_bottom.png"))

    # 3. Steam Turbine
    render_ascii_texture(TURBINE_FRONT_OFF_ART, TURBINE_PALETTE).save(os.path.join(BLOCKS_DIR, "steam_turbine_front.png"))
    render_ascii_texture(TURBINE_FRONT_ON_ART, TURBINE_PALETTE).save(os.path.join(BLOCKS_DIR, "steam_turbine_front_on.png"))
    casing_img.save(os.path.join(BLOCKS_DIR, "steam_turbine_side.png"))
    casing_img.save(os.path.join(BLOCKS_DIR, "steam_turbine_top.png"))
    casing_img.save(os.path.join(BLOCKS_DIR, "steam_turbine_bottom.png"))

    # 4. Steam Crusher
    render_ascii_texture(CRUSHER_FRONT_OFF_ART, CRUSHER_PALETTE).save(os.path.join(BLOCKS_DIR, "steam_crusher_front.png"))
    render_ascii_texture(CRUSHER_FRONT_ON_ART, CRUSHER_PALETTE).save(os.path.join(BLOCKS_DIR, "steam_crusher_front_on.png"))
    casing_img.save(os.path.join(BLOCKS_DIR, "steam_crusher_side.png"))
    exhaust_img.save(os.path.join(BLOCKS_DIR, "steam_crusher_top.png"))
    casing_img.save(os.path.join(BLOCKS_DIR, "steam_crusher_bottom.png"))

    # 5. Steam Alloy Smelter
    render_ascii_texture(ALLOY_FRONT_OFF_ART, ALLOY_PALETTE).save(os.path.join(BLOCKS_DIR, "steam_alloy_smelter_front.png"))
    render_ascii_texture(ALLOY_FRONT_ON_ART, ALLOY_PALETTE).save(os.path.join(BLOCKS_DIR, "steam_alloy_smelter_front_on.png"))
    casing_img.save(os.path.join(BLOCKS_DIR, "steam_alloy_smelter_side.png"))
    exhaust_img.save(os.path.join(BLOCKS_DIR, "steam_alloy_smelter_top.png"))
    casing_img.save(os.path.join(BLOCKS_DIR, "steam_alloy_smelter_bottom.png"))

    # 6. Bronze Fluid Tank & Pipe
    render_ascii_texture(TANK_SIDE_ART, TANK_PALETTE).save(os.path.join(BLOCKS_DIR, "bronze_fluid_tank.png"))
    casing_img.save(os.path.join(BLOCKS_DIR, "bronze_fluid_tank_top.png"))
    render_ascii_texture(PIPE_ART, BRONZE_PALETTE).save(os.path.join(BLOCKS_DIR, "bronze_fluid_pipe.png"))

    # 7. Items
    render_ascii_texture(STEAM_PISTON_ART, BRONZE_PALETTE).save(os.path.join(ITEMS_DIR, "steam_piston.png"))
    render_ascii_texture(PRESSURE_GAUGE_ART, PRESSURE_GAUGE_PALETTE).save(os.path.join(ITEMS_DIR, "pressure_gauge.png"))
    render_ascii_texture(BRONZE_VALVE_ART, BRONZE_PALETTE).save(os.path.join(ITEMS_DIR, "bronze_valve.png"))
    render_ascii_texture(PRESSURE_GAUGE_ART, PRESSURE_GAUGE_PALETTE).save(os.path.join(ITEMS_DIR, "steam_bucket.png"))

    # 8. Fluids
    steam_img = render_ascii_texture(STEAM_FLUID_ART, STEAM_FLUID_PALETTE)
    steam_img.save(os.path.join(FLUIDS_DIR, "steam_still.png"))
    steam_img.save(os.path.join(FLUIDS_DIR, "steam_flow.png"))

    print("[SUCCESS] All Steam Era textures generated successfully in assets/modernmachines/textures/!")

if __name__ == "__main__":
    generate()
