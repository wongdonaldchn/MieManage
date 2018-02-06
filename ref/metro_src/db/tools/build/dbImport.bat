@echo off
ECHO ******************************************************************
ECHO dbImport
ECHO DB���\�z����
ECHO ******************************************************************

setlocal enabledelayedexpansion

REM ******************************************************************
REM * ��������
REM ******************************************************************


REM ==============================================
REM * ���ݒ�
REM ==============================================

REM �ڑ���
FOR /F "delims=" %%A IN ( constr.ini ) DO (
	SET CONSTR=%%A
)

REM ==============================================
REM * �萔��`
REM ==============================================

REM �c�[�����[�g
SET DIR_ROOT=%~dp0

REM ���s����
SET TMP_TIME2=%TIME: =0%
SET STRING_RUNTIME=%TMP_TIME2:~0,2%%TMP_TIME2:~3,2%%TMP_TIME2:~6,2%
SET STRING_RUNDATE=%DATE:~-10,4%%DATE:~-5,2%%DATE:~-2,2%

REM ���O�t�@�C��
SET FILE_LOG=%DIR_ROOT%\log\%STRING_RUNDATE%\dbImport_%STRING_RUNDATE%-%STRING_RUNTIME%.txt
SET FILE_GENERATELOG=%DIR_ROOT%\log\%STRING_RUNDATE%\generate-ddl_%STRING_RUNDATE%-%STRING_RUNTIME%.log
SET FILE_SEQUENCELOG=%DIR_ROOT%\log\%STRING_RUNDATE%\sequence_%STRING_RUNDATE%-%STRING_RUNTIME%.log
SET FILE_PROCEDURELOG=%DIR_ROOT%\log\%STRING_RUNDATE%\sequence_%STRING_RUNDATE%-%STRING_RUNTIME%.log

REM �f�B���N�g���\��
SET DIR_GENERATE=generate-ddl
SET DIR_SEQUENCE=sequence
SET DIR_PROCEDURE=procedure

REM ���s�N�G���z�u��
SET DIR_EXCECUTETAMPLATE=%DIR_ROOT%\scripts

REM ���s�N�G��
SET FILE_EXECUTE=execute.sql

REM ���s�p���X�g�t�@�C��
SET FILE_LIST=list.sql

REM SQL*Plus�G���[���f������
SET STRING_ORA=ORA-


REM ==============================================
REM * �ϐ�������
REM ==============================================

REM �G���[�t���O
SET BOOL_ERR=0


REM ==============================================
REM * ��������
REM ==============================================

IF NOT EXIST %DIR_ROOT%\log\NUL ( MKDIR %DIR_ROOT%\log )
IF NOT EXIST %DIR_ROOT%\log\%STRING_RUNDATE%\NUL ( MKDIR %DIR_ROOT%\log\%STRING_RUNDATE% )

ECHO [!DATE!-!TIME!][INFO]�J�n >>%FILE_LOG%
ECHO [!DATE!-!TIME!][INFO]���s��:%CONSTR% >>%FILE_LOG%


ECHO ������
ECHO %CONSTR%


:SETTARGET
REM ==============================================
REM * �J�n�C���^�[�t�F�[�X
REM ==============================================

IF '%1'=='' (
	ECHO.
	ECHO DDL�Ώۃf�B���N�g��
	SET /P DIR_DDL=
	IF '!DIR_DDL!'=='' ( GOTO :SETTARGET )
	IF NOT EXIST !DIR_DDL!\NUL (
		ECHO !DIR_DDL!�͑��݂��܂���
		GOTO :SETTARGET
	)

) ELSE (
	SET DIR_DDL=%1
	IF NOT EXIST !DIR_DDL!\NUL (
		ECHO [!DATE!-!TIME!][ERROR]!DIR_DDL!�͑��݂��܂��� >>%FILE_LOG%
		SET BOOL_ERR=1
		GOTO :END
	)

)


ECHO [!DATE!-!TIME!][INFO]�Ώۃf�B���N�g��:%DIR_DDL% >>%FILE_LOG%


REM ******************************************************************
REM * ���C������
REM ******************************************************************


REM ==============================================
REM * ���������ΏۃI�u�W�F�N�g�쐬(execute generate ddl)
REM ==============================================

ECHO EDM��莩�����������I�u�W�F�N�g�쐬
ECHO [!DATE!-!TIME!][INFO]EDM��莩�����������I�u�W�F�N�g�쐬 >>%FILE_LOG%

REM // �f�B���N�g�����݃`�F�b�N //
IF NOT EXIST %DIR_DDL%\%DIR_GENERATE%\NUL (
	ECHO [!DATE!-!TIME!][EXCEPTION]���������p�f�B���N�g��������܂��� >>%FILE_LOG%
	SET /A INT_EXCEPTION=!INT_EXCEPTION!+1
	ECHO [!DATE!-!TIME!][INFO]�������X�L�b�v���܂� >>%FILE_LOG%
	GOTO :GENERATEEND
)

