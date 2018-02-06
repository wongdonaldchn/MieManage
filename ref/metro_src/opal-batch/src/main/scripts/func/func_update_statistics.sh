#!/bin/sh

###############################################################################
#  Script name  : func_update_statistics.sh
#  Description  : 統計情報を更新する
#  Usage        : func_update_statistics.sh OWNER TABLE_NAME SAMPLE_SIZE DEGREE STALE_STATS
#                     OWNER
#                       スキーマ名
#                     TABLE_NAME
#                       統計情報更新対象テーブル名
#                     SAMPLE_SIZE
#                       サンプルレート
#                     DEGREE
#                       並列度
#                     STALE_STATS
#                       失効確認
#                       (確認する:1、確認しない:0)
#  Date         : 2017/05/31
#  Author       : Wataru Ito
#  Returns      : 0   正常終了
#               : 110 異常終了(引数過不足)
#               : 111 異常終了(並列度不正)
#               : 112 異常終了(失効確認不正)
#               : 113 異常終了(統計情報更新対象テーブル未存在)
#               : 114 異常終了(統計情報更新エラー)
###############################################################################

### シェルスクリプト共通設定ファイル読込
. ${COMMON_CONF_DIR}/common.sh

### ディレクトリ情報設定ファイル読込
. ${COMMON_CONF_DIR}/batch_dir.config

### 障害メッセージ設定ファイル読込
. ${COMMON_DIR}/conf/error.message

### sqlplus用共通設定ファイル読込
. ${COMMON_DIR}/func/conf/func_sqlplus_common.sh

### sqlplus用接続ユーザ設定ファイル名
LOGIN_FILE=${COMMON_DIR}/func/conf/aplsta.config

###############################################################################
# スクリプト本文
###############################################################################

### 処理開始ログ出力
LOG_MSG "PARAMETER = [${*}]"

### 引数個数精査
if [ ${#} -ne 5 ]; then
    LOG_MSG "${B922A0601}"
    LOG_MSG "EXIT_CODE = [110]"
    exit 110
fi

### 引数取得
OWNER=${1}
shift
TABLE_NAME=${1}
shift
SAMPLE_SIZE=${1}
shift
DEGREE=${1}
shift
STALE_STATS=${1}
shift

### 引数精査
if [ "${DEGREE}" != "DBMS_STATS.AUTO_DEGREE" ]; then
    expr ${DEGREE} + 1 > /dev/null 2>&1
    if [ ${?} -ge 2 ] || [ "${DEGREE}" -le 0 ]; then
        LOG_MSG "${B922A0602}"
        LOG_MSG "EXIT_CODE = [111]"
        exit 111
    fi
fi

if [ "${STALE_STATS}" -ne 0 ] && [ "${STALE_STATS}" -ne 1 ]; then
    LOG_MSG "${B922A0603}"
    LOG_MSG "EXIT_CODE = [112]"
    exit 112
fi

### テーブル存在精査・統計情報失効確認・統計情報更新
SQLPLUS_EXEC_FILE ${LOGIN_FILE} "${COMMON_DIR}/func/conf/func_update_statistics.sql ${OWNER} ${TABLE_NAME} ${SAMPLE_SIZE} ${DEGREE} ${STALE_STATS}"
SQL_CODE=${?}

### 処理ログ出力
if [ ${SQL_CODE} -eq 0 ]; then
     LOG_MSG "UPDATE STATISTICS SCEHMA = [${OWNER}] TABLE = [${TABLE_NAME}] ACTION = [UPDATE]"
elif [ ${SQL_CODE} -eq 2 ]; then
     LOG_MSG "UPDATE STATISTICS SCEHMA = [${OWNER}] TABLE = [${TABLE_NAME}] ACTION = [OMIT]"
elif [ ${SQL_CODE} -eq 3 ]; then
    LOG_MSG "${B922A0604}"
    LOG_MSG "EXIT_CODE = [113]"
    exit 113
else
    LOG_MSG "${B922A0605}"
    LOG_MSG "EXIT_CODE = [114]"
    exit 114
fi

### 処理終了ログ出力
LOG_MSG "EXIT_CODE = [0]"
exit 0
