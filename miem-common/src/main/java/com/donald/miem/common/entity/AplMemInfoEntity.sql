------------------------------------------------------------------------------------------------------
--アプリ会員情報排他制御用SQL
------------------------------------------------------------------------------------------------------
SELECT_APL_MEM_INFO_BY_APL =
SELECT
    APPLICATION_MEMBER_ID,               -- アプリ会員ID
    MEMBER_CONTROL_NUMBER,               -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                 -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                 -- OP番号
    APPLICATION_ID,                      -- アプリID
    DEVICE_ID,                           -- デバイスID
    LOGIN_ID,                            -- ログインID
    PASSWORD,                            -- パスワード
    PASSWORD_SALT,                       -- パスワードSALT
    STRETCHING_TIMES,                    -- ストレッチング回数
    BIRTHDATE,                           -- 生年月日
    SEX_CODE,                            -- 性別コード
    MAIL_ADDRESS,                        -- メールアドレス
    MAIL_DELIVER_STATUS_DIVISION,        -- メール配信状態区分
    RECOMMEND_USE_ACCEPT_FLAG,           -- レコメンド利用承諾可否フラグ
    ENQUETE_1,                           -- アンケート1
    ENQUETE_2,                           -- アンケート2
    ENQUETE_3,                           -- アンケート3
    ENQUETE_4,                           -- アンケート4
    ENQUETE_5,                           -- アンケート5
    ENQUETE_6,                           -- アンケート6
    ENQUETE_7,                           -- アンケート7
    ENQUETE_8,                           -- アンケート8
    ENQUETE_9,                           -- アンケート9
    ENQUETE_10,                          -- アンケート10
    MAIN_USE_STATION_1,                  -- 主なご利用駅1
    MAIN_USE_STATION_2,                  -- 主なご利用駅2
    MAIN_USE_STATION_3,                  -- 主なご利用駅3
    MAIN_USE_STATION_4,                  -- 主なご利用駅4
    MAIN_USE_STATION_5,                  -- 主なご利用駅5
    DAY_OFF_1,                           -- 休日1
    DAY_OFF_2,                           -- 休日2
    APPLICATION_MEMBER_STATUS_CODE,      -- アプリ会員状態コード
    OSAKA_PITAPA_AUTHENTICATE_FLAG,      -- OP認証フラグ
    OSAKA_PITAPA_WITHDRAW_FLAG,          -- OP退会フラグ
    OP_AUTH_TIMES,                       -- OP認証回数
    INSERT_USER_ID,                      -- 登録者ID
    INSERT_DATE_TIME,                    -- 登録日時
    UPDATE_USER_ID,                      -- 最終更新者ID
    UPDATE_DATE_TIME,                    -- 最終更新日時
    DELETED_FLG,                         -- 削除フラグ
    DELETED_DATE,                        -- 論理削除日
    VERSION                              -- バージョン番号
FROM
    APL_MEM_INFO                         --アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId  --アプリ会員ID
    -- アプリ会員情報TBL.アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員),"D"(OP非会員))
    AND APPLICATION_MEMBER_STATUS_CODE IN (:opAuthAplMem, :notOpMem)
    AND DELETED_FLG = '0'               --削除フラグ(0:未削除)
FOR UPDATE

