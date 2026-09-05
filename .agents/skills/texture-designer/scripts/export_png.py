#!/usr/bin/env python3
"""
Pure Python 16x16 PNG Texture Exporter for Minecraft Modding.
Converts hex color matrix JSON files or direct CLI arguments into lossless 32-bit RGBA PNG textures.
Zero external dependencies required (uses only Python standard library: struct, zlib, json, argparse).
"""

import argparse
import json
import os
import struct
import sys
import zlib

def parse_hex_color(color_str):
    """Parses a hex color string (#RRGGBB, #RRGGBBAA, 0xRRGGBB, or 0xAARRGGBB) into RGBA tuple (0-255)."""
    s = color_str.strip()
    if s.startswith('#'):
        s = s[1:]
    elif s.startswith('0x') or s.startswith('0X'):
        s = s[2:]
    
    if len(s) == 6:
        r = int(s[0:2], 16)
        g = int(s[2:4], 16)
        b = int(s[4:6], 16)
        a = 255
        return (r, g, b, a)
    elif len(s) == 8:
        # Check if alpha is at start or end. If standard web RGBA: RRGGBBAA
        r = int(s[0:2], 16)
        g = int(s[2:4], 16)
        b = int(s[4:6], 16)
        a = int(s[6:8], 16)
        return (r, g, b, a)
    elif len(s) == 3:
        r = int(s[0] * 2, 16)
        g = int(s[1] * 2, 16)
        b = int(s[2] * 2, 16)
        a = 255
        return (r, g, b, a)
    elif len(s) == 4:
        r = int(s[0] * 2, 16)
        g = int(s[1] * 2, 16)
        b = int(s[2] * 2, 16)
        a = int(s[3] * 2, 16)
        return (r, g, b, a)
    elif s.lower() == 'transparent' or s.lower() == 'none':
        return (0, 0, 0, 0)
    else:
        raise ValueError(f"Unrecognized color format: '{color_str}'")

def write_png(file_path, width, height, rgba_matrix):
    """Writes a 2D matrix of (r, g, b, a) tuples as a lossless 32-bit RGBA PNG."""
    os.makedirs(os.path.dirname(os.path.abspath(file_path)), exist_ok=True)
    
    raw_data = bytearray()
    for row in rgba_matrix:
        raw_data.append(0)  # Filter type 0 (None)
        for pixel in row:
            if isinstance(pixel, (tuple, list)):
                r, g, b = pixel[0], pixel[1], pixel[2]
                a = pixel[3] if len(pixel) > 3 else 255
            elif isinstance(pixel, str):
                r, g, b, a = parse_hex_color(pixel)
            else:
                raise ValueError(f"Invalid pixel value: {pixel}")
            raw_data.extend([r, g, b, a])
            
    compressed = zlib.compress(bytes(raw_data), level=9)
    
    png = bytearray(b'\x89PNG\r\n\x1a\n')
    
    # IHDR Chunk
    ihdr_data = struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0)
    ihdr_crc = zlib.crc32(b'IHDR' + ihdr_data) & 0xffffffff
    png.extend(struct.pack('>I', len(ihdr_data)) + b'IHDR' + ihdr_data + struct.pack('>I', ihdr_crc))
    
    # IDAT Chunk
    idat_crc = zlib.crc32(b'IDAT' + compressed) & 0xffffffff
    png.extend(struct.pack('>I', len(compressed)) + b'IDAT' + compressed + struct.pack('>I', idat_crc))
    
    # IEND Chunk
    iend_crc = zlib.crc32(b'IEND') & 0xffffffff
    png.extend(struct.pack('>I', 0) + b'IEND' + struct.pack('>I', iend_crc))
    
    with open(file_path, 'wb') as f:
        f.write(png)
    
    print(f"Successfully exported {width}x{height} PNG to: {file_path}")

def export_from_json(json_path):
    """Parses a texture JSON definition and writes all configured PNG files."""
    with open(json_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    # Format: {"textures": [{"output": "path/to/texture.png", "width": 16, "height": 16, "palette": {...}, "pixels": [...]}]}
    # OR direct single texture: {"output": "path/to/texture.png", "pixels": [...]}
    textures = data.get("textures", [data])
    
    for tex in textures:
        output_path = tex.get("output")
        if not output_path:
            print(f"Skipping texture entry with missing 'output' field.")
            continue
        
        width = tex.get("width", 16)
        height = tex.get("height", 16)
        palette = tex.get("palette")
        raw_pixels = tex.get("pixels")
        
        if not raw_pixels:
            print(f"Skipping '{output_path}': missing 'pixels' matrix.")
            continue
        
        rgba_matrix = []
        for row in raw_pixels:
            matrix_row = []
            for item in row:
                if palette and isinstance(item, (int, str)) and str(item) in palette:
                    matrix_row.append(palette[str(item)])
                elif palette and isinstance(item, int) and item < len(palette) and isinstance(palette, list):
                    matrix_row.append(palette[item])
                else:
                    matrix_row.append(item)
            rgba_matrix.append(matrix_row)
        
        write_png(output_path, width, height, rgba_matrix)

def main():
    parser = argparse.ArgumentParser(description="Export 16x16 Minecraft pixel textures to PNG")
    parser.add_argument("--json", "-j", help="Path to JSON texture definition file")
    parser.add_argument("--output", "-o", help="Target PNG output path")
    parser.add_argument("--width", "-W", type=int, default=16, help="Width in pixels (default: 16)")
    parser.add_argument("--height", "-H", type=int, default=16, help="Height in pixels (default: 16)")
    
    args = parser.parse_args()
    
    if args.json:
        export_from_json(args.json)
    else:
        print("Usage: python export_png.py --json <path_to_texture_definition.json>")
        sys.exit(1)

if __name__ == "__main__":
    main()
