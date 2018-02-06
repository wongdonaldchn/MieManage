------------------------------------------------
-- ログインID・パスワード再登録API
------------------------------------------------
-- ログイン情報再登録一時情報取得
SELECT_LOGIN_INFO_REREGIST_TEMP_INFO=
SELECT
    LOGIN.APPLICATION_MEMBER_ID,                                             -- アプリ会員ID
    LOGIN.LOGIN_ID,                                                          -- ログインID
    LOGIN.PASSWORD,                                                          -- パスワード
    LOGIN.PASSWORD_SALT,                                                     -- パスワードSALT
    LOGIN.STRETCHING_TIMES,                                                  -- ストレッチング回数
    APL.MAIL_DELIVER_STATUS_DIVISION,                                        -- メール配信状態区分
    APL.MAIL_ADDRESS                                                         -- メールアドレス
FROM
    LOGIN_INFO_REREGIST_TEMP_INFO LOGIN                                     -- ログイン情報再登録一時情報
INNER JOIN
    APL_MEM_INFO APL                                                        -- アプリ会員情報
ON
    APL.APPLICATION_MEMBER_ID = LOGIN.APPLICATION_MEMBER_ID                 -- アプリ会員情報TBL.アプリ会員ID = ログイン情報再登録一時情報TBL.アプリ会員ID
WHERE
    LOGIN.LOGIN_INFO_REREGIST_RCPT_ID = :loginInfoReregistRcptId             -- ログイン情報再登録一時情報TBL.ログイン情報再登録受付ID = 2.2.2で取得されたメールアドレス認証キー
    AND LOGIN.PROCESSED_FLAG = '0'                                           -- ログイン情報再登録一時情報TBL.処理済フラグ = "0"(未処理)
    AND LOGIN.DELETED_FLG = '0'                                              -- ログイン情報再登録一時情報TBL.削除済フラグ = "0"(未削除)
    AND APL.APPLICATION_MEMBER_STATUS_CODE IN (:statusA,:statusD)            -- アプリ会員状態コード
    AND APL.DELETED_FLG = '0'                                                -- アプリ会員情報TBL.削除済フラグ = "0"(未削除)

-- ログイン情報再登録一時情報更新
UPDATE_LOGIN_INFO_REREGIST_TEMP_INFO=
UPDATE
    LOGIN_INFO_REREGIST_TEMP_INFO                                      -- ログイン情報再登録一時情報
SET
    PROCESSED_FLAG = :processedFlag,                                   -- 処理済フラグ
    UPDATE_USER_ID = :updateUserId,                                    -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime                                 -- 最終更新日時
WHERE
    LOGIN_INFO_REREGIST_RCPT_ID = :loginInfoReregistRcptId             -- アプリ会員一時情報TBL.アプリ会員登録受付ID = 2.3.4.1)で取得したメールアドレス認証キー

-- アプリ会員情報更新（ログインID・パスワード再登録）
UPDATE_APL_MEM_INFO=
UPDATE
    APL_MEM_INFO                                                       -- アプリ会員情報
SET
    LOGIN_ID = :loginId,                                               -- ログインID
    PASSWORD = :password,                                              -- パスワード
    PASSWORD_SALT = :passwordSalt,                                     -- パスワードSALT
    STRETCHING_TIMES = :stretchingTimes,                               -- ストレッチング回数
    UPDATE_USER_ID = :updateUserId,                                    -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,                                -- 最終更新日時
    VERSION = VERSION + 1                                              -- バージョン番号
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                       -- アプリ会員ID
    AND APPLICATION_MEMBER_STATUS_CODE IN (:statusA,:statusD)         -- アプリ会員状態コード
    AND DELETED_FLG = :deletedFlg                                     -- 削除フラグ
