import os
from PIL import Image

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

TABLET_PALETTE = {
    '#': (30, 32, 40),      # Dark chassis bezel
    'd': (50, 54, 68),      # Mid chassis
    'h': (90, 98, 120),     # Highlight bezel
    'H': (140, 150, 180),   # Specular corner
    'c': (0, 200, 240),     # Cyan glowing screen
    'C': (100, 240, 255),   # Bright screen glow / spark
    'g': (0, 120, 160),     # Dark screen grid
    's': (20, 25, 35),      # Screen background
    'G': (0, 230, 120),     # Green power LED
    '.': None
}

TABLET_ART = """
..############..
.#Hhhhhhhhhhh##.
#Hdddddddddddd##
#ddssssssssssdd#
#ddscCscscCgsdd#
#ddssscscCcssdd#
#ddsCgssgssgsdd#
#ddsgsssgssgsdd#
#ddssssssssssdd#
#ddssssssssssdd#
#ddssGsssssssdd#
#dddddddddddd##.
.#Hhhhhhhhhhh##.
..############..
................
................
"""

TERMINAL_TOP_PALETTE = {
    '#': (35, 38, 48),      # Dark border
    'd': (65, 70, 85),      # Steel top plate
    'h': (110, 118, 140),   # Bevel highlight
    'H': (160, 170, 195),   # Bright rivet
    'c': (0, 210, 255),     # Screen glowing cyan
    'C': (120, 245, 255),   # Bright scanline
    's': (15, 20, 30),      # Screen dark glass
    'G': (0, 230, 120),     # Green LED
    'R': (240, 60, 60),     # Red alert LED
    'k': (45, 48, 60),      # Keyboard keys
    'K': (75, 80, 100),     # Keyboard highlight
    '.': None
}

TERMINAL_TOP_ART = """
################
#HddddddddddddH#
#dssssssssssssd#
#dsCccccCccsssd#
#dsscscscsssssd#
#dsccsssssssssd#
#dssssssssssssd#
#dsGRsssssssssd#
#dddddddddddddd#
#dKKKKKKKKKKKKd#
#dkkkkkkkkkkkkd#
#dKKKKKKKKKKKKd#
#dkkkkkkkkkkkkd#
#dddddddddddddd#
#HddddddddddddH#
################
"""

TERMINAL_SIDE_PALETTE = {
    '#': (30, 32, 40),      # Border
    'd': (60, 65, 80),      # Metal casing
    'h': (100, 108, 130),   # Bevel highlight
    'H': (150, 160, 185),   # Rivet
    'v': (20, 22, 28),      # Dark vent grill
    'V': (40, 44, 55),      # Vent shadow
    'l': (0, 180, 220),     # LED status strip
    '.': None
}

TERMINAL_SIDE_ART = """
################
#HhhhhhhhhhhhhH#
#dddddddddddddd#
#ddvvvvvvvvvvdd#
#ddVVVVVVVVVVdd#
#ddvvvvvvvvvvdd#
#ddVVVVVVVVVVdd#
#dddddddddddddd#
#ddllllllllllld#
#dddddddddddddd#
#ddvvvvvvvvvvdd#
#ddVVVVVVVVVVdd#
#ddvvvvvvvvvvdd#
#dddddddddddddd#
#HddddddddddddH#
################
"""

TERMINAL_BOTTOM_PALETTE = {
    '#': (25, 28, 35),
    'd': (50, 55, 68),
    'h': (80, 88, 105),
    'H': (120, 130, 150),
    'r': (15, 18, 22),     # Rubber feet
    '.': None
}

TERMINAL_BOTTOM_ART = """
################
#HrrrrddddrrrrH#
#rrrrrddddrrrrr#
#rrrrrddddrrrrr#
#dddddddddddddd#
#dddddddddddddd#
#dddddddddddddd#
#dddddddddddddd#
#dddddddddddddd#
#dddddddddddddd#
#dddddddddddddd#
#dddddddddddddd#
#rrrrrddddrrrrr#
#rrrrrddddrrrrr#
#HrrrrddddrrrrH#
################
"""

item_dir = "src/main/resources/assets/modernmachines/textures/item"
block_dir = "src/main/resources/assets/modernmachines/textures/block"

apply_palette_map(TABLET_ART, TABLET_PALETTE).save(f"{item_dir}/engineers_tablet.png")
apply_palette_map(TERMINAL_TOP_ART, TERMINAL_TOP_PALETTE).save(f"{block_dir}/engineers_terminal_top.png")
apply_palette_map(TERMINAL_SIDE_ART, TERMINAL_SIDE_PALETTE).save(f"{block_dir}/engineers_terminal_side.png")
apply_palette_map(TERMINAL_BOTTOM_ART, TERMINAL_BOTTOM_PALETTE).save(f"{block_dir}/engineers_terminal_bottom.png")

print("Engineer's Tablet & Terminal textures generated successfully!")
