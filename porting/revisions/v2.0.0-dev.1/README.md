# ニコ生 (β) の開発版

ベースは正規版タグ `v1.11.1`（`dc19a9c75848d7edd8d04060b9d045d107f1093a`）。移植元は復元した `feature/nicolive-2.0.0` の監査済みコミット `a44e47dab7162e609e921327eb1f2f147e2053c6`。このディレクトリはブランチ `v2.0.0-dev` の開発版ビルド用。

## ビルド

Java 21 と Python 3 が必要。元の nicoid 6.49 APK と正規版 `patches-1.11.1.mpp` を用意し、このディレクトリで実行する。

```sh
python3 bootstrap_tools.py
python3 bootstrap_live.py
python3 build_mpp.py --base /path/to/patches-1.11.1.mpp \
  --apk /path/to/nicoid-6.49.apk --tools tools --media deps \
  --output /path/to/nicoid-re-2.0.0-dev.1-22.mpp
java -jar tools/morphe.jar patch --unsigned \
  -p /path/to/nicoid-re-2.0.0-dev.1-22.mpp -o /path/to/test.apk /path/to/nicoid-6.49.apk
python3 verify_mpp.py /path/to/patches-1.11.1.mpp \
  /path/to/nicoid-re-2.0.0-dev.1-22.mpp /path/to/test.apk
```

ビルドは配布済みMPPのハッシュと入力APKのDEXを照合する。既存Gradleタスクは正規版ベースのビルド経路。ニコ生入り開発版には上記のコマンドを使う。

## 移植範囲

- サイドメニューの「ニコ生 (β)」から番組一覧を開く。
- 番組一覧・フォロー・視聴履歴の入口、ニコ生URL、番組画面、Foreground Service、Media3再生、コメント、NG、ポップアップ。
- 通常動画の開始時はニコ生を停止する。ニコ生を開始する際は、通知サービスと独立して保持される v1.11.1 の通常動画セッションも終了する。
- 正規版のパッチ処理、ネイティブメソッド差分、既存のリソースIDを使用する。旧版の通常動画ペイロードをコピーする経路は使用しない。
- ビルド入力用のツールと依存ライブラリは固定ハッシュで取得する。元APKとその抽出物は配布物に含めない。

## 初回移植版 v2.0.0-dev.1 の検証

| 確認項目 | 結果 |
| --- | --- |
| Javaコンパイル、D8、Smali、MPP生成 | 成功 |
| 元の nicoid 6.49 に対する Morphe 適用 | 成功。未署名の検証APKを生成 |
| MPP内の既存ヘルパー | 1,924クラスを保持。逆アセンブル比較で変更があるのは入口、動画開始時の停止フック、バージョン表示2箇所のみ |
| ネイティブメソッド差分 | 正規版 v1.11.1 とバイト一致 |
| リソース | Manifest以外の1,190エントリーが正規版とバイト一致。適用後のManifestにもActivity、非公開Service、Foreground Service権限を確認 |
| 適用後DEX | 14,927クラス。重複クラス0件。MPPの全ヘルパーが存在 |
| JVMテスト | 移植Javaを直接コンパイルし8件成功。URL判定、ゲスト権限、コメントフレームとprotobufの境界等 |
| DEXを再変換したJVMテスト | dex2jar変換結果にJVMの検証・型解決エラーがあり、DEX実行の成功判定には使用していない |

Android実機での起動、実配信、認証済みコメント投稿、長時間視聴、通常動画との往復、ポップアップ切り替えは未確認。旧版で報告された約1分でのクラッシュと切り替え時の黒画面は、今回のAPK適用結果から解消を判断できない。

## Local playlists: custom order (v2.0.0-dev.1-22)

- Use the rightmost reorder button in the playback-options row to enter reorder mode.
- Long-press and drag a row's handle; completed moves persist per playlist using existing stable video IDs and atomic playlist storage.
- Custom order is added as a fourth sort option; other sort modes leave manual order intact.
- Exit reorder mode with the checkmark or Back. Sorting preference is remembered per playlist.
- Preserve playback/long-press actions outside reorder mode. No official release is made by this branch.

## Thumbnail regression fix (v2.0.0-dev.1-22)

- Bind local-playlist image URLs through the existing `ShortImages` cache via `ThumbnailCorners.load` after constructing native video rows.
- Bind thumbnails in both normal and manual-reordering modes, avoiding the black placeholders seen after the playlist adapter replacement.
- Reuse in-memory/disk caches and limit network workers using the existing loader; retain video duration, viewing progress, and other metadata.
- Keep the manual sorting behavior, persistent custom order and existing video actions unchanged.

## v2.0.0-dev.1-22: playlist regressions

- Restore `Dev116.playlistThumbnail` in both normal and reorder rows so missing thumbnail URLs, image visibility, and cached refreshes use the original stable pipeline, removing the incompatible generic image loader.
- Persist the open playlist ID and parent group, restoring the same valid nested location when the last-used tab reopens after app restart.
- Preserve existing drag reorder, custom sort, cached thumbnail progress and the single-ZIP MPP packaging.

## Development fixes: v2.0.0-dev.1-22 (pre-release blocked)

- Recover from Media3 HLS AES-128 decryption failures (`BadPaddingException/BAD_DECRYPT`) by acquiring a fresh live watch session/stream URI and its AES key. Retry at most three times in a rolling five-minute window; do not retry generic playback errors or user-paused media.
- Restore the native ActionBarDrawerToggle for the local-playlist hamburger button. Hardware Back remains the folder-up navigation action.
- Preserve the audited v2.0.0-dev.1-20 timeshift reservation/cancellation fix: serialize write requests separately from feed-image cancellation, preserve callbacks, and refresh reservations on return.
- Re-run Java compile, MPP integrity and Robolectric unit checks. Live playback for 120+ seconds still requires actual Android/device verification; do not publish a pre-release before validation.

## Development fixes: v2.0.0-dev.1-22 (pre-release still blocked)

- Render authenticated live program artwork in Android media controls, both the notification's large icon and MediaSession artwork metadata. Decode a size-limited HTTPS image from trusted Niconico hosts on a worker thread; cancel stale results when programs change or playback ends.
- Keep the live session's paused/buffering/playing/error media controls synchronized. On a terminal playback error, offer Reconnect directly rather than displaying an inappropriate Pause button.
- Live streaming has no seekable duration and does not advertise seek actions. Android may still draw a disabled timeline bar, which the app cannot forcibly hide across all OEM variants.
- Retain AES-128 recovery, local-playlist drawer restoration, serialized timeshift reservation writes, and all prior fixes. No pre-release publication until long-running device playback passes.

## v2.0.0-dev.1-23: live HLS key-rotation mitigation (pre-release blocked)

- Repeated SC-52D logs (2026-11-12) show `BAD_DECRYPT` at approximately 60-second intervals and `Skipping atom with length > 2147483647` from the fMP4 extractor.
- Work around Media3 AES-128 key caching by assigning a unique fragment-only identity to each key URI on playlist refresh. The HTTP endpoint, signed query, key bytes, encryption IV, segment data, cookies, and user-facing controls remain unchanged.
- Allow the exact suspicious fMP4 atom-length error to use bounded encrypted-HLS watch-session recovery, and only after an AES-128 playlist has been seen. All unrelated parse errors remain terminal to prevent retry loops.
- Source contracts and Java-level tests validate signed URL preservation, AES-128-only rewriting, encrypted fMP4 recovery gating, and unchanged UI. Device playback over multiple key rotation intervals remains unverified.
