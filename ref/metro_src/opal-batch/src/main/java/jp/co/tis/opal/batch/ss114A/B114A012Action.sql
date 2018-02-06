------------------------------------------------------------------------------------------------------
-- OP会員情報更新バッチ(B114A012Action)
------------------------------------------------------------------------------------------------------

-- OP会員一時情報取得SQL
SELECT_OP_MEM_TEMP_INFO =
SELECT
    MEMBER_CONTROL_NUMBER,               -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                 -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                 -- OP番号
    DATA_RELATE_DIVISION,                -- データ連携区分
    PITAPA_EXPIRATION_DATE,              -- PiTaPa有効期限
    CARD_TYPE,                           -- カード種類
    BIRTHDATE,                           -- 生年月日
    SEX_CODE,                            -- 性別コード
    TELEPHONE_NUMBER,                    -- 自宅電話番号
    CELLPHONE_NUMBER,                    -- 携帯電話番号
    POSTCODE,                            -- 郵便番号
    SERVICE_CATEGORY,                    -- サービス種別
    REGIST_STATION_1,                    -- 登録駅1
    REGIST_STATION_2,                    -- 登録駅2
    RELATIONSHIP_CODE                    -- 続柄コード
FROM
    OP_MEM_TEMP_INFO                     -- OP会員一時情報
WHERE
    PROCESSED_FLAG = '0'                 -- 処理済フラグ(0:未処理)
    AND DELETED_FLG = '0'                -- 削除フラグ(0:未削除)
ORDER BY
    MEMBER_CONTROL_NUMBER ASC,           -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM ASC              -- 会員管理番号枝番

-- OP会員情報登録SQL
INSERT_OP_MEM_INFO =
INSERT INTO
    OP_MEM_INFO                          -- OP会員情報
    (
    MEMBER_CONTROL_NUMBER,               -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                 -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                 -- OP番号
    PITAPA_EXPIRATION_DATE,              -- PiTaPa有効期限
    CARD_TYPE,                           -- カード種類
    BIRTHDATE,                           -- 生年月日
    SEX_CODE,                            -- 性別コード
    TELEPHONE_NUMBER,                    -- 自宅電話番号
    CELLPHONE_NUMBER,                    -- 携帯電話番号
    POSTCODE,                            -- 郵便番号
    SERVICE_CATEGORY,                    -- サービス種別
    REGIST_STATION_1,                    -- 登録駅1
    REGIST_STATION_2,                    -- 登録駅2
    RELATIONSHIP_CODE,                   -- 続柄コード
    OSAKA_PITAPA_WITHDRAW_FLAG,          -- OP退会フラグ
    INSERT_USER_ID,                      -- 登録者ID
    INSERT_DATE_TIME,                    -- 登録日時
    UPDATE_USER_ID,                      -- 最終更新者ID
    UPDATE_DATE_TIME,                    -- 最終更新日時
    DELETED_FLG,                         -- 削除フラグ
    DELETED_DATE,                        -- 論理削除日
    VERSION                              -- バージョン番号
    )
    VALUES
    (
    :memberControlNumber,                -- 会員管理番号
    :memCtrlNumBrNum,                    -- 会員管理番号枝番
    :osakaPitapaNumber,                  -- OP番号
    :pitapaExpirationDate,               -- PiTaPa有効期限
    :cardType,                           -- カード種類
    :birthdate,                          -- 生年月日
    :sexCode,                            -- 性別コード
    :telephoneNumber,                    -- 自宅電話番号
    :cellphoneNumber,                    -- 携帯電話番号
    :postcode,                           -- 郵便番号
    :serviceCategory,                    -- サービス種別
    :registStation1,                     -- 登録駅1
    :registStation2,                     -- 登録駅2
    :relationshipCode,                   -- 続柄コード
    :osakaPitapaWithdrawFlag,            -- OP退会フラグ(0:未退会)
    :insertUserId,                       -- 登録者ID
    :insertDateTime,                     -- 登録日時
    :updateUserId,                       -- 最終更新者ID
    :updateDateTime,                     -- 最終更新日時
    :deletedFlg,                         -- 削除フラグ(0:未削除)
    :deletedDate,                        -- 論理削除日
    :version                             -- バージョン番号
    )

