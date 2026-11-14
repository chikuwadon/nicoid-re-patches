#!/usr/bin/env node
'use strict';

// Issue #110: npm v12.2.2 includes fixed undici@6.29.1 inside its bundle.
// Check the actual published npm package, not only the top-level override.
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');

const root = path.resolve(__dirname, '../..');
const json = relative => JSON.parse(fs.readFileSync(path.join(root, relative), 'utf8'));
const pkg = json('package.json');
const lock = json('package-lock.json');

assert.equal(pkg.overrides.npm, '12.2.2', 'npm override should use the fixed release');
assert.equal(lock.packages[''].overrides, undefined);
assert.equal(lock.packages['node_modules/npm'].version, '12.2.2',
  'Lockfile must contain fixed npm release');
assert.equal(lock.packages['node_modules/npm/node_modules/undici'].version, '6.29.1',
  'Lockfile must contain fixed bundled undici');
assert.match(lock.packages['node_modules/npm'].integrity, /^sha512-/,
  'Published npm package integrity required');

for (const [relative, expected] of [
  ['node_modules/npm/package.json', '12.2.2'],
  ['node_modules/npm/node_modules/undici/package.json', '6.29.1']
]) {
  const file = path.join(root, relative);
  if (!fs.existsSync(file)) continue;
  assert.equal(JSON.parse(fs.readFileSync(file, 'utf8')).version, expected,
    'Installed build tool version mismatch: ' + relative);
}

console.log('PASS: fixed npm 12.2.2, bundled undici 6.29.1, integrity-pinned lockfile');


// GHSA-g3hv-75wg-m249: npm 12.2.2 bundles minimatch 10.2.5. npm
// overrides cannot change bundled dependencies; do not falsely claim this
// advisory is fixed by a top-level override. Fail if another affected copy
// enters the graph, or if the unused npm-publishing plugin becomes active.
const isAffectedMinimatch = version => {
  const parts = (version || '').split('.').map(Number);
  if (parts.length < 3 || parts.slice(0, 3).some(n => !Number.isInteger(n))) return true;
  const [major, minor, patch] = parts;
  return major >= 7 && (major < 11 || (major === 11 && (minor === 0 && patch < 1)));
};
const affectedMinimatchPaths = Object.entries(lock.packages)
  .filter(([name, entry]) => name.endsWith('/minimatch') && isAffectedMinimatch(entry.version))
  .map(([name]) => name).sort();

if (affectedMinimatchPaths.length > 0) {
  assert.deepEqual(affectedMinimatchPaths, ['node_modules/npm/node_modules/minimatch'],
    'Unexpected vulnerable minimatch: review any new direct/transitive exposure');
  assert.equal(lock.packages['node_modules/npm/node_modules/minimatch'].inBundle, true,
    'The accepted exception is limited to the upstream npm CLI bundle');
  assert.ok(lock.packages['node_modules/npm'].bundleDependencies.includes('minimatch'),
    'The minimatch exception must remain an upstream-bundled copy');

  const config = json('.releaserc');
  const configuredPlugins = config.plugins.map(plugin => Array.isArray(plugin) ? plugin[0] : plugin);
  assert.ok(!configuredPlugins.includes('@semantic-release/npm'),
    'npm publishing would change exposure; review this security exception first');
  console.warn('KNOWN UNRESOLVED: npm CLI bundles minimatch below 11.0.1 ' +
    '(GHSA-g3hv-75wg-m249). No safe upstream npm bundle fix available yet.');
} else {
  console.log('PASS: no minimatch affected by GHSA-g3hv-75wg-m249 in the lockfile');
}
