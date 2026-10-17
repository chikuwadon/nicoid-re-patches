# Fullscreen playback layout validation

The reported image requires one continuous title/control background starting at the screen edges, a reading inset for the title, slightly more spacing between icons, and a bottom background reaching the right and bottom screen edges. Seek track endpoints must remain inset.

## Checked behavior

- Android framework measure/layout tests ran through Robolectric on API 28 and 34 (eight passing tests).
- The previous helper reproduced a controller ending at x=1955 and y=919 inside a 2048x955 screen fixture: 93px right and 36px bottom gaps.
- The new helper placed the controller at left=0, right=2048, bottom=955 even with that same narrow parent. The bottom action row also reached x=2048 and aligned above the controller.
- A touch outside the old parent bounds reached the original rightmost button listener.
- The top row has a single 0x77000000 background; its nested host is transparent. The title alone receives a 12dp inset.
- The seek track has equal 12dp left/right padding. Both thumb endpoints remain inside the screen, with equal insets. Its background container has no outside margin.
- Shipped toolbar slots widen from 40dp to 44dp and text slots from 48dp to 52dp. Height remains 40dp and glyph scaling is unchanged; the icon renderer uses the smaller of width/height, so symbols retain their size.
- Portrait/landscape size changes update the renderer after layout, without retaining stale clipping. The original parent, index, parameters and padding restore on exit.
- The existing oversized comment-surface regression and previous viewport regression also passed.
- The final MPP changes only the helper payload and version manifest. All DEX SHA-1 and Adler-32 checksums passed.

## Limits

These are framework fixture tests and payload checks, not an end-to-end nicoid screenshot or video-playback test. Android 16 / Samsung SC-52D display and gesture behavior have not been verified on a device. No hardware/emulator is attached.