--OP番号更新
SELECT_APL_MEM_INFO_BY_OP_AND_APL =
SELECT
    APPLICATION_MEMBER_ID,               -- アプリ会員ID
    MEMBER_CONTROL_NUMBER,               -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                 -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                 -- OP番号
    APPLICATION_ID,                      -- アプリID
    DEVICE_ID,                           -- デバイスID
    LOGIN_ID,                            -- ログインID
    PASSWORD,                            -- パスワード
    PASSWORD_SALT,                       -- パスワードSALT
    STRETCHING_TIMES,                    -- ストレッチング回数
    BIRTHDATE,                           -- 生年月日
    SEX_CODE,                            -- 性別コード
    MAIL_ADDRESS,                        -- メールアドレス
    MAIL_DELIVER_STATUS_DIVISION,        -- メール配信状態区分
    RECOMMEND_USE_ACCEPT_FLAG,           -- レコメンド利用承諾可否フラグ
    ENQUETE_1,                           -- アンケート1
    ENQUETE_2,                           -- アンケート2
    ENQUETE_3,                           -- アンケート3
    ENQUETE_4,                           -- アンケート4
    ENQUETE_5,                           -- アンケート5
    ENQUETE_6,                           -- アンケート6
    ENQUETE_7,                           -- アンケート7
    ENQUETE_8,                           -- アンケート8
    ENQUETE_9,                           -- アンケート9
    ENQUETE_10,                          -- アンケート10
    MAIN_USE_STATION_1,                  -- 主なご利用駅1
    MAIN_USE_STATION_2,                  -- 主なご利用駅2
    MAIN_USE_STATION_3,                  -- 主なご利用駅3
    MAIN_USE_STATION_4,                  -- 主なご利用駅4
    MAIN_USE_STATION_5,                  -- 主なご利用駅5
    DAY_OFF_1,                           -- 休日1
    DAY_OFF_2,                           -- 休日2
    APPLICATION_MEMBER_STATUS_CODE,      -- アプリ会員状態コード
    OSAKA_PITAPA_AUTHENTICATE_FLAG,      -- OP認証フラグ
    OSAKA_PITAPA_WITHDRAW_FLAG,          -- OP退会フラグ
    OP_AUTH_TIMES,                       -- OP認証回数
    INSERT_USER_ID,                      -- 登録者ID
    INSERT_DATE_TIME,                    -- 登録日時
    UPDATE_USER_ID,                      -- 最終更新者ID
    UPDATE_DATE_TIME,                    -- 最終更新日時
    DELETED_FLG,                         -- 削除フラグ
    DELETED_DATE,                        -- 論理削除日
    VERSION                              -- バージョン番号
FROM
    APL_MEM_INFO                         --アプリ会員情報
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber    -- 会員管理番号
    AND MEM_CTRL_NUM_BR_NUM = :memCtrlNumBrNum      -- 会員管理番号枝番
    AND DELETED_FLG = '0'                           -- 削除フラグ(0:未削除)
FOR UPDATE

--OP会員取込み時に作成したアプリ会員情報退会（解約）
SELECT_APL_MEM_INFO_FOR_OP_WITHDRAW =
SELECT
    APPLICATION_MEMBER_ID,               -- アプリ会員ID
    MEMBER_CONTROL_NUMBER,               -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                 -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                 -- OP番号
    APPLICATION_ID,                      -- アプリID
    DEVICE_ID,                           -- デバイスID
    LOGIN_ID,                            -- ログインID
    PASSWORD,                            -- パスワード
    PASSWORD_SALT,                       -- パスワードSALT
    STRETCHING_TIMES,                    -- ストレッチング回数
    BIRTHDATE,                           -- 生年月日
    SEX_CODE,                            -- 性別コード
    MAIL_ADDRESS,                        -- メールアドレス
    MAIL_DELIVER_STATUS_DIVISION,        -- メール配信状態区分
    RECOMMEND_USE_ACCEPT_FLAG,           -- レコメンド利用承諾可否フラグ
    ENQUETE_1,                           -- アンケート1
    ENQUETE_2,                           -- アンケート2
    ENQUETE_3,                           -- アンケート3
    ENQUETE_4,                           -- アンケート4
    ENQUETE_5,                           -- アンケート5
    ENQUETE_6,                           -- アンケート6
    ENQUETE_7,                           -- アンケート7
    ENQUETE_8,                           -- アンケート8
    ENQUETE_9,                           -- アンケート9
    ENQUETE_10,                          -- アンケート10
    MAIN_USE_STATION_1,                  -- 主なご利用駅1
    MAIN_USE_STATION_2,                  -- 主なご利用駅2
    MAIN_USE_STATION_3,                  -- 主なご利用駅3
    MAIN_USE_STATION_4,                  -- 主なご利用駅4
    MAIN_USE_STATION_5,                  -- 主なご利用駅5
    DAY_OFF_1,                           -- 休日1
    DAY_OFF_2,                           -- 休日2
    APPLICATION_MEMBER_STATUS_CODE,      -- アプリ会員状態コード
    OSAKA_PITAPA_AUTHENTICATE_FLAG,      -- OP認証フラグ
    OSAKA_PITAPA_WITHDRAW_FLAG,          -- OP退会フラグ
    OP_AUTH_TIMES,                       -- OP認証回数
    INSERT_USER_ID,                      -- 登録者ID
    INSERT_DATE_TIME,                    -- 登録日時
    UPDATE_USER_ID,                      -- 最終更新者ID
    UPDATE_DATE_TIME,                    -- 最終更新日時
    DELETED_FLG,                         -- 削除フラグ
    DELETED_DATE,                        -- 論理削除日
    VERSION                              -- バージョン番号
