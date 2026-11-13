"""Verify the candidate MPP and require the new recovery helper in the shipped DEX."""
from pathlib import Path
import runpy
import sys
from zipfile import ZipFile

from build_mpp import BASELINE, VERSION

base, candidate = sys.argv[1:3]
verifier = BASELINE / "verify_ci_candidate.py"
sys.argv = [str(verifier), base, candidate, VERSION]
namespace = runpy.run_path(str(verifier), run_name="__main__")
with ZipFile(Path(candidate)) as archive:
    classes = namespace["dex_classes"](archive.read("nicoid/helpers.mpe"))
assert "Le/e/a/LiveWindowRecovery;" in classes, "Missing Media3 live-window recovery"

