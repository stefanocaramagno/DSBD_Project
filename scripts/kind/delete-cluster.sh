#!/usr/bin/env bash
set -euo pipefail

# Elimina un cluster kind locale.
# Uso:
#   ./scripts/kind/delete-cluster.sh dsbd-local

CLUSTER_NAME="${1:-dsbd-local}"
echo "==> Eliminazione cluster kind: ${CLUSTER_NAME}"
kind delete cluster --name "${CLUSTER_NAME}"
echo "OK: cluster eliminato."