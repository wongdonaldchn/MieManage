------------------------------------------------
-- マイル利用明細取得API
------------------------------------------------
-- アプリ会員情報取得
SELECT_APL_MEM_INFO=
SELECT
    APPLICATION_MEMBER_ID                                           -- アプリ会員ID
FROM
    APL_MEM_INFO                                                    -- アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                    -- アプリ会員情報TBL.アプリ会員ID = マイル利用明細取得要求電文.アプリ会員ID
    AND DELETED_FLG = '0'                                           -- アプリ会員情報TBL.削除フラグ = "0"(未削除)
    AND APPLICATION_MEMBER_STATUS_CODE IN (:statusA,:statusD)       -- アプリ会員情報TBL.アプリ会員状態コード IN ( "A", "D")

-- マイル履歴情報取得
SELECT_MILE_HISTORY_INFO=
SELECT
    MILE_HISTORY_REGIST_DATE,                                           -- マイル履歴登録日
    MILE_CATEGORY_CODE,                                                 -- マイル種別コード
    MILE_AMOUNT                                                         -- マイル数
FROM
    MILE_HISTORY_INFORMATION                                            -- マイル履歴情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                        -- マイル履歴情報TBL.アプリ会員ID = マイル利用明細取得要求電文.アプリ会員ID
    AND SUBSTR(MILE_HISTORY_REGIST_DATE,1,6) = :objectYearMonth         -- マイル履歴情報TBL.マイル履歴登録日の年月 = マイル利用明細取得要求電文.照会対象年月
    AND DELETED_FLG = '0'                                               -- マイル履歴情報TBL.削除フラグ = "0"(未削除)
ORDER BY
    MILE_HISTORY_ID ASC