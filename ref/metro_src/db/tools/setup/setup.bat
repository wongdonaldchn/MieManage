@echo off
SET CURDIR=%CD%
SET WORKDIR=%~dp0

REM ������
SET LOGFILE=%WORKDIR%log\run.log
IF NOT EXIST %WORKDIR%log\NUL ( MKDIR %WORKDIR%log )
ECHO setup >%LOGFILE%

IF EXIST %WORKDIR%execsql\NUL ( RMDIR /S /Q %WORKDIR%execsql )
MKDIR %WORKDIR%execsql

REM �ڑ���
FOR /F "delims=" %%A IN ( constr.ini ) DO (
	SET CONSTR=%%A
)

REM �Z�b�g�A�b�v���sSQL�擾
ECHO - �Z�b�g�A�b�v�Ώ�SQL�擾
ECHO XCOPY /S /E /Y /EXCLUDE:%WORKDIR%exclude.ini %WORKDIR%..\..\setup\main %WORKDIR%\execsql >> %LOGFILE%
XCOPY /S /E /Y /EXCLUDE:%WORKDIR%exclude.ini %WORKDIR%..\..\setup\main %WORKDIR%\execsql >> %LOGFILE% 2>&1
IF NOT %ERRORLEVEL%==0 GOTO OSERROR
ECHO XCOPY /S /E /Y /EXCLUDE:%WORKDIR%exclude.ini %WORKDIR%..\..\setup\env\dev %WORKDIR%\execsql >> %LOGFILE%
XCOPY /S /E /Y /EXCLUDE:%WORKDIR%exclude.ini %WORKDIR%..\..\setup\env\dev %WORKDIR%\execsql >> %LOGFILE% 2>&1
IF NOT %ERRORLEVEL%==0 GOTO OSERROR

CD /D %WORKDIR%..\..\test_master
SET DATA_PUMP_DIR=%CD%

CD /D %WORKDIR%
ECHO - �Z�b�g�A�b�v���s
ECHO SQLPLUS %CONSTR% @execute.sql create.sql %DATA_PUMP_DIR% >> %LOGFILE%
SQLPLUS %CONSTR% @execute.sql create.sql %DATA_PUMP_DIR% >> %LOGFILE%
FIND /C "ORA-" %LOGFILE% >NUL
IF %ERRORLEVEL%==0 GOTO SQLERROR

ECHO - ����ɏI�����܂����B
GOTO END

:OSERROR
ECHO - OS�̏������ŃG���[���������܂����B%LOGFILE%���m�F���ĉ������B
GOTO END


:SQLERROR
ECHO - SQL���s�ŃG���[���������܂����B%LOGFILE%���m�F���ĉ������B
GOTO END

:END
CD /D %CURDIR%

ECHO - �{�^�������ŏI���B
PAUSE >NUL
EXIT /B
