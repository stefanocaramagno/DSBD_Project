#!/usr/bin/env bash
set -euo pipefail

# Carica nel cluster kind le immagini Docker locali (utile in assenza di registry).
# Uso:
#   ./scripts/kind/load-images.sh dev dsbd-local

TAG="${1:-dev}"
CLUSTER_NAME="${2:-dsbd-local}"

declare -a IMAGES=(
  "dsbd/user-manager-service:${TAG}"
  "dsbd/data-collector-service:${TAG}"
  "dsbd/alert-system-service:${TAG}"
  "dsbd/alert-notifier-service:${TAG}"
)

for img in "${IMAGES[@]}"; do
  echo "==> kind load: ${img}"
  kind load docker-image "${img}" --name "${CLUSTER_NAME}"
done

echo "OK: immagini caricate nel cluster kind."