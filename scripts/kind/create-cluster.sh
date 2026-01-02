#!/usr/bin/env bash
set -euo pipefail

# Crea un cluster kind locale usando la configurazione versionata in k8s/kind/.
# Uso:
#   ./scripts/kind/create-cluster.sh dsbd-local

CLUSTER_NAME="${1:-dsbd-local}"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

echo "==> Creazione cluster kind: ${CLUSTER_NAME}"
kind create cluster --name "${CLUSTER_NAME}" --config "${ROOT_DIR}/k8s/kind/kind-cluster.yaml"

echo "==> Applicazione namespace dsbd"
kubectl apply -f "${ROOT_DIR}/k8s/00-namespace.yaml"

echo "OK: cluster creato e namespace applicato."