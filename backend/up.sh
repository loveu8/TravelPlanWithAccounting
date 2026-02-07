#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${BACKEND_ENV_FILE:-}"

if [ -z "$ENV_FILE" ]; then
  if [ -f "$SCRIPT_DIR/../.backendEnv" ]; then
    ENV_FILE="$SCRIPT_DIR/../.backendEnv"
  elif [ -f "$SCRIPT_DIR/.backendEnv" ]; then
    ENV_FILE="$SCRIPT_DIR/.backendEnv"
  fi
fi

if [ -n "$ENV_FILE" ]; then
  export BACKEND_ENV_FILE="$ENV_FILE"
  docker compose --env-file "$ENV_FILE" up -d --remove-orphans --force-recreate
else
  echo "[WARN] 找不到 .backendEnv，將只使用系統環境變數啟動。"
  docker compose up -d --remove-orphans --force-recreate
fi
