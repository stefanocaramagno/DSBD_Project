# Elimina un cluster kind locale.
# Uso:
#   .\scripts\kind\delete-cluster.ps1 -ClusterName dsbd-local

param(
  [string]$ClusterName = "dsbd-local"
)

$ErrorActionPreference = "Stop"

Write-Host "==> Eliminazione cluster kind: $ClusterName" -ForegroundColor Yellow
kind delete cluster --name $ClusterName

Write-Host "OK: cluster eliminato." -ForegroundColor Green