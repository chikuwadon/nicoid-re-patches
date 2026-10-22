package e.e.a;
import java.net.URI;import org.json.JSONObject;
/** Session-local compatibility for the legacy receiver's executable object-message protocol. */
public final class CastPlayback216 {
 private CastPlayback216(){}
 public static String message(JSONObject request){
  String json=request.toString();
  if(request.optInt("type",-1)!=0)return json;
  try{URI uri=URI.create(request.optString("loadurl"));
   if(!"http".equals(uri.getScheme())||uri.getHost()==null||uri.getUserInfo()!=null||uri.getQuery()!=null||uri.getFragment()!=null||!uri.getPath().matches("/cast/[a-f0-9-]{36}/[0-9]+\\.m3u8"))return json;
   CastDiagnostics.record("Cast: enabling receiver fMP4 compatibility");
   return CastCompat216Script.CODE+"("+json.replace("\u2028","\\u2028").replace("\u2029","\\u2029")+")";
  }catch(Exception ignored){return json;}
 }
}
