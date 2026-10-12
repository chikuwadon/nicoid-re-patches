package e.e.a;
import java.text.*;
public class SearchDatePresetTest {
 static void check(String want,String actual){if(!want.equals(actual))throw new AssertionError(actual+" != "+want);}
 static long now(String text)throws Exception{return new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").parse(text).getTime();}
 static void range(int mode,String date,String first,String last)throws Exception{long[] r=SearchRange.preset(mode,now(date));check(first,SearchRange.date(r[0]));check(last,SearchRange.date(r[1]));}
 public static void main(String[] args)throws Exception{String n="2026-10-13 00:12:00+0900";range(0,n,"2026-10-13","2026-10-13");range(1,n,"2026-10-06","2026-10-13");range(2,n,"2026-09-13","2026-10-13");range(3,n,"2025-10-13","2026-10-13");range(2,"2026-03-31 21:00:00+0900","2026-02-28","2026-03-31");range(3,"2024-02-29 21:00:00+0900","2023-02-28","2024-02-29");System.out.println("Current-date search presets: 12 JST/day/week/month/year boundary checks passed");}
}
