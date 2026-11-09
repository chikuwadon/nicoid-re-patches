# v1.10.7-dev.8 — single-player app-switch prototype

Developer test build, not a regular release. Preserve the original normal
VideoView and its playback timeline while leaving the app. The foreground
Service only provides a notification and process keeper; native HLS startup
is bypassed. Avoid the native Fragment stop/pause for that player. On re-entry
with "通常再生に戻る", bring the existing normal VideoActivity to the front
instead of starting a new decoder or seeking. Notification Play/Pause/Stop
act on the original player.

The legacy alternate Service-starting fallback is removed from app-switch
callbacks. Explicit Back and popup retain their separate existing semantics.
Logs use tag "nicoid-retained" to trace token, task, time and playing state.

Android 9/14/16 device checks are mandatory. Build and static checks cannot
guarantee uninterrupted audio across device-specific Activity/window lifecycle.
