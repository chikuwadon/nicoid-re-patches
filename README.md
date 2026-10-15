<a id="nicoid-re"></a>

<div align="center">

<h1>nicoid Re</h1>

<p>Android向けニコニコ動画プレイヤー「nicoid」のためのMorpheパッチ</p>

<p>
<a href="https://github.com/chikuwadon/nicoid-re-patches/releases/latest"><img alt="最新リリース" src="https://img.shields.io/github/v/release/chikuwadon/nicoid-re-patches?style=flat-square&amp;color=167D8D"></a>
<img alt="対象アプリ nicoid 6.49" src="https://img.shields.io/badge/nicoid-6.49-167D8D?style=flat-square">
<a href="LICENSE"><img alt="ライセンス GPL v3" src="https://img.shields.io/badge/License-GPL_v3-687078?style=flat-square"></a>
</p>

<p>
<a href="https://morphe.software/add-source?github=chikuwadon%2Fnicoid-re-patches&amp;name=nicoid%20Re"><img alt="Morphe Managerにソースを追加" src="https://img.shields.io/badge/ADD_TO_MORPHE-167D8D?style=for-the-badge"></a>
<a href="https://github.com/chikuwadon/nicoid-re-patches/releases/latest"><img alt="最新版のパッチをダウンロード" src="https://img.shields.io/badge/DOWNLOAD-37474F?style=for-the-badge"></a>
</p>

<p><a href="#installation">導入・更新</a> · <a href="#features">機能</a> · <a href="#guides">使い方</a> · <a href="#development">開発</a></p>

</div>

nicoid Reは、nicoidを現在のニコニコ動画の再生形式に対応させ、再生・コメント・検索・動画管理の機能を拡張するパッチである。nicoid 6.49の元APKにMorphe Managerで適用すると、nicoid Reとして利用できる。

<a id="installation"></a>

## 導入・更新

