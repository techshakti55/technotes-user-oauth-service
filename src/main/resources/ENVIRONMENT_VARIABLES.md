# TechNotes User OAuth Service - Environment Variables Guide

## 1. Purpose

TechNotes User OAuth Service में database credentials और owner-account credentials source code में hard-code नहीं किए जाते।

Sensitive values को:

- `application.yml` में hard-code नहीं करना है
- GitHub पर commit नहीं करना है
- Flyway migration में नहीं रखना है
- documentation में actual passwords नहीं लिखने हैं

Application environment variables से इन values को read करती है.

---

## 2. Environment Variables Used by the Service

| Variable | Purpose | Example |
|---|---|---|
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/technotes_auth` |
| `DB_USERNAME` | PostgreSQL application user | `technotes_auth_user` |
| `DB_PASSWORD` | PostgreSQL password | Never document actual value |
| `OWNER_EMAIL` | Initial owner account email | `techshakti55@gmail.com` |
| `OWNER_DISPLAY_NAME` | Initial owner display name | `techShakti` |
| `OWNER_PASSWORD` | Initial owner login password | Never document actual value |

---

# 3. Temporary Environment Variables

PowerShell में `$env:` से variable set करने पर वह current terminal/session के लिए available होता है.

Example:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/technotes_auth"
$env:DB_USERNAME="technotes_auth_user"
$env:DB_PASSWORD="YOUR_PRIVATE_DB_PASSWORD"

$env:OWNER_EMAIL="techshakti55@gmail.com"
$env:OWNER_DISPLAY_NAME="techShakti"
$env:OWNER_PASSWORD="YOUR_PRIVATE_OWNER_PASSWORD"
```

### Important

`YOUR_PRIVATE_DB_PASSWORD` और `YOUR_PRIVATE_OWNER_PASSWORD` को actual local passwords से replace करें.

Actual passwords को:

- documentation में paste न करें
- Git commit न करें
- screenshots में expose न करें
- chat/messages में share न करें

Temporary variables current PowerShell process के बंद होने के बाद available नहीं रह सकते.

---

# 4. Check Temporary Environment Variables

Non-sensitive variables:

```powershell
Write-Host $env:DB_URL
Write-Host $env:DB_USERNAME
Write-Host $env:OWNER_EMAIL
Write-Host $env:OWNER_DISPLAY_NAME
```

Passwords को directly print नहीं करना है.

Safe verification:

```powershell
Write-Host "DB_PASSWORD configured:" (-not [string]::IsNullOrWhiteSpace($env:DB_PASSWORD))
Write-Host "OWNER_PASSWORD configured:" (-not [string]::IsNullOrWhiteSpace($env:OWNER_PASSWORD))
```

Expected:

```text
DB_PASSWORD configured: True
OWNER_PASSWORD configured: True
```

---

# 5. Permanent Windows User Environment Variables

Windows User Environment Variables को PowerShell से permanently save किया जा सकता है.

Non-sensitive values:

```powershell
[Environment]::SetEnvironmentVariable(
    "DB_URL",
    "jdbc:postgresql://localhost:5432/technotes_auth",
    "User"
)

[Environment]::SetEnvironmentVariable(
    "DB_USERNAME",
    "technotes_auth_user",
    "User"
)

[Environment]::SetEnvironmentVariable(
    "OWNER_EMAIL",
    "techshakti55@gmail.com",
    "User"
)

[Environment]::SetEnvironmentVariable(
    "OWNER_DISPLAY_NAME",
    "techShakti",
    "User"
)
```

Passwords local machine पर set करने के लिए:

```powershell
[Environment]::SetEnvironmentVariable(
    "DB_PASSWORD",
    "YOUR_PRIVATE_DB_PASSWORD",
    "User"
)

[Environment]::SetEnvironmentVariable(
    "OWNER_PASSWORD",
    "YOUR_PRIVATE_OWNER_PASSWORD",
    "User"
)
```

Actual password documentation में कभी न लिखें.

---

# 6. Verify Permanently Saved Variables

Permanent User-level values check करने के लिए:

```powershell
[Environment]::GetEnvironmentVariable("DB_URL", "User")
[Environment]::GetEnvironmentVariable("DB_USERNAME", "User")
[Environment]::GetEnvironmentVariable("OWNER_EMAIL", "User")
[Environment]::GetEnvironmentVariable("OWNER_DISPLAY_NAME", "User")
```

Password को print करने के बजाय सिर्फ configured/not-configured check करें:

```powershell
Write-Host "Permanent DB_PASSWORD configured:" `
(-not [string]::IsNullOrWhiteSpace(
    [Environment]::GetEnvironmentVariable("DB_PASSWORD", "User")
))

Write-Host "Permanent OWNER_PASSWORD configured:" `
(-not [string]::IsNullOrWhiteSpace(
    [Environment]::GetEnvironmentVariable("OWNER_PASSWORD", "User")
))
```

Expected:

