"""Check actual packaged entry points and reject duplicate DEX method definitions."""
from pathlib import Path
import re
import sys

root = Path(sys.argv[1])
for file in root.rglob('*.smali'):
    signatures = re.findall(r'^\.method[^\n]* ([^ ]+\([^\n]+)$', file.read_text(), re.M)
    assert len(signatures) == len(set(signatures)), ('Duplicate method', file)

def method(owner, name):
    text = (root / 'e/e/a' / (owner + '.smali')).read_text()
    found = re.search(r'^\.method[^\n]* ' + re.escape(name) + r'\([^\n]*\n.*?^\.end method', text, re.M | re.S)
    assert found, (owner, name)
    return found.group(0)

for owner, name, target in [
    ('ModernRanking', 'populateCategories', 'Ranking190;->populate'),
    ('ModernRanking', 'load', 'RankingFetch190;->parseOfficial'),
    ('CreatorPage', 'following', 'Following190;->attach'),
    ('CreatorPage', 'open', 'Ranking190;->open'),
    ('ListActions', 'adjustHistoryMenu', 'MylistFollow190;->menu'),
    ('VideoDetails', 'bind', 'Nicoad190;->bind'),
    ('VideoDetails', 'addLike', 'Nicoad190;->attach'),
]:
    assert target in method(owner, name), ('Disconnected entry point', owner, target)

for owner in ['Ranking190', 'RankingFetch190', 'RankingRules190', 'Following190', 'MylistFollow190', 'Nicoad190', 'UiGlyph190']:
    assert (root / 'e/e/a' / (owner + '.smali')).exists(), ('Missing packaged helper', owner)
assert 'v1.9.1-dev.1 @chikuwadon' in (root / 'e/e/a/ModernShorts.smali').read_text()
print('Development feature entry points and unique DEX method definitions verified')

for owner in ["ModernRanking", "ModernSearch"]:
    assert method(owner,"load").count("Ljava/net/HttpURLConnection;->getInputStream()") == 1, ("Patcher requires exactly one page-cache insertion point",owner)

assert 'RankingPages190;->rank' in method('ModernRanking','load')
assert 'Ranking190;->startupSettings' in method('StartupScreen','settings')
assert 'Ranking190;->startupIntent' in method('StartupScreen','open')
assert 'Ranking190;->startupIntent' in method('StartupRouter','onCreate')

assert 'Landroid/view/ViewGroup$MarginLayoutParams;->topMargin:I' in method('DialogInputs','pad')
assert 'DialogColors;->ink' in method('PanelUi','ink')
assert 'ThemeChoice;->spinner' in method('UiDialogs','inputs')

assert 'Ranking190;->managerMenu' in method('FollowFeed','menu')
assert 'SearchRange;->alignInput' in method('DialogInputs','pad')
assert 'Following190;->roundAvatar' in method('CreatorPage','channelRow')
