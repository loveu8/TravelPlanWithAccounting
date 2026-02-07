@echo off
setlocal

set "ENV_FILE=%BACKEND_ENV_FILE%"
if "%ENV_FILE%"=="" (
  if exist "%~dp0..\.backendEnv" set "ENV_FILE=%~dp0..\.backendEnv"
  if "%ENV_FILE%"=="" if exist "%~dp0.backendEnv" set "ENV_FILE=%~dp0.backendEnv"
)

if not "%ENV_FILE%"=="" (
  set "BACKEND_ENV_FILE=%ENV_FILE%"
  docker compose --env-file "%ENV_FILE%" down
) else (
  echo [WARN] 找不到 .backendEnv，將只使用系統環境變數停止。
  docker compose down
)

endlocal
