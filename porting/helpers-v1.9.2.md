# Canonical helper implementation for v1.9.2

`helpers-v1.9.2.zip` contains the complete editable smali implementation from the
user-verified v1.9.2-dev.8 bundle (SHA-256
`509baea099050c35d847aa2e591364fb2c5b4895563b72636d77af24492955fa`).
It is assembled as the final override layer by the normal Gradle release build.
`@NICOID_PATCH_VERSION@` is replaced with the current build version. Native method
changes are in `patches/src/main/resources/nicoid/method-delta.dex`; their latest
source changes and regression checks are under `porting/revisions/`.

This layer preserves the device-tested implementation while its Java sources
are recovered incrementally. `helper-ownership.json` explicitly identifies
shadowed Java families and hashes both their Java reference and canonical smali.
Update the canonical layer and ownership hashes together; when a family is
ported fully to Java, remove it from the snapshot and switch its owner to Java.
Do not make behavioral fixes only in a shadowed Java reference.

The archive uses fixed entry timestamps and sorted paths. To edit it, extract
it, update the smali source, and rebuild the archive in sorted path order.
The existing DEX argument, routing, settings, queue and maintenance checks also
run against the merged implementation before packaging.
