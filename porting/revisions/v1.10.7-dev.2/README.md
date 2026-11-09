# v1.10.7-dev.1 — Development source

Based on the verified v1.10.6 production release; no changes are made to the published version.

- Add "Add tab" to configurable list gestures, using the same native menu as the + tab button
- Repair app resume from background playback to normal playback by preserving the pending handoff until the background service is ready
- Apply existing round-corner dialog styling to native setting choice dialogs, including resume playback and tab-bar button visibility
- Adjust tab-list title margins and give the dialog close control an accent-colored distinct icon
- Incorporate Gradle 10 deprecation fixes from the audited two-file commit (AGP 9.5.0, Gradle wrapper 9.8.2, Java 8 output unchanged)
- Retain existing behavior, multilingual labels, and v1.10.6 production baseline

Developer testing build only; **not** an official release.
