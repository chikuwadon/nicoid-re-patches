"""Add the Media3 playback bridge to the supported APK's existing method delta."""
from pathlib import Path
import hashlib
import os
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parent
DELEGATE = "Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;"


def verify_original(apk_path, stable_path, destination):
    with ZipFile(stable_path) as base, ZipFile(apk_path) as apk:
        expected = dict(line.split()[::-1] for line in base.read("nicoid/input-hashes.txt").decode().splitlines())
        native = apk.read("classes.dex")
        if hashlib.sha256(native).hexdigest() != expected["classes.dex"]:
            raise ValueError("Use the original supported nicoid 6.49 APK")
        destination.write_bytes(native)


def java_classpath(tools):
    return os.pathsep.join(map(str, [*sorted((tools / "assemble").glob("*.jar")),
                                   tools / "dexlib2.jar", tools / "guava.jar"]))


def build_delta(run, base, work, tools, output, apk):
    classpath = java_classpath(tools)
    delta = work / "method-delta.dex"
    with ZipFile(base) as archive:
        delta.write_bytes(archive.read("nicoid/method-delta.dex"))
    smali = work / "method-smali"
    run("java", "-cp", classpath, "org.jf.baksmali.Main", "disassemble", delta, "-o", smali)

    delegate = smali / (DELEGATE[1:-1] + ".smali")
    if delegate.exists():
        raise ValueError("Baseline already overrides the video delegate")
    delegate.parent.mkdir(parents=True, exist_ok=True)
    delegate.write_text((ROOT / "ExoVideoDelegate.smali").read_text(encoding="utf-8"), encoding="utf-8")

    error_callback = smali / "com/sauzask/nicoid/NicoidVideoFragment$k.smali"
    source = error_callback.read_text(encoding="utf-8")
    start = "    invoke-virtual {p1}, Ljava/lang/Exception;->printStackTrace()V"
    end = "    invoke-static {v3}, Le/e/a/ModernDebug;->record(Ljava/lang/String;)V"
    if source.count(start) != 2 or source.count(end) != 1 or "Le/d/b/a/p0/v$e;" not in source:
        raise ValueError("Unexpected legacy video error handler")
    before, tail = source.split(start, 1)
    _, after = tail.split(end, 1)
    source = (before + "    invoke-static {p1}, Le/e/a/Media3VideoPlayer;->describeError(Ljava/lang/Exception;)Ljava/lang/String;\n"
              "    move-result-object v3\n\n" + end + after)
    raw_log = "    invoke-static {v1, v3, p1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I"
    if source.count(raw_log) != 1:
        raise ValueError("Unexpected video error log")
    source = source.replace(raw_log, "    invoke-static {v1, v3}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I")
    error_callback.write_text(source, encoding="utf-8")

    for filename, listener, seekbar, callback, owner, player_field in (
        ("NicoidVideoFragment", "l1", "v1", "v2", "p0", "a0"),
        ("NicoidPopupViewService", "o0", "v2", "v3", "p0", "e"),
    ):
        path = smali / "com/sauzask/nicoid" / (filename + ".smali")
        source = path.read_text(encoding="utf-8")
        owner_type = f"Lcom/sauzask/nicoid/{filename};"
        view_type = "Lcom/devbrackets/android/exomedia/ui/widget/VideoView;"
        callback_type = "Landroid/widget/SeekBar$OnSeekBarChangeListener;"
        anchor = (f"    invoke-direct {{{callback}, {owner}}}, Le/e/a/{listener};-><init>({owner_type})V\n\n"
                  f"    invoke-virtual {{{seekbar}, {callback}}}, "
                  f"Landroid/widget/SeekBar;->setOnSeekBarChangeListener({callback_type})V")
        if source.count(anchor) != 1:
            raise ValueError(f"Unexpected seek bar listener in {filename}")
        replacement = (anchor.split("\n", 1)[0] + "\n\n"
                       f"    iget-object v4, {owner}, {owner_type}->{player_field}:{view_type}\n\n"
                       f"    invoke-static {{{seekbar}, {callback}, v4}}, Le/e/a/Media3SeekPreview;"
                       f"->attach(Landroid/widget/SeekBar;{callback_type}Landroid/view/View;)V")
        path.write_text(source.replace(anchor, replacement), encoding="utf-8")

    run("java", "-cp", classpath, "org.jf.smali.Main", "assemble", "--api", 28, smali, "-o", output)
    if apk is not None:
        native = work / "original.dex"
        verify_original(apk, base, native)
        run("java", "-jar", tools / "ecj.jar", "-8", "-proc:none", "-cp", classpath, "-d", work,
            ROOT / "VerifyVideoHooks.java")
        run("java", "-cp", str(work) + os.pathsep + classpath, "VerifyVideoHooks", native, output)
