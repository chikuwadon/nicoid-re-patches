"""Compile and compare the tracked base against working sources using one JDK.

No API traffic is sent. Supply local Android/stub jars for the native row binder.
"""
from pathlib import Path
import argparse, subprocess, json, re
p=argparse.ArgumentParser();p.add_argument('--work',type=Path,required=True);p.add_argument('--android',required=True);p.add_argument('--helpers',required=True);p.add_argument('--ecj',required=True);p.add_argument('--base',default='095c69a');a=p.parse_args()
repo=Path(__file__).resolve().parents[3];tests=Path(__file__).parent;src=repo/'extensions/extension/src/main/java/e/e/a';w=a.work.resolve();w.mkdir(parents=True,exist_ok=True)
names=['PageCache','NetworkTask','ContentFilterRules','VideoRows191','ImageDiskCache']
baseline=w/'baseline';baseline.mkdir(exist_ok=True)
for name in names:
 (baseline/(name+'.java')).write_bytes(subprocess.check_output(['git','show',a.base+':'+str((src/(name+'.java')).relative_to(repo))],cwd=repo))
s=(baseline/'VideoRows191.java').read_text();start=s.index('    static long millis(');end=s.index('    static String date(String source',start);date=re.search(r'    private static final Pattern DATE = [^\n]+',s).group()
(w/'LegacyDates.java').write_text('package e.e.a;import java.util.*;import java.text.*;import java.util.regex.*;final class LegacyDates {\n'+date+'\n'+s[start:end]+'}\n')
(w/'UiStrings.java').write_text('package e.e.a;public final class UiStrings {public static String translate(String s){return s;}}')
result={}
for mode in ['before','after']:
 out=w/mode;out.mkdir(exist_ok=True)
 sources=[(baseline if mode=='before' else src)/(n+'.java') for n in names if n!='VideoRows191']
 # Compile the exact timestamp methods without replacing native Android row types.
 facade=w/(mode+'-source');facade.mkdir(exist_ok=True)
 methods=s[start:end] if mode=='before' else 'static long millis(String s){return RowDates194.millis(s);} static String date(long t,String label){return RowDates194.format(t,label);}'
 (facade/'VideoRows191.java').write_text('package e.e.a;import java.util.*;import java.text.*;import java.util.regex.*;final class VideoRows191 {\n'+(date if mode=='before' else '')+'\n'+methods+'}\n')
 sources.append(facade/'VideoRows191.java')
 if mode=='after':sources.append(src/'RowDates194.java')
 sources += [w/'UiStrings.java',tests/'AuditBenchmark.java']
 if mode=='after':sources += [w/'LegacyDates.java',tests/'AuditRegressionTest.java',repo/'porting/tests/PerformanceTest.java',src/'HistoryRules.java',repo/'porting/tests/ContentHistoryRulesTest.java']
 subprocess.run(['java','-jar',a.ecj,'-1.8','-warn:none','-encoding','UTF-8','-cp',a.android+':'+a.helpers,'-d',str(out),*map(str,sources)],check=True)
 cp=str(out)+':'+a.android
 if mode=='after':
  for test in ['AuditRegressionTest','PerformanceTest','ContentHistoryRulesTest']:subprocess.run(['java','-cp',cp,'e.e.a.'+test],check=True)
 # Independent processes expose warm-up variance; keep every raw result.
 runs=[]
 for n in range(3):
  run=subprocess.check_output(['java','-Xms64m','-Xmx256m','-cp',cp,'e.e.a.AuditBenchmark'],text=True);runs.append(json.loads(run))
 result[mode]=runs
 (w/'measurements.json').write_text(json.dumps(result,ensure_ascii=False,indent=2))
 print(mode,json.dumps(runs,ensure_ascii=False),flush=True)
print(w/'measurements.json')
