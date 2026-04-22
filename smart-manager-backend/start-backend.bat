@echo off
echo Starting Smart Manager Backend...

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "MAVEN_HOME=e:\apache-maven-3.9.4-bin\apache-maven-3.9.4"
set "PATH=%MAVEN_HOME%\bin;%PATH%"

cd /d "%~dp0"
call mvn spring-boot:run

pause
