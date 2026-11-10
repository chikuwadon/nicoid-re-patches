#!/usr/bin/env python3
"""Guards against foreground ambient compositing and stale tablet insets."""
from pathlib import Path
r=Path(__file__).resolve().parents[2]
ambient=(r/"porting/revisions/v1.11.1-dev.6/java/e/e/a/AmbientLight.java").read_text()
viewport=(r/"extensions/extension/src/main/java/e/e/a/FullscreenViewport209.java").read_text()
dev=(r/"porting/revisions/v1.11.1-dev.6/java/e/e/a/Dev115.java").read_text()
assert "host.getOverlay().add(glow)" not in ambient
assert "installed.getOverlay().remove(glow)" not in ambient
for token in ("commentlay","LayerDrawable","target.setBackground(background)","target.setPadding","void remove()","perimeterGlow","installPerimeter(target)","removePerimeter()"):
 assert token in ambient,token
for token in ("restoreNormalViewport","viewportSessions.remove(root)","int desired=original+Math.max(0,status-origin)"):
 assert token in viewport,token
# A full-sized normal video must not suppress the visible inner glow.
assert "glow.inner = mode == POPUP;" in ambient
assert "glow.spread = dp(host, mode == POPUP ?" in ambient
assert "if (mode == POPUP) return root" not in ambient
assert "if (mode == NORMAL && frame.left <= 0" not in ambient
assert "if (mode != FULL || host == target" in ambient
assert "perimeterGlow.corners = true;" in ambient
assert "int id = root.getResources().getIdentifier(\"commentlay\"" in ambient
# The normal halo must be a native player child between video and comments:
# no foreground window overlays, no glyph-rectangle cutouts, no right side panel.
for token in ('"videoLayout"', "View videoChild = childUnder(container, video)",
              "View commentsChild = childUnder(container, target)",
              "container.indexOfChild(videoChild) >= container.indexOfChild(commentsChild)",
              "container.addView(view, container.indexOfChild(commentsChild)",
              "normalGlowView.invalidate()", "perimeterGlow.corners = true;",
              "void drawCorners(Canvas c)", "new RadialGradient(", "if (x1 <= x0 && y1 > y0)",
              "final boolean next = !isSelected()", "current.scheduleRefresh()",
              "MAIN.post(queuedRefresh)", "MAIN.removeCallbacks(queuedRefresh)"):
 assert token in ambient, token
for forbidden in ("decor.getOverlay().add(perimeterGlow)",
                  "normalOverlayHost", "protectGlyphs(", "foregroundRoot",
                  "Region.Op.DIFFERENCE", "glow.inner = mode != FULL;"):
 assert forbidden not in ambient, forbidden
assert 'getMethod("restoreNormalViewport",View.class)' in dev
print("PASS: ambient comment/control layering and tablet status bar recovery")
