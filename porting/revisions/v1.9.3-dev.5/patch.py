from pathlib import Path
import sys
root=Path(sys.argv[1]);p=root/'e/e/a/FullscreenScreen203$State.smali';s=p.read_text().replace('FullscreenViewport204','FullscreenViewport205')
# All of the upper row shares one background. Text receives its own inset.
start=s.index('    iget-object v1, p0, Le/e/a/FullscreenScreen203$State;->bar:Landroid/widget/LinearLayout;',s.index('    .line 71'));end=s.index('    invoke-virtual {v0}, Landroid/view/View;->getContext()',start)
s=s[:start]+'''    iget-object v1, p0, Le/e/a/FullscreenScreen203$State;->bar:Landroid/widget/LinearLayout;
    iget-object v2, p0, Le/e/a/FullscreenScreen203$State;->host:Landroid/view/View;
    iget-object v3, p0, Le/e/a/FullscreenScreen203$State;->tools:Landroid/widget/LinearLayout;
    iget-object v4, p0, Le/e/a/FullscreenScreen203$State;->title:Landroid/view/View;
    invoke-static {v0, v1, v2, v3, v4}, Le/e/a/FullscreenViewport205;->top(Landroid/view/View;Landroid/view/View;Landroid/view/View;Landroid/view/View;Landroid/view/View;)V
''' +s[end:]
# 4dp more between symbols; keep height and drawable scale unchanged.
s=s.replace('const/16 v13, 0x30','const/16 v13, 0x34')
needle='''    move v12, v5''';assert needle in s;s=s.replace(needle,'''    invoke-virtual {v0}, Landroid/view/View;->getContext()Landroid/content/Context;
    move-result-object v12
    const/16 v13, 0x2c
    invoke-static {v12, v13}, Le/e/a/PanelUi;->dp(Landroid/content/Context;I)I
    move-result v12''')
p.write_text(s)
p=root/'e/e/a/FullscreenScreen203.smali';p.write_text(p.read_text().replace('FullscreenViewport204','FullscreenViewport205'))
p=root/'e/e/a/ModernShorts.smali';p.write_text(p.read_text().replace('1.9.3-dev.4','1.9.3-dev.5'))
