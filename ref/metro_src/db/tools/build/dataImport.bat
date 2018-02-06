@echo off
ECHO ******************************************************************
ECHO dataImport
ECHO データを投入する
ECHO ******************************************************************

setlocal enabledelayedexpansion

REM ******************************************************************
REM * 初期処理
REM ******************************************************************


REM ==============================================
REM * 環境設定
REM ==============================================

REM 接続先
FOR /F "delims=" %%A IN ( constr.ini ) DO (
	SET CONSTR=%%A
)

REM ==============================================
REM * 定数定義
REM ==============================================

REM ツールルート
SET DIR_ROOT=%~dp0

REM 実行時刻
SET TMP_TIME2=%TIME: =0%
SET STRING_RUNTIME=%TMP_TIME2:~0,2%%TMP_TIME2:~3,2%%TMP_TIME2:~6,2%
SET STRING_RUNDATE=%DATE:~-10,4%%DATE:~-5,2%%DATE:~-2,2%

REM ログファイル
SET FILE_LOG=%DIR_ROOT%\log\%STRING_RUNDATE%\dataImport_%STRING_RUNDATE%-%STRING_RUNTIME%.txt
SET FILE_DATALOG=%DIR_ROOT%\log\%STRING_RUNDATE%\data_%STRING_RUNDATE%-%STRING_RUNTIME%.log

REM 実行クエリ配置先
SET DIR_EXCECUTETAMPLATE=%DIR_ROOT%\scripts

REM 実行クエリ
SET FILE_EXECUTE=execute.sql

REM 実行用リストファイル
SET FILE_LIST=list.sql

REM SQL*Plusエラー判断文字列
SET STRING_ORA=ORA-


REM ==============================================
REM * 変数初期化
REM ==============================================

REM エラーフラグ
SET BOOL_ERR=0


REM ==============================================
REM * 環境初期化
REM ==============================================

IF NOT EXIST %DIR_ROOT%\log\NUL ( MKDIR %DIR_ROOT%\log )
IF NOT EXIST %DIR_ROOT%\log\%STRING_RUNDATE%\NUL ( MKDIR %DIR_ROOT%\log\%STRING_RUNDATE% )

ECHO [!DATE!-!TIME!][INFO]開始 >>%FILE_LOG%
ECHO [!DATE!-!TIME!][INFO]実行環境:%CONSTR% >>%FILE_LOG%


ECHO 投入先
ECHO %CONSTR%


:SETTARGET
REM ==============================================
REM * 開始インターフェース
REM ==============================================

IF '%1'=='' (
	ECHO.
	ECHO 対象ディレクトリ
	SET /P DIR_DML=
	IF '!DIR_DML!'=='' ( GOTO :SETTARGET )
	IF NOT EXIST !DIR_DML!\NUL (
		ECHO !DIR_DML!は存在しません
		GOTO :SETTARGET
	)

) ELSE (
	SET DIR_DML=%1
	IF NOT EXIST !DIR_DML!\NUL (
		ECHO [!DATE!-!TIME!][ERROR]!DIR_DML!は存在しません >>%FILE_LOG%
		SET BOOL_ERR=1
		GOTO :END
	)

)

ECHO [!DATE!-!TIME!][INFO]対象ディレクトリ:%DIR_DML% >>%FILE_LOG%


REM ******************************************************************
REM * メイン処理
REM ******************************************************************


REM ==============================================
REM * データ投入(insert)
REM ==============================================

ECHO データ投入
ECHO [!DATE!-!TIME!][INFO]データ投入 >>%FILE_LOG%

REM // ディレクトリ存在チェック //
IF NOT EXIST %DIR_DML%\NUL (
	ECHO [!DATE!-!TIME!][EXCEPTION]データ用ディレクトリがありません >>%FILE_LOG%
	SET /A INT_EXCEPTION=!INT_EXCEPTION!+1
	ECHO [!DATE!-!TIME!][INFO]データ投入をスキップします >>%FILE_LOG%
	GOTO :DATAEND
)

REM // 初期化 //
IF EXIST %DIR_DML%\%FILE_EXECUTE% (
	DEL /S /Q %DIR_DML%\%FILE_EXECUTE% >NUL
)
IF EXIST %DIR_DML%\%FILE_LIST% (
	DEL /S /Q %DIR_DML%\%FILE_LIST% >NUL
)

REM // [@ + ファイル名 ]をリストファイルに書込  //
FOR /F "usebackq delims=" %%A IN (`DIR /B /A:-D %DIR_DML%`) DO (
	ECHO @%%A >> %DIR_DML%\%FILE_LIST%
)

REM // 実行 //
COPY %DIR_EXCECUTETAMPLATE%\%FILE_EXECUTE% %DIR_DML% >NUL
PUSHD %DIR_DML%
sqlplus -L %CONSTR% @%FILE_EXECUTE% @%FILE_LIST% > %FILE_DATALOG%
POPD
DEL /S /Q %DIR_DML%\%FILE_EXECUTE% >NUL
DEL /S /Q %DIR_DML%\%FILE_LIST% >NUL

REM // ログ確認 //
ECHO [!DATE!-!TIME!][INFO]データ投入ログ確認 >>%FILE_LOG%
FINDSTR "%STRING_ORA%" %FILE_DATALOG%
IF '%ERRORLEVEL%'=='0' (
	ECHO [!DATE!-!TIME!][ERROR]データ投入でエラーが発生しています >>%FILE_LOG%
	SET BOOL_ERR=1
)

:DATAEND



:END
REM ******************************************************************
REM *終了処理
REM ******************************************************************


REM ==============================================
REM * 終了インターフェース
REM ==============================================

ECHO.
IF '%BOOL_ERR%'=='0' (
	ECHO [!DATE!-!TIME!][COMPLETE]処理が完了しました >>%FILE_LOG%
) ELSE (
	ECHO [!DATE!-!TIME!][ERROR]エラーが発生しました >>%FILE_LOG%

)


REM ==============================================
REM * 終了
REM ==============================================

IF '%1'=='' (
	endlocal
	pause
) ELSE (
	exit /b %BOOL_ERR%
)


GOTO :EOF


:EOF