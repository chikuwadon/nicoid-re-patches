package e.e.a;
import android.app.*;import android.content.*;import android.content.res.*;import android.graphics.*;import android.preference.*;import android.view.*;import android.widget.*;import java.lang.reflect.*;
import org.junit.*;import static org.junit.Assert.*;import org.junit.runner.RunWith;import org.robolectric.*;import org.robolectric.annotation.*;

@RunWith(RobolectricTestRunner.class) @Config(sdk={28,34,36},manifest=Config.NONE)
public class MiniSurface220Test {
 public static class Screen extends AppliedApkTest.Screen {}
 public static class NativeScreen extends com.sauzask.nicoid.NicoidVideoActivity {
  Resources.Theme theme;
  public Resources getResources(){return AppliedApkTest.apkResources==null?super.getResources():AppliedApkTest.apkResources;}
  public AssetManager getAssets(){return getResources().getAssets();}
  public String getPackageName(){return "com.sauzask.nicoid.hls";}
  public Resources.Theme getTheme(){if(AppliedApkTest.apkResources==null)return super.getTheme();if(theme==null){theme=getResources().newTheme();theme.applyStyle(android.R.style.Theme_Material,true);}return theme;}
  public void setTheme(int id){if(AppliedApkTest.apkResources==null)super.setTheme(id);else getTheme().applyStyle(id,true);}
 }
 public static class Texture extends TextureView {
  SurfaceTexture texture;boolean attached=true,available=true;int replacements;
  Texture(Context c,SurfaceTexture t){super(c);texture=t;}
  public SurfaceTexture getSurfaceTexture(){return texture;}
  public void setSurfaceTexture(SurfaceTexture t){texture=t;replacements++;}
  public boolean isAvailable(){return available;}
  public boolean isAttachedToWindow(){return attached;}
 }
 static class Listener implements TextureView.SurfaceTextureListener {int available,destroyed,resized,updated;
  public void onSurfaceTextureAvailable(SurfaceTexture s,int w,int h){available++;}
  public boolean onSurfaceTextureDestroyed(SurfaceTexture s){destroyed++;s.release();return true;}
  public void onSurfaceTextureSizeChanged(SurfaceTexture s,int w,int h){resized++;}
  public void onSurfaceTextureUpdated(SurfaceTexture s){updated++;}
 }
 static class TrackedSurface extends SurfaceTexture {int releases;TrackedSurface(){super(false);}public void release(){releases++;super.release();}}
 Screen a;NativeScreen videoActivity;TrackedSurface retained;SurfaceTexture temp;Texture texture;Listener original;
 @Before public void setup()throws Exception{new AppliedApkTest().prepare();a=Robolectric.buildActivity(Screen.class).setup().visible().get();a.setTheme(0x7f1000a2);videoActivity=Robolectric.buildActivity(NativeScreen.class).get();videoActivity.setTheme(0x7f1000a2);PreferenceManager.getDefaultSharedPreferences(a).edit().clear().commit();retained=new TrackedSurface();temp=new SurfaceTexture(false);texture=new Texture(a,retained);original=new Listener();texture.setSurfaceTextureListener(original);}
 @After public void cleanup(){MiniSurface220.destroy(a);if(retained!=null)retained.release();if(temp!=null)temp.release();a.finish();AppliedApkTest.apkResources=null;}
 int id(String name){return a.getResources().getIdentifier(name,"layout",a.getPackageName());}
 Object implementation(com.devbrackets.android.exomedia.ui.widget.VideoView v)throws Exception{return Playback214.get(v,"videoViewImpl");}
 @Test public void actualInflatedPlayerUsesSurfaceBackingWhenMiniIsDisabled()throws Exception{View root=LayoutInflater.from(videoActivity).inflate(0x7f0b0061,null);com.devbrackets.android.exomedia.ui.widget.VideoView v=(com.devbrackets.android.exomedia.ui.widget.VideoView)PortraitTimeline213.find(root,"video_view");try{assertTrue(implementation(v).getClass().getName().endsWith("ExoSurfaceVideoView"));}finally{v.release();}}
 @Test public void actualInflatedPlayerUsesRetainableTextureBackingWhenMiniIsEnabled()throws Exception{PreferenceManager.getDefaultSharedPreferences(a).edit().putBoolean(PlayerGestures219.MINI,true).commit();View root=LayoutInflater.from(videoActivity).inflate(0x7f0b0061,null);com.devbrackets.android.exomedia.ui.widget.VideoView v=(com.devbrackets.android.exomedia.ui.widget.VideoView)PortraitTimeline213.find(root,"video_view");try{assertTrue(implementation(v).getClass().getName().endsWith("ExoTextureVideoView"));assertFalse("An unprepared player must not enter mini mode",MiniSurface220.ready(v));}finally{v.release();}}
 @Test public void enablingOptionDoesNotReplacePopupOrUnrelatedRenderers(){PreferenceManager.getDefaultSharedPreferences(a).edit().putBoolean(PlayerGestures219.MINI,true).commit();assertEquals(id("exomedia_default_exo_surface_video_view"),MiniSurface220.layout(a,null,id("exomedia_default_exo_surface_video_view")));assertEquals(id("player"),MiniSurface220.layout(videoActivity,null,id("player")));}
 @Test public void temporaryDetachDoesNotCallTheDestructiveListener(){MiniSurface220.Bridge b=new MiniSurface220.Bridge(a,texture);assertFalse(b.onSurfaceTextureDestroyed(retained));assertEquals(0,original.destroyed);assertEquals(0,retained.releases);b.close();}
 @Test public void reattachReusesTextureWithoutNewAvailabilityOrDecoderSurface(){MiniSurface220.Bridge b=new MiniSurface220.Bridge(a,texture);b.onSurfaceTextureDestroyed(retained);texture.texture=temp;b.onSurfaceTextureAvailable(temp,320,180);assertSame(retained,texture.getSurfaceTexture());assertEquals(1,texture.replacements);assertEquals(0,original.available);assertEquals(0,original.destroyed);assertEquals(1,original.resized);b.close();}
 @Test public void repeatedHostTransfersPreserveTheSameTexture(){MiniSurface220.Bridge b=new MiniSurface220.Bridge(a,texture);for(int n=0;n<10;n++){assertFalse(b.onSurfaceTextureDestroyed(retained));texture.texture=temp;b.onSurfaceTextureAvailable(temp,320,180);assertSame(retained,texture.texture);}assertEquals(0,original.available);assertEquals(0,original.destroyed);assertEquals(10,b.reattachments);b.close();}
 @Test public void normalRestoreWaitsForTextureReattachment(){MiniSurface220.Bridge b=new MiniSurface220.Bridge(a,texture);texture.setSurfaceTextureListener(b);b.onSurfaceTextureDestroyed(retained);texture.attached=false;texture.available=false;texture.texture=null;b.restore();assertFalse(b.closed);texture.attached=true;texture.available=true;texture.texture=temp;b.onSurfaceTextureAvailable(temp,640,360);assertTrue(b.closed);assertSame(original,texture.getSurfaceTextureListener());assertSame(retained,texture.texture);assertEquals(0,original.destroyed);}
 @Test public void disposalOfDetachedWindowReleasesItsRetainedTexture(){MiniSurface220.Bridge b=new MiniSurface220.Bridge(a,texture);b.onSurfaceTextureDestroyed(retained);texture.available=false;texture.texture=null;b.close();assertEquals(1,retained.releases);assertEquals(1,original.destroyed);b.close();assertEquals(1,original.destroyed);}
 @Test public void destructionOfOwnerCannotRetainItsTextures(){FrameLayout root=new FrameLayout(a);root.addView(texture);MiniSurface220.Session s=MiniSurface220.retain(a,root);texture.available=false;texture.texture=null;MiniSurface220.destroy(a);assertEquals(1,retained.releases);assertSame(original,texture.getSurfaceTextureListener());}
 @Test public void commentRendererDoesNotSpawnAnotherThreadOnReattach(){MiniSurface220.Bridge b=new MiniSurface220.Bridge(a,texture);for(int n=0;n<3;n++){b.onSurfaceTextureDestroyed(retained);texture.texture=temp;b.onSurfaceTextureAvailable(temp,200+n,100+n);}assertEquals(0,original.available);assertEquals(3,original.resized);b.close();}
 @Test public void nativeDecoderSurfaceIsKeptAcrossTheActualListenerPath()throws Exception{
  com.devbrackets.android.exomedia.ui.widget.VideoView real=new com.devbrackets.android.exomedia.ui.widget.VideoView(a);
  try{TextureView impl=(TextureView)implementation(real);TextureView.SurfaceTextureListener nativeListener=impl.getSurfaceTextureListener();assertNotNull(nativeListener);texture.setSurfaceTextureListener(nativeListener);nativeListener.onSurfaceTextureAvailable(retained,640,360);
   Object delegate=Playback214.get(impl,"delegate"),decoder=Playback214.get(delegate,"exoMediaPlayer"),surface=Playback214.get(decoder,"surface");assertNotNull(surface);
   MiniSurface220.Bridge b=new MiniSurface220.Bridge(a,texture);assertFalse(b.onSurfaceTextureDestroyed(retained));assertSame(surface,Playback214.get(decoder,"surface"));texture.texture=temp;b.onSurfaceTextureAvailable(temp,320,180);assertSame(surface,Playback214.get(decoder,"surface"));assertEquals(0,retained.releases);b.close();
  }finally{real.release();}
 }
 @Test public void baselineNativeListenerDestroysTheOutputEvenWhenDecoderIsKept()throws Exception{
  com.devbrackets.android.exomedia.ui.widget.VideoView real=new com.devbrackets.android.exomedia.ui.widget.VideoView(a);try{TextureView impl=(TextureView)implementation(real);TextureView.SurfaceTextureListener listener=impl.getSurfaceTextureListener();listener.onSurfaceTextureAvailable(retained,640,360);Object decoder=Playback214.get(Playback214.get(impl,"delegate"),"exoMediaPlayer");assertNotNull(Playback214.get(decoder,"surface"));assertTrue(listener.onSurfaceTextureDestroyed(retained));assertNull(Playback214.get(decoder,"surface"));assertEquals(1,retained.releases);}finally{real.release();}
 }
 @Test public void actualCommentFactoryMatchesTexturePlayerWhenMiniEnabled()throws Exception{PreferenceManager.getDefaultSharedPreferences(a).edit().putBoolean(PlayerGestures219.MINI,true).commit();LinearLayout comments=new LinearLayout(videoActivity);v factory=new v(videoActivity,null,Float.valueOf(1f),comments);assertTrue(factory.a instanceof TextureView);assertFalse(((TextureView)factory.a).isOpaque());assertSame(factory.a,comments.getChildAt(0));}
 @Test public void bothCommentConstructorsUseRetainableTransparentTextures()throws Exception{PreferenceManager.getDefaultSharedPreferences(a).edit().putBoolean(PlayerGestures219.MINI,true).commit();v factory=new v(videoActivity,null);assertTrue(factory.a instanceof TextureView);assertFalse(((TextureView)factory.a).isOpaque());}
 @Test public void unrelatedCommentChoiceIsPreserved(){PreferenceManager.getDefaultSharedPreferences(a).edit().putBoolean(PlayerGestures219.MINI,true).commit();assertEquals(36,MiniSurface220.commentApi(a,36));assertEquals(18,MiniSurface220.commentApi(videoActivity,36));PreferenceManager.getDefaultSharedPreferences(a).edit().putBoolean(PlayerGestures219.MINI,false).commit();assertEquals(36,MiniSurface220.commentApi(videoActivity,36));}
 @Test public void realPlayerAndCommentTreeHasNoSurfaceHoleWhenMiniEnabled()throws Exception{PreferenceManager.getDefaultSharedPreferences(a).edit().putBoolean(PlayerGestures219.MINI,true).commit();View root=LayoutInflater.from(videoActivity).inflate(0x7f0b0061,null);com.devbrackets.android.exomedia.ui.widget.VideoView player=(com.devbrackets.android.exomedia.ui.widget.VideoView)PortraitTimeline213.find(root,"video_view");LinearLayout comments=(LinearLayout)PortraitTimeline213.find(root,"commentlay");try{new v(videoActivity,null,Float.valueOf(1f),comments);assertEquals(0,surfaces(root));assertEquals(2,textures(root));}finally{player.release();}}
 int surfaces(View v){int n=v instanceof SurfaceView?1:0;if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++)n+=surfaces(g.getChildAt(i));}return n;}
 int textures(View v){int n=v instanceof TextureView?1:0;if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++)n+=textures(g.getChildAt(i));}return n;}

}
