"""Verify the installed Media3 helpers and native hooks in the candidate MPP."""
from pathlib import Path
import runpy
import sys
from zipfile import ZipFile

from build_mpp import BASELINE, VERSION

base, candidate = sys.argv[1:3]
verifier = BASELINE / "verify_ci_candidate.py"
sys.argv = [str(verifier), base, candidate, VERSION, "--media3-video"]
namespace = runpy.run_path(str(verifier), run_name="__main__")
with ZipFile(Path(candidate)) as archive:
    helpers = archive.read("nicoid/helpers.mpe")
    classes = namespace["dex_classes"](helpers)
    delta = archive.read("nicoid/method-delta.dex")
    method_classes = namespace["dex_classes"](delta)
    assert "Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;" in method_classes
    assert b"Le/e/a/Media3VideoPlayer;" in delta
    assert b"Le/e/a/Media3SeekPreview;" in delta
    assert b"media3Player" in helpers
    assert b"Le/e/a/Media3PlaybackHandoff;" in delta
    assert "Lcom/sauzask/nicoid/NicoidPopupViewService;" in method_classes
    assert b"addPopupWindow\0" in delta
    assert b"bindReturnedView\0" in helpers
    assert VERSION.encode() + b"\0" in helpers, "Missing installed patch version"
    assert b"2.0.2-dev.4\0" not in helpers, "Stale installed patch version"
    for name in ("LiveWindowRecovery", "Media3VideoPlayer", "VideoHttpSource", "Dev139",
                 "Media3SeekPreview", "Media3ControlStyle", "Media3PlaybackHandoff"):
        assert "Le/e/a/" + name + ";" in classes, "Missing Media3 helper: " + name
    assert "Lcom/devbrackets/android/exomedia/core/ListenerMux;" not in classes, "Packaged compiler stub"
    assert "Landroidx/media3/exoplayer/ExoPlayer;" in classes, "Missing Media3 engine"
    print("PASS: Media3 playback bridge and native method overrides in MPP")

