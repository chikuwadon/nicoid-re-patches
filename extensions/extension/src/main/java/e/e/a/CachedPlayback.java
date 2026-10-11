package e.e.a;

import android.content.Context;
import android.os.Bundle;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Reuse a completed local cache before creating a network playback session. */
public final class CachedPlayback {
    private CachedPlayback() {}
    static String path(Context context,String value) {
        String id=HistoryRules.id(value);if(!id.matches("(?:sm|nm|so|ss)[0-9]+"))return null;
        String root=CacheFolders.root(context)+"/nicoid/nicoid_cache/";
        File marker=new CacheFile(root,id+".mp4"),playlist=new CacheFile(root,id+".m3u8");
        if(!marker.isFile()||!new CacheFile(root,id+".json").isFile())return null;
        if(playlist.isFile())return CachePlayback.url(playlist.getAbsolutePath());
        return marker.length()>0?CachePlayback.url(marker.getAbsolutePath()):null;
    }
    public static String stream(Object model,String id) {
        try {
            Context context=(Context)field(model,"K").get(model);String path=path(context,id);if(path==null)return null;
            Bundle data=(Bundle)model.getClass().getMethod("h",String.class).invoke(model,id);if(data==null)return null;
            data.putString("flvUrl",path);field(model,"m").set(model,data);field(model,"G").set(model,path);
            // Reading saved comment JSON must not reuse watch metadata for another video.
            field(model,"modernWatch").set(model,null);
            Object renderer=field(model,"b").get(model);if(renderer!=null)field(renderer,"d").set(renderer,path);
            Object listener=field(model,"k").get(model);
            if(listener!=null){Method callback=Class.forName("e.e.a.l3.j").getMethod("a",Bundle.class);callback.invoke(listener,data);}
            if(!field(model,"modernCommentsRequested").getBoolean(model)){
                field(model,"modernCommentsRequested").setBoolean(model,true);
                new Thread(()->{try{Class.forName("e.e.a.ModernComments").getMethod("load",model.getClass()).invoke(null,model);}catch(Exception e){android.util.Log.w("nicoid-cache","Unable to read cached comments",e);}},"nicoid-cached-comments").start();
            }
            return path;
        }catch(Exception e){android.util.Log.w("nicoid-cache","Unable to reuse cached video",e);return null;}
    }
    private static Field field(Object o,String name)throws Exception {Field f=o.getClass().getField(name);f.setAccessible(true);return f;}
}
