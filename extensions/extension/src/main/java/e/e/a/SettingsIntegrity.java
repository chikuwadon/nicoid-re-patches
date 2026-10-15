package e.e.a;
import android.preference.*;
import android.content.*;
import android.widget.*;
import android.app.AlertDialog;
/** Restore essential controls independently of legacy XML anchors. */
public final class SettingsIntegrity {
 private SettingsIntegrity(){}
 public static void ensure(PreferenceActivity a){
  PreferenceGroup root=a.getPreferenceScreen();if(root==null)return;
  remove(root,"remove_ad_option");remove(root,"labs");ThemeChoice.settings(a);
  Preference p=a.findPreference("play_mode");ListPreference mode;
  if(p instanceof ListPreference)mode=(ListPreference)p;
  else{mode=new ListPreference(a);mode.setKey("play_mode");mode.setDefaultValue("0");Preference group=a.findPreference("setting_whole");(group instanceof PreferenceGroup?(PreferenceGroup)group:root).addPreference(mode);}
  comments(a,root);casts(a,root);moveComments(a,root);
  mode.setTitle(UiStrings.translate("再生モード"));mode.setEntries(new CharSequence[]{UiStrings.translate("ストリーミング"),UiStrings.translate("キャッシュ")});mode.setEntryValues(new CharSequence[]{"0","1"});String value=PreferenceManager.getDefaultSharedPreferences(a).getString("play_mode","0");mode.setValue(CachePlaybackPolicy.acquire(value)?"1":"0");mode.setSummary(modeLabel(value));mode.setOnPreferenceChangeListener((row,next)->{String selected=String.valueOf(next);if(!selected.equals("0")&&!selected.equals("1"))return false;((ListPreference)row).setValue(selected);row.setSummary(modeLabel(selected));return true;});stripSummaries(a);
 }
 private static void casts(PreferenceActivity a,PreferenceGroup root){
  Preference old=a.findPreference("google_cast");PreferenceGroup group;
  if(old instanceof PreferenceGroup)group=(PreferenceGroup)old;
  else{PreferenceCategory category=new PreferenceCategory(a);category.setKey("google_cast");category.setTitle("Google Cast");root.addPreference(category);group=category;}
  Preference connect=a.findPreference("google_cast_connect");if(connect==null){connect=new Preference(a);connect.setKey("google_cast_connect");connect.setTitle(UiStrings.translate("接続"));connect.setSummary(UiStrings.translate("接続する端末を選択"));group.addPreference(connect);}
  connect.setIntent(new Intent(Intent.ACTION_VIEW).setClassName(a,"com.sauzask.nicoid.NicoidChromecastReceiverSelect"));
  Preference disconnect=a.findPreference("google_cast_disconnect");if(disconnect==null){disconnect=new Preference(a);disconnect.setKey("google_cast_disconnect");disconnect.setTitle(UiStrings.translate("接続解除"));disconnect.setSummary(UiStrings.translate("Google Cast の接続を解除"));group.addPreference(disconnect);}
  disconnect.setOnPreferenceClickListener(row->{a.stopService(new Intent().setClassName(a,"com.sauzask.nicoid.NicoidChormecastSenderService"));return true;});
 }
 private static void comments(PreferenceActivity a,PreferenceGroup root){
  Preference existing=a.findPreference("comment_fetch_limit");
  Preference p=existing==null?new Preference(a):existing;p.setKey("comment_fetch_limit");p.setTitle(UiStrings.translate("コメント取得数"));
  SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(a);p.setSummary(limitLabel(prefs.getInt("comment_fetch_limit",0)));
  p.setOnPreferenceClickListener(row->{Context c=PlaybackSession.dialogContext(a);LinearLayout box=PanelUi.column(c);int pad=PanelUi.dp(c,24);box.setPadding(pad,pad,pad,pad);TextView label=PanelUi.text(c,"",18);box.addView(label);SeekBar bar=new SeekBar(c);bar.setMax(20);bar.setProgress(Math.max(0,Math.min(20,prefs.getInt("comment_fetch_limit",0)/500)));label.setText(limitValue(bar.getProgress()*500));box.addView(bar,new LinearLayout.LayoutParams(-1,-2));bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int n,boolean user){label.setText(limitValue(n*500));}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}});AlertDialog d=UiDialogs.builder(c).setTitle(row.getTitle()).setView(box).setPositiveButton(UiStrings.translate("OK"),(dialog,which)->{int limit=bar.getProgress()*500;prefs.edit().putInt("comment_fetch_limit",limit).apply();row.setSummary(limitLabel(limit));}).setNegativeButton(UiStrings.translate("キャンセル"),null).create();PlaybackSession.showDialog(d);return true;});
  Preference group=a.findPreference("comment");if(existing==null)(group instanceof PreferenceGroup?(PreferenceGroup)group:root).addPreference(p);
 }
 private static String limitValue(int value){return value==0?UiStrings.translate("標準（追加取得なし）"):Math.max(0,Math.min(10000,value))+UiStrings.translate("件");}
 private static String limitLabel(int value){String current=value==0?UiStrings.translate("標準（追加取得なし）"):Math.max(0,Math.min(10000,value))+UiStrings.translate("件");return noFinalStop(current+"："+UiStrings.translate("標準の取得結果に過去のコメントを追加します。取得可能な件数は動画とサーバーの応答に依存します。"));}
 private static String modeLabel(String value){return noFinalStop(CachePlaybackPolicy.acquire(value)?UiStrings.translate("キャッシュ：通信しながら再生し、バックグラウンドで動画を保存します。取得済みの動画は保存したデータを再利用します。"):UiStrings.translate("ストリーミング：通信しながら動画を再生します。取得済みのキャッシュがあれば再利用します。"));}
 static String noFinalStop(String text){return text==null?null:text.trim().replaceAll("[。.]$","");}
 public static void stripSummaries(PreferenceActivity a){for(String key:new String[]{"play_mode","comment_fps","comment_fetch_limit","comment_cast_min","nicoid_share_debug"}){Preference p=a.findPreference(key);if(p!=null&&p.getSummary()!=null)p.setSummary(noFinalStop(p.getSummary().toString()));}}
 private static void moveComments(PreferenceActivity a,PreferenceGroup root){Preference category=a.findPreference("comment");if(!(category instanceof PreferenceGroup))return;PreferenceGroup target=(PreferenceGroup)category;java.util.ArrayList<String> keys=new java.util.ArrayList<>();commentKeys(root,keys);for(String key:keys){Preference p=a.findPreference(key);if(p==null)continue;PreferenceGroup old=SettingsTools.parent(root,p);if(old!=null&&old!=target){old.removePreference(p);p.setOrder(target.getPreferenceCount());target.addPreference(p);}}String[] ordered={"comment_fetch_limit","legacy_comment_limit","comment_hide","comment_opacity_percent","comment_alpha","comment_size_percent","comment_size_level","comment_bold","comment_rows","comment_duration_seconds","comment_shadow_tenths","comment_shadow_dp","comment_shadow","comment_fps","comment_cast_min","share_ng_level"};for(int n=0;n<ordered.length;n++){Preference p=a.findPreference(ordered[n]);if(p!=null)p.setOrder(n*10);}int remaining=200;for(String key:keys){if(java.util.Arrays.asList(ordered).contains(key))continue;Preference p=a.findPreference(key);if(p!=null)p.setOrder(remaining++);}}
 private static void commentKeys(PreferenceGroup group,java.util.List<String> keys){for(int n=0;n<group.getPreferenceCount();n++){Preference p=group.getPreference(n);String key=p.getKey();if(key!=null&&(key.startsWith("comment_")||key.equals("share_ng_level")||key.equals("legacy_comment_limit")))keys.add(key);if(p instanceof PreferenceGroup)commentKeys((PreferenceGroup)p,keys);}}
 private static void remove(PreferenceGroup group,String key){for(int n=group.getPreferenceCount()-1;n>=0;n--){Preference p=group.getPreference(n);if(key.equals(p.getKey()))group.removePreference(p);else if(p instanceof PreferenceGroup)remove((PreferenceGroup)p,key);}}
}
