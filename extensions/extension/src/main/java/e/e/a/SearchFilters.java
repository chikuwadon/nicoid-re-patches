package e.e.a;
import android.content.*;import android.net.Uri;import android.preference.PreferenceManager;import android.view.View;import android.widget.*;import java.text.*;import java.util.*;import org.json.JSONObject;
/** Stable filter values are independent of the visible order in the dialog. */
public final class SearchFilters {
 static final int[] LENGTH_ORDER={0,3,4,1,5,6,2,7};
 static final String[] PERIOD={"指定なし","24時間以内","1週間以内","1ヶ月以内","半年以内","1年以内","3年以内","5年以内"};
 static final String[] LENGTH={"指定なし","5分以内","20分以上","1分以内","3分以内","10分以内","20分以内","60分以上"};
 static String tr(Context c,String ja){return UiStrings.translate(ja);}
 public static void install(Context c,View root,int[] selected){
  SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(c);
  choices(c,(Spinner)root.findViewById(0x7f080189),PERIOD,null,selected,2,p.getInt("search_Time",selected[2]));
  choices(c,(Spinner)root.findViewById(0x7f08018a),LENGTH,LENGTH_ORDER,selected,3,p.getInt("search_Date",selected[3]));
  selected[2]=Math.max(0,Math.min(8,p.getInt("search_Time",selected[2])));selected[3]=Math.max(0,Math.min(8,p.getInt("search_Date",selected[3])));SearchRange.install(c,root,selected);
 }
 static void choices(Context c,Spinner s,String[] names,int[] order,int[] selected,int slot,int stored){if(s==null)return;int value=Math.max(0,Math.min(names.length-1,stored));ArrayAdapter<String> a=new ArrayAdapter<>(c,android.R.layout.simple_spinner_item);a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);int index=0;for(int i=0;i<names.length;i++){int k=order==null?i:order[i];a.add(tr(c,names[k]));if(k==value)index=i;}selected[slot]=value;s.setOnItemSelectedListener(null);s.setAdapter(a);s.setSelection(index);s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){public void onNothingSelected(AdapterView<?> p){}public void onItemSelected(AdapterView<?> p,View v,int pos,long id){selected[slot]=order==null?pos:order[pos];}});}
 public static String url(int[] values,String keyword){return SearchRange.url(values,keyword);}
 public static void apply(Uri input,Uri.Builder target){String since=input.getQueryParameter("minRegisteredAt"),min=input.getQueryParameter("minDuration"),max=input.getQueryParameter("maxDuration");if(since!=null){target.appendQueryParameter("start",since.substring(0,Math.min(10,since.length())));target.appendQueryParameter("minRegisteredAt",since);}String until=input.getQueryParameter("maxRegisteredAt");if(until!=null){target.appendQueryParameter("end",until.substring(0,Math.min(10,until.length())));target.appendQueryParameter("maxRegisteredAt",until);}if(min!=null){target.appendQueryParameter("minDuration",min);target.appendQueryParameter("min_duration",min);if(Integer.parseInt(min)>=1200)target.appendQueryParameter("l_range","2");}if(max!=null){target.appendQueryParameter("maxDuration",max);target.appendQueryParameter("max_duration",max);if(Integer.parseInt(max)<=300)target.appendQueryParameter("l_range","1");}}
 public static boolean matches(JSONObject item,String request){return SearchFilterRules.matches(item,request);}
}
