@echo off
setlocal
where java >nul 2>nul || (echo Java is not installed or not on PATH. Install JDK 21. & pause & exit /b 1)
where mvn >nul 2>nul || (echo Maven is not installed or not on PATH. Install Maven 3.9+. & pause & exit /b 1)
if "%VMS_DB_USER%"=="" set VMS_DB_USER=root
if "%VMS_DB_PASSWORD%"=="" (
  echo Set your MySQL password first, for example in PowerShell:
  echo   $env:VMS_DB_PASSWORD = "your-password"
  echo Then run mvn exec:java in this folder.
  pause
  exit /b 1
)
mvn clean test exec:java
endlocal
