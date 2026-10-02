const fs = require('node:fs');
const path = require('node:path');
module.exports.generateNotes = async (options, context) => {
  if (context.nextRelease.version === '1.3.0') {
    return fs.readFileSync(path.join(context.cwd, '.github/release-notes/v1.3.0.md'), 'utf8');
  }
  return require('@semantic-release/release-notes-generator').generateNotes(options, context);
};
