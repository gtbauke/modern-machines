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
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 255))
    for y in range(16):
        line = lines[y] if y < len(lines) else ""
        for x in range(16):
            char = line[x] if x < len(line) else "."
            if char in palette:
                color = palette[char]
                if len(color) == 3:
                    color = color + (255,)
                img.putpixel((x, y), color)
    return img

blocks_dir = "src/main/resources/assets/modernmachines/textures/block"
os.makedirs(blocks_dir, exist_ok=True)

# -------------------------------------------------------------
# 1. PART BUILDER TEXTURES
# -------------------------------------------------------------
PART_BUILDER_PALETTE = {
    '#': (45, 30, 18),    # Dark wood border
    '=': (65, 45, 28),    # Corner brackets / dark grain
    'W': (155, 115, 75),  # Oak wood plank
    'w': (135, 95, 60),   # Medium wood
    'L': (180, 135, 90),  # Light wood highlight
    'd': (110, 80, 50),   # Dark wood knot / groove
    'I': (170, 175, 185), # Iron stencil bracket
    'i': (120, 125, 135), # Iron shadow
    'P': (210, 165, 110), # Blank pattern paper/stencil on table
    'p': (185, 140, 90),  # Stencil shadow
    'G': (74, 226, 82),   # Carving guide line
}

PART_BUILDER_TOP_ART = """
#==============#
=LLLLLLLLLLLLLL=
=LWWWWWWWWWWWWL=
=LWWWWWWWWWWWWL=
=LWWwwPPPPPwwWL=
=LWWwwPPPPPwwWL=
=LWWwwPPdPPwwWL=
=LWIddPPPPPddWL=
=LWIIddppddddWL=
=LWWIIIIdddddWL=
=LWWWWdIIddddWL=
=LWWWWdddddddWL=
=LWWWWWWWWWWWWL=
=LwwwwwwwwwwwwL=
=LLLLLLLLLLLLLL=
#==============#
"""

PART_BUILDER_SIDE_ART = """
#==============#
=LLLLLLLLLLLLLL=
=LWWWWWWWWWWWWL=
=LWWWWWWWWWWWWL=
=L##dddddddd##L=
=L#W========W#L=
=L#WLWWWWWWLW#L=
=L#WLIiIIiILW#L=
=L#WLWWWWWWLW#L=
=L#W========W#L=
=L##dddddddd##L=
=LWWWWWWWWWWWWL=
=LWWWWWWWWWWWWL=
=LwwwwwwwwwwwwL=
=LLLLLLLLLLLLLL=
#==============#
"""

PART_BUILDER_BOTTOM_ART = """
################
#dddddddddddddd#
#dwwwwwwwwwwwwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwwwwwwwwwwwwd#
#dddddddddddddd#
################
"""

render_ascii_texture(PART_BUILDER_TOP_ART, PART_BUILDER_PALETTE).save(f"{blocks_dir}/part_builder_top.png")
render_ascii_texture(PART_BUILDER_SIDE_ART, PART_BUILDER_PALETTE).save(f"{blocks_dir}/part_builder_side.png")
render_ascii_texture(PART_BUILDER_BOTTOM_ART, PART_BUILDER_PALETTE).save(f"{blocks_dir}/part_builder_bottom.png")
render_ascii_texture(PART_BUILDER_TOP_ART, PART_BUILDER_PALETTE).save(f"{blocks_dir}/part_builder.png")

# -------------------------------------------------------------
# 2. TINKERING TABLE TEXTURES
# -------------------------------------------------------------
TINKERING_PALETTE = {
    '#': (35, 25, 20),    # Iron/wood dark trim
    '=': (50, 40, 30),    # Reinforced iron border
    'W': (140, 100, 65),  # Oak plank base
    'w': (120, 85, 50),   # Shadow wood
    'L': (165, 120, 80),  # Highlight wood
    'd': (95, 65, 40),    # Deep groove
    'A': (190, 195, 205), # Anvil plate highlight
    'a': (145, 150, 160), # Anvil plate surface
    'm': (100, 105, 115), # Anvil plate shadow
    'B': (180, 130, 45),  # Brass ruler / gauge
    'b': (140, 95, 30),   # Brass shadow
    'H': (80, 85, 95),    # Hammer head metal
    'h': (130, 90, 50),   # Hammer handle
}

