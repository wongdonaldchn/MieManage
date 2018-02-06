------------------------------------------------------------------------------------------------------
-- OP認証共通コンポーネント
------------------------------------------------------------------------------------------------------

-- アプリ会員情報（アプリ会員登録時に作成したデータ）取得用SQL
SELECT_APL_MEM_INFO =
SELECT
    BIRTHDATE,                                                     -- 生年月日
    SEX_CODE,                                                      -- 性別コード
    MAIL_ADDRESS,                                                  -- メールアドレス
    MAIL_DELIVER_STATUS_DIVISION,                                  -- メール配信状態区分
    OP_AUTH_TIMES                                                  -- OP認証回数
FROM
    APL_MEM_INFO                                                   -- アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                   -- アプリ会員ID
    AND DELETED_FLG = '0'                                          -- 削除フラグ(0:未削除)

-- OP会員情報取得用SQL
SELECT_OP_MEM_INFO =
SELECT
    BIRTHDATE,                                                     -- 生年月日
    SEX_CODE                                                       -- 性別コード
FROM
    OP_MEM_INFO                                                    -- OP会員情報
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber                   -- 会員管理番号
    AND MEM_CTRL_NUM_BR_NUM = :memCtrlNumBrNum                     -- 会員管理番号枝番
    AND DELETED_FLG = '0'                                          -- 削除フラグ(0:未削除)

-- アプリ会員情報（アプリ会員登録時に作成したデータ）のOP認証状態更新用SQL
UPDATE_APL_MEM_INFO =
UPDATE
    APL_MEM_INFO                                                   -- アプリ会員情報
SET
    MEMBER_CONTROL_NUMBER = :memberControlNumber,                  -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM = :memCtrlNumBrNum,                        -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER = :osakaPitapaNumber,                      -- OP番号
    BIRTHDATE = :birthdate,                                        -- 生年月日
    SEX_CODE = :sexCode,                                           -- 性別コード
    APPLICATION_MEMBER_STATUS_CODE = :applicationMemberStatusCode, -- アプリ会員状態コード
    OP_AUTH_TIMES = :opAuthTimes,                                  -- OP認証回数
    UPDATE_USER_ID = :updateUserId,                                -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,                            -- 最終更新日時
    VERSION = VERSION + 1                                          -- バージョン番号
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                   -- アプリ会員ID
    AND DELETED_FLG = '0'                                          -- 削除フラグ(0:未削除)

-- アプリ会員IDに紐づくマイル残高情報取得用SQL
SELECT_MILE_BALANCE_INFO_BY_APL_MEM =
SELECT
    APPLICATION_MEMBER_ID,                                         -- アプリ会員ID
    OBJECT_YEAR_MONTH,                                             -- 対象年月
    MILE_BALANCE                                                   -- マイル残高
FROM
    MILE_BALANCE_INFO                                              -- マイル残高情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                   -- アプリ会員ID
    AND OBJECT_YEAR_MONTH >= :objectYearMonth                      -- 対象年月
    AND MILE_BALANCE > 0                                           -- マイル残高
    AND DELETED_FLG = '0'                                          -- 削除フラグ(0:未削除)

-- 対象年月、アプリ会員に紐づくマイル残高情報取得用SQL
SELECT_MILE_BALANCE_INFO =
SELECT
    MILE_BALANCE                                                   -- マイル残高
FROM
    MILE_BALANCE_INFO                                              -- マイル残高情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                   -- アプリ会員ID
    AND OBJECT_YEAR_MONTH = :objectYearMonth                       -- 対象年月
    AND DELETED_FLG = '0'                                          -- 削除フラグ(0:未削除)

-- マイル残高情報登録(アプリ会員へマイル残高統合)用SQL
INSERT_MILE_BALANCE_INFO =
INSERT INTO
    MILE_BALANCE_INFO                                              -- マイル残高情報
    (
    APPLICATION_MEMBER_ID,                                         -- アプリ会員ID
    OBJECT_YEAR_MONTH,                                             -- 対象年月
    MILE_BALANCE,                                                  -- マイル残高
    INSERT_USER_ID,                                                -- 登録者ID
    INSERT_DATE_TIME,                                              -- 登録日時
    UPDATE_USER_ID,                                                -- 最終更新者ID
    UPDATE_DATE_TIME,                                              -- 最終更新日時
    DELETED_FLG,                                                   -- 削除フラグ
    DELETED_DATE,                                                  -- 論理削除日
    VERSION                                                        -- バージョン番号
    )
    VALUES
    (
    :applicationMemberId,                                          -- アプリ会員ID
    :objectYearMonth,                                              -- 対象年月
    :mileBalance,                                                  -- マイル残高
    :insertUserId,                                                 -- 登録者ID
    :insertDateTime,                                               -- 登録日時
    :updateUserId,                                                 -- 最終更新者ID
    :updateDateTime,                                               -- 最終更新日時
    :deletedFlg,                                                   -- 削除フラグ(0:未削除)
    :deletedDate,                                                  -- 論理削除日
    :version                                                       -- バージョン番号
    )

-- マイル残高情報更新(アプリ会員へマイル残高統合)用SQL
UPDATE_ADD_MILE_BALANCE_INFO =
UPDATE
    MILE_BALANCE_INFO                                              -- マイル残高情報
