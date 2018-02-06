#!/bin/sh

###############################################################################
#  Script name  : func_truncate_table.sh
#  Description  : 指定されたテーブル内においてレコード全件の
#                 物理削除(TRUNCATE)を行う。
#  Usage        : func_truncate_table.sh TABLE_NAME
#                     TABLE_NAME
#                       全件削除対象テーブル名
#  Date         : 2017/05/31
#  Author       : Wataru Ito
#  Returns      : 0   正常終了
#               : 110 異常終了(引数過不足)
#               : 111 異常終了(物理削除実行エラー)
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
LOGIN_FILE=${COMMON_DIR}/func/conf/apltrn.config

###############################################################################
# スクリプト本文
###############################################################################

### 処理開始ログ出力
LOG_MSG "PARAMETER = [${*}]"

### 引数精査
if [ ${#} -ne 1 ]; then
    LOG_MSG "${B922A0401}"
    LOG_MSG "EXIT_CODE = [110]"
    exit 110
fi

### 指定テーブル全件削除処理
SQLPLUS ${LOGIN_FILE} "truncate table ${1};"

### 指定テーブル全件削除処理を失敗した場合のログ出力
if [ ${?} -ne 0 ]; then
    LOG_MSG "${B922A0402}"
    LOG_MSG "EXIT_CODE = [111]"
    exit 111
fi

### 処理終了ログ出力
LOG_MSG "EXIT_CODE = [0]"
exit 0
