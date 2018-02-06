#!/bin/sh

###############################################################################
#  Script name  : func_backup_file.sh
#  Description  : ファイルをバックアップする（ファイル名：元ファイル名_%Y%m%d%H%M%S）
#  Usage        : func_backup_file.sh INPUT_DIR_PATH INPUT_FILE_NAME BACKUP_DIR_PATH
#                     INPUT_DIR_PATH
#                       バックアップ元ディレクトリパス（環境ごとのベースディレクトリからのパス）
#                     INPUT_FILE_NAME
#                       ファイル名
#                     BACKUP_DIR_PATH
#                       バックアップ先ディレクトリパス（環境ごとのベースディレクトリからのパス）
#  Date         : 2017/05/31
#  Author       : Wataru Ito
#  Returns      : 0   正常終了
#                 110 異常終了(引数過不足)
#                 111 異常終了(バックアップ元ディレクトリ変数名不正、
#                              またはバックアップ元ディレクトリが未存在)
#                 112 異常終了(バックアップ先ディレクトリ変数名不正、
#                              またはバックアップ先ディレクトリが未存在)
#                 114 異常終了(バックアップに失敗した場合)
#                 115 異常終了(ベースディレクトリ未存在)
###############################################################################

### シェルスクリプト共通設定ファイルの読込
. ${COMMON_CONF_DIR}/common.sh

### ディレクトリ情報設定ファイルの読込
. ${COMMON_CONF_DIR}/batch_dir.config

### 障害メッセージ設定ファイルの読込
. ${COMMON_DIR}/conf/error.message

###############################################################################
# スクリプト本文
###############################################################################

### 処理開始ログ出力
LOG_MSG "PARAMETER = [${*}]"

### ベースディレクトリ存在チェック
if [ ! -d "${FILE_TRANCEFER_BASE_DIR}" ]; then
    LOG_MSG "${B922A0106}"
    LOG_MSG "PATH = ${FILE_TRANCEFER_BASE_DIR}"
    LOG_MSG "EXIT_CODE = [115]"
    exit 115
fi

### 引数個数精査
if [ ${#} -ne 3 ]; then
    LOG_MSG "${B922A0101}"
    LOG_MSG "EXIT_CODE = [110]"
    exit 110
fi

### 引数取得
INPUT_DIR_PATH=${FILE_TRANCEFER_BASE_DIR}/${1}
shift
INPUT_FILE_NAME=${1}
shift
BACKUP_DIR_PATH=${FILE_TRANCEFER_BASE_DIR}/${1}
shift

### バックアップ元ディレクトリパス存在チェック
if [ ! -d "${INPUT_DIR_PATH}" ]; then
    LOG_MSG "${B922A0102}"
    LOG_MSG "PATH = ${INPUT_DIR_PATH}"
    LOG_MSG "EXIT_CODE = [111]"
    exit 111
fi

### バックアップ先ディレクトリパス存在チェック
if [ ! -d "${BACKUP_DIR_PATH}" ]; then
    LOG_MSG "${B922A0103}"
    LOG_MSG "PATH = ${BACKUP_DIR_PATH}"
    LOG_MSG "EXIT_CODE = [112]"
    exit 112
fi

### バックアップ対象ファイル
INPUT_FILE_PATH=${INPUT_DIR_PATH}/${INPUT_FILE_NAME}

### ファイルバックアップ
DATE=`date "+%Y%m%d%H%M%S"`

for INPUT_FILE_NAME_DETAIL in `ls ${INPUT_FILE_PATH} 2>/dev/null | xargs -i basename {}`
do
    INPUT_FILE_PATH_DETAIL=${INPUT_DIR_PATH}/${INPUT_FILE_NAME_DETAIL}
    BACKUP_FILE_PATH_DETAIL=${BACKUP_DIR_PATH}/${INPUT_FILE_NAME_DETAIL}_${DATE}

    ### 入出力ファイルログ出力
    LOG_MSG "INPUT_FILE = [${INPUT_FILE_PATH_DETAIL}]"
    LOG_MSG "BACKUP_FILE = [${BACKUP_FILE_PATH_DETAIL}]"

    ### ファイルバックアップ
    cp -pf ${INPUT_FILE_PATH_DETAIL} ${BACKUP_FILE_PATH_DETAIL}

    ### バックアップが失敗した場合
    if [ ${?} -ne 0 ]; then
        LOG_MSG "${B922A0105}"
        LOG_MSG "EXIT_CODE = [114]"
        exit 114
    fi
done

### 終了処理ログ出力
LOG_MSG "EXIT_CODE = [0]"

exit 0
