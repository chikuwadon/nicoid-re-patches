from pathlib import Path
import re,sys
root=Path(sys.argv[1]);failed=[];count=0
for f in root.rglob('*.smali'):
 for m in re.finditer(r'(?ms)^\.method ([^\n]*run\(\)V)\n(.*?)^\.end method',f.read_text()):
  sig,b=m.groups();r=re.search(r'\.registers (\d+)',b)
  if not r or ' static ' in ' '+sig:continue
  count+=1;p0=int(r[1])-1
  for w in re.finditer(r'(?:const-wide[^ ]*|move-result-wide|iget-wide|sget-wide|move-wide[^ ]*) v(\d+)[^\n]*',b):
   if int(w[1])<=p0<=int(w[1])+1 and re.search(r'\bp0\b',b[w.end():]):failed.append((f.name,w[0],p0))
print('Runnable wide/this alias audit:',count,'methods;',len(failed),'suspect writes')
for v in failed:print(v)
sys.exit(bool(failed))
