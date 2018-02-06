@echo off
SET CURDIR=%CD%
SET WORKDIR=%~dp0

REM ������
SET LOGFILE=%WORKDIR%log\run.log
IF NOT EXIST %WORKDIR%log\NUL ( MKDIR %WORKDIR%log )
ECHO setup_test_master >%LOGFILE%

REM �ڑ���
FOR /F "delims=" %%A IN ( constr.ini ) DO (
	SET CONSTR=%%A
)

REM �f�[�^���L�X�L�[�}�̃G�N�X�|�[�g
expdp %CONSTR% directory=DIR_DATA_PUMP schemas=opal dumpfile=test_master.dmp reuse_dumpfiles=YES

REM �}�X�^�f�[�^�����p�X�L�[�}�ւ̃C���|�[�g
impdp %CONSTR% directory=DIR_DATA_PUMP remap_schema=opal:test_master dumpfile=test_master.dmp table_exists_action=REPLACE

