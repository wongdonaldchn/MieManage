------------------------------------------------
-- 郵送情報取得API
------------------------------------------------
-- アプリ会員情報取得
SELECT_APL_MEM_INFO =
SELECT
    APPLICATION_MEMBER_ID                                     --アプリ会員ID
FROM
    APL_MEM_INFO                                              --アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId              --郵送情報取得要求電文.アプリ会員ID
    -- アプリ会員情報TBL.アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員),"D"(OP非会員))
    AND APPLICATION_MEMBER_STATUS_CODE IN (:opAuthAplMem, :notOpMem)
    AND DELETED_FLG = '0'                                     --削除フラグ(0:未削除)

-- 郵送情報取得
SELECT_POST_INFO=
SELECT
    POSTCODE,                                            -- 郵便番号
    ADDRESS,                                             -- 住所
    NAME,                                                -- 氏名
    TELEPHONE_NUMBER                                     -- 電話番号
FROM
(
SELECT
    POSTCODE,                                            -- 郵便番号
    ADDRESS,                                             -- 住所
    NAME,                                                -- 氏名
    TELEPHONE_NUMBER                                     -- 電話番号
FROM
    POST_INFORMATION                                     -- 郵送情報TBL
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId         -- 郵送情報TBL.アプリ会員ID = 郵送情報取得要求電文.アプリ会員ID
    AND DELETED_FLG = '0'                               -- 郵送情報TBL.削除フラグ = "0"(未削除)
ORDER BY POST_RECEIPT_REGIST_DATE_TIME DESC            -- 郵送情報TBL.郵送受付登録日時 (降順)
)
WHERE
    ROWNUM = 1                                           -- ROWNUM = 1