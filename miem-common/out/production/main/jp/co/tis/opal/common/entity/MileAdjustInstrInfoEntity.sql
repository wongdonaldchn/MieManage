------------------------------------------------------------------------------------------------------
--マイル調整指示情報排他制御用SQL
------------------------------------------------------------------------------------------------------
SELECT_MILE_ADJUST_INSTR_INFO =
SELECT
    MILE_ADJUST_INSTR_ID,                             --マイル調整指示ID
    APPLICATION_MEMBER_ID,                            --アプリ会員ID
    ADJUST_MILE_AMOUNT,                               --調整マイル数
    MILE_ADJUST_INSTR_DATE,                           --マイル調整指示日
    MILE_CATEGORY_CODE,                               --マイル種別コード
    MILE_ADJUST_STATUS_DIVI,                          --マイル調整状況区分
    INSERT_USER_ID,                                   --登録者ID
    INSERT_DATE_TIME,                                 --登録日時
    UPDATE_USER_ID,                                   --最終更新者ID
    UPDATE_DATE_TIME,                                 --最終更新日時
    DELETED_FLG,                                      --削除フラグ
    DELETED_DATE,                                     --論理削除日
    VERSION                                           --バージョン番号
FROM
    MILE_ADJUST_INSTR_INFO                            --マイル調整指示情報
WHERE
    MILE_ADJUST_INSTR_ID = :mileAdjustInstrId         --マイル調整指示ID
FOR UPDATE
