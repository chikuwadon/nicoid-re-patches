#!/usr/bin/env python3
"""Source-level regression contracts for the current v2.0.0-dev candidate.

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
assert 'mutateReservation(id,cookie,"DELETE",' in b
assert 'mutateReservation(id,cookie,"POST",' in b
assert 'request.setRequestMethod(method)' in b
assert 'reservationWork.execute' in b
assert 'reservationWork.shutdown()' in b
assert 'timeshift/reservations?programIds=' in b
assert 'if(status>=200&&status<300){refresh();result.done("");}' in b
assert 'NicoliveData.accessLabel(p)' in src["NicoliveData"]
assert 'static String accessLabel(JSONObject info)' in src["NicoliveData"]
assert "if(!program.canWatch)" not in a, "do not preemptively block the live listing"
build=(R/"build_mpp.py").read_text()
version=next((line.split('"')[1] for line in build.splitlines()
              if line.startswith('VERSION = "')), None)
assert version and version.startswith("2.0.0-dev.1-"), "unexpected development version"
assert 'f"Version: {version}' in (R/"verify_mpp.py").read_text()
corners=(R/"java/e/e/a/ThumbnailCorners.java").read_text()
entry=(R/"java/e/e/a/NicoliveEntry.java").read_text()
for marker in ('rounded.setDefaultValue(true)', 'OriginalOutline original = ORIGINALS.remove(view)',
               'view.setClipToOutline(original.clip)', 'applyFrame(View frame)'):
    assert marker in corners, marker
assert 'ThumbnailCorners.settings(activity, other)' in entry
assert 'ThumbnailCorners.applyFrame(artwork)' in a
assert 'applyWithParent(View thumbnail)' in corners
assert 'applyDetails(View root)' in corners
assert 'appの再起動' not in corners
assert '変更はアプリの再起動後に反映されます' in corners
assert '->applyDetails(Landroid/view/View;)V' in build
assert 'Missing playback mini-thumbnail' in build
assert 'Missing exit method:' in build
assert 'ExitModeOptions.mode(this,ExitModeOptions.LIVE_KEY)' in a
assert 'ExitModeOptions.doubleBack(this,' in a
exit_modes = (R/"java/e/e/a/ExitModeOptions.java").read_text()
assert 'public static final int DOUBLE_BACK = 2' in exit_modes
assert 'nicoid_exit_mode' in exit_modes and 'nicolive_exit_mode' in exit_modes
assert 'ExitBackPolicy.isSecondPress(first, now)' in exit_modes
assert 'exitSummary' not in a, 'NicoLive exit mode should not display a separate description'
local=(R/"java/e/e/a/LocalPlaylists.java").read_text()
merge=(R/"MergeLiveDex.java").read_text()
for marker in ('カスタム順','static void reorderFolder(','void installDrag(ListView list)',
               'startDragAndDrop(','reordering?2:selecting?1:0',
               'if(sort!=3)Collections.sort(visible'):
    assert marker in local, marker
assert 'livePlaylist.contains(type)' in merge
assert 'Le/e/a/LocalPlaylists$Page;' in merge
assert 'nicoid_local_playlist_sort_' in local
assert 'static void bindArtwork(View row,BaseAdapter adapter,JSONObject video)' in local
assert 'Dev116.playlistThumbnail(row,adapter,video);' in local
assert 'if(!selecting&&!reordering){ListActions.bind(row,nativeRows,n);bindArtwork(row,display,video);return row;}' in local
assert 'bindArtwork(row,display,video);' in local
assert 'image.setImageDrawable(null);' not in local
assert 'ThumbnailCorners.load(url,image,null)' not in local
assert 'private static final String LAST_FOLDER=' in local
assert 'private static final String LAST_GROUP=' in local
assert 'saved.getString(LAST_FOLDER,null)' in local
assert 'group=saved.getString(LAST_GROUP,null)' in local
assert 'void saveLocation()' in local
assert 'saveLocation();' in local
assert 'if(folder.isEmpty())group="@unfiled";' in local
assert 'group=parent.isEmpty()?"@unfiled":parent;' in local
assert 'public static boolean home(Activity a,MenuItem item){return Dev116.drawer(a,item);}' in local
assert 'item.getItemId()==android.R.id.home&&back(a)' not in local
service=(R/"java/e/e/a/NicoliveService.java").read_text()
policy=(R/"java/e/e/a/LiveRecoveryPolicy.java").read_text()
assert 'handlePlaybackError(error);' in service
assert 'LiveRecoveryPolicy.isAes128Failure(error)' in service
assert 'session.reconnect();' in service
assert 'cryptoRecoveryCount<LiveRecoveryPolicy.MAX_ATTEMPTS' in service
assert 'cryptoRecoveryPending=false;session.reconnect();' in service
assert 'instanceof BadPaddingException' in policy
assert 'MAX_ATTEMPTS=3' in policy
artwork=(R/"java/e/e/a/NicoliveArtwork.java").read_text()
assert 'NicoliveArtwork.isTrusted(p.thumbnail)' in service
assert 'NicoliveArtwork.fetch(url)' in service
assert 'mediaArtwork=bitmap;' in service
assert '.setLargeIcon(mediaArtwork)' in service
assert 'MediaMetadata.METADATA_KEY_ALBUM_ART,mediaArtwork' in service
assert 'MediaMetadata.METADATA_KEY_DISPLAY_ICON,mediaArtwork' in service
assert 'PlaybackState.PLAYBACK_POSITION_UNKNOWN' in service
assert 'PlaybackState.STATE_ERROR' in service
assert 'Live HLS has no user-seekable duration' in service
assert 'host.endsWith(".nimg.jp")' in artwork
assert 'host.endsWith(".nicovideo.jp")' in artwork
assert 'connection.setInstanceFollowRedirects(false)' in artwork
assert 'out.size()+n>LIMIT' in artwork
print("PASS: live features, thumbnail artwork, media controls and manual playlist sorting contracts")
