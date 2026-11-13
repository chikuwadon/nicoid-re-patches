"""Add the Media3 playback bridge to the supported APK's existing method delta."""
from pathlib import Path
import hashlib
import os
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parent
DELEGATE = "Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;"
VIDEO_VIEW = "Lcom/devbrackets/android/exomedia/ui/widget/VideoView;"
POPUP = "Lcom/sauzask/nicoid/NicoidPopupViewService;"
HANDOFF = "Le/e/a/Media3PlaybackHandoff;"


def replace_once(source, old, new):
    if source.count(old) != 1:
        raise ValueError(f"Expected one Media3 hook: {old!r}")
    return source.replace(old, new)


def patch_popup(path):
    source = path.read_text(encoding="utf-8")
    source = replace_once(
        source, "    invoke-virtual {p1}, Ljava/lang/Thread;->start()V",
        f"    invoke-static {{p0, p1}}, {HANDOFF}->adopt(Landroid/app/Service;Ljava/lang/Thread;)Z\n"
        "    move-result p2\n"
        "    if-eqz p2, :nicoid_popup_load_new_video\n"
        "    const/4 p2, 0x0\n"
        f"    invoke-virtual {{p0, p2}}, {POPUP}->c(Ljava/lang/String;)V\n"
        "    goto :cond_56e\n"
        "    :nicoid_popup_load_new_video\n"
        "    invoke-virtual {p1}, Ljava/lang/Thread;->start()V")
    setup = "    :goto_1e\n    iget-boolean v0, p0, " + POPUP + "->directPopup:Z"
    source = replace_once(
        source, setup,
        "    :goto_1e\n"
        f"    iget-object v0, p0, {POPUP}->e:{VIDEO_VIEW}\n"
        f"    invoke-static {{v0}}, {HANDOFF}->isBinding(Ljava/lang/Object;)Z\n"
        "    move-result v0\n"
        "    if-nez v0, :goto_6f\n"
        f"    iget-boolean v0, p0, {POPUP}->directPopup:Z")
    ready = "    invoke-static {p0}, Le/e/a/ModernEnhancements;->attach(Ljava/lang/Object;)V"
    source = replace_once(
        source, ready,
        ready + f"\n\n    invoke-static {{p0}}, {HANDOFF}->notifyRetained(Landroid/app/Service;)V")
    source = replace_once(
        source, "    invoke-interface {p2, p3, p1}, Landroid/view/WindowManager;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V",
        f"    invoke-static {{p0, p2, p3, p1}}, {HANDOFF}->addPopupWindow("
        "Landroid/app/Service;Landroid/view/WindowManager;Landroid/view/View;Landroid/view/WindowManager$LayoutParams;)V")
    initial_start = f"    invoke-virtual {{v0}}, {VIDEO_VIEW}->start()V\n\n    :cond_8d"
    source = replace_once(
        source, initial_start,
        f"    invoke-static {{v0}}, {HANDOFF}->startPopup(Landroid/view/View;)V\n\n    :cond_8d")
    start = "    invoke-static/range {p0 .. p1}, Le/e/a/DirectBackground235;->begin(Ljava/lang/Object;Landroid/content/Intent;)V"
    source = replace_once(
        source, start,
        f"    invoke-static/range {{p0 .. p1}}, {HANDOFF}->showPopup(Landroid/app/Service;Landroid/content/Intent;)Z\n"
        "    move-result v0\n"
        "    if-eqz v0, :nicoid_check_popup_request\n"
        "    const/4 v0, 0x2\n"
        "    return v0\n"
        "    :nicoid_check_popup_request\n"
        "    if-eqz p1, :nicoid_popup_native_start\n"
        "    invoke-virtual {p1}, Landroid/content/Intent;->getAction()Ljava/lang/String;\n"
        "    move-result-object v0\n"
        '    const-string v1, "com.sauzask.nicoid.MEDIA3_SHOW_POPUP"\n'
        "    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z\n"
        "    move-result v0\n"
        "    if-eqz v0, :nicoid_popup_native_start\n"
        "    const/4 v0, 0x2\n"
        "    return v0\n"
        "    :nicoid_popup_native_start\n" + start)
    path.write_text(source, encoding="utf-8")


