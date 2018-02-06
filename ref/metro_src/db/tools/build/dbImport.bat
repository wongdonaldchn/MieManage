@echo off
ECHO ******************************************************************
ECHO dbImport
ECHO DBを構築する
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
SET FILE_LOG=%DIR_ROOT%\log\%STRING_RUNDATE%\dbImport_%STRING_RUNDATE%-%STRING_RUNTIME%.txt
SET FILE_GENERATELOG=%DIR_ROOT%\log\%STRING_RUNDATE%\generate-ddl_%STRING_RUNDATE%-%STRING_RUNTIME%.log
SET FILE_SEQUENCELOG=%DIR_ROOT%\log\%STRING_RUNDATE%\sequence_%STRING_RUNDATE%-%STRING_RUNTIME%.log
SET FILE_PROCEDURELOG=%DIR_ROOT%\log\%STRING_RUNDATE%\sequence_%STRING_RUNDATE%-%STRING_RUNTIME%.log

REM ディレクトリ構成
SET DIR_GENERATE=generate-ddl
SET DIR_SEQUENCE=sequence
SET DIR_PROCEDURE=procedure

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
	ECHO DDL対象ディレクトリ
	SET /P DIR_DDL=
	IF '!DIR_DDL!'=='' ( GOTO :SETTARGET )
	IF NOT EXIST !DIR_DDL!\NUL (
		ECHO !DIR_DDL!は存在しません
		GOTO :SETTARGET
	)

) ELSE (
	SET DIR_DDL=%1
	IF NOT EXIST !DIR_DDL!\NUL (
		ECHO [!DATE!-!TIME!][ERROR]!DIR_DDL!は存在しません >>%FILE_LOG%
		SET BOOL_ERR=1
		GOTO :END
	)

)


ECHO [!DATE!-!TIME!][INFO]対象ディレクトリ:%DIR_DDL% >>%FILE_LOG%


REM ******************************************************************
REM * メイン処理
REM ******************************************************************


REM ==============================================
REM * 自動生成対象オブジェクト作成(execute generate ddl)
REM ==============================================

ECHO EDMより自動生成したオブジェクト作成
ECHO [!DATE!-!TIME!][INFO]EDMより自動生成したオブジェクト作成 >>%FILE_LOG%

REM // ディレクトリ存在チェック //
IF NOT EXIST %DIR_DDL%\%DIR_GENERATE%\NUL (
	ECHO [!DATE!-!TIME!][EXCEPTION]自動生成用ディレクトリがありません >>%FILE_LOG%
	SET /A INT_EXCEPTION=!INT_EXCEPTION!+1
	ECHO [!DATE!-!TIME!][INFO]処理をスキップします >>%FILE_LOG%
	GOTO :GENERATEEND
)

REM // 初期化 //
IF EXIST %DIR_DDL%\%DIR_GENERATE%\%FILE_EXECUTE% (
	DEL /S /Q %DIR_DDL%\%DIR_GENERATE%\%FILE_EXECUTE% >NUL
)
IF EXIST %DIR_DDL%\%DIR_GENERATE%\%FILE_LIST% (
	DEL /S /Q %DIR_DDL%\%DIR_GENERATE%\%FILE_LIST% >NUL
)

REM // [@ + ファイル名 ]をリストファイルに書込  //
FOR /F "usebackq delims=" %%A IN (`DIR /B /A:-D %DIR_DDL%\%DIR_GENERATE%`) DO (
	ECHO @%%A >> %DIR_DDL%\%DIR_GENERATE%\%FILE_LIST%
)

REM // 実行 //
COPY %DIR_EXCECUTETAMPLATE%\%FILE_EXECUTE% %DIR_DDL%\%DIR_GENERATE% >NUL
PUSHD %DIR_DDL%\%DIR_GENERATE%
sqlplus -L %CONSTR% @%FILE_EXECUTE% @%FILE_LIST% > %FILE_GENERATELOG%
POPD
DEL /S /Q %DIR_DDL%\%DIR_GENERATE%\%FILE_EXECUTE% >NUL
DEL /S /Q %DIR_DDL%\%DIR_GENERATE%\%FILE_LIST% >NUL

REM // ログ確認 //
ECHO [!DATE!-!TIME!][INFO]テーブル作成ログ確認 >>%FILE_LOG%
FINDSTR "%STRING_ORA%" %FILE_GENERATELOG%
IF '%ERRORLEVEL%'=='0' (
	ECHO [!DATE!-!TIME!][ERROR]EDMより自動生成したオブジェクト作成でエラーが発生しています >>%FILE_LOG%
	SET BOOL_ERR=1
)

:GENERATEEND

REM ==============================================
REM * シーケンス作成(sequence)
REM ==============================================

ECHO シーケンス作成
ECHO [!DATE!-!TIME!][INFO]シーケンス作成 >>%FILE_LOG%

