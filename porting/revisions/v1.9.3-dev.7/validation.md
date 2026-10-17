# Side-toolbar status-bar clearance and action background corner

- Only the comment-post tile's top-right background corner is rounded (6dp). Its remaining three corners and all information-tile corners stay square. Both backgrounds remain connected below that corner.
- Side-information mode positions the upper toolbar below the visible status-bar inset in window coordinates. Android 30+ uses the visible statusBars inset; older APIs use the legacy top inset. Parent window position and padding are subtracted to avoid duplicate spacing.
- The inset applies only to the side-information toolbar. Fullscreen overlay edges remain unchanged. A toolbar row already inside the title bar does not receive a second inset.
- Twenty-four framework tests passed on API 28 and 34. Added checks cover a visible 64px status inset, top-action touch delivery, repeated updates without accumulating margins, a parent already below the inset, hiding the status bar, restoration, and the single rounded corner's native-rendered pixels. Other background corners and the connected area below the radius remain fully painted.
- The rendered strip was inspected. Final ZIP/DEX integrity passed. Only helpers and the version manifest differ from dev.6; the native information-transition method patch is preserved byte-for-byte.

Full nicoid APK playback and Android 16 / SC-52D hardware verification remain unavailable. These are framework fixtures and payload checks. GitHub source publication remains pending the previously reported authorization.
