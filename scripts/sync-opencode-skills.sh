#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
source_dir="$repo_root/.agents/skills"
opencode_dir="$repo_root/.opencode"
commands_dir="$opencode_dir/commands"
skills_link="$opencode_dir/skills"
expected_link="../.agents/skills"

if [[ -L "$skills_link" ]]; then
    if [[ "$(readlink "$skills_link")" != "$expected_link" ]]; then
        rm "$skills_link"
        ln -s "$expected_link" "$skills_link"
    fi
elif [[ ! -e "$skills_link" ]]; then
    ln -s "$expected_link" "$skills_link"
else
    printf 'Refusing to replace non-symlink: %s\n' "$skills_link" >&2
    exit 1
fi

mkdir -p "$commands_dir"

synced=0
for skill_file in "$source_dir"/*/SKILL.md; do
    [[ -f "$skill_file" ]] || continue
    skill_name="${skill_file%/SKILL.md}"
    skill_name="${skill_name##*/}"
    command_file="$commands_dir/$skill_name.md"

    cp "$skill_file" "$command_file"
    printf '\n\n## User Request\n\n%s\n' '$ARGUMENTS' >> "$command_file"
    ((synced += 1))
done

for command_file in "$commands_dir"/*.md; do
    [[ -e "$command_file" ]] || continue
    skill_name="${command_file##*/}"
    skill_name="${skill_name%.md}"
    [[ -f "$source_dir/$skill_name/SKILL.md" ]] || rm "$command_file"
done

printf 'Synced %s skills to %s\n' "$synced" "$commands_dir"
