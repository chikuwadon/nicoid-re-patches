package e.e.a;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/** Persistent transfer feedback for the cache service. */
public final class CacheProgress {
    private static int ID = 18421;
    private static final String CHANNEL = "nicoid_cache_download";
    private static final AtomicLong BYTES = new AtomicLong();
    private static final AtomicLong ASSET_BYTES = new AtomicLong();
    private static volatile boolean completed;
    private static volatile int plannedAssets;private static final java.util.concurrent.atomic.AtomicInteger finishedAssets=new java.util.concurrent.atomic.AtomicInteger();
    public static void assetDone(){ASSET_BYTES.set(0);assetLength=-1;finishedAssets.incrementAndGet();}
    private static int percent(){if(completed)return 100;if(totalBytes>0)return (int)Math.min(99,BYTES.get()*100/totalBytes);if(plannedAssets>0){double part=assetLength>0?Math.min(1,ASSET_BYTES.get()/(double)assetLength):0;return (int)Math.min(99,(finishedAssets.get()+part)*100/plannedAssets);}return 0;}
    private static volatile int assetLength = -1;
    private static volatile long totalBytes=-1;
    private static volatile String videoTitle="";
    private static volatile Context app;
    private static volatile long lastUpdate;
    private static volatile int generation;
    private static final java.util.concurrent.ExecutorService SIZE_WORK=java.util.concurrent.Executors.newSingleThreadExecutor();
    private static java.util.concurrent.Future<?> sizeTask;
    private static void cancelSize(){generation++;if(sizeTask!=null){sizeTask.cancel(true);sizeTask=null;}}
    private static android.app.PendingIntent cancel;
    private static Context tintContext;
    private CacheProgress() {}