REM // ������ //
IF EXIST %DIR_DDL%\%DIR_GENERATE%\%FILE_EXECUTE% (
	DEL /S /Q %DIR_DDL%\%DIR_GENERATE%\%FILE_EXECUTE% >NUL
)
IF EXIST %DIR_DDL%\%DIR_GENERATE%\%FILE_LIST% (
	DEL /S /Q %DIR_DDL%\%DIR_GENERATE%\%FILE_LIST% >NUL
)

REM // [@ + �t�@�C���� ]�����X�g�t�@�C���ɏ���  //
FOR /F "usebackq delims=" %%A IN (`DIR /B /A:-D %DIR_DDL%\%DIR_GENERATE%`) DO (
	ECHO @%%A >> %DIR_DDL%\%DIR_GENERATE%\%FILE_LIST%
)

REM // ���s //
COPY %DIR_EXCECUTETAMPLATE%\%FILE_EXECUTE% %DIR_DDL%\%DIR_GENERATE% >NUL
PUSHD %DIR_DDL%\%DIR_GENERATE%
sqlplus -L %CONSTR% @%FILE_EXECUTE% @%FILE_LIST% > %FILE_GENERATELOG%
POPD
DEL /S /Q %DIR_DDL%\%DIR_GENERATE%\%FILE_EXECUTE% >NUL
DEL /S /Q %DIR_DDL%\%DIR_GENERATE%\%FILE_LIST% >NUL

