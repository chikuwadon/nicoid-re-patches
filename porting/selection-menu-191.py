from pathlib import Path
import sys
root=Path(sys.argv[1])
for name in ['NicoidCacheManagerActivity','NicoidVideoListActivity']:
 paths=list(root.glob('**/'+name+'.smali'));assert len(paths)==1
 p=paths[0];s=p.read_text();needle='    invoke-super {p0, p1}, Lcom/sauzask/nicoid/NicoidActivity;->onCreateOptionsMenu(Landroid/view/Menu;)Z'
 hook='    invoke-static {p0, p1}, Le/e/a/BulkSelection;->menu(Landroid/app/Activity;Landroid/view/Menu;)V\n\n'
 if hook not in s:assert needle in s;s=s.replace(needle,hook+needle)
 p.write_text(s)
