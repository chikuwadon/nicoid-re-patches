"""Build a Media3 verification MPP with the v2.0.2 source baseline."""
import argparse
import hashlib
import importlib.util
import os
from pathlib import Path
import shutil
import sys
from tempfile import TemporaryDirectory

VERSION = "2.0.3-media3.3"
DEVELOPMENT_REF = "refs/heads/feature/media3-migration"
ROOT = Path(__file__).resolve().parent
BASELINE = ROOT.parent / "v2.0.2-dev.4"

sys.path.insert(0, str(BASELINE))
spec = importlib.util.spec_from_file_location("nicoid_v202_builder", BASELINE / "build_mpp.py")
baseline = importlib.util.module_from_spec(spec)
spec.loader.exec_module(baseline)
from patch_202 import once
from video_delta import build_delta


def stage(base, destination):
    baseline.stage(base, destination)
    service = destination / "NicoliveService.java"
    source = service.read_text(encoding="utf-8")
    source = once(
        source, " final Timeline.Window window=new Timeline.Window();",
        " private final LiveWindowRecovery liveWindowRecovery=new LiveWindowRecovery();\n"
        " final Timeline.Window window=new Timeline.Window();", "live-window recovery lifecycle")
    source = once(
        source, "  long now=android.os.SystemClock.elapsedRealtime();",
        """  long now=android.os.SystemClock.elapsedRealtime();
  if(error.errorCode==PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW
     &&player!=null&&player.getPlayWhenReady()&&player.getCurrentMediaItem()!=null
     &&session!=null&&!terminal&&!userPaused&&!stopped
     &&liveWindowRecovery.claimAttempt(now)){
   NicoliveDiagnostics.record("PLAYER_RECOVERY","seek-to-live-default");
   scheduled.clear();
   if(frame!=null)frame.comments.clear();
   player.seekToDefaultPosition();
   player.prepare();
   notifyObservers();
   return;
  }""", "live-window error handler")
    source = once(
        source, 'NicoliveDiagnostics.record("WATCH","begin-new-session");',
        'liveWindowRecovery.reset();NicoliveDiagnostics.record("WATCH","begin-new-session");',
        "live-window retry reset")
    service.write_text(source, encoding="utf-8")
    for name in ("LiveWindowRecovery", "Media3VideoPlayer", "VideoHttpSource", "Dev139", "Media3SeekPreview"):
        shutil.copyfile(ROOT / f"{name}.java", destination / f"{name}.java")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    for flag in ("base", "tools", "media", "output"):
        parser.add_argument("--" + flag, type=Path, required=True)
    parser.add_argument("--apk", type=Path)
    parser.add_argument("--ci-without-apk", action="store_true")
    parser.add_argument("--compile-jar", action="append", type=Path, default=[])
    args = parser.parse_args()
    args.official_release = False
    if args.apk is None and (not args.ci_without_apk or len(args.compile_jar) < 2):
        parser.error("Use the original nicoid 6.49 APK or the pinned CI compiler JARs")
    if args.apk is not None and args.ci_without_apk:
        parser.error("Choose APK or CI compilation")
    if args.output.resolve() == args.base.resolve():
        parser.error("Choose a separate output path")
    if hashlib.sha256(args.base.read_bytes()).hexdigest() != baseline.BASE_HASH:
        parser.error("Expected the official v1.11.1 MPP used to rebuild v2.0.2")
    if args.apk is None and (os.environ.get("GITHUB_ACTIONS") != "true"
                            or os.environ.get("GITHUB_REF") != DEVELOPMENT_REF):
        parser.error("CI-only compiler JARs require the Media3 feature branch workflow")
    with TemporaryDirectory(prefix="nicoid-media3-") as folder:
        work = Path(folder)
        method_delta = work / "media3-method-delta.dex"
        build_delta(baseline.run, args.base, work, args.tools.resolve(), method_delta, args.apk)
        extra_compile_jars = []
        if args.apk is None:
            api = work / "native-api"
            api.mkdir()
            baseline.run("javac", "--release", "8", "-d", api, *sorted((ROOT / "api").glob("*.java")))
            api_jar = work / "native-api.jar"
            baseline.run("jar", "cf", api_jar, "-C", api, ".")
            extra_compile_jars.append(api_jar)
        baseline.build(args, work, stage_sources=stage, version=VERSION,
                       development_ref=DEVELOPMENT_REF, extra_compile_jars=extra_compile_jars,
                       method_delta=method_delta)


if __name__ == "__main__":
    main()

