package e.e.a;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.lang.reflect.*;
/** Restore the native cookie store when the feed is the first screen opened. */
public final class SessionCookies194 {
 private SessionCookies194(){}
 public static String cookie(Context context,Object preferred)throws Exception {
  SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(context);
  if(p.getBoolean("nologin",false))return "";
  Class<?> type=Class.forName("e.e.a.v0"),store=Class.forName("org.apache.http.client.CookieStore");
  Method serialize=type.getMethod("a",store);
  synchronized(type){
   if(p.getBoolean("nologin",false))return "";
   String value=header(serialize,preferred);if(authenticated(value))return value;
   Field field=type.getField("b");value=header(serialize,field.get(null));if(authenticated(value))return value;
   String saved=p.getString("save_cookie","");if(!authenticated(saved))return "";
   Object restored=type.getMethod("b",String.class).invoke(null,saved);value=header(serialize,restored);
   if(!authenticated(value)||p.getBoolean("nologin",false))return "";
   field.set(null,restored);return value;
  }
 }
 private static String header(Method method,Object store)throws Exception{return store==null?"":(String)method.invoke(null,store);}
 private static boolean authenticated(String header){if(header==null)return false;for(String part:header.split(";")){int i=part.indexOf('=');if(i>=0&&"user_session".equals(part.substring(0,i).trim())&&!part.substring(i+1).trim().isEmpty())return true;}return false;}
}
