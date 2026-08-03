#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
if [ ! -f local.properties ]; then
  echo "Create local.properties first" >&2
  exit 1
fi
base_url=$(sed -n 's/^BASE_URL=//p' local.properties | tail -n 1)
alerts_url=$(sed -n 's/^ALERTS_BASE_URL=//p' local.properties | tail -n 1)
if [ -z "$base_url" ]; then
  echo "BASE_URL is required" >&2
  exit 1
fi
case "$base_url" in
  https://*) base_config="https:/"'$()'"/${base_url#https://}" ;;
  *) echo "BASE_URL must use HTTPS" >&2; exit 1 ;;
esac
case "$alerts_url" in
  "") alerts_config="" ;;
  https://*) alerts_config="https:/"'$()'"/${alerts_url#https://}" ;;
  *) echo "ALERTS_BASE_URL must use HTTPS" >&2; exit 1 ;;
esac
printf 'BASE_URL = %s\nALERTS_BASE_URL = %s\n' "$base_config" "$alerts_config" > iosApp/Config.xcconfig
cd iosApp
xcodegen generate
