# v1.10.7-dev.5 — Development source

Based on the verified v1.10.6 production release; no changes are made to the published version.

- Add "Add tab" to configurable list gestures, using the same native menu as the + tab button
- Repair app resume from background playback to normal playback by preserving the pending handoff until the background service is ready
- Apply existing round-corner dialog styling to native setting choice dialogs, including resume playback and tab-bar button visibility
- Adjust tab-list title margins and give the dialog close control an accent-colored distinct icon
- Incorporate Gradle 10 deprecation fixes from the audited two-file commit (AGP 9.5.0, Gradle wrapper 9.8.2, Java 8 output unchanged)
- Retain existing behavior, multilingual labels, and v1.10.6 production baseline

Additional v1.10.7-dev.5 playback fixes:\n\n- Prefer the retained decoder when switching from normal to background playback; fallback remains available\n- Coalesce duplicate resume requests while the background Service is settling\n- Properly stop foreground Service/media notifications, including retained mini-player playback\n- Consume Back when the asynchronous playback mode transition has been accepted\n\nv1.10.7-dev.5 playback lifecycle fixes:\n\n- Remove old fallback that recreated HLS when returning to normal playback\n- Wait for the already-playing, prepared decoder before attempting retained return\n- Use a single mode-transfer controller for Back and app switching\n- Send notification Stop to the existing signed media receiver instead of starting a second Service\n- Make Back cancel an unfinished retained transfer instead of silently swallowing input\n\nv1.10.7-dev.5 fixes based on Android 16 SC-52D bugreport (20:29):

- Fix the return-to-normal setting by locating the active DirectBackground235 Service, not the unrelated popup-only weak reference
- Mark the direct retained Service as restoring before launching the normal Activity, preventing lifecycle callbacks from closing it mid-transfer
- Reverse the audio handoff order: start the prepared target before pausing the previous AudioTrack
- Keep the v1.10.7-dev.3 cleanup of obsolete HLS reload fallbacks and old duplicated playback policy

v1.10.7-dev.5 follow-up on SC-52D Android 16 bugreport (21:00):
- Detect background-to-foreground re-entry for all app Activities, including when the browsing screen is shown during background playback
- Do not terminate the original AudioTrack until the new player's position is actually progressing (or cancel and retain source if startup stalls)
- Refuse stale unadopted PopupReturn244 requests; distinguish return target creation, decoder adoption, and ready callback in diagnostics
- Keep the previous removal of obsolete playback reload fallbacks

Developer testing build only; **not** an official release.
