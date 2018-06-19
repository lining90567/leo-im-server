@REM app launcher script
@REM
@REM Environment:
@REM JAVA_HOME - location of a JDK home dir (optional if java on path)
@setlocal enabledelayedexpansion

@echo off

cd %~dp0
cd ../

if "%JAVA_OPS%" == "" set JAVA_OPS=-Dfile.encoding=utf-8 -Dio.netty.noUnsafe=true -server -Xmx128m -Xms128m -Xss256k
java %JAVA_OPS% -Dconf.home=%cd%\conf\ -jar %cd%\lib\leo-im-starter-1.0.jar