#!/usr/bin/env python3
"""Original story-linked Sanctuary visual identity, entirely stdlib/generated.

All art is original procedural pixel art. No copied game logos, licensed textures,
third-party shader archives, executable pack scripts or downloaded artwork.
Run before Gradle so the main-menu and loading GUI assets enter the mod JAR.
"""
from __future__ import annotations

import json
import math
import random
import struct
import zlib
from pathlib import Path
from zipfile import ZIP_DEFLATED, ZipFile

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/shatteredseals/textures/gui"
OUT = ROOT / "build/brand"
RESOURCE_PACK = OUT / "The-Shattered-Seals-Ashen-Covenant.zip"

# Block letter glyphs are original 5x7 patterns, not a font redistribution.
GLYPHS = {
    "A": ("01110","10001","10001","11111","10001","10001","10001"),
    "B": ("11110","10001","10001","11110","10001","10001","11110"),
    "C": ("01111","10000","10000","10000","10000","10000","01111"),
    "D": ("11110","10001","10001","10001","10001","10001","11110"),
    "E": ("11111","10000","10000","11110","10000","10000","11111"),
    "F": ("11111","10000","10000","11110","10000","10000","10000"),
    "G": ("01111","10000","10000","10111","10001","10001","01111"),
    "H": ("10001","10001","10001","11111","10001","10001","10001"),
    "I": ("11111","00100","00100","00100","00100","00100","11111"),
    "J": ("00111","00010","00010","00010","10010","10010","01100"),
    "K": ("10001","10010","10100","11000","10100","10010","10001"),
    "L": ("10000","10000","10000","10000","10000","10000","11111"),
    "M": ("10001","11011","10101","10101","10001","10001","10001"),
    "N": ("10001","11001","10101","10011","10001","10001","10001"),
    "O": ("01110","10001","10001","10001","10001","10001","01110"),
    "P": ("11110","10001","10001","11110","10000","10000","10000"),
    "Q": ("01110","10001","10001","10001","10101","10010","01101"),
    "R": ("11110","10001","10001","11110","10100","10010","10001"),
    "S": ("01111","10000","10000","01110","00001","00001","11110"),
    "T": ("11111","00100","00100","00100","00100","00100","00100"),
    "U": ("10001","10001","10001","10001","10001","10001","01110"),
    "V": ("10001","10001","10001","10001","10001","01010","00100"),
    "W": ("10001","10001","10001","10101","10101","10101","01010"),
    "X": ("10001","10001","01010","00100","01010","10001","10001"),
    "Y": ("10001","10001","01010","00100","00100","00100","00100"),
    "Z": ("11111","00001","00010","00100","01000","10000","11111"),
    "0": ("01110","10001","10011","10101","11001","10001","01110"),
    "1": ("00100","01100","00100","00100","00100","00100","01110"),
    "2": ("01110","10001","00001","00010","00100","01000","11111"),
    "3": ("11110","00001","00001","01110","00001","00001","11110"),
    "4": ("00010","00110","01010","10010","11111","00010","00010"),
    "5": ("11111","10000","10000","11110","00001","00001","11110"),
    "6": ("01110","10000","10000","11110","10001","10001","01110"),
    ".": ("00000","00000","00000","00000","00000","00110","00110"),
    "-": ("00000","00000","00000","11111","00000","00000","00000"),
    ":": ("00000","00100","00100","00000","00100","00100","00000"),
    " ": ("00000",) * 7,
}
SEALS = [(220,166,82), (225,95,58), (151,123,182),
         (88,182,222), (96,172,129), (174,69,111)]


def canvas(w: int, h: int, top=(13, 20, 32), bottom=(40, 30, 56)):
    pixels = []
    for y in range(h):
        t = y / max(1, h - 1)
        pixels.append([
            (int(top[c] * (1-t) + bottom[c]*t), int(top[c] * (1-t) + bottom[c]*t),
             int(top[c] * (1-t) + bottom[c]*t), 255) for c in ()
        ])
        # Vertical midnight gradient. Build each scanline without images/libs.
        rgb = tuple(round(top[c] * (1-t) + bottom[c] * t) for c in range(3))
        pixels[-1] = [(*rgb, 255)] * w
    return pixels