FROM
    APL_MEM_INFO                         --アプリ会員情報
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber           -- 会員管理番号
    AND MEM_CTRL_NUM_BR_NUM = :memCtrlNumBrNum             -- 会員管理番号枝番
    AND OSAKA_PITAPA_WITHDRAW_FLAG = '0'                   -- OP退会フラグ(0:OP未退会)
    AND LOGIN_ID IS NULL                                   -- ログインID
    AND DELETED_FLG = '0'                                  -- 削除フラグ(0:未削除)
FOR UPDATE

--アプリ会員登録時に作成したアプリ会員情報のOP認証解約
SELECT_APL_MEM_INFO_FOR_APL_WITHDRAW =
SELECT
    APPLICATION_MEMBER_ID,               -- アプリ会員ID
    MEMBER_CONTROL_NUMBER,               -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                 -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                 -- OP番号
    APPLICATION_ID,                      -- アプリID
    DEVICE_ID,                           -- デバイスID
    LOGIN_ID,                            -- ログインID
    PASSWORD,                            -- パスワード
    PASSWORD_SALT,                       -- パスワードSALT
    STRETCHING_TIMES,                    -- ストレッチング回数
    BIRTHDATE,                           -- 生年月日
    SEX_CODE,                            -- 性別コード
    MAIL_ADDRESS,                        -- メールアドレス
    MAIL_DELIVER_STATUS_DIVISION,        -- メール配信状態区分
    RECOMMEND_USE_ACCEPT_FLAG,           -- レコメンド利用承諾可否フラグ
    ENQUETE_1,                           -- アンケート1
    ENQUETE_2,                           -- アンケート2
    ENQUETE_3,                           -- アンケート3
    ENQUETE_4,                           -- アンケート4
    ENQUETE_5,                           -- アンケート5
    ENQUETE_6,                           -- アンケート6
    ENQUETE_7,                           -- アンケート7
    ENQUETE_8,                           -- アンケート8
    ENQUETE_9,                           -- アンケート9
    ENQUETE_10,                          -- アンケート10
    MAIN_USE_STATION_1,                  -- 主なご利用駅1
    MAIN_USE_STATION_2,                  -- 主なご利用駅2
    MAIN_USE_STATION_3,                  -- 主なご利用駅3
    MAIN_USE_STATION_4,                  -- 主なご利用駅4
    MAIN_USE_STATION_5,                  -- 主なご利用駅5
    DAY_OFF_1,                           -- 休日1
    DAY_OFF_2,                           -- 休日2
    APPLICATION_MEMBER_STATUS_CODE,      -- アプリ会員状態コード
    OSAKA_PITAPA_AUTHENTICATE_FLAG,      -- OP認証フラグ
    OSAKA_PITAPA_WITHDRAW_FLAG,          -- OP退会フラグ
    OP_AUTH_TIMES,                       -- OP認証回数
    INSERT_USER_ID,                      -- 登録者ID
    INSERT_DATE_TIME,                    -- 登録日時
    UPDATE_USER_ID,                      -- 最終更新者ID
    UPDATE_DATE_TIME,                    -- 最終更新日時
    DELETED_FLG,                         -- 削除フラグ
    DELETED_DATE,                        -- 論理削除日
    VERSION                              -- バージョン番号
