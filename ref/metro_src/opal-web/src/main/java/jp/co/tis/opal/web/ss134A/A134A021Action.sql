------------------------------------------------
-- マイル減算API(A134A021Action)
------------------------------------------------

--アプリ会員情報取得用SQL
SELECT_APL_MEM_INFO =
SELECT
    APPLICATION_MEMBER_ID                                                    --アプリ会員ID
FROM
    APL_MEM_INFO                                                             --アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                             --アプリ会員ID
    AND APPLICATION_MEMBER_STATUS_CODE IN (:statusCodeA, :statusCodeD)      --アプリ会員状態コード
    AND DELETED_FLG = '0'                                                    --削除フラグ(0:未削除)

--マイル残高合計取得用SQL
SELECT_MILE_BALANCE_SUM =
SELECT
    SUM(MILE_BALANCE) SUM_MILE_BALANCE              --マイル残高合計
FROM
    MILE_BALANCE_INFO                               --マイル残高情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId    --アプリ会員ID
    AND OBJECT_YEAR_MONTH >= :objectYearMonth      --対象年月
    AND MILE_BALANCE > 0                            --マイル残高
    AND DELETED_FLG = '0'                           --削除フラグ(0:未削除)