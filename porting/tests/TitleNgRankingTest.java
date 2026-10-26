package e.e.a;
import java.util.*;
public final class TitleNgRankingTest {
 public static class Row {public String g;Row(String g){this.g=g;}}
 static void check(boolean value,String label){if(!value)throw new AssertionError(label);}
 public static void main(String[] args)throws Exception{
  ArrayList<Row> rows=new ArrayList<>(Arrays.asList(new Row("2"),new Row("3"),new Row("5"),new Row("101")));
  ContentFilter.renumber(rows);
  check(rows.get(0).g.equals("1")&&rows.get(1).g.equals("2")&&rows.get(2).g.equals("3")&&rows.get(3).g.equals("4"),"Hidden ranking rows leave no gaps");
  ArrayList<Row> unranked=new ArrayList<>(Arrays.asList(new Row("7"),new Row(null)));ContentFilter.renumber(unranked);
  check(unranked.get(0).g.equals("7"),"Rows without ranks are unchanged");
  ContentFilterRules.Rule rule=new ContentFilterRules.Rule("【実況】ゲーム Part1","exact",true);
  check(rule.matches("【実況】ゲーム Part1")&&!rule.matches("【実況】ゲーム Part10"),"Full title rule matches only that title");
  System.out.println("Title NG and ranking display order verified");
 }
}
