#!/usr/bin/env python3
"""Generate three ORIGINAL 20-second dark-fantasy compositions as Ogg Vorbis.

No borrowed recordings or melodies. Deterministic additive synthesis gives the
pack a modest, redistributable soundtrack with zero third-party assets.
Requires ffmpeg on the build runner (already provided by our CI step).
"""
import array
import math
import shutil
import subprocess
import tempfile
import wave
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "src/main/resources/assets/shatteredseals/sounds/music"
RATE = 22050
SECONDS = 20
SCALE = {
 "boss": (110.0, [(0,3),(2,4),(5,6),(7,3),(10,5),(12,4),(15,7),(17,2)], 0.34),
 "sanctuary": (164.81, [(0,0),(2,4),(5,7),(7,11),(10,12),(12,7),(15,4),(17,0)], 0.24),
 "explore": (146.83, [(0,0),(2,3),(5,7),(7,10),(10,7),(12,5),(15,3),(17,0)], 0.22),
}

def compose(label, root, notes, volume):
    timeline = [0] * (SECONDS * RATE)
    step = math.pi * 2 / RATE
    chords = (0, 3, 7) if label != "sanctuary" else (0, 4, 7)
    for start, semitone in notes:
        duration = min(3.2 if label == "boss" else 4.4, SECONDS-start)
        frequency = root * (2 ** (semitone / 12))
        for j in range(int(duration * RATE)):
            t = j / RATE
            env = min(1.0, t * 3.0) * max(0.0, 1.0 - t / duration) ** 1.7
            tone = (math.sin(step * frequency * j) * 0.66
                    + math.sin(step * frequency * 2 * j) * 0.17
                    + math.sin(step * frequency * 0.5 * j) * 0.13)
            timeline[start * RATE + j] += tone * env * 0.36
    for chord in range(0, SECONDS, 5):
        for semitone in chords:
            f = root * (2 ** ((semitone - 12) / 12))
            for j in range(min(RATE * 5, (SECONDS-chord) * RATE)):
                t = j / RATE
                envelope = 0.62 * math.sin(math.pi * t/5)**2
                timeline[chord*RATE+j] += math.sin(step*f*j)*envelope*0.15
    if label == "boss":
        for beat in range(0, SECONDS * 2):
            start = beat * RATE // 2
            for j in range(RATE//8):
                t = j / RATE
                timeline[start+j] += math.sin(step*(70-45*t)*j)*math.exp(-30*t)*0.36
    data = array.array("h", (int(max(-1.0, min(1.0, s * volume))*32767) for s in timeline))
    if shutil.which("ffmpeg") is None:
        raise SystemExit("ffmpeg is required to encode the original Ogg soundtrack")
    OUT.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory() as folder:
        wav = Path(folder) / (label+".wav")
        with wave.open(str(wav), "wb") as audio:
            audio.setnchannels(1)
            audio.setsampwidth(2)
            audio.setframerate(RATE)
            audio.writeframes(data.tobytes())
        subprocess.run(["ffmpeg","-hide_banner","-loglevel","error","-y","-i",str(wav),
                        "-c:a","libvorbis","-q:a","3",str(OUT/(label+".ogg"))],check=True)
    print(label, (OUT/(label+".ogg")).stat().st_size, "bytes")

if __name__ == "__main__":
    for key, (root, notes, volume) in SCALE.items():
        compose(key, root, notes, volume)
