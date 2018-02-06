------------------------------------------------
-- 乗車適用日参照API
------------------------------------------------
-- アプリ会員情報取得
SELECT_APL_MEM_INFO =
SELECT
    APPLICATION_MEMBER_ID,                                    --アプリ会員ID
    APPLICATION_MEMBER_STATUS_CODE                            --アプリ会員状態コード
FROM
    APL_MEM_INFO                                              --アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId              --乗車適用日参照要求電文.アプリ会員ID
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

-- 家族会員サービス情報取得
SELECT_FAMILY_MEM_SERVICE_CTRL_ID=
SELECT
    FMSI.FAMILY_MEM_SERVICE_CTRL_ID                                         --家族会員サービス管理ID
FROM
    APL_MEM_INFO AMI                                                        --アプリ会員情報
INNER JOIN
    FAMILY_MEM_SERVICE_INFO FMSI                                            --家族会員サービス情報
ON
    AMI.MEMBER_CONTROL_NUMBER = FMSI.MEMBER_CONTROL_NUMBER                  --アプリ会員情報TBL.会員管理番号 = 家族会員サービス情報TBL.会員管理番号
WHERE
    AMI.APPLICATION_MEMBER_ID = :applicationMemberId                        --アプリ会員情報TBL.アプリ会員ID = 乗車適用日参照要求電文.アプリ会員ID
    AND AMI.APPLICATION_MEMBER_STATUS_CODE = :applicationMemberStatusCode   --アプリ会員情報TBL.アプリ会員状態コード = "A"(OP認証実施済み)
    AND AMI.DELETED_FLG = '0'                                               --アプリ会員情報TBL.削除フラグ = "0"(未削除)
    AND FMSI.REGIST_STATUS_DIVISION = :registStatusDivision                 --家族会員サービス情報TBL.登録状況区分 = "1"(登録)
    AND FMSI.DELETED_FLG = '0'                                              --家族会員サービス情報TBL.削除フラグ = "0"(未削除)

-- 家族乗車適用日（前月分）取得
SELECT_FAMILY_RIDE_APPLY_DATE_LAST_MONTH=
SELECT
    RIDE_APPLY_DATE,                                           --乗車適用日
    USER_CHOOSE_DIVISION                                       --ユーザ選択区分
FROM
    FAMILY_RIDE_APPLY_DATE                                     --家族乗車適用日
WHERE
    FAMILY_MEM_SERVICE_CTRL_ID = :memServiceCtrlId             --家族会員サービス管理ID
    AND RIDE_APPLY_YEAR_MONTH = :rideApplyYearMonth           --乗車適用年月(システム日付の年月 - 1ヶ月)
    AND DELETED_FLG = '0'                                     --削除フラグ = "0"(未削除)
ORDER BY RIDE_APPLY_DATE ASC                                 --家族乗車適用日TBL.乗車適用日 昇順

-- 家族乗車適用日（今月分）取得
SELECT_FAMILY_RIDE_APPLY_DATE_THIS_MONTH=
SELECT
    RIDE_APPLY_DATE,                                           --乗車適用日
    USER_CHOOSE_DIVISION                                       --ユーザ選択区分
FROM
    FAMILY_RIDE_APPLY_DATE                                     --家族乗車適用日
WHERE
    FAMILY_MEM_SERVICE_CTRL_ID = :memServiceCtrlId             --家族会員サービス管理ID
    AND RIDE_APPLY_YEAR_MONTH = :rideApplyYearMonth            --乗車適用年月(システム日付の年月)
    AND DELETED_FLG = '0'                                      --削除フラグ = "0"(未削除)
ORDER BY RIDE_APPLY_DATE ASC                                  --家族乗車適用日TBL.乗車適用日 昇順

-- パートナー会員サービス情報取得
SELECT_PARTNER_MEM_SERVICE_CTRL_ID=
SELECT
    PMSI.PARTNER_MEM_SERVICE_CTRL_ID                            --パートナー会員サービス管理ID
