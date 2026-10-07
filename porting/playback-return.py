#!/usr/bin/env python3
"""Use a bounded foreground handoff and the authenticated upload feed."""
from pathlib import Path
import re, sys
root = Path(sys.argv[1]) / 'smali'
def replace(path, signature, body):
    file = root / path
    source = file.read_text()
    pattern = r'(?m)^\.method [^\n]*' + re.escape(signature) + r'\n[\s\S]*?^\.end method'
    source, count = re.subn(pattern, body, source)
    assert count == 1, (path, signature, count)
    file.write_text(source)
replace('e/e/a/q0.smali', 'onClick(Landroid/view/View;)V', '''.method public onClick(Landroid/view/View;)V
    .locals 1
    iget-object v0, p0, Le/e/a/q0;->a:Lcom/sauzask/nicoid/NicoidPopupViewService;
    invoke-static {v0}, Le/e/a/PlaybackReturn;->open(Ljava/lang/Object;)V
    return-void
.end method''')
replace('com/sauzask/nicoid/NicoidNicorepoActivity.smali', 'onCreateOptionsMenu(Landroid/view/Menu;)Z', '''.method public onCreateOptionsMenu(Landroid/view/Menu;)Z
    .locals 1
    invoke-static {p0, p1}, Le/e/a/FollowFeed;->menu(Landroid/app/Activity;Landroid/view/Menu;)Z
    move-result v0
    return v0
.end method''')
replace('com/sauzask/nicoid/NicoidNicorepoActivity.smali', 'onPrepareOptionsMenu(Landroid/view/Menu;)Z', '''.method public onPrepareOptionsMenu(Landroid/view/Menu;)Z
    .locals 1
    const/4 v0, 0x1
    return v0
.end method''')
replace('com/sauzask/nicoid/NicoidNicorepoActivity.smali', 't()V', '''.method public final t()V
    .locals 0
    invoke-static {p0}, Le/e/a/FollowFeed;->load(Landroid/app/Activity;)V
    return-void
.end method''')
