package e.e.a;
import android.content.Context;
import android.net.*;
import java.io.IOException;
import java.net.*;
import java.util.*;

/** Resolve a usable LAN address once when the legacy Wi-Fi API returns no address. */
public final class CastAddress194 {
 private CastAddress194(){}
 public static String resolve(Object owner,String original)throws IOException {
  if(usable(original))return original;
  if(owner instanceof Context){
   ConnectivityManager manager=(ConnectivityManager)((Context)owner).getSystemService(Context.CONNECTIVITY_SERVICE);
   if(manager!=null)try{
    Network active=manager.getActiveNetwork();String address=address(manager,active);if(address!=null)return address;
    // One snapshot on Cast startup, never periodic polling or a second network request.
    for(Network network:manager.getAllNetworks()){if(network.equals(active))continue;address=address(manager,network);if(address!=null)return address;}
   }catch(SecurityException error){throw new IOException("Cast network metadata unavailable",error);}
  }
  throw new IOException("No usable local Cast address");
 }
 private static String address(ConnectivityManager manager,Network network){
  if(network==null)return null;NetworkCapabilities caps=manager.getNetworkCapabilities(network);
  if(caps==null||caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)||!(caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)||caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)))return null;
  LinkProperties properties=manager.getLinkProperties(network);if(properties==null)return null;
  for(LinkAddress link:properties.getLinkAddresses()){InetAddress ip=link.getAddress();if(ip instanceof Inet4Address&&!ip.isAnyLocalAddress()&&!ip.isLoopbackAddress()&&!ip.isLinkLocalAddress()&&!ip.isMulticastAddress())return "http://"+ip.getHostAddress()+":52862/";}
  return null;
 }
 static boolean usable(String base){
  if(base==null)return false;
  try{URI uri=new URI(base);if(!"http".equals(uri.getScheme())||uri.getPort()!=52862)return false;String host=uri.getHost();if(host==null)return false;String[] parts=host.split("\\.");if(parts.length!=4)return false;for(String part:parts)if(!part.matches("[0-9]{1,3}")||Integer.parseInt(part)>255)return false;int first=Integer.parseInt(parts[0]);return first!=0&&first!=127&&first<224&&!(first==169&&"254".equals(parts[1]));}catch(Exception error){return false;}
 }
}
