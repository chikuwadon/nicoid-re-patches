"""Guard essential settings and intentional removals in the compiled payload."""
import sys
import zipfile

from VerifyManifest import elements


with zipfile.ZipFile(sys.argv[1]) as bundle:
    keys = {
        value
        for _, attrs in elements(bundle.read("res/xml/pref.xml"))
        for (_, key), value in attrs.items()
        if key == "key"
    }
    required={"setting_whole","intent_type","main_rotation","player_rotation","play_mode","display_mode","cache_cate","cache_all_delete","cache_size","cache_offline","cache_dir","nologin","login","videolist_tap","player","quality_mode","mobile_low_quality","player_no_wait","player_statusbar_hide","player_sleep_play","comment","comment_fps","comment_alpha","comment_shadow","comment_hide","share_ng_level","comment_cast_min","player_lang","app_lang","about","license","app_translation","info","nicoid_patch_version"}
    assert required<=keys, "Essential settings missing: "+str(sorted(required-keys))
    assert not keys.intersection({"labs","labs_cashe_speed_up"}), "Removed experimental controls returned"
    assert "remove_ad_option" not in keys, "Removed purchase option returned"
    assert "nicoid_patch_version" in keys, "Version information missing"
    for tag, attrs in elements(bundle.read("AndroidManifest.xml")):
        for (_, key), value in attrs.items():
            if key == "name" and isinstance(value, str):
                assert "NicoidInAppBillingActivity" not in value, "Purchase screen registered"
                assert value != "com.android.vending.BILLING", "Billing permission returned"
print("Compiled settings and disabled purchase entry points verified")
