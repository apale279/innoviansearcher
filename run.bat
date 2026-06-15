@echo off
REM Avvia InnovianSearcher su Windows.
REM Al primo avvio costruisce l'applicazione (serve collegamento a Internet),
REM poi la apre. Le volte successive parte subito.
cd /d "%~dp0"

if not exist "target\InnovianSearcher.jar" (
  echo Primo avvio: costruzione dell'applicazione in corso ^(puo' richiedere qualche minuto^)...
  call mvnw.cmd -q clean package
  if errorlevel 1 (
    echo.
    echo Si e' verificato un errore durante la costruzione.
    pause
    exit /b 1
  )
)

java -jar "target\InnovianSearcher.jar"
pause
