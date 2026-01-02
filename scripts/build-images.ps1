# Build delle immagini Docker dei microservizi.
# Uso:
#   .\scripts\build-images.ps1 -Tag dev

param(
  [string]$Tag = "dev"
)

$ErrorActionPreference = "Stop"

# Root del repository: ...\DSBD_Project
$RepoRoot = Split-Path -Parent $PSScriptRoot

$images = @(
  @{ Name = "user-manager-service";   Image = "dsbd/user-manager-service:$Tag" },
  @{ Name = "data-collector-service"; Image = "dsbd/data-collector-service:$Tag" },
  @{ Name = "alert-system-service";   Image = "dsbd/alert-system-service:$Tag" },
  @{ Name = "alert-notifier-service"; Image = "dsbd/alert-notifier-service:$Tag" }
)

foreach ($item in $images) {
  $svc = $item.Name
  $img = $item.Image
  $context = Join-Path $RepoRoot $svc

  Write-Host "==> Build: $img" -ForegroundColor Cyan
  Write-Host "    Context: $context"

  docker build -t $img $context
  if ($LASTEXITCODE -ne 0) { throw "Build fallita per $svc" }
}

Write-Host "OK: build completata per tutte le immagini." -ForegroundColor Green