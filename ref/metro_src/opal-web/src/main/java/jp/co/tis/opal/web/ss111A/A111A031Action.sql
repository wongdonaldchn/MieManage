------------------------------------------------
-- アプリ会員一時登録API
------------------------------------------------
-- アプリ会員情報TBLにログインIDの存在チェック
SELECT_APL_MEM_INFO_BY_LOGIN_ID=
SELECT
    APPLICATION_MEMBER_ID                    -- アプリ会員情報TBL.アプリ会員ID
FROM
    APL_MEM_INFO                             -- アプリ会員情報
WHERE
    LOGIN_ID = :loginId                      -- アプリ会員情報TBL.ログインID = アプリ会員一時登録要求電文.ログインID

-- アプリ会員一時情報TBLにログインIDの存在チェック
SELECT_APL_MEM_TEMP_INFO_BY_LOGIN_ID=
SELECT
    APL_MEM_REGIST_RCPT_ID,                  -- アプリ会員一時情報TBL.アプリ会員登録受付ID
    PASSWORD,                                -- アプリ会員一時情報TBL.パスワード
    PASSWORD_SALT,                           -- アプリ会員一時情報TBL.パスワードSALT
    STRETCHING_TIMES                         -- アプリ会員一時情報TBL.ストレッチング回数
FROM
    APL_MEM_TEMP_INFO                        -- アプリ会員一時情報
WHERE
    LOGIN_ID = :loginId                      -- アプリ会員一時情報TBL.ログインID = アプリ会員一時登録要求電文.ログインID
    AND PROCESSED_FLAG = '0'                 -- アプリ会員一時情報TBL.処理フラグ = "0"(未処理)
    AND DELETED_FLG = '0'                    -- アプリ会員一時情報TBL.削除フラグ = "0"(未削除)

-- アプリ会員情報TBLにメールアドレスの存在チェック
SELECT_APPLICATION_MEMBER_ID=
SELECT
    APPLICATION_MEMBER_ID                    -- アプリ会員ID
FROM
    APL_MEM_INFO                             -- アプリ会員情報
WHERE
    MAIL_ADDRESS = :mailAddress              -- アプリ会員情報TBL.メールアドレス = アプリ会員一時登録要求電文.メールアドレス
    AND DELETED_FLG = '0'                    -- アプリ会員情報TBL.削除フラグ = "0"(未削除)

-- ログイン情報再登録一時情報TBLにログインIDの存在チェック
SELECT_LOGIN_INFO_REREGIST_TEMP_INFO=
SELECT
    LOGIN_INFO_REREGIST_RCPT_ID              -- ログイン情報再登録一時情報TBL.ログイン情報再登録受付ID
FROM
    LOGIN_INFO_REREGIST_TEMP_INFO            -- ログイン情報再登録一時情報
WHERE
    LOGIN_ID = :loginId                      -- ログイン情報再登録一時情報TBL.ログインID = アプリ会員一時登録要求電文.ログインID
    AND DELETED_FLG = '0'                    -- ログイン情報再登録一時情報TBL.削除フラグ = "0"(未削除)

-- アプリ会員一時情報TBLにメールアドレス存在チェック
SELECT_APL_MEM_TEMP_INFO=
SELECT
    APL.APL_MEM_REGIST_RCPT_ID                                -- アプリ会員登録受付ID
FROM
    APL_MEM_TEMP_INFO APL                                     -- アプリ会員一時
INNER JOIN
    MAIL_ADDRESS_AUTH_INFO MAIL                               -- メールアドレス認証情報
ON
    APL.APL_MEM_REGIST_RCPT_ID = MAIL.MAIL_ADDRESS_AUTH_KEY   -- アプリ会員一時情報TBL.アプリ会員登録受付ID = メールアドレス認証情報TBL.メールアドレス認証キー
WHERE
    APL.MAIL_ADDRESS = :mailAddress                           -- アプリ会員一時情報TBL.メールアドレス = アプリ会員一時登録要求電文.メールアドレス
    AND APL.DELETED_FLG = '0'                                 -- アプリ会員一時情報TBL.削除フラグ = "0"(未削除)
    AND APL.PROCESSED_FLAG = '0'                              -- アプリ会員一時情報TBL.処理済フラグ = "0"(未処理)
    AND MAIL.MAIL_ADDRESS_AUTH_EXPI_DATE >= :sysDateTime      -- メールアドレス認証情報TBL.メールアドレス認証有効期限 ≧ システム日時
    AND MAIL.PROCESS_DIVISION = :processDivision              -- メールアドレス認証情報TBL.処理区分 = "1"(アプリ会員登録)
    AND MAIL.PROCESSED_FLAG = '0'                             -- メールアドレス認証情報TBL.処理済フラグ = "0"(未処理)
    AND MAIL.DELETED_FLG = '0'                                -- メールアドレス認証情報TBL.削除フラグ = "0"(未削除)

