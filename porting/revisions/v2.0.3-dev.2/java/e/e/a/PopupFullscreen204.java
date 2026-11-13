package e.e.a;
import android.content.Intent;

/** The popup's fullscreen control must open landscape fullscreen, not portrait. */
public final class PopupFullscreen204 {
 private PopupFullscreen204(){}
 public static void prepare(Intent intent){
  if(intent==null)return;
  intent.putExtra("isFullScreen",true);
  intent.putExtra("isLandscape",true);
  intent.putExtra("nicoid_portrait_restore",false);
  intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
  ModernDebug.record("Popup-to-fullscreen: landscape mode requested before Activity launch");
 }
}
