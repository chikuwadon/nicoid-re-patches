# Playback information transition and action row

- Restoring fullscreen snapshots at the end of a native mode change overwrote the newly assigned picture width. The restore now runs before native g(Z), h(Z) and i(I) change the layout. The native information panel can reserve the right-hand column.
- The side-pane path no longer applies the old screen-wide controller override; title and bottom controls remain within the picture column.
- Both lower buttons now use the upper toolbar's 44dp by 40dp slots and the same 1.344 drawing scale. Existing vector symbols are constructed with their individual background surfaces disabled, retaining the current theme's ripple accent.
- A single rectangular background spans the visible lower action buttons. No rounded per-button background remains. The background is limited to those buttons, with the rest of the row transparent.
- Eighteen Android framework tests passed on API 28 and 34, including native Canvas rendering. Tests cover information transitions, the old late-restore failure, controls staying inside the picture column, edge placement, click listeners, rotations, comment sizing, equal slot dimensions, glyph transform, connected background pixels and restoration.
- The rendered action strip was inspected locally. The final MPP changes only helpers, method delta and its version manifest. All DEX SHA-1 / Adler-32 checksums passed, and all three native transition methods contain the restore as their first executable instruction.

These are framework fixtures and payload checks. The complete patched nicoid APK, real video playback and Android 16 / SC-52D device display have not been exercised. GitHub changes remain unsubmitted following the previously reported approval-review rejection.
