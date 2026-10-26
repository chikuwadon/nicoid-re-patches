"""Validate hold-to-speed move guards against the canonical bundled smali."""
from pathlib import Path
import re
import unittest
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[2]
BUILD = ROOT / "patches" / "build.gradle.kts"
ARCHIVE = ROOT / "porting" / "helpers-v1.9.3.zip"

PATCHES = {
    "e/e/a/HoldSpeed.smali": ("hold_speed_pre_hold_move", "return v3"),
    "e/e/a/PopupHoldSpeed.smali": ("popup_hold_speed_pre_hold_move", "return v2"),
}


def replacement_for(path):
    source = BUILD.read_text(encoding="utf-8")
    # The build task applies these exact, guarded substitutions after archive staging.
    expression = re.compile(
        r'"' + re.escape(path) + r'"\s+to\s+Pair\(\s*"(?P<old>[^"]+)",\s*"""(?P<new>.*?)"""',
        re.S,
    )
    matches = list(expression.finditer(source))
    if len(matches) != 1:
        raise AssertionError(f"Expected one guarded substitution for {path}")
    old = matches[0]["old"].replace(r"\n", "\n")
    return old, matches[0]["new"]


class HoldSpeedMovementTest(unittest.TestCase):
    def test_all_guards_apply_to_canonical_archive(self):
        with ZipFile(ARCHIVE) as archive:
            for path, (label, ret) in PATCHES.items():
                with self.subTest(path=path):
                    original = archive.read(path).decode("utf-8")
                    before, after = replacement_for(path)
                    self.assertEqual(original.count(before), 1)
                    patched = original.replace(before, after)
                    self.assertNotEqual(original, patched)
                    self.assertEqual(patched.count(":" + label), 4 if path.endswith("/HoldSpeed.smali") else 4)
                    # Active one-finger ACTION_MOVE exits before the pre-hold slop gate.
                    guard = patched[patched.index(after):patched.index(after) + len(after)]
                    self.assertIn("const/4 v5, 0x2", guard)
                    self.assertIn("->getPointerCount()I", guard)
                    self.assertIn("if-ne v5, v4, :" + label, guard)
                    self.assertIn(ret, guard)
                    self.assertIn(":" + label + "\n", guard)

    def test_terminal_and_multitouch_cleanup_remains(self):
        with ZipFile(ARCHIVE) as archive:
            normal = archive.read("e/e/a/HoldSpeed.smali").decode()
            popup = archive.read("e/e/a/PopupHoldSpeed.smali").decode()
        old, new = replacement_for("e/e/a/HoldSpeed.smali")
        normal = normal.replace(old, new)
        self.assertIn("if-eq v2, v4, :cond_12a", normal)  # ACTION_UP
        self.assertIn("if-ne v2, v5, :cond_ee", normal)  # ACTION_CANCEL
        self.assertIn("if-gt v6, v4, :cond_121", normal)  # second pointer
        old, new = replacement_for("e/e/a/PopupHoldSpeed.smali")
        popup = popup.replace(old, new)
        self.assertIn("if-eq v0, v4, :cond_101", popup)  # ACTION_UP
        self.assertIn("if-eq v0, v5, :cond_101", popup)  # ACTION_CANCEL
        self.assertIn("if-le v5, v4, :cond_d3", popup)  # second pointer


if __name__ == "__main__":
    unittest.main()
