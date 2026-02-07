#!/bin/bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_ENV_FILE="$SCRIPT_DIR/../.backendEnv"
ALT_ENV_FILE="$SCRIPT_DIR/.backendEnv"

if [ -f "$DEFAULT_ENV_FILE" ]; then
  ENV_FILE="$DEFAULT_ENV_FILE"
elif [ -f "$ALT_ENV_FILE" ]; then
  ENV_FILE="$ALT_ENV_FILE"
else
  ENV_FILE="$DEFAULT_ENV_FILE"
fi

export BACKEND_ENV_FILE="$ENV_FILE"
docker compose --env-file "$ENV_FILE" up -d --remove-orphans --force-recreate
