#!/usr/bin/env python3
"""Source guards for ambient layering, safe gutter fade and tablet recovery."""
from pathlib import Path
root=Path(__file__).resolve().parents[2]
ambient=(root/"porting/revisions/v1.11.1-dev.7/java/e/e/a/AmbientLight.java").read_text()
viewport=(root/"extensions/extension/src/main/java/e/e/a/FullscreenViewport209.java").read_text()
dev=(root/"porting/revisions/v1.11.1-dev.7/java/e/e/a/Dev115.java").read_text()

for token in ('"commentlay"', "LayerDrawable", "glow.inner = mode == POPUP;",
              "installNormalExterior(target)", "decor.getOverlay().add(perimeterGlow)",
              "normalOverlayHost.getOverlay().remove(perimeterGlow)",
              "normalEdgeLimits[0]", "normalEdgeLimits[1]",
              "normalEdgeLimits[2]", "normalEdgeLimits[3]",
              "perimeterGlow.edgeLimits = normalEdgeLimits;",
              "perimeterGlow.edgeLimits = null;",
              "int depthLimit = edgeLimits == null ? spread : Math.min(spread, edgeLimits[edge]);",
              "new RadialGradient(0, 0, 1f,", "c.scale(xRadius, yRadius)",
              "final boolean next = !isSelected()", "current.scheduleRefresh()",
              "MAIN.post(queuedRefresh)", "MAIN.removeCallbacks(queuedRefresh)"):
    assert token in ambient, token
for forbidden in ("normalGlowView", "normalGlowHost", "protectGlyphs(",
                  "getTextPath(", "Region.Op.DIFFERENCE", "new GlowSurface"):
    assert forbidden not in ambient, forbidden
for token in ("restoreNormalViewport", "viewportSessions.remove(root)",
              "int desired=original+Math.max(0,status-origin)"):
    assert token in viewport, token
assert 'getMethod("restoreNormalViewport",View.class)' in dev

# The coordinate cap can only draw in available exterior space.
# In split landscape, top/bottom and the right-side gutter all emit light;
# in portrait, an edge touching the device border cannot spill outside it.
def caps(w,h,video,density,split, insets=(0,0,0,0)):
    left,top,right,bottom=video
    il,it,ir,ib=insets
    top_limit=round((24 if split else 12)*density)
    bottom_limit=round((20 if split else 9)*density)
    side_limit=round((8 if split else 10)*density)
    return [max(0,min(top_limit,top-it)),
            max(0,min(bottom_limit,h-ib-bottom)),
            max(0,min(side_limit,left-il)),
            max(0,min(side_limit,w-ir-right))]
landscape=caps(1536,704,(0,134,658,504),2.0,True)
assert landscape[0]>0 and landscape[1]>0 and landscape[3]>0
assert landscape[2]==0
portrait=caps(760,1470,(0,0,760,428),2.0,False)
assert portrait[0]==0 and portrait[3]==0 and portrait[1]>0
assert all(n>=0 for n in caps(760,1470,(0,0,760,428),2.0,False))
print("PASS: normal ambient safe exterior gutters, fade caps, and tablet recovery")
