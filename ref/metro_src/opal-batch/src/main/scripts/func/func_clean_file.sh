#!/bin/sh

###############################################################################
#  Script name  : func_clean_file.sh
#  Description  : システムバックアップ済みのファイルを削除する
#  Usage        : func_clean_file.sh INI_FILE_NAME
#                     INI_FILE_NAME
#                       ファイルクリーニング定義ファイル名
#  Date         : 2017/09/20
#  Author       : Wataru Ito
#  Returns      : 0   正常終了
#                 110 異常終了(引数過不足)
#                 111 異常終了(ファイルクリーニング定義ファイル未存在)
#                 112 異常終了(ファイル削除に失敗した場合)
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

### 引数個数精査
if [ ${#} -ne 1 ]; then
    LOG_MSG "${BZ11A0101}"
    LOG_MSG "EXIT_CODE = [110]"
    exit 110
fi

### 引数取得
INI_FILE=${COMMON_DIR}/func/conf/${1}
shift

### ファイルクリーニング定義ファイル存在チェック
if [ ! -e "${INI_FILE}" ]; then
    LOG_MSG "${BZ11A0102}"
    LOG_MSG "PATH = ${INI_FILE}"
    LOG_MSG "EXIT_CODE = [111]"
    exit 111
fi

### ファイルクリーニング
COUNT=0
while read CLEAN_LIST
do
    CLEAN_DIR_PATH=`echo ${CLEAN_LIST} | cut -d ' ' -f 1`
    CLEAN_FILE_NAME=`echo ${CLEAN_LIST} | cut -d ' ' -f 2`
    EXCESS_GEN=`echo ${CLEAN_LIST} | cut -d ' ' -f 3`
    DATE_LENGTH=`echo ${CLEAN_LIST} | cut -d ' ' -f 4`

    ### 削除対象日付取得
    DELETE_DATE=`date -d "${EXCESS_GEN} days ago" '+%Y%m%d'`

    ### クリーニング対象候補ファイルリスト作成
    for CLEAN_FILE_DETAIL in `ls ${CLEAN_DIR_PATH} 2>/dev/null | xargs -i basename {}`
    do
        if [ ! `echo "${CLEAN_FILE_DETAIL}" | grep -F "${CLEAN_FILE_NAME}"` ]; then
            continue
        fi

        ### ファイル名日付部分取得
        FILE_DATE=`expr "${CLEAN_FILE_DETAIL}" : ".*\([0-9]\{${DATE_LENGTH}\}\)"`

        ### 日付が取得できない場合は処理対象外
        expr ${FILE_DATE:0:8} + 1 > /dev/null 2>&1
        if [ ${?} -ge 2 ]; then
            LOG_MSG "DELETE_FILE_PATH = [${CLEAN_DIR_PATH}/${CLEAN_FILE_DETAIL}] ACTION = [OMIT]"
            continue
        fi

        ### ファイル名日付部分が削除対象日付以降の場合は処理対象外
        if [ ${FILE_DATE:0:8} -gt ${DELETE_DATE} ]; then
            LOG_MSG "DELETE_FILE_PATH = [${CLEAN_DIR_PATH}/${CLEAN_FILE_DETAIL}] ACTION = [OMIT]"
            continue
        fi

        ### ファイル削除
        LOG_MSG "DELETE_FILE_PATH = [${CLEAN_DIR_PATH}/${CLEAN_FILE_DETAIL}] ACTION = [DELETE]"
        rm ${CLEAN_DIR_PATH}/${CLEAN_FILE_DETAIL}

        ### ファイル削除に失敗した場合
        if [ ${?} -ne 0 ]; then
            LOG_MSG "${BZ11A0103}"
            LOG_MSG "EXIT_CODE = [112]"
            exit 112
        fi

        COUNT=`expr ${COUNT} + 1`
    done
done <${INI_FILE}

### 終了処理ログ出力
LOG_MSG "RESULT: DELETE FILE COUNT = [${COUNT}]"
LOG_MSG "EXIT_CODE = [0]"

exit 0
