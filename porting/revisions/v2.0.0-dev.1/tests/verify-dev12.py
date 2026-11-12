#!/usr/bin/env python3
"""Source-level regression contracts for the local v2.0.0-dev.1-12 candidate.

Does not replace compilation of the Android UI/Media3 sources or an APK run.
"""
from pathlib import Path
R=Path(__file__).resolve().parents[1]
src={name:(R/"java/e/e/a"/(name+".java")).read_text(encoding="utf-8")
     for name in ("NicoliveUi","NicoliveActivity","NicoliveBrowser","NicoliveData","NicoliveCatalog")}
a=src["NicoliveActivity"]
for k in (
 "drawCircle(12,5,1.8f,p)", # checked in the custom glyph below
 "nicolive_tap_behavior",
 "new String[]{\"通常再生\",\"ポップアップ再生\"}",
 "openSelectedProgram(selected.id)",
 "openPopupFromHub(id)",
 "service.showPopup();if(service.popup!=null){",
 "confirmHistoryRemoval(program)",
 "confirmReservationRemoval(program)",
 "program.accessLabel()",
 "access.isEmpty()?View.GONE:View.VISIBLE",
 "if(service.program!=null&&!service.program.canWatch)showWatchRestriction(service.program)",
):
 if k.startswith("drawCircle"): assert k in src["NicoliveUi"],k
 else: assert k in a,k
assert "moveTaskToBack(true)" not in a
assert 'prefs().getInt(TAP_BEHAVIOR,0)==1' in a
assert '.putInt(TAP_BEHAVIOR,selections[2])' in a
assert 'preferences.edit().putString("nicolive_history",retained.toString()).commit()' in a
b=src["NicoliveBrowser"]
assert 'request.setRequestMethod("DELETE")' in b
assert 'timeshift/reservations?programIds=' in b
assert 'if(status>=200&&status<300){refresh();result.done("");}' in b
assert 'NicoliveData.accessLabel(p)' in src["NicoliveData"]
assert 'static String accessLabel(JSONObject info)' in src["NicoliveData"]
assert "if(!program.canWatch)" not in a, "do not preemptively block the live listing"
assert 'v2.0.0-dev.1-12' not in (R/"build_mpp.py").read_text().replace('VERSION = "2.0.0-dev.1-12"',''), "unexpected hardcoded version"
assert 'VERSION = "2.0.0-dev.1-12"' in (R/"build_mpp.py").read_text()
assert "2.0.0-dev.1-12" in (R/"verify_mpp.py").read_text()
print("PASS: 5 requested v2 features and pre-build risk checks")
