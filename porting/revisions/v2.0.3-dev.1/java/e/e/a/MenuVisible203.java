package e.e.a;
import android.content.SharedPreferences;

/** One-time migration of v2.0.2's reversed popup/background menu labels. */
public final class MenuVisible203 {
 private static final String MARK="video_menu_semantic_203_migrated";
 private static final String POPUP="video_menu_visible_7";       // native order 2
 private static final String BACKGROUND="video_menu_visible_9";  // native order 3
 private MenuVisible203(){}
 public static void migrate(SharedPreferences prefs) {
  if(prefs==null||prefs.getBoolean(MARK,false))return;
  synchronized(MenuVisible203.class) {
   if(prefs.getBoolean(MARK,false))return;
   boolean oldPopup=prefs.contains(BACKGROUND), oldBackground=prefs.contains(POPUP);
   boolean popup=prefs.getBoolean(BACKGROUND,true);
   boolean background=prefs.getBoolean(POPUP,true);
   SharedPreferences.Editor edit=prefs.edit();
   if(oldPopup)edit.putBoolean(POPUP,popup);else edit.remove(POPUP);
   if(oldBackground)edit.putBoolean(BACKGROUND,background);else edit.remove(BACKGROUND);
   edit.putBoolean(MARK,true).commit();
  }
 }
}
