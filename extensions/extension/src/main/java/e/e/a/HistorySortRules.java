package e.e.a;
import java.text.*;import java.util.*;
final class HistorySortRules {
 static long watched(String text){if(text==null)return 0;text=text.replaceAll("<[^>]+>","").trim();for(String pattern:new String[]{"yyyy年MM月dd日 HH時mm分","yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm","yyyy/MM/dd HH:mm:ss","yyyy/MM/dd HH:mm","yyyy-MM-dd'T'HH:mm:ssXXX"})try{SimpleDateFormat f=new SimpleDateFormat(pattern,Locale.ROOT);f.setLenient(false);f.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));Date date=f.parse(text);if(date!=null)return date.getTime();}catch(Exception ignored){}return 0;}
 static int compare(long first,long second,int firstPosition,int secondPosition,boolean oldest){if(first<=0||second<=0){if(first>0)return -1;if(second>0)return 1;return Integer.compare(firstPosition,secondPosition);}int value=Long.compare(first,second);return oldest?value:-value;}
}