def px(p, x, y, color):
    if 0 <= x < len(p[0]) and 0 <= y < len(p):
        p[y][x] = tuple(color[:3]) + (color[3] if len(color) == 4 else 255,)


def rect(p, x0, y0, x1, y1, color):
    for y in range(max(0, y0), min(len(p), y1)):
        for x in range(max(0, x0), min(len(p[0]), x1)):
            px(p, x, y, color)


def line(p, x0, y0, x1, y1, color, thickness=1):
    count = max(abs(x1-x0), abs(y1-y0), 1)
    for i in range(count + 1):
        x = round(x0 + (x1-x0) * i / count)
        y = round(y0 + (y1-y0) * i / count)
        rect(p, x, y, x + thickness, y + thickness, color)


def circle(p, cx, cy, radius, color, thickness=1):
    for i in range(360):
        theta = math.tau * i / 360
        x, y = round(cx + radius*math.cos(theta)), round(cy + radius*math.sin(theta))
        rect(p, x, y, x+thickness, y+thickness, color)


def lettering(p, text, x, y, scale=1, color=(246, 210, 153), center=False):
    text = text.upper()
    if center:
        x -= (len(text) * 6 - 1) * scale // 2
    for ch in text:
        bitmap = GLYPHS.get(ch, GLYPHS[" "])
        for gy, row in enumerate(bitmap):
            for gx, bit in enumerate(row):
                if bit == "1":
                    rect(p, x + gx*scale, y + gy*scale,
                         x + (gx+1)*scale, y + (gy+1)*scale, color)
        x += 6 * scale


