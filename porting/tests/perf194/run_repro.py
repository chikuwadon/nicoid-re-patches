"""Demand original failures and corrected successes from identical assertions."""
from pathlib import Path
import argparse,subprocess,json
p=argparse.ArgumentParser();p.add_argument('--host-work',type=Path,required=True);p.add_argument('--ecj',required=True);a=p.parse_args();source=Path(__file__).parent;w=a.host_work.resolve();out=w/'repro';out.mkdir(exist_ok=True)
subprocess.run(['java','-jar',a.ecj,'-1.8','-warn:none','-cp',str(w/'after'),'-d',str(out),str(source/'BeforeAfterReproTest.java')],check=True)
rows=[]
for case in ['shared','auth','queue','late']:
 row={'case':case}
 for mode in ['before','after']:
  r=subprocess.run(['java','-cp',str(out)+':'+str(w/mode),'e.e.a.BeforeAfterReproTest',case],capture_output=True,text=True,timeout=15)
  row[mode]={'exit':r.returncode,'output':r.stdout+r.stderr}
 assert row['before']['exit']!=0 and row['after']['exit']==0,row
 rows.append(row);print(case,'original FAIL / corrected PASS',flush=True)
(w/'reproduction-results.json').write_text(json.dumps(rows,indent=2))
