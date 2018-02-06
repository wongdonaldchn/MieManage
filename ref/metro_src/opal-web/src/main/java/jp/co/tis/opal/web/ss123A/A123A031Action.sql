------------------------------------------------
-- パートナー会員サービス解除API
------------------------------------------------
-- アプリ会員情報取得
SELECT_APPLICATION_MEMBER_STATUS_CODE =
SELECT
    APPLICATION_MEMBER_STATUS_CODE,                       -- アプリ会員状態コード
    MEMBER_CONTROL_NUMBER,                                -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM                                   -- 会員管理番号枝番
FROM
    APL_MEM_INFO                                          -- アプリ会員情報TBL
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId          -- アプリ会員情報TBL.アプリ会員ID = パートナー会員サービス更新要求電文.アプリ会員ID
    -- アプリ会員情報TBL.アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員),"D"(OP非会員))
    AND APPLICATION_MEMBER_STATUS_CODE IN (:opAuthAplMem, :notOpMem)
    AND DELETED_FLG = '0'                                 -- アプリ会員情報TBL.削除フラグ = "0"(未削除)

-- パートナー会員サービス情報取得
SELECT_PARTNER_MEM_SERVICE_INFO=
SELECT
    PARTNER_MEM_SERVICE_CTRL_ID                                   -- パートナー会員サービス管理ID
FROM
    PARTNER_MEM_SERVICE_INFO                                      --パートナー会員サービス情報
WHERE
    --パートナー会員サービス情報TBL.パートナー会員サービス管理ID = パートナー会員サービス更新要求電文.パートナー会員サービス管理ID
    PARTNER_MEM_SERVICE_CTRL_ID = :partnerMemServiceCtrlId
    AND
    (
      (
        --パートナー会員サービス情報TBL.パートナー登録者会員管理番号 = 取得したアプリ会員の会員管理番号
        PARTNER_REGIST_CTRL_NUM = :memberControlNumber
        --パートナー会員サービス情報TBL.パートナー登録者会員管理番号枝番 = 取得したアプリ会員の会員管理番号枝番
        AND
        PARTNER_REGIST_CTRL_BR_NUM = :memCtrlNumBrNum
      )
      OR
      (
        --パートナー会員サービス情報TBL.パートナー会員管理番号 = 取得したアプリ会員の会員管理番号
        PARTNER_USER_MEM_CTRL_NUM = :memberControlNumber
        --パートナー会員サービス情報TBL.パートナー会員管理番号枝番 = 取得したアプリ会員の会員管理番号枝番
        AND
        PARTNER_USER_MEM_CTRL_BR_NUM = :memCtrlNumBrNum
      )
    )
    AND ADMIT_STATUS_DIVISION = :admitStatusDivision             --承認状況区分（1:承認済み）
    AND DELETED_FLG = '0'                                        --削除フラグ(0:未削除)

-- パートナー会員サービス情報更新
UPDATE_PARTNER_MEM_SERVICE_INFO=
UPDATE
    PARTNER_MEM_SERVICE_INFO                                   --パートナー会員サービス情報
SET
    ADMIT_STATUS_DIVISION = :admitStatusDivision,              --承認状況区分
    APPLY_END_DATE_TIME = :applyEndDateTime,                   --適用終了日時
    UPDATE_USER_ID = :updateUserId,                            --最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,                        --最終更新日時
    DELETED_DATE = :deletedDate,                               --論理削除日
    VERSION = VERSION + 1                                      --バージョン番号
WHERE
    --パートナー会員サービス情報TBL.パートナー会員サービス管理ID = パートナー会員サービス更新要求電文.パートナー会員サービス管理ID
    PARTNER_MEM_SERVICE_CTRL_ID = :partnerMemServiceCtrlId
    AND DELETED_FLG = '0'                                     --削除フラグ = "0"(未削除)