@echo off
ECHO ******************************************************************
ECHO dbClear
ECHO ******************************************************************

setlocal enabledelayedexpansion

REM ******************************************************************
REM * �萔��`
REM ******************************************************************

REM �c�[�����[�g
SET DIR_ROOT=%~dp0


REM ���s����
SET TMP_TIME2=%TIME: =0%
SET STRING_RUNTIME=%TMP_TIME2:~0,2%%TMP_TIME2:~3,2%%TMP_TIME2:~6,2%
SET STRING_RUNDATE=%DATE:~-10,4%%DATE:~-5,2%%DATE:~-2,2%

REM ���O�t�@�C��
SET FILE_LOG=%DIR_ROOT%\log\%STRING_RUNDATE%\dbClear_%STRING_RUNDATE%-%STRING_RUNTIME%.txt

IF NOT EXIST %DIR_ROOT%\log\NUL ( MKDIR %DIR_ROOT%\log )
IF NOT EXIST %DIR_ROOT%\log\%STRING_RUNDATE%\NUL ( MKDIR %DIR_ROOT%\log\%STRING RUNDATE% )

REM �ڑ���
FOR /F "delims=" %%A IN ( constr.ini ) DO (
	SET CONSTR=%%A
)

REM ******************************************************************
REM * ���s
REM ******************************************************************

if exist execSql\NUL rmdir /s /q execSql
if exist execSql\NUL goto :ERR
mkdir execSql

sqlplus %CONSTR% @./scripts/execute.sql @clear.sql
sqlplus %CONSTR% @./scripts/execute.sql @check.sql > %FILE_LOG%

goto :END


:ERR

exit /b 1

goto :END



:END


exit /b 0

:EOF