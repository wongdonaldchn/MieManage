------------------------------------------------------------------------------------------------------
-- アプリ会員情報・家族会員サービス情報取得
------------------------------------------------------------------------------------------------------
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
FOR UPDATE