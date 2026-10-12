package android.net;
public final class Uri {
 private String value;private Uri(String v){value=v;}public static Uri parse(String v){return new Uri(v);}
 public String getQueryParameter(String k){try{String q=value.substring(value.indexOf('?')+1);for(String x:q.split("&")){String[] p=x.split("=",2);if(java.net.URLDecoder.decode(p[0],"UTF-8").equals(k))return p.length==2?java.net.URLDecoder.decode(p[1],"UTF-8"):"";}}catch(Exception ignored){}return null;}
 public static class Builder {public Builder appendQueryParameter(String key,String value){return this;}}
}
