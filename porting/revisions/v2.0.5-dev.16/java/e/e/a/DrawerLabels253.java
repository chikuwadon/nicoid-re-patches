package e.e.a;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.ViewTreeObserver;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/** Localize the legacy native drawer group header without replacing its menu. */
public final class DrawerLabels253 {
 private DrawerLabels253(){}
 private static final WeakHashMap<Activity,Watcher> WATCHERS=new WeakHashMap<>();
 private static final WeakHashMap<TextView,LabelWatcher> LABELS=new WeakHashMap<>();
 /** Native drawer headers are rebound after the first open frame. */
 public static void install(Activity activity){
  if(activity==null||activity.isFinishing()||activity.isDestroyed())return;
  View root=activity.getWindow().getDecorView();
  if(root==null)return;
  Watcher watcher=WATCHERS.get(activity);
  if(watcher!=null&&watcher.root.get()==root){refresh(root);return;}
  if(watcher!=null)watcher.detach();
  watcher=new Watcher(root);
  WATCHERS.put(activity,watcher);
  watcher.attach();
  refresh(root);
 }
 private static final class Watcher implements ViewTreeObserver.OnPreDrawListener,
     ViewTreeObserver.OnGlobalLayoutListener,View.OnAttachStateChangeListener{
  final WeakReference<View> root;
  long checkedAt;
  Watcher(View view){root=new WeakReference<>(view);}
  void attach(){
   View v=root.get();if(v==null)return;
   ViewTreeObserver o=v.getViewTreeObserver();
   if(o.isAlive()){o.addOnGlobalLayoutListener(this);o.addOnPreDrawListener(this);}
   v.addOnAttachStateChangeListener(this);
  }
  void detach(){
   View v=root.get();if(v==null)return;
   ViewTreeObserver o=v.getViewTreeObserver();
   if(o.isAlive()){o.removeOnPreDrawListener(this);o.removeOnGlobalLayoutListener(this);}
   v.removeOnAttachStateChangeListener(this);
  }
  public void onGlobalLayout(){View v=root.get();if(v!=null)refresh(v);}
  public boolean onPreDraw(){
   long now=SystemClock.uptimeMillis();
   if(now-checkedAt>=80L){checkedAt=now;View v=root.get();if(v!=null)refresh(v);}
   return true;
  }
  public void onViewDetachedFromWindow(View v){detach();}
  public void onViewAttachedToWindow(View v){
   ViewTreeObserver o=v.getViewTreeObserver();
   if(o.isAlive()){o.addOnGlobalLayoutListener(this);o.addOnPreDrawListener(this);}
  }
 }


 /** The native drawer's adapter can overwrite a heading after pre-draw. */
 private static final class LabelWatcher implements TextWatcher {
  final WeakReference<TextView> label;
  boolean editing;
  LabelWatcher(TextView view){label=new WeakReference<>(view);}
  public void beforeTextChanged(CharSequence s,int start,int count,int after){}
  public void onTextChanged(CharSequence s,int start,int before,int count){}
  public void afterTextChanged(Editable s){
   TextView view=label.get();
   if(view==null||editing)return;
   String current=s==null?"":s.toString().trim();
   if(!relevant(current))return;
   String translated=translation(view,current);
   if(translated.equals(current))return;
   editing=true;
   try{
    view.setText(translated);
    int[] xy={0,0};view.getLocationOnScreen(xy);
    android.util.Log.i("nicoid-drawer","Native text rebind repaired: "+current+
       " -> "+translated+" at "+xy[0]+","+xy[1]);
   }finally{editing=false;}
  }
 }
 private static boolean relevant(String value){
  return "Setting".equalsIgnoreCase(value)||"Settings".equalsIgnoreCase(value)||
         "設定".equals(value)||"例のアレ".equals(value)||
         "You Know What".equals(value)||"那個".equals(value);
 }
 private static String translation(TextView view,String value){
  String lang=PreferenceManager.getDefaultSharedPreferences(view.getContext())
     .getString("app_lang","0");
  UiStrings.selectLanguage(lang);
  if("Setting".equalsIgnoreCase(value)||"Settings".equalsIgnoreCase(value)||
      "設定".equals(value))
   return PanelUi.tr(view.getContext(),"設定","Settings","設定");
  return PanelUi.tr(view.getContext(),"例のアレ","You Know What","那個");
 }
 private static void bind(TextView label){
  if(label instanceof EditText)return;
  if(LABELS.containsKey(label))return;
  LabelWatcher watcher=new LabelWatcher(label);
  LABELS.put(label,watcher);
  label.addTextChangedListener(watcher);
  int[] xy={0,0};label.getLocationOnScreen(xy);
  android.util.Log.i("nicoid-drawer","Observing native label at "+xy[0]+","+xy[1]+
    " view="+label.getClass().getName());
 }
 public static void refresh(View root){
  if(root==null)return;
  String language=PreferenceManager.getDefaultSharedPreferences(root.getContext())
     .getString("app_lang","0");
  UiStrings.selectLanguage(language);
  int changed=update(root,0);
  if(changed>0)android.util.Log.i("nicoid-drawer",
      "Native labels localized count="+changed+" app_lang="+language);
 }
 private static int update(View node,int depth){
  if(node==null||depth>48)return 0;
  int changed=0;
  if(node instanceof TextView&&!(node instanceof EditText)){
   TextView label=(TextView)node;
   CharSequence raw=label.getText();
   String value=raw==null?"":raw.toString().trim();
   if(relevant(value)){
    bind(label);
    String desired=translation(label,value);
    if(!desired.equals(value)){label.setText(desired);changed++;}
   }
  }
  if(node instanceof ViewGroup){
   ViewGroup group=(ViewGroup)node;
   for(int i=0;i<group.getChildCount();i++)
    changed+=update(group.getChildAt(i),depth+1);
  }
  return changed;
 }
}
