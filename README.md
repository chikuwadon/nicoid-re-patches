# nicoid Mod Patches

Morphe patches for nicoid 6.49.
Input: `com.sauzask.nicoid` / 6.49. Output: `com.sauzask.nicoid.hls` / 6.49, display name `nicoid Re`.
Stable release remains `1.0.0` (app display `1.00`). This development candidate is `1.1.0-dev.1` (app display `1.10-dev.1`).
Android versionCode advances for updates; the displayed app versionName remains `6.49`.
Changing versionName does not make a modified APK a supported input: the original APK is still required.
Original APK SHA-256: `17fc6b46228af184437ade7e6f5573915bc655b86996307ff3270fdf35279cce`.
The patch verifies the original DEX, manifest and resource table before writing.

## Patch releases

<!-- PATCHES_START EXPANDED -->
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

