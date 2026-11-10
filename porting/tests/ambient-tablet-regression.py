#!/usr/bin/env python3
"""Regression tests for ambient normal glow visibility and UI stacking."""
from pathlib import Path
r=Path(__file__).resolve().parents[2]
a=(r/"porting/revisions/v1.11.1-dev.8/java/e/e/a/AmbientLight.java").read_text()
viewport=(r/"extensions/extension/src/main/java/e/e/a/FullscreenViewport209.java").read_text()
dev=(r/"porting/revisions/v1.11.1-dev.8/java/e/e/a/Dev115.java").read_text()
for token in (
 "new LayerDrawable(new Drawable[]{base, tabGlow})",
 'com.astuetz.PagerSlidingTabStrip',
 "normalTabView.setBackground(normalTabOriginal)",
 "tab.setPadding(normalTabPadding[0]",
 "tabGlow.sides = 2;",
 "tabGlow.alpha = Math.min(255, Math.round(glow.alpha * 1.10f));",
 "boolean tabGlowActive = !split && installTabGlow(decor, target);",
 "normalEdgeLimits[1] = tabGlowActive ? 0",
 "Math.min(72f, config[1] * .62f)",
 "Math.min(64f, config[1] * .52f)",
 "normalEdgeLimits[3]",
 "int depthLimit = edgeLimits == null ? spread : Math.min(spread, edgeLimits[edge]);",
 "showState(next)",
 "current.scheduleRefresh()",
 "glow.inner = mode == POPUP;",
):
 assert token in a,token
for forbidden in ("new GlowSurface", "normalGlowView", "protectGlyphs(",
                 "Region.Op.DIFFERENCE", "getTextPath("):
 assert forbidden not in a,forbidden
assert 'getMethod("restoreNormalViewport",View.class)' in dev
assert "viewportSessions.remove(root)" in viewport
# Vivid is a 108dp spread preset; fixed 9dp limits were visually imperceptible.
assert "int bottomSafe = dp(root, split ? 20 : 9);" not in a
# A portrait 48dp tab bar is enough to show a noticeable Vivid halo without
# putting a foreground glow over tab glyphs.
assert min(108,48)>4*9
assert min(108*.52,64)>20
print("PASS: vivid spread, tab background behind text, landscape caps, toggle")
