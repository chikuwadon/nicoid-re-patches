# Build-tool minimatch advisory (2026-11-15)

## Status

**Unresolved upstream; not an application-runtime vulnerability.** The Dependabot
alert for GHSA-g3hv-75wg-m249 (CVE-2026-118764, Moderate) must remain open
until the vulnerable dependency is removed from the resolved lockfile.

The current dependency chain is:

```
semantic-release@25.0.9
  -> @semantic-release/npm@13.1.5 (installed but not configured as a plugin)
     -> npm@12.2.2
        -> bundled minimatch@10.2.5 (affected: >=7.0.0 <11.0.1)
```

The GitHub release configuration (`.releaserc`) explicitly omits
`@semantic-release/npm`, so the npm-registry publishing plugin is not executed.
This does not remove the bundled npm CLI from the release tool dependencies;
the npm CLI is still present and can be invoked by build tooling.

## Why this is not overridden

The first patched minimatch release is 11.0.1. npm 12.2.2 (the latest
published npm CLI checked on 2026-11-15) bundles minimatch 10.2.5, and
its dependencies request minimatch 10.x. npm's root `overrides` cannot
replace dependencies embedded in a published npm `bundleDependencies`
archive. Blindly forcing minimatch 11.x or downgrading npm would create
unverified incompatibilities and could undo the earlier undici security fix.

## Containment and follow-up

- Only the upstream npm CLI's bundled minimatch copy is accepted by
  `.github/scripts/verify-release-npm.cjs`. Additional affected copies,
  or a change that activates `@semantic-release/npm`, fail the check.
- No untrusted glob patterns are intentionally passed to minimatch in
  the project's release configuration. This limits the known exposure
  but does not prove the bundled package safe.
- Keep the Dependabot alert **open**; this is a scoped risk control, not
  a patch or dismissal.
- On an upstream npm release that bundles minimatch >=11.0.1, update
  `overrides.npm` and regenerate `package-lock.json` with the supported
  npm toolchain, then run `npm ci --ignore-scripts`, the npm verifier,
  the release CLI smoke test, and existing build checks.
- Alternatively, evaluate an upstream semantic-release version that
  makes the unused npm-publishing plugin optional, preserving the
  project's explicit release plugin configuration.

Upstream references:
- https://github.com/advisories/GHSA-g3hv-75wg-m249
- https://github.com/npm/cli/releases
- https://github.com/semantic-release/semantic-release/issues/1260
