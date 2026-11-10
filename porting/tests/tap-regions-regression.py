"""Execute current normal and popup toggle bridges with Android boundary doubles."""
import ast
from pathlib import Path
import subprocess
import tempfile

repo = Path(__file__).resolve().parents[2]
revision = repo / "porting/revisions/v1.10.7-dev.12"
old = ast.parse((repo / "porting/tests/regression155.py").read_text())
fixtures = ast.literal_eval(next(n.value for n in old.body if isinstance(n, ast.Assign)
                                and n.targets[0].id == "fixtures"))
del fixtures["e/e/a/Bridge155Test.java"]
fixtures.update({
    "android/content/Context.java": """package android.content;
    public class Context {public Context getApplicationContext(){return this;}}""",
    "android/content/SharedPreferences.java": """package android.content;
    public class SharedPreferences {
        String value="all"; OnSharedPreferenceChangeListener listener;
        public interface OnSharedPreferenceChangeListener {
            void onSharedPreferenceChanged(SharedPreferences prefs,String key);
        }
        public boolean seek;
        public boolean getBoolean(String key,boolean fallback){return seek;}
        public String getString(String key,String fallback){return value;}
        public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener l){listener=l;}
        public Editor edit(){return new Editor();}
        public class Editor {
            String key,next;
            public Editor putString(String k,String v){key=k;next=v;return this;}
            public void apply(){value=next;if(listener!=null)listener.onSharedPreferenceChanged(SharedPreferences.this,key);}
        }
    }""",
    "android/preference/PreferenceManager.java": """package android.preference;
    public class PreferenceManager {
      public static final android.content.SharedPreferences PREFS=new android.content.SharedPreferences();
      public static android.content.SharedPreferences getDefaultSharedPreferences(android.content.Context c){return PREFS;}
    }""",
    "android/os/Looper.java": """package android.os;
    public class Looper {public static Looper getMainLooper(){return new Looper();}}""",
    "android/os/SystemClock.java": """package android.os;
    public class SystemClock {public static long now;public static long uptimeMillis(){return now;}}""",
    "android/os/Handler.java": """package android.os;import java.util.*;
    public class Handler {
        static Map<Runnable,Long> tasks=new LinkedHashMap<>();
        public Handler(Looper l){}
        public boolean postAtTime(Runnable r,long t){tasks.put(r,t);return true;}
        public void removeCallbacks(Runnable r){tasks.remove(r);}
        public static void advance(long t){
            SystemClock.now=t;
            for(Runnable r:new ArrayList<>(tasks.keySet()))
                if(tasks.containsKey(r)&&tasks.get(r)<=t){tasks.remove(r);r.run();}
        }
    }""",
    "android/view/ViewParent.java": "package android.view; public interface ViewParent {}",
    "android/view/SurfaceView.java": "package android.view; public class SurfaceView extends View {}",
    "android/view/TextureView.java": "package android.view; public class TextureView extends View {}",
    "android/util/DisplayMetrics.java": "package android.util; public class DisplayMetrics {public float density=1;}",
    "android/content/res/Resources.java": """package android.content.res;
    public class Resources {public android.util.DisplayMetrics getDisplayMetrics(){return new android.util.DisplayMetrics();}}""",
    "e/e/a/Dev112.java": """package e.e.a;
    class Dev112 {static boolean always;
      static Object staticCall(String type,String method,Class<?>[] arguments,Object... values){return always;}
    }""",
    "e/e/a/Tap141Test.java": """package e.e.a;
    import android.app.Service;
    import android.os.Handler;
    import android.view.*;
    import android.preference.PreferenceManager;
    public class Tap141Test {
        static int checks;
        static void check(boolean ok,String name){checks++;if(!ok)throw new AssertionError(name);}
        public static class Controls extends View {public void hide(){shown=false;}}
        public static class Player extends ViewGroup {
            public final Controls controls=new Controls();
            public Player(){controls.shown=false;}
            public Controls getVideoControls(){return controls;}
            public void showControls(){controls.shown=true;}
        }
        public static class Owner extends Service implements View.OnTouchListener {
            public Player e=new Player();
            public int forwards;
            public boolean onTouch(View view,MotionEvent event){forwards++;return false;}
        }
        public static class Listener {
            int toggles;
            public boolean onSingleTapConfirmed(MotionEvent e){
                if(TapControls141.nativeAllowed(this,e))toggles++;
                return true;
            }
        }
        static MotionEvent event(int action,long down,long at,float x){
            MotionEvent e=new MotionEvent(action,at,x,100);
            e.down=down;return e;
        }
        static void stream(View root,Owner owner,long time,float x){
            PopupTouch125.dispatch(root,owner,event(0,time,time,x));
            PopupTouch125.dispatch(root,owner,event(1,time,time+40,x));
        }
        static void normal(Listener listener,View view,long time,float x){
            MotionEvent d=event(0,time,time,x);
            TapControls141.nativeTouch(listener,view,d);
            TapControls141.nativeTouch(listener,view,event(1,time,time+40,x));
            Handler.advance(time+300);
            listener.onSingleTapConfirmed(d);
        }
        static void mode(String value){
            PreferenceManager.PREFS.edit().putString(TapControls141.KEY,value).apply();
        }
        public static void main(String[] args){
            check(TapRegions141.parse(null).contains(50,50,100,100,0),"missing default");
            check(TapRegions141.parse("all").contains(0,50,100,100,0),"entire video");
            TapRegions141 edges=TapRegions141.parse("edges");
            check(edges.contains(15,50,100,100,0)&&edges.contains(85,50,100,100,0)&&!edges.contains(50,50,100,100,0),"edge boundaries");
            TapRegions141 center=TapRegions141.parse("center");
            check(center.contains(30,50,100,100,0)&&center.contains(70,50,100,100,0)&&!center.contains(71,50,100,100,0),"center boundaries");
            TapRegions141 custom=TapRegions141.parse("custom:0-12,34-52,48-75");
            check(custom.contains(50,50,100,100,0)&&custom.contains(70,50,100,100,0)&&!custom.contains(20,50,100,100,0),"overlapping union");
            check(custom.encode().equals("rects:0-12-0-100,34-52-0-100,48-75-0-100"),"round trip preserves three areas");
            for(String bad:new String[]{"", "bad", "custom:0-101","custom:45-45","custom:40-10","custom:0-1,2-3,4-5,6-7"})
                check(TapRegions141.parse(bad).mode==TapRegions141.ALL,"safe fallback "+bad);
            TapRegions141 vertical=TapRegions141.parse("top-bottom");
            check(vertical.contains(50,15,100,100,0)&&vertical.contains(50,85,100,100,0)
                    &&!vertical.contains(50,50,100,100,0),"top and bottom bands");
            TapRegions141 rects=TapRegions141.parse("rects:8-20-10-30,65-94-70-95,15-32-24-45");
            check(rects.contains(18,29,100,100,0)&&rects.contains(26,40,100,100,0)
                    &&!rects.contains(26,18,100,100,0),"rectangular union leaves intervening spaces");
            check(rects.encode().equals("rects:8-20-10-30,65-94-70-95,15-32-24-45"),"four coordinates round trip");
            check(TapRegions141.parse("rects:10-20-80-30").mode==TapRegions141.ALL,"reversed vertical bounds fallback");
            check(TapRegions141.parse("rects:0-1-0-1").contains(8,8,40,40,24),"popup minimum applies to both axes");
            TapRegions141 tiny=TapRegions141.parse("custom:0-1,85-86");
            check(tiny.contains(8,50,40,100,24)&&!tiny.contains(20,50,40,100,24),"minimum popup hit width stays local");
            check(!tiny.contains(8,50,40,100,0),"normal playback keeps exact percentages");

            ViewGroup root=new ViewGroup();Owner owner=new Owner();
            mode("edges");
            stream(root,owner,1000,500); Handler.advance(1341);
            check(!owner.e.controls.shown,"popup center blocked");
            stream(root,owner,1500,100); Handler.advance(1800);
            check(!owner.e.controls.shown,"popup single waits after first up");
            Handler.advance(1841);
            check(owner.e.controls.shown,"popup confirmed edge opens");
            stream(root,owner,2000,500);Handler.advance(2341);
            check(owner.e.controls.shown,"popup visible controls stay open for invalid tap");
            stream(root,owner,2500,100);Handler.advance(2841);
            check(!owner.e.controls.shown,"popup confirmed edge closes");
            PreferenceManager.PREFS.seek=true;
            stream(root,owner,3000,500);stream(root,owner,3160,502);Handler.advance(3600);
            check(PopupGestures.calls==1&&!owner.e.controls.shown,"popup center double seek unaffected");
            stream(root,owner,3800,100);stream(root,owner,3960,102);Handler.advance(4400);
            check(PopupGestures.calls==2&&!owner.e.controls.shown,"popup edge double seek has no first toggle");
            stream(root,owner,4500,100);
            PopupTouch125.cancel(owner);Handler.advance(4900);
            check(!owner.e.controls.shown,"popup pinch/hold cancellation drops deferred tap");
            stream(root,owner,5000,100);
            owner.e=new Player();Handler.advance(5400);
            check(!owner.e.controls.shown,"popup replacement never gets stale tap");
            ButtonStub.hit(root,owner);

            Listener listener=new Listener();View view=new View();
            view.x=200;view.w=600;
            normal(listener,view,6000,500);Handler.advance(6341);
            check(listener.toggles==0,"normal center blocked against video bounds");
            normal(listener,view,6500,230);
            check(listener.toggles==0,"native single waits for remaining seek interval");
            Handler.advance(6841);
            check(listener.toggles==1,"native confirmed edge toggles");
            normal(listener,view,7000,230);
            TapControls141.cancelFor(view);Handler.advance(7341);
            check(listener.toggles==1,"native second tap consumed by app cancels deferred toggle");
            mode("center");view.x=50;view.w=300;
            normal(listener,view,7500,200);Handler.advance(7841);
            check(listener.toggles==2,"resized normal video and updated preset");
            Dev112.always=true;
            normal(listener,view,8000,200);Handler.advance(8341);
            check(listener.toggles==2,"always-visible controls are not toggled");
            Dev112.always=false;
            mode("custom:0-5,45-55,90-100");
            check(TapControls141.allows(view,200,100,false),"saved custom region active");
            mode("rects:30-70-60-80");
            view.y=100;view.h=200;
            check(TapControls141.allows(view,200,240,false)
                    &&!TapControls141.allows(view,200,160,false),"normal uses video-relative Y");
            Owner shifted=new Owner();shifted.e.x=100;shifted.e.y=200;shifted.e.w=300;shifted.e.h=200;
            check(TapControls141.allows(shifted.e,250,340,true)
                    &&!TapControls141.allows(shifted.e,250,240,true),"popup uses shifted video-relative Y");
            TapControls141.save(view.getContext(),TapRegions141.preset(TapRegions141.ALL));
            check(TapControls141.settings(view.getContext()).mode==TapRegions141.ALL,"restore default persists");
            System.out.println("Single-tap regions and production bridges: "+checks+" assertions passed");
        }
        private static class ButtonStub {
            static void hit(ViewGroup root,Owner owner) {
                android.widget.Button button=new android.widget.Button();button.x=450;button.w=100;
                root.children.add(button);
                int forwarded=owner.forwards;
                boolean routed=PopupTouch125.dispatch(root,owner,event(0,5550,5550,500));
                check(!routed&&owner.forwards==forwarded,"direct control remains delegated in disabled region");
                root.children.clear();
            }
        }
    }"""
})
fixtures["android/view/View.java"] = fixtures["android/view/View.java"].replace(
    "public class View{", "public class View implements ViewParent{public boolean isAttachedToWindow(){return true;}"
    "public boolean hasWindowFocus(){return true;}public ViewParent getParent(){return null;}"
    "public android.content.res.Resources getResources(){return new android.content.res.Resources();}"
    "public boolean isClickable(){return clickable;}")
fixtures["android/view/MotionEvent.java"] = fixtures["android/view/MotionEvent.java"].replace(
    "int a;long t;", "int a;long t;public long down;"
    "public long getDownTime(){return down;} public void recycle(){}"
    "public static MotionEvent obtain(MotionEvent original){return original;}")

with tempfile.TemporaryDirectory(prefix="tap-regions-") as tmp:
    dest = Path(tmp)
    for name, source in fixtures.items():
        path = dest / name
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(source)
    helpers = revision / "java/e/e/a"
    actual = [helpers / (name + ".java") for name in
              ("TapRegions141", "TapControls141", "PopupTouch125", "PopupTouchPolicy125")]
    subprocess.run(["java", "com.sun.tools.javac.Main", "-d", tmp,
                    *map(str, dest.rglob("*.java")), *map(str, actual)], check=True)
    subprocess.run(["java", "-cp", tmp, "e.e.a.Tap141Test"], check=True)
