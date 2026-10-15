"""Check the installed native adapter order and the packaged following UI path."""
from pathlib import Path
import re,sys
helpers,native=map(Path,sys.argv[1:])
activity=(native/'com/sauzask/nicoid/NicoidFavUserActivity.smali').read_text()
methods=re.findall(r'^\.method[^\n]*\n.*?^\.end method',activity,re.M|re.S)
hooks=[m for m in methods if 'CreatorPage;->following(' in m]
assert len(hooks)==1
method=hooks[0]
assert method.index('ListView;->setAdapter(')<method.index('CreatorPage;->following(')
assert 'ListView;->setAdapter(' not in method.split('CreatorPage;->following(',1)[1], 'Native adapter must not overwrite styled rows'
def text(name):return (helpers/'e/e/a'/f'{name}.smali').read_text()
assert 'Following190$UserRows;-><init>' in text('Following190')
assert 'Following190;->style(' in text('Following190$UserRows')
assert 'Following190;->roundAvatar(' in text('Following190')
assert any('Landroid/graphics/Outline;->setOval(' in p.read_text() for p in (helpers/'e/e/a').glob('Following190$*.smali'))
assert 'Following190;->sizeRow(' in text('CreatorPage')
assert 'ViewPropertyAnimator;->translationX(' in text('Following190$Pager')
assert 'Following190$Gestures;->isHorizontal(' in text('TabViewport110')
assert 'Following190$Gestures;->shouldSwitch(' in text('TabViewport110')
print('Following native adapter order, circular rendering path and animated swipe verified')

assert ".super Le/e/a/TabViewport110;" in text("Following190$Pager")
assert ".super Le/e/a/TabViewport110;" in text("Ui110$Swipe")
