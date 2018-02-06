@echo off
SET CURDIR=%CD%
SET WORKDIR=%~dp0

REM 初期化
SET LOGFILE=%WORKDIR%log\run.log
IF NOT EXIST %WORKDIR%log\NUL ( MKDIR %WORKDIR%log )
ECHO setup >%LOGFILE%

IF EXIST %WORKDIR%execsql\NUL ( RMDIR /S /Q %WORKDIR%execsql )
MKDIR %WORKDIR%execsql

REM 接続先
FOR /F "delims=" %%A IN ( constr.ini ) DO (
	SET CONSTR=%%A
)

REM セットアップ実行SQL取得
ECHO - セットアップ対象SQL取得
ECHO XCOPY /S /E /Y /EXCLUDE:%WORKDIR%exclude.ini %WORKDIR%..\..\setup\main %WORKDIR%\execsql >> %LOGFILE%
XCOPY /S /E /Y /EXCLUDE:%WORKDIR%exclude.ini %WORKDIR%..\..\setup\main %WORKDIR%\execsql >> %LOGFILE% 2>&1
IF NOT %ERRORLEVEL%==0 GOTO OSERROR
ECHO XCOPY /S /E /Y /EXCLUDE:%WORKDIR%exclude.ini %WORKDIR%..\..\setup\env\dev %WORKDIR%\execsql >> %LOGFILE%
XCOPY /S /E /Y /EXCLUDE:%WORKDIR%exclude.ini %WORKDIR%..\..\setup\env\dev %WORKDIR%\execsql >> %LOGFILE% 2>&1
IF NOT %ERRORLEVEL%==0 GOTO OSERROR

CD /D %WORKDIR%..\..\test_master
SET DATA_PUMP_DIR=%CD%

CD /D %WORKDIR%
ECHO - セットアップ実行
ECHO SQLPLUS %CONSTR% @execute.sql create.sql %DATA_PUMP_DIR% >> %LOGFILE%
SQLPLUS %CONSTR% @execute.sql create.sql %DATA_PUMP_DIR% >> %LOGFILE%
FIND /C "ORA-" %LOGFILE% >NUL
IF %ERRORLEVEL%==0 GOTO SQLERROR

ECHO - 正常に終了しました。
GOTO END

:OSERROR
ECHO - OSの処理内でエラーが発生しました。%LOGFILE%を確認して下さい。
GOTO END


:SQLERROR
ECHO - SQL実行でエラーが発生しました。%LOGFILE%を確認して下さい。
GOTO END

:END
CD /D %CURDIR%

ECHO - ボタン押下で終了。
PAUSE >NUL
EXIT /B
