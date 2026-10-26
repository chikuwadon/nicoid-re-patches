package e.e.a;

import android.app.Activity;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/** Keep the existing decoder/session across a temporary Activity stop. Final destruction is native. */
public final class PlaybackLifecycle219 {
 private static final WeakHashMap<Object,Snapshot> PAUSED=new WeakHashMap<Object,Snapshot>();
 private static final class Snapshot {WeakReference<Object> player;String id;boolean resume;}
 private PlaybackLifecycle219(){}
 public static boolean retain(Object owner){try{
  Activity a=(Activity)Playback214.get(owner,"A1");
  if(a==null||a.isFinishing()||a.isDestroyed()||a.isChangingConfigurations()||PlayerGestures219.flag(owner,"x0"))return false;
  if(MiniPlayer219.owns(a))return true;
  Object player=Playback214.get(owner,"a0");String id=String.valueOf(Playback214.get(owner,"b0"));
  if(player==null||((Number)player.getClass().getMethod("getDuration").invoke(player)).longValue()<=0)return false;
  Snapshot s=PAUSED.get(owner);if(s!=null&&s.player.get()==player&&s.id.equals(id))return true;
  s=new Snapshot();s.player=new WeakReference<Object>(player);s.id=id;s.resume=Boolean.TRUE.equals(player.getClass().getMethod("isPlaying").invoke(player));
  if(s.resume)player.getClass().getMethod("pause").invoke(player);PAUSED.put(owner,s);
  ModernDebug.record("Playback retained while activity stopped");return true;
 }catch(Exception e){android.util.Log.w("nicoid-session","Cannot retain stopped playback",e);return false;}}
 public static void resume(Object owner){Snapshot s=PAUSED.remove(owner);if(s==null)return;try{
  Activity a=(Activity)Playback214.get(owner,"A1");Object player=Playback214.get(owner,"a0");
  if(a==null||a.isFinishing()||a.isDestroyed()||player!=s.player.get()||!s.id.equals(String.valueOf(Playback214.get(owner,"b0"))))return;
  if(s.resume)player.getClass().getMethod("start").invoke(player);
  ModernDebug.record("Playback resumed from retained session");
 }catch(Exception e){android.util.Log.w("nicoid-session","Cannot resume retained playback",e);}}
 public static void destroy(Object owner){PAUSED.remove(owner);}
 public static boolean retryTask(Object task){try{Object owner=Playback214.get(task,"fragment");if(!retryAllowed(owner))return false;
  String name=task.getClass().getName();if(name.endsWith("$Retry"))return Playback214.get(task,"model")==Playback214.get(owner,"h1")&&java.util.Objects.equals(Playback214.get(task,"id"),Playback214.get(owner,"b0"));
  Object model=Playback214.get(owner,"g1");return model!=null&&java.util.Objects.equals(Playback214.get(task,"url"),Playback214.get(model,"d"));
 }catch(Exception e){return false;}}
 public static boolean retryAllowed(Object owner){try{
  Activity a=(Activity)Playback214.get(owner,"A1");
  return a!=null&&!a.isFinishing()&&!a.isDestroyed()&&!PAUSED.containsKey(owner)&&!MiniPlayer219.owns(a)&&!PlayerGestures219.flag(owner,"x0")&&PlayerGestures219.flag(owner,"M0")&&PlaybackSession.allowRetry(owner);
 }catch(Exception e){return false;}}
}
