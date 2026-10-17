"""Check helper class references against all DEXes of an applied APK (disassembled)."""
from pathlib import Path
import re,json,sys
root=Path(sys.argv[1]);defined=set();references={}
for path in root.rglob('*.smali'):
 for line in path.read_text().splitlines():
  if line.startswith('.class '):defined.add(line.split()[-1])
  if line.lstrip().startswith(('#','.')):continue
  for name in re.findall(r'Le/e/a/[^;\s]+;',line):references.setdefault(name,set()).add(str(path.relative_to(root)))
assert defined and references,'No disassembled classes/helper references found'
missing={name:sorted(paths) for name,paths in references.items() if name not in defined}
print(json.dumps({'defined_classes':len(defined),'referenced_helper_types':len(references),'missing':missing},indent=2))
sys.exit(1 if missing else 0)
