------------------------------------------------
-- 乗車適用日選択API
------------------------------------------------
-- アプリ会員情報取得
SELECT_APL_MEM_INFO =
SELECT
    APPLICATION_MEMBER_ID,                                    --アプリ会員ID
    APPLICATION_MEMBER_STATUS_CODE                            --アプリ会員状態コード
FROM
    APL_MEM_INFO                                              --アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId              --乗車適用日選択要求電文.アプリ会員ID
    -- アプリ会員情報TBL.アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員),"D"(OP非会員))
    AND APPLICATION_MEMBER_STATUS_CODE IN (:opAuthAplMem, :notOpMem)
    AND DELETED_FLG = '0'                                     --削除フラグ(0:未削除)

-- 乗車適用日登録上限回数取得
SELECT_RIDE_UPPER_LIMIT_TIMES=
SELECT
    UPPER_LIMIT_TIMES                                --上限回数
FROM
    RIDE_UPPER_LIMIT_TIMES                           --乗車適用日登録上限回数
WHERE
    RIDE_APPLY_YEAR_MONTH = :rideApplyYearMonth      --乗車適用年月
    AND SERVICE_DIVISION = :serviceDivision          --サービス区分
    AND DELETED_FLG = '0'                            --削除フラグ = "0"(未削除)

-- 家族乗車適用日選択状況初期化
UPDATE_FAMILY_RIDE_APPLY_DATE_CLEAR=
UPDATE
    FAMILY_RIDE_APPLY_DATE                                     --家族乗車適用日
SET
    USER_CHOOSE_DIVISION = :userChooseDivision,                --ユーザ選択区分
    UPDATE_USER_ID = :updateUserId,                            --最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime                         --最終更新日時
WHERE
    FAMILY_MEM_SERVICE_CTRL_ID = :memServiceCtrlId             --家族会員サービス管理ID
    AND RIDE_APPLY_YEAR_MONTH = :rideApplyYearMonth           --乗車適用年月(システム日付の年月)
    AND DELETED_FLG = '0'                                     --削除フラグ = "0"(未削除)

-- 家族乗車適用日選択
UPDATE_FAMILY_RIDE_APPLY_DATE=
UPDATE
    FAMILY_RIDE_APPLY_DATE                                     --家族乗車適用日
SET
    USER_CHOOSE_DIVISION = :userChooseDivision,                --ユーザ選択区分
    UPDATE_USER_ID = :updateUserId,                            --最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime                         --最終更新日時
WHERE
    FAMILY_MEM_SERVICE_CTRL_ID = :memServiceCtrlId             --家族会員サービス管理ID
    AND RIDE_APPLY_DATE = :rideApplyDate                      --乗車適用日(乗車適用日選択要求電文.乗車適用日情報リスト.乗車適用日)
    AND DELETED_FLG = '0'                                     --削除フラグ = "0"(未削除)

-- パートナー乗車適用日選択状況初期化
UPDATE_PARTNER_RIDE_APPLY_DATE_CLEAR=
UPDATE
    PARTNER_RIDE_APPLY_DATE                                    --パートナー乗車適用日
SET
    USER_CHOOSE_DIVISION = :userChooseDivision,                --ユーザ選択区分
    UPDATE_USER_ID = :updateUserId,                            --最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime                         --最終更新日時
WHERE
    PARTNER_MEM_SERVICE_CTRL_ID = :memServiceCtrlId            --パートナー会員サービス管理ID
    AND RIDE_APPLY_YEAR_MONTH = :rideApplyYearMonth            --乗車適用年月(システム日付の年月)
    AND DELETED_FLG = '0'                                      --削除フラグ = "0"(未削除)

-- パートナー乗車適用日選択
UPDATE_PARTNER_RIDE_APPLY_DATE=
UPDATE
    PARTNER_RIDE_APPLY_DATE                                    --パートナー乗車適用日
SET
    USER_CHOOSE_DIVISION = :userChooseDivision,                --ユーザ選択区分
    UPDATE_USER_ID = :updateUserId,                            --最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime                         --最終更新日時
WHERE
    PARTNER_MEM_SERVICE_CTRL_ID = :memServiceCtrlId            --パートナー会員サービス管理ID
    AND RIDE_APPLY_DATE = :rideApplyDate                      --乗車適用日(乗車適用日選択要求電文.乗車適用日情報リスト.乗車適用日)
    AND DELETED_FLG = '0'                                     --削除フラグ = "0"(未削除)