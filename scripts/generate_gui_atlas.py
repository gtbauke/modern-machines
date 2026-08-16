import os
from PIL import Image, ImageDraw

output_dir = "src/main/resources/assets/modernmachines/textures/gui"
atlas = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
draw = ImageDraw.Draw(atlas)

def render_ascii_sprite(ox, oy, ascii_art, palette):
    lines = [l for l in ascii_art.strip().splitlines()]
    for y, line in enumerate(lines):
        for x, ch in enumerate(line):
            if ch in palette:
                color = palette[ch]
                if len(color) == 3:
                    color = color + (255,)
                atlas.putpixel((ox + x, oy + y), color)

# -------------------------------------------------------------
# 1. WINDOW FRAME 9-SLICE (0, 0, 24x24)
# -------------------------------------------------------------
WINDOW_PALETTE = {
    '#': (24, 26, 30),    # Outer dark rim
    'B': (62, 68, 78),    # Bevel highlight
    'b': (40, 44, 50),    # Bevel shadow
    '.': (32, 35, 40),    # Window background
}
WINDOW_ART = """
########################
#BBBBBBBBBBBBBBBBBBBBBB#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#B....................b#
#bbbbbbbbbbbbbbbbbbbbbb#
########################
"""
render_ascii_sprite(0, 0, WINDOW_ART, WINDOW_PALETTE)

# -------------------------------------------------------------
# 2. HEADER BAR (32, 0, 24x20)
# -------------------------------------------------------------
HEADER_PALETTE = {
    '#': (24, 26, 30),
    'H': (55, 60, 70),
    'h': (42, 46, 54),
    '.': (28, 30, 35),
}
HEADER_ART = """
########################
#HHHHHHHHHHHHHHHHHHHHHH#
#H....................h#
#H....................h#
#H....................h#
#H....................h#
#H....................h#
#H....................h#
#H....................h#
#H....................h#
#H....................h#
#H....................h#
#H....................h#
#H....................h#
#H....................h#
#H....................h#
#H....................h#
#H....................h#
#hhhhhhhhhhhhhhhhhhhhhh#
########################
"""
render_ascii_sprite(32, 0, HEADER_ART, HEADER_PALETTE)

# -------------------------------------------------------------
# 3. INSET SLOT WELL (64, 0, 18x18) & HOVER (84, 0, 18x18)
# -------------------------------------------------------------
SLOT_PALETTE = {
    '#': (20, 22, 25),    # Slot shadow outer
    'S': (26, 28, 32),    # Slot shadow inner
    '.': (18, 19, 22),    # Slot well cavity
    'b': (52, 56, 64),    # Slot highlight bottom/right
    'B': (72, 78, 90),    # Slot bright corner
}
SLOT_ART = """
##################
#SSSSSSSSSSSSSSSSb
#S..............#b
#S..............#b
#S..............#b
#S..............#b
#S..............#b
#S..............#b
#S..............#b
#S..............#b
#S..............#b
#S..............#b
#S..............#b
#S..............#b
#S..............#b
#S..............#b
#S###############b
#bbbbbbbbbbbbbbbbB
"""
render_ascii_sprite(64, 0, SLOT_ART, SLOT_PALETTE)

# Active/Hovered slot highlight (84, 0, 18x18)
for y in range(18):
    for x in range(18):
        if x == 0 or x == 17 or y == 0 or y == 17:
            atlas.putpixel((84 + x, y), (74, 226, 82, 220))
        elif x == 1 or x == 16 or y == 1 or y == 16:
            atlas.putpixel((84 + x, y), (74, 226, 82, 100))
        else:
            atlas.putpixel((84 + x, y), (74, 226, 82, 35))