TINKERING_TOP_ART = """
#==============#
=LLLLLLLLLLLLLL=
=LWWWWWWWWWWWWL=
=LWAAAAAAAAAAWL=
=LWAaaaaaaaamWL=
=LWAaaBBBBaamWL=
=LWAaabHHhaamWL=
=LWAaabHHhaamWL=
=LWAaab..haamWL=
=LWAaab..haamWL=
=LWAaaBBBBaamWL=
=LWmmmmmmmmmmWL=
=LWWWWWWWWWWWWL=
=LwwwwwwwwwwwwL=
=LLLLLLLLLLLLLL=
#==============#
"""

TINKERING_SIDE_ART = """
#==============#
=LLLLLLLLLLLLLL=
=LWWWWWWWWWWWWL=
=L##========##L=
=L#dAAAAAAAA#dL=
=L#daaaaaaaam#L=
=L#daaaaaaaam#L=
=L#dmmmmmmmm#dL=
=L##========##L=
=LWWWWWWWWWWWWL=
=LWWWWWWWWWWWWL=
=LWWWWWWWWWWWWL=
=LWWWWWWWWWWWWL=
=LwwwwwwwwwwwwL=
=LLLLLLLLLLLLLL=
#==============#
"""

TINKERING_BOTTOM_ART = """
################
#dddddddddddddd#
#dwwwwwwwwwwwwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwWWWWWWWWWWwd#
#dwwwwwwwwwwwwd#
#dddddddddddddd#
################
"""

render_ascii_texture(TINKERING_TOP_ART, TINKERING_PALETTE).save(f"{blocks_dir}/tinkering_table_top.png")
render_ascii_texture(TINKERING_SIDE_ART, TINKERING_PALETTE).save(f"{blocks_dir}/tinkering_table_side.png")
render_ascii_texture(TINKERING_BOTTOM_ART, TINKERING_PALETTE).save(f"{blocks_dir}/tinkering_table_bottom.png")
render_ascii_texture(TINKERING_TOP_ART, TINKERING_PALETTE).save(f"{blocks_dir}/tinkering_table.png")

# -------------------------------------------------------------
# 3. BASIC ALLOY SMELTER CONTROLLER TEXTURES
# -------------------------------------------------------------
CONTROLLER_PALETTE = {
    '#': (22, 24, 28),    # Outer chassis rim
    'C': (55, 60, 68),    # Carbon steel casing
    'c': (42, 46, 52),    # Dark steel bevel
    'H': (88, 96, 108),   # Steel highlight
    'K': (185, 95, 45),   # Copper conduit pipe
    'k': (130, 60, 25),   # Copper shadow
    'V': (30, 33, 38),    # Vent grill slat
    'G': (74, 226, 82),   # Green LED status
    'R': (235, 55, 55),   # Red heat sensor
    'D': (18, 19, 22),    # Dark monitor / gauge screen
    'd': (32, 34, 38),    # Gauge bezel
    's': (36, 39, 45),    # Shadow seam
    'B': (190, 205, 230), # Blue glass / screen readout
    'b': (100, 140, 190), # Dim blue screen
    'F': (255, 230, 90),  # Active flame readout
    'O': (255, 140, 20),  # Orange alert readout
}

CONTROLLER_TOP_ART = """
################
#HHHHHHHHHHHHHH#
#HCCCCcCCCCcCCH#
#HcVVVcVVVcVcCH#
#HcVVVcVVVcVcCH#
#HcVVVcVVVcVcCH#
#HcVVVcVVVcVcCH#
#HCKKKcKKKcKcCH#
#HcKKKcKKKcKcCH#
#HcVVVcVVVcVcCH#
#HcVVVcVVVcVcCH#
#HcVVVcVVVcVcCH#
#HcVVVcVVVcVcCH#
#HCCCCcCCCCcCCH#
#cccccccccccccc#
################
"""

CONTROLLER_SIDE_ART = """
################
#HHHHHHHHHHHHHH#
#HCCCCCCKKCCCCs#
#HCCCCCCkkCCCCs#
#Hc############s#
#Hc#CCCCcCCCC#s#
#Hc#CcVVcVVcC#s#
#Hc#CcVVcVVcC#s#
#Hc#CcVVcVVcC#s#
#Hc#CcVVcVVcC#s#
#Hc#CCCCcCCCC#s#
#Hc############s#
#HCCCCCCkkCCCCs#
#HCCCCCCKKCCCCs#
#ssssssssssssss#
################
"""

