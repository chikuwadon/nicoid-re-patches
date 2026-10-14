package e.e.a;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Creator content page for Shorts and series. */
public final class CreatorPage {
    private static final ExecutorService WORK = Executors.newFixedThreadPool(2);
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private CreatorPage() {}

    private static String tr(Context c, String ja, String en, String zh) { return PanelUi.tr(c, ja, en, zh); }

    public static void launch(Context c, String userId, String name) {
        if (userId == null || !userId.matches("[0-9]+")) return;
        c.startActivity(new Intent().setClassName(c, "com.sauzask.nicoid.NicoidNicorepoActivity")
            .putExtra("nicoid_creator_page", userId).putExtra("nicoid_creator_name", name == null ? "" : name));
    }

    public static void decorateUser(Activity a) {
        String uri=a.getIntent().getDataString();if(uri==null)return;
        java.util.regex.Matcher match=java.util.regex.Pattern.compile("/user/([0-9]+)").matcher(uri);if(!match.find())return;
        String user=match.group(1);
        a.getWindow().getDecorView().post(()->{try{
            LinearLayout root=(LinearLayout)Review181.field(a,"z");if(root==null||root.findViewWithTag("nicoid_creator_tabs")!=null)return;
            LinearLayout tabs=new LinearLayout(a);tabs.setTag("nicoid_creator_tabs");
            for(boolean series:new boolean[]{false,true}){Button button=PanelUi.button(a,series?tr(a,"シリーズ","Series","系列"):tr(a,"ショート","Shorts","短影片"));tabs.addView(button,new LinearLayout.LayoutParams(0,-2,1));button.setOnClickListener(v->a.startActivity(new Intent().setClassName(a,"com.sauzask.nicoid.NicoidNicorepoActivity").putExtra("nicoid_creator_page",user).putExtra("nicoid_creator_series",series).putExtra("nicoid_creator_name","")));}
            root.addView(tabs,0);
        }catch(Exception e){android.util.Log.w("nicoid-creator","Unable to add creator tabs",e);}});
    }

