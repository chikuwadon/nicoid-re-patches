from pathlib import Path
import re,shutil,sys
root=Path(sys.argv[1]);p=root/'e/e/a/FullscreenScreen203$State.smali';s=p.read_text()
s=re.sub(r'\.method bounds\(\)V.*?\.end method','''.method bounds()V
    .registers 5
    iget-object v0, p0, Le/e/a/FullscreenScreen203$State;->safe:Landroid/graphics/Rect;
    iget-object v1, p0, Le/e/a/FullscreenScreen203$State;->decor:Landroid/view/ViewGroup;
    invoke-virtual {v1}, Landroid/view/ViewGroup;->getWidth()I
    move-result v2
    invoke-virtual {v1}, Landroid/view/ViewGroup;->getHeight()I
    move-result v3
    const/4 v1, 0x0
    invoke-virtual {v0, v1, v1, v2, v3}, Landroid/graphics/Rect;->set(IIII)V
    return-void
.end method''',s,flags=re.S)
needle='''    :cond_1c
    invoke-virtual {v0}, Landroid/view/View;->getContext()Landroid/content/Context;'''
assert needle in s
s=s.replace(needle,'''    :cond_1c
    iget-object v1, p0, Le/e/a/FullscreenScreen203$State;->decor:Landroid/view/ViewGroup;
    iget-object v2, p0, Le/e/a/FullscreenScreen203$State;->bar:Landroid/widget/LinearLayout;
    invoke-static {v0, v1, v2}, Le/e/a/FullscreenViewport204;->update(Landroid/view/View;Landroid/view/ViewGroup;Landroid/view/View;)V
    invoke-virtual {v0}, Landroid/view/View;->getContext()Landroid/content/Context;''')
# The shared v4 remains GONE for hidden children; only the outside gap is zero.
needle='''    invoke-static {v1, v4}, Le/e/a/PanelUi;->dp(Landroid/content/Context;I)I

    move-result v3'''
assert needle in s;s=s.replace(needle,'''    const/4 v3, 0x0''')
# Paint only the actual button strip, leaving the title/spacer transparent.
needle='''    :cond_147
    invoke-virtual {v0}, Landroid/view/View;->getContext()Landroid/content/Context;'''
assert needle in s;s=s.replace(needle,'''    :cond_147
    iget-object v1, p0, Le/e/a/FullscreenScreen203$State;->bar:Landroid/widget/LinearLayout;
    const/4 v2, 0x0
    invoke-virtual {v1, v2}, Landroid/view/View;->setBackgroundColor(I)V
    iget-object v1, p0, Le/e/a/FullscreenScreen203$State;->host:Landroid/view/View;
    const/high16 v2, 0x77000000
    invoke-virtual {v1, v2}, Landroid/view/View;->setBackgroundColor(I)V
    invoke-virtual {v0}, Landroid/view/View;->getContext()Landroid/content/Context;''')
# Text labels need less horizontal space; icon slots and Glyph scaling stay unchanged.
s=s.replace('const/16 v13, 0x40','const/16 v13, 0x30')
p.write_text(s)
p=root/'e/e/a/FullscreenScreen203.smali';s=p.read_text();needle='''.method public static restore(Landroid/view/View;)V
    .registers 2''';assert needle in s;s=s.replace(needle,needle+'''
    invoke-static {p0}, Le/e/a/FullscreenViewport204;->restore(Landroid/view/View;)V
''');p.write_text(s)
# Compact the painted strip to 40dp without changing the glyph's pixel size.
p=root/'e/e/a/FullscreenScreen203$State.smali';s=p.read_text();start=s.index('.method layout()V');a=s[:start];b=s[start:];needle='const/16 v2, 0x30';assert needle in b;b=b.replace(needle,'const/16 v2, 0x28',1);p.write_text(a+b)
p=root/'e/e/a/FullscreenScreen203$Glyph.smali';s=p.read_text();assert '0x3f8f5c29' in s;s=s.replace('0x3f8f5c29    # 1.12f','0x3fac0831    # 1.344f = 1.12 * 48 / 40');p.write_text(s)
p=root/'e/e/a/ModernShorts.smali';p.write_text(p.read_text().replace('1.9.3-dev.3','1.9.3-dev.4'))
