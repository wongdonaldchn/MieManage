@echo off
SET WORKDIR=%~dp0
lsnrctl RELOAD
net stop OracleJobSchedulerOPAL
net stop OracleServiceIOPAL
sc delete OracleJobSchedulerOPAL
sc delete OracleServiceOPAL
cd /D C:\
rmdir /S /Q C:\oraclepe\bin
rmdir /S /Q C:\oraclepe\log
del /S /Q C:\oraclepe\OraclePEforOPAL.bat
unzip %WORKDIR%db\oraclePE.zip
cd oraclepe
cmd /c OraclePEforOPAL.bat
rmdir /S /Q bin
del OraclePEforOPAL.bat
exit