def patch_popup_prepared(path):
    source = path.read_text(encoding="utf-8")
    anchor = f"    sget-boolean v0, {POPUP}->p0:Z\n\n    if-eqz v0, :cond_62"
    source = replace_once(
        source, anchor,
        f"    sget-boolean v0, {POPUP}->p0:Z\n"
        "    if-nez v0, :cond_5d\n"
        f"    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidPopupViewService$c;->a:{POPUP}\n"
        f"    iget-object v0, v0, {POPUP}->e:{VIDEO_VIEW}\n"
        f"    invoke-static {{v0}}, {HANDOFF}->isBinding(Ljava/lang/Object;)Z\n"
        "    move-result v0\n"
        "    if-eqz v0, :cond_62")
    path.write_text(source, encoding="utf-8")


def patch_helpers(stable, hooks):
    transfer = "PlaybackTransfer221.smali"
    source = (stable / "e/e/a" / transfer).read_text(encoding="utf-8")
    target = "    :goto_39\n    invoke-static {p0, p1}, Le/e/a/PlaybackTransfer221;->get(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;\n\n    move-result-object p0"
    source = replace_once(
        source, target,
        target + "\n\n"
        f"    invoke-static {{p0}}, {HANDOFF}->isBinding(Ljava/lang/Object;)Z\n"
        "    move-result p1\n"
        "    if-eqz p1, :nicoid_prepare_new_player\n"
        "    invoke-static {v1}, Le/e/a/PlaybackTransfer221;->complete(Le/e/a/PlaybackTransfer221$Transfer;)V\n"
        "    return-void\n"
        "    :nicoid_prepare_new_player")
    (hooks / transfer).write_text(source, encoding="utf-8")
    popup_return = "PopupReturn244.smali"
    source = (stable / "e/e/a" / popup_return).read_text(encoding="utf-8")
    anchor = ("    invoke-virtual {v0, v1, v3}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;\n\n"
              "    iget-object v0, p0, Le/e/a/PopupReturn244$Pending;->video:Landroid/view/View;")
    source = replace_once(
        source, anchor,
        f"    invoke-static {{p0, v0, v1, v3}}, {HANDOFF}->bindReturnedView("
        "Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;\n\n"
        "    iget-object v0, p0, Le/e/a/PopupReturn244$Pending;->video:Landroid/view/View;")
    (hooks / popup_return).write_text(source, encoding="utf-8")
    media_controls = "MediaControls.smali"
    source = (stable / "e/e/a" / media_controls).read_text(encoding="utf-8")
    anchor = "    new-instance v8, Landroid/app/Notification$MediaStyle;"
    source = replace_once(
        source, anchor,
        "    iget-object v8, v0, Le/e/a/MediaControls$State;->owner:Ljava/lang/Object;\n"
        f"    invoke-static {{v1, v8}}, {HANDOFF}->addPopupAction(Landroid/app/Notification$Builder;Ljava/lang/Object;)V\n\n"
        + anchor)
    (hooks / media_controls).write_text(source, encoding="utf-8")


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

    patch_popup(smali / "com/sauzask/nicoid/NicoidPopupViewService.smali")
    patch_popup_prepared(smali / "com/sauzask/nicoid/NicoidPopupViewService$c.smali")

    run("java", "-cp", classpath, "org.jf.smali.Main", "assemble", "--api", 28, smali, "-o", output)
    if apk is not None:
        native = work / "original.dex"
        verify_original(apk, base, native)
        run("java", "-jar", tools / "ecj.jar", "-8", "-proc:none", "-cp", classpath, "-d", work,
            ROOT / "VerifyVideoHooks.java")
        run("java", "-cp", str(work) + os.pathsep + classpath, "VerifyVideoHooks", native, output)