| 項目 | 内容 |
|---|---|
| 対象アプリ | **nicoid 6.49** |
| パッチ適用 | **Morphe Manager** |
| 適用後のアプリ名 | **nicoid Re** |
| パッチ配布 | [最新の正式版](https://github.com/chikuwadon/nicoid-re-patches/releases/latest) |

### 初めて利用する

1. Morphe ManagerをインストールしたAndroid端末で、[nicoid Reの追加リンク](https://morphe.software/add-source?github=chikuwadon%2Fnicoid-re-patches&name=nicoid%20Re)を開く。
2. Morpheの確認画面でソース名とURLを確認し、「追加」をタップする。
3. nicoid **6.49**の元APKを用意し、Morphe Managerでパッチを適用する。
4. 生成されたアプリをインストールする。

<details>
<summary>対応APKの詳細</summary>

| 項目 | 値 |
|---|---|
| 元APKのパッケージ名 | `com.sauzask.nicoid` |
| 元APKのバージョン | `6.49` |
| 適用後のパッケージ名 | `com.sauzask.nicoid.hls` |

元APKのSHA-256：

```text
17fc6b46228af184437ade7e6f5573915bc655b86996307ff3270fdf35279cce
```

</details>

<details>
<summary>追加リンクが開かない場合</summary>

1. Morphe Managerのホーム画面下部から「Sources」を開く。
2. 「＋」をタップし、「Remote」を選ぶ。
3. 次のURLを入力して「Add」をタップする。

```text
https://github.com/chikuwadon/nicoid-re-patches
```

</details>

### パッチを更新する

Morphe Managerは追加したソースの新しいパッチを定期的に確認する。すぐに確認する場合は、「Sources」を開き、nicoid Reのソースカードにある更新ボタン（↻）をタップする。

ホーム画面でnicoid Reのカードに「Update」が表示されたら、カードを開いて案内に沿って再パッチする。

> **ソースの更新と、アプリへのパッチ適用は別の操作である。** ソースを更新した後、アプリにも新しいパッチを適用する必要がある。

<a id="features"></a>

## 機能

[再生・操作](#playback) · [コメント](#comments) · [NG・フィルター](#filters) · [ランキング・検索](#discovery) · [キャッシュ・プレイリスト](#library) · [フォロー・動画情報](#following) · [テーマ・設定](#settings)

<a id="playback"></a>

### 再生・操作

- **再生方法**：通常再生・バックグラウンド再生・ポップアップ再生に対応。
- **速度・画質**：通常再生とポップアップ再生で変更可能。再生速度は0.1〜3.0倍のスライダーで調整でき、モバイル通信時の画質も指定可能。
- **ジェスチャー**：上下スワイプで音量・輝度を調整。動画の左右をダブルタップしてシーク可能。
- **再生動作**：再生位置の保存、アプリ切替時、再生中に「戻る」を押した際の動作を設定可能。
- **常時表示**：再生中の操作部を常時表示するモードに対応。
- **ポップアップ**：ピンチイン・アウトによるサイズ変更に対応。
- **メディアコントロール**：Androidのメディアコントロールから再生を操作可能。
- **Google Cast**：現在の動画配信方式に対応。関連機能は設定から利用可能。
- **イヤホン切断時**：再生を一時停止し、自動再開を抑止。
- **関連動画**：選択メニューからポップアップ再生・バックグラウンド再生・キャッシュ取得を実行可能。

<a id="comments"></a>

### コメント

- **表示調整**：文字サイズ10〜300%、太字、不透明度、影の大きさ、最大行数、表示時間を設定可能。表示時間の変更でスクロール速度を調整できる。
- **コマンド**：コメントコマンドによる色指定・上下固定・文字サイズに対応。
- **描画設定**：描画上限を25・30・60・90・120fpsから選択可能。実際のフレームレートは端末性能と画面の更新頻度に依存する。
- **再生速度との連携**：動画の再生速度を変更しても、流れるコメントの速度を等倍に維持。
- **コメント取得**：標準のコメントに加え、過去コメントを含む最大10,000件を目標に取得可能。
- **一覧操作**：再生位置に合わせた自動スクロール、自動追従の切り替え、並べ替えに対応。コメントをダブルタップすると、そのコメントの再生時間へ移動する。
- **ニコる**：コメント一覧からニコる操作が可能。ニコる数の表示と、ニコる数順の並べ替えに対応。

<a id="filters"></a>

### NG・コンテンツフィルター

- **NGコメント**：一覧から追加・削除・複数削除が可能。
- **動画の非表示**：動画タイトルのキーワードや、投稿者・チャンネル名を条件として設定可能。
- **一致方法**：部分一致・完全一致・正規表現から選択可能。
- **ルール管理**：有効・無効の切り替え、個別削除・複数削除に対応。
- **長押し操作**：NGルールの長押しで個別削除の確認ダイアログを表示。動画操作メニューから投稿者NGを登録可能。

<a id="discovery"></a>

### ランキング・検索

- **ランキング**：ジャンルの並べ替え・非表示に対応。最大1,000位まで追加読み込み可能。
- **カスタムランキング**：ジャンルとタグ条件を組み合わせて作成・編集可能。起動時の画面にも指定できる。
- **検索条件**：投稿日の開始日・終了日と、動画時間の下限・上限を指定可能。
- **検索操作**：入力サジェストと、検索結果の先頭でのスワイプ更新に対応。
- **ショート動画**：ニコニコショートの視聴に対応。
- **一覧表示**：有料動画にラベルを表示。ランキング・検索・キャッシュ・視聴履歴・マイリスト・プレイリストで情報配置を統一。
- **日時表示**：画面に応じて投稿・登録・視聴日時を表示。マイリストでは登録日時と動画の投稿日時を区別して表示する。

<a id="library"></a>

### キャッシュ・プレイリスト

- **キャッシュ管理**：保存先の変更と、容量・取得率・取得状態の確認に対応。
- **取得の中断**：確認ダイアログから進行中のキャッシュ取得を停止可能。
- **ローカルプレイリスト**：アカウントから独立して管理し、フォルダーの作成・名前変更・移動・削除で整理可能。
- **再生方法**：連続再生・シャッフル再生に対応。
- **一覧操作**：動画リストの長押しで操作メニューを表示。複数選択による動画管理に対応。

<a id="following"></a>

### フォロー・動画情報

- **フォロー管理**：動画情報から投稿者をフォロー・解除可能。公開マイリストのフォロー・解除にも対応。
- **フォロー一覧**：ユーザー・チャンネル・マイリストのタブを、横スワイプで切り替え可能。
- **ニコレポ**：投稿者や活動の種類による絞り込みに対応。
- **マイページ**：プロフィールとアカウントページへのショートカットを表示。
- **いいね**：動画へのいいね操作に対応。
- **関連作品**：動画情報にシリーズ・親作品・子作品を表示。
- **ニコニ広告**：動画情報から内部ブラウザで表示可能。

<a id="settings"></a>

### テーマ・設定

- **テーマ**：ライト・ダーク・AMOLEDダーク・Material Youに対応。
- **デザイン**：装飾的な影を抑えたフラットなUI。動画一覧の文字・アイコン・情報配置を共通化。
- **設定管理**：設定検索、起動時画面の選択、設定のエクスポート・インポートに対応。
- **ログイン**：Cookie手動入力によるログインと、設定からのログアウトに対応。
- **補助機能**：メニューからのアプリ再起動と、設定からのデバッグログ保存に対応。
- **対応環境・言語**：Android 16、日本語・英語・繁體中文に対応。
- **広告**：広告バナーと広告リクエストを削除。

<a id="guides"></a>

## 使い方

<details>
<summary>コメント取得数を設定する</summary>

設定の「コメント取得数」で、「標準（追加取得なし）」または500〜10,000件の取得目標を500件単位で選択する。変更は次の再生から反映される。

標準のコメントに加えて過去のコメントを取得し、重複を除いて表示する。実際に取得できる件数は動画やログイン状態によって異なり、動画に表示される総コメント数や設定した件数のすべてを取得できるとは限らない。

</details>

<details>
<summary>Cookieを手動入力してログインする</summary>

WebViewでログインできない端末では、設定のログインから「Cookie手動入力」を選択する。

1. 別の端末・PCでニコニコにログインし、ブラウザの開発者ツールでニコニコのCookieを確認する。
2. `user_session`の値をコピーし、nicoid Reの入力欄に貼り付けて保存する。`user_session=値`の形式や、`user_session`を含むCookieヘッダーも入力可能。
3. 保存後は動画再生やアカウントのマイリストなどで動作を確認する。

Cookieの保存だけではサイト側の認証成功は確認されない。期限切れやログアウトで利用できなくなった場合は、ログインし直してCookieを再取得する。

**Cookieはログイン情報である。他の人に渡したり、Issueやログに貼り付けたりしないこと。**

</details>

<details>
<summary>デバッグログを保存する</summary>

設定の「デバッグ」から「デバッグログの保存」を選択し、確認画面で「保存」をタップする。

Downloadフォルダーに、`nicoid-re_log_yyyyMMddHHmmss.txt`の形式で保存される。

</details>

<a id="development"></a>

## 開発

<details>
<summary>ローカルでビルドする</summary>

リポジトリのルートで次のコマンドを実行する。

```sh
./gradlew :patches:buildAndroid --no-daemon
```

生成されるパッチファイルは`patches/build/libs/*.mpp`である。ローカルでビルドする場合は、MorpheのGitHub Packagesを読み取るための認証が必要。

</details>

<details>
<summary>配布パッチの詳細</summary>

<!-- PATCHES_START EXPANDED -->
> **[v1.9.1](https://github.com/chikuwadon/nicoid-re-patches/releases/tag/v1.9.1)**&nbsp;&nbsp;•&nbsp;&nbsp;`main`ブランチ&nbsp;&nbsp;•&nbsp;&nbsp;パッチ1件
<details open>
<summary>📦 nicoid&nbsp;&nbsp;•&nbsp;&nbsp;パッチ1件</summary>
<br>

**🎯 対応バージョン：**

| 6.49 |
| :---: |

| 💊 パッチ | 📜 説明 | ⚙️ 設定 |
|----------|----------------|-----------|
| [nicoid Re](#nicoid-re) | nicoid向けのMorpheパッチ。現在のニコニコ動画の仕様に対応。ダークモードとAndroid 16に対応。その他、各種機能の改善・追加。 | なし |

</details>

<!-- PATCHES_END -->

</details>

## ライセンス・クレジット

テンプレート由来のコードは[GPLv3](LICENSE)および[NOTICE](NOTICE)に従う。

nicoid：© sauza-sk
