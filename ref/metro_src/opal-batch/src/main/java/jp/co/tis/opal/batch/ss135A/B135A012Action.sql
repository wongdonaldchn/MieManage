------------------------------------------------------------------------------------------------------
-- 乗車マイル情報加算バッチ(B135A012Action)
------------------------------------------------------------------------------------------------------

-- 乗車マイル取込一時情報・アプリ会員情報
SELECT_RIDE_MILE_RIN_TEMP_INFO =
SELECT
    MILE.MEMBER_CONTROL_NUMBER,                                  -- 乗車マイル取込一時情報TBL.会員管理番号
    MILE.MEM_CTRL_NUM_BR_NUM,                                    -- 乗車マイル取込一時情報TBL.会員管理番号枝番
    MILE.OBJECT_YEAR_MONTH,                                      -- 乗車マイル取込一時情報TBL.対象年月
    MILE.RIDE_MILE_AMOUNT,                                       -- 乗車マイル取込一時情報TBL.乗車マイル数
    MILE.MILE_CATEGORY_CODE,                                     -- 乗車マイル取込一時情報TBL.マイル種別コード
    APL.APPLICATION_MEMBER_ID,                                   -- アプリ会員情報TBL.アプリ会員ID
    APL.APPLICATION_ID,                                          -- アプリ会員情報TBL.アプリID
    APL.DEVICE_ID,                                               -- アプリ会員情報TBL.デバイスID
    APL.APPLICATION_MEMBER_STATUS_CODE,                          -- アプリ会員情報TBL.アプリ会員状態コード
    APL.OSAKA_PITAPA_WITHDRAW_FLAG                               -- アプリ会員情報TBL.OP退会フラグ
FROM
    RIDE_MILE_RIN_TEMP_INFO MILE                                 -- 乗車マイル取込一時情報
LEFT JOIN
    APL_MEM_INFO APL                                             -- アプリ会員情報
ON
    MILE.MEMBER_CONTROL_NUMBER = APL.MEMBER_CONTROL_NUMBER       -- 乗車マイル取込一時情報TBL.会員管理番号 = アプリ会員情報TBL.会員管理番号
    AND MILE.MEM_CTRL_NUM_BR_NUM = APL.MEM_CTRL_NUM_BR_NUM       -- 乗車マイル取込一時情報TBL.会員管理番号枝番 = アプリ会員情報TBL.会員管理番号枝番
    AND APL.DELETED_FLG = '0'                                    -- アプリ会員情報TBL.削除フラグ = "0"(未削除)
    AND APL.OSAKA_PITAPA_AUTHENTICATE_FLAG = '0'                 -- アプリ会員情報TBL.OP認証フラグ = "0"(OP認証未済み)
WHERE
    MILE.PROCESSED_FLAG = '0'                                    -- 乗車マイル取込一時情報TBL.処理済フラグ = "0"(未処理)
    AND MILE.DELETED_FLG = '0'                                   -- 乗車マイル取込一時情報TBL.削除フラグ = "0"(未削除)
ORDER BY
    MILE.MEMBER_CONTROL_NUMBER ASC,                              -- 乗車マイル取込一時情報TBL.会員管理番号 (昇順)
    MILE.MEM_CTRL_NUM_BR_NUM ASC                                 -- 乗車マイル取込一時情報TBL.会員管理番号枝番 (昇順)

-- アプリ会員情報更新（C⇒B）
UPDATE_APL_MEM_INFO =
UPDATE
    APL_MEM_INFO                                                        -- アプリ会員情報
SET
    APPLICATION_MEMBER_STATUS_CODE = :statusB,                          -- アプリ会員状態コード
    UPDATE_USER_ID = :updateUserId,                                     -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,                                 -- 最終更新日時
    VERSION = VERSION + 1                                               -- バージョン番号
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                        -- アプリ会員ID
    AND DELETED_FLG = :deletedFlg                                       -- 削除フラグ

-- 乗車マイル取込一時情報更新
UPDATE_RIDE_MILE_RIN_TEMP_INFO =
UPDATE
    RIDE_MILE_RIN_TEMP_INFO                                             -- 乗車マイル取込一時情報
SET
    PROCESSED_FLAG = :processedFlag,                                    -- 処理済フラグ
    UPDATE_USER_ID = :updateUserId,                                     -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime                                  -- 最終更新日時
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber                        -- 乗車マイル取込一時情報TBL.会員管理番号 = 取得した会員管理番号
    AND MEM_CTRL_NUM_BR_NUM = :memCtrlNumBrNum                          -- 乗車マイル取込一時情報TBL.会員管理番号枝番 = 取得した会員管理番号枝番
    AND OBJECT_YEAR_MONTH = :objectYearMonth                            -- 乗車マイル取込一時情報TBL.対象年月 = 取得した対象年月
    AND RIDE_MILE_AMOUNT = :rideMileAmount                              -- 乗車マイル取込一時情報TBL.乗車マイル数 = 取得した乗車マイル数
    AND MILE_CATEGORY_CODE = :mileCategoryCode                          -- 乗車マイル取込一時情報TBL.マイル種別コード = 取得したマイル種別コード