```text
Permanent DB_PASSWORD configured: True
Permanent OWNER_PASSWORD configured: True
```

---

# 7. Why `$env:VARIABLE` Can Still Be Blank

Example:

```powershell
[Environment]::SetEnvironmentVariable(
    "DB_USERNAME",
    "technotes_auth_user",
    "User"
)

Write-Host $env:DB_USERNAME
```

The second command may still show blank in the current PowerShell window.

Reason:

`SetEnvironmentVariable(..., "User")` updates the persistent Windows User environment.

It does not automatically refresh the environment already loaded by the currently running PowerShell process.

---

# 8. After Setting Permanent Variables

After permanent variables are configured:

1. Close the current terminal.
2. Open a new PowerShell terminal.
3. If using IntelliJ's integrated terminal, restart IntelliJ if necessary.
4. Verify the variables again.

Example:

```powershell
Write-Host $env:DB_URL
Write-Host $env:DB_USERNAME
Write-Host $env:OWNER_EMAIL
Write-Host $env:OWNER_DISPLAY_NAME
```

Password verification:

```powershell
Write-Host "DB_PASSWORD configured:" `
(-not [string]::IsNullOrWhiteSpace($env:DB_PASSWORD))

Write-Host "OWNER_PASSWORD configured:" `
(-not [string]::IsNullOrWhiteSpace($env:OWNER_PASSWORD))
```

---

# 9. Remove a Permanent Environment Variable

If a variable needs to be removed:

```powershell
[Environment]::SetEnvironmentVariable(
    "VARIABLE_NAME",
    $null,
    "User"
)
```

Example:

```powershell
[Environment]::SetEnvironmentVariable(
    "OWNER_PASSWORD",
    $null,
    "User"
)
```

Open a new terminal after removing it.

---

# 10. Update a Permanent Variable

To change an existing variable, run `SetEnvironmentVariable` again with the new value.

Example:

```powershell
[Environment]::SetEnvironmentVariable(
    "DB_USERNAME",
    "technotes_auth_user",
    "User"
)
```

The new value replaces the previous User-level value.

Restart the terminal/IDE before expecting the new value in `$env:`.

---

# 11. application.yml Integration

The service reads database configuration using environment placeholders.

Example:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/technotes_auth}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

Meaning:

- `${DB_URL:...}` uses `DB_URL`; if missing, the configured local URL is the fallback.
- `${DB_USERNAME}` requires `DB_USERNAME`.
- `${DB_PASSWORD}` requires `DB_PASSWORD`.

Database passwords must not be hard-coded into `application.yml`.

---

# 12. Owner Provisioning Variables

The first-live owner provisioning uses:

```text
OWNER_EMAIL
OWNER_DISPLAY_NAME
OWNER_PASSWORD
```

Current local owner configuration:

```text
OWNER_EMAIL=techshakti55@gmail.com
OWNER_DISPLAY_NAME=techShakti
```

`OWNER_PASSWORD` must remain private.

The application must store only the encoded password in PostgreSQL.

---

# 13. Troubleshooting

## Error

```text
password authentication failed for user "${DB_USERNAME}"
```

### Meaning

Spring received the literal `${DB_USERNAME}` instead of the actual environment variable value.

### Check

```powershell
Write-Host $env:DB_USERNAME
```

If blank, the variable is not available in the current process.

Check permanent storage:

```powershell
[Environment]::GetEnvironmentVariable("DB_USERNAME", "User")
```

If the permanent value exists but `$env:DB_USERNAME` is blank:

- close the terminal
- open a new terminal
- restart IntelliJ when using its integrated terminal

---

# 14. Security Rules

Never commit:

```text
Real database passwords
Real owner passwords
Private keys
JWT signing private keys
Secrets
Access tokens
Session cookies
```

Safe to document:

```text
Variable names
Non-secret local URLs
Database name
Application username
Owner email
Owner display name
Example placeholders
Setup and verification commands
```

Use placeholders such as:

```text
YOUR_PRIVATE_DB_PASSWORD
YOUR_PRIVATE_OWNER_PASSWORD
```

instead of real credentials.

---

# 15. Quick Reference

## Temporary - Current PowerShell Session

```powershell
$env:VARIABLE_NAME="VALUE"
```

## Permanent - Windows User

```powershell
[Environment]::SetEnvironmentVariable(
    "VARIABLE_NAME",
    "VALUE",
    "User"
)
```

## Read Current Session

```powershell
$env:VARIABLE_NAME
```

## Read Permanent User Value

```powershell
[Environment]::GetEnvironmentVariable(
    "VARIABLE_NAME",
    "User"
)
```

## Remove Permanent Variable

```powershell
[Environment]::SetEnvironmentVariable(
    "VARIABLE_NAME",
    $null,
    "User"
)
```

---

## Project Rule

For TechNotes User OAuth Service:

**Configuration may reference secrets, but source code, migrations, documentation and Git history must never contain the actual secret values.**
