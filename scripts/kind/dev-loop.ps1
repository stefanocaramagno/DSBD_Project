<#
Scopo:
  Dev loop per kind: build immagini -> load nel cluster kind -> rollout restart dei Deployment.
  Questo evita il caso tipico in cui si rebuilda con tag fisso (es. :dev) ma Kubernetes non fa rollout
  perché lo spec del Deployment non è cambiato.

Uso:
  .\scripts\kind\dev-loop.ps1 -Tag dev -ClusterName dsbd-local -Namespace dsbd
  .\scripts\kind\dev-loop.ps1   # usa default

Note:
  - Richiede che esistano già gli script:
      scripts/build-images.ps1
      scripts/kind/load-images.ps1
  - Non modifica i manifest: opera con rollout restart per rendere effettive le nuove immagini.
#>

[CmdletBinding()]
param(
    [Parameter(Mandatory = $false)]
    [string]$Tag = "dev",

    [Parameter(Mandatory = $false)]
    [string]$ClusterName = "dsbd-local",

    [Parameter(Mandatory = $false)]
    [string]$Namespace = "dsbd",

    [Parameter(Mandatory = $false)]
    [switch]$ApplyStack
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Assert-Command([string]$cmd) {
    if (-not (Get-Command $cmd -ErrorAction SilentlyContinue)) {
        throw "Comando richiesto non trovato nel PATH: $cmd"
    }
}

Assert-Command "docker"
Assert-Command "kubectl"
Assert-Command "kind"

# Root repository: risaliamo da scripts/kind -> repo root
$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$BuildScript = Join-Path $RepoRoot "scripts\build-images.ps1"
$LoadScript  = Join-Path $RepoRoot "scripts\kind\load-images.ps1"
$StackPath   = Join-Path $RepoRoot "k8s\stack"

if (-not (Test-Path $BuildScript)) { throw "File non trovato: $BuildScript" }
if (-not (Test-Path $LoadScript))  { throw "File non trovato: $LoadScript" }

Write-Host "==> (1/3) Build immagini (Tag=$Tag)" -ForegroundColor Cyan
& $BuildScript -Tag $Tag

Write-Host "==> (2/3) Load immagini nel cluster kind (ClusterName=$ClusterName, Tag=$Tag)" -ForegroundColor Cyan
& $LoadScript -Tag $Tag -ClusterName $ClusterName

if ($ApplyStack) {
    if (-not (Test-Path $StackPath)) { throw "Directory stack non trovata: $StackPath" }

    Write-Host "==> Applico lo stack Kubernetes (kustomize): $StackPath" -ForegroundColor Cyan
    kubectl apply -k $StackPath | Out-Host
}

# Deployment che dipendono da immagini locali dsbd/* buildate in host e caricate in kind
$DeploymentsToRestart = @(
    "user-manager-service",
    "data-collector-service",
    "alert-system-service",
    "alert-notifier-service"
)

Write-Host "==> (3/3) Rollout restart dei Deployment per rendere effettive le nuove immagini" -ForegroundColor Cyan
foreach ($dep in $DeploymentsToRestart) {
    Write-Host "    - rollout restart deploy/$dep (ns=$Namespace)" -ForegroundColor Yellow
    kubectl -n $Namespace rollout restart "deploy/$dep" | Out-Host
}

Write-Host "==> Attendo stabilizzazione rollout (timeout 180s per deployment)" -ForegroundColor Cyan
foreach ($dep in $DeploymentsToRestart) {
    kubectl -n $Namespace rollout status "deploy/$dep" --timeout=180s | Out-Host
}

Write-Host "OK: dev-loop completato (build -> load -> rollout)." -ForegroundColor Green