package e.e.a;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import java.util.WeakHashMap;
import android.graphics.SurfaceTexture;
import android.preference.PreferenceManager;
import android.view.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/** Retain the existing decoder output while its TextureView moves between app windows. */
public final class MiniSurface220 {
 private MiniSurface220(){}
 private static final WeakHashMap<Activity,ArrayList<Session>> SESSIONS=new WeakHashMap<Activity,ArrayList<Session>>();
 static boolean playerContext(Context context){for(int n=0;n<8&&context!=null;n++){if(context instanceof Activity){for(Class<?> c=context.getClass();c!=null;c=c.getSuperclass())if(c.getName().equals("com.sauzask.nicoid.NicoidVideoActivity"))return true;return false;}if(!(context instanceof ContextWrapper))return false;context=((ContextWrapper)context).getBaseContext();}return false;}
 public static int commentApi(Context context,int original){return playerContext(context)&&PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PlayerGestures219.MINI,false)?18:original;}
 public static int layout(Context context,Object attributes,int selected){
  if(!playerContext(context))return selected;
  if(!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PlayerGestures219.MINI,false))return selected;
  // Respect explicitly supplied/nonstandard renderers; only substitute the two stock surface layouts.
  String pkg=context.getPackageName();String name;
  try{name=context.getResources().getResourceEntryName(selected);}catch(Exception e){return selected;}
  String target="exomedia_default_exo_surface_video_view".equals(name)?"exomedia_default_exo_texture_video_view":"exomedia_default_native_surface_video_view".equals(name)?"exomedia_default_native_texture_video_view":null;
  if(target==null)return selected;
  int id=context.getResources().getIdentifier(target,"layout",pkg);return id==0?selected:id;
 }
 public static final class Session {
  final ArrayList<Bridge> bridges=new ArrayList<Bridge>();final WeakReference<Activity> owner;
  Session(Activity a,View frame){owner=new WeakReference<Activity>(a);collect(a,frame);ArrayList<Session> list=SESSIONS.get(a);if(list==null){list=new ArrayList<Session>();SESSIONS.put(a,list);}list.add(this);}
  void collect(Activity owner,View view){if(view instanceof TextureView){Bridge b=new Bridge(owner,(TextureView)view);bridges.add(b);b.view.setSurfaceTextureListener(b);}
   if(view instanceof ViewGroup){ViewGroup group=(ViewGroup)view;for(int n=0;n<group.getChildCount();n++)collect(owner,group.getChildAt(n));}}
  public void restore(){boolean all=true;for(Bridge b:bridges){b.restore();all&=b.closed;}if(all){bridges.clear();forget();}}
  public void close(){for(Bridge b:bridges)b.close();bridges.clear();forget();}
  void forget(){Activity a=owner.get();ArrayList<Session> list=SESSIONS.get(a);if(list!=null){list.remove(this);if(list.isEmpty())SESSIONS.remove(a);}}
 }
 public static void destroy(Activity owner){ArrayList<Session> list=SESSIONS.remove(owner);if(list!=null)for(Session session:new ArrayList<Session>(list))session.close();}
 public static Session retain(Activity owner,View frame){return new Session(owner,frame);}
 static boolean hasTexture(View view){if(view instanceof TextureView)return true;if(view instanceof ViewGroup){ViewGroup g=(ViewGroup)view;for(int n=0;n<g.getChildCount();n++)if(hasTexture(g.getChildAt(n)))return true;}return false;}
 public static boolean ready(View player){
  // A player created before the option was enabled must be reopened, never converted mid-stream.
  boolean nativePlayer=false;for(Class<?> type=player.getClass();type!=null;type=type.getSuperclass())if(type.getName().equals("com.devbrackets.android.exomedia.ui.widget.VideoView"))nativePlayer=true;if(!nativePlayer)return true;
  if(!hasTexture(player))return false;
  try{Object impl=Playback214.get(player,"videoViewImpl");if(!(impl instanceof TextureView))return false;TextureView view=(TextureView)impl;
   Object decoder=Playback214.get(Playback214.get(impl,"delegate"),"exoMediaPlayer");return view.isAvailable()&&view.getSurfaceTexture()!=null&&Playback214.get(decoder,"surface")!=null;
  }catch(Exception e){return false;}
 }
 static final class Bridge implements TextureView.SurfaceTextureListener {
  final TextureView view;final TextureView.SurfaceTextureListener original;final WeakReference<Activity> owner;
  SurfaceTexture retained;boolean restoring,closed;int reattachments;
  Bridge(Activity a,TextureView v){owner=new WeakReference<Activity>(a);view=v;original=v.getSurfaceTextureListener();retained=v.getSurfaceTexture();}
  boolean keep(){Activity a=owner.get();return !closed&&a!=null&&!a.isFinishing()&&!a.isDestroyed();}
  public boolean onSurfaceTextureDestroyed(SurfaceTexture texture){
   if(keep()){retained=texture;ModernDebug.record("Mini: retained output texture during view transfer");return false;}
   retained=null;return original==null||original.onSurfaceTextureDestroyed(texture);
  }
  public void onSurfaceTextureAvailable(SurfaceTexture texture,int width,int height){
   if(keep()&&retained!=null){
    if(texture!=retained){view.setSurfaceTexture(retained);reattachments++;ModernDebug.record("Mini: reattached the original output texture");}
    // Do not notify availability again: that would create a new decoder Surface or comment thread.
    if(original!=null)original.onSurfaceTextureSizeChanged(retained,width,height);
   }else{retained=texture;if(original!=null)original.onSurfaceTextureAvailable(texture,width,height);}
   if(restoring)finishRestore();
  }
  public void onSurfaceTextureSizeChanged(SurfaceTexture texture,int width,int height){if(original!=null)original.onSurfaceTextureSizeChanged(texture,width,height);}
  public void onSurfaceTextureUpdated(SurfaceTexture texture){if(original!=null)original.onSurfaceTextureUpdated(texture);if(restoring)finishRestore();}
  void restore(){restoring=true;finishRestore();}
  void finishRestore(){if(!view.isAttachedToWindow()||!view.isAvailable()||view.getSurfaceTexture()!=retained)return;closed=true;view.setSurfaceTextureListener(original);retained=null;}
  void close(){if(closed)return;closed=true;
   if(view.isAvailable()&&view.getSurfaceTexture()==retained){view.setSurfaceTextureListener(original);retained=null;return;}
   SurfaceTexture texture=retained;retained=null;view.setSurfaceTextureListener(original);
   if(texture!=null){boolean releasedByOwner=original!=null&&original.onSurfaceTextureDestroyed(texture);if(!releasedByOwner)texture.release();}
  }
 }
}
