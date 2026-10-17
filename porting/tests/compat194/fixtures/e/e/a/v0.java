package e.e.a;
import org.apache.http.client.CookieStore;
public class v0 {public static CookieStore b;public static int parses;public static class Store implements CookieStore {final String header;public Store(String s){header=s;}}public static String a(CookieStore s){return ((Store)s).header;}public static CookieStore b(String s){parses++;return new Store(s);}}
