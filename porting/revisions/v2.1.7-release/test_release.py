#!/usr/bin/env python3
"""Check the exact reviewed release staging, its public notes and approval guards."""
from pathlib import Path
import hashlib
import re

ROOT = Path(__file__).resolve().parents[3]
def read(path):
    return (ROOT / path).read_text(encoding="utf-8")

assert re.search(r"(?m)^version\s*=\s*2\.1\.7\s*$", read("gradle.properties"))
notes = read(".github/release-notes/v2.1.7.md")
assert notes.startswith("## 2.1.7\n")
assert notes.count("* **nicoid:** ") == 4, "Unapproved release note entries"
assert "### 🐛 Bug Fixes" in notes and "### 🔧 Improvements" in notes
assert notes.index("### 🐛 Bug Fixes") < notes.index("### 🔧 Improvements")
for phrase in ("external links", "popup playback", "background playback",
               "fullscreen playback", "without requiring an app restart"):
    assert phrase in notes, phrase
for line in notes.splitlines():
    if line.startswith("* **nicoid:** "):
        assert "**" not in line[len("* **nicoid:** "):]
source = ROOT / "porting/revisions/v2.1.7-dev.2/stage_candidate.py"
blob = source.read_bytes()
actual = hashlib.sha1(b"blob " + str(len(blob)).encode() + b"\0" + blob).hexdigest()
assert actual == "4a5311c993c96469cd973e0b90fae2ef0067760c", "Source reviewed in dev.2 changed"
shell = read(".github/scripts/stage-v2.1.7-mpp.sh")
assert "4a5311c993c96469cd973e0b90fae2ef0067760c" in shell
assert "c8e1c627bb15c46ebf49345bc0a703f00ed05998783fb280326c555527bb2978" in shell
assert "2[.]1[.]7[[:space:]]*$" in shell
assert 'script.count(\'"2.1.7-dev.2"\') != 1' in shell
assert 'script.replace(\'"2.1.7-dev.2"\', \'"2.1.7"\')' in shell
assert "porting/revisions/v2.1.7-dev.2/stage_candidate.py" in shell
for path in (".github/workflows/release.yml", ".github/workflows/verify-main.yml", ".releaserc"):
    assert "stage-v2.1.7-mpp.sh" in read(path), path
assert 'if version == "2.1.7":' in read(".github/scripts/verify-release-mpp.py")
assert 'project.version.toString() != "2.1.7"' in read("patches/build.gradle.kts")
print("PASS: v2.1.7 stable release inputs and approval policy")