FROM
    APL_MEM_INFO AMI                                            --アプリ会員情報
INNER JOIN
    PARTNER_MEM_SERVICE_INFO PMSI                               --パートナー会員サービス情報
ON
    (
      --アプリ会員情報TBL.会員管理番号 = パートナー会員サービス情報TBL.パートナー登録者会員管理番号
      AMI.MEMBER_CONTROL_NUMBER = PMSI.PARTNER_REGIST_CTRL_NUM
      --アプリ会員情報TBL.会員管理番号枝番 = パートナー会員サービス情報TBL.パートナー登録者会員管理番号枝番
      AND AMI.MEM_CTRL_NUM_BR_NUM = PMSI.PARTNER_REGIST_CTRL_BR_NUM
    )
    OR
    (
      --アプリ会員情報TBL.会員管理番号 = パートナー会員サービス情報TBL.パートナー会員管理番号
      AMI.MEMBER_CONTROL_NUMBER = PMSI.PARTNER_USER_MEM_CTRL_NUM
      --アプリ会員情報TBL.会員管理番号枝番 = パートナー会員サービス情報TBL.パートナー会員管理番号枝番
      AND AMI.MEM_CTRL_NUM_BR_NUM = PMSI.PARTNER_USER_MEM_CTRL_BR_NUM
    )
WHERE
    AMI.APPLICATION_MEMBER_ID = :applicationMemberId                        --アプリ会員情報TBL.アプリ会員ID = 乗車適用日参照要求電文.アプリ会員ID
    AND AMI.APPLICATION_MEMBER_STATUS_CODE = :applicationMemberStatusCode   --アプリ会員情報TBL.アプリ会員状態コード = "A"(OP認証実施済み)
    AND AMI.DELETED_FLG = '0'                                               --アプリ会員情報TBL.削除フラグ = "0"(未削除)
    AND PMSI.ADMIT_STATUS_DIVISION = :admitStatusDivision                   --パートナー会員サービス情報TBL.承認状況区分 = "1"(承認済み)
    AND PMSI.DELETED_FLG = '0'                                              --パートナー会員サービス情報TBL.削除フラグ = "0"(未削除)

-- パートナー乗車適用日（前月分）取得
SELECT_PARTNER_RIDE_APPLY_DATE_LAST_MONTH=
SELECT
    RIDE_APPLY_DATE,                                           --乗車適用日
    USER_CHOOSE_DIVISION                                       --ユーザ選択区分
FROM
    PARTNER_RIDE_APPLY_DATE                                    --パートナー乗車適用日
WHERE
    PARTNER_MEM_SERVICE_CTRL_ID = :memServiceCtrlId           --パートナー会員サービス管理ID
    AND RIDE_APPLY_YEAR_MONTH = :rideApplyYearMonth           --乗車適用年月(システム日付の年月 - 1ヶ月)
    AND DELETED_FLG = '0'                                     --削除フラグ = "0"(未削除)
ORDER BY RIDE_APPLY_DATE ASC                                 --パートナー乗車適用日TBL.乗車適用日 昇順

-- パートナー乗車適用日（今月分）取得
SELECT_PARTNER_RIDE_APPLY_DATE_THIS_MONTH=
SELECT
    RIDE_APPLY_DATE,                                           --乗車適用日
    USER_CHOOSE_DIVISION                                       --ユーザ選択区分
FROM
    PARTNER_RIDE_APPLY_DATE                                    --パートナー乗車適用日
WHERE
    PARTNER_MEM_SERVICE_CTRL_ID = :memServiceCtrlId            --パートナー会員サービス管理ID
    AND RIDE_APPLY_YEAR_MONTH = :rideApplyYearMonth           --乗車適用年月(システム日付の年月)
    AND DELETED_FLG = '0'                                     --削除フラグ = "0"(未削除)
ORDER BY RIDE_APPLY_DATE ASC                                 --パートナー乗車適用日TBL.乗車適用日 昇順