FROM
    APL_MEM_INFO                         --アプリ会員情報
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber                   -- 会員管理番号
    AND MEM_CTRL_NUM_BR_NUM = :memCtrlNumBrNum                     -- 会員管理番号枝番
    AND APPLICATION_MEMBER_STATUS_CODE = :aplMemberStatusCode      -- アプリ会員状態コード(A：OP認証済みのアプリ会員)
    AND DELETED_FLG = '0'                                          -- 削除フラグ(0:未削除)
FOR UPDATE

--アプリ会員ログインAPI
SELECT_APL_MEM_INFO_BY_LOGIN_ID =
SELECT
    APPLICATION_MEMBER_ID,               -- アプリ会員ID
    MEMBER_CONTROL_NUMBER,               -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                 -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                 -- OP番号
    APPLICATION_ID,                      -- アプリID
    DEVICE_ID,                           -- デバイスID
    LOGIN_ID,                            -- ログインID
    PASSWORD,                            -- パスワード
    PASSWORD_SALT,                       -- パスワードSALT
    STRETCHING_TIMES,                    -- ストレッチング回数
    BIRTHDATE,                           -- 生年月日
    SEX_CODE,                            -- 性別コード
    MAIL_ADDRESS,                        -- メールアドレス
    MAIL_DELIVER_STATUS_DIVISION,        -- メール配信状態区分
    RECOMMEND_USE_ACCEPT_FLAG,           -- レコメンド利用承諾可否フラグ
    ENQUETE_1,                           -- アンケート1
    ENQUETE_2,                           -- アンケート2
    ENQUETE_3,                           -- アンケート3
    ENQUETE_4,                           -- アンケート4
    ENQUETE_5,                           -- アンケート5
    ENQUETE_6,                           -- アンケート6
    ENQUETE_7,                           -- アンケート7
    ENQUETE_8,                           -- アンケート8
    ENQUETE_9,                           -- アンケート9
    ENQUETE_10,                          -- アンケート10
    MAIN_USE_STATION_1,                  -- 主なご利用駅1
    MAIN_USE_STATION_2,                  -- 主なご利用駅2
    MAIN_USE_STATION_3,                  -- 主なご利用駅3
    MAIN_USE_STATION_4,                  -- 主なご利用駅4
    MAIN_USE_STATION_5,                  -- 主なご利用駅5
    DAY_OFF_1,                           -- 休日1
    DAY_OFF_2,                           -- 休日2
    APPLICATION_MEMBER_STATUS_CODE,      -- アプリ会員状態コード
    OSAKA_PITAPA_AUTHENTICATE_FLAG,      -- OP認証フラグ
    OSAKA_PITAPA_WITHDRAW_FLAG,          -- OP退会フラグ
    OP_AUTH_TIMES,                       -- OP認証回数
    INSERT_USER_ID,                      -- 登録者ID
    INSERT_DATE_TIME,                    -- 登録日時
    UPDATE_USER_ID,                      -- 最終更新者ID
    UPDATE_DATE_TIME,                    -- 最終更新日時
    DELETED_FLG,                         -- 削除フラグ
    DELETED_DATE,                        -- 論理削除日
    VERSION                              -- バージョン番号
FROM
    APL_MEM_INFO                         --アプリ会員情報
WHERE
    LOGIN_ID = :loginId                  -- ログインID
    -- アプリ会員情報TBL.アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員),"D"(OP非会員))
    AND APPLICATION_MEMBER_STATUS_CODE IN (:opAuthAplMem, :notOpMem)
    AND DELETED_FLG = '0'               --削除フラグ(0:未削除)
FOR UPDATE

