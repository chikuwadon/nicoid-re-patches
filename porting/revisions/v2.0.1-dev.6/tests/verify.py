"""Protect native tab creation arity while retaining an eight-page LRU cache."""
from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
repo=root.parents[2]
src=(root/"build_mpp.py").read_text()
smali=(repo/"porting/revisions/v1.10.1/helpers/e/e/a/MultiTabs227$Host.smali").read_text()
m=re.search(r"(?ms)^\\.method activate\\(Ljava/lang/String;\\)V.*?^\\.end method",smali)
assert m, "Native tab activate method is missing"
native=m.group(0)
constant="    const/4 v12, 0x3"
array="    new-array v15, v12"
anchor="    :goto_19a\\n    iget-object v5, v0, Le/e/a/MultiTabs227$Host;->pages:Ljava/util/LinkedHashMap;"
assert native.count(constant)==1
assert native.count(array)==2
assert native.count(anchor.replace("\\n","\\n"))==1
# Both reflective argument arrays must still use the initial three-item value.
assert native.index(constant)<native.index(array)<native.index(":goto_19a")
assert native.rindex(array)<native.index(":goto_19a")
assert 'native_arity = "    const/4 v12, 0x3"' in src
assert 'tab_host.count(array_use) != 2' in src
assert '"    const/16 v12, 0x8\\n"' in src
assert 'tab_host.replace(lru_anchor' in src
assert 'tab_host.replace(cache_limit' not in src
assert "nicoid_capture_keep_saved_scroll" in src
assert 'VERSION = "2.0.1-dev.6"' in src
assert 'v2.0.1-dev.5/java' in src
workflow=(repo/".github/workflows/private-build.yml").read_text()
assert 'nicoid-re-v2.0.1-dev.6-mpp' in workflow
assert 'nicoid-re-v2.0.1-dev.6-source' in workflow
print("PASS: three-argument tab creation, separate 8-page LRU and dev.6 artifact naming")
