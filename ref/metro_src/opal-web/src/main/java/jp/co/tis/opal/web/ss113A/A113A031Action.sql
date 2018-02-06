------------------------------------------------
-- メールアドレス一時変更API(A113A031Action)
------------------------------------------------
-- アプリ会員情報にアプリ会員IDの存在チェック
SELECT_APL_MEM_INFO =
SELECT
    APPLICATION_MEMBER_ID                                         -- アプリ会員ID
FROM
    APL_MEM_INFO                                                  --アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                  --アプリ会員ID
    AND DELETED_FLG = '0'                                         --削除フラグ(0:未削除)
    AND APPLICATION_MEMBER_STATUS_CODE IN (:statusA,:statusD)     --アプリ会員情報TBL.アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員),"D"(OP非会員))

-- アプリ会員情報にメールアドレスの存在チェック
SELECT_APPLICATION_MEMBER_ID =
SELECT
    APPLICATION_MEMBER_ID                                -- アプリ会員ID
FROM
    APL_MEM_INFO                                         -- アプリ会員情報
WHERE
    MAIL_ADDRESS = :mailAddress                          -- メールアドレス
    AND DELETED_FLG = '0'                                -- 削除フラグ(0:未削除)

-- アプリ会員一時情報にメールアドレスの存在チェック
SELECT_APL_MEM_TEMP_INFO =
SELECT
    APL.APL_MEM_REGIST_RCPT_ID                               -- アプリ会員登録受付ID
FROM
    APL_MEM_TEMP_INFO APL                                    -- アプリ会員一時情報
INNER JOIN
    MAIL_ADDRESS_AUTH_INFO MAIL                              -- メールアドレス認証情報
ON
    APL.APL_MEM_REGIST_RCPT_ID = MAIL.MAIL_ADDRESS_AUTH_KEY  -- アプリ会員一時情報TBL.アプリ会員登録受付ID = メールアドレス認証情報TBL.メールアドレス認証キー
WHERE
    APL.MAIL_ADDRESS = :mailAddress                          -- アプリ会員一時情報TBL.メールアドレス = メールアドレス一時変更要求電文.メールアドレス
    AND APL.DELETED_FLG = '0'                                -- アプリ会員一時情報TBL.削除フラグ = "0"(未削除)
    AND APL.PROCESSED_FLAG = '0'                             -- アプリ会員一時情報TBL.処理済フラグ = "0"(未処理)
    AND MAIL.MAIL_ADDRESS_AUTH_EXPI_DATE >= :sysDateTime     -- メールアドレス認証情報TBL.メールアドレス認証有効期限 ≧ システム日時
    AND MAIL.PROCESS_DIVISION = :procseeDivision             -- メールアドレス認証情報TBL.処理区分 = "1"(アプリ会員登録)
    AND MAIL.PROCESSED_FLAG = '0'                            -- メールアドレス認証情報TBL.処理済フラグ = "0"(未処理)
    AND MAIL.DELETED_FLG = '0'                               -- メールアドレス認証情報TBL.削除フラグ = "0"(未削除)

-- メールアドレス変更一時情報にメールアドレスの存在チェック
SELECT_MAIL_ADDRESS_CHANGE_TEMP_INFO =
SELECT
    CHANGE.MAIL_ADDRESS_CHANGE_RCPT_ID                                     -- メールアドレス変更受付ID
FROM
    MAIL_ADDRESS_CHANGE_TEMP_INFO CHANGE                                   -- メールアドレス変更一時情報
INNER JOIN
    MAIL_ADDRESS_AUTH_INFO AUTH                                            -- メールアドレス認証情報
ON
    CHANGE.MAIL_ADDRESS_CHANGE_RCPT_ID = AUTH.MAIL_ADDRESS_AUTH_KEY