--パートナー会員サービス登録API
SELECT_APL_MEM_INFO_BY_PARTNER =
SELECT
    APPLICATION_MEMBER_ID,               -- アプリ会員ID
    MEMBER_CONTROL_NUMBER,               -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                 -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                 -- OP番号
    APPLICATION_ID,                      -- アプリID
    DEVICE_ID,                           -- デバイスID
    LOGIN_ID,                            -- ログインID
    PASSWORD,                            -- パスワード
    PASSWORD_SALT,                       -- パスワードSALT
    STRETCHING_TIMES,                    -- ストレッチング回数
    BIRTHDATE,                           -- 生年月日
    SEX_CODE,                            -- 性別コード
    MAIL_ADDRESS,                        -- メールアドレス
    MAIL_DELIVER_STATUS_DIVISION,        -- メール配信状態区分
    RECOMMEND_USE_ACCEPT_FLAG,           -- レコメンド利用承諾可否フラグ
    ENQUETE_1,                           -- アンケート1
    ENQUETE_2,                           -- アンケート2
    ENQUETE_3,                           -- アンケート3
    ENQUETE_4,                           -- アンケート4
    ENQUETE_5,                           -- アンケート5
    ENQUETE_6,                           -- アンケート6
    ENQUETE_7,                           -- アンケート7
    ENQUETE_8,                           -- アンケート8
    ENQUETE_9,                           -- アンケート9
    ENQUETE_10,                          -- アンケート10
    MAIN_USE_STATION_1,                  -- 主なご利用駅1
    MAIN_USE_STATION_2,                  -- 主なご利用駅2
    MAIN_USE_STATION_3,                  -- 主なご利用駅3
    MAIN_USE_STATION_4,                  -- 主なご利用駅4
    MAIN_USE_STATION_5,                  -- 主なご利用駅5
    DAY_OFF_1,                           -- 休日1
    DAY_OFF_2,                           -- 休日2
    APPLICATION_MEMBER_STATUS_CODE,      -- アプリ会員状態コード
    OSAKA_PITAPA_AUTHENTICATE_FLAG,      -- OP認証フラグ
    OSAKA_PITAPA_WITHDRAW_FLAG,          -- OP退会フラグ
    OP_AUTH_TIMES,                       -- OP認証回数
    INSERT_USER_ID,                      -- 登録者ID
    INSERT_DATE_TIME,                    -- 登録日時
    UPDATE_USER_ID,                      -- 最終更新者ID
    UPDATE_DATE_TIME,                    -- 最終更新日時
    DELETED_FLG,                         -- 削除フラグ
    DELETED_DATE,                        -- 論理削除日
    VERSION                              -- バージョン番号
FROM
    APL_MEM_INFO                         --アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID IN (:applicationMemberId, :partnerApplicationMemberId)  --アプリ会員ID,パートナー相手アプリ会員ID
    -- アプリ会員情報TBL.アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員),"D"(OP非会員))
    AND APPLICATION_MEMBER_STATUS_CODE IN (:opAuthAplMem, :notOpMem)
    AND DELETED_FLG = '0'               --削除フラグ(0:未削除)
FOR UPDATE

