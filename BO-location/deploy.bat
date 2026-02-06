@echo off
setlocal enabledelayedexpansion

REM Configuration
set "PROJECT_ROOT=%CD%"
set "WEBAPP_NAME=BO-location"
set "TOMCAT_HOME=D:\Mahery\SERVLET\tomcat-10.1.2"
set "TOMCAT_WEBAPPS=%TOMCAT_HOME%\webapps"

REM Création des répertoires temporaires
set "BUILD_DIR=%PROJECT_ROOT%\build"
set "CLASSES_DIR=%BUILD_DIR%\WEB-INF\classes"

echo === Nettoyage des anciens builds ===
if exist "%BUILD_DIR%" (
    rmdir /s /q "%BUILD_DIR%"
)
mkdir "%CLASSES_DIR%"

echo === Compilation des fichiers Java ===
REM Création du CLASSPATH
set "CLASSPATH="
for %%f in ("%PROJECT_ROOT%\lib\*.jar") do (
    if defined CLASSPATH (
        set "CLASSPATH=!CLASSPATH!;%%f"
    ) else (
        set "CLASSPATH=%%f"
    )
)

REM Compilation des fichiers Java
set "JAVA_FILES="
for /r "%PROJECT_ROOT%\src\main\java" %%f in (*.java) do (
    set "JAVA_FILES=!JAVA_FILES! %%f"
)

if defined JAVA_FILES (
    javac -parameters -cp "!CLASSPATH!" -d "%CLASSES_DIR%" !JAVA_FILES!
    if errorlevel 1 (
        echo Erreur lors de la compilation
        exit /b 1
    )
)

echo === Copie des ressources Web ===
REM Copie du contenu webapp
if exist "%PROJECT_ROOT%\src\main\webapp" (
    xcopy /s /e /y /q "%PROJECT_ROOT%\src\main\webapp\*" "%BUILD_DIR%\" >nul
)

REM Copie des bibliothèques
mkdir "%BUILD_DIR%\WEB-INF\lib" 2>nul
if exist "%PROJECT_ROOT%\lib\*.jar" (
    copy /y "%PROJECT_ROOT%\lib\*.jar" "%BUILD_DIR%\WEB-INF\lib\" >nul 2>&1
)

REM Suppression de servlet-api.jar du WAR (fourni par Tomcat)
if exist "%BUILD_DIR%\WEB-INF\lib\servlet-api.jar" (
    del "%BUILD_DIR%\WEB-INF\lib\servlet-api.jar"
)

echo === Création du WAR ===
cd /d "%BUILD_DIR%"
jar -cvf "%WEBAPP_NAME%.war" * >nul

echo === Déploiement vers Tomcat ===
REM Suppression de l'ancienne version si elle existe
if exist "%TOMCAT_WEBAPPS%\%WEBAPP_NAME%.war" (
    del "%TOMCAT_WEBAPPS%\%WEBAPP_NAME%.war"
)
if exist "%TOMCAT_WEBAPPS%\%WEBAPP_NAME%" (
    rmdir /s /q "%TOMCAT_WEBAPPS%\%WEBAPP_NAME%"
)

REM Copie du nouveau WAR
copy /y "%WEBAPP_NAME%.war" "%TOMCAT_WEBAPPS%\" >nul

echo === Déploiement terminé ===
echo.
echo Accédez à votre application sur: http://localhost:8888/%WEBAPP_NAME%
echo.

cd /d "%PROJECT_ROOT%"
endlocal
