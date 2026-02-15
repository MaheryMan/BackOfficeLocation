# ========================================
# Script de build local pour BackOffice
# ========================================

Write-Host "🚀 Build BackOffice Location" -ForegroundColor Cyan
Write-Host "================================`n" -ForegroundColor Cyan

# Vérifier si Maven est installé
try {
    $mavenVersion = mvn --version
    Write-Host "✓ Maven trouvé" -ForegroundColor Green
} catch {
    Write-Host "✗ Maven n'est pas installé ou pas dans le PATH" -ForegroundColor Red
    Write-Host "Téléchargez Maven depuis https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    exit 1
}

# Se déplacer dans le dossier du projet
Set-Location "BO-location"

Write-Host "`n📦 Nettoyage et compilation..." -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n✓ Build réussi!" -ForegroundColor Green
    Write-Host "`nLe fichier WAR a été créé ici:" -ForegroundColor Cyan
    Write-Host "  → target\backoffice-location.war`n" -ForegroundColor White
    
    Write-Host "Pour déployer sur Tomcat local:" -ForegroundColor Yellow
    Write-Host "  1. Copiez target\backoffice-location.war dans le dossier webapps de Tomcat" -ForegroundColor White
    Write-Host "  2. Démarrez Tomcat" -ForegroundColor White
    Write-Host "  3. Accédez à http://localhost:8080/backoffice-location`n" -ForegroundColor White
} else {
    Write-Host "`n✗ Erreur lors du build" -ForegroundColor Red
    exit 1
}

Set-Location ..
