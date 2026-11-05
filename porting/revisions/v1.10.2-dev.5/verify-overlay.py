#!/usr/bin/env python3
"""Fail fast on missing development features and incomplete payload assembly."""
import pathlib
import sys
import hashlib

root = pathlib.Path(sys.argv[1])
output = pathlib.Path(sys.argv[2])
helper = root / "helpers/e/e/a"
expected = {
    "AppSwitchGuard$1.smali": ["Dev111;->left()V"],
    "AppSwitchGuard$2.smali": ["Dev111;->resumed(Landroid/app/Activity;)V"],
    "SettingsPlacement233.smali": ["Dev111;->settings(Landroid/preference/PreferenceActivity;)V"],
    "SettingsTools.smali": ["Dev111;->settings(Landroid/preference/PreferenceActivity;)V"],
    "MultiTabs227.smali": ["Dev111;->loadedTabs(Ljava/lang/Object;Landroid/content/Context;)V"],
    "MultiTabs227$Host.smali": ["Dev111;->initialTab(Ljava/lang/Object;)V", "Dev111;->pageResumed(Landroid/app/Activity;)V"],
    "MultiTabs227$1.smali": ["Dev111;->capturePage(Landroid/app/Activity;)V"],
    "MultiTabs227$Host$1.smali": ["Dev112;->restoreList(Landroid/widget/ListView;II)V"],
    "GestureControls110.smali": ["Dev112;->verticalTurnReady(Ljava/lang/Object;Landroid/view/MotionEvent;F)Z"],
    "ListActions.smali": ["Dev116;->playlistMenu(Landroid/app/Activity;Ljava/lang/Object;Ljava/lang/Object;Landroid/view/Menu;Ljava/util/List;)V"],
    "PatchVersion237.smali": ["1.10.2-dev.5"],
    "ModernShorts.smali": ["v1.10.2-dev.5 @chikuwadon"],
    "TabRoutes228.smali": [
        "Dev111;->capturePage(Landroid/app/Activity;)V",
        "Dev111;->preparePlaylist(Landroid/app/Activity;)V",
        "Dev111;->enteredPage(Landroid/app/Activity;)V",
    ],
    "LocalPlaylists.smali": ["Dev111;->preparePlaylist(Landroid/app/Activity;)V", "Dev111;->playlistsChanged()V"],
    "LocalPlaylists$Page.smali": ["LocalPlaylistData;->toString()Ljava/lang/String;", "Dev115;->thumbnail(Lorg/json/JSONObject;)Ljava/lang/String;"],
    "FullscreenViewport209.smali": ["Dev115;->afterFullscreenRestore(Landroid/view/View;)V"],
    "Dev111.smali": ["nicoid_restore_tab_scroll", "nicoid_app_resume_playback", "nicoid_tab_scroll111_"],
    "Dev112.smali": ["nicoid_confirm_exit", "verticalTurnReady", "playlistMenu"],
    "StorageDiagnostics112.smali": ["nicoid_storage_diagnostic", ".method public static install(Landroid/preference/PreferenceActivity;)V"],
    "Dev115.smali": ["cache_all_delete", "seamlessReturn", "afterFullscreenRestore", "thumbnail"],
    "Dev116.smali": ["playlistMenu", "playlistThumbnail", "mp4_tree_uri", "show_watch_progress", "drawer"],
    "LocalPlaylists$Page$2.smali": ["Dev116;->playlistThumbnail(Landroid/view/View;Landroid/widget/BaseAdapter;Lorg/json/JSONObject;)V"],
    "LegacyCacheCleanup113.smali": ["nicoid_old_cache_delete", "cache_all_delete", "nicoid/nicoid_cache"],
}
for name, needles in expected.items():
    text = (helper / name).read_text(encoding="utf-8")
    for needle in needles:
        assert needle in text, f"Missing {needle} in {name}"
native = (root / "method-delta/com/sauzask/nicoid/NicoidActivity.smali").read_text(encoding="utf-8")
assert "Dev112;->back(Landroid/app/Activity;)Z" in native
video_list=(root / "method-delta/com/sauzask/nicoid/NicoidVideoListActivity.smali").read_text(encoding="utf-8")
assert "Dev116;->drawer(Landroid/app/Activity;Landroid/view/MenuItem;)Z" in video_list
for file in ["helpers.mpe", "method-delta.dex"]:
    data = (output / file).read_bytes()
    assert data[:4] == bytes((100, 101, 120, 10)), f"Invalid DEX magic: {file}"
    assert len(data) > 1000, f"Unexpected empty payload: {file}"
    print(f"{file}: {len(data)} bytes / sha256={hashlib.sha256(data).hexdigest()}")
print("Development overlay hooks, settings, playlist refresh and DEX output: PASS")
