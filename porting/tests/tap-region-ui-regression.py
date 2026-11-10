#!/usr/bin/env python3
"""dev.14: static UI layout invariants for the single-tap area editor.

The existing tap-regions-regression.py covers behavior. This guards the
presentation-only polish without changing persisted preset IDs.
"""
from pathlib import Path

root = Path(__file__).resolve().parents[2]
source = (root / "porting/revisions/v1.10.7-dev.14/java/e/e/a/TapRegionSettings141.java").read_text(encoding="utf-8")
regions = (root / "porting/revisions/v1.10.7-dev.14/java/e/e/a/TapRegions141.java").read_text(encoding="utf-8")
touch = (root / "porting/revisions/v1.10.7-dev.14/java/e/e/a/TapControls141.java").read_text(encoding="utf-8")
checks = 0

def require(condition, description):
    global checks
    checks += 1
    if not condition:
        raise AssertionError(description)

for token in (
    "int[] order = {TapRegions141.ALL, TapRegions141.CUSTOM,",
    "TapRegions141.VERTICAL_EDGES, TapRegions141.CENTER, TapRegions141.EDGES};",
    "int from = line == 0 ? 0 : 2;",
    "int until = line == 0 ? 2 : order.length;",
    "presets[option] = preset;",
    "rowSize.topMargin = dp(line == 0 ? 3 : 7);",
    "buttonSize.leftMargin = buttonSize.rightMargin = dp(4);",
):
    require(token in source, "Preset order / spacing changed: " + token)

for token in (
    "GradientDrawable.OVAL",
    "new LinearLayout.LayoutParams(dp(38), dp(38))",
    "background.setCornerRadius(dp(12));",
    "selection.setCornerRadius(dp(10));",
    "pair.setPadding(0, dp(i == 0 ? 3 : 7), 0, dp(3));",
    "selectorSize.setMargins(dp(2), dp(7), dp(2), dp(8));",
    "actions.setPadding(0, dp(3), 0, dp(6));",
    "resetRow.setPadding(0, dp(7), 0, dp(7));",
):
    require(token in source, "Requested corner / spacing polish missing: " + token)

for token in (
    "setSingleChoiceItems(items, selected",
    "roundDialog(dialog, 28);",
    "roundDialog(picker, 24);",
    "Dev112.show(picker);",
):
    require(token in source, "Rounded area selection dialog missing: " + token)

require("new PopupMenu(" not in source, "Rectangular native popup selector must be replaced")
require("if (i > 0) divider(body, 1, 1);" not in source, "Redundant quick adjust line still present")

for token in (
    "static final int ALL = 0, EDGES = 1, VERTICAL_EDGES = 2, CENTER = 3, CUSTOM = 4;",
    "static TapRegions141 custom(int[][] ranges)",
    "static TapRegions141 parse(String value)",
):
    require(token in regions, "Stored region identity / compatibility changed: " + token)
for token in (
    "nicoid_single_tap_control_region",
    "activityTouch(Activity",
    "nativeDown(",
):
    require(token in touch, "Existing touch routing changed: " + token)

print(f"PASS: {checks} tap-area UI presentation and compatibility invariants")
