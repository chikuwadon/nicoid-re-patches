# Cross-feature audit after v1.8.4

Date: 2026-10-13 (Japan)
Baseline: 326e836ae4195c6aa35ce73db945e2743e960f30
Disposition: review findings; production implementation and released assets unchanged.

## Scope and evidence

Reviewed cross-feature ownership, cancellation, storage failure handling, repeated work,
input validation and the Java/smali/payload build boundary. This was not limited to the
v1.8.4 diff. Inventory: 114 Java helpers, including 44 root-class families also present
in the smali override directory. Representative packaged call paths were checked in the
previously downloaded official v1.8.4 helper DEX.

The source review covered cache files/containers/comments/listing/playback/progress/copy,
network tasks and thumbnail/page caches, local playlists, account/channel/follow feeds,
search ranges, settings import, filtering, posting, playback transfers, gesture/comment
lifecycle and the release/build checks. This is not a line-by-line audit of every original
application class or a claim that every device/runtime path was executed.

## Findings requiring fixes

### F1 — Failed comment replacement can destroy the last usable copy (high)

Location: `extensions/extension/src/main/java/e/e/a/CacheComments.java`, `save`.

A new gzip is written to `.comments.partial`, the previous `.comments.gz` is deleted,
and only then is the temporary file renamed. If the provider rejects rename, the old
copy is already gone and `finally` removes the temporary copy. Existing comments are
lost even though writing the new data succeeded. The temporary filename is also shared
by concurrent saves for the same video; that concurrency path needs separate testing.

Reproduced using the production Java class with a file adapter that fails rename after
an initial successful save: a previously readable comment cache becomes absent.
This demonstrates replacement failure handling, not a measured failure frequency on SAF.

Fix direction: per-video serialization and unique temporary files; retain a recoverable
old copy until installation succeeds, restore on failure and recover interrupted swaps.
Test rename/delete/open failures and concurrent save/cleanup, including SAF providers.

### F2 — Implicit HLS byte ranges are collapsed in transfer planning (medium)

Location: `CacheTransferPlan.java`, `walk`; used by `CacheProgress.measure`.

The asset key is URL plus the original BYTERANGE text. Successive equal-length implicit
ranges on the same URI have identical keys even though they refer to different offsets.
The later map insertion overwrites the earlier segment.

Reproduced playlist: `100@0`, `100`, `100`, all referring to `v.mp4`.
Expected total: 300 bytes. Actual result: 200 bytes. Asset count is also reduced.
This affects size/progress estimates; the reproduction does not demonstrate missing
video bytes in the downloader.

Fix direction: resolve implicit offsets and identify assets by URI and effective range;
cover maps, explicit/implicit offsets and duplicate references consistently with the downloader.

### F3 — Malformed local playlist entries can crash rendering (medium)

Location: `LocalPlaylistData.java`, constructor/hasFolder/delete; `LocalPlaylists.java`,
`read`, `Page.render`, `choose`.

The constructor accepts arrays containing null/non-object elements, while several consumers
immediately dereference `optJSONObject`. `{"folders":[null]}` followed by `hasFolder("test")`
reproduces NullPointerException. Invalid JSON text is separately converted to an unchecked
IllegalStateException by `read`; `Page.render` does not provide a recovery UI.

This requires damaged or otherwise malformed stored data; normal UI-created data was not
shown to produce it. Do not silently erase the user's playlist as a recovery strategy.

Fix direction: validate the snapshot and entry schema at the storage boundary, preserve
invalid originals for recovery and show a usable failure/recovery state.

### F4 — Duration input and range slider can disagree (medium)

Location: `SearchRange.java`, `durations`; `DurationRangeBar.java`.

The slider updates both text fields, but text edits have no reverse listener. For example,
editing the minimum from 0:00 to 10:00 leaves `bar.low` at 0; subsequently moving the upper
handle writes both stale slider values back and loses the typed lower bound. The unlimited
checkbox also leaves the finite upper control active without expressing the distinction.

Confirmed by source event flow; no rendered Android gesture test was run this turn.
Fix direction: one validated range model shared by text fields, slider and unlimited state,
with update-loop protection. Test mixed typing/dragging/preset/unlimited interactions.

## Performance and maintainability improvements

### F5 — SAF lookups repeatedly traverse unrelated video folders (medium)

Location: `CacheFolders.document/cacheChildren/stat`, `CacheFile.listFiles/isFile/length`,
`CachePlayback.serve`.

