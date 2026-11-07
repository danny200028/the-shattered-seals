#!/usr/bin/env python3
"""Deterministic source check. NOT a substitute for compilation or in-game testing."""
from pathlib import Path
import json, sys, re
root=Path(__file__).resolve().parents[1]
errors=[]
required=[
    'build.gradle','settings.gradle',
    'src/main/resources/META-INF/neoforge.mods.toml',
    'src/main/java/com/shatteredseals/ShatteredSeals.java',
    'src/main/java/com/shatteredseals/client/SkillTreeScreen.java',
    'src/main/java/com/shatteredseals/net/SkillNetworking.java',
    'src/main/resources/data/shatteredseals/function/load.mcfunction',
    'src/main/resources/data/shatteredseals/function/progression/tick.mcfunction',
    'src/main/resources/data/shatteredseals/function/talents/buy.mcfunction',
    'src/main/resources/data/shatteredseals/function/talents/master_buy.mcfunction',
    'src/main/resources/data/shatteredseals/function/progression/reset.mcfunction',
]
for rel in required:
    if not (root/rel).is_file():errors.append('Missing: '+rel)
json_files=list(root.rglob('*.json'))+list(root.rglob('*.mcmeta'))
for p in json_files:
    if any(x in p.parts for x in ('.gradle','build')):continue
    try:json.loads(p.read_text(encoding='utf-8'))
    except Exception as e:errors.append('Bad JSON: '+str(p.relative_to(root))+' '+str(e))
load=root/'src/main/resources/data/shatteredseals/function/load.mcfunction'
if load.is_file():
    ls=load.read_text(encoding='utf-8')
    for objective in ['ss_level','ss_points','ss_branch','ss_ascend','ss_switch','ss_respec','ss_b11','ss_b43','ss_a11','ss_a43']:
        if not re.search(r'^scoreboard objectives add '+objective+r' ',ls,re.M):errors.append('Missing scoreboard objective: '+objective)
meta=root/'src/main/resources/META-INF/neoforge.mods.toml'
if meta.is_file() and 'modId="shatteredseals"' not in meta.read_text(): errors.append('Invalid mod id')
print('Static check:',len(json_files),'JSON/mcmeta resources,',len(required),'essential files')
if errors:
    print('FAILED:',*errors,sep='\n - ',file=sys.stderr)
    sys.exit(1)
print('PASS (static only; does NOT confirm Java compilation or Minecraft functionality)')
