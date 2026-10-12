package e.e.a;
import java.util.*;import java.text.*;import org.json.JSONObject;
final class SearchFilterRules {
 static final int[] LENGTH_ORDER={0,3,4,1,5,6,2,7};
 static final String[] PERIOD={"指定なし","24時間以内","1週間以内","1ヶ月以内","半年以内","1年以内","3年以内","5年以内"};
 static final String[] LENGTH={"指定なし","5分以内","20分以上","1分以内","3分以内","10分以内","20分以内","60分以上"};
 static String encode(String s){try{return java.net.URLEncoder.encode(s,"UTF-8");}catch(Exception e){throw new IllegalArgumentException(e);}}
 public static String url(int[] values,String keyword){String[] sorts={"lastCommentTime","lastCommentTime","viewCount","viewCount","commentCount","commentCount","mylistCount","mylistCount","registeredAt","registeredAt","duration","duration","hotMylist"};int sort=Math.max(0,Math.min(12,values[1]));StringBuilder s=new StringBuilder("https://nvapi.nicovideo.jp/v1/search/video?").append(values[0]==1?"tag=":"keyword=").append(encode(keyword)).append("&sortKey=").append(sorts[sort]).append("&sortOrder=").append(sort==12?"none":sort%2==0?"desc":"asc");
  String since=since(values[2],System.currentTimeMillis());if(since!=null)s.append("&minRegisteredAt=").append(encode(since));int min=min(values[3]),max=max(values[3]);if(min>0)s.append("&minDuration=").append(min);if(max>0)s.append("&maxDuration=").append(max);return s.append("&sensitiveContents=mask").toString();}
 static String since(int period,long now){if(period<=0||period>=PERIOD.length)return null;Calendar c=Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"));c.setTimeInMillis(now);switch(period){case 1:c.add(Calendar.HOUR_OF_DAY,-24);break;case 2:c.add(Calendar.DAY_OF_YEAR,-7);break;case 3:c.add(Calendar.MONTH,-1);break;case 4:c.add(Calendar.MONTH,-6);break;case 5:c.add(Calendar.YEAR,-1);break;case 6:c.add(Calendar.YEAR,-3);break;case 7:c.add(Calendar.YEAR,-5);break;}SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX",Locale.ROOT);f.setTimeZone(c.getTimeZone());return f.format(c.getTime());}
 static int min(int index){return index==2?1200:index==7?3600:0;}static int max(int index){return index==1?300:index==3?60:index==4?180:index==5?600:index==6?1200:0;}
 private static final LinkedHashMap<String,Bounds> FILTERS=new LinkedHashMap<String,Bounds>(8,.75f,true){protected boolean removeEldestEntry(Map.Entry<String,Bounds> e){return size()>8;}};
 private static final ThreadLocal<SimpleDateFormat> DATES=new ThreadLocal<SimpleDateFormat>(){protected SimpleDateFormat initialValue(){SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX",Locale.ROOT);f.setLenient(false);return f;}};
 private static long date(String value){ParsePosition position=new ParsePosition(0);Date date=DATES.get().parse(value,position);if(date==null||position.getIndex()!=value.length())throw new IllegalArgumentException("Invalid search date");return date.getTime();}
 static final class Bounds {
  final int low,high;final long from,to;final boolean valid;
  Bounds(String request){int min=-1,max=-1;long first=Long.MIN_VALUE,last=Long.MAX_VALUE;boolean ok=true;
   try{Query q=new Query(request);String low=q.getQueryParameter("minDuration"),high=q.getQueryParameter("maxDuration"),from=q.getQueryParameter("minRegisteredAt"),to=q.getQueryParameter("maxRegisteredAt");if(low!=null){min=Integer.parseInt(low);if(min<0)throw new IllegalArgumentException();}if(high!=null){max=Integer.parseInt(high);if(max<0)throw new IllegalArgumentException();}if(from!=null)first=date(from);if(to!=null)last=date(to);if(max>=0&&min>max||first>last)throw new IllegalArgumentException();}catch(RuntimeException e){ok=false;}
   this.low=min;this.high=max;this.from=first;this.to=last;this.valid=ok;
  }
 }
 static synchronized Bounds bounds(String request){Bounds result=FILTERS.get(request);if(result==null){result=new Bounds(request);if(request!=null&&request.length()<=16384)FILTERS.put(request,result);}return result;}
 public static boolean matches(JSONObject item,String request){Bounds b=bounds(request);if(!b.valid||item==null)return false;int duration=item.optInt("duration",-1);if(b.low>=0&&(duration<0||duration<b.low)||b.high>=0&&(duration<0||duration>b.high))return false;if(b.from!=Long.MIN_VALUE||b.to!=Long.MAX_VALUE){try{long actual=date(item.optString("registeredAt"));if(actual<b.from||actual>b.to)return false;}catch(RuntimeException e){return false;}}return true;}
 static final class Query {final java.util.Map<String,String> values=new java.util.HashMap<>();Query(String url){try{String q=new java.net.URI(url).getRawQuery();if(q!=null)for(String part:q.split("&")){String[] p=part.split("=",2);values.put(java.net.URLDecoder.decode(p[0],"UTF-8"),p.length==2?java.net.URLDecoder.decode(p[1],"UTF-8"):"");}}catch(Exception e){throw new IllegalArgumentException(e);}}String getQueryParameter(String key){return values.get(key);}}
}