# -------------------------------------------------------------
# 4. BUTTON 9-SLICES (0..72, 32, 24x20)
# -------------------------------------------------------------
BTN_PALETTE = {
    '#': (20, 22, 25),
    'H': (90, 96, 110),
    'h': (70, 75, 86),
    'm': (52, 56, 64),
    'd': (38, 41, 48),
    'D': (28, 30, 35),
}
BTN_NORMAL_ART = """
########################
#HHHHHHHHHHHHHHHHHHHHHH#
#HhhhhhhhhhhhhhhhhhhhhD#
#HhmmmmmmmmmmmmmmmmmmdD#
#HhmmmmmmmmmmmmmmmmmmdD#
#HhmmmmmmmmmmmmmmmmmmdD#
#HhmmmmmmmmmmmmmmmmmmdD#
#HhmmmmmmmmmmmmmmmmmmdD#
#HhmmmmmmmmmmmmmmmmmmdD#
#HhmmmmmmmmmmmmmmmmmmdD#
#HhmmmmmmmmmmmmmmmmmmdD#
#HhmmmmmmmmmmmmmmmmmmdD#
#HhmmmmmmmmmmmmmmmmmmdD#
#HhmmmmmmmmmmmmmmmmmmdD#
#HhmmmmmmmmmmmmmmmmmmdD#
#HhmmmmmmmmmmmmmmmmmmdD#
#HhmmmmmmmmmmmmmmmmmmdD#
#HddddddddddddddddddddD#
#DDDDDDDDDDDDDDDDDDDDDD#
########################
"""

BTN_HOVER_PALETTE = {
    '#': (25, 28, 32),
    'H': (120, 130, 150),
    'h': (95, 105, 120),
    'm': (72, 78, 90),
    'd': (50, 55, 65),
    'D': (35, 38, 45),
}

BTN_PRESSED_PALETTE = {
    '#': (18, 20, 22),
    'H': (35, 38, 45),
    'h': (45, 50, 58),
    'm': (40, 44, 52),
    'd': (65, 72, 85),
    'D': (85, 95, 110),
}

render_ascii_sprite(0, 32, BTN_NORMAL_ART, BTN_PALETTE)
render_ascii_sprite(24, 32, BTN_NORMAL_ART, BTN_HOVER_PALETTE)
render_ascii_sprite(48, 32, BTN_NORMAL_ART, BTN_PRESSED_PALETTE)

# -------------------------------------------------------------
# 5. SIDE TABS (0, 64: Left 28x26, 28, 64: Right 28x26)
# -------------------------------------------------------------
TAB_LEFT_PALETTE = {
    '#': (24, 26, 30),
    'B': (65, 72, 84),
    '.': (36, 40, 46),
    'b': (28, 30, 35),
}
TAB_LEFT_ART = """
############################
#BBBBBBBBBBBBBBBBBBBBBBBBBB#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#B.........................#
#bbbbbbbbbbbbbbbbbbbbbbbbbb#
############################
"""
render_ascii_sprite(0, 64, TAB_LEFT_ART, TAB_LEFT_PALETTE)

TAB_RIGHT_PALETTE = {
    '#': (24, 26, 30),
    'B': (65, 72, 84),
    '.': (36, 40, 46),
    'b': (28, 30, 35),
}
TAB_RIGHT_ART = """
############################
#BBBBBBBBBBBBBBBBBBBBBBBBBB#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#.........................b#
#bbbbbbbbbbbbbbbbbbbbbbbbbb#
############################
"""
render_ascii_sprite(28, 64, TAB_RIGHT_ART, TAB_RIGHT_PALETTE)

# -------------------------------------------------------------
# 6. PROGRESS ARROW (0, 128: Empty 22x15, 24, 128: Full 22x15)
# -------------------------------------------------------------
bedrock_empty_arrow = r"C:\Users\gusta\dev\bedrock-samples-main\bedrock-samples-main\resource_pack\textures\ui\arrow_inactive.png"
bedrock_full_arrow = r"C:\Users\gusta\dev\bedrock-samples-main\bedrock-samples-main\resource_pack\textures\ui\arrow_active.png"
sprites_dir = os.path.join(output_dir, "sprites")
os.makedirs(sprites_dir, exist_ok=True)