-- メールアドレス変更一時情報TBLにメールアドレスの存在チェック
SELECT_MAIL_ADDRESS_CHANGE_TEMP_INFO=
SELECT
    CHANGE.MAIL_ADDRESS_CHANGE_RCPT_ID                                 -- メールアドレス変更受付ID
FROM
    MAIL_ADDRESS_CHANGE_TEMP_INFO CHANGE                               -- メールアドレス変更一時情報
INNER JOIN
    MAIL_ADDRESS_AUTH_INFO AUTH                                        -- メールアドレス認証情報
ON
    CHANGE.MAIL_ADDRESS_CHANGE_RCPT_ID = AUTH.MAIL_ADDRESS_AUTH_KEY    -- メールアドレス変更一時情報TBL.メールアドレス変更受付ID = メールアドレス認証情報TBL.メールアドレス認証キー
WHERE
    CHANGE.MAIL_ADDRESS = :mailAddress                                 -- メールアドレス変更一時情報TBL.メールアドレス = アプリ会員一時登録要求電文.メールアドレス
    AND CHANGE.DELETED_FLG = '0'                                       -- メールアドレス変更一時情報TBL.削除フラグ = "0"(未削除)
    AND CHANGE.PROCESSED_FLAG = '0'                                    -- メールアドレス変更一時情報TBL.処理済フラグ = "0"(未処理)
    AND AUTH.MAIL_ADDRESS_AUTH_EXPI_DATE >= :sysDateTime               -- メールアドレス認証情報TBL.メールアドレス認証有効期限 ≧ システム日時
    AND AUTH.PROCESS_DIVISION = :processDivision                       -- メールアドレス認証情報TBL.処理区分 = "3"(メールアドレス変更)
    AND AUTH.PROCESSED_FLAG = '0'                                      -- メールアドレス認証情報TBL.処理済フラグ = "0"(未処理)
    AND AUTH.DELETED_FLG = '0'                                         -- メールアドレス認証情報TBL.削除フラグ = "0"(未削除)

-- アプリ会員一時情報更新
UPDATE_APL_MEM_TEMP_INFO=
UPDATE
    APL_MEM_TEMP_INFO                                 -- アプリ会員一時情報TBL
SET
    PROCESSED_FLAG = '1',                             -- 処理済フラグ
    UPDATE_USER_ID = :updateUserId,                   -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime                -- 最終更新日時
WHERE
    LOGIN_ID = :loginId                               -- アプリ会員一時情報TBL.ログインID = アプリ会員一時登録要求電文.ログインID
    AND PROCESSED_FLAG = '0'                          -- アプリ会員一時情報TBL.処理済フラグ = "0"(未処理)
    AND DELETED_FLG = '0'                             -- アプリ会員一時情報TBL.削除フラグ = "0"(未削除)

