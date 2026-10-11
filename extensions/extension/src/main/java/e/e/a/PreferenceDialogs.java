package e.e.a;
import android.app.*;
import android.preference.*;
/** Style framework-owned preference dialogs after their normal click opens them. */
public final class PreferenceDialogs {
 public static void attach(PreferenceActivity activity){walk(activity.getPreferenceScreen());}
 private static void walk(Preference preference){
  if(preference instanceof PreferenceGroup){PreferenceGroup group=(PreferenceGroup)preference;for(int i=0;i<group.getPreferenceCount();i++)walk(group.getPreference(i));}
  if(preference instanceof ListPreference && ("quality_mode".equals(preference.getKey())||"mobile_quality_mode".equals(preference.getKey()))){ListPreference list=(ListPreference)preference;Preference.OnPreferenceChangeListener prior=list.getOnPreferenceChangeListener();list.setOnPreferenceChangeListener((clicked,value)->{if(prior!=null&&!prior.onPreferenceChange(clicked,value))return false;list.setValue(String.valueOf(value));list.setSummary("%s");return true;});}
  if(preference instanceof DialogPreference){Preference.OnPreferenceClickListener prior=preference.getOnPreferenceClickListener();preference.setOnPreferenceClickListener(clicked->{boolean result=prior!=null&&prior.onPreferenceClick(clicked);Dialog dialog=((DialogPreference)clicked).getDialog();if(dialog instanceof AlertDialog)PlaybackSession.styleDialog((AlertDialog)dialog);return result;});}
 }
}
