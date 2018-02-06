#!/bin/sh

###############################################################################
#  Script name  : func_check_file_exist.sh
#  Description  : ファイル存在を確認し、存在しない場合は同名の0byteファイルをダミーファイルとして作成する
#  Usage        : func_check_file_exist.sh INPUT_DIR_PATH INPUT_FILE_NAME
#                     INPUT_DIR_PATH
#                       存在チェック対象ディレクトリパス（環境ごとのベースディレクトリからのパス）
#                     INPUT_FILE_NAME
#                       存在チェック対象ファイル名
#  Date         : 2017/09/14
#  Author       : Wataru Ito
#  Returns      : 0   正常終了
#                 110 異常終了(引数指定過不足)
#                 111 異常終了(存在チェック対象ディレクトリ変数名不正、または
#                              存在チェック対象ディレクトリが存在しない場合)
#                 112 異常終了(ダミーファイル作成に失敗)
#                 113 異常終了(ベースディレクトリ未存在)
###############################################################################

### シェルスクリプト共通設定ファイルの読込
. ${COMMON_CONF_DIR}/common.sh

### ディレクトリ情報読み込み
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
    LOG_MSG "${B922A0704}"
    LOG_MSG "PATH = ${FILE_TRANCEFER_BASE_DIR}"
    LOG_MSG "EXIT_CODE = [113]"
    exit 113
fi

### 引数個数精査
if [ ${#} -ne 2 ]; then
    LOG_MSG "${B922A0701}"
    LOG_MSG "EXIT_CODE = [110]"
    exit 110
fi

### 引数取得
DIR_PATH=${FILE_TRANCEFER_BASE_DIR}/${1}
shift
FILE_NAME=${DIR_PATH}/${1}
shift

### 存在チェック対象ディレクトリパス存在チェック
if [ ! -d "${DIR_PATH}" ]; then
    LOG_MSG "${B922A0702}"
    LOG_MSG "PATH = ${DIR_PATH}"
    LOG_MSG "EXIT_CODE = [111]"
    exit 111
fi

### ファイル存在チェック
### 存在しない場合は同名の0byteファイルをダミーファイルとして作成
if [ -e "${FILE_NAME}" ]; then
    LOG_MSG "PATH = ${FILE_NAME} STATUS = [EXIST] ACTION = [OMIT]"
else
    LOG_MSG "PATH = ${FILE_NAME} STATUS = [NOT EXIST] ACTION = [MAKE DUMMY FILE]"
    touch ${FILE_NAME}

    ### ダミーファイル作成に失敗した場合
    if [ ${?} -ne 0 ]; then
        LOG_MSG "${B922A0703}"
        LOG_MSG "EXIT_CODE = [112]"
        exit 112
    fi
fi


LOG_MSG "EXIT_CODE = [0]"

exit 0