    public static void start(Context context) {
        // Update the service's existing foreground notification, retaining its stop action.
        try{ID=context.getClass().getField("a").getInt(context);cancel=(android.app.PendingIntent)context.getClass().getField("e").get(context);}catch(Exception ignored){ID=18421;cancel=null;}
        cancelSize();completed=false;plannedAssets=0;finishedAssets.set(0);app = context.getApplicationContext(); BYTES.set(0); ASSET_BYTES.set(0); assetLength=-1; totalBytes=-1; videoTitle=""; lastUpdate=0;
        android.content.SharedPreferences settings=android.preference.PreferenceManager.getDefaultSharedPreferences(app);
        String mode=ThemeRules.mode(settings.getString("app_theme",null),settings.getBoolean("material_you_mode",false));
        boolean night=(app.getResources().getConfiguration().uiMode&android.content.res.Configuration.UI_MODE_NIGHT_MASK)==android.content.res.Configuration.UI_MODE_NIGHT_YES;
        int theme=app.getResources().getIdentifier(ThemeRules.style(mode,night,Build.VERSION.SDK_INT),"style",app.getPackageName());
        tintContext=theme==0?context:new android.view.ContextThemeWrapper(app,theme);
        NotificationManager manager = (NotificationManager)app.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;
        if (Build.VERSION.SDK_INT >= 26) manager.createNotificationChannel(new NotificationChannel(CHANNEL, UiStrings.translate("キャッシュ取得"), NotificationManager.IMPORTANCE_LOW));
        publish(false);
    }
    public static void title(String value){videoTitle=value==null?"":value;publish(false);}
    public static void prepare(String url,Object cache){
        if(app==null)return;final int session=generation;sizeTask=SIZE_WORK.submit(()->measure(url,cache,session));
    }
    private static void measure(String url,Object cache,int session){
        if(app==null||session!=generation)return;
        try{java.lang.reflect.Field f=cache.getClass().getDeclaredField("cookie");f.setAccessible(true);String cookie=(String)f.get(cache);
            long measured=CacheTransferPlan.bytes(new java.net.URL(url),new CacheTransferPlan.Fetch(){public void assets(int count){if(session==generation)plannedAssets=count;}public String text(java.net.URL target)throws java.io.IOException{
                if(session!=generation||Thread.currentThread().isInterrupted())throw new java.io.InterruptedIOException();
                if(!"https".equals(target.getProtocol()))throw new java.io.IOException("Invalid media URL");
                java.net.HttpURLConnection c=(java.net.HttpURLConnection)target.openConnection();c.setConnectTimeout(8000);c.setReadTimeout(8000);
                if(cookie!=null&&!cookie.isEmpty())c.setRequestProperty("Cookie",cookie);
                try(java.io.InputStream input=c.getInputStream();java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream()){
                    byte[] buffer=new byte[8192];int n;while((n=input.read(buffer))!=-1){if(out.size()+n>4*1024*1024)throw new java.io.IOException("Playlist too large");out.write(buffer,0,n);}return out.toString("UTF-8");
                }finally{c.disconnect();}
            }
            public long length(java.net.URL target)throws java.io.IOException{
                if(session!=generation||Thread.currentThread().isInterrupted())throw new java.io.InterruptedIOException();
                if(!"https".equals(target.getProtocol()))throw new java.io.IOException("Invalid media URL");
                java.net.HttpURLConnection connection=(java.net.HttpURLConnection)target.openConnection();
                connection.setConnectTimeout(8000);connection.setReadTimeout(8000);
                connection.setRequestProperty("Range","bytes=0-0");
                if(cookie!=null&&!cookie.isEmpty())connection.setRequestProperty("Cookie",cookie);
                try{int status=connection.getResponseCode();if(status<200||status>=300)return -1;
                    String range=connection.getHeaderField("Content-Range");if(range!=null&&range.contains("/"))try{return Long.parseLong(range.substring(range.lastIndexOf('/')+1));}catch(NumberFormatException ignored){}
                    return status==200?connection.getContentLengthLong():-1;
                }finally{connection.disconnect();}
            }});if(session==generation&&app!=null){totalBytes=measured;publish(false);}
        }catch(Exception error){if(session!=generation)return;totalBytes=-1;android.util.Log.i("nicoid-cache","Full transfer size unavailable; showing received bytes");}
    }
    public static void asset(int length) { assetLength=length>0?length:-1; ASSET_BYTES.set(0); }
    public static void transfer(long total,long resumed){totalBytes=total;BYTES.set(Math.max(0,resumed));publish(false);}
    public static void bytes(int count) { if (count > 0 && app != null) { BYTES.addAndGet(count); ASSET_BYTES.addAndGet(count); long now=android.os.SystemClock.elapsedRealtime(); if(now-lastUpdate>=750){lastUpdate=now;publish(false);} } }
    public static void stop(){cancelSize();Context c=app;app=null;if(c!=null)try{NotificationManager m=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);if(m!=null)m.cancel(ID);}catch(SecurityException ignored){}}
    public static int finishResource(int original){if(original!=0x7f0f0027)return original;if(completed)return 0x7f0f0024;return app!=null?0:original;}
    public static void done() { completed=true;cancelSize();if (app != null) { publish(true); app = null; } }

    private static synchronized void publish(boolean done) {
        Context c = app; if (c == null) return;
        NotificationManager manager = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE); if (manager == null) return;
        long bytes = BYTES.get(); String amount = String.format(Locale.getDefault(), "%.1f MB", bytes / 1048576f);
        try{Class<?> service=Class.forName("com.sauzask.nicoid.NicoidDownloadCache");service.getField("s").setLong(null,bytes);service.getField("r").setLong(null,totalBytes>0?totalBytes:0);}catch(Exception ignored){}
        String title = done ? UiStrings.translate("キャッシュ取得完了") : UiStrings.translate("キャッシュ取得中");
        long expected=totalBytes>0?totalBytes:assetLength;long current=totalBytes>0?bytes:ASSET_BYTES.get();int percent=done?100:expected>0?(int)Math.min(99,current*100/expected):-1;
        String message = done ? amount : UiStrings.translate("取得済み: ") + amount +
            (percent>=0?" · "+(totalBytes>0?"":UiStrings.translate("現在のファイル: "))+String.format(Locale.getDefault(),"%d%% (%s / %s)",percent,format(current),format(expected)):"");
        Notification.Builder builder = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(c, CHANNEL) : new Notification.Builder(c);
        builder.setSmallIcon(android.R.drawable.stat_sys_download).setContentTitle(videoTitle.isEmpty()?title:videoTitle+" · "+title).setContentText(message).setOnlyAlertOnce(true).setOngoing(!done).setProgress(percent<0?0:100,percent<0?0:percent,percent<0&&!done);
        if(!done&&cancel!=null)builder.addAction(android.R.drawable.ic_menu_close_clear_cancel,UiStrings.translate("中止"),cancel);
        android.util.TypedValue tint = new android.util.TypedValue();
        Context palette=tintContext==null?c:tintContext;
        if(palette.getTheme().resolveAttribute(0x7f03005e,tint,true)) builder.setColor(tint.resourceId==0?tint.data:palette.getResources().getColor(tint.resourceId));
        try{manager.notify(ID,builder.build());}catch(SecurityException denied){android.util.Log.w("nicoid-cache","Cache notification permission unavailable");}
    }

    private static final android.os.Handler MAIN=new android.os.Handler(android.os.Looper.getMainLooper());
    private static final java.util.WeakHashMap<android.view.View,RowProgress> ROWS=new java.util.WeakHashMap<>();
    public static void bind(android.view.View view,Object adapter,Object row){
        try{Object host=adapter.getClass().getField("d").get(adapter);if(!host.getClass().getName().equals("com.sauzask.nicoid.NicoidCacheManagerActivity"))return;
            RowProgress old=ROWS.remove(view);if(old!=null){old.cancel();view.removeOnAttachStateChangeListener(old);}
            android.view.View label=VideoRows191.transfer(view);if(!(label instanceof android.widget.TextView))return;
            String id=HistoryRules.id(ListActions.value(row,"videourl"));RowProgress progress=new RowProgress(view,(android.widget.TextView)label,id);
            ROWS.put(view,progress);view.addOnAttachStateChangeListener(progress);view.post(progress);
        }catch(Exception ignored){}
    }
    private static final class RowProgress implements Runnable,android.view.View.OnAttachStateChangeListener{
        final java.lang.ref.WeakReference<android.view.View> view;final java.lang.ref.WeakReference<android.widget.TextView> label;final String id;CharSequence base;boolean wasActive;long lastReceived,lastTotal;int attachmentWait;
        RowProgress(android.view.View v,android.widget.TextView text,String video){view=new java.lang.ref.WeakReference<>(v);label=new java.lang.ref.WeakReference<>(text);id=video;}
        public void cancel(){MAIN.removeCallbacks(this);}
        public void onViewAttachedToWindow(android.view.View v){cancel();run();}
        public void onViewDetachedFromWindow(android.view.View v){cancel();}
        public void run(){android.view.View root=view.get();android.widget.TextView text=label.get();if(root==null||text==null||ROWS.get(root)!=this)return;
            if(base==null)base="";
            try{Class<?> service=Class.forName("com.sauzask.nicoid.NicoidDownloadCache");String current=HistoryRules.id(String.valueOf(service.getField("t").get(null)));boolean active=service.getField("v").getBoolean(null)&&id.equals(current);if(active){long received=BYTES.get(),total=totalBytes;lastReceived=received;lastTotal=total;String amount=format(received);String status=amount+(total>0?" / "+format(total):"")+" ("+percent()+"%)";CharSequence value=new android.text.SpannableStringBuilder(UiStrings.translate("キャッシュ取得中")).append(" · ").append(status);text.setVisibility(android.view.View.VISIBLE);if(!value.toString().contentEquals(text.getText()))text.setText(value);wasActive=true;}else if(wasActive){text.setText(new android.text.SpannableStringBuilder(UiStrings.translate(completed||lastTotal>0&&lastReceived>=lastTotal?"キャッシュ取得完了":"未完了")).append(" · ").append(format(lastReceived)).append(completed||lastTotal>0&&lastReceived>=lastTotal?" (100%)":""));wasActive=false;}}
            catch(Exception ignored){}if(root.isAttachedToWindow()){attachmentWait=0;MAIN.postDelayed(this,500);}else if(attachmentWait++<10)MAIN.postDelayed(this,500);
        }
    }
    private static String format(long bytes){return String.format(Locale.getDefault(),"%.0f KB",bytes/1024d);}
}
