------------------------------------------------
-- 郵送情報登録API
------------------------------------------------
-- アプリ会員情報を取得する。
SELECT_APL_MEM_INFO =
SELECT
    APPLICATION_ID                                    --アプリID
FROM
    APL_MEM_INFO                                      --アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId      --アプリ会員ID
    --アプリ会員情報TBL.アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員), "D"(OP非会員))
    AND APPLICATION_MEMBER_STATUS_CODE IN (:opAuthAplMem, :notOpMem)
    AND DELETED_FLG = '0'                            --削除フラグ(0:未削除)

-- マイル残高情報TBLにマイル残高合計を取得する。
SELECT_MILE_BALANCE_SUM =
SELECT
    SUM(MILE_BALANCE) SUM_MILE_BALANCE              --マイル残高合計
FROM
    MILE_BALANCE_INFO                               --マイル残高情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId    --アプリ会員ID
    AND OBJECT_YEAR_MONTH >= :objectYearMonth       --対象年月
    AND MILE_BALANCE > 0                            --マイル残高
    AND DELETED_FLG = '0'                           --削除フラグ(0:未削除)

-- 郵送情報登録
INSERT_POST_INFORMATION =
INSERT INTO
    POST_INFORMATION                        -- 郵送情報
    (
    POST_RECEIPT_ID,                        -- 郵送受付ID
    APPLICATION_MEMBER_ID,                  -- アプリ会員ID
    APPLICATION_ID,                         -- アプリID
    POST_RECEIPT_REGIST_DATE_TIME,          -- 郵送受付登録日時
    POST_CATEGORY,                          -- 郵送種別
    POST_CONTROL_NUMBER,                    -- 郵送管理番号
    POSTCODE,                               -- 郵便番号
    ADDRESS,                                -- 住所
    NAME,                                   -- 氏名
    TELEPHONE_NUMBER,                       -- 電話番号
    OTHER,                                  -- その他
    INSERT_USER_ID,                         -- 登録者ID
    INSERT_DATE_TIME,                       -- 登録日時
    UPDATE_USER_ID,                         -- 最終更新者ID
    UPDATE_DATE_TIME,                       -- 最終更新日時
    DELETED_FLG,                            -- 削除フラグ
    DELETED_DATE                            -- 論理削除日
    )
VALUES
    (
    :postReceiptId,                         -- 郵送受付ID
    :applicationMemberId,                   -- アプリ会員ID
    :applicationId,                         -- アプリID
    :postReceiptRegistDateTime,             -- 郵送受付登録日時
    :postCategory,                          -- 郵送種別
    :postControlNumber,                     -- 郵送管理番号
    :postcode,                              -- 郵便番号
    :address,                               -- 住所
    :name,                                  -- 氏名
    :telephoneNumber,                       -- 電話番号
    :other,                                 -- その他
    :insertUserId,                          -- 登録者ID
    :insertDateTime,                        -- 登録日時
    :updateUserId,                          -- 最終更新者ID
    :updateDateTime,                        -- 最終更新日時
    :deletedFlg,                            -- 削除フラグ(0:未削除)
    :deletedDate                            -- 論理削除日
    )