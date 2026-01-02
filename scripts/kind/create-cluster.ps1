# Crea un cluster kind locale usando la configurazione versionata in k8s/kind/.
# Uso:
#   .\scripts\kind\create-cluster.ps1 -ClusterName dsbd-local

param(
  [string]$ClusterName = "dsbd-local"
)

$ErrorActionPreference = "Stop"

$RepoRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$KindConfig = Join-Path $RepoRoot "k8s\kind\kind-cluster.yaml"
$NamespaceFile = Join-Path $RepoRoot "k8s\00-namespace.yaml"

Write-Host "==> Creazione cluster kind: $ClusterName" -ForegroundColor Cyan
kind create cluster --name $ClusterName --config $KindConfig

Write-Host "==> Applicazione namespace dsbd" -ForegroundColor Cyan
kubectl apply -f $NamespaceFile

Write-Host "OK: cluster creato e namespace applicato." -ForegroundColor Green