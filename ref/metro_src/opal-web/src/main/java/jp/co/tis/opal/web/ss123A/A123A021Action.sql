------------------------------------------------
-- パートナー会員サービス情報取得API
------------------------------------------------
-- アプリ会員情報取得
SELECT_APL_MEM_INFO=
SELECT
    APPLICATION_MEMBER_ID,               -- アプリ会員ID
    MEMBER_CONTROL_NUMBER,               -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                 -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                 -- OP番号
    APPLICATION_MEMBER_STATUS_CODE       -- アプリ会員状態コード
FROM
    APL_MEM_INFO                         --アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId  --アプリ会員ID
    -- アプリ会員情報TBL.アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員),"D"(OP非会員))
    AND APPLICATION_MEMBER_STATUS_CODE IN (:opAuthAplMem, :notOpMem)
    AND DELETED_FLG = '0'               --削除フラグ(0:未削除)

-- パートナー会員サービス情報取得
SELECT_PARTNER_MEM_SERVICE_INFO=
SELECT
    PMSI.PARTNER_MEM_SERVICE_CTRL_ID,                                   -- パートナー会員サービス管理ID
    CAST(:applicationMemberId AS NUMBER) REGIST_APPLICATION_MEMBER_ID,  -- パートナー登録者のアプリ会員ID:パートナー会員サービス情報取得要求電文.アプリ会員ID
    CAST(:osakaPitapaNumber AS CHAR(10)) REGIST_OSAKA_PITAPA_NUMBER,   -- パートナー登録者のOP番号:2.3.4.1)で取得したOP番号
    AMI.APPLICATION_MEMBER_ID PARTNER_APPLICATION_MEMBER_ID,            -- パートナーのアプリ会員ID:アプリ会員情報TBL(1).アプリ会員ID
    AMI.OSAKA_PITAPA_NUMBER PARTNER_OSAKA_PITAPA_NUMBER,                -- パートナーのOP番号:アプリ会員情報TBL(1).OP番号
    PMSI.APPLY_START_DATE_TIME                                          -- 適用開始日時
FROM
    PARTNER_MEM_SERVICE_INFO PMSI                                       -- パートナー会員サービス情報
INNER JOIN APL_MEM_INFO AMI                                            -- アプリ会員情報TBL(1)
ON
    -- パートナー会員サービス情報TBL.パートナー会員番号 = アプリ会員情報TBL(1).会員管理番号
    PMSI.PARTNER_USER_MEM_CTRL_NUM = AMI.MEMBER_CONTROL_NUMBER
    -- パートナー会員サービス情報TBL.パートナー会員管理番号枝番 = アプリ会員情報TBL(1).会員管理番号枝番
    AND PMSI.PARTNER_USER_MEM_CTRL_BR_NUM = AMI.MEM_CTRL_NUM_BR_NUM
WHERE
    -- パートナー会員サービス情報TBL.パートナー登録者会員管理番号 = 2.3.4.1)で取得した会員管理番号
    PMSI.PARTNER_REGIST_CTRL_NUM = :memberControlNumber
    -- パートナー会員サービス情報TBL.パートナー登録者会員管理番号枝番 = 2.3.4.1)で取得した会員管理番号枝番
    AND PMSI.PARTNER_REGIST_CTRL_BR_NUM = :memCtrlNumBrNum
    -- パートナー会員サービス情報TBL.承認状況区分 = "1"(承認済み)
    AND PMSI.ADMIT_STATUS_DIVISION = :admitStatusDivision
    -- アプリ会員情報TBL(1).アプリ会員状態コード = "A"(OP認証済みのアプリ会員)
    AND AMI.APPLICATION_MEMBER_STATUS_CODE = :applicationMemberStatusCode
    -- パートナー会員サービス情報TBL.削除フラグ = "0"(未削除)
    AND PMSI.DELETED_FLG = '0'
    -- アプリ会員情報TBL(1).削除フラグ = "0"(未削除)
    AND AMI.DELETED_FLG = '0'
UNION ALL
SELECT
    PMSI.PARTNER_MEM_SERVICE_CTRL_ID,                                    -- パートナー会員サービス管理ID
    AMI.APPLICATION_MEMBER_ID REGIST_APPLICATION_MEMBER_ID,              -- パートナー登録者のアプリ会員ID:アプリ会員情報TBL(2).アプリ会員ID
    AMI.OSAKA_PITAPA_NUMBER REGIST_OSAKA_PITAPA_NUMBER,                  -- パートナー登録者のOP番号:アプリ会員情報TBL(2).OP番号
    CAST(:applicationMemberId AS NUMBER) PARTNER_APPLICATION_MEMBER_ID,  -- パートナーのアプリ会員ID:パートナー会員サービス情報取得要求電文.アプリ会員ID
    CAST(:osakaPitapaNumber AS CHAR(10)) PARTNER_OSAKA_PITAPA_NUMBER,   -- パートナーのOP番号:2.3.4.1)で取得したOP番号
    PMSI.APPLY_START_DATE_TIME                                           -- 適用開始日時
FROM
    PARTNER_MEM_SERVICE_INFO PMSI                                        -- パートナー会員サービス情報
INNER JOIN APL_MEM_INFO AMI                                             -- アプリ会員情報TBL(2)
ON
    -- パートナー会員サービス情報TBL.パートナー登録者会員番号 = アプリ会員情報TBL(2).会員管理番号
    PMSI.PARTNER_REGIST_CTRL_NUM = AMI.MEMBER_CONTROL_NUMBER
    -- パートナー会員サービス情報TBL.パートナー登録者会員管理番号枝番 = アプリ会員情報TBL(2).会員管理番号枝番
    AND PMSI.PARTNER_REGIST_CTRL_BR_NUM = AMI.MEM_CTRL_NUM_BR_NUM
WHERE
    -- パートナー会員サービス情報TBL.パートナー会員管理番号 = 2.3.4.1)で取得した会員管理番号
    PMSI.PARTNER_USER_MEM_CTRL_NUM = :memberControlNumber
    -- パートナー会員サービス情報TBL.パートナー会員管理番号枝番 = 2.3.4.1)で取得した会員管理番号枝番
    AND PMSI.PARTNER_USER_MEM_CTRL_BR_NUM = :memCtrlNumBrNum
    -- パートナー会員サービス情報TBL.承認状況区分 = "1"(承認済み)
    AND PMSI.ADMIT_STATUS_DIVISION = :admitStatusDivision
    -- アプリ会員情報TBL(2).アプリ会員状態コード = "A"(OP認証済みのアプリ会員)
    AND AMI.APPLICATION_MEMBER_STATUS_CODE = :applicationMemberStatusCode
    -- パートナー会員サービス情報TBL.削除フラグ = "0"(未削除)
    AND PMSI.DELETED_FLG = '0'
    -- アプリ会員情報TBL(2).削除フラグ = "0"(未削除)
    AND AMI.DELETED_FLG = '0'