if os.path.exists(bedrock_empty_arrow) and os.path.exists(bedrock_full_arrow):
    empty_arrow_img = Image.open(bedrock_empty_arrow).convert("RGBA")
    full_arrow_img = Image.open(bedrock_full_arrow).convert("RGBA")
    atlas.paste(empty_arrow_img, (0, 128), empty_arrow_img)
    atlas.paste(full_arrow_img, (24, 128), full_arrow_img)
    
    empty_arrow_img.save(os.path.join(sprites_dir, "arrow_inactive.png"))
    full_arrow_img.save(os.path.join(sprites_dir, "arrow_active.png"))
else:
    ARROW_EMPTY_PALETTE = {
        '#': (22, 24, 28),
        'S': (32, 35, 40),
        's': (42, 46, 54),
    }
    ARROW_EMPTY_ART = """
......................
......................
................#.....
...............#s#....
..............#ss#....
############# #sss#...
#SSSSSSSSSSSS##ssss#..
#SSSSSSSSSSSS#sssss#..
#SSSSSSSSSSSS##ssss#..
############# #sss#...
..............#ss#....
...............#s#....
................#.....
......................
......................
"""
    render_ascii_sprite(0, 128, ARROW_EMPTY_ART, ARROW_EMPTY_PALETTE)

    ARROW_FULL_PALETTE = {
        '#': (18, 55, 20),
        'S': (74, 226, 82),
        's': (130, 245, 140),
    }
    ARROW_FULL_ART = """
......................
......................
................#.....
...............#s#....
..............#ss#....
############# #sss#...
#SSSSSSSSSSSS##ssss#..
#SSSSSSSSSSSS#sssss#..
#SSSSSSSSSSSS##ssss#..
############# #sss#...
..............#ss#....
...............#s#....
................#.....
......................
......................
"""
    render_ascii_sprite(24, 128, ARROW_FULL_ART, ARROW_FULL_PALETTE)

# -------------------------------------------------------------
# 7. FLAME PROGRESS (48, 128: Empty 13x13, 62, 128: Full 13x13)
# -------------------------------------------------------------
bedrock_empty_flame = r"C:\Users\gusta\dev\bedrock-samples-main\bedrock-samples-main\resource_pack\textures\ui\flame_empty_image.png"
bedrock_full_flame = r"C:\Users\gusta\dev\bedrock-samples-main\bedrock-samples-main\resource_pack\textures\ui\flame_full_image.png"

if os.path.exists(bedrock_empty_flame) and os.path.exists(bedrock_full_flame):
    empty_flame_img = Image.open(bedrock_empty_flame).convert("RGBA")
    full_flame_img = Image.open(bedrock_full_flame).convert("RGBA")
    atlas.paste(empty_flame_img, (48, 128), empty_flame_img)
    atlas.paste(full_flame_img, (62, 128), full_flame_img)
    
    empty_flame_img.save(os.path.join(sprites_dir, "flame_empty.png"))
    full_flame_img.save(os.path.join(sprites_dir, "flame_full.png"))

# -------------------------------------------------------------
# 7b. LINEAR PROGRESS BAR (76, 128: Empty 13x5, 90, 128: Full 13x5)
# -------------------------------------------------------------
bedrock_empty_prog = r"C:\Users\gusta\dev\bedrock-samples-main\bedrock-samples-main\resource_pack\textures\ui\empty_progress_bar.png"
bedrock_full_prog = r"C:\Users\gusta\dev\bedrock-samples-main\bedrock-samples-main\resource_pack\textures\ui\filled_progress_bar.png"

if os.path.exists(bedrock_empty_prog) and os.path.exists(bedrock_full_prog):
    empty_prog_img = Image.open(bedrock_empty_prog).convert("RGBA")
    full_prog_img = Image.open(bedrock_full_prog).convert("RGBA")
    atlas.paste(empty_prog_img, (76, 128), empty_prog_img)
    atlas.paste(full_prog_img, (90, 128), full_prog_img)
    
    empty_prog_img.save(os.path.join(sprites_dir, "empty_progress_bar.png"))
    full_prog_img.save(os.path.join(sprites_dir, "filled_progress_bar.png"))
