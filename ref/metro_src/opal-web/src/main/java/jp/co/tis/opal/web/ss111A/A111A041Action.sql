------------------------------------------------
-- アプリ会員登録API
------------------------------------------------
-- アプリ会員一時情報取得
SELECT_APL_MEM_TEMP_INFO=
SELECT
    APL_MEM_REGIST_RCPT_ID,                            -- アプリ会員登録受付ID
    OSAKA_PITAPA_NUMBER,                               -- OP番号
    APPLICATION_ID,                                    -- アプリID
    DEVICE_ID,                                         -- デバイスID
    LOGIN_ID,                                          -- ログインID
    PASSWORD,                                          -- パスワード
    PASSWORD_SALT,                                     -- パスワードSALT
    STRETCHING_TIMES,                                  -- ストレッチング回数
    BIRTHDATE,                                         -- 生年月日
    SEX_CODE,                                          -- 性別コード
    MAIL_ADDRESS,                                      -- メールアドレス
    RECOMMEND_USE_ACCEPT_FLAG,                         -- レコメンド利用承諾可フラグ
    ENQUETE_1,                                         -- アンケート1
    ENQUETE_2,                                         -- アンケート2
    ENQUETE_3,                                         -- アンケート3
    ENQUETE_4,                                         -- アンケート4
    ENQUETE_5,                                         -- アンケート5
    ENQUETE_6,                                         -- アンケート6
    ENQUETE_7,                                         -- アンケート7
    ENQUETE_8,                                         -- アンケート8
    ENQUETE_9,                                         -- アンケート9
    ENQUETE_10,                                        -- アンケート10
    MAIN_USE_STATION_1,                                -- 主なご利用駅1
    MAIN_USE_STATION_2,                                -- 主なご利用駅2
    MAIN_USE_STATION_3,                                -- 主なご利用駅3
    MAIN_USE_STATION_4,                                -- 主なご利用駅4
    MAIN_USE_STATION_5,                                -- 主なご利用駅5
    DAY_OFF_1,                                         -- 休日1
    DAY_OFF_2                                          -- 休日2
FROM
    APL_MEM_TEMP_INFO                                 -- アプリ会員一時情報TBL
WHERE
    APL_MEM_REGIST_RCPT_ID = :aplMemRegistRcptId      -- アプリ会員一時情報TBL.アプリ会員登録受付ID = 2.2.2で取得したメールアドレス認証キー
    AND PROCESSED_FLAG = '0'                          -- アプリ会員一時情報TBL.処理済フラグ = "0"(未処理)
    AND DELETED_FLG = '0'                             -- アプリ会員一時情報TBL.削除済フラグ = "0"(未削除)

-- アプリ会員一時情報更新
UPDATE_APL_MEM_TEMP_INFO=
UPDATE
    APL_MEM_TEMP_INFO                                 -- アプリ会員一時情報TBL
SET
    PROCESSED_FLAG = :processedFlag1,                 --処理済フラグ
    UPDATE_USER_ID = :updateUserId,                   --最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime                --最終更新日時
WHERE
    APL_MEM_REGIST_RCPT_ID = :aplMemRegistRcptId      -- アプリ会員一時情報TBL.アプリ会員登録受付ID = 2.2.2で取得したメールアドレス認証キー
    AND PROCESSED_FLAG = :processedFlag0              -- アプリ会員一時情報TBL.処理済フラグ = "0"(未処理)
    AND DELETED_FLG = :deletedFlg                     -- アプリ会員一時情報TBL.削除フラグ = "0"(未削除)

-- アプリ会員情報登録
INSERT_APL_MEM_INFO=
INSERT INTO
    APL_MEM_INFO                                      -- アプリ会員情報
    (
    APPLICATION_MEMBER_ID,                            -- アプリ会員ID
    MEMBER_CONTROL_NUMBER,                            -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                              -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                              -- OP番号
    APPLICATION_ID,                                   -- アプリID
    DEVICE_ID,                                        -- デバイスID
    LOGIN_ID,                                         -- ログインID
    PASSWORD,                                         -- パスワード
    PASSWORD_SALT,                                    -- パスワードSALT
    STRETCHING_TIMES,                                 -- ストレッチング回数
    BIRTHDATE,                                        -- 生年月日
    SEX_CODE,                                         -- 性別コード
    MAIL_ADDRESS,                                     -- メールアドレス
    MAIL_DELIVER_STATUS_DIVISION,                     -- メール配信状態区分
    RECOMMEND_USE_ACCEPT_FLAG,                        -- レコメンド利用承諾可フラグ
    ENQUETE_1,                                        -- アンケート1
    ENQUETE_2,                                        -- アンケート2
    ENQUETE_3,                                        -- アンケート3
    ENQUETE_4,                                        -- アンケート4
    ENQUETE_5,                                        -- アンケート5
    ENQUETE_6,                                        -- アンケート6
    ENQUETE_7,                                        -- アンケート7
    ENQUETE_8,                                        -- アンケート8
    ENQUETE_9,                                        -- アンケート9
    ENQUETE_10,                                       -- アンケート10
    MAIN_USE_STATION_1,                               -- 主なご利用駅1
    MAIN_USE_STATION_2,                               -- 主なご利用駅2
    MAIN_USE_STATION_3,                               -- 主なご利用駅3
    MAIN_USE_STATION_4,                               -- 主なご利用駅4
    MAIN_USE_STATION_5,                               -- 主なご利用駅5
    DAY_OFF_1,                                        -- 休日1
    DAY_OFF_2,                                        -- 休日2
    APPLICATION_MEMBER_STATUS_CODE,                   -- アプリ会員状態コード
    OSAKA_PITAPA_AUTHENTICATE_FLAG,                   -- OP認証フラグ
    OSAKA_PITAPA_WITHDRAW_FLAG,                       -- OP退会フラグ
    OP_AUTH_TIMES,                                    -- OP認証回数
    INSERT_USER_ID,                                   -- 登録者ID
    INSERT_DATE_TIME,                                 -- 登録日時
    UPDATE_USER_ID,                                   -- 最終更新者ID
    UPDATE_DATE_TIME,                                 -- 最終更新日時
    DELETED_FLG,                                      -- 削除フラグ
    DELETED_DATE,                                     -- 論理削除日
    VERSION                                           -- バージョン番号
    )
    VALUES
    (
    :applicationMemberId,                    -- アプリ会員ID
    :memberControlNumber,                    -- 会員管理番号
    :memCtrlNumBrNum,                        -- 会員管理番号枝番
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
    :mailDeliverStatusDivision,              -- メール配信状態区分
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
    :applicationMemberStatusCode,            -- アプリ会員状態コード
    :osakaPitapaAuthenticateFlag,            -- OP認証フラグ
    :osakaPitapaWithdrawFlag,                -- OP退会フラグ
    :opAuthTimes,                            -- OP認証回数
    :insertUserId,                           -- 登録者ID
    :insertDateTime,                         -- 登録日時
    :updateUserId,                           -- 最終更新者ID
    :updateDateTime,                         -- 最終更新日時
    :deletedFlag,                            -- 削除フラグ
    :deletedDate,                            -- 論理削除日
    :version                                 -- バージョン番号
    )