def landscape(p):
    w, h = len(p[0]), len(p)
    sky = h // 2
    r = random.Random(602161)
    # Ash particles and six chapter constellation seals.
    for _ in range(max(40, w*h//1300)):
        x, y = r.randrange(w), r.randrange(max(sky, 1))
        px(p, x, y, (120+r.randrange(110), 106, 133))
    for i, color in enumerate(SEALS):
        cx = w * (i+1)//7
        cy = max(10, h//8) + (i % 2) * max(5, h//40)
        circle(p, cx, cy, max(5, w//48), color, max(1,w//256))
        line(p, cx, cy - 4, cx+4, cy, color, max(1,w//256))
        line(p, cx+4, cy, cx, cy+4, color, max(1,w//256))
        line(p, cx, cy+4, cx-4, cy, color, max(1,w//256))
        line(p, cx-4, cy, cx, cy-4, color, max(1,w//256))
    silhouette = (15, 19, 31)
    stone = (33, 39, 52)
    # Distinctive central high keep and asymmetrical crenellated towers.
    base = round(h*.76)
    rect(p, int(w*.12), int(h*.57), int(w*.88), base, stone)
    for i,(left,right,high) in enumerate((
        (.12,.23,.30),(.31,.41,.43),(.44,.56,.22),(.66,.74,.39),(.79,.88,.29)
    )):
        x0,x1 = int(w*left),int(w*right)
        yh=int(h*high)
        rect(p, x0,yh,x1,base,silhouette)
        for x in range(x0,x1,max(5,w//100)):
            rect(p,x,yh-max(3,h//110),min(x+max(3,w//90),x1),yh,stone)
        # Lantern windows and narrow Gothic arrow slits.
        for y in range(yh+max(10,h//18),base,max(12,h//10)):
            mid=(x0+x1)//2
            rect(p,mid-2,y,mid+2,y+max(7,h//40), (129, 104, 151))
    # Central rose window and grand gate.
    circle(p,w//2,int(h*.48),max(8,w//32),(163,117,164),max(1,w//256))
    rect(p,w//2-w//45,int(h*.65),w//2+w//45,base,(8,10,19))
    # A broken golden line represents the sixth seal's fracture.
    line(p,int(w*.12),int(h*.73),int(w*.49),int(h*.73),(186,132,74),2)
    line(p,int(w*.51),int(h*.73),int(w*.88),int(h*.73),(186,132,74),2)


def png_bytes(pixels):
    height, width = len(pixels), len(pixels[0])
    def chunk(tag, payload):
        return struct.pack(">I",len(payload)) + tag + payload + struct.pack(
            ">I",zlib.crc32(tag+payload)&0xffffffff)
    scan = b"".join(b"\x00"+b"".join(bytes(pixel) for pixel in row)
                    for row in pixels)
    return (b"\x89PNG\r\n\x1a\n" +
        chunk(b"IHDR",struct.pack(">IIBBBBB",width,height,8,6,0,0,0)) +
        chunk(b"IDAT",zlib.compress(scan,9)) + chunk(b"IEND",b""))


def block_texture(key: str, seed: int) -> bytes:
    base = {
        "deepslate_bricks": (55,62,80),
        "polished_deepslate": (66,71,85),
        "chiseled_stone_bricks": (108,111,115),
        "dark_oak_planks": (76,55,53),
        "polished_blackstone_bricks": (47,44,60),
        "chiseled_deepslate": (60,64,78),
    }[key]
    rng = random.Random(seed)
    p = [[None for _ in range(16)] for _ in range(16)]
    for y in range(16):
        for x in range(16):
            mortar = y in (0,7,15) or (x in (0,8) if y<7 else x in (4,12))
            grain = rng.randrange(-15,16)
            shimmer = 7 if (x+y)%11==0 else 0
            delta = (-20 if mortar else 0) + grain + shimmer
            p[y][x] = tuple(max(0,min(255,c+delta)) for c in base)+(255,)
    return png_bytes(p)


def main():
    ASSETS.mkdir(parents=True, exist_ok=True)
    OUT.mkdir(parents=True, exist_ok=True)

    title = canvas(256,80)
    landscape(title)
    rect(title,9,43,247,80,(13,20,33,241))
    lettering(title,"THE",128,44,1,(208,163,101),center=True)
    lettering(title,"SHATTERED SEALS",128,53,2,(249,215,158),center=True)
    lettering(title,"SIX CHAPTERS  ONE COVENANT",128,72,1,(154,187,203),center=True)
    (ASSETS/"title_banner.png").write_bytes(png_bytes(title))

    loading = canvas(512,256,top=(11,16,31),bottom=(42,26,49))
    landscape(loading)
    rect(loading,0,175,512,256,(13,19,32,255))
    lettering(loading,"THE SHATTERED SEALS",256,191,3,(239,204,139),center=True)
    lettering(loading,"AWAKENING THE SIX SEALS",256,229,2,(157,201,213),center=True)
    (ASSETS/"loading_banner.png").write_bytes(png_bytes(loading))

    thumb = canvas(512,512,top=(12,19,34),bottom=(57,32,57))
    landscape(thumb)
    rect(thumb,0,366,512,512,(12,19,32,255))
    lettering(thumb,"THE",256,383,3,(201,162,99),center=True)
    lettering(thumb,"SHATTERED",256,414,4,(247,210,150),center=True)
    lettering(thumb,"SEALS",256,453,4,(247,210,150),center=True)
    lettering(thumb,"VANILLA+ RPG",256,490,2,(137,188,212),center=True)
    thumbnail = png_bytes(thumb)
    (OUT/"curseforge_thumbnail.png").write_bytes(thumbnail)

    with ZipFile(RESOURCE_PACK, "w", compression=ZIP_DEFLATED, compresslevel=9) as pack:
        pack.writestr("pack.mcmeta",json.dumps({
            "pack":{"pack_format":34,
                    "description":"The Shattered Seals | Ashen Covenant: original Gothic stone, timber and seal colours"}
        },indent=2))
        pack.writestr("pack.png",thumbnail)
        for i,key in enumerate((
            "deepslate_bricks","polished_deepslate","chiseled_stone_bricks",
            "dark_oak_planks","polished_blackstone_bricks","chiseled_deepslate"
        )):
            pack.writestr(f"assets/minecraft/textures/block/{key}.png",
                          block_texture(key,601+i))
    print(f"Original branded assets: {ASSETS}; pack: {RESOURCE_PACK}; CurseForge thumbnail: {OUT/'curseforge_thumbnail.png'}")


if __name__ == "__main__":
    main()
