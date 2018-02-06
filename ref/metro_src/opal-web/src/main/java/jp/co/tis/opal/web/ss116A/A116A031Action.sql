------------------------------------------------
-- ログインID・パスワード一時登録API
------------------------------------------------
-- アプリ会員情報
SELECT_APL_MEM_INFO=
SELECT
    APPLICATION_MEMBER_ID,                                    -- アプリ会員ID
    MAIL_DELIVER_STATUS_DIVISION,                             -- メール配信状態区分
    MAIL_ADDRESS                                              -- メールアドレス
FROM
    APL_MEM_INFO                                              -- アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId              -- アプリ会員情報TBL.アプリ会員ID = ログインID・パスワード一時登録要求電文.アプリ会員ID
    AND DELETED_FLG = '0'                                     -- アプリ会員情報TBL.削除フラグ = "0"(未削除)
    AND APPLICATION_MEMBER_STATUS_CODE IN (:statusA,:statusD) --アプリ会員情報TBL.アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員),"D"(OP非会員))

-- アプリ会員情報・ログインID
SELECT_APL_MEM_INFO_LOGIN_ID=
SELECT
    APPLICATION_MEMBER_ID                                     -- アプリ会員ID
FROM
    APL_MEM_INFO                                              -- アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID <> :applicationMemberId             -- アプリ会員情報TBL.アプリ会員ID <> ログインID・パスワード一時登録要求電文.アプリ会員ID
    AND LOGIN_ID = :loginId                                   -- アプリ会員情報TBL.ログインID = ログインID・パスワード一時登録要求電文.ログインID

-- アプリ会員一時情報
SELECT_APL_MEM_TEMP_INFO=
SELECT
    APL.APL_MEM_REGIST_RCPT_ID                                -- アプリ会員登録受付ID
FROM
    APL_MEM_TEMP_INFO APL                                     -- アプリ会員一時情報
INNER JOIN
    MAIL_ADDRESS_AUTH_INFO MAIL                               -- メールアドレス認証情報
ON
    APL.APL_MEM_REGIST_RCPT_ID = MAIL.MAIL_ADDRESS_AUTH_KEY   -- アプリ会員一時情報TBL.アプリ会員登録受付ID = メールアドレス認証情報TBL.メールアドレス認証キー
WHERE
    APL.LOGIN_ID = :loginId                                   -- アプリ会員一時情報TBL.ログインID = ログインID・パスワード一時登録要求電文.ログインID
    AND APL.DELETED_FLG = '0'                                 -- アプリ会員一時情報TBL.削除フラグ = "0"(未削除)
    AND APL.PROCESSED_FLAG = '0'                              -- アプリ会員一時情報TBL.処理済フラグ = "0"(未処理)
    AND MAIL.MAIL_ADDRESS_AUTH_EXPI_DATE >= :sysDateTime      -- メールアドレス認証情報TBL.メールアドレス認証有効期限 ≧ システム日時
    AND MAIL.PROCESS_DIVISION = :procseeDivision              -- メールアドレス認証情報TBL.処理区分 = "1"(アプリ会員登録)
    AND MAIL.PROCESSED_FLAG = '0'                             -- メールアドレス認証情報TBL.処理済フラグ = "0"(未処理)
    AND MAIL.DELETED_FLG = '0'                                -- メールアドレス認証情報TBL.削除フラグ = "0"(未削除)

-- ログイン情報再登録一時情報
SELECT_LOGIN_INFO_REREGIST_TEMP_INFO=
SELECT
    LOGIN.APPLICATION_MEMBER_ID                                        -- アプリ会員ID
FROM
    LOGIN_INFO_REREGIST_TEMP_INFO LOGIN                                -- ログイン情報再登録一時情報
INNER JOIN
    MAIL_ADDRESS_AUTH_INFO MAIL                                        -- メールアドレス認証情報
ON
    LOGIN.LOGIN_INFO_REREGIST_RCPT_ID = MAIL.MAIL_ADDRESS_AUTH_KEY     -- ログイン情報再登録一時情報TBL.ログイン情報再登録受付ID = メールアドレス認証情報TBL.メールアドレス認証キー
