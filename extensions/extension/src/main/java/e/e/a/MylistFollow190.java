package e.e.a;

import android.app.*;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.util.concurrent.*;import java.util.ArrayList;

/** Following belongs to the signed-in account, never to a local bookmark. */
public final class MylistFollow190 {
 private static final ExecutorService WORK=Executors.newFixedThreadPool(2);
 private static final android.os.Handler MAIN=new android.os.Handler(android.os.Looper.getMainLooper());
 private MylistFollow190(){}
 static String id(Activity a){android.net.Uri uri=a.getIntent().getData();if(uri==null)return "";String host=uri.getHost(),path=uri.getPath();return host!=null&&(host.equals("nicovideo.jp")||host.endsWith(".nicovideo.jp"))&&path!=null&&path.matches("/mylist/[0-9]+")?uri.getLastPathSegment():"";}
 static String label(Activity a,boolean followed){return followed?PanelUi.tr(a,"マイリストのフォローを解除","Unfollow mylist","取消追蹤播放清單"):PanelUi.tr(a,"マイリストをフォロー","Follow mylist","追蹤播放清單");}
 static void menu(Activity a,Menu menu){try{if(a.getClass().getField("E").getInt(a)!=5)return;String id=id(a);if(id.isEmpty())return;menu.removeItem(18190);MenuItem item=menu.add(0,18190,90,PanelUi.tr(a,"マイリストのフォロー…","Mylist following…","播放清單追蹤…"));item.setOnMenuItemClickListener(v->{show(a,id);return true;});}catch(Exception e){android.util.Log.w("nicoid-following","Unable to add mylist action",e);}}
 static String folderId(java.util.Map<?,?> row){Object url=row.get("url");if(url==null)return "";String link=String.valueOf(url);return link.matches("https?://(?:www\\.)?nicovideo\\.jp/mylist/[0-9]+")?link.substring(link.lastIndexOf('/')+1):"";}
 public static void userFolders(Object callback){try{Object fragment=Review181.field(callback,"b");Activity a=(Activity)Review181.field(fragment,"b0");ListView list=(ListView)Review181.field(fragment,"d0");list.setOnItemLongClickListener((parent,view,position,rowId)->{try{ArrayList<?> rows=(ArrayList<?>)Review181.field(fragment,"g0");int index=position-list.getHeaderViewsCount();if(index<0||index>=rows.size())return false;Object row=rows.get(index);if(!(row instanceof java.util.Map))return false;String id=folderId((java.util.Map<?,?>)row);if(id.isEmpty())return false;show(a,id);return true;}catch(Exception e){return false;}});}catch(Exception e){android.util.Log.w("nicoid-following","Unable to bind public mylist folders",e);}}
 static void show(Activity a,String id){if(!id.matches("[0-9]+"))return;VideoDetails.initializeCookies(a);if(!VideoDetails.cookie().contains("user_session=")){Toast.makeText(a,PanelUi.tr(a,"ログインしてください","Sign in first","請先登入"),1).show();return;}
  LinearLayout box=PanelUi.column(PlaybackSession.dialogContext(a));UiDialogs.insetContent(box);TextView status=PanelUi.text(box.getContext(),PanelUi.tr(a,"読み込み中…","Loading…","載入中…"),14);box.addView(status);AlertDialog dialog=UiDialogs.builder(box.getContext()).setTitle(PanelUi.tr(a,"マイリストのフォロー","Mylist following","播放清單追蹤")).setView(box).setNegativeButton(UiStrings.translate("キャンセル"),null).create();NetworkTask[] active={new NetworkTask()};dialog.setOnDismissListener(d->active[0].cancel());UiDialogs.show(dialog);
  active[0].start(WORK,()->{try{JSONObject mylist=VideoDetails.request("https://nvapi.nicovideo.jp/v2/mylists/"+id+"?pageSize=1","GET",true,active[0]).getJSONObject("data").getJSONObject("mylist");if(!mylist.has("isFollowing"))throw new java.io.IOException("Missing follow status");boolean followed=mylist.getBoolean("isFollowing");MAIN.post(()->{if(active[0].cancelled()||a.isFinishing()||!dialog.isShowing())return;status.setText(mylist.optString("name"));Button change=PanelUi.button(box.getContext(),label(a,followed));box.addView(change);change.setOnClickListener(v->{change.setEnabled(false);NetworkTask task=new NetworkTask();active[0]=task;task.start(WORK,()->{try{VideoDetails.request("https://nvapi.nicovideo.jp/v1/users/me/following/mylists/"+id,followed?"DELETE":"POST",true,task);MAIN.post(()->{if(task.cancelled()||a.isFinishing())return;dialog.dismiss();Toast.makeText(a,PanelUi.tr(a,followed?"フォローを解除しました":"フォローしました",followed?"Unfollowed":"Followed",followed?"已取消追蹤":"已追蹤"),0).show();});}catch(Exception e){MAIN.post(()->{if(task.cancelled()||!dialog.isShowing())return;change.setEnabled(true);status.setText(PanelUi.tr(a,"変更できませんでした。再試行してください","Unable to update. Retry","無法更新，請重試"));});}});});});}catch(Exception e){MAIN.post(()->{if(active[0].cancelled()||!dialog.isShowing())return;status.setText(PanelUi.tr(a,"フォロー状態を取得できませんでした","Unable to load following status","無法載入追蹤狀態"));});}});
 }
}
