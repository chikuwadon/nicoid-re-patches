package e.e.a;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import org.json.*;
import e.e.a.FollowFeedData.Item;

/** Authenticated, cursor-based uploads from followed accounts. */
public final class FollowFeed {
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor();
    private static final WeakHashMap<Activity,State> STATES = new WeakHashMap<>();
    private static final class State {
        final ArrayList<Item> items = new ArrayList<>();
        final HashSet<String> ids = new HashSet<>();
        ListView list; TextView status; ProgressBar progress; Rows rows;
        NetworkTask task; String cursor; boolean end, busy;
    }
    private FollowFeed() {}
    private static int dp(Activity a,int v) { return Math.round(v*a.getResources().getDisplayMetrics().density); }
    private static String text(String ja,String en,String zh) {
        String setting=android.preference.PreferenceManager.getDefaultSharedPreferences(currentContext).getString("app_lang","0");
        String lang="-1".equals(setting)?Locale.getDefault().getLanguage():setting;
        return "en".equals(lang)||"1".equals(lang)?en:"zh".equals(lang)||"2".equals(lang)?zh:ja;
    }
    private static android.content.Context currentContext;
    public static void load(Activity a) {
        currentContext=a.getApplicationContext();
        try {
            a.getClass().getField("H").setBoolean(a,false);
            a.getClass().getField("I").setBoolean(a,true);
            State s=STATES.get(a);
            if(s==null) {
                s=new State(); s.list=(ListView)PlaybackSession.get(a,"A");
                if(s.list==null)return;
                View footer=(View)PlaybackSession.get(a,"Q");if(footer!=null)s.list.removeFooterView(footer);
                Object bar=PlaybackSession.get(a,"O");if(bar!=null)PlaybackSession.call(bar,"b",new Class<?>[]{CharSequence.class},(Object)null);
                LinearLayout header=new LinearLayout(a);header.setGravity(Gravity.CENTER_VERTICAL);
                header.setPadding(dp(a,12),dp(a,4),dp(a,12),dp(a,4));
                s.status=new TextView(a);s.status.setTextSize(14);s.status.setTextColor(ThemeChoice.textColor(s.status));
                header.addView(s.status,new LinearLayout.LayoutParams(0,-2,1));
                s.progress=new ProgressBar(a);s.progress.getIndeterminateDrawable().mutate().setColorFilter(ThemeChoice.accent(a),PorterDuff.Mode.SRC_IN);
                header.addView(s.progress,new LinearLayout.LayoutParams(dp(a,24),dp(a,24)));
                Button refresh=new Button(a);refresh.setText(text("更新","Refresh","重新整理"));ThemeChoice.button(refresh);
                header.addView(refresh);s.list.addHeaderView(header,null,false);
                State state=s;refresh.setOnClickListener(v->request(a,state,true));
                s.rows=new Rows(a,s);s.list.setAdapter(s.rows);ThemeChoice.background(s.list);
                s.list.setOnItemClickListener((parent,view,position,id)->{
                    int index=position-state.list.getHeaderViewsCount();
                    if(index<0||index>=state.items.size())return;
                    a.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.nicovideo.jp/watch/"+state.items.get(index).id))
                        .setClassName(a,"com.sauzask.nicoid.NicoidVideoActivity"));
                });
                s.list.setOnScrollListener(new AbsListView.OnScrollListener(){
                    public void onScrollStateChanged(AbsListView list,int scrollState){}
                    public void onScroll(AbsListView list,int first,int count,int total){
                        if(count>0 && first+count>=total-4 && !state.items.isEmpty())request(a,state,false);
                    }
                });
                STATES.put(a,s);
                a.getApplication().registerActivityLifecycleCallbacks(new android.app.Application.ActivityLifecycleCallbacks(){
                    public void onActivityCreated(Activity x,android.os.Bundle b){}
                    public void onActivityStarted(Activity x){}
                    public void onActivityResumed(Activity x){if(x==a){ShortImages.resume(a);if(!state.busy&&state.items.isEmpty())request(a,state,true);}}
                    public void onActivityPaused(Activity x){}
                    public void onActivityStopped(Activity x){if(x==a){if(state.task!=null)state.task.cancel();state.busy=false;state.progress.setVisibility(View.GONE);ShortImages.cancel(a);}}
                    public void onActivitySaveInstanceState(Activity x,android.os.Bundle b){}
                    public void onActivityDestroyed(Activity x){if(x==a){STATES.remove(a);a.getApplication().unregisterActivityLifecycleCallbacks(this);}}
                });
            }
            request(a,s,true);
        } catch(Exception error) { android.util.Log.w("nicoid-feed","Feed view failed",error); }
    }
    public static boolean menu(Activity a,Menu menu) {
        currentContext=a.getApplicationContext();menu.clear();
        menu.add(text("更新","Refresh","重新整理")).setOnMenuItemClickListener(item->{load(a);return true;});
        return true;
    }
    private static String cookie(Activity a) throws Exception {
        Class<?> v=Class.forName("e.e.a.v0");Object store=PlaybackSession.get(a,"E");
        if(store==null)store=v.getField("b").get(null);
        if(store==null)return "";
        return (String)v.getMethod("a",Class.forName("org.apache.http.client.CookieStore")).invoke(null,store);
    }
    private static void request(Activity a,State s,boolean refresh) {
        if(a.isFinishing()||a.isDestroyed()||(!refresh&&(s.busy||s.end)))return;
        if(s.task!=null)s.task.cancel();
        if(refresh){s.cursor=null;s.end=false;s.items.clear();s.ids.clear();s.rows.notifyDataSetChanged();}
        String credentials;
        try{credentials=cookie(a);}catch(Exception e){credentials="";}
        if(credentials.isEmpty()){s.status.setText(text("ログインが必要です","Sign in to view followed uploads","請先登入以查看追蹤的新影片"));s.progress.setVisibility(View.GONE);return;}
        final String auth=credentials,cursor=s.cursor;
        NetworkTask task=new NetworkTask();s.task=task;s.busy=true;s.progress.setVisibility(View.VISIBLE);
        s.status.setText(text("読み込み中…","Loading…","載入中…"));
        task.start(WORKER,()->{
            HttpURLConnection c=null;
            try {
                String url="https://api.feed.nicovideo.jp/v1/activities/followings/video?context=my_timeline&limit=50";
                if(cursor!=null)url+="&cursor="+URLEncoder.encode(cursor,"UTF-8");
                c=(HttpURLConnection)new URL(url).openConnection();if(!task.bind(c))return;
                c.setConnectTimeout(10000);c.setReadTimeout(10000);c.setRequestProperty("X-Frontend-Id","6");
                c.setRequestProperty("X-Frontend-Version","0");c.setRequestProperty("Cookie",auth);
                if(c.getResponseCode()!=200)throw new IOException("Feed HTTP "+c.getResponseCode());
                StringBuilder body=new StringBuilder();
                try(Reader reader=new InputStreamReader(c.getInputStream(),"UTF-8")){
                    char[] buf=new char[4096];int n;while((n=reader.read(buf))!=-1){if(task.cancelled())return;body.append(buf,0,n);if(body.length()>4*1024*1024)throw new IOException("Feed response too large");}
                }
                JSONObject response=new JSONObject(body.toString());
                if(!"ok".equals(response.optString("code")))throw new IOException("Feed did not return ok");
                ArrayList<Item> items=FollowFeedData.parse(response);String next=response.optString("nextCursor","");
                MAIN.post(()->{if(task.cancelled()||s.task!=task||a.isDestroyed())return;
                    for(Item item:items)if(s.ids.add(item.id))s.items.add(item);
                    s.cursor=next.isEmpty()?null:next;s.end=s.cursor==null||next.equals(cursor);s.busy=false;
                    s.rows.notifyDataSetChanged();s.progress.setVisibility(View.GONE);
                    s.status.setText(s.items.isEmpty()?text("新着動画はありません","No new videos","沒有新影片"):text("フォロー新着","Following uploads","追蹤的新影片"));
                });
            } catch(Exception error) {
                MAIN.post(()->{if(task.cancelled()||s.task!=task||a.isDestroyed())return;s.busy=false;s.progress.setVisibility(View.GONE);
                    s.status.setText(text("取得できませんでした。更新して再試行してください","Could not load. Refresh to retry","無法取得，請重新整理以重試"));});
                android.util.Log.w("nicoid-feed","Feed request failed",error);
            } finally {if(c!=null){task.release(c);c.disconnect();}}
        });
    }
    private static final class Rows extends BaseAdapter {
        final Activity a;final State s;Rows(Activity a,State s){this.a=a;this.s=s;}
        public int getCount(){return s.items.size();}public Object getItem(int i){return s.items.get(i);}public long getItemId(int i){return i;}
        public View getView(int i,View recycled,ViewGroup parent){
            LinearLayout row;ImageView image;TextView placeholder,title,meta;
            if(recycled==null){
                row=new LinearLayout(a);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(a,12),dp(a,8),dp(a,12),dp(a,8));
                FrameLayout thumb=new FrameLayout(a);image=new ImageView(a);image.setScaleType(ImageView.ScaleType.CENTER_CROP);thumb.addView(image,new FrameLayout.LayoutParams(-1,-1));
                placeholder=new TextView(a);placeholder.setText("▶");placeholder.setGravity(Gravity.CENTER);placeholder.setTextColor(ThemeChoice.accent(a));thumb.addView(placeholder,new FrameLayout.LayoutParams(-1,-1));
                row.addView(thumb,new LinearLayout.LayoutParams(dp(a,112),dp(a,64)));
                LinearLayout labels=new LinearLayout(a);labels.setOrientation(LinearLayout.VERTICAL);labels.setPadding(dp(a,12),0,0,0);
                title=new TextView(a);title.setTextSize(16);title.setMaxLines(2);title.setEllipsize(android.text.TextUtils.TruncateAt.END);title.setTextColor(ThemeChoice.textColor(title));
                meta=new TextView(a);meta.setTextSize(12);meta.setTextColor(ThemeChoice.textColor(meta));meta.setAlpha(.7f);labels.addView(title);labels.addView(meta);row.addView(labels,new LinearLayout.LayoutParams(0,-2,1));
                row.setTag(new View[]{image,placeholder,title,meta});
            }else{row=(LinearLayout)recycled;View[] v=(View[])row.getTag();image=(ImageView)v[0];placeholder=(TextView)v[1];title=(TextView)v[2];meta=(TextView)v[3];}
            Item item=s.items.get(i);title.setText(item.title);meta.setText(item.actor+"  "+item.date.replace('T',' ').replaceAll("[+]\\d\\d:\\d\\d$|Z$",""));
            image.setImageDrawable(null);image.setTag(item.thumbnail);placeholder.setVisibility(View.VISIBLE);ShortImages.load(item.thumbnail,image,placeholder);return row;
        }
    }
}
