# Local verification revision v1.9.2-dev.6

This revision is based on the exact dev.5 MPP identified in `manifest.json`.
The source repository checkout predates several dev.2–dev.5 changes, so rebuilding
all helpers from that checkout would remove existing development features.
`build.py` therefore retains the supplied MPP and applies only the listed helper
overrides, the native queue initialization guard, and newly compiled Java helpers.
It does not publish, update main, or change any issue state.

Inputs: Java, ECJ, Android compile library, D8, smali/baksmali and dependencies,
the existing Java helper classes for the compilation classpath, and the dev.5 MPP.

```sh
python build.py --base /path/nicoid-re-1.9.2-dev.5.mpp \
  --tools /path/tools --smali-classpath '<smali and dependency jars>' \
  --compile-helpers /path/helpers.jar --work /path/build \
  --output /path/nicoid-re-1.9.2-dev.6.mpp
```

The regression cases in `tests` run with Robolectric 4.14.1 / SDK 28. They cover
incremental tag filtering, interleaved ranks, deduplication, candidate limits,
retry and cancellation, popup visibility/position, and empty queue restoration.
`VerifyRevision196.java` additionally checks register types at the new DEX calls.

Validation: 16 regression tests, 199 release-continuity checks, all 1115 previous
helper classes and public entry points retained, and 42 new-helper calls checked
across 26 methods. All 800 PNG resource entries and 28 icon families retain their
existing format and dimensions.

Device verification remains necessary for the screenshot crash, popup/native
animations, authenticated history deletion, and the final screen layouts.

Issue review: #62/#66/#67/#68 implementation paths remain present. Account history
deletion is not verified on a signed-in device. Tag suggestions requested in #68,
comment art (#69), and Cast playback (#70) are not implemented by this revision.
No issue is considered closed by these checks.