--家族会員サービス登録API
SELECT_APL_MEM_INFO_BY_FAMILY =
SELECT
    APPLICATION_MEMBER_ID,               -- アプリ会員ID
    MEMBER_CONTROL_NUMBER,               -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                 -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                 -- OP番号
    APPLICATION_ID,                      -- アプリID
    DEVICE_ID,                           -- デバイスID
    LOGIN_ID,                            -- ログインID
    PASSWORD,                            -- パスワード
    PASSWORD_SALT,                       -- パスワードSALT
    STRETCHING_TIMES,                    -- ストレッチング回数
    BIRTHDATE,                           -- 生年月日
    SEX_CODE,                            -- 性別コード
    MAIL_ADDRESS,                        -- メールアドレス
    MAIL_DELIVER_STATUS_DIVISION,        -- メール配信状態区分
    RECOMMEND_USE_ACCEPT_FLAG,           -- レコメンド利用承諾可否フラグ
    ENQUETE_1,                           -- アンケート1
    ENQUETE_2,                           -- アンケート2
    ENQUETE_3,                           -- アンケート3
    ENQUETE_4,                           -- アンケート4
    ENQUETE_5,                           -- アンケート5
    ENQUETE_6,                           -- アンケート6
    ENQUETE_7,                           -- アンケート7
    ENQUETE_8,                           -- アンケート8
    ENQUETE_9,                           -- アンケート9
    ENQUETE_10,                          -- アンケート10
    MAIN_USE_STATION_1,                  -- 主なご利用駅1
    MAIN_USE_STATION_2,                  -- 主なご利用駅2
    MAIN_USE_STATION_3,                  -- 主なご利用駅3
    MAIN_USE_STATION_4,                  -- 主なご利用駅4
    MAIN_USE_STATION_5,                  -- 主なご利用駅5
    DAY_OFF_1,                           -- 休日1
    DAY_OFF_2,                           -- 休日2
    APPLICATION_MEMBER_STATUS_CODE,      -- アプリ会員状態コード
    OSAKA_PITAPA_AUTHENTICATE_FLAG,      -- OP認証フラグ
    OSAKA_PITAPA_WITHDRAW_FLAG,          -- OP退会フラグ
    OP_AUTH_TIMES,                       -- OP認証回数
    INSERT_USER_ID,                      -- 登録者ID
    INSERT_DATE_TIME,                    -- 登録日時
    UPDATE_USER_ID,                      -- 最終更新者ID
    UPDATE_DATE_TIME,                    -- 最終更新日時
    DELETED_FLG,                         -- 削除フラグ
    DELETED_DATE,                        -- 論理削除日
    VERSION                              -- バージョン番号
FROM
    APL_MEM_INFO                         --アプリ会員情報
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber                   -- 会員管理番号
    AND OSAKA_PITAPA_AUTHENTICATE_FLAG = :opAuthenticateFlag       -- OP認証フラグ
    AND OSAKA_PITAPA_WITHDRAW_FLAG = '0'                           -- OP退会フラグ
    AND DELETED_FLG = '0'                                          -- 削除フラグ(0:未削除)
FOR UPDATE

--OP認証コンポーネント
SELECT_APL_MEM_INFO_BY_OP =
SELECT
    APPLICATION_MEMBER_ID,               -- アプリ会員ID
    MEMBER_CONTROL_NUMBER,               -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                 -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                 -- OP番号
    APPLICATION_ID,                      -- アプリID
    DEVICE_ID,                           -- デバイスID
    LOGIN_ID,                            -- ログインID
    PASSWORD,                            -- パスワード
    PASSWORD_SALT,                       -- パスワードSALT
    STRETCHING_TIMES,                    -- ストレッチング回数
    BIRTHDATE,                           -- 生年月日
    SEX_CODE,                            -- 性別コード
    MAIL_ADDRESS,                        -- メールアドレス
    MAIL_DELIVER_STATUS_DIVISION,        -- メール配信状態区分
    RECOMMEND_USE_ACCEPT_FLAG,           -- レコメンド利用承諾可否フラグ
    ENQUETE_1,                           -- アンケート1
    ENQUETE_2,                           -- アンケート2
    ENQUETE_3,                           -- アンケート3
    ENQUETE_4,                           -- アンケート4
    ENQUETE_5,                           -- アンケート5
    ENQUETE_6,                           -- アンケート6
    ENQUETE_7,                           -- アンケート7
    ENQUETE_8,                           -- アンケート8
    ENQUETE_9,                           -- アンケート9
    ENQUETE_10,                          -- アンケート10
    MAIN_USE_STATION_1,                  -- 主なご利用駅1
    MAIN_USE_STATION_2,                  -- 主なご利用駅2
    MAIN_USE_STATION_3,                  -- 主なご利用駅3
    MAIN_USE_STATION_4,                  -- 主なご利用駅4
    MAIN_USE_STATION_5,                  -- 主なご利用駅5
    DAY_OFF_1,                           -- 休日1
    DAY_OFF_2,                           -- 休日2
    APPLICATION_MEMBER_STATUS_CODE,      -- アプリ会員状態コード
    OSAKA_PITAPA_AUTHENTICATE_FLAG,      -- OP認証フラグ
    OSAKA_PITAPA_WITHDRAW_FLAG,          -- OP退会フラグ
    OP_AUTH_TIMES,                       -- OP認証回数
    INSERT_USER_ID,                      -- 登録者ID
    INSERT_DATE_TIME,                    -- 登録日時
    UPDATE_USER_ID,                      -- 最終更新者ID
    UPDATE_DATE_TIME,                    -- 最終更新日時
    DELETED_FLG,                         -- 削除フラグ
    DELETED_DATE,                        -- 論理削除日
    VERSION                              -- バージョン番号
