package e.e.a;
import java.net.URI;
import java.util.Locale;
/** HLS is handled by the player's HLS source, never by the legacy single-file MP4 proxy. */
public final class PlaybackUrls {
 private PlaybackUrls(){}
 public static boolean isHls(String value){if(value==null)return false;try{String path=new URI(value).getPath();return path!=null&&path.toLowerCase(Locale.ROOT).endsWith(".m3u8");}catch(Exception ignored){return false;}}
 public static String directHls(String value){if(value==null)return null;try{URI local=new URI(value);String host=local.getHost();if(!"http".equalsIgnoreCase(local.getScheme())||local.getPort()!=52862||!("127.0.0.1".equals(host)||"localhost".equalsIgnoreCase(host)))return value;String path=local.getRawPath();if(path==null||!path.startsWith("/https://"))return value;String remote=value.substring(value.indexOf("/https://")+1);URI parsed=new URI(remote);if("https".equalsIgnoreCase(parsed.getScheme())&&parsed.getHost()!=null&&isHls(remote))return remote;}catch(Exception ignored){}return value;}
}
