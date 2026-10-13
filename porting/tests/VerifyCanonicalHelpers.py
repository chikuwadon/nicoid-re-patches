"""Require explicit ownership for every Java helper and acknowledgement of shadowed edits."""
from pathlib import Path
import hashlib
import json

repo = Path(__file__).resolve().parents[2]
java = repo / "extensions/extension/src/main/java/e/e/a"
smali = repo / "porting/helpers-smali/e/e/a"
manifest = json.loads((repo / "porting/helper-ownership.json").read_text())
actual = {p.stem for p in java.glob("*.java")}
assert actual == set(manifest), "Update helper ownership when adding/removing a source family"

def digest(paths):
    h = hashlib.sha256()
    for p in sorted(paths):
        h.update(p.name.encode())
        h.update(b"\0")
        h.update(p.read_bytes())
    return h.hexdigest()

shadowed = 0
for name, entry in manifest.items():
    overrides = [p for p in smali.glob("*.smali") if p.stem.split("$", 1)[0] == name]
    if entry["owner"] == "java":
        assert not overrides, f"Legacy override masks canonical Java family: {name}"
    else:
        assert entry["owner"] == "smali" and overrides, f"Missing declared implementation: {name}"
        assert digest([java / (name + ".java")]) == entry["java_sha256"], f"Shadowed Java changed without ownership review: {name}"
        assert digest(overrides) == entry["smali_sha256"], f"Override changed without ownership review: {name}"
        shadowed += 1
print(f"Implementation ownership verified: {len(actual)} families, {shadowed} reviewed overrides")