REM // ���O�m�F //
ECHO [!DATE!-!TIME!][INFO]�e�[�u���쐬���O�m�F >>%FILE_LOG%
FINDSTR "%STRING_ORA%" %FILE_GENERATELOG%
IF '%ERRORLEVEL%'=='0' (
	ECHO [!DATE!-!TIME!][ERROR]EDM��莩�����������I�u�W�F�N�g�쐬�ŃG���[���������Ă��܂� >>%FILE_LOG%
	SET BOOL_ERR=1
)

:GENERATEEND

REM ==============================================
REM * �V�[�P���X�쐬(sequence)
REM ==============================================

ECHO �V�[�P���X�쐬
ECHO [!DATE!-!TIME!][INFO]�V�[�P���X�쐬 >>%FILE_LOG%

REM // �f�B���N�g�����݃`�F�b�N //
IF NOT EXIST %DIR_DDL%\%DIR_SEQUENCE%\NUL (
	ECHO [!DATE!-!TIME!][EXCEPTION]�V�[�P���X�p�f�B���N�g��������܂��� >>%FILE_LOG%
	SET /A INT_EXCEPTION=!INT_EXCEPTION!+1
	ECHO [!DATE!-!TIME!][INFO]�������X�L�b�v���܂� >>%FILE_LOG%
	GOTO :SEQUENCEEND
)

REM // ������ //
IF EXIST %DIR_DDL%\%DIR_SEQUENCE%\%FILE_EXECUTE% (
	DEL /S /Q %DIR_DDL%\%DIR_SEQUENCE%\%FILE_EXECUTE% >NUL
)
IF EXIST %DIR_DDL%\%DIR_SEQUENCE%\%FILE_LIST% (
	DEL /S /Q %DIR_DDL%\%DIR_SEQUENCE%\%FILE_LIST% >NUL
)

REM // [@ + �t�@�C���� ]�����X�g�t�@�C���ɏ���  //
FOR /F "usebackq delims=" %%A IN (`DIR /B /A:-D %DIR_DDL%\%DIR_SEQUENCE%`) DO (
	ECHO @%%A >> %DIR_DDL%\%DIR_SEQUENCE%\%FILE_LIST%
)

REM // ���s //
COPY %DIR_EXCECUTETAMPLATE%\%FILE_EXECUTE% %DIR_DDL%\%DIR_SEQUENCE% >NUL
PUSHD %DIR_DDL%\%DIR_SEQUENCE%
sqlplus -L %CONSTR% @%FILE_EXECUTE% @%FILE_LIST% > %FILE_SEQUENCELOG%
POPD
DEL /S /Q %DIR_DDL%\%DIR_SEQUENCE%\%FILE_EXECUTE% >NUL
DEL /S /Q %DIR_DDL%\%DIR_SEQUENCE%\%FILE_LIST% >NUL

REM // ���O�m�F //
ECHO [!DATE!-!TIME!][INFO]�V�[�P���X�쐬���O�m�F >>%FILE_LOG%
FINDSTR "%STRING_ORA%" %FILE_SEQUENCELOG%
IF '%ERRORLEVEL%'=='0' (
	ECHO [!DATE!-!TIME!][ERROR]�V�[�P���X�쐬�ŃG���[���������Ă��܂� >>%FILE_LOG%
	SET BOOL_ERR=1
)

:SEQUENCEEND

REM ==============================================
REM * �v���V�[�W���쐬(procedure)
REM ==============================================

ECHO �v���V�[�W���쐬
ECHO [!DATE!-!TIME!][INFO]�v���V�[�W���쐬 >>%FILE_LOG%

REM // �f�B���N�g�����݃`�F�b�N //
IF NOT EXIST %DIR_DDL%\%DIR_PROCEDURE%\NUL (
	ECHO [!DATE!-!TIME!][EXCEPTION]�V�[�P���X�p�f�B���N�g��������܂��� >>%FILE_LOG%
	SET /A INT_EXCEPTION=!INT_EXCEPTION!+1
	ECHO [!DATE!-!TIME!][INFO]�������X�L�b�v���܂� >>%FILE_LOG%
	GOTO :PROCEDUREEND
)

REM // ������ //
IF EXIST %DIR_DDL%\%DIR_PROCEDURE%\%FILE_EXECUTE% (
	DEL /S /Q %DIR_DDL%\%DIR_PROCEDURE%\%FILE_EXECUTE% >NUL
)
IF EXIST %DIR_DDL%\%DIR_PROCEDURE%\%FILE_LIST% (
	DEL /S /Q %DIR_DDL%\%DIR_PROCEDURE%\%FILE_LIST% >NUL
)

REM // [@ + �t�@�C���� ]�����X�g�t�@�C���ɏ���  //
FOR /F "usebackq delims=" %%A IN (`DIR /B /A:-D %DIR_DDL%\%DIR_PROCEDURE%`) DO (
	ECHO @%%A >> %DIR_DDL%\%DIR_PROCEDURE%\%FILE_LIST%
)

REM // ���s //
COPY %DIR_EXCECUTETAMPLATE%\%FILE_EXECUTE% %DIR_DDL%\%DIR_PROCEDURE% >NUL
PUSHD %DIR_DDL%\%DIR_PROCEDURE%
sqlplus -L %CONSTR% @%FILE_EXECUTE% @%FILE_LIST% > %FILE_PROCEDURELOG%
POPD
DEL /S /Q %DIR_DDL%\%DIR_PROCEDURE%\%FILE_EXECUTE% >NUL
DEL /S /Q %DIR_DDL%\%DIR_PROCEDURE%\%FILE_LIST% >NUL

REM // ���O�m�F //
ECHO [!DATE!-!TIME!][INFO]�v���V�[�W���쐬���O�m�F >>%FILE_LOG%
FINDSTR "%STRING_ORA%" %FILE_PROCEDURELOG%
IF '%ERRORLEVEL%'=='0' (
	ECHO [!DATE!-!TIME!][ERROR]�v���V�[�W���쐬�ŃG���[���������Ă��܂� >>%FILE_LOG%
	SET BOOL_ERR=1
)

:PROCEDUREEND


:END
REM ******************************************************************
REM *�I������
REM ******************************************************************


REM ==============================================
REM * �I���C���^�[�t�F�[�X
REM ==============================================

ECHO.
IF '%BOOL_ERR%'=='0' (
	ECHO [!DATE!-!TIME!][COMPLETE]�������������܂��� >>%FILE_LOG%
) ELSE (
	ECHO [!DATE!-!TIME!][ERROR]�G���[���������܂��� >>%FILE_LOG%

)


REM ==============================================
REM * �I��
REM ==============================================

IF '%1'=='' (
	endlocal
	pause
) ELSE (
	exit /b %BOOL_ERR%
)


GOTO :EOF


:EOF