--------------------------------------------------------------------------------
-- データクリーニングストアドプロシージャ
--------------------------------------------------------------------------------
create or replace procedure opal_apldba.DATA_CLEAN(
    tableName in varchar2,          -- テーブル名
    conditionCode in varchar2,      -- 削除条件コード
    commitCount in number,          -- コミット件数
    maxDeleteCount in number,       -- 最大削除件数
    deleteCount out number          -- 削除件数
)
AUTHID CURRENT_USER
is
    type rowidListType is table of rowid index by binary_integer;
    rowidList rowidListType;        -- 削除対象レコード検索の結果セット（ROWIDのリスト）
    selectSQL varchar2(1000);       -- 削除対象レコード検索SQL
    deleteSQL varchar2(1000);       -- 削除処理実行SQL

    condition varchar2(1000);       -- 削除条件
    executeCount number;            -- 削除処理実行件数
    type targetCursorType is ref cursor;
    targetCursor targetCursorType;  -- 削除対象レコード検索カーソル

begin

    -- 削除条件設定
    case conditionCode
        when 'B1' then condition := 'DELETED_FLG = ''1''';
        when 'B2' then condition := 'DELETED_FLG = ''1'' and DELETED_DATE <= ' || to_char( sysdate, 'YYYYMMDD');
        when 'B3' then condition := 'DELETED_DATE <= ' || to_char( sysdate, 'YYYYMMDD');
    end case;

    -- 削除対象レコード検索SQL取得
    selectSQL := 'select rowid from ' || tableName 
            || ' where ' || condition
            || ' and rownum <= ' || maxDeleteCount;
    
    -- 削除処理実行SQL取得
    deleteSQL := 'delete from ' || tableName || ' where rowid = :1';

    executeCount := 0;
    deleteCount := 0;

    open targetCursor for selectSQL;

    <<FETCH_LOOP>>
    loop
        -- 削除対象レコードをコミット件数分取得
        fetch targetCursor bulk collect into rowidList
            limit commitCount;

        exit when rowidList.count = 0;

        -- 削除処理実行
        forall i in  rowidList.first..rowidList.last
            execute immediate deleteSQL using rowidList(i);
        commit;

        -- 削除処理実行件数取得
        executeCount := executeCount + rowidList.count;

    end loop FETCH_LOOP;

    commit;
    close targetCursor;

    deleteCount := executeCount;

end;
/
GRANT EXECUTE ON opal_apldba.DATA_CLEAN TO role_apl
/
CREATE PUBLIC SYNONYM DATA_CLEAN FOR opal_apldba.DATA_CLEAN
/
