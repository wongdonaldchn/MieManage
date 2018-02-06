------------------------------------------------
-- メールアドレス変更API(A113A041Action)
------------------------------------------------
-- メールアドレス変更一時情報取得
SELECT_MAIL_ADDRESS_CHANGE_TEMP_INFO =
SELECT
    MAIL.APPLICATION_MEMBER_ID,                                      -- アプリ会員ID
    MAIL.MAIL_ADDRESS                                                -- メールアドレス
FROM
    MAIL_ADDRESS_CHANGE_TEMP_INFO MAIL                               -- メールアドレス変更一時情報
INNER JOIN
    APL_MEM_INFO APL                                                 -- アプリ会員情報
ON
    APL.APPLICATION_MEMBER_ID = MAIL.APPLICATION_MEMBER_ID           -- メールアドレス変更一時情報TBL.アプリ会員ID = アプリ会員情報TBL.アプリ会員ID
WHERE
    MAIL.MAIL_ADDRESS_CHANGE_RCPT_ID = :mailAddressAuthKey           -- メールアドレス変更一時情報TBL.メールアドレス変更受付ID = 取得されたメールアドレス認証キー
    AND MAIL.PROCESSED_FLAG = '0'                                    -- メールアドレス変更一時情報TBL.処理済フラグ = "0"(未処理)
    AND MAIL.DELETED_FLG = '0'                                       -- メールアドレス変更一時情報TBL.削除済フラグ = "0"(未削除)
    AND APL.APPLICATION_MEMBER_STATUS_CODE IN (:statusA,:statusD)    -- アプリ会員情報TBL.アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員),"D"(OP非会員))
    AND APL.DELETED_FLG = '0'                                        -- アプリ会員情報TBL.削除フラグ = "0"(未削除)

-- アプリ会員情報更新（メールアドレス変更）
UPDATE_APL_MEM_INFO =
UPDATE
    APL_MEM_INFO                                                     -- アプリ会員情報
SET
    MAIL_ADDRESS = :mailAddress,                                     -- メールアドレス
    MAIL_DELIVER_STATUS_DIVISION = :mailDeliverStatusDivision,       -- メール配信状態区分
    UPDATE_USER_ID = :updateUserId,                                  -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,                              -- 最終更新日時
    VERSION = VERSION + 1                                            -- バージョン番号
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                     -- アプリ会員情報TBL.アプリ会員ID = 取得されたアプリ会員ID
    AND DELETED_FLG = :deletedFlg                                    -- アプリ会員情報TBL.削除フラグ = "0"(未削除)
    AND APPLICATION_MEMBER_STATUS_CODE IN (:statusA,:statusD)        -- アプリ会員状態コード

-- メールアドレス変更一時情報更新
UPDATE_MAIL_ADDRESS_CHANGE_TEMP_INFO =
UPDATE
    MAIL_ADDRESS_CHANGE_TEMP_INFO                                    -- メールアドレス変更一時情報
SET
    PROCESSED_FLAG = :processedFlag,                                 -- 処理済フラグ
    UPDATE_USER_ID = :updateUserId,                                  -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime                               -- 最終更新日時
WHERE
    MAIL_ADDRESS_CHANGE_RCPT_ID = :mailAddressAuthKey                -- メールアドレス変更一時情報TBL.メールアドレス変更受付ID = 取得したメールアドレス認証キー