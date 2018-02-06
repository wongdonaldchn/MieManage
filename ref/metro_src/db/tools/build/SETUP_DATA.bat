@echo off
ECHO ******************************************************************
ECHO SETUP_DATA
ECHO ******************************************************************


SET DIR_ROOT=%~dp0

REM �ڑ���
FOR /F "delims=" %%A IN ( constr.ini ) DO (
	SET CONSTR=%%A
)

REM ���s����
SET TMP_TIME2=%TIME: =0%
SET STRING_RUNTIME=%TMP_TIME2:~0,2%%TMP_TIME2:~3,2%%TMP_TIME2:~6,2%
SET STRING_RUNDATE=%DATE:~-10,4%%DATE:~-5,2%%DATE:~-2,2%

REM ��������
SET DIR_LOG=%DIR_ROOT%\log\%STRING_RUNDATE%
IF NOT EXIST %DIR_ROOT%\log\NUL ( MKDIR %DIR_ROOT%\log )
IF NOT EXIST %DIR_LOG%\NUL ( MKDIR %DIR_LOG% )


REM ���C��

if "%1" == "-all" (
	call :drop
	call :table
) else if "%1" == "-d" (
	call :drop
) else if "%1" == "-table" (
	call :table
)

goto end



:drop
REM ******************************************************************

call %DIR_ROOT%\dbClear.bat


REM ******************************************************************
goto :EOF



:table
REM ******************************************************************

REM ==============================================
REM * DDL���s
REM ==============================================

call %DIR_ROOT%\dbImport.bat %DIR_ROOT%..\..\ddl\dev
if not "%ERRORLEVEL%"=="0" (
	echo ���s���܂���
	goto :ERR
)

REM ==============================================
REM * �f�[�^����(insert)
REM ==============================================

REM ���ʕ��i
call %DIR_ROOT%\dataImport.bat %DIR_ROOT%..\..\data\main
if not "%ERRORLEVEL%"=="0" (
	SET BOOL_ERR=1
	goto :END
)

REM ������
call %DIR_ROOT%\dataImport.bat %DIR_ROOT%..\..\data\env\dev
if not "%ERRORLEVEL%"=="0" (
	SET BOOL_ERR=1
	goto :END
)

REM ******************************************************************
goto :EOF



:end

exit /b 0

:ERR

exit /b 1


:EOF