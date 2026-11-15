"""v2.0.5-dev.14: targeted native settings and ambient header overlays."""
from pathlib import Path
import importlib.util
ROOT=Path(__file__).resolve().parent
PREVIOUS=ROOT.parent/"v2.0.5-dev.13/patch_234.py"
spec=importlib.util.spec_from_file_location("_nicoid_dev13",PREVIOUS)
previous=importlib.util.module_from_spec(spec)
spec.loader.exec_module(previous)
OVERRIDES=frozenset(set(previous.OVERRIDES)|{"Dev111.java","AmbientLight.java"})
def once(text,old,new,tag):
    if text.count(old)!=1:raise AssertionError(tag+": expected exactly one native source anchor")
    return text.replace(old,new)
def stage(base,destination):
    result=previous.stage(base,destination)
    destination=Path(destination)
    stable=ROOT.parent/"v1.11.1/java/e/e/a"
    original=(stable/"Dev111.java").read_text(encoding="utf-8")
    updated=once(original,
      'list.setEnabled("background".equals(shared.getString("app_switch_playback", "none")));',
      'list.setEnabled("background".equals(shared.getString("app_switch_playback", "none")) || "popup".equals(shared.getString("app_switch_playback", "none")));',
      "Allow #139 popup return preference in legacy summary updater")
    (destination/"Dev111.java").write_text(updated,encoding="utf-8")
    original=(stable/"AmbientLight.java").read_text(encoding="utf-8")
    updated=once(original,
      'detailHeader.update(content == null ? null : findId(content, "buttonlay"), page, pageGlow);',
      '''// The comment sorting/follow toolbar is above the ListView and has
            // its own opaque themed background. Extend the ambient light
            // into that background, never on top of text or controls.
            View commentBar = page.findViewById(0xfa1731);
            detailHeader.update(content == null ? commentBar : findId(content, "buttonlay"), page, pageGlow);''',
      "Extend ambient into comment control bar")
    (destination/"AmbientLight.java").write_text(updated,encoding="utf-8")
    return result
