#!/bin/sh

###############################################################################
#  Script name  : func_delete_file.sh
#  Description  : ファイルを削除する
#  Usage        : func_delete_file.sh DIR_PATH DELETE_FILE_NAME
#                     DIR_PATH_VAR
#                       ディレクトリパス（環境ごとのベースディレクトリからのパス）
#                     DELETE_FILE_NAME
#                       削除対象ファイル名
#  Date         : 2017/05/31
#  Author       : Wataru Ito
#  Returns      : 0   正常終了
#                 110 異常終了(引数指定過不足)
#                 111 異常終了(ディレクトリ変数名不正、またはディレクトリ未存在)
#                 113 異常終了(対象ファイルの削除失敗)
#                 114 異常終了(ベースディレクトリ未存在)
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
    LOG_MSG "${B922A0305}"
    LOG_MSG "PATH = ${FILE_TRANCEFER_BASE_DIR}"
    LOG_MSG "EXIT_CODE = [114]"
    exit 114
fi

### 引数個数精査
if [ ${#} -ne 2 ]; then
    LOG_MSG "${B922A0301}"
    LOG_MSG "EXIT_CODE = [110]"
    exit 110
fi

### 引数取得
DIR_PATH=${FILE_TRANCEFER_BASE_DIR}/${1}
shift
DELETE_FILE_NAME=${1}
shift

### ディレクトリパス存在チェック
if [ ! -d "${DIR_PATH}" ]; then
    LOG_MSG "${B922A0302}"
    LOG_MSG "PATH = ${DIR_PATH}"
    LOG_MSG "EXIT_CODE = [111]"
    exit 111
fi

### 削除対象ファイル存在チェック
DELETE_FILE_PATH=${DIR_PATH}/${DELETE_FILE_NAME}

ls ${DELETE_FILE_PATH} >/dev/null 2>&1
result=${?}
if [ ${result} -ne 0 ]; then
    LOG_MSG "PATH = ${DELETE_FILE_PATH} ACTION = [OMIT]"
    LOG_MSG "EXIT_CODE = [0]"
    exit 0
fi

### ファイル削除
rm ${DELETE_FILE_PATH}

### 対象ファイル削除に失敗した場合
if [ ${?} -ne 0 ]; then
    LOG_MSG "${B922A0304}"
    LOG_MSG "EXIT_CODE = [113]"
    exit 113
fi

### 処理終了ログ出力
LOG_MSG "DELETE_FILE_PATH = [${DELETE_FILE_PATH}] ACTION = [DELETE]"
LOG_MSG "EXIT_CODE = [0]"

exit 0
