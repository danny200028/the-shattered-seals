#!/usr/bin/env python3
"""Original pixel-art boss mask, trophy and Keystone models and PNG textures.

Fully generated with Python standard library; no third-party art or license.
"""
from pathlib import Path
import json
import struct
import zlib

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT/"src/main/resources/assets/shatteredseals"
PALETTES = {
    "smuggler_king": ((178,128,47),(28,25,22),(234,205,108),"hook"),
    "cinder_warden": ((216,71,33),(69,22,20),(255,186,57),"flame"),
    "hollow_knight": ((121,103,160),(29,28,42),(200,187,211),"horn"),
    "arcane_seer": ((78,188,222),(18,44,78),(220,244,255),"spire"),
    "sixfold_guardian": ((70,165,111),(31,61,44),(221,205,79),"leaf"),
    "hollow_king": ((145,31,58),(22,17,37),(235,194,93),"crown"),
}

def png(path, grid):
    h,w=len(grid),len(grid[0])
    def chunk(name,payload):
        return struct.pack(">I",len(payload))+name+payload+struct.pack(">I",zlib.crc32(name+payload)&0xffffffff)
    scan=b"".join(b"\x00"+b"".join(bytes(pixel) for pixel in row) for row in grid)
    path.parent.mkdir(parents=True,exist_ok=True)
    path.write_bytes(b"\x89PNG\r\n\x1a\n"+chunk(b"IHDR",struct.pack(">IIBBBBB",w,h,8,6,0,0,0))
        +chunk(b"IDAT",zlib.compress(scan,9))+chunk(b"IEND",b""))

def painted(name, primary, shadow, accent, icon):
    canvas=[[(0,0,0,0) for _ in range(32)] for _ in range(32)]
    def pixel(x,y,color):
        if 0<=x<32 and 0<=y<32: canvas[y][x]=(*color,255)
    for y in range(5,26):
        for x in range(5,27):
            edge=x in (5,6,25,26) or y in (5,6,24,25)
            pixel(x,y,shadow if edge else tuple(max(0,c-(y-5)*2) for c in primary))
    for x in range(7,25):
        for y in range(12,15):
            pixel(x,y,shadow)
    for x in (10,11,20,21):
        for y in (12,13): pixel(x,y,accent)
    for y in range(4,10):
        pixel(16,y,accent)
        pixel(15,y,accent)
    for x in range(13,20):
        pixel(x,20,accent if x%2==1 else shadow)
    if icon=="crown":
        for x in (7,12,19,24):
            for y in range(1,7):pixel(x,y,accent)
    elif icon in ("flame","horn"):
        for y in range(2,9):
            pixel(6+(8-y)//2,y,accent);pixel(25-(8-y)//2,y,accent)
    elif icon=="spire":
        for y in range(0,11):pixel(16,y,accent)
    elif icon=="leaf":
        for y in range(2,10):
            pixel(4+(y//3),y,accent);pixel(27-(y//3),y,accent)
    elif icon=="hook":
        for y in range(2,9):pixel(5,y,accent);pixel(26,y,accent)
    png(ASSETS/f"textures/item/boss_mask_{name}.png",canvas)

def mask_model(name,style):
    texture=f"shatteredseals:item/boss_mask_{name}"
    def cube(a,b,uv=(0,0,16,16)):
        return {"from":a,"to":b,"faces":{face:{"texture":"#all","uv":list(uv)}
            for face in ("north","south","east","west","up","down")}}
    elements=[cube([2,1,2],[14,13,14])]
    for x in (2,12):
        elements.append(cube([x,11,4],[x+2,16 if style in ("horn","flame","crown") else 14,12]))
    if style in ("spire","crown"):elements.append(cube([7,13,7],[9,16,9]))
    return {"parent":"minecraft:block/block","textures":{"all":texture,"particle":texture},
        "elements":elements,"display":{
            "gui":{"rotation":[30,45,0],"translation":[0,0,0],"scale":[0.78,0.78,0.78]},
            "head":{"rotation":[0,0,0],"translation":[0,0,0],"scale":[1,1,1]},
            "thirdperson_righthand":{"rotation":[75,45,0],"translation":[0,1,0],"scale":[0.55,0.55,0.55]},
            "ground":{"scale":[0.45,0.45,0.45]}}}

def ornament(name,primary,shadow,accent):
    grid=[[(0,0,0,0) for _ in range(32)] for _ in range(32)]
    for y in range(5,27):
        for x in range(5,27):
            if 6<=abs(x-16)+abs(y-16)<=18:
                grid[y][x]=(*accent,255)
            elif abs(x-16)+abs(y-16)<=7:
                grid[y][x]=(*primary,255)
    for x in range(12,21):
        for y in range(13,20):
            if x in (12,20) or y in (13,19):grid[y][x]=(*shadow,255)
    png(ASSETS/f"textures/item/{name}.png",grid)
    model={"parent":"minecraft:item/generated","textures":{"layer0":f"shatteredseals:item/{name}"}}
    dest=ASSETS/f"models/item/{name}.json"
    dest.parent.mkdir(parents=True,exist_ok=True)
    dest.write_text(json.dumps(model,indent=2)+"\n")

def entity_skin(name, primary, shadow, accent):
    """Original complete mob UV atlas; vanilla mobs retain vanilla renderer
    unless they carry the corresponding Shattered Seals boss tag."""
    size = 128 if name in ("sixfold_guardian","hollow_king") else 64
    canvas=[[(0,0,0,255) for _ in range(size)] for _ in range(size)]
    for y in range(size):
        for x in range(size):
            stripe = ((x//4 + y//7) % 9 == 0)
            grain = ((x*31+y*17+x*y*3) % 13)
            color = accent if stripe and grain < 3 else shadow if (x+y)%19==0 else primary
            shaded = tuple(max(0,min(255,c-(y*9//size)+(grain-6)*2)) for c in color)
            canvas[y][x]=(*shaded,255)
    # Distinctive glowing eye-bars and heraldry on the texture UV panels.
    for x in range(size//8,size//8+size//8):
        for y in range(size//8,size//8+size//10):
            if y%3==0 or x%7==0:canvas[y][x]=(*accent,255)
    for y in range(size//2,size//2+size//5):
        for x in range(size//3,size//3+size//5):
            if (x-y)%6 in (0,1):canvas[y][x]=(*shadow,255)
    png(ASSETS/f"textures/entity/boss/{name}.png",canvas)

def main():
    for name,(primary,shadow,accent,style) in PALETTES.items():
        painted(name,primary,shadow,accent,style)
        entity_skin(name,primary,shadow,accent)
        out=ASSETS/f"models/item/boss_mask_{name}.json"
        out.parent.mkdir(parents=True,exist_ok=True)
        out.write_text(json.dumps(mask_model(name,style),indent=2)+"\n")
    ornament("sanctuary_keystone",(80,171,211),(22,44,66),(236,207,112))
    ornament("archivists_crown",(124,81,184),(35,24,63),(235,189,101))
    print("Generated eight original 32px item textures, six full-entity skins and six 3D boss-mask models")

if __name__=="__main__":main()
