------------------------------------------------
-- 乗車適用日登録API
------------------------------------------------
-- アプリ会員情報取得
SELECT_APL_MEM_INFO =
SELECT
    APPLICATION_MEMBER_ID,                                    --アプリ会員ID
    APPLICATION_MEMBER_STATUS_CODE                            --アプリ会員状態コード
FROM
    APL_MEM_INFO                                              --アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId              --乗車適用日登録要求電文.アプリ会員ID
    -- アプリ会員情報TBL.アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員),"D"(OP非会員))
    AND APPLICATION_MEMBER_STATUS_CODE IN (:opAuthAplMem, :notOpMem)
    AND DELETED_FLG = '0'                                     --削除フラグ(0:未削除)

-- 家族乗車適用日取得
SELECT_FAMILY_RIDE_APPLY_DATE =
SELECT
    FAMILY_MEM_SERVICE_CTRL_ID                                --家族会員サービス管理ID
FROM
    FAMILY_RIDE_APPLY_DATE                                    --家族乗車適用日
WHERE
    FAMILY_MEM_SERVICE_CTRL_ID = :memServiceCtrlId            --家族会員サービス管理ID
    AND RIDE_APPLY_DATE = :rideApplyDate                      --乗車適用日(システム日付)

-- パートナー乗車適用日取得
SELECT_PARTNER_RIDE_APPLY_DATE =
SELECT
    PARTNER_MEM_SERVICE_CTRL_ID                                --パートナー会員サービス管理ID
FROM
    PARTNER_RIDE_APPLY_DATE                                    --パートナー乗車適用日
WHERE
    PARTNER_MEM_SERVICE_CTRL_ID = :memServiceCtrlId            --パートナー会員サービス管理ID
    AND RIDE_APPLY_DATE = :rideApplyDate                       --乗車適用日(システム日付)

-- 家族乗車適用日登録
INSERT_FAMILY_RIDE_APPLY_DATE =
INSERT INTO
    FAMILY_RIDE_APPLY_DATE             --家族乗車適用日
    (
    FAMILY_MEM_SERVICE_CTRL_ID,        --家族会員サービス管理ID
    RIDE_APPLY_DATE,                   --乗車適用日
    RIDE_APPLY_YEAR_MONTH,             --乗車適用年月
    USER_CHOOSE_DIVISION,              --ユーザ選択区分
    INSERT_USER_ID,                    --登録者ID
    INSERT_DATE_TIME,                  --登録日時
    UPDATE_USER_ID,                    --最終更新者ID
    UPDATE_DATE_TIME,                  --最終更新日時
    DELETED_FLG,                       --削除フラグ
    DELETED_DATE                       --論理削除日
    )
VALUES
    (
    :memServiceCtrlId,                 --家族会員サービス管理ID
    :rideApplyDate,                    --乗車適用日
    :rideApplyYearMonth,               --乗車適用年月
    :userChooseDivision,               --ユーザ選択区分
    :insertUserId,                     --登録者ID
    :insertDateTime,                   --登録日時
    :updateUserId,                     --最終更新者ID
    :updateDateTime,                   --最終更新日時
    :deletedFlg,                       --削除フラグ(0:未削除)
    :deletedDate                       --論理削除日
    )

-- パートナー乗車適用日登録
INSERT_PARTNER_RIDE_APPLY_DATE =
INSERT INTO
    PARTNER_RIDE_APPLY_DATE            --パートナー乗車適用日
    (
    PARTNER_MEM_SERVICE_CTRL_ID,       --パートナー会員サービス管理ID
    RIDE_APPLY_DATE,                   --乗車適用日
    RIDE_APPLY_YEAR_MONTH,             --乗車適用年月
    USER_CHOOSE_DIVISION,              --ユーザ選択区分
    INSERT_USER_ID,                    --登録者ID
    INSERT_DATE_TIME,                  --登録日時
    UPDATE_USER_ID,                    --最終更新者ID
    UPDATE_DATE_TIME,                  --最終更新日時
    DELETED_FLG,                       --削除フラグ
    DELETED_DATE                       --論理削除日
    )
VALUES
    (
    :memServiceCtrlId,                 --パートナー会員サービス管理ID
    :rideApplyDate,                    --乗車適用日
    :rideApplyYearMonth,               --乗車適用年月
    :userChooseDivision,               --ユーザ選択区分
    :insertUserId,                     --登録者ID
    :insertDateTime,                   --登録日時
    :updateUserId,                     --最終更新者ID
    :updateDateTime,                   --最終更新日時
    :deletedFlg,                       --削除フラグ(0:未削除)
    :deletedDate                       --論理削除日
    )

