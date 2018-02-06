------------------------------------------------------------------------------------------------------
--マイル残高情報排他制御用SQL
------------------------------------------------------------------------------------------------------
SELECT_MILE_BALANCE_INFO =
SELECT
    APPLICATION_MEMBER_ID,                          -- アプリ会員ID
    OBJECT_YEAR_MONTH,                              -- 対象年月
    MILE_BALANCE,                                   -- マイル残高
    INSERT_USER_ID,                                 -- 登録者ID
    INSERT_DATE_TIME,                               -- 登録日時
    UPDATE_USER_ID,                                 -- 最終更新者ID
    UPDATE_DATE_TIME,                               -- 最終更新日時
    DELETED_FLG,                                    -- 削除フラグ
    DELETED_DATE,                                   -- 論理削除日
    VERSION                                         -- バージョン番号
FROM
    MILE_BALANCE_INFO                               -- マイル残高情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId    -- アプリ会員ID
    AND DELETED_FLG = '0'                           -- 削除フラグ(0:未削除)
FOR UPDATE
