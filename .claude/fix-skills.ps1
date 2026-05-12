# PostToolUse hook: normalize skills-lock.json skillPaths after npx skills add
$inputJson = $null
try {
    $raw = [Console]::In.ReadToEnd()
    $inputJson = $raw | ConvertFrom-Json
} catch { }

if (-not ($inputJson.tool_input.command -like "*npx skills add*")) {
    exit 0
}

$lockPath = "skills-lock.json"
if (!(Test-Path $lockPath)) { exit 0 }

$lock = Get-Content $lockPath -Raw | ConvertFrom-Json

foreach ($skillName in $lock.skills.PSObject.Properties.Name) {
    $skill = $lock.skills.$skillName
    $normalPath = "$skillName/SKILL.md"
    $localFile = ".agents/skills/$normalPath"

    if ($skill.skillPath -ne $normalPath -and (Test-Path $localFile)) {
        $skill.skillPath = $normalPath
        Write-Host "fix-skills: fixed skillPath for '$skillName'"
    }
}

$lock | ConvertTo-Json -Depth 10 | Set-Content $lockPath -Encoding utf8