REM // ディレクトリ存在チェック //
IF NOT EXIST %DIR_DDL%\%DIR_SEQUENCE%\NUL (
	ECHO [!DATE!-!TIME!][EXCEPTION]シーケンス用ディレクトリがありません >>%FILE_LOG%
	SET /A INT_EXCEPTION=!INT_EXCEPTION!+1
	ECHO [!DATE!-!TIME!][INFO]処理をスキップします >>%FILE_LOG%
	GOTO :SEQUENCEEND
)

REM // 初期化 //
IF EXIST %DIR_DDL%\%DIR_SEQUENCE%\%FILE_EXECUTE% (
	DEL /S /Q %DIR_DDL%\%DIR_SEQUENCE%\%FILE_EXECUTE% >NUL
)
IF EXIST %DIR_DDL%\%DIR_SEQUENCE%\%FILE_LIST% (
	DEL /S /Q %DIR_DDL%\%DIR_SEQUENCE%\%FILE_LIST% >NUL
)

REM // [@ + ファイル名 ]をリストファイルに書込  //
FOR /F "usebackq delims=" %%A IN (`DIR /B /A:-D %DIR_DDL%\%DIR_SEQUENCE%`) DO (
	ECHO @%%A >> %DIR_DDL%\%DIR_SEQUENCE%\%FILE_LIST%
)

REM // 実行 //
COPY %DIR_EXCECUTETAMPLATE%\%FILE_EXECUTE% %DIR_DDL%\%DIR_SEQUENCE% >NUL
PUSHD %DIR_DDL%\%DIR_SEQUENCE%
sqlplus -L %CONSTR% @%FILE_EXECUTE% @%FILE_LIST% > %FILE_SEQUENCELOG%
POPD
DEL /S /Q %DIR_DDL%\%DIR_SEQUENCE%\%FILE_EXECUTE% >NUL
DEL /S /Q %DIR_DDL%\%DIR_SEQUENCE%\%FILE_LIST% >NUL

REM // ログ確認 //
ECHO [!DATE!-!TIME!][INFO]シーケンス作成ログ確認 >>%FILE_LOG%
FINDSTR "%STRING_ORA%" %FILE_SEQUENCELOG%
IF '%ERRORLEVEL%'=='0' (
	ECHO [!DATE!-!TIME!][ERROR]シーケンス作成でエラーが発生しています >>%FILE_LOG%
	SET BOOL_ERR=1
)

:SEQUENCEEND

REM ==============================================
REM * プロシージャ作成(procedure)
REM ==============================================

ECHO プロシージャ作成
ECHO [!DATE!-!TIME!][INFO]プロシージャ作成 >>%FILE_LOG%

REM // ディレクトリ存在チェック //
IF NOT EXIST %DIR_DDL%\%DIR_PROCEDURE%\NUL (
	ECHO [!DATE!-!TIME!][EXCEPTION]シーケンス用ディレクトリがありません >>%FILE_LOG%
	SET /A INT_EXCEPTION=!INT_EXCEPTION!+1
	ECHO [!DATE!-!TIME!][INFO]処理をスキップします >>%FILE_LOG%
	GOTO :PROCEDUREEND
)

REM // 初期化 //
IF EXIST %DIR_DDL%\%DIR_PROCEDURE%\%FILE_EXECUTE% (
	DEL /S /Q %DIR_DDL%\%DIR_PROCEDURE%\%FILE_EXECUTE% >NUL
)
IF EXIST %DIR_DDL%\%DIR_PROCEDURE%\%FILE_LIST% (
	DEL /S /Q %DIR_DDL%\%DIR_PROCEDURE%\%FILE_LIST% >NUL
)

REM // [@ + ファイル名 ]をリストファイルに書込  //
FOR /F "usebackq delims=" %%A IN (`DIR /B /A:-D %DIR_DDL%\%DIR_PROCEDURE%`) DO (
	ECHO @%%A >> %DIR_DDL%\%DIR_PROCEDURE%\%FILE_LIST%
)

REM // 実行 //
COPY %DIR_EXCECUTETAMPLATE%\%FILE_EXECUTE% %DIR_DDL%\%DIR_PROCEDURE% >NUL
PUSHD %DIR_DDL%\%DIR_PROCEDURE%
sqlplus -L %CONSTR% @%FILE_EXECUTE% @%FILE_LIST% > %FILE_PROCEDURELOG%
POPD
DEL /S /Q %DIR_DDL%\%DIR_PROCEDURE%\%FILE_EXECUTE% >NUL
DEL /S /Q %DIR_DDL%\%DIR_PROCEDURE%\%FILE_LIST% >NUL

REM // ログ確認 //
ECHO [!DATE!-!TIME!][INFO]プロシージャ作成ログ確認 >>%FILE_LOG%
FINDSTR "%STRING_ORA%" %FILE_PROCEDURELOG%
IF '%ERRORLEVEL%'=='0' (
	ECHO [!DATE!-!TIME!][ERROR]プロシージャ作成でエラーが発生しています >>%FILE_LOG%
	SET BOOL_ERR=1
)

:PROCEDUREEND


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