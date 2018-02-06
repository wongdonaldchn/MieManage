@echo off

pushd ..\..\

echo mvn clean
call mvn clean
set code=%ERRORLEVEL%
if not '%code%'=='0' ( goto :ERROR )

echo mvn -P development generate-resources
call mvn -P development generate-resources
set code=%ERRORLEVEL%
if not '%code%'=='0' ( goto :ERROR )

echo mvn -P prod generate-resources
call mvn -P prod generate-resources
set code=%ERRORLEVEL%
if not '%code%'=='0' ( goto :ERROR )

popd
exit /b 0

goto :EOF


:ERROR

popd
exit /b %code%

:EOF