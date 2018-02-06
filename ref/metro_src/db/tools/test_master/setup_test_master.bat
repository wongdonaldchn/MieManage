@echo off
SET CURDIR=%CD%
SET WORKDIR=%~dp0

REM 初期化
SET LOGFILE=%WORKDIR%log\run.log
IF NOT EXIST %WORKDIR%log\NUL ( MKDIR %WORKDIR%log )
ECHO setup_test_master >%LOGFILE%

REM 接続先
FOR /F "delims=" %%A IN ( constr.ini ) DO (
	SET CONSTR=%%A
)

REM データ所有スキーマのエクスポート
expdp %CONSTR% directory=DIR_DATA_PUMP schemas=opal dumpfile=test_master.dmp reuse_dumpfiles=YES

REM マスタデータ復旧用スキーマへのインポート
impdp %CONSTR% directory=DIR_DATA_PUMP remap_schema=opal:test_master dumpfile=test_master.dmp table_exists_action=REPLACE

