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

GEAR_PALETTE = {
    '#': (45, 45, 45),      # Dark outline/depth
    'd': (85, 85, 85),      # Deep shadow
    'm': (150, 150, 150),   # Midtone
    'h': (215, 215, 215),   # Highlight
    'H': (255, 255, 255),   # Specular white
    '.': None
}

GEAR_ART = """
......##..##....
....#Hhh##hh#...
..#Hh#......#h#.
.#H#..#hhhh#..#d
.#h.#Hh####h#.#d
#H#.Hh#....#m#.#
#h.#h#......#m#.
##.hh#......#md#
##.hh#......#md#
#d.#m#......#dd#
#d#.mm#....#d#.#
.#d.#mm####d#.#d
.#d#..#dddd#..#d
..#d#......#d#..
....#dd##dd#....
......##..##....
"""

SCREW_PALETTE = {
    '#': (40, 40, 40),      # Dark outline & deep thread groove
    's': (20, 20, 20),      # Screwdriver slot
    'd': (80, 80, 80),      # Shadow face
    'm': (155, 155, 155),   # Metal midtone
    'h': (220, 220, 220),   # Thread ridge / head highlight
    'H': (255, 255, 255),   # Specular shine
    '.': None
}

SCREW_ART = """
.####...........
#HhhH#..........
#hs.sh#.........
#HhhH#d#........
.###Hhm#........
...##d#hm#......
....##d#hm#.....
.....##d#hm#....
......##d#hm#...
.......##d#hm#..
........##d#hm#.
.........##d#hm#
..........##d#h#
...........##d##
............##..
................
"""

template_dir = "src/main/resources/assets/modernmachines/textures/item/template"

gear_img = apply_palette_map(GEAR_ART, GEAR_PALETTE)
screw_img = apply_palette_map(SCREW_ART, SCREW_PALETTE)

gear_img.save(f"{template_dir}/gear.png")
screw_img.save(f"{template_dir}/screw.png")

print("New Clockwork Gear and Diagonal Slotted Screw templates generated successfully!")
