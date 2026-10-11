package e.e.a;
import org.json.JSONObject;
public final class PageRulesTest {
 static void check(boolean value){if(!value)throw new AssertionError();}
 public static void main(String[] args)throws Exception {
  JSONObject total=new JSONObject().put("totalCount",305);int pages=0,loaded=0;
  for(int page=1;;page++){int count=Math.min(100,305-loaded);loaded+=count;pages++;if(!PageRules.more(total,count,page,100))break;}
  check(loaded==305&&pages==4);
  check(PageRules.more(new JSONObject(),100,2,100));
  check(!PageRules.more(new JSONObject(),99,3,100));
  check(!PageRules.more(new JSONObject().put("hasNext",true),0,1,100));
  check(!PageRules.more(new JSONObject().put("hasNext",false),100,1,100));
  check(PageRules.more(new JSONObject().put("hasNext",true),20,1,100));
  check(!PageRules.more(new JSONObject().put("total",200),100,2,100));
  System.out.println("Pagination: 305 items, page boundaries and empty/explicit end passed");
 }
}
