from pathlib import Path
import re,sys
root=Path(sys.argv[1]);delta=Path(sys.argv[2])
for name in ['FullscreenScreen203.smali','FullscreenScreen203$State.smali']:
 p=root/'e/e/a'/name;p.write_text(p.read_text().replace('FullscreenViewport205','FullscreenViewport206'))
p=root/'e/e/a/ModernShorts.smali';p.write_text(p.read_text().replace('1.9.3-dev.5','1.9.3-dev.6'))
p=delta/'com/sauzask/nicoid/NicoidVideoFragment.smali';s=p.read_text()
for signature in ['g(Z)V','h(Z)V','i(I)V']:
 pattern=r'(\.method public final '+re.escape(signature)+r'\n    \.registers \d+\n)'
 s,count=re.subn(pattern,r'\1\n    invoke-static/range {p0 .. p0}, Le/e/a/FullscreenScreen203;->before(Ljava/lang/Object;)V\n',s)
 assert count==1,(signature,count)
p.write_text(s)

# The side pane owns the remaining width. Skip the old screen-wide control override.
p=root/'e/e/a/Revision201.smali';s=p.read_text();needle="""    :cond_12
    invoke-virtual {p0}, Landroid/view/View;->getContext()Landroid/content/Context;""";assert needle in s;s=s.replace(needle,"""    :cond_12
    invoke-static {p0}, Le/e/a/FullscreenViewport206;->side(Landroid/view/View;)Z
    move-result v1
    if-eqz v1, :side_normal206
    return-void
    :side_normal206
    invoke-virtual {p0}, Landroid/view/View;->getContext()Landroid/content/Context;""",1);p.write_text(s)