-- アプリ会員一時情報登録
INSERT_APL_MEM_TEMP_INFO=
INSERT INTO
    APL_MEM_TEMP_INFO                        -- アプリ会員一時情報
    (
    APL_MEM_REGIST_RCPT_ID,                  -- アプリ会員登録受付ID
    OSAKA_PITAPA_NUMBER,                     -- OP番号
    APPLICATION_ID,                          -- アプリID
    DEVICE_ID,                               -- デバイスID
    LOGIN_ID,                                -- ログインID
    PASSWORD,                                -- パスワード
    PASSWORD_SALT,                           -- パスワードSALT
    STRETCHING_TIMES,                        -- ストレッチング回数
    BIRTHDATE,                               -- 生年月日
    SEX_CODE,                                -- 性別コード
    MAIL_ADDRESS,                            -- メールアドレス
    RECOMMEND_USE_ACCEPT_FLAG,               -- レコメンド利用承諾可フラグ
    ENQUETE_1,                               -- アンケート1
    ENQUETE_2,                               -- アンケート2
    ENQUETE_3,                               -- アンケート3
    ENQUETE_4,                               -- アンケート4
    ENQUETE_5,                               -- アンケート5
    ENQUETE_6,                               -- アンケート6
    ENQUETE_7,                               -- アンケート7
    ENQUETE_8,                               -- アンケート8
    ENQUETE_9,                               -- アンケート9
    ENQUETE_10,                              -- アンケート10
    MAIN_USE_STATION_1,                      -- 主なご利用駅1
    MAIN_USE_STATION_2,                      -- 主なご利用駅2
    MAIN_USE_STATION_3,                      -- 主なご利用駅3
    MAIN_USE_STATION_4,                      -- 主なご利用駅4
    MAIN_USE_STATION_5,                      -- 主なご利用駅5
    DAY_OFF_1,                               -- 休日1
    DAY_OFF_2,                               -- 休日2
    PROCESSED_FLAG,                          -- 処理済フラグ
    INSERT_USER_ID,                          -- 登録者ID
    INSERT_DATE_TIME,                        -- 登録日時
    UPDATE_USER_ID,                          -- 最終更新者ID
    UPDATE_DATE_TIME,                        -- 最終更新日時
    DELETED_FLG,                             -- 削除フラグ
    DELETED_DATE                             -- 論理削除日
    )
    VALUES
    (
    :aplMemRegistRcptId,                     -- アプリ会員登録受付ID
    :osakaPitapaNumber,                      -- OP番号
    :applicationId,                          -- アプリID
    :deviceId,                               -- デバイスID
    :loginId,                                -- ログインID
    :password,                               -- パスワード
    :passwordSalt,                           -- パスワードSALT
    :stretchingTimes,                        -- ストレッチング回数
    :birthdate,                              -- 生年月日
    :sexCode,                                -- 性別コード
    :mailAddress,                            -- メールアドレス
    :recommendUseAcceptFlag,                 -- レコメンド利用承諾可フラグ
    :enquete1,                               -- アンケート1
    :enquete2,                               -- アンケート2
    :enquete3,                               -- アンケート3
    :enquete4,                               -- アンケート4
    :enquete5,                               -- アンケート5
    :enquete6,                               -- アンケート6
    :enquete7,                               -- アンケート7
    :enquete8,                               -- アンケート8
    :enquete9,                               -- アンケート9
    :enquete10,                              -- アンケート10
    :mainUseStation1,                        -- 主なご利用駅1
    :mainUseStation2,                        -- 主なご利用駅2
    :mainUseStation3,                        -- 主なご利用駅3
    :mainUseStation4,                        -- 主なご利用駅4
    :mainUseStation5,                        -- 主なご利用駅5
    :dayOff1,                                -- 休日1
    :dayOff2,                                -- 休日2
    :processedFlag,                          -- 処理済フラグ
    :insertUserId,                           -- 登録者ID
    :insertDateTime,                         -- 登録日時
    :updateUsetId,                           -- 最終更新者ID
    :updateDateTime,                         -- 最終更新日時
    :deletedFlg,                             -- 削除フラグ
    :deletedDate                             -- 論理削除日
    )

-- メールアドレス認証情報登録
INSERT_MAIL_ADDRESS_AUTH_INFO=
INSERT INTO
    MAIL_ADDRESS_AUTH_INFO                   -- メールアドレス認証情報
    (
    MAIL_ADDRESS_AUTH_CODE,                  -- メールアドレス認証コード
    MAIL_ADDRESS_AUTH_KEY,                   -- メールアドレス認証キー
    MAIL_ADDRESS_AUTH_SALT,                  -- メールアドレス認証SALT
    MAIL_ADDRESS_AUTH_EXPI_DATE,             -- メールアドレス認証有効期限
    PROCESS_DIVISION,                        -- 処理区分
    MAIL_ADDRESS,                            -- メールアドレス
    PROCESSED_FLAG,                            -- 処理済フラグ
    INSERT_USER_ID,                          -- 登録者ID
    INSERT_DATE_TIME,                        -- 登録日時
    UPDATE_USER_ID,                          -- 最終更新者ID
    UPDATE_DATE_TIME,                        -- 最終更新日時
    DELETED_FLG,                             -- 削除フラグ
    DELETED_DATE,                            -- 論理削除日
    VERSION                                  -- バージョン番号
    )
    VALUES
    (
    :mailAddressAuthCode,                    -- メールアドレス認証コード
    :mailAddressAuthKey,                     -- メールアドレス認証キー
    :mailAddressAuthSalt,                    -- メールアドレス認証SALT
    :mailAddressAuthExpiDate,                -- メールアドレス認証有効期限
    :processDivision,                        -- 処理区分
    :mailAddress,                            -- メールアドレス
    :processFlag,                            -- 処理済フラグ
    :insertUserId,                           -- 登録者ID
    :insertDateTime,                         -- 登録日時
    :updateUserId,                           -- 最終更新者ID
    :updareDateTime,                         -- 最終更新日時
    :deletedFlg,                             -- 削除フラグ
    :deletedDate,                            -- 論理削除日
    :version                                 -- バージョン番号
    )