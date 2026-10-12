"""Guard removed purchase settings in the actual compiled release payload."""
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
    assert "remove_ad_option" not in keys, "Removed purchase option returned"
    assert "nicoid_patch_version" in keys, "Version information missing"
    for tag, attrs in elements(bundle.read("AndroidManifest.xml")):
        for (_, key), value in attrs.items():
            if key == "name" and isinstance(value, str):
                assert "NicoidInAppBillingActivity" not in value, "Purchase screen registered"
                assert value != "com.android.vending.BILLING", "Billing permission returned"
print("Compiled settings and disabled purchase entry points verified")
