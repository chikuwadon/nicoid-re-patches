#!/usr/bin/env bash
# Rebuild approved dev.1 -> dev.2 -> dev.3, then change version strings only.
set -euo pipefail
[[ "$GITHUB_ACTIONS" == "true" && "$GITHUB_REF" == "refs/heads/main" ]] || {
  echo "::error::v2.1.4 staging requires main Actions" >&2; exit 1;
}
[[ "$NICOID_OFFICIAL_RELEASE_APPROVED" == "APPROVE" || "$NICOID_OFFICIAL_RELEASE_APPROVED" == "VERIFY" ]] || {
  echo "::error::Explicit release approval or verification required" >&2; exit 1;
}
grep -Eq '^version[[:space:]]*=[[:space:]]*2[.]1[.]4[[:space:]]*$' gradle.properties || {
  echo "::error::Expected stable v2.1.4" >&2; exit 1;
}
mpp=patches/build/libs/patches-2.1.4.mpp
test -s "$mpp"
python3 .github/scripts/verify_android_mpp.py "$mpp" 2.1.4
scratch=$(mktemp -d)
trap 'rm -rf "$scratch"' EXIT
cp "$mpp" "$scratch/compiled.mpp"

curl --fail --location --silent --show-error --retry 3 --retry-delay 2 \
  "https://github.com/chikuwadon/nicoid-re-patches/releases/download/v2.1.3/patches-2.1.3.mpp" \
  --output "$scratch/v2.1.3.mpp"
echo "89e3892ad8cf44666e03bc4af6be3632599a320d8817777b4dfdb56f239c932a  $scratch/v2.1.3.mpp" | sha256sum -c -

android_jar="$ANDROID_HOME/platforms/android-35/android.jar"
d8="$ANDROID_HOME/build-tools/35.0.0/d8"
test -s "$android_jar"
test -x "$d8"
python3 porting/revisions/v2.1.0-release/prepare_smali_tools.py
tools="porting/revisions/v2.0.0-dev.1/tools"

python3 porting/revisions/v2.1.4-dev.1/stage_candidate.py \
  --base "$scratch/v2.1.3.mpp" --output "$scratch/dev1.mpp" \
  --tools "$tools" --android-jar "$android_jar" --d8 "$d8"
echo "8723eadc14184d11898a44e2ea072b3284dd3db41310a3a60fd34a0dbebcda89  $scratch/dev1.mpp" | sha256sum -c -

python3 porting/revisions/v2.1.4-dev.2/stage_candidate.py \
  --base "$scratch/dev1.mpp" --output "$scratch/dev2.mpp" \
  --tools "$tools" --android-jar "$android_jar" --d8 "$d8"
echo "f23b6c6c58a01e660268925fef3c0980c32d422883cfe9a83c551b8714337482  $scratch/dev2.mpp" | sha256sum -c -

python3 porting/revisions/v2.1.4-dev.3/stage_candidate.py \
  --base "$scratch/dev2.mpp" --output "$scratch/dev3.mpp" \
  --tools "$tools" --android-jar "$android_jar" --d8 "$d8"
echo "37d04536d8bbc4ee94f1a2909ea58b50a3f30c441929373df19e73f0a48f6e76  $scratch/dev3.mpp" | sha256sum -c -

python3 porting/revisions/v2.1.4-release/stage_candidate.py \
  --compiled "$scratch/compiled.mpp" --reviewed "$scratch/dev3.mpp" \
  --output "$mpp" --tools "$tools"
python3 .github/scripts/verify_android_mpp.py "$mpp" 2.1.4
sha256sum "$mpp"
