------------------------------------------------
-- マイル加算API(A134A011Action)
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