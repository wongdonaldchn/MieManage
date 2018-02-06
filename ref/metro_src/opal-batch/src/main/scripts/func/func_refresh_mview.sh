#!/bin/sh

###############################################################################
#  Script name  : func_refresh_mview.sh
#  Description  : 指定されたマテリアライズド・ビューの
#                 リフレッシュ処理を行う。
#  Usage        : func_refresh_mview.sh MVIEW_NAME
#                     MVIEW_NAME
#                       リフレッシュ対象MVIEW名
#  Date         : 2017/10/16
#  Author       : Wataru Ito
#  Returns      : 0   正常終了
#               : 110 異常終了(引数過不足)
#               : 111 異常終了(リフレッシュエラー)
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
LOGIN_FILE=${COMMON_DIR}/func/conf/aplmvw.config

###############################################################################
# スクリプト本文
###############################################################################

### 処理開始ログ出力
LOG_MSG "PARAMETER = [${*}]"

### 引数精査
if [ ${#} -ne 1 ]; then
    LOG_MSG "${B922A0801}"
    LOG_MSG "EXIT_CODE = [110]"
    exit 110
fi

### MVIEWリフレッシュ処理
SQLPLUS ${LOGIN_FILE} "exec dbms_mview.refresh('${1}');"

### MVIEWリフレッシュ処理を失敗した場合のログ出力
if [ ${?} -ne 0 ]; then
    LOG_MSG "${B922A0802}"
    LOG_MSG "EXIT_CODE = [111]"
    exit 111
fi

### 処理終了ログ出力
LOG_MSG "EXIT_CODE = [0]"
exit 0