CONTROLLER_BOTTOM_ART = """
################
#cccccccccccccc#
#cCCCCCCCCCCCCc#
#cCKKKcKKKcKcCc#
#cCKKKcKKKcKcCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCKKKcKKKcKcCc#
#cCKKKcKKKcKcCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cccccccccccccc#
################
"""

CONTROLLER_FRONT_UNLIT_ART = """
################
#HHHHHHHHHHHHHH#
#HCCGCCCcCCCDDs#
#Hccccccccccccs#
#Hc##########cs#
#Hc#dddddddd#cs#
#Hc#dDDDDDDd#cs#
#Hc#dDbDDbDD#cs#
#Hc#dDbDDbDD#cs#
#Hc#dDDDDDDd#cs#
#Hc#dddddddd#cs#
#Hc##########cs#
#HCKKKcKKKcKCCs#
#Hckkkckkkckccs#
#ssssssssssssss#
################
"""

CONTROLLER_FRONT_LIT_ART = """
################
#HHHHHHHHHHHHHH#
#HCCGCCCcCCCFFs#
#Hccccccccccccs#
#Hc##########cs#
#Hc#dddddddd#cs#
#Hc#dBBBBBBd#cs#
#Hc#dBFBOFBd#cs#
#Hc#dBFBOFBd#cs#
#Hc#dBBBBBBd#cs#
#Hc#dddddddd#cs#
#Hc##########cs#
#HCKKKcKKKcKCCs#
#Hckkkckkkckccs#
#ssssssssssssss#
################
"""

render_ascii_texture(CONTROLLER_TOP_ART, CONTROLLER_PALETTE).save(f"{blocks_dir}/basic_alloy_smelter_controller_top.png")
render_ascii_texture(CONTROLLER_SIDE_ART, CONTROLLER_PALETTE).save(f"{blocks_dir}/basic_alloy_smelter_controller_side.png")
render_ascii_texture(CONTROLLER_BOTTOM_ART, CONTROLLER_PALETTE).save(f"{blocks_dir}/basic_alloy_smelter_controller_bottom.png")
render_ascii_texture(CONTROLLER_FRONT_UNLIT_ART, CONTROLLER_PALETTE).save(f"{blocks_dir}/basic_alloy_smelter_controller_front.png")
render_ascii_texture(CONTROLLER_FRONT_LIT_ART, CONTROLLER_PALETTE).save(f"{blocks_dir}/basic_alloy_smelter_controller_front_on.png")
render_ascii_texture(CONTROLLER_FRONT_UNLIT_ART, CONTROLLER_PALETTE).save(f"{blocks_dir}/basic_alloy_smelter_controller.png")

# Also keep old alloy_smelter names as aliases for safety
render_ascii_texture(CONTROLLER_TOP_ART, CONTROLLER_PALETTE).save(f"{blocks_dir}/alloy_smelter_top.png")
render_ascii_texture(CONTROLLER_SIDE_ART, CONTROLLER_PALETTE).save(f"{blocks_dir}/alloy_smelter_side.png")
render_ascii_texture(CONTROLLER_BOTTOM_ART, CONTROLLER_PALETTE).save(f"{blocks_dir}/alloy_smelter_bottom.png")
render_ascii_texture(CONTROLLER_FRONT_UNLIT_ART, CONTROLLER_PALETTE).save(f"{blocks_dir}/alloy_smelter_front.png")
render_ascii_texture(CONTROLLER_FRONT_LIT_ART, CONTROLLER_PALETTE).save(f"{blocks_dir}/alloy_smelter_front_on.png")
render_ascii_texture(CONTROLLER_FRONT_UNLIT_ART, CONTROLLER_PALETTE).save(f"{blocks_dir}/alloy_smelter.png")