WHERE
    CHANGE.MAIL_ADDRESS = :mailAddress                                     -- メールアドレス変更一時情報TBL.メールアドレス = メールアドレス一時変更要求電文.メールアドレス
    AND CHANGE.DELETED_FLG = '0'                                           -- メールアドレス変更一時情報TBL.削除フラグ = "0"(未削除)
    AND CHANGE.PROCESSED_FLAG = '0'                                        -- メールアドレス変更一時情報TBL.処理済フラグ = "0"(未処理)
    AND AUTH.MAIL_ADDRESS_AUTH_EXPI_DATE >= :sysDateTime                   -- メールアドレス認証情報TBL.メールアドレス認証有効期限 ≧ システム日時
    AND AUTH.PROCESS_DIVISION = :procseeDivision                           -- メールアドレス認証情報TBL.処理区分 = "3"(アプリ会員登録)
    AND AUTH.PROCESSED_FLAG = '0'                                          -- メールアドレス認証情報TBL.処理済フラグ = "0"(未処理)
    AND AUTH.DELETED_FLG = '0'                                             -- メールアドレス認証情報TBL.削除フラグ = "0"(未削除)

-- メールアドレス変更一時情報登録
INSERT_MAIL_ADDRESS_CHANGE_TEMP_INFO =
INSERT INTO
    MAIL_ADDRESS_CHANGE_TEMP_INFO                        -- メールアドレス変更一時情報
    (
    MAIL_ADDRESS_CHANGE_RCPT_ID,                         -- メールアドレス変更受付ID
    APPLICATION_MEMBER_ID,                               -- アプリ会員ID
    MAIL_ADDRESS,                                        -- メールアドレス
    PROCESSED_FLAG,                                      -- 処理済フラグ
    INSERT_USER_ID,                                      -- 登録者ID
    INSERT_DATE_TIME,                                    -- 登録日時
    UPDATE_USER_ID,                                      -- 最終更新者ID
    UPDATE_DATE_TIME,                                    -- 最終更新日時
    DELETED_FLG,                                         -- 削除フラグ
    DELETED_DATE                                         -- 論理削除日
    )
VALUES
    (
    :mailAddressChangeRcptId,                            -- メールアドレス変更受付ID
    :applicationMemberId,                                -- アプリ会員ID
    :mailAddress,                                        -- メールアドレス
    :processedFlag,                                      -- 処理済フラグ
    :insertUserId,                                       -- 登録者ID
    :insertDateTime,                                     -- 登録日時
    :updateUserId,                                       -- 最終更新者ID
    :updateDateTime,                                     -- 最終更新日時
    :deletedFlg,                                         -- 削除フラグ
    :deletedDate                                         -- 論理削除日
    )

-- メールアドレス認証情報登録
INSERT_MAIL_ADDRESS_AUTH_INFO=
INSERT INTO
    MAIL_ADDRESS_AUTH_INFO                               -- メールアドレス認証情報
    (
    MAIL_ADDRESS_AUTH_CODE,                              -- メールアドレス認証コード
    MAIL_ADDRESS_AUTH_KEY,                               -- メールアドレス認証キー
    MAIL_ADDRESS_AUTH_SALT,                              -- メールアドレス認証SALT
    MAIL_ADDRESS_AUTH_EXPI_DATE,                         -- メールアドレス認証有効期限
    PROCESS_DIVISION,                                    -- 処理区分
    MAIL_ADDRESS,                                        -- メールアドレス
    PROCESSED_FLAG,                                      -- 処理済フラグ
    INSERT_USER_ID,                                      -- 登録者ID
    INSERT_DATE_TIME,                                    -- 登録日時
    UPDATE_USER_ID,                                      -- 最終更新者ID
    UPDATE_DATE_TIME,                                    -- 最終更新日時
    DELETED_FLG,                                         -- 削除フラグ
    DELETED_DATE,                                        -- 論理削除日
    VERSION                                              -- バージョン番号
    )
VALUES
    (
    :mailAddressAuthCode,                                -- メールアドレス認証コード
    :mailAddressAuthKey,                                 -- メールアドレス認証キー
    :mailAddressAuthSalt,                                -- メールアドレス認証SALT
    :mailAddressAuthExpiDate,                            -- メールアドレス認証有効期限
    :processDivision,                                    -- 処理区分
    :mailAddress,                                        -- メールアドレス
    :processedFlag,                                      -- 処理済フラグ
    :insertUserId,                                       -- 登録者ID
    :insertDateTime,                                     -- 登録日時
    :updateUserId,                                       -- 最終更新者ID
    :updateDateTime,                                     -- 最終更新日時
    :deletedFlg,                                         -- 削除フラグ
    :deletedDate,                                        -- 論理削除日
    :version                                             -- バージョン番号
    )