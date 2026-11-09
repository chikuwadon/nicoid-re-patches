# v1.10.7-dev.7 — Development source

Based on the verified v1.10.6 production release; no changes are made to the published version.

- Add "Add tab" to configurable list gestures, using the same native menu as the + tab button
- Repair app resume from background playback to normal playback by preserving the pending handoff until the background service is ready
- Apply existing round-corner dialog styling to native setting choice dialogs, including resume playback and tab-bar button visibility
- Adjust tab-list title margins and give the dialog close control an accent-colored distinct icon
- Incorporate Gradle 10 deprecation fixes from the audited two-file commit (AGP 9.5.0, Gradle wrapper 9.8.2, Java 8 output unchanged)
- Retain existing behavior, multilingual labels, and v1.10.6 production baseline

Additional v1.10.7-dev.7 playback fixes:\n\n- Prefer the retained decoder when switching from normal to background playback; fallback remains available\n- Coalesce duplicate resume requests while the background Service is settling\n- Properly stop foreground Service/media notifications, including retained mini-player playback\n- Consume Back when the asynchronous playback mode transition has been accepted\n\nv1.10.7-dev.7 playback lifecycle fixes:\n\n- Remove old fallback that recreated HLS when returning to normal playback\n- Wait for the already-playing, prepared decoder before attempting retained return\n- Use a single mode-transfer controller for Back and app switching\n- Send notification Stop to the existing signed media receiver instead of starting a second Service\n- Make Back cancel an unfinished retained transfer instead of silently swallowing input\n\nv1.10.7-dev.7 fixes based on Android 16 SC-52D bugreport (20:29):

- Fix the return-to-normal setting by locating the active DirectBackground235 Service, not the unrelated popup-only weak reference
- Mark the direct retained Service as restoring before launching the normal Activity, preventing lifecycle callbacks from closing it mid-transfer
- Reverse the audio handoff order: start the prepared target before pausing the previous AudioTrack
- Keep the v1.10.7-dev.3 cleanup of obsolete HLS reload fallbacks and old duplicated playback policy

v1.10.7-dev.7 follow-up on SC-52D Android 16 bugreport (21:00):
- Detect background-to-foreground re-entry for all app Activities, including when the browsing screen is shown during background playback
- Do not terminate the original AudioTrack until the new player's position is actually progressing (or cancel and retain source if startup stalls)
- Refuse stale unadopted PopupReturn244 requests; distinguish return target creation, decoder adoption, and ready callback in diagnostics
- Keep the previous removal of obsolete playback reload fallbacks

v1.10.7-dev.7 emergency crash hotfix (Galaxy SC-52D Android 16, 21:33):
- Remove all v1.10.7-dev.5 PopupReturn244 smali injections; injected move-result v0 conflicted with a live String register in ART's verifier and crashed on video opening.
- Preserve the original PopupReturn244 smali bytes and assert exact identity with the verified v1.10.1 baseline in CI.
- Handle any stale retained-return request via Java reflection, without rewriting shared smali registers.
- Keep audio-clock handoff and foreground detection from v1.10.7-dev.5 pending device verification.
- No official release.

v1.10.7-dev.7 safety rollback following Android 16 SC-52D bugreport (22:34):
- Fix ART VerifyError in AppSwitchGuard$2.onActivityStopped caused by invoking the callback on p1 after p1 had been overwritten with a Runnable. Execute the Activity callback before register reuse.
- Revert experimental Dev136 overlapping dual-decoder audio handoff, restore byte-identical v1.10.2 PlaybackTransfer221 prepared/complete logic (the dev.6 overlap caused codec-stop errors and possible position jumps).
- Add assembled-smali structural verification for the lifecycle Activity register and decoder handoff identity.
- Retain app-wide foreground tracking and Java-side stale-return cleanup for focused evaluation.
- Device-level seamless playback remains unverified; this is NOT an official release.

Developer testing build only; **not** an official release.
