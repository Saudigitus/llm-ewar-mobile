param(
    [string]$ModelUrl,
    [string]$Sha256
)

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$propertiesPath = Join-Path $repoRoot 'local.properties'
if (Test-Path -LiteralPath $propertiesPath) {
    foreach ($line in Get-Content -LiteralPath $propertiesPath) {
        if ($line -match '^\s*MODEL_URL\s*=(.*)$' -and -not $ModelUrl) { $ModelUrl = $Matches[1].Trim() }
        if ($line -match '^\s*MODEL_SHA256\s*=(.*)$' -and -not $Sha256) { $Sha256 = $Matches[1].Trim() }
    }
}
if ($ModelUrl -notmatch '^https://') { throw 'MODEL_URL must be an HTTPS URL.' }
if ($Sha256 -notmatch '^[a-fA-F0-9]{64}$') { throw 'MODEL_SHA256 must contain 64 hexadecimal characters.' }

$modelsDir = Join-Path $repoRoot 'androidApp/src/main/assets/models'
New-Item -ItemType Directory -Path $modelsDir -Force | Out-Null
$target = Join-Path $modelsDir 'climasaude.litertlm'
$temporary = Join-Path $modelsDir 'climasaude.litertlm.part'
try {
    Invoke-WebRequest -Uri $ModelUrl -OutFile $temporary
    $actualHash = (Get-FileHash -LiteralPath $temporary -Algorithm SHA256).Hash
    if ($actualHash -ine $Sha256) { throw "Model checksum mismatch: $actualHash" }
    Move-Item -LiteralPath $temporary -Destination $target -Force
    Write-Output "Model ready: $target"
} finally {
    if (Test-Path -LiteralPath $temporary) { Remove-Item -LiteralPath $temporary }
}
