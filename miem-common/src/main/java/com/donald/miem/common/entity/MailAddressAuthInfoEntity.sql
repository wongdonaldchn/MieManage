------------------------------------------------------------------------------------------------------
--メールアドレス認証情報排他制御用SQL
------------------------------------------------------------------------------------------------------
SELECT_MAIL_ADDRESS_AUTH_INFO_LOGIN =
SELECT
    MAIL_ADDRESS_AUTH_CODE,                       -- メールアドレス認証コード
    MAIL_ADDRESS_AUTH_KEY,                        -- メールアドレス認証キー
    MAIL_ADDRESS_AUTH_SALT,                       -- メールアドレス認証SALT
    MAIL_ADDRESS_AUTH_EXPI_DATE,                  -- メールアドレス認証有効期限
    PROCESS_DIVISION,                             -- 処理区分
    MAIL_ADDRESS,                                 -- メールアドレス
    PROCESSED_FLAG,                               -- 処理済フラグ
    INSERT_USER_ID,                               -- 登録者ID
    INSERT_DATE_TIME,                             -- 登録日時
    UPDATE_USER_ID,                               -- 最終更新者ID
    UPDATE_DATE_TIME,                             -- 最終更新日時
    DELETED_FLG,                                  -- 削除フラグ
    DELETED_DATE,                                 -- 論理削除日
    VERSION                                       -- バージョン番号
FROM
    MAIL_ADDRESS_AUTH_INFO                        -- メールアドレス認証情報
WHERE
    MAIL_ADDRESS_AUTH_CODE = :mailAddressAuthCode -- メールアドレス認証コード
    AND DELETED_FLG = '0'                         -- 削除フラグ(0:未削除)
    AND PROCESS_DIVISION = :processDivision       -- 処理区分
FOR UPDATE

------------------------------------------------------------------------------------------------------
--メールアドレス認証情報排他制御用SQL
------------------------------------------------------------------------------------------------------
SELECT_MAIL_ADDRESS_AUTH_INFO_LOCK_BY_KEY =
SELECT
    MAIL_ADDRESS_AUTH_CODE,                       -- メールアドレス認証コード
    MAIL_ADDRESS_AUTH_KEY,                        -- メールアドレス認証キー
    MAIL_ADDRESS_AUTH_SALT,                       -- メールアドレス認証SALT
    MAIL_ADDRESS_AUTH_EXPI_DATE,                  -- メールアドレス認証有効期限
    PROCESS_DIVISION,                             -- 処理区分
    MAIL_ADDRESS,                                 -- メールアドレス
    PROCESSED_FLAG,                               -- 処理済フラグ
    INSERT_USER_ID,                               -- 登録者ID
    INSERT_DATE_TIME,                             -- 登録日時
    UPDATE_USER_ID,                               -- 最終更新者ID
    UPDATE_DATE_TIME,                             -- 最終更新日時
    DELETED_FLG,                                  -- 削除フラグ
    DELETED_DATE,                                 -- 論理削除日
    VERSION                                       -- バージョン番号
FROM
    MAIL_ADDRESS_AUTH_INFO                        -- メールアドレス認証情報
WHERE
    MAIL_ADDRESS_AUTH_KEY = :mailAddressAuthKey   -- メールアドレス認証キー
    AND PROCESSED_FLAG = '0'                      -- 処理済フラグ(0:未処理)
    AND DELETED_FLG = '0'                         -- 削除フラグ(0:未削除)
FOR UPDATE