WHERE
    LOGIN.LOGIN_ID = :loginId                                          -- ログイン情報再登録一時情報TBL.ログインID = ログインID・パスワード一時登録要求電文.ログインID
    AND LOGIN.DELETED_FLG = '0'                                        -- ログイン情報再登録一時情報TBL.削除フラグ = "0"(未削除)
    AND LOGIN.PROCESSED_FLAG = '0'                                     -- ログイン情報再登録一時情報TBL.処理済フラグ = "0"(未処理)
    AND MAIL.MAIL_ADDRESS_AUTH_EXPI_DATE >= :sysDateTime               -- メールアドレス認証情報TBL.メールアドレス認証有効期限 ≧ システム日時
    AND MAIL.PROCESS_DIVISION = :procseeDivision                       -- メールアドレス認証情報TBL.処理区分 = "2"(ログインID・パスワード再登録)
    AND MAIL.PROCESSED_FLAG = '0'                                      -- メールアドレス認証情報TBL.処理済フラグ = "0"(未処理)
    AND MAIL.DELETED_FLG = '0'                                         -- メールアドレス認証情報TBL.削除フラグ = "0"(未削除)

-- ログイン情報再登録一時情報登録
INSERT_LOGIN_INFO_REREGIST_TEMP_INFO=
INSERT INTO
    LOGIN_INFO_REREGIST_TEMP_INFO                             -- ログイン情報再登録一時情報
    (
    LOGIN_INFO_REREGIST_RCPT_ID,                              -- ログイン情報再登録受付ID
    APPLICATION_MEMBER_ID,                                    -- アプリ会員ID
    LOGIN_ID,                                                 -- ログインID
    PASSWORD,                                                 -- パスワード
    PASSWORD_SALT,                                            -- パスワードSALT
    STRETCHING_TIMES,                                         -- ストレッチング回数
    PROCESSED_FLAG,                                           -- 処理済フラグ
    INSERT_USER_ID,                                           -- 登録者ID
    INSERT_DATE_TIME,                                         -- 登録日時
    UPDATE_USER_ID,                                           -- 最終更新者ID
    UPDATE_DATE_TIME,                                         -- 最終更新日時
    DELETED_FLG,                                              -- 削除フラグ
    DELETED_DATE                                              -- 論理削除日
    )
VALUES
    (
    :loginInfoReregistRcptId,                                 -- ログイン情報再登録受付ID
    :applicationMemberId,                                     -- アプリ会員ID
    :loginId,                                                 -- ログインID
    :password,                                                -- パスワード
    :passwordSalt,                                            -- パスワードSALT
    :stretchingTimes,                                         -- ストレッチング回数
    :processedFlag,                                           -- 処理済フラグ
    :insertUserId,                                            -- 登録者ID
    :insertDateTime,                                          -- 登録日時
    :updateUserId,                                            -- 最終更新者ID
    :updateDateTime,                                          -- 最終更新日時
    :deletedFlg,                                              -- 削除フラグ
    :deletedDate                                              -- 論理削除日
    )

-- メールアドレス認証情報登録
INSERT_MAIL_ADDRESS_AUTH_INFO=
INSERT INTO
    MAIL_ADDRESS_AUTH_INFO                                    -- メールアドレス認証情報
    (
    MAIL_ADDRESS_AUTH_CODE,                                   -- メールアドレス認証コード
    MAIL_ADDRESS_AUTH_KEY,                                    -- メールアドレス認証キー
    MAIL_ADDRESS_AUTH_SALT,                                   -- メールアドレス認証SALT
    MAIL_ADDRESS_AUTH_EXPI_DATE,                              -- メールアドレス認証有効期限
    PROCESS_DIVISION,                                         -- 処理区分
    MAIL_ADDRESS,                                             -- メールアドレス
    PROCESSED_FLAG,                                           -- 処理済フラグ
    INSERT_USER_ID,                                           -- 登録者ID
    INSERT_DATE_TIME,                                         -- 登録日時
    UPDATE_USER_ID,                                           -- 最終更新者ID
    UPDATE_DATE_TIME,                                         -- 最終更新日時
    DELETED_FLG,                                              -- 削除フラグ
    DELETED_DATE,                                             -- 論理削除日
    VERSION                                                   -- バージョン番号
    )
VALUES
    (
    :mailAddressAuthCode,                                     -- メールアドレス認証コード
    :mailAddressAuthKey,                                      -- メールアドレス認証キー
    :mailAddressAuthSalt,                                     -- メールアドレス認証SALT
    :mailAddressAuthExpiDate,                                 -- メールアドレス認証有効期限
    :processDivision,                                         -- 処理区分
    :mailAddress,                                             -- メールアドレス
    :processedFlag,                                           -- 処理済フラグ
    :insertUserId,                                            -- 登録者ID
    :insertDateTime,                                          -- 登録日時
    :updateUserId,                                            -- 最終更新者ID
    :updateDateTime,                                          -- 最終更新日時
    :deletedFlg,                                              -- 削除フラグ
    :deletedDate,                                             -- 論理削除日
    :version                                                  -- バージョン番号
    )