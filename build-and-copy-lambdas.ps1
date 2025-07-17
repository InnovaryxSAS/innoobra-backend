# Compila todos los módulos y copia los JARs a infra/dev/functions
$modules = @('activity','apudetail','attribute','chapter','company','project','role','user','common')

foreach ($module in $modules) {
    $srcPath = "lambda-java/$module"
    $targetJar = "target/$module-1.0-SNAPSHOT.jar"
    $destPath = "infra/dev/functions/$module/$module-1.0-SNAPSHOT.jar"
    
    Write-Host "Compilando $module..."
    Push-Location $srcPath
    mvn clean package
    Pop-Location
    
    if (Test-Path "$srcPath/$targetJar") {
        if (!(Test-Path "infra/dev/functions/$module")) {
            New-Item -ItemType Directory -Path "infra/dev/functions/$module" | Out-Null
        }
        Copy-Item "$srcPath/$targetJar" "$destPath" -Force
        Write-Host "Copiado: $destPath"
    } else {
        Write-Host "No se encontró el JAR para $module"
    }
}
Write-Host "Proceso terminado."
