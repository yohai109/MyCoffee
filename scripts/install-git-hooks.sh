#!/usr/bin/env sh
set -eu

root=$(git rev-parse --show-toplevel)
git -C "$root" config core.hooksPath .githooks
chmod +x "$root/.githooks/pre-commit"
printf '%s\n' "Installed MyCoffee Git hooks."
