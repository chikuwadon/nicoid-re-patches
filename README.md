# nicoid Re / nicoid Mod Patches

nicoid 6.49を「nicoid Re」として改修する、コミュニティ開発のMorpheパッチです。動画再生機能とAndroid互換性の改善を目的としています。
Community-maintained Morphe patches that modernize nicoid 6.49 as **nicoid Re**, improving video playback and Android compatibility.

## 日本語

- アプリ名は **nicoid Re**、Androidの表示バージョンは **6.49** 固定です。変更はパッチの版数で管理します。
- 関連動画の選択メニューには「動画再生」「動画情報」「ポップアップ再生」「バックグラウンド再生」「キャッシュの取得」を表示します。動画を直接タップしたときの既定動作は維持します。
- Material Youに対応した速度・画質選択、9段階の再生速度、デフォルト速度設定、任意の再生位置保存に対応します。
- アプリ切替時・戻る操作時の動作を設定できます。初期値「何もしない」は従来の停止・画面終了動作を維持します。再生位置保存の初期値はOFFです。
- ポップアップのピンチ拡大縮小、コメントサイズ設定、画質変更時の位置・一時停止状態の復元、イヤホン切断時の一時停止に対応します。
- 正式版は **v1.0.0**、開発・検証版は **dev** で管理します。実機での確認結果を確認するまで正式版に昇格しません。
- 正確な元APKが必要です。元APK、実機ログ、署名鍵をこのリポジトリにアップロードしないでください。

以下に英語の仕様、変更内容、ビルド方法、検証上の注意を記載します。
The English specification, change list, build instructions, and validation notes follow.

## English

Morphe patches for nicoid 6.49.
Input: `com.sauzask.nicoid` / 6.49. Output: `com.sauzask.nicoid.hls` / 6.49, display name `nicoid Re`.
Stable release remains `1.0.0` (app display `1.00`). This development candidate is `1.1.0-dev.2` (app display `1.10-dev.2`).
Android versionCode advances for updates; the displayed app versionName remains `6.49`.
Changing versionName does not make a modified APK a supported input: the original APK is still required.
Original APK SHA-256: `17fc6b46228af184437ade7e6f5573915bc655b86996307ff3270fdf35279cce`.
The patch verifies the original DEX, manifest and resource table before writing.

## Patch releases

<!-- PATCHES_START EXPANDED -->
> **[v1.1.0-dev.2](https://github.com/chikuwadon/nicoid-mod-patches/releases/tag/v1.1.0-dev.2)**&nbsp;&nbsp;•&nbsp;&nbsp;`dev`&nbsp;&nbsp;•&nbsp;&nbsp;1 patches total
<details open>
<summary>📦 nicoid&nbsp;&nbsp;•&nbsp;&nbsp;1 patch</summary>
<br>

**🎯 Supported versions:**

| 6.49 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [nicoid Mod](#nicoid-mod) | nicoid Re 検証版 v1.10-dev.2：関連動画から通常・ポップアップ・バックグラウンド再生を選択。表示バージョンは6.49固定。 / nicoid Re dev patch v1.10-dev.2: select normal, popup or background playback from related videos. App version remains 6.49. |  |

</details>

<!-- PATCHES_END -->

## Changes

- Two-finger popup pinch resize: preserves aspect ratio, clamps size/position to screen bounds,
  cancels child button/seek actions and suppresses remaining finger events until release.
  Existing single-finger movement and corner drag resize are retained; resized bounds are saved.
- Fix popup-to-normal comment renderer transfer crash.
- Five comment sizes: 60%, 80%, 100%, 120%, 140%; normal and popup playback. Applied on next playback.
- Move Google Cast connect/disconnect below comment settings; remove menu/sidebar entries.
- Popup controller row: quality, nine speeds (0.5/0.75/1/1.15/1.25/1.4/1.5/1.75/2x), loop.
- Themed speed and quality dialogs in both players, including dynamic-color popup dialogs.
- Repair the information-only activity layout after removing the advertising anchor; accept numeric and string metadata versions.
- Related-video menus always expose normal, background and popup playback alongside information/cache actions. Default tap behavior is unchanged.
- Player settings: default speed, app-switch and Back playback policies, optional per-video playback-position saving.
  Policy `none` retains existing behavior; background/popup policies apply to active normal playback.
  Position saves every five seconds and at lifecycle boundaries. Near-complete videos restart from zero.
- Pause normal/popup/background playback on `AUDIO_BECOMING_NOISY`; suppress automatic popup retries after unplugging.
- Restore position and playing/paused state after quality changes.
- Fix light-mode uploader/action surfaces and comment-list text, including system dark mode with the app's light theme.
- Place compact popup speed, quality and loop controls at the top, leaving the bottom seek bar unobstructed.
- Right-align popup controls; use 14sp bold text and 56x44dp hit areas for both popup and normal text controls.
- Show available video resolutions in player quality settings after playback metadata is loaded; otherwise describe video-dependent resolution levels.
- Correct the high-quality preference mapping to the highest available stream.
- Remove advertising startup components and ad-removal billing screen registration; disable banner creation and remaining ad requests.
- Advertising SDK classes remain inert for binary compatibility; this is not a claim that every SDK byte was removed.

## Build

Run `./gradlew :patches:buildAndroid --no-daemon`.
Output: `patches/build/libs/*.mpp`.
Local builds require GitHub Packages read authentication for Morphe.
The Private patch build on `dev` stores the bundle as a private Actions artifact for seven days.
Original APK, device log and signing keys are excluded.

The host build and policy tests do not prove device behavior. Verify information-only display,
light/dark dynamic dialogs, all speeds, both mode-switch policies, saved positions, and wired/Bluetooth disconnects on Android before promoting this candidate.

## Porting

`method-delta.dex` contains changed methods only; `helpers.mpe` contains added helper classes only.
The patch edits class members through Morphe APIs and does not replace the whole application DEX.
`resources.zip` contains compiled resource differences locked to the exact original input.

## Validation

Modified smali/resources compiled successfully with Apktool 2.11.1.
Additional controls compiled with the Android SDK.
Morphe bundle build/application and physical-device verification are recorded separately.
Verify popup -> normal -> popup, all comment sizes, Cast connect/disconnect, quality position restoration,
paused-state restoration, speed and repeat on a physical device.

## License

Template-derived code follows [GPLv3](LICENSE) and [NOTICE](NOTICE).