    public static boolean open(Activity a) {
        if (a.getIntent().getBooleanExtra("nicoid_followed_channels", false)) return openChannels(a);
        if (a.getIntent().getBooleanExtra("nicoid_followed_mylists", false)) return openMylists(a);
        String userId = a.getIntent().getStringExtra("nicoid_creator_page");
        if (userId == null) return false;
        if (!userId.matches("[0-9]+")) { a.finish(); return true; }
        try {
            LinearLayout page = PanelUi.column(a); ThemeChoice.background(page);
            a.getClass().getField("y").set(a, page);
            a.getClass().getField("H").setBoolean(a, false); a.getClass().getField("I").setBoolean(a, true);
            Object toolbar = Review181.field(a, "O");
            String name = a.getIntent().getStringExtra("nicoid_creator_name");
            String title = name == null || name.isEmpty() ? tr(a, "投稿者", "Creator", "投稿者") : name;
            toolbar.getClass().getMethod("b", CharSequence.class).invoke(toolbar, title);
            toolbar.getClass().getMethod("c", CharSequence.class).invoke(toolbar, tr(a, "ショート・シリーズ", "Shorts · Series", "短影片・系列"));

            if(name==null||name.isEmpty()||name.equals("投稿者")||name.matches("[0-9]+")){NetworkTask profile=new NetworkTask();profile.start(WORK,()->{try{JSONObject response=VideoDetails.request("https://nvapi.nicovideo.jp/v1/users/"+userId,"GET",false,profile).optJSONObject("data");JSONObject user=response==null?null:response.optJSONObject("user");if(user==null)user=response;final String nickname=user==null?"":user.optString("nickname",user.optString("name",""));MAIN.post(()->{if(a.isFinishing()||nickname.isEmpty())return;try{toolbar.getClass().getMethod("b",CharSequence.class).invoke(toolbar,nickname);}catch(Exception ignored){}});}catch(Exception ignored){}});}
            LinearLayout tabs = new LinearLayout(a); tabs.setGravity(Gravity.CENTER_VERTICAL);
            Button shorts = PanelUi.button(a, tr(a, "ショート", "Shorts", "短影片"));
            Button series = PanelUi.button(a, tr(a, "シリーズ", "Series", "系列"));
            tabs.addView(shorts, new LinearLayout.LayoutParams(0, -2, 1));
            tabs.addView(series, new LinearLayout.LayoutParams(0, -2, 1)); page.addView(tabs);
            ScrollView scroll = new ScrollView(a); LinearLayout rows = PanelUi.column(a); scroll.addView(rows);
            page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
            shorts.setOnClickListener(v -> loadShorts(a, userId, rows));
            series.setOnClickListener(v -> loadSeries(a, userId, rows));
            if(a.getIntent().getBooleanExtra("nicoid_creator_series",false))loadSeries(a,userId,rows);else loadShorts(a, userId, rows); return true;
        } catch (Exception e) {
            android.util.Log.w("nicoid-creator", "Unable to open creator page", e);
            Toast.makeText(a, tr(a, "投稿者ページを開けませんでした", "Unable to open creator page", "無法開啟投稿者頁面"), Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private static boolean openChannels(Activity a) {
        try {
            LinearLayout page=PanelUi.column(a);ThemeChoice.background(page);a.getClass().getField("y").set(a,page);
            a.getClass().getField("H").setBoolean(a,false);a.getClass().getField("I").setBoolean(a,true);
            Object toolbar=Review181.field(a,"O");toolbar.getClass().getMethod("b",CharSequence.class).invoke(toolbar,tr(a,"フォロー中のチャンネル","Followed channels","已追蹤的頻道"));toolbar.getClass().getMethod("c",CharSequence.class).invoke(toolbar,"");
            page.addView(PanelUi.text(a,tr(a,"読み込み中…","Loading…","載入中…"),14));NetworkTask task=new NetworkTask();task.start(WORK,()->{
                try{ArrayList<JSONObject> values=followedChannels(task);
                    MAIN.post(()->{if(task.cancelled()||a.isFinishing())return;page.removeAllViews();if(values.isEmpty()){page.addView(PanelUi.text(a,tr(a,"フォロー中のチャンネルはありません","No followed channels found","沒有已追蹤的頻道"),14));return;}for(JSONObject channel:values){channelRow(page,channel);}});
                }catch(Exception e){MAIN.post(()->{if(!task.cancelled()&&!a.isFinishing()){page.removeAllViews();page.addView(PanelUi.text(a,tr(a,"チャンネル一覧を取得できませんでした","Unable to load followed channels","無法載入已追蹤頻道"),14));}});}
            });return true;
        }catch(Exception e){android.util.Log.w("nicoid-creator","Unable to open followed channels",e);return true;}
    }

    private static ArrayList<JSONObject> followedChannels(NetworkTask task)throws Exception {
        ArrayList<JSONObject> values=new ArrayList<>(); java.util.HashSet<String> seen=new java.util.HashSet<>();
        for(int offset=0;;offset+=100) {
            if(task.cancelled())throw new java.io.InterruptedIOException();
            JSONObject response=VideoDetails.request("https://public-api.ch.nicovideo.jp/v1/user/followees/channels?limit=100&offset="+offset,"GET",true,task);
            JSONObject data=response.optJSONObject("data");JSONArray items=response.optJSONArray("data");
            if(items==null&&data!=null)for(String key:new String[]{"channels","items","followees"}){items=data.optJSONArray(key);if(items!=null)break;}
            if(items==null)throw new java.io.IOException("Missing followed channels");
            int added=0;
            for(int n=0;n<items.length();n++) {
                JSONObject item=items.optJSONObject(n);if(item==null)continue;JSONObject channel=item.optJSONObject("channel");if(channel!=null)item=channel;
                String id=item.optString("id",item.optString("channelId",""));if(!id.startsWith("ch"))id="ch"+id;
                if(id.matches("ch[0-9]+")&&seen.add(id)) {values.add(new JSONObject().put("id",id).put("name",item.optString("name",item.optString("channelName",id))).put("iconUrl",item.optString("iconUrl",item.optString("thumbnailUrl",""))));added++;}
            }
            if(items.length()<100)break;
            if(added==0)throw new java.io.IOException("Repeated channel page");
        }
        enrichChannelIcons(values,task);return values;
    }

    private static JSONArray allPages(String url, NetworkTask task) throws Exception {
        JSONArray result = new JSONArray(); java.util.HashSet<String> seen = new java.util.HashSet<>();
        for (int page=1; ;page++) {
            if (task.cancelled()) throw new java.io.InterruptedIOException();
            JSONObject data = VideoDetails.request(url + "&page=" + page,"GET",false,task).optJSONObject("data");
            if (data == null || data.optJSONArray("items") == null) throw new java.io.IOException("Missing page items");
            JSONArray items = data.optJSONArray("items"); int added=0;
            for (int n=0;n<items.length();n++) {
                JSONObject item=items.optJSONObject(n); if(item==null) continue;
                JSONObject video=CreatorData.video(item);
                String id=video.optString("id",video.optString("contentId",video.optString("watchId",item.optString("seriesId",""))));
                if(seen.add(id.isEmpty()?item.toString():id)){result.put(item);added++;}
            }
            if (!PageRules.more(data,items.length(),page,100)) break;
            if (added==0) throw new java.io.IOException("Repeated creator page");
        }
        return result;
    }

    private static void enrichChannelIcons(ArrayList<JSONObject> channels,NetworkTask task){boolean missing=false;for(JSONObject channel:channels)if(channel.optString("iconUrl").isEmpty())missing=true;if(!missing)return;try{JSONObject response=VideoDetails.request("https://api.feed.nicovideo.jp/v1/actors?limit=50","GET",true,task);for(FollowFeedData.Actor actor:FollowFeedData.actors(response)){String id=actor.id.startsWith("ch")?actor.id:"ch"+actor.id;for(JSONObject channel:channels)if(id.equals(channel.optString("id"))&&!actor.icon.isEmpty())channel.put("iconUrl",actor.icon);}}catch(Exception ignored){}}
    /** Keep native user rows and their actions, adding channels in the same scrolling list. */
    public static void following(Activity a) {
        try {
            android.widget.ListView list=(android.widget.ListView)Review181.field(a,"A");
            VideoDetails.initializeCookies(a);
            LinearLayout channels=PanelUi.column(a);
            channels.setTag("nicoid_followed_channels");
            channels.setPadding(0,0,0,0);
            ThemeChoice.background(channels);
            if(list.findViewWithTag("nicoid_followed_channels")!=null)return;
            TextView users=category(a,tr(a,"ユーザー","Users","使用者"));list.addHeaderView(users,null,false);
            list.addFooterView(channels,null,false);
            NetworkTask task=new NetworkTask();
            channels.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener(){
                public void onViewAttachedToWindow(View v){}
                public void onViewDetachedFromWindow(View v){task.cancel();}
            });
            loadFollowingChannels(a,channels,task);
        } catch(Exception error){android.util.Log.w("nicoid-creator","Unable to add followed channels",error);}
    }

    private static void loadFollowingChannels(Activity a,LinearLayout rows,NetworkTask task) {
        rows.removeAllViews();
        TextView loading=PanelUi.text(a,tr(a,"チャンネルを読み込み中…","Loading channels…","載入頻道…"),14);
        rows.addView(loading);
        task.start(WORK,()->{
            try {
                ArrayList<JSONObject> channels=followedChannels(task);
                MAIN.post(()->{
                    if(task.cancelled()||a.isFinishing())return;
                    rows.removeAllViews();
                    if(!channels.isEmpty())rows.addView(category(a,tr(a,"公式チャンネル","Official channels","官方頻道")));
                    for(JSONObject channel:channels){
                        channelRow(rows,channel);
                    }
                });
            } catch(Exception error){MAIN.post(()->{
                if(task.cancelled()||a.isFinishing())return;
                rows.removeAllViews();Button retry=PanelUi.button(a,tr(a,"チャンネルを取得できませんでした。再試行","Unable to load channels. Retry","無法載入頻道。重試"));
                retry.setOnClickListener(v->loadFollowingChannels(a,rows,new NetworkTask()));rows.addView(retry);
            });}
        });
    }

    private static void loadShorts(Activity a, String user, LinearLayout rows) {
        NetworkTask previous = rows.getTag() instanceof NetworkTask ? (NetworkTask)rows.getTag() : null;
        if (previous != null) previous.cancel();
        rows.removeAllViews(); rows.addView(PanelUi.text(a, tr(a, "読み込み中…", "Loading…", "載入中…"), 14));
        NetworkTask task = new NetworkTask(); rows.setTag(task);
        task.start(WORK, () -> {
            try {
                JSONArray items = allPages("https://nvapi.nicovideo.jp/v3/users/" + user +
                    "/videos?selectContentType=short&sortKey=registeredAt&sortOrder=desc&pageSize=100",task);
                ArrayList<String> ids = new ArrayList<>(), titles = new ArrayList<>(), thumbs = new ArrayList<>();ArrayList<JSONObject> videos=new ArrayList<>();
                if (items != null) for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.optJSONObject(i); if (item == null) continue;
                    item=CreatorData.video(item);
                    String id = item.optString("id", item.optString("contentId", item.optString("watchId", "")));
                    if (!id.matches("ss[0-9]+")) continue;
                    videos.add(item);ids.add(id); titles.add(item.optString("title", id)); thumbs.add(thumbnail(item));
                }
                String[] idArray = ids.toArray(new String[0]), titleArray = titles.toArray(new String[0]), thumbArray = thumbs.toArray(new String[0]);
                MAIN.post(() -> {
                    if (task.cancelled() || rows.getTag() != task || a.isFinishing()) return;
                    rows.removeAllViews();
                    if (idArray.length == 0) { rows.addView(PanelUi.text(a, tr(a, "ショート動画はありません", "No Shorts found", "沒有短影片"), 14)); return; }
                    LinearLayout playback=new LinearLayout(a);CheckBox continuous=new CheckBox(a),random=new CheckBox(a);continuous.setText(tr(a,"連続再生","Continuous playback","連續播放"));random.setText(tr(a,"シャッフル再生","Shuffle playback","隨機播放"));PanelUi.tint(continuous);PanelUi.tint(random);playback.addView(continuous,new LinearLayout.LayoutParams(0,-2,1));playback.addView(random,new LinearLayout.LayoutParams(0,-2,1));rows.addView(playback);android.content.SharedPreferences prefs=android.preference.PreferenceManager.getDefaultSharedPreferences(a);int savedMode=prefs.getInt("creator_shorts_play_"+user,0);continuous.setChecked(savedMode==1);random.setChecked(savedMode==2);continuous.setOnCheckedChangeListener((button,on)->{if(on)random.setChecked(false);prefs.edit().putInt("creator_shorts_play_"+user,random.isChecked()?2:continuous.isChecked()?1:0).apply();});random.setOnCheckedChangeListener((button,on)->{if(on)continuous.setChecked(false);prefs.edit().putInt("creator_shorts_play_"+user,random.isChecked()?2:continuous.isChecked()?1:0).apply();});
                    for (int n = 0; n < idArray.length; n++) {
                        final int index=n;VideoDetails.videoRow(rows,videos.get(n),"",()->ModernShorts.openVideos(a,idArray,titleArray,thumbArray,index,continuous.isChecked()||random.isChecked(),random.isChecked()));
                    }
                });
            } catch (Exception e) {
                MAIN.post(() -> { if (!task.cancelled() && rows.getTag() == task && !a.isFinishing()) {
                    rows.removeAllViews(); rows.addView(PanelUi.text(a, tr(a, "ショート動画を取得できませんでした", "Unable to load Shorts", "無法載入短影片"), 14));
                }});
            }
        });
    }

    private static void loadSeries(Activity a, String user, LinearLayout rows) {
        NetworkTask previous = rows.getTag() instanceof NetworkTask ? (NetworkTask)rows.getTag() : null;
        if (previous != null) previous.cancel();
        rows.removeAllViews(); rows.addView(PanelUi.text(a, tr(a, "読み込み中…", "Loading…", "載入中…"), 14));
        NetworkTask task = new NetworkTask(); rows.setTag(task);
        task.start(WORK, () -> {
            try {
                JSONArray items = allPages("https://nvapi.nicovideo.jp/v1/users/" + user + "/series?pageSize=100",task);
                ArrayList<JSONObject> series = new ArrayList<>(); if (items != null) for (int i=0;i<items.length();i++) {
                    JSONObject item=items.optJSONObject(i); if(item!=null) series.add(item);
                }
                MAIN.post(() -> {
                    if (task.cancelled() || rows.getTag() != task || a.isFinishing()) return; rows.removeAllViews();
                    if (series.isEmpty()) { rows.addView(PanelUi.text(a, tr(a, "シリーズはありません", "No series found", "沒有系列"), 14)); return; }
                    for (JSONObject item : series) {
                        String id=item.optString("id",item.optString("seriesId","")); if(!id.matches("[0-9]+"))continue;
                        String label=item.optString("title",item.optString("name",id)); Button row=PanelUi.button(a,label);
                        row.setGravity(Gravity.START|Gravity.CENTER_VERTICAL); row.setOnClickListener(v -> { task.cancel(); rows.removeAllViews(); rows.addView(PanelUi.text(a,label,18)); VideoDetails.seriesVideos(rows,id); }); rows.addView(row); PanelUi.divider(rows);
                    }
                });
            } catch (Exception e) {
                MAIN.post(() -> { if (!task.cancelled() && rows.getTag() == task && !a.isFinishing()) { rows.removeAllViews(); rows.addView(PanelUi.text(a,
                    tr(a, "シリーズを取得できませんでした", "Unable to load series", "無法載入系列"), 14)); }});
            }
        });
    }


    private static boolean openMylists(Activity a) {
        try {
            VideoDetails.initializeCookies(a);
            LinearLayout page=PanelUi.column(a);ThemeChoice.background(page);
            a.getClass().getField("y").set(a,page);a.getClass().getField("H").setBoolean(a,false);a.getClass().getField("I").setBoolean(a,true);
            Object toolbar=Review181.field(a,"O");toolbar.getClass().getMethod("b",CharSequence.class).invoke(toolbar,tr(a,"フォロー中のマイリスト","Followed mylists","已追蹤的播放清單"));toolbar.getClass().getMethod("c",CharSequence.class).invoke(toolbar,"");
            ScrollView scroll=new ScrollView(a);LinearLayout rows=PanelUi.column(a);scroll.addView(rows);page.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
            loadMylists(a,rows,1);return true;
        } catch(Exception e){android.util.Log.w("nicoid-creator","Unable to open followed mylists",e);return true;}
    }
    private static void loadMylists(Activity a,LinearLayout rows,int page) {
        Button more=PanelUi.button(a,tr(a,"読み込み中…","Loading…","載入中…"));more.setEnabled(false);rows.addView(more);
        NetworkTask task=new NetworkTask();rows.setTag(task);
        rows.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener(){public void onViewAttachedToWindow(View v){}public void onViewDetachedFromWindow(View v){task.cancel();}});
        task.start(WORK,()->{
            try {
                JSONObject data=VideoDetails.request("https://nvapi.nicovideo.jp/v1/users/me/following/mylists?pageSize=100&page="+page,"GET",true,task).optJSONObject("data");
                if(data==null)throw new java.io.IOException("Missing mylists");
                JSONArray items=data.optJSONArray("items");if(items==null)items=data.optJSONArray("mylists");
                final JSONArray result=items;final boolean next=data.optBoolean("hasNext",items!=null&&items.length()==100);
                MAIN.post(()->{
                    if(task.cancelled()||a.isFinishing())return;rows.removeView(more);
                    if(result!=null)for(int n=0;n<result.length();n++){
                        JSONObject item=result.optJSONObject(n);if(item==null)continue;JSONObject nested=item.optJSONObject("mylist");if(nested!=null)item=nested;
                        String id=item.optString("id",item.optString("mylistId",""));if(!id.matches("[0-9]+"))continue;
                        String name=item.optString("name",item.optString("title",id));Button row=PanelUi.button(a,name);row.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
                        row.setOnClickListener(v->a.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.nicovideo.jp/mylist/"+id)).setClassName(a,"com.sauzask.nicoid.NicoidVideoListActivity").putExtra("type",5).putExtra("name",name)));rows.addView(row);PanelUi.divider(rows);
                    }
                    if(page==1&&rows.getChildCount()==0)rows.addView(PanelUi.text(a,tr(a,"フォロー中のマイリストはありません","No followed mylists","沒有已追蹤的播放清單"),14));
                    if(next&&result!=null&&result.length()>0){more.setText(tr(a,"もっと見る","View more","查看更多"));more.setEnabled(true);more.setOnClickListener(v->{rows.removeView(more);loadMylists(a,rows,page+1);});rows.addView(more);}
                });
            }catch(Exception e){MAIN.post(()->{if(!task.cancelled()&&!a.isFinishing()){more.setText(tr(a,"取得できませんでした。再試行","Unable to load. Retry","無法載入。重試"));more.setEnabled(true);more.setOnClickListener(v->{rows.removeView(more);loadMylists(a,rows,page);});}});}
        });
    }


    private static TextView category(Context c,String label){TextView title=PanelUi.text(c,label,14);title.setPadding(PanelUi.dp(c,12),PanelUi.dp(c,8),PanelUi.dp(c,12),PanelUi.dp(c,8));title.setBackgroundColor(UiDialogs.accent(c)&0xffffff|0x22000000);return title;}
    private static void unfollowChannel(LinearLayout rows,View row,JSONObject channel){Context c=rows.getContext();Activity a=c instanceof Activity?(Activity)c:null;android.app.AlertDialog d=new android.app.AlertDialog.Builder(PlaybackSession.dialogContext(c)).setMessage(channel.optString("name")+"\n"+tr(c,"フォローを解除しますか？","Unfollow this channel?","取消追蹤此頻道？")).setPositiveButton(tr(c,"フォロー解除","Unfollow","取消追蹤"),(x,w)->{row.setEnabled(false);NetworkTask task=new NetworkTask();task.start(WORK,()->{boolean ok=false;try{VideoDetails.initializeCookies(c);ModernPosting.jsonPost("https://public-api.ch.nicovideo.jp/v1/user/follow",new JSONObject().put("channelId",Long.parseLong(channel.optString("id").replaceFirst("^ch",""))).put("mode","delete"),task);ok=true;}catch(Exception e){android.util.Log.w("nicoid-creator","Unable to unfollow channel: "+e.getClass().getSimpleName());}boolean success=ok;MAIN.post(()->{if(a!=null&&a.isFinishing())return;if(success){int n=rows.indexOfChild(row);if(n>=0){if(n+1<rows.getChildCount()&&rows.getChildAt(n+1).getLayoutParams().height<=PanelUi.dp(c,2))rows.removeViewAt(n+1);rows.removeView(row);}}else{row.setEnabled(true);Toast.makeText(c,tr(c,"フォロー解除に失敗しました","Unable to unfollow","取消追蹤失敗"),1).show();}});});}).setNegativeButton(tr(c,"キャンセル","Cancel","取消"),null).create();PlaybackSession.showDialog(d);}
    private static void channelRow(LinearLayout rows,JSONObject channel){Context c=rows.getContext();LinearLayout row=new LinearLayout(c);row.setGravity(Gravity.CENTER_VERTICAL);row.setMinimumHeight(PanelUi.dp(c,56));android.widget.FrameLayout avatar=new android.widget.FrameLayout(c);android.widget.ImageView icon=new android.widget.ImageView(c);icon.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);TextView placeholder=PanelUi.text(c,"▶",14);avatar.addView(placeholder,new android.widget.FrameLayout.LayoutParams(-1,-1));avatar.addView(icon,new android.widget.FrameLayout.LayoutParams(-1,-1));LinearLayout.LayoutParams image=new LinearLayout.LayoutParams(PanelUi.dp(c,50),PanelUi.dp(c,50));image.setMargins(PanelUi.dp(c,3),PanelUi.dp(c,3),0,PanelUi.dp(c,3));row.addView(avatar,image);TextView name=PanelUi.text(c,channel.optString("name",channel.optString("id")),14);name.setPadding(PanelUi.dp(c,6),0,0,0);name.setGravity(Gravity.CENTER_VERTICAL);row.addView(name,new LinearLayout.LayoutParams(0,PanelUi.dp(c,56),1));row.setOnClickListener(v->ChannelPage.launch(c,channel));row.setOnLongClickListener(v->{unfollowChannel(rows,row,channel);return true;});rows.addView(row);PanelUi.divider(rows);ShortImages.load(channel.optString("iconUrl"),icon,placeholder);}

    private static String thumbnail(JSONObject item) {
        String value=item.optString("thumbnailUrl", ""); JSONObject thumb=item.optJSONObject("thumbnail");
        if(value.isEmpty()&&thumb!=null)value=thumb.optString("largeUrl",thumb.optString("url",thumb.optString("middleUrl","")));
        return value;
    }
}
