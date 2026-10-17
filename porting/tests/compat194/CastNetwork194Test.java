package e.e.a;
import android.app.Service;
import android.content.*;
import android.net.*;
import android.os.IBinder;
import java.net.URL;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.*;
import static org.junit.Assert.*;

/** Exercise the emitted relay and Android network metadata, without contacting a TV. */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE,sdk={28,34})
public class CastNetwork194Test {
 public static class Host extends Service {
  public Object n=new Object();public String i="http://0.0.0.0:52862/",j="legacy";
  @Override public IBinder onBind(Intent intent){return null;}
 }
 public static class Callback {public Host a;Callback(Host host){a=host;}}
 Host host;ShadowConnectivityManager manager;
 @Before public void setup()throws Exception{host=Robolectric.buildService(Host.class).create().get();manager=Shadows.shadowOf((ConnectivityManager)host.getSystemService(Context.CONNECTIVITY_SERVICE));manager.clearAllNetworks();Class.forName("e.e.a.ModernPlayback").getField("domandCookie").set(null,"domand_bid=fixture");}
 @After public void teardown()throws Exception{Class.forName("e.e.a.CastRelay").getMethod("detach",Object.class).invoke(null,host.n);host.onDestroy();}
 void network(int id,int transport,String ip)throws Exception{Network network=new Network(id);manager.addNetwork(network,ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.CONNECTED,transport==NetworkCapabilities.TRANSPORT_WIFI?ConnectivityManager.TYPE_WIFI:ConnectivityManager.TYPE_ETHERNET,0,true,true));NetworkCapabilities capabilities=new NetworkCapabilities();capabilities.addTransportType(transport);manager.setNetworkCapabilities(network,capabilities);LinkProperties properties=new LinkProperties();properties.addLinkAddress(new LinkAddress(java.net.InetAddress.getByName(ip),24));manager.setLinkProperties(network,properties);}
 void attach()throws Exception{Object callback=new Callback(host);Class<?> relay=Class.forName("e.e.a.CastRelay");relay.getMethod("prepare",Object.class,String.class).invoke(null,callback,"https://delivery.domand.nicovideo.jp/hls/master.m3u8");relay.getMethod("attach",Object.class).invoke(null,callback);}
 @Test public void unavailableLegacyWifiAddressUsesLanMetadata()throws Exception{network(108,NetworkCapabilities.TRANSPORT_WIFI,"192.0.2.11");attach();assertEquals("192.0.2.11",new URL(host.j).getHost());assertEquals("http://192.0.2.11:52862/",host.i);}
 @Test public void ethernetIsUsableWithoutWifiInfo()throws Exception{network(109,NetworkCapabilities.TRANSPORT_ETHERNET,"192.0.2.12");attach();assertEquals("192.0.2.12",new URL(host.j).getHost());}
 @Test public void unusableNetworksDoNotAdvertiseWildcardOrVpnAddresses()throws Exception{network(110,NetworkCapabilities.TRANSPORT_VPN,"192.0.2.13");attach();assertEquals("legacy",host.j);}
 @Test public void validNativeWifiAddressIsPreserved()throws Exception{host.i="http://192.0.2.10:52862/";network(111,NetworkCapabilities.TRANSPORT_ETHERNET,"192.0.2.12");attach();assertEquals("192.0.2.10",new URL(host.j).getHost());}
}