else:
    FLAME_EMPTY_PALETTE = {
        '#': (22, 24, 28),
        '.': (35, 38, 44),
    }
    FLAME_EMPTY_ART = """
.....##......
....####.....
...######....
...######....
..########...
..########...
.##########..
.##########..
.##########..
.##########..
.##########..
..########...
...######....
"""
    render_ascii_sprite(48, 128, FLAME_EMPTY_ART, FLAME_EMPTY_PALETTE)

    FLAME_FULL_PALETTE = {
        '#': (160, 40, 10),
        'O': (245, 110, 20),
        'Y': (255, 220, 80),
        'W': (255, 255, 230),
    }
    FLAME_FULL_ART = """
.....##......
....#OO#.....
...#OOOO#....
...#OYYO#....
..#OYYYYO#...
..#OYYYYO#...
.#OYYWWYYO#..
.#OYYWWYYO#..
.#OYYYYYYO#..
.#OYYYYYYO#..
.#OOOOOOOO#..
..#OOOOOO#...
...#OOOO#....
"""
    render_ascii_sprite(62, 128, FLAME_FULL_ART, FLAME_FULL_PALETTE)

# -------------------------------------------------------------
# 8. ENERGY GAUGE (0, 160: Frame 14x52, 14, 160: Fill 12x50)
# -------------------------------------------------------------
for y in range(52):
    for x in range(14):
        if x == 0 or x == 13 or y == 0 or y == 51:
            color = (22, 24, 28, 255)
        elif x == 1 or y == 1:
            color = (18, 20, 22, 255)
        elif x == 12 or y == 50:
            color = (60, 66, 76, 255)
        else:
            color = (14, 15, 18, 255)
        atlas.putpixel((x, 160 + y), color)

for y in range(50):
    for x in range(12):
        ratio = y / 50.0
        r = int(245 * (1 - ratio) + 190 * ratio)
        g = int(60 * (1 - ratio) + 20 * ratio)
        b = int(30 * (1 - ratio) + 10 * ratio)
        atlas.putpixel((14 + x, 160 + y), (r, g, b, 255))

# -------------------------------------------------------------
# 9. ICONS (y = 96, 16x16 each)
# -------------------------------------------------------------
# Energy Bolt (0, 96)
BOLT_PALETTE = {'#': (180, 130, 0), 'Y': (255, 220, 40), 'W': (255, 255, 200)}
BOLT_ART = """
......###.......
.....#YY#.......
....#YYY#.......
...#YYWW#.......
..#YYYYY#####...
.#############..
....#YYYYYY#....
...#YYYYYY#.....
..#YYWW##Y#.....
.#############..
....#YYYYY#.....
...#YYYY#.......
..#YYY#.........
.#YY#...........
.##.............
................
"""
render_ascii_sprite(0, 96, BOLT_ART, BOLT_PALETTE)

# Fluid Drop (16, 96)
DROP_PALETTE = {'#': (20, 80, 160), 'B': (56, 189, 248), 'W': (200, 240, 255)}
DROP_ART = """
.......#........
......#B#.......
.....#BBB#......
....#BBWW#......
...#BBBWBB#.....
...#BBBWBB#.....
..#BBBBBBBB#....
..#BBBBBBBB#....
..#BBBBBBBB#....
..#BBBBBBBB#....
...#BBBBBB#.....
....#BBBB#......
.....####.......
................
................
................
"""
render_ascii_sprite(16, 96, DROP_ART, DROP_PALETTE)

