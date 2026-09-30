# nicoid Mod Patches

Morphe patches for nicoid 6.49.
Input: `com.sauzask.nicoid` / 6.49. Output: `com.sauzask.nicoid.hls` / 6.49, display name `nicoid mod`.
Stable patch version: `1.00`, displayed below the app version in Settings. The SemVer bundle/release tag is `1.0.0`.
Android versionCode advances for updates; the displayed app versionName remains `6.49`.
Changing versionName does not make a modified APK a supported input: the original APK is still required.
Original APK SHA-256: `17fc6b46228af184437ade7e6f5573915bc655b86996307ff3270fdf35279cce`.
The patch verifies the original DEX, manifest and resource table before writing.

## Patch releases

<!-- PATCHES_START EXPANDED -->
> **[v1.0.0-dev.1](https://github.com/chikuwadon/nicoid-mod-patches/releases/tag/v1.0.0-dev.1)**&nbsp;&nbsp;•&nbsp;&nbsp;`dev`&nbsp;&nbsp;•&nbsp;&nbsp;1 patches total
<details open>
<summary>📦 nicoid&nbsp;&nbsp;•&nbsp;&nbsp;1 patch</summary>
<br>

**🎯 Supported versions:**

| 6.49 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [nicoid Mod](#nicoid-mod) | Patch 0.91 (development): pinch-resizable popup, modern playback, bold right-aligned controls, readable themes, resolution labels and disabled advertising. App version remains 6.49. |  |

</details>

<!-- PATCHES_END -->

## Changes

- Two-finger popup pinch resize: preserves aspect ratio, clamps size/position to screen bounds,
  cancels child button/seek actions and suppresses remaining finger events until release.
  Existing single-finger movement and corner drag resize are retained; resized bounds are saved.
- Fix popup-to-normal comment renderer transfer crash.
- Five comment sizes: 60%, 80%, 100%, 120%, 140%; normal and popup playback. Applied on next playback.
- Move Google Cast connect/disconnect below comment settings; remove menu/sidebar entries.
- Popup controller row: quality, speed (0.75/1/1.25/1.5/2x), loop.
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

