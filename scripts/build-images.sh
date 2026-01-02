#!/usr/bin/env bash
set -euo pipefail

# Build delle immagini Docker dei microservizi.
# Uso:
#   ./scripts/build-images.sh dev

TAG="${1:-dev}"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

declare -a SERVICES=(
  "user-manager-service"
  "data-collector-service"
  "alert-system-service"
  "alert-notifier-service"
)

for svc in "${SERVICES[@]}"; do
  img="dsbd/${svc}:${TAG}"
  echo "==> Build: ${img}"
  docker build -t "${img}" "${ROOT_DIR}/${svc}"
done

echo "OK: build completata per tutte le immagini."