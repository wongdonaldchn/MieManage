#!/bin/sh

###############################################################################
#  Script name  : func_resident_act_check.sh
#  Description  : プロセスの停止の確認を行う
#  Usage        : func_resident_act_check.sh JOB_ID [CHECK_NUM] [CHECK_INTERVAL]
#                     JOB_ID
#                       ジョブID
#                     CHECK_NUM
#                       チェック回数（デフォルト：1）
#                     CHECK_INTERVAL
#                       チェック間隔(秒)（デフォルト：60）
#  Date         : 2017/05/31
#  Author       : Wataru Ito
#  Returns      : 0   正常終了
#               : 110 異常終了(引数過不足)
#               : 111 異常終了(チェック回数不正)
#               : 112 異常終了(チェック間隔不正)
#               : 113 異常終了(規定回数チェック後も対象プロセス起動状態)
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
if [ ${#} -ne 1 ] && [ ${#} -ne 2 ] && [ ${#} -ne 3 ]; then
    LOG_MSG "${B922A0501}"
    LOG_MSG "EXIT_CODE = [110]"
    exit 110
fi

### 引数取得
JOB_ID=${1}
shift
if [ "${1}" = "" ]; then
    CHECK_NUM=1
else
    CHECK_NUM=${1}
    shift
fi
if [ "${1}" = "" ]; then
    CHECK_INTERVAL=60
else
    CHECK_INTERVAL=${1}
    shift
fi

### 引数精査
expr ${CHECK_NUM} + 1 > /dev/null 2>&1
if [ ${?} -ge 2 ] || [ "${CHECK_NUM}" -le 0 ]; then
    LOG_MSG "${B922A0502}"
    LOG_MSG "EXIT_CODE = [111]"
    exit 111
fi

expr ${CHECK_INTERVAL} + 1 > /dev/null 2>&1
if [ ${?} -ge 2 ] || [ "${CHECK_INTERVAL}" -le 0 ]; then
    LOG_MSG "${B922A0503}"
    LOG_MSG "EXIT_CODE = [112]"
    exit 111
fi

### プロセスチェック
COUNT=1
while true
do
    REC=`ps -ef|grep "monitor-request-id=${JOB_ID}"|grep -v grep|wc -l`
    if [ ${REC} -eq 0 ]; then
        LOG_MSG "EXIT_CODE = [0]"
        exit 0
    fi

    if [ ${COUNT} -ge ${CHECK_NUM} ]; then
        break
    fi
    COUNT=`expr ${COUNT} + 1`

    sleep ${CHECK_INTERVAL}
done

### プロセス起動エラー出力
LOG_MSG "${B922A0504}"
LOG_MSG "EXIT_CODE = [113]"
exit 113
