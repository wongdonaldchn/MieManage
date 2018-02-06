###############################################################################
#  File name    : func_sqlplus_common.sh
#  Description  : sqlplus接続で共通的に使用する変数・関数を定義する
#  Date         : 2017/05/31
#  Author       : Wataru Ito
###############################################################################

###################################################
# sqlplusのPATHを通す処理
###################################################
export ORACLE_HOME
export PATH=${ORACLE_HOME}/bin:${PATH}


###################################################
# Function      : SQLPLUS
# Description   : sqlplusによるsql実行（sql直接実行）
# Usage         : SQLPLUS LOGIN_FILE SQL
#                    LOGIN_FILE
#                      sqlplusに接続するための情報を記載したconfigファイル
#                    SQL
#                      sql文
#  Returns      : 0   正常終了
#               : 1~  異常終了
###################################################

function SQLPLUS
{
    
    ### sqlplus実行
    sqlplus /nolog <<EOF
whenever sqlerror exit failure rollback
whenever oserror exit failure rollback
@${1}
${2}
exit 0;
EOF

    return ${?}
}

###################################################
# Function      : SQLPLUS_EXEC_FILE
# Description   : sqlplusによるsql実行（sqlファイル実行）
#                 使用する際は、実行対象のsqlファイル内に
#                 sqlplusの終了コードを返却する実装をする。実装されていない場合は
#                 終了コード1を返却する。
# Usage         : SQLPLUS LOGIN_FILE SQL_FILE
#                    LOGIN_FILE
#                      sqlplusに接続するための情報を記載したconfigファイル
#                    SQL_FILE
#                      sqlファイル
#  Returns      : 0   正常終了
#               : 1~  異常終了
###################################################

function SQLPLUS_EXEC_FILE
{
    
    ### sqlplus実行
    sqlplus /nolog <<EOF
whenever sqlerror exit failure rollback
whenever oserror exit failure rollback
@${1}
@${2}
exit 1;
EOF

    return ${?}
}
