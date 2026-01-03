#!/usr/bin/env bash
# Scopo:
#   Dev loop per kind: build immagini -> load nel cluster kind -> rollout restart dei Deployment.
#   Evita il caso tipico in cui si rebuilda con tag fisso (es. :dev) ma Kubernetes non fa rollout
#   perché lo spec del Deployment non è cambiato.
#
# Uso:
#   ./scripts/kind/dev-loop.sh --tag dev --cluster dsbd-local --namespace dsbd
#   ./scripts/kind/dev-loop.sh              # usa default
#   ./scripts/kind/dev-loop.sh --apply-stack
#
# Note:
#   - Richiede che esistano già gli script:
#       scripts/build-images.sh
#       scripts/kind/load-images.sh
#   - Non modifica i manifest: opera con rollout restart per rendere effettive le nuove immagini.

set -euo pipefail

TAG="dev"
CLUSTER_NAME="dsbd-local"
NAMESPACE="dsbd"
APPLY_STACK="false"

usage() {
  cat <<EOF
Usage: $(basename "$0") [options]

Options:
  --tag <tag>           Tag immagini Docker (default: dev)
  --cluster <name>      Nome cluster kind (default: dsbd-local)
  --namespace <ns>      Namespace Kubernetes (default: dsbd)
  --apply-stack         Esegue anche: kubectl apply -k k8s/stack
  -h, --help            Mostra help

Esempi:
  $(basename "$0") --tag dev --cluster dsbd-local --namespace dsbd
  $(basename "$0") --apply-stack
EOF
}

require_cmd() {
  local cmd="$1"
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "Errore: comando richiesto non trovato nel PATH: $cmd" >&2
    exit 1
  fi
}

# Parsing argomenti
while [[ $# -gt 0 ]]; do
  case "$1" in
    --tag)
      TAG="${2:-}"; shift 2 ;;
    --cluster)
      CLUSTER_NAME="${2:-}"; shift 2 ;;
    --namespace)
      NAMESPACE="${2:-}"; shift 2 ;;
    --apply-stack)
      APPLY_STACK="true"; shift 1 ;;
    -h|--help)
      usage; exit 0 ;;
    *)
      echo "Argomento non riconosciuto: $1" >&2
      usage
      exit 1 ;;
  esac
done

require_cmd docker
require_cmd kubectl
require_cmd kind

# Repo root: scripts/kind -> repo root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

BUILD_SCRIPT="$REPO_ROOT/scripts/build-images.sh"
LOAD_SCRIPT="$REPO_ROOT/scripts/kind/load-images.sh"
STACK_DIR="$REPO_ROOT/k8s/stack"

if [[ ! -f "$BUILD_SCRIPT" ]]; then
  echo "Errore: file non trovato: $BUILD_SCRIPT" >&2
  exit 1
fi

if [[ ! -f "$LOAD_SCRIPT" ]]; then
  echo "Errore: file non trovato: $LOAD_SCRIPT" >&2
  exit 1
fi

echo "==> (1/3) Build immagini (Tag=$TAG)"
bash "$BUILD_SCRIPT" --tag "$TAG"

echo "==> (2/3) Load immagini nel cluster kind (ClusterName=$CLUSTER_NAME, Tag=$TAG)"
bash "$LOAD_SCRIPT" --tag "$TAG" --cluster "$CLUSTER_NAME"

if [[ "$APPLY_STACK" == "true" ]]; then
  if [[ ! -d "$STACK_DIR" ]]; then
    echo "Errore: directory stack non trovata: $STACK_DIR" >&2
    exit 1
  fi

  echo "==> Applico lo stack Kubernetes (kustomize): $STACK_DIR"
  kubectl apply -k "$STACK_DIR"
fi

# Deployment che dipendono da immagini locali dsbd/* buildate in host e caricate in kind
DEPLOYMENTS=(
  "user-manager-service"
  "data-collector-service"
  "alert-system-service"
  "alert-notifier-service"
)

echo "==> (3/3) Rollout restart dei Deployment per rendere effettive le nuove immagini"
for dep in "${DEPLOYMENTS[@]}"; do
  echo "    - rollout restart deploy/$dep (ns=$NAMESPACE)"
  kubectl -n "$NAMESPACE" rollout restart "deploy/$dep"
done

echo "==> Attendo stabilizzazione rollout (timeout 180s per deployment)"
for dep in "${DEPLOYMENTS[@]}"; do
  kubectl -n "$NAMESPACE" rollout status "deploy/$dep" --timeout=180s
done

echo "OK: dev-loop completato (build -> load -> rollout)."