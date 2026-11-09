package e.e.a;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

/** Confirm actual target position progress before retiring the previous audio decoder. */
public final class Dev136 {
 private static final Handler MAIN = new Handler(Looper.getMainLooper());
 private static final IdentityHashMap<Object,Runnable> JOBS = new IdentityHashMap<>();
 private Dev136(){}
 private static final java.util.WeakHashMap<Object,Long> RETURN_REQUESTS=new java.util.WeakHashMap<>();
 /** A pending return is not success unless its target has been adopted. */
 public static synchronized boolean keepPending(Object pending){
  if(pending==null)return false;
  long now=SystemClock.uptimeMillis();
  Long first=RETURN_REQUESTS.get(pending);
  if(first==null){first=now;RETURN_REQUESTS.put(pending,first);}
  try {
   Object target=field(pending,"target");
   boolean adopted=Boolean.TRUE.equals(field(pending,"adopted"));
   if(adopted&&target instanceof android.app.Activity
      && !((android.app.Activity)target).isFinishing())return true;
  }catch(Exception ignored){}
  if(now-first>=2500L){
   RETURN_REQUESTS.remove(pending);
   Log.w("nicoid-return","Abandoning stale unadopted normal-return request");
   return false;
  }
  return true;
 }
 public static void returnStage(String stage) {
  Log.i("nicoid-return",stage);
 }

 private static Object field(Object owner,String name)throws Exception{
  Field f=owner.getClass().getDeclaredField(name); f.setAccessible(true);return f.get(owner);
 }
 private static long position(Object p)throws Exception{
  return ((Number)p.getClass().getMethod("getCurrentPosition").invoke(p)).longValue();
 }
 private static boolean playing(Object p)throws Exception{
  return Boolean.TRUE.equals(p.getClass().getMethod("isPlaying").invoke(p));
 }
 private static void controller(String name,Object transfer)throws Exception{
  Class<?> cls=Class.forName("e.e.a.PlaybackTransfer221");
  Method found=null;
  for(Method m:cls.getDeclaredMethods())
   if(name.equals(m.getName())&&m.getParameterTypes().length==1){found=m;break;}
  if(found==null)throw new NoSuchMethodException(name);
  found.setAccessible(true);found.invoke(null,transfer);
 }
 private static synchronized void remove(Object transfer,Runnable task){
  if(JOBS.get(transfer)==task)JOBS.remove(transfer);
 }
 public static synchronized void completeWhenAudible(final Object transfer){
  if(transfer==null||JOBS.containsKey(transfer))return;
  final Object target;final long initial;
  try{
   target=field(transfer,"targetPlayer");
   if(target==null)return;
   initial=position(target);
  }catch(Exception e){Log.w("nicoid-transfer","Cannot inspect target decoder",e);return;}
  final long started=SystemClock.uptimeMillis();
  Runnable task=new Runnable(){public void run(){
   synchronized(Dev136.class){if(JOBS.get(transfer)!=this)return;}
   try{
    Class<?> cls=Class.forName("e.e.a.PlaybackTransfer221");
    Field current=cls.getDeclaredField("pending");current.setAccessible(true);
    if(current.get(null)!=transfer){remove(transfer,this);return;}
    long elapsed=SystemClock.uptimeMillis()-started;
    if(elapsed>=180L&&playing(target)&&position(target)-initial>=140L){
     remove(transfer,this);controller("completeNow",transfer);
     Log.i("nicoid-transfer","Target position progressed; source decoder retired");
     return;
    }
    if(elapsed>=2500L){
     remove(transfer,this);
     Log.w("nicoid-transfer","New decoder did not advance; preserving source playback");
     controller("cancel",transfer);
     return;
    }
   }catch(Exception e){
    remove(transfer,this);
    Log.w("nicoid-transfer","Audio transition failed; cancelling new decoder",e);
    try{controller("cancel",transfer);}catch(Exception rollback){Log.w("nicoid-transfer","Rollback failed",rollback);}
    return;
   }
   MAIN.postDelayed(this,70L);
  }};
  JOBS.put(transfer,task);
  MAIN.postDelayed(task,70L);
 }
}