FROM
    APL_MEM_INFO                         --アプリ会員情報
WHERE
    OSAKA_PITAPA_NUMBER = :osakaPitapaNumber -- OP番号
    AND DELETED_FLG = '0'                    --削除フラグ(0:未削除)
    AND OSAKA_PITAPA_WITHDRAW_FLAG = '0'     -- OP退会フラグ(0:OP未退会)
ORDER BY APPLICATION_MEMBER_STATUS_CODE DESC

--OP認証API
SELECT_APL_MEM_INFO_LOCK_BY_OP =
SELECT
    APPLICATION_MEMBER_ID,               -- アプリ会員ID
    MEMBER_CONTROL_NUMBER,               -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                 -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                 -- OP番号
    APPLICATION_ID,                      -- アプリID
    DEVICE_ID,                           -- デバイスID
    LOGIN_ID,                            -- ログインID
    PASSWORD,                            -- パスワード
    PASSWORD_SALT,                       -- パスワードSALT
    STRETCHING_TIMES,                    -- ストレッチング回数
    BIRTHDATE,                           -- 生年月日
    SEX_CODE,                            -- 性別コード
    MAIL_ADDRESS,                        -- メールアドレス
    MAIL_DELIVER_STATUS_DIVISION,        -- メール配信状態区分
    RECOMMEND_USE_ACCEPT_FLAG,           -- レコメンド利用承諾可否フラグ
    ENQUETE_1,                           -- アンケート1
    ENQUETE_2,                           -- アンケート2
    ENQUETE_3,                           -- アンケート3
    ENQUETE_4,                           -- アンケート4
    ENQUETE_5,                           -- アンケート5
    ENQUETE_6,                           -- アンケート6
    ENQUETE_7,                           -- アンケート7
    ENQUETE_8,                           -- アンケート8
    ENQUETE_9,                           -- アンケート9
    ENQUETE_10,                          -- アンケート10
    MAIN_USE_STATION_1,                  -- 主なご利用駅1
    MAIN_USE_STATION_2,                  -- 主なご利用駅2
    MAIN_USE_STATION_3,                  -- 主なご利用駅3
    MAIN_USE_STATION_4,                  -- 主なご利用駅4
    MAIN_USE_STATION_5,                  -- 主なご利用駅5
    DAY_OFF_1,                           -- 休日1
    DAY_OFF_2,                           -- 休日2
    APPLICATION_MEMBER_STATUS_CODE,      -- アプリ会員状態コード
    OSAKA_PITAPA_AUTHENTICATE_FLAG,      -- OP認証フラグ
    OSAKA_PITAPA_WITHDRAW_FLAG,          -- OP退会フラグ
    OP_AUTH_TIMES,                       -- OP認証回数
    INSERT_USER_ID,                      -- 登録者ID
    INSERT_DATE_TIME,                    -- 登録日時
    UPDATE_USER_ID,                      -- 最終更新者ID
    UPDATE_DATE_TIME,                    -- 最終更新日時
    DELETED_FLG,                         -- 削除フラグ
    DELETED_DATE,                        -- 論理削除日
    VERSION                              -- バージョン番号
FROM
    APL_MEM_INFO                         --アプリ会員情報
WHERE
    OSAKA_PITAPA_NUMBER = :osakaPitapaNumber -- OP番号
    AND DELETED_FLG = '0'                    --削除フラグ(0:未削除)
    AND OSAKA_PITAPA_WITHDRAW_FLAG = '0'     -- OP退会フラグ(0:OP未退会)
ORDER BY APPLICATION_MEMBER_STATUS_CODE DESC
FOR UPDATE