@echo off
ECHO ******************************************************************
ECHO SETUP_DATA
ECHO ******************************************************************


SET DIR_ROOT=%~dp0

REM 接続先
FOR /F "delims=" %%A IN ( constr.ini ) DO (
	SET CONSTR=%%A
)

REM 実行時刻
SET TMP_TIME2=%TIME: =0%
SET STRING_RUNTIME=%TMP_TIME2:~0,2%%TMP_TIME2:~3,2%%TMP_TIME2:~6,2%
SET STRING_RUNDATE=%DATE:~-10,4%%DATE:~-5,2%%DATE:~-2,2%

REM 環境初期化
SET DIR_LOG=%DIR_ROOT%\log\%STRING_RUNDATE%
IF NOT EXIST %DIR_ROOT%\log\NUL ( MKDIR %DIR_ROOT%\log )
IF NOT EXIST %DIR_LOG%\NUL ( MKDIR %DIR_LOG% )


REM メイン

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
REM * DDL実行
REM ==============================================

call %DIR_ROOT%\dbImport.bat %DIR_ROOT%..\..\ddl\dev
if not "%ERRORLEVEL%"=="0" (
	echo 失敗しました
	goto :ERR
)

REM ==============================================
REM * データ投入(insert)
REM ==============================================

REM 共通部品
call %DIR_ROOT%\dataImport.bat %DIR_ROOT%..\..\data\main
if not "%ERRORLEVEL%"=="0" (
	SET BOOL_ERR=1
	goto :END
)

REM 環境差分
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