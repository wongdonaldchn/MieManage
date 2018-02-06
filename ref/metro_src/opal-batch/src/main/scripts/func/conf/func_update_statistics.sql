/*
   File name    : func_update_statistics.sql
   Description  : 統計情報更新用sqlファイル
   Usage        : @func_update_statistics.sql OWNER TABLE_NAME SAMPLE_SIZE DEGREE STALE_STATS
                      OWNER
                        スキーマ名
                      TABLE_NAME
                        統計情報更新対象テーブル名
                      SAMPLE_SIZE
                        サンプルレート
                      DEGREE
                        並列度
                      STALE_STATS
                        失効確認
                        (確認する:1、確認しない:0)
   Date         : 2017/05/31
   Author       : Wataru Ito
*/

-- 終了コード用の変数設定
var ERROR_CODE number;
execute :ERROR_CODE:=0;

declare

    -- 対象テーブルの存在を確認変数設定
    TABLE_CNT number;

    -- 対象テーブルの統計情報の失効/有効確認変数設定
    TBL_STALE_CNT number;

    -- 例外定義名設定
    NO_EXECUTE_UPDATE exception;
    NOT_FOUND_TABLE_ERROR exception;
    UPDATE_STATISTICS_ERROR exception;

begin

    -- 対象テーブルの存在を確認する
    select count(OWNER) into TABLE_CNT from DBA_TABLES where OWNER = upper('&1') and TABLE_NAME = upper('&2');

    -- 取得した件数が1以外の場合
    if TABLE_CNT <> 1 then
        raise NOT_FOUND_TABLE_ERROR;
    end if;

    -- 失効確認フラグ 確認する(1)
    if &5 = 1 then
        DBMS_STATS.FLUSH_DATABASE_MONITORING_INFO;
        select count(*) into TBL_STALE_CNT from DBA_TAB_STATISTICS where OWNER = upper('&1') and TABLE_NAME = upper('&2') and OBJECT_TYPE in ('TABLE','PARTITION') and STALE_STATS = 'YES';
        if TBL_STALE_CNT = 0 then   -- YESが0件(すべてNO) 統計情報が有効 失効していない
            raise NO_EXECUTE_UPDATE;
        end if;
    end if;

    begin
        -- 統計情報更新
        dbms_stats.gather_table_stats(ownname =>'&1',tabname =>'&2',method_opt =>'FOR ALL INDEXED',estimate_percent =>&3,degree =>&4,cascade =>TRUE,force =>TRUE,granularity =>'ALL');

    -- 例外処理
    exception
        when OTHERS then
            raise UPDATE_STATISTICS_ERROR;
    end;

    -- 正常終了戻り値設定
    :ERROR_CODE:=0;
    
-- 例外処理
exception

    when NO_EXECUTE_UPDATE then
        :ERROR_CODE:=2;
    when NOT_FOUND_TABLE_ERROR then
        :ERROR_CODE:=3;
    when UPDATE_STATISTICS_ERROR then
        :ERROR_CODE:=4;
    when OTHERS then
        :ERROR_CODE:=5;
        dbms_output.put_line('[' || SQLCODE || ']:' || SQLERRM);

end;
/

exit :ERROR_CODE;
