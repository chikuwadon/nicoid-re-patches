# nicoid Mod Patches

Private Morphe patch development for nicoid 6.49.
Input: `com.sauzask.nicoid` / 6.49. Output: `com.sauzask.nicoid.hls` / 6.49.46-modern.
Original APK SHA-256: `17fc6b46228af184437ade7e6f5573915bc655b86996307ff3270fdf35279cce`.
The patch verifies the original DEX, manifest and resource table before writing.

## Changes

- Fix popup-to-normal comment renderer transfer crash.
- Five comment sizes: 60%, 80%, 100%, 120%, 140%; normal and popup playback. Applied on next playback.
- Move Google Cast connect/disconnect below comment settings; remove menu/sidebar entries.
- Popup controller row: quality, speed (0.75/1/1.25/1.5/2x), loop.
- Restore position and playing/paused state after quality changes.

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

