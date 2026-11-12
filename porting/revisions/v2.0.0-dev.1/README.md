# ニコ生 (β) の開発版

ベースは正規版タグ `v1.11.1`（`dc19a9c75848d7edd8d04060b9d045d107f1093a`）。移植元は復元した `feature/nicolive-2.0.0` の監査済みコミット `a44e47dab7162e609e921327eb1f2f147e2053c6`。このディレクトリはブランチ `v2.0.0-dev` の開発版ビルド用。

## ビルド

Java 21 と Python 3 が必要。元の nicoid 6.49 APK と正規版 `patches-1.11.1.mpp` を用意し、このディレクトリで実行する。

```sh
python3 bootstrap_tools.py
python3 bootstrap_live.py
python3 build_mpp.py --base /path/to/patches-1.11.1.mpp \
  --apk /path/to/nicoid-6.49.apk --tools tools --media deps \
  --output /path/to/nicoid-re-2.0.0-dev.1-12.mpp
java -jar tools/morphe.jar patch --unsigned \
  -p /path/to/nicoid-re-2.0.0-dev.1-12.mpp -o /path/to/test.apk /path/to/nicoid-6.49.apk
python3 verify_mpp.py /path/to/patches-1.11.1.mpp \
  /path/to/nicoid-re-2.0.0-dev.1-12.mpp /path/to/test.apk
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