SET
    MILE_BALANCE = :mileBalance,                                   -- マイル残高
    UPDATE_USER_ID = :updateUserId,                                -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,                            -- 最終更新日時
    VERSION = VERSION + 1                                          -- バージョン番号
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                   -- アプリ会員ID
    AND OBJECT_YEAR_MONTH = :objectYearMonth                       -- 対象年月
    AND DELETED_FLG = '0'                                          -- 削除フラグ(0:未削除)

-- アプリ会員のマイル残高移行用SQL
UPDATE_CLEAR_MILE_BALANCE_INFO =
UPDATE
    MILE_BALANCE_INFO                                              -- マイル残高情報
SET
    MILE_BALANCE = :mileBalance,                                   -- マイル残高
    UPDATE_USER_ID = :updateUserId,                                -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,                            -- 最終更新日時
    VERSION = VERSION + 1                                          -- バージョン番号
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                   -- アプリ会員ID
    AND OBJECT_YEAR_MONTH >= :objectYearMonth                      -- 対象年月
    AND MILE_BALANCE > 0                                           -- マイル残高
    AND DELETED_FLG = '0'                                          -- 削除フラグ(0:未削除)

-- OP会員のマイル残高情報論理削除用SQL
UPDATE_DELETED_MILE_BALANCE_INFO =
UPDATE
    MILE_BALANCE_INFO                                              -- マイル残高情報
SET
    DELETED_FLG = :deletedFlg,                                     -- 削除フラグ
    UPDATE_USER_ID = :updateUserId,                                -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,                            -- 最終更新日時
    VERSION = VERSION + 1                                          -- バージョン番号
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                   -- アプリ会員ID
    AND OBJECT_YEAR_MONTH >= :objectYearMonth                      -- 対象年月
    AND DELETED_FLG = '0'                                          -- 削除フラグ(0:未削除)

-- アプリ会員のマイル履歴情報登録用SQL
INSERT_MILE_HIST_INFO =
INSERT INTO
    MILE_HISTORY_INFORMATION                                       -- マイル履歴情報
    (
    MILE_HISTORY_ID,                                               -- マイル履歴ID
    APPLICATION_MEMBER_ID,                                         -- アプリ会員ID
    MILE_ADD_SUB_RCPT_NUM,                                         -- マイル加算減算受付番号
    MILE_CATEGORY_CODE,                                            -- マイル種別コード
    MILE_AMOUNT,                                                   -- マイル数
    MILE_HISTORY_REGIST_DATE,                                      -- マイル履歴登録日
    INSERT_USER_ID,                                                -- 登録者ID
    INSERT_DATE_TIME,                                              -- 登録日時
    UPDATE_USER_ID,                                                -- 最終更新者ID
    UPDATE_DATE_TIME,                                              -- 最終更新日時
    DELETED_FLG,                                                   -- 削除フラグ
    DELETED_DATE                                                   -- 論理削除日
    )
    VALUES
    (
    :mileHistoryId,                                                -- マイル履歴ID
    :applicationMemberId,                                          -- アプリ会員ID
    :mileAddSubRcptNum,                                            -- マイル加算減算受付番号
    :mileCategoryCode,                                             -- マイル種別コード
    :mileAmount,                                                   -- マイル数
    :mileHistoryRegistDate,                                        -- マイル履歴登録日
    :insertUserId,                                                 -- 登録者ID
    :insertDateTime,                                               -- 登録日時
    :updateUserId,                                                 -- 最終更新者ID
    :updateDateTime,                                               -- 最終更新日時
    :deletedFlg,                                                   -- 削除フラグ(0:未削除)
    :deletedDate                                                   -- 論理削除日
    )

-- マイル履歴情報取得用SQL
SELECT_MILE_HISTORY_INFORMATION=
SELECT
    MILE_AMOUNT,                                      -- マイル履歴情報TBL.マイル数
    MILE_ADD_SUB_RCPT_NUM                             -- マイル履歴情報TBL.マイル加算減算受付番号
FROM
    MILE_HISTORY_INFORMATION                          -- マイル履歴情報TBL
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId      -- マイル履歴情報TBL.アプリ会員ID = アプリ会員情報TBL.アプリ会員ID
    AND MILE_CATEGORY_CODE = :mileCategoryCode        -- マイル履歴情報TBL.マイル種別コード = 新規登録ボーナス
    AND DELETED_FLG = '0'                             -- マイル履歴情報TBL.削除フラグ = "0"(未削除)

-- マイル残高情報取得用SQL
SELECT_MILE_BALANCE_SUM=
SELECT
    NVL(SUM(MILE_BALANCE), 0) AS MILE_BALANCE_SUM                 -- マイル残高情報TBL.マイル残高
FROM
    MILE_BALANCE_INFO                                             -- マイル残高情報TBL
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                  -- マイル残高情報TBL.アプリ会員ID
    AND OBJECT_YEAR_MONTH >= :objectYearMonth                     -- マイル残高情報TBL.対象年月
    AND MILE_BALANCE > 0                                          -- マイル残高情報TBL.マイル残高
    AND DELETED_FLG = '0'                                         -- マイル残高情報TBL.削除フラグ(0:未削除)