# Gear Config (32, 96)
GEAR_PALETTE = {'#': (40, 44, 52), 'G': (180, 188, 200), 'W': (230, 235, 245)}
GEAR_ART = """
......####......
.....#GGGG#.....
..##.#GGGG#.##..
.#WW##GGGG##WW#.
.#GGGGGGGGGGGG#.
.##GGGG##GGGG##.
..#GGG####GGG#..
..#GG######GG#..
..#GGG####GGG#..
.##GGGG##GGGG##.
.#GGGGGGGGGGGG#.
.#WW##GGGG##WW#.
..##.#GGGG#.##..
.....#GGGG#.....
......####......
................
"""
render_ascii_sprite(32, 96, GEAR_ART, GEAR_PALETTE)

# Redstone Torch (48, 96)
TORCH_PALETTE = {'#': (50, 20, 20), 'R': (235, 45, 45), 'Y': (255, 180, 50), 'W': (130, 85, 40), 'w': (90, 60, 30)}
TORCH_ART = """
......####......
.....#RRRR#.....
.....#RYYR#.....
.....#RRRR#.....
......#RR#......
......#WW#......
......#Ww#......
......#Ww#......
......#Ww#......
......#Ww#......
......#Ww#......
......#Ww#......
.......##.......
................
................
................
"""
render_ascii_sprite(48, 96, TORCH_ART, TORCH_PALETTE)

# Info Stats (64, 96)
INFO_PALETTE = {'#': (30, 60, 110), 'B': (70, 150, 245), 'W': (210, 230, 255)}
INFO_ART = """
................
.....######.....
....#BBWWBB#....
....#BBWWBB#....
.....######.....
................
......####......
.....#BBBB#.....
.....#BBBB#.....
.....#BBBB#.....
.....#BBBB#.....
.....#BBBB#.....
....#BBBBBB#....
....#BBBBBB#....
.....######.....
................
"""
render_ascii_sprite(64, 96, INFO_ART, INFO_PALETTE)

# Close X (80, 96)
X_PALETTE = {'#': (70, 20, 20), 'R': (235, 70, 70), 'W': (255, 180, 180)}
X_ART = """
.##..........##.
.#RR#......#RR#.
..#RR#....#RR#..
...#RR#..#RR#...
....#RR##RR#....
.....#RRRR#.....
......#RR#......
.....#RRRR#.....
....#RR##RR#....
...#RR#..#RR#...
..#RR#....#RR#..
.#RR#......#RR#.
.##..........##.
................
................
................
"""
render_ascii_sprite(80, 96, X_ART, X_PALETTE)

# Popout Pin (96, 96)
PIN_PALETTE = {'#': (40, 44, 52), 'G': (180, 188, 200), 'W': (240, 245, 255)}
PIN_ART = """
......######....
.....#WWWWWW#...
....#WWWWWWWW#..
....#WWGGGGWW#..
....#WWGGGGWW#..
.....#GGGGGG#...
......##GG##....
........##......
........##......
........##......
........##......
.........#......
................
................
................
................
"""
render_ascii_sprite(96, 96, PIN_ART, PIN_PALETTE)

# Upgrade Chip (112, 96)
CHIP_PALETTE = {'#': (20, 50, 40), 'G': (52, 211, 153), 'Y': (250, 204, 21), 'W': (209, 250, 229)}
CHIP_ART = """
..#...#...#...#.
.###############
.#GGGGGGGGGGGGG#
.#GG#GGGGGGG#GG#
.#GG#GGYYYGG#GG#
.#GGGGYYYYYGGGG#
.#GGGGYYYYYGGGG#
.#GG#GGYYYGG#GG#
.#GG#GGGGGGG#GG#
.#GGGGGGGGGGGGG#
.###############
..#...#...#...#.
................
................
................
................
"""
render_ascii_sprite(112, 96, CHIP_ART, CHIP_PALETTE)

output_dir = "src/main/resources/assets/modernmachines/textures/gui"
os.makedirs(output_dir, exist_ok=True)
atlas.save(f"{output_dir}/gui_sprites.png")
print("Pixel-perfect master GUI atlas generated successfully!")
