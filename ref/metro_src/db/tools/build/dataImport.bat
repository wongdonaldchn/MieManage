@echo off
ECHO ******************************************************************
ECHO dataImport
ECHO �f�[�^�𓊓�����
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
SET FILE_LOG=%DIR_ROOT%\log\%STRING_RUNDATE%\dataImport_%STRING_RUNDATE%-%STRING_RUNTIME%.txt
SET FILE_DATALOG=%DIR_ROOT%\log\%STRING_RUNDATE%\data_%STRING_RUNDATE%-%STRING_RUNTIME%.log

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
	ECHO �Ώۃf�B���N�g��
	SET /P DIR_DML=
	IF '!DIR_DML!'=='' ( GOTO :SETTARGET )
	IF NOT EXIST !DIR_DML!\NUL (
		ECHO !DIR_DML!�͑��݂��܂���
		GOTO :SETTARGET
	)

) ELSE (
	SET DIR_DML=%1
	IF NOT EXIST !DIR_DML!\NUL (
		ECHO [!DATE!-!TIME!][ERROR]!DIR_DML!�͑��݂��܂��� >>%FILE_LOG%
		SET BOOL_ERR=1
		GOTO :END
	)

)

ECHO [!DATE!-!TIME!][INFO]�Ώۃf�B���N�g��:%DIR_DML% >>%FILE_LOG%


REM ******************************************************************
REM * ���C������
REM ******************************************************************


REM ==============================================
REM * �f�[�^����(insert)
REM ==============================================

ECHO �f�[�^����
ECHO [!DATE!-!TIME!][INFO]�f�[�^���� >>%FILE_LOG%

REM // �f�B���N�g�����݃`�F�b�N //
IF NOT EXIST %DIR_DML%\NUL (
	ECHO [!DATE!-!TIME!][EXCEPTION]�f�[�^�p�f�B���N�g��������܂��� >>%FILE_LOG%
	SET /A INT_EXCEPTION=!INT_EXCEPTION!+1
	ECHO [!DATE!-!TIME!][INFO]�f�[�^�������X�L�b�v���܂� >>%FILE_LOG%
	GOTO :DATAEND
)

REM // ������ //
IF EXIST %DIR_DML%\%FILE_EXECUTE% (
	DEL /S /Q %DIR_DML%\%FILE_EXECUTE% >NUL
)
IF EXIST %DIR_DML%\%FILE_LIST% (
	DEL /S /Q %DIR_DML%\%FILE_LIST% >NUL
)

REM // [@ + �t�@�C���� ]�����X�g�t�@�C���ɏ���  //
FOR /F "usebackq delims=" %%A IN (`DIR /B /A:-D %DIR_DML%`) DO (
	ECHO @%%A >> %DIR_DML%\%FILE_LIST%
)

REM // ���s //
COPY %DIR_EXCECUTETAMPLATE%\%FILE_EXECUTE% %DIR_DML% >NUL
PUSHD %DIR_DML%
sqlplus -L %CONSTR% @%FILE_EXECUTE% @%FILE_LIST% > %FILE_DATALOG%
POPD
DEL /S /Q %DIR_DML%\%FILE_EXECUTE% >NUL
DEL /S /Q %DIR_DML%\%FILE_LIST% >NUL

REM // ���O�m�F //
ECHO [!DATE!-!TIME!][INFO]�f�[�^�������O�m�F >>%FILE_LOG%
FINDSTR "%STRING_ORA%" %FILE_DATALOG%
IF '%ERRORLEVEL%'=='0' (
	ECHO [!DATE!-!TIME!][ERROR]�f�[�^�����ŃG���[���������Ă��܂� >>%FILE_LOG%
	SET BOOL_ERR=1
)

:DATAEND



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