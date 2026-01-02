# Carica nel cluster kind le immagini Docker locali (utile in assenza di registry).
# Uso:
#   .\scripts\kind\load-images.ps1 -Tag dev -ClusterName dsbd-local

param(
  [string]$Tag = "dev",
  [string]$ClusterName = "dsbd-local"
)

$ErrorActionPreference = "Stop"

$images = @(
  "dsbd/user-manager-service:$Tag",
  "dsbd/data-collector-service:$Tag",
  "dsbd/alert-system-service:$Tag",
  "dsbd/alert-notifier-service:$Tag"
)

foreach ($img in $images) {
  Write-Host "==> kind load: $img" -ForegroundColor Cyan
  kind load docker-image $img --name $ClusterName
  if ($LASTEXITCODE -ne 0) { throw "Caricamento fallito per $img" }
}

Write-Host "OK: immagini caricate nel cluster kind." -ForegroundColor Green