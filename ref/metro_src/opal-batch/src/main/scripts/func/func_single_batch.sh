#!/bin/sh

###############################################################################
#  Script name  : func_single_batch.sh
#  Description  : Javaで実装された都度起動型バッチプログラムを起動する
#  Usage        : func_single_batch.sh JOB_ID REQUEST_PATH USER_ID [PARAM_MAIN]...
#                     JOB_ID
#                       ジョブID
#                     REQUEST_PATH
#                       リクエストパス
#                     USER_ID
#                       ユーザID
#                     PARAM_MAIN
#                       Mainクラス引数
#  Date         : 2017/05/31
#  Author       : Wataru Ito
#  Returns      : 0   正常終了
#               : 1~  異常終了(起動した都度起動型バッチプログラムの終了コード)
###############################################################################

### シェルスクリプト共通設定ファイルの読込
. ${COMMON_CONF_DIR}/common.sh

### ディレクトリ情報読込
. ${COMMON_CONF_DIR}/batch_dir.config

### JAVA実行情報読込
. ${COMMON_CONF_DIR}/java_env.config

### JAVA実行共通関数読込
. ${COMMON_DIR}/func/conf/func_java_common.sh

###############################################################################
# スクリプト本文
###############################################################################

### 処理開始ログ出力
LOG_MSG "PARAMETER = [${INP_NAB_OPT} ${*}]"

### 引数取得
JOB_ID=${1}
shift
REQ_PATH=${1}
shift
USER_ID=${1}
shift

### 障害監視用オプション取得
MNT_REQ_ID="-Dmonitor-request-id=${JOB_ID}"

### プロセス毎にコピーする必要があるJARをジョブ管理システム配下にコピー
RUN_LIB_OUT_DIR=${RUN_LIB_OUT}/${JOB_ID}_$$
COPY_RUN_JARS ${RUN_LIB_OUT_DIR}

### クラスパスを設定する変数
SET_RUNTIME_CLASSPATH ${RUN_LIB_OUT_DIR}

### 実行オプションの生成
NAB_OPT="-classpath ${RUNTIME_CLASSPATH} ${SING_OPT} ${INP_NAB_OPT} "

### 起動プロセス設定
NAB_OPT="${NAB_OPT} -Dnablarch.bootProcess=${JOB_ID} "

### アプリケーションログファイル名を設定
NAB_OPT=${NAB_OPT}"-Dwriter.appFile.filePath=${APP_LOG_DIR}/${JOB_ID}_application.log "
NAB_OPT=${NAB_OPT}"-Dwriter.monitorFile.filePath=${MNT_LOG_DIR}/${JOB_ID}_monitor.log "
NAB_OPT=${NAB_OPT}"-Dwriter.MESSAGING_CSV.filePath=${MSG_CSV_LOG_DIR}/${JOB_ID}_messaging_csv.log "

### Mainクラス引数を設定
MAIN_ARGS="-diConfig ${SING_DI_CONF}"
MAIN_ARGS="${MAIN_ARGS} -requestPath ${REQ_PATH}"
MAIN_ARGS="${MAIN_ARGS} -userId ${USER_ID}"

### バッチ実行
eval ${JAVA_HOME_PATH}/bin/java ${MNT_REQ_ID} ${NAB_OPT} ${MAIN} ${MAIN_ARGS} "$@"

### 処理終了ログ出力
EXIT_CODE=${?}
LOG_MSG "EXIT_CODE = [${EXIT_CODE}]"

### プロセス毎にコピーしたJARディレクトリを削除
# 万一、ディスク障害などで削除できなかった場合を考慮する。
rm -rf ${RUN_LIB_OUT_DIR} || DIE

exit ${EXIT_CODE}
