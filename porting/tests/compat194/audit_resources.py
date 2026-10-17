"""Inventory decoded APK locale resources and Java UI literal coverage (not a UI crawler)."""
from pathlib import Path
import argparse,json,re,xml.etree.ElementTree as ET
p=argparse.ArgumentParser();p.add_argument('--decoded-apk',type=Path,required=True);a=p.parse_args();repo=Path(__file__).resolve().parents[3];source=repo/'extensions/extension/src/main/java/e/e/a';res=a.decoded_apk/'res';text=(source/'UiStrings.java').read_text()
pattern=r'TEXT\.put\("((?:\\.|[^"\\])*)",\s*new String\[\]\s*\{\s*"((?:\\.|[^"\\])*)"\s*,\s*"((?:\\.|[^"\\])*)"\s*\}\s*\)'
keys=[json.loads('"'+k+'"') for k,v,w in re.findall(pattern,text)];table=set(keys)
report={'dictionary_keys':len(keys),'duplicate_keys':len(keys)-len(table),'java_helper_families':len(list(source.glob('*.java'))),'resource_layout_variants':len(list(res.glob('layout*/*.xml'))),'locales':{},'base_kana_missing_locale_and_dictionary':{},'direct_ui_call_dictionary_gaps':[]}
def values(qualifier):
 file=res/qualifier/'strings.xml';return {entry.attrib['name']:''.join(entry.itertext()) for entry in ET.parse(file).getroot()} if file.exists() else {}
base=values('values')
for qualifier in ['values','values-en','values-zh-rTW','values-zh-rCN','values-zh-rHK']:
 localized=values(qualifier);report['locales'][qualifier]={'entries':len(localized),'kana_entry_count':sum(bool(re.search('[ぁ-ゖァ-ヺ]',value)) for value in localized.values())}
 if qualifier in ['values-en','values-zh-rTW']:report['base_kana_missing_locale_and_dictionary'][qualifier]=[(key,value) for key,value in base.items() if re.search('[ぁ-ゖァ-ヺ]',value) and key not in localized and value not in table and value.strip('"').replace('\\n','\n') not in table]
for file in source.glob('*.java'):
 if file.name=='UiStrings.java':continue
 for match in re.finditer(r'(?:setText|setTitle|setMessage|setHint|makeText|setPositiveButton|setNegativeButton|setNeutralButton|setContentTitle|setContentText)\(\s*(?:[A-Za-z0-9_.]+\s*,\s*)?"((?:\\.|[^"\\])*)"',file.read_text()):
  value=json.loads('"'+match.group(1)+'"')
  if re.search('[ぁ-ゖァ-ヺ]',value) and value not in table:report['direct_ui_call_dictionary_gaps'].append(value)
print(json.dumps(report,ensure_ascii=False,indent=2))
assert report['dictionary_keys']>0 and not report['duplicate_keys']
assert not any(report['base_kana_missing_locale_and_dictionary'].values()) and not report['direct_ui_call_dictionary_gaps']
