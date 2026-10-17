package e.e.a;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/** Check every dictionary entry, including format arguments and runtime injection. */
public final class I18nAudit194Test {
 static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 static List<String> formats(String value){List<String> result=new ArrayList<>();Matcher m=Pattern.compile("%(?:(\\d+)\\$)?[-#+ 0,(<]*[0-9]*(?:\\.[0-9]+)?([a-zA-Z%])").matcher(value);while(m.find()){String type=m.group(2);if(!"%".equals(type)&&!"n".equals(type))result.add((m.group(1)==null?"":m.group(1))+":"+type);}return result;}
 @SuppressWarnings("unchecked") public static void main(String[] args)throws Exception{
  Field f=UiStrings.class.getDeclaredField("TEXT");f.setAccessible(true);Map<String,String[]> table=(Map<String,String[]>)f.get(null);
  Set<String> injected=new HashSet<>(Files.readAllLines(Paths.get(args[0]),StandardCharsets.UTF_8));List<String> failure=new ArrayList<>();int entries=0;
  for(Map.Entry<String,String[]> entry:table.entrySet()){
   String key=entry.getKey();String[] values=entry.getValue();entries++;check(values!=null&&values.length==2,"Language count: "+key);
   for(int lang=0;lang<2;lang++){String value=values[lang];if(value==null||value.trim().isEmpty())failure.add("Empty translation: "+key);if(Pattern.compile("[ぁ-ゖァ-ヺ]").matcher(value).find())failure.add("Japanese kana in translation: "+key+" => "+value);if(!formats(key).equals(formats(value)))failure.add("Format mismatch: "+key+" => "+value);}
  }
  check(failure.isEmpty(),failure.toString());
  for(String line:Files.readAllLines(Paths.get(args[1]),StandardCharsets.UTF_8)){if(line.trim().isEmpty()||line.startsWith("#"))continue;String key=line.split("\t",-1)[0].replace("\\n","\n");check(table.containsKey(key),"New key missing: "+key);check(injected.contains(key.replace("\n","\\n")),"Injection missing: "+key);}
  for(String locale:new String[]{"zh-TW","zh_TW","zh-HK","zh_CN"}){UiStrings.selectLanguage(locale);check("儲存".equals(UiStrings.translate("保存")),"Existing Chinese variant: "+locale);}
  UiStrings.selectLanguage("en_US");check("1 row".equals(UiStrings.translate("1行")),"Singular rows");check("2 rows".equals(UiStrings.translate("2行")),"Plural rows");check("1 second".equals(UiStrings.translate("1秒")),"Singular seconds");check("2 seconds".equals(UiStrings.translate("2秒")),"Plural seconds");
  UiStrings.selectLanguage("ja_JP");check("ログインせずに使用".equals(UiStrings.translate("不登入使用")),"Legacy Chinese alias");
  UiStrings.selectLanguage("1");check("Save".equals(UiStrings.translate("保存")),"Language changed");UiStrings.selectLanguage("0");check("保存".equals(UiStrings.translate("保存")),"Language changed back");
  System.out.println("Dictionary entries="+entries+", translations="+(entries*2)+"; formats, kana, new-key injection, Chinese variants, singular/plural and live selection verified");
 }
}
