"""Prevent generated UI from bypassing the shared theme context and palette."""
from pathlib import Path
import json,re,xml.etree.ElementTree as ET
repo=Path(__file__).resolve().parents[2];source=repo/'extensions/extension/src/main/java/e/e/a';ownership=json.loads((repo/'porting/helper-ownership.json').read_text());checked=0
for name,entry in ownership.items():
 if entry['owner']!='java' or name=='UiDialogs':continue
 text=(source/(name+'.java')).read_text()
 assert not re.search(r'new (?:android\.app\.)?AlertDialog\.Builder\(',text),('Use the shared dialog factory',name)
 for call in re.finditer(r'new PopupMenu\(([^,]+),',text):assert 'UiTheme191.context(' in call.group(1),('Use a themed popup context',name)
 checked+=1
factory=(source/'UiDialogs.java').read_text();assert 'new AlertDialog.Builder(UiTheme191.context(c))' in factory
assert 'return UiTheme191.context(c)' in (source/'PlaybackSession.java').read_text()
assert 'nicoid-managed-input' in (source/'DialogInputs.java').read_text()
assert 'nicoid-managed-input' in (repo/'porting/helpers-smali/e/e/a/DialogInputs.smali').read_text()
overlay=json.loads((repo/'porting/theme-191/app-theme-overlays.json').read_text())
for name in ['MyThemeDark','MyThemeLight','MyThemeAMOLED']:
 items=overlay['values/'+name];assert 'android:alertDialogTheme' in items and 'colorControlActivated' in items
 assert 'colorPrimary' not in items,'Keep the existing AppBar palette'
colors={v.attrib['name']:v.text for v in ET.parse(repo/'porting/theme-191/nicoid_palette.xml').getroot()}
accent=colors['nicoid_accent_dark'];assert accent.lower()=='#ff52cca3'
for style in ET.parse(repo/'porting/theme-191/nicoid_dialogs.xml').getroot():
 if not style.attrib['name'].startswith('NicoidDialog'):continue
 names={x.attrib['name'] for x in style};assert {'android:colorBackground','android:textColorPrimary','android:colorControlActivated','android:colorAccent'}<=names
assert colors['nicoid_tag_item_dark']=='#ff24262b' and colors['nicoid_tag_item_light']=='#fff0f0f0'
print('Shared dialog factories, explicit mode overlays and protected custom inputs verified:',checked,'source families')
