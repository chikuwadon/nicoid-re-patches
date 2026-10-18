package e.e.a;
import android.app.*;import android.content.*;import android.widget.*;import java.lang.reflect.*;
// Test boundaries: activity fields and remote channel/mylist loaders.
class Review181 {static Object field(Object o,String n){try{return o.getClass().getField(n).get(o);}catch(Exception e){return null;}}}
class PlaybackSession {static int cookies;static Object get(Object o,String n){if(n.equals("E"))cookies++;return Review181.field(o,n);}static Object call(Object o,String n,Class[] t,Object... args){return null;}}
class v0 {public static Object b;}
class CreatorPage {static int channels,mylists;static NetworkTask channelTask;public static boolean open(Activity a){return false;}static void loadFollowingChannels(Activity a,LinearLayout rows,NetworkTask task){channels++;channelTask=task;rows.addView(new TextView(a));}static void loadMylists(Activity a,LinearLayout rows,int page){mylists++;rows.setTag(new NetworkTask());rows.addView(new TextView(a));}}
class ThemeChoice {static boolean night;static int themeAccent=0xff00aa88;static boolean isNight(Context c){return night;}static int accent(Context c){return themeAccent;}static int textColor(android.view.View v){return 0xff111111;}static void background(android.view.View v){}static void button(Button v){}}
class UiDialogs {static int accent(Context c){return ThemeChoice.accent(c);}static int surface(Context c){return 0xffeeeeee;}}
class DialogColors {static int onAccent(int color){return 0xffffffff;}static int ink(boolean night){return 0xff111111;}}
class ListDividers191 {static int color(Context c){return 0xffdddddd;}}
class VideoDetails {static void initializeCookies(Context a){}}
class ShortImages {static void resume(Activity a){}static void cancel(Activity a){}static void load(String url,ImageView image,TextView placeholder){}}
