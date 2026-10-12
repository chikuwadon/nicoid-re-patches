"""Prevent legacy smali from masking maintained Java implementations at merge time."""
from pathlib import Path

repo = Path(__file__).resolve().parents[2]
canonical = {
    "CacheFolders", "CacheComments", "SearchFilters", "SearchFilterRules",
    "SettingsBackupData", "CommentHistory", "PatchInfo", "ModernShorts",
}
for source in (repo / "porting/helpers-smali/e/e/a").glob("*.smali"):
    assert source.stem.split("$", 1)[0] not in canonical, (
        "Legacy override masks maintained Java source", source.name
    )
print(f"Canonical helper precedence verified: {len(canonical)} class families")