On a two-second index expiry, resolving a single filename calls `cacheChildren`, which
queries the root plus every matching video directory. The scan is performed while holding
the class monitor. `listFiles` separately scans without populating that lookup index.
`isFile` uses exists and isDirectory, each issuing metadata work, and playback then asks
for length again. Consequently an unrelated slow directory can delay the target video
and concurrent cache operations.

Code confirms query amplification; no SD-card latency/ANR measurement is claimed.
Fix direction: cache per-video directory/document identities and reuse returned metadata,
avoid provider calls under a global lock, and invalidate narrowly after mutations.
Benchmark 10/100/1,000 videos with provider query counters and concurrent playback/copy.

### F6 — Playlist selection rebuilds the entire UI and disables row reuse (medium)

Location: `LocalPlaylists.Page.toggle/render/save`.

Each selection invokes `render`, clears all views, reparses the whole stored snapshot,
rebuilds every model row, and creates a fresh ListView/adapter. The row wrapper also passes
null to `nativeRows.getView`, ignoring the supplied recycled row. Saving serializes the
entire snapshot and calls synchronous SharedPreferences.commit from UI callbacks.

Confirmed repeated work, not measured frame latency. Large lists are the relevant case.
Fix direction: retain list/adapter, update selection state and affected rows, preserve scroll,
reuse compatible row types, and serialize writes off the UI thread without lost updates.

### F7 — Thumbnail disk trimming sorts the whole cache after every write (low)

Location: `ImageDiskCache.put/trim`.

Every new image lists all cached files, sorts them by modification time, then totals and
trims them while holding the instance lock. This blocks other disk-cache readers during
repeated list population. Count grows independently of the 32 MiB byte limit.

Fix direction: amortize cleanup with maintained byte accounting and periodic/threshold
sweeps while preserving a strict size bound, expiry and startup reconciliation. Measure
provider-independent filesystem operations before changing cache policy.

### F8 — Tests of Java source do not fully protect the shipped implementation (medium)

Location: `patches/src/buildHelpers/java/NicoidDexMerger.java`,
`porting/tests/VerifyCanonicalHelpers.py`, `patches/build.gradle.kts`, release workflow.

The merger intentionally prioritizes smali overrides. There are 44 Java root classes with
matching smali roots. The current canonical-source guard protects eight selected families;
it does not require acknowledgement when another shadowed Java file changes. A source-only
unit test can therefore pass for behavior that is absent from the generated MPP.
This is an architectural risk, not proof all 44 families differ or are broken.

Fix direction: maintain an explicit implementation-owner manifest, reject unreviewed
changes to shadowed sources, and progressively reconcile each family. Check the actual
MPP and an APK patched through the supported Morphe path before release. Do not simply
delete all overrides: some preserve device-tested behavior absent from the Java copy.

## Verification performed this turn

- Freshly compiled all 114 Java helper sources against the Android API.
- Freshly compiled and ran 13 existing suites: transfer planning, performance policies,
  search bounds, settings/search/channel rules, follow-feed parsing, NG data, content/history
  rules, playback URL routing, date presets, cache row metadata, themes, watch request headers
  and posting rules. All passed. Existing success-case tests do not cover F1–F3.
- Three additional isolated JVM reproductions confirmed F1–F3.
- Compiled settings, manifest and eight-family canonical-source guards passed.
- Checked official helper DEX integration for the transfer planner and production source
  precedence for the finding classes; these classes are not among the smali-shadowed roots.

Reproduce F1–F3 from the repository root with a Java runtime, ECJ and org.json JAR:

```sh
python3 porting/reviews/reproduce-post-1.8.4.py --ecj /path/to/ecj.jar --json /path/to/json.jar
```

The audit harness intentionally asserts the current defective outcomes; it is a diagnostic,
not a passing regression gate for a future fix. Convert it to preservation/correctness
expectations when implementing fixes.

## Recommended order and remaining checks

1. Protect existing comment data and handle malformed playlist snapshots (F1, F3).
2. Correct HLS progress accounting and mixed search-range input (F2, F4).
3. Reduce SAF and playlist UI work with query/allocation measurements (F5, F6).
4. Extend shipped-payload/source ownership gates and amortize thumbnail cleanup (F8, F7).

A full Gradle build was not repeated for this documentation-only audit; v1.8.4's release
build previously succeeded. No new APK/device session, live authenticated service requests,
Cast receiver test, SAF failure-rate benchmark or full patched-APK sweep was executed.
Those remain necessary before claiming the proposed changes are validated for release.
