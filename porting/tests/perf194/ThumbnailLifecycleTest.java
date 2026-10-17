package e.e.a;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(manifest=Config.NONE,sdk=28)
public class ThumbnailLifecycleTest {
    Activity activity;ThreadPoolExecutor workers;CountDownLatch release;
    @SuppressWarnings("unchecked") static <T>T field(Class<?> type,String name)throws Exception{Field f=type.getDeclaredField(name);f.setAccessible(true);return (T)f.get(null);}
    @Before public void setup()throws Exception{
        activity=Robolectric.buildActivity(Activity.class).setup().get();workers=field(ShortImages.class,"WORKERS");
        release=new CountDownLatch(1);CountDownLatch entered=new CountDownLatch(3);
        for(int i=0;i<3;i++)workers.execute(()->{entered.countDown();try{release.await(10,TimeUnit.SECONDS);}catch(InterruptedException ex){Thread.currentThread().interrupt();}});
        assertTrue(entered.await(2,TimeUnit.SECONDS));
    }
    @After public void cleanup()throws Exception{ShortImages.cancel(activity);release.countDown();activity.finish();((LruCache<?,?>)field(ShortImages.class,"memory")).evictAll();}
    @Test public void rebindCancelsOldRequestAndQueuedClosure()throws Exception{
        ImageView row=new ImageView(activity);TextView placeholder=new TextView(activity);
        ShortImages.load("https://example.invalid/old",row,placeholder);assertEquals(1,workers.getQueue().size());
        ShortImages.load("https://example.invalid/new",row,placeholder);
        Map<String,?> jobs=field(ShortImages.class,"jobs");assertFalse(jobs.containsKey("https://example.invalid/old"));assertTrue(jobs.containsKey("https://example.invalid/new"));assertEquals(1,workers.getQueue().size());
        ShortImages.cancel(activity);assertTrue(jobs.isEmpty());assertTrue(workers.getQueue().isEmpty());
    }
    @Test public void sharedImageSurvivesOneSubscriberRebind()throws Exception{
        ImageView a=new ImageView(activity),b=new ImageView(activity);
        ShortImages.load("https://example.invalid/shared",a,null);ShortImages.load("https://example.invalid/shared",b,null);assertEquals(1,workers.getQueue().size());
        ShortImages.load("https://example.invalid/other",a,null);
        Map<String,?> jobs=field(ShortImages.class,"jobs");assertEquals(2,jobs.size());assertEquals(2,workers.getQueue().size());
        Object shared=jobs.get("https://example.invalid/shared");Field f=shared.getClass().getDeclaredField("subscribers");f.setAccessible(true);assertEquals(1,((List<?>)f.get(shared)).size());
    }
    @Test public void cachedReplacementRemovesOldNetworkSubscription()throws Exception{
        ImageView row=new ImageView(activity);TextView placeholder=new TextView(activity);
        ShortImages.load("https://example.invalid/old",row,placeholder);
        LruCache<String,Bitmap> cache=field(ShortImages.class,"memory");cache.put("https://example.invalid/hit",Bitmap.createBitmap(4,4,Bitmap.Config.ARGB_8888));
        ShortImages.load("https://example.invalid/hit",row,placeholder);
        assertEquals(View.GONE,placeholder.getVisibility());assertNotNull(row.getDrawable());assertTrue(((Map<?,?>)field(ShortImages.class,"jobs")).isEmpty());assertTrue(workers.getQueue().isEmpty());
    }
    @Test public void repeatedSameUrlUsesOneRequestAndListener()throws Exception{
        ImageView row=new ImageView(activity);for(int i=0;i<100;i++)ShortImages.load("https://example.invalid/same",row,null);
        Map<String,?> jobs=field(ShortImages.class,"jobs");assertEquals(1,jobs.size());assertEquals(1,workers.getQueue().size());
        Object shared=jobs.values().iterator().next();Field f=shared.getClass().getDeclaredField("subscribers");f.setAccessible(true);assertEquals(1,((List<?>)f.get(shared)).size());
        assertTrue(workers.allowsCoreThreadTimeOut());
    }
}