# -------------------------------------------------------------
# 4. BASIC ALLOY SMELTER HEATER TEXTURES
# -------------------------------------------------------------
HEATER_PALETTE = {
    '#': (22, 24, 28),    # Outer chassis rim
    'C': (48, 52, 58),    # Reinforced firebrick steel casing
    'c': (36, 40, 45),    # Dark bevel
    'H': (75, 82, 92),    # Steel highlight
    'K': (185, 95, 45),   # Copper conduit pipe
    'k': (130, 60, 25),   # Copper shadow
    'V': (25, 27, 32),    # Vent grill / grating
    'G': (38, 42, 48),    # Iron grate bars
    'g': (28, 30, 35),    # Iron grate shadow
    'D': (15, 16, 18),    # Dark cold furnace firebox
    'F': (255, 235, 110), # Blazing white/yellow flame center
    'O': (255, 140, 20),  # Radiant orange heat
    'o': (215, 65, 15),   # Deep orange thermal ember
    'e': (140, 30, 10),   # Dark ember edge
    's': (32, 35, 40),    # Shadow seam
}

HEATER_TOP_ART = """
################
#HHHHHHHHHHHHHH#
#HCCCCcCCCCcCCH#
#HcKKKcKKKcKcCH#
#HcKKKcKKKcKcCH#
#HcVVVcVVVcVcCH#
#HcVVVcVVVcVcCH#
#HcVVVcVVVcVcCH#
#HcVVVcVVVcVcCH#
#HcVVVcVVVcVcCH#
#HcVVVcVVVcVcCH#
#HcKKKcKKKcKcCH#
#HcKKKcKKKcKcCH#
#HCCCCcCCCCcCCH#
#cccccccccccccc#
################
"""

HEATER_SIDE_ART = """
################
#HHHHHHHHHHHHHH#
#HCCCCcCCCCcCCH#
#Hccccccccccccs#
#Hc##########cs#
#Hc#CCCCcCCCC#cs#
#Hc#CcVVcVVcC#cs#
#Hc#CcVVcVVcC#cs#
#Hc#CcVVcVVcC#cs#
#Hc#CcVVcVVcC#cs#
#Hc#CCCCcCCCC#cs#
#Hc##########cs#
#HCCCCCCCCCCCCs#
#HCCCCCCCCCCCCs#
#ssssssssssssss#
################
"""

HEATER_BOTTOM_ART = """
################
#cccccccccccccc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cCCCCCCCCCCCCc#
#cccccccccccccc#
################
"""

HEATER_FRONT_UNLIT_ART = """
################
#HHHHHHHHHHHHHH#
#HCCCCCCCCCCCCs#
#Hccccccccccccs#
#Hc##########cs#
#Hc#GGgGGgGG#cs#
#Hc#GDDgDDgG#cs#
#Hc#GDDgDDgG#cs#
#Hc#GGgGGgGG#cs#
#Hc#GDDgDDgG#cs#
#Hc#GDDgDDgG#cs#
#Hc#GGgGGgGG#cs#
#Hc##########cs#
#HCCCCCCCCCCCCs#
#ssssssssssssss#
################
"""

HEATER_FRONT_LIT_ART = """
################
#HHHHHHHHHHHHHH#
#HCCCCCCCCCCCCs#
#Hccccccccccccs#
#Hc##########cs#
#Hc#GGgGGgGG#cs#
#Hc#GoOgFFoG#cs#
#Hc#GOOgFOOG#cs#
#Hc#GGgGGgGG#cs#
#Hc#GOOgFFOG#cs#
#Hc#GoegeeeG#cs#
#Hc#GGgGGgGG#cs#
#Hc##########cs#
#HCCCCCCCCCCCCs#
#ssssssssssssss#
################
"""

render_ascii_texture(HEATER_TOP_ART, HEATER_PALETTE).save(f"{blocks_dir}/basic_alloy_smelter_heater_top.png")
render_ascii_texture(HEATER_SIDE_ART, HEATER_PALETTE).save(f"{blocks_dir}/basic_alloy_smelter_heater_side.png")
render_ascii_texture(HEATER_BOTTOM_ART, HEATER_PALETTE).save(f"{blocks_dir}/basic_alloy_smelter_heater_bottom.png")
render_ascii_texture(HEATER_FRONT_UNLIT_ART, HEATER_PALETTE).save(f"{blocks_dir}/basic_alloy_smelter_heater_front.png")
render_ascii_texture(HEATER_FRONT_LIT_ART, HEATER_PALETTE).save(f"{blocks_dir}/basic_alloy_smelter_heater_front_on.png")
render_ascii_texture(HEATER_FRONT_UNLIT_ART, HEATER_PALETTE).save(f"{blocks_dir}/basic_alloy_smelter_heater.png")

print("All station textures for Part Builder, Tinkering Table, Basic Alloy Smelter Controller, and Heater generated successfully!")
