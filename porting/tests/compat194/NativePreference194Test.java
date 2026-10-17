package e.e.a;
import android.app.Activity;
import android.preference.*;
import java.lang.reflect.Method;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE,sdk={28,34})
public class NativePreference194Test {
 @Test public void everySelectedEntrySummaryUpdatesWithoutLeavingSettings()throws Exception {
  Activity a=Robolectric.buildActivity(Activity.class).setup().get();
  Method translate=Class.forName("e.e.a.Followup173").getDeclaredMethod("translatePreferences",PreferenceGroup.class);translate.setAccessible(true);
  for(String lang:new String[]{"ja","en","zh-TW"}) {
   Class.forName("e.e.a.UiStrings").getMethod("selectLanguage",String.class).invoke(null,lang);
   PreferenceScreen root=PreferenceManager.class.getDeclaredConstructor(Activity.class,int.class).newInstance(a,100).createPreferenceScreen(a);
   for(String key:new String[]{"startup_screen","mobile_quality_mode","app_switch_playback","back_playback","default_playback_speed","future_choice"}) {
    ListPreference p=new ListPreference(a);p.setPersistent(false);p.setKey(key);p.setTitle("起動時の画面");p.setEntries(new String[]{"ホーム","ランキング","Custom 100%"});p.setEntryValues(new String[]{"home","ranking","custom-x"});p.setValue("home");p.setSummary("%s");root.addPreference(p);
   }
   ListPreference explanation=new ListPreference(a);explanation.setPersistent(false);explanation.setEntries(new String[]{"A","B"});explanation.setEntryValues(new String[]{"a","b"});explanation.setValue("a");explanation.setSummary("次の再生から反映されます");root.addPreference(explanation);
   for(int pass=0;pass<2;pass++) {
    translate.invoke(null,root);Class.forName("e.e.a.UiText").getMethod("preferences",Preference.class).invoke(null,root);
    for(int i=0;i<root.getPreferenceCount()-1;i++) {
     ListPreference p=(ListPreference)root.getPreference(i);p.setValue("ranking");assertEquals(lang+" / "+p.getKey(),p.getEntry(),p.getSummary());
     android.view.View row=p.getView(null,new android.widget.FrameLayout(a));
     assertEquals(p.getEntry().toString(),((android.widget.TextView)row.findViewById(android.R.id.summary)).getText().toString());
     p.setValue("custom-x");assertEquals("Custom 100%",p.getSummary());p.setValue("home");assertEquals(p.getEntry(),p.getSummary());
    }
    assertNotEquals(explanation.getEntry(),explanation.getSummary());
   }
  }
 }
}
