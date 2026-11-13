package e.e.a;
import java.lang.reflect.Field;
import java.util.WeakHashMap;

/** Keep a seekable duration even when the legacy Activity changes metadata bundles. */
public final class MediaSeek206 {
 private static final WeakHashMap<Object,Snapshot> LAST=new WeakHashMap<>();
 private static final class Snapshot {String video;long duration;int misses;}
 private MediaSeek206(){}
 public static synchronized long validDuration(Object state,long duration){
  if(state==null)return duration;
  String video=null;
  try{
   Field field=state.getClass().getDeclaredField("video");
   field.setAccessible(true);
   Object id=field.get(state);
   if(id!=null)video=String.valueOf(id);
  }catch(Exception ignored){}
  Snapshot previous=LAST.get(state);
  if(previous==null){previous=new Snapshot();previous.video=video;LAST.put(state,previous);}
  if(video!=null&&!video.equals(previous.video)){
   previous.video=video;previous.duration=0;previous.misses=0;
  }
  if(duration>0){previous.duration=duration;previous.misses=0;return duration;}
  if(previous.duration>0){
   if(previous.misses++==0)
    android.util.Log.i("nicoid-seek","Invalid media duration; retaining seekable metadata");
   return previous.duration;
  }
  return duration;
 }
 public static void requested(long position){
  android.util.Log.i("nicoid-seek","MediaSession onSeekTo "+Math.max(position,0)+"ms");
 }
}
