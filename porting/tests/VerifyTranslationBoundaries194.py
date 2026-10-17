"""Inspect actual applied DEX disassembly; preferences must never be translated as resources."""
from pathlib import Path
import argparse,json,re
p=argparse.ArgumentParser();p.add_argument('smali',type=Path,nargs='+');a=p.parse_args()
counts={'resource_calls_translated':0,'data_calls':0,'data_calls_translated':0};failures=[]
for root in a.smali:
 for file in root.rglob('*.smali'):
  for match in re.finditer(r'^\.method[^\n]*\n(.*?)^\.end method',file.read_text(),re.M|re.S):
   instructions=[s.strip() for s in match.group(1).splitlines() if s.strip() and not s.strip().startswith(('.',':','#'))]
   for i,line in enumerate(instructions):
    call=re.search(r'(L(?:android/content/|android/app/|android/preference/|com/sauzask/nicoid/)[^;]+;)->getString\(([^)]*)\)Ljava/lang/String;',line)
    if not call:continue
    resource=call.group(2) in ['I','I[Ljava/lang/Object;']
    translated=i+2<len(instructions) and instructions[i+1].startswith('move-result-object') and 'Le/e/a/UiStrings;->translate(Ljava/lang/String;)' in instructions[i+2]
    if resource:
     if translated:counts['resource_calls_translated']+=1
    else:
     counts['data_calls']+=1
     if translated:counts['data_calls_translated']+=1;failures.append(str(file.relative_to(root))+': '+match.group(0).splitlines()[0])
print(json.dumps({'counts':counts,'violations':failures},ensure_ascii=False,indent=2))
assert counts['resource_calls_translated']>0,'No real resource hook found; wrong input?'
assert not failures,'Resource hook translated non-resource application data'