-- アプリ会員情報・家族会員サービス情報取得
SELECT_FAMILY_MEM_SERVICE_INFO_BY_APL=
SELECT
    FMSI.FAMILY_MEM_SERVICE_CTRL_ID,                                       -- 家族会員サービス管理ID
    FMSI.MEMBER_CONTROL_NUMBER,                                            -- 会員管理番号
    FMSI.APPLICANT_MEM_CTRL_NUM_BR_NUM,                                    -- 申込者会員管理番号枝番
    FMSI.RCPT_DATE_TIME,                                                   -- 受付日時
    FMSI.REGIST_STATUS_DIVISION,                                           -- 登録状況区分
    FMSI.APPLY_START_DATE_TIME,                                            -- 適用開始日時
    FMSI.APPLY_END_DATE_TIME,                                              -- 適用終了日時
    FMSI.INSERT_USER_ID,                                                   -- 登録者ID
    FMSI.INSERT_DATE_TIME,                                                 -- 登録日時
    FMSI.UPDATE_USER_ID,                                                   -- 最終更新者ID
    FMSI.UPDATE_DATE_TIME,                                                 -- 最終更新日時
    FMSI.DELETED_FLG,                                                      -- 削除フラグ
    FMSI.DELETED_DATE,                                                     -- 論理削除日
    FMSI.VERSION                                                           -- バージョン番号
FROM
    APL_MEM_INFO AMI                                                       --アプリ会員情報
INNER JOIN
    FAMILY_MEM_SERVICE_INFO FMSI                                           --家族会員サービス情報
ON
    AMI.MEMBER_CONTROL_NUMBER = FMSI.MEMBER_CONTROL_NUMBER                 --アプリ会員情報TBL.会員管理番号 = 家族会員サービス情報TBL.会員管理番号
WHERE
    AMI.APPLICATION_MEMBER_ID = :applicationMemberId                       --アプリ会員ID
    AND AMI.APPLICATION_MEMBER_STATUS_CODE = :applicationMemberStatusCode  --アプリ会員状態コード(A:OP認証済みのアプリ会員)
    AND AMI.DELETED_FLG = '0'                                              --削除フラグ(0:未削除)
    AND FMSI.REGIST_STATUS_DIVISION = :registStatusDivision                --登録状況区分（1:登録）
    AND FMSI.DELETED_FLG = '0'                                             --削除フラグ(0:未削除)

-- アプリ会員情報・パートナー会員サービス情報
SELECT_PARTNER_MEM_SERVICE_INFO_BY_APL=
SELECT
    PMSI.PARTNER_MEM_SERVICE_CTRL_ID,                                   -- パートナー会員サービス管理ID
    PMSI.PARTNER_REGIST_CTRL_NUM,                                       -- パートナー登録者会員管理番号
    PMSI.PARTNER_REGIST_CTRL_BR_NUM,                                    -- パートナー登録者会員管理番号枝番
    PMSI.PARTNER_USER_MEM_CTRL_NUM,                                     -- パートナー会員管理番号
    PMSI.PARTNER_USER_MEM_CTRL_BR_NUM,                                  -- パートナー会員管理番号枝番
    PMSI.ADMIT_STATUS_DIVISION,                                         -- 承認状況区分
    PMSI.APPLY_START_DATE_TIME,                                         -- 適用開始日時
    PMSI.APPLY_END_DATE_TIME,                                           -- 適用終了日時
    PMSI.INSERT_USER_ID,                                                -- 登録者ID
    PMSI.INSERT_DATE_TIME,                                              -- 登録日時
    PMSI.UPDATE_USER_ID,                                                -- 最終更新者ID
    PMSI.UPDATE_DATE_TIME,                                              -- 最終更新日時
    PMSI.DELETED_FLG,                                                   -- 削除フラグ
    PMSI.DELETED_DATE,                                                  -- 論理削除日
    PMSI.VERSION                                                        -- バージョン番号
FROM
    APL_MEM_INFO AMI                                                    --アプリ会員情報
INNER JOIN
    PARTNER_MEM_SERVICE_INFO PMSI                                       --パートナー会員サービス情報
ON
    (
      (
        --パートナー会員サービス情報TBL.パートナー登録者会員管理番号 = アプリ会員情報TBL.会員管理番号
        PMSI.PARTNER_REGIST_CTRL_NUM = AMI.MEMBER_CONTROL_NUMBER
        --パートナー会員サービス情報TBL.パートナー登録者会員管理番号枝番 = アプリ会員情報TBL.会員管理番号枝番
        AND
        PMSI.PARTNER_REGIST_CTRL_BR_NUM = AMI.MEM_CTRL_NUM_BR_NUM
      )
      OR
      (
        --パートナー会員サービス情報TBL.パートナー会員管理番号 = アプリ会員情報TBL.会員管理番号
        PMSI.PARTNER_USER_MEM_CTRL_NUM = AMI.MEMBER_CONTROL_NUMBER
        --パートナー会員サービス情報TBL.パートナー会員管理番号枝番 = アプリ会員情報TBL.会員管理番号枝番
        AND
        PMSI.PARTNER_USER_MEM_CTRL_BR_NUM = AMI.MEM_CTRL_NUM_BR_NUM
      )
    )
WHERE
    AMI.APPLICATION_MEMBER_ID = :applicationMemberId                        --アプリ会員ID
    AND AMI.APPLICATION_MEMBER_STATUS_CODE = :applicationMemberStatusCode  --アプリ会員状態コード(A:OP認証済みのアプリ会員)
    AND AMI.DELETED_FLG = '0'                                              --削除フラグ(0:未削除)
    AND PMSI.ADMIT_STATUS_DIVISION = :admitStatusDivision                  --承認状況区分（1:承認済み）
    AND PMSI.DELETED_FLG = '0'                                             --削除フラグ(0:未削除)
