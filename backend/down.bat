@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "DEFAULT_ENV_FILE=%SCRIPT_DIR%..\.backendEnv"
set "ALT_ENV_FILE=%SCRIPT_DIR%.backendEnv"

if exist "%DEFAULT_ENV_FILE%" (
  set "ENV_FILE=%DEFAULT_ENV_FILE%"
) else if exist "%ALT_ENV_FILE%" (
  set "ENV_FILE=%ALT_ENV_FILE%"
) else (
  set "ENV_FILE=%DEFAULT_ENV_FILE%"
)

set "BACKEND_ENV_FILE=%ENV_FILE%"
docker compose --env-file "%ENV_FILE%" down
endlocal
