------------------------------------------------
-- マイル履歴取得API
------------------------------------------------
-- アプリ会員情報取得
SELECT_APL_MEM_INFO=
SELECT
    APPLICATION_MEMBER_ID                                           -- アプリ会員ID
FROM
    APL_MEM_INFO                                                    -- アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                    -- アプリ会員情報TBL.アプリ会員ID = マイル履歴取得要求電文.アプリ会員ID
    AND DELETED_FLG = '0'                                           -- アプリ会員情報TBL.削除フラグ = "0"(未削除)
    AND APPLICATION_MEMBER_STATUS_CODE IN (:statusA,:statusD)       -- アプリ会員情報TBL.アプリ会員状態コード IN ( "A", "D")

-- マイル集計情報取得
SELECT_MILE_SUMMARY_INFO=
SELECT
    MILE_SUM_YEAR_MONTH,                                                -- マイル集計年月
    ACQUIRE_MILE_TOTAL,                                                 -- 獲得マイル・合計
    USE_MILE_TOTAL                                                      -- 使用マイル・合計
FROM
    MILE_SUMMARY_INFORMATION                                            -- マイル集計情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                        -- マイル集計情報TBL.アプリ会員ID = マイル履歴取得要求電文.アプリ会員ID
    AND DELETED_FLG = '0'                                               -- マイル集計情報TBL.削除フラグ = "0"(未削除)
    AND MILE_SUM_YEAR_MONTH BETWEEN :startYearMonth AND :endYearMonth -- マイル集計情報TBL.マイル集計年月 BETWEEN マイル履歴取得要求電文.照会開始年月 AND マイル履歴取得要求電文.照会終了年月
ORDER BY
    MILE_SUM_YEAR_MONTH ASC                                             -- マイル集計情報TBL.マイル集計年月 昇順

-- マイル種別集計情報取得
SELECT_MILE_CATEGORY_SUMMARY_INFO=
SELECT
    MILE_SUM_YEAR_MONTH,                                                -- マイル集計年月
    MILE_CATEGORY_CODE,                                                 -- マイル種別コード
    MILE_TOTAL                                                          -- マイル合計
FROM
    MILE_CATEGORY_SUM_INFO                                              -- マイル種別集計情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                        -- マイル種別集計情報TBL.アプリ会員ID = マイル履歴取得要求電文.アプリ会員ID
    AND MILE_SUM_YEAR_MONTH = :objectYearMonth                          -- マイル種別集計情報TBL.マイル集計年月 = マイル集計情報TBL.マイル集計年月
    AND DELETED_FLG = '0'                                               -- マイル種別集計情報TBL.削除フラグ = "0"(未削除)
ORDER BY
    MILE_SUM_YEAR_MONTH ASC,                                            -- マイル種別集計情報TBL.マイル集計年月 昇順
    MILE_CATEGORY_CODE ASC                                              -- マイル種別集計情報TBL.マイル種別コード 昇順