-- アプリ会員情報登録SQL
INSERT_APL_MEM_INFO =
INSERT INTO
    APL_MEM_INFO                         -- アプリ会員情報
    (
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
    )
    VALUES
    (
    :applicationMemberId,                -- アプリ会員ID
    :memberControlNumber,                -- 会員管理番号
    :memCtrlNumBrNum,                    -- 会員管理番号枝番
    :osakaPitapaNumber,                  -- OP番号
    NULL,                                -- アプリID
    NULL,                                -- デバイスID
    NULL,                                -- ログインID
    NULL,                                -- パスワード
    NULL,                                -- パスワードSALT
    NULL,                                -- ストレッチング回数
    NULL,                                -- 生年月日
    NULL,                                -- 性別コード
    NULL,                                -- メールアドレス
    NULL,                                -- レコメンド利用承諾可否フラグ
    NULL,                                -- アンケート1
    NULL,                                -- アンケート2
    NULL,                                -- アンケート3
    NULL,                                -- アンケート4
    NULL,                                -- アンケート5
    NULL,                                -- アンケート6
    NULL,                                -- アンケート7
    NULL,                                -- アンケート8
    NULL,                                -- アンケート9
    NULL,                                -- アンケート10
    NULL,                                -- 主なご利用駅1
    NULL,                                -- 主なご利用駅2
    NULL,                                -- 主なご利用駅3
    NULL,                                -- 主なご利用駅4
    NULL,                                -- 主なご利用駅5
    NULL,                                -- 休日1
    NULL,                                -- 休日2
    :applicationMemberStatusCode,        -- アプリ会員状態コード(C:OP会員でアプリ会員でない方)
    :osakaPitapaAuthenticateFlag,        -- OP認証フラグ(0:未認証)
    :osakaPitapaWithdrawFlag,            -- OP退会フラグ(0:未退会)
    :opAuthTimes,                        -- OP認証回数
    :insertUserId,                       -- 登録者ID
    :insertDateTime,                     -- 登録日時
    :updateUserId,                       -- 最終更新者ID
    :updateDateTime,                     -- 最終更新日時
    :deletedFlg,                         -- 削除フラグ(0:未削除)
    NULL,                                -- 論理削除日
    :version                             -- バージョン番号
    )

-- 主なご利用駅情報更新SQL
UPDATE_MAIN_USE_STA_INFO =
UPDATE
    MAIN_USE_STA_INFO                               -- 主なご利用駅情報
SET
    DELETED_FLG = :deletedFlag1,                    -- 削除フラグ(1:削除済)
    UPDATE_USER_ID = :updateUserId,                 -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,             -- 最終更新日時
    DELETED_DATE = :deletedDate,                    -- 論理削除日
    VERSION = VERSION + 1                           -- バージョン番号+1
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber    -- 会員管理番号
    AND MEM_CTRL_NUM_BR_NUM = :memCtrlNumBrNum      -- 会員管理番号枝番
    AND DELETED_FLG = :deletedFlag0                 -- 削除フラグ(0:未削除)

-- OP会員一時情報更新SQL
UPDATE_OP_MEM_TEMP_INFO =
UPDATE
    OP_MEM_TEMP_INFO                                -- OP会員一時情報
SET
    PROCESSED_FLAG = :processedFlag,                -- 処理済フラグ(1:処理済)
    UPDATE_USER_ID = :updateUserId,                 -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime              -- 最終更新日時
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber    -- 会員管理番号
    AND MEM_CTRL_NUM_BR_NUM = :memCtrlNumBrNum      -- 会員管理番号枝番
    AND PROCESSED_FLAG = '0'                        -- 処理済フラグ(0:未処理)
    AND DELETED_FLG = '0'                           -- 削除フラグ(0:未削除)