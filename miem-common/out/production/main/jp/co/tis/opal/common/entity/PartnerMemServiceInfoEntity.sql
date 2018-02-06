------------------------------------------------------------------------------------------------------
--パートナー会員サービス情報排他制御用SQL
------------------------------------------------------------------------------------------------------
SELECT_PARTNER_MEM_SERVICE_INFO_FOR_DELETE =
SELECT
    PARTNER_MEM_SERVICE_CTRL_ID,                    -- パートナー会員サービス管理ID
    PARTNER_REGIST_CTRL_NUM,                        -- パートナー登録者会員管理番号
    PARTNER_REGIST_CTRL_BR_NUM,                     -- パートナー登録者会員管理番号枝番
    PARTNER_USER_MEM_CTRL_NUM,                      -- パートナー会員管理番号
    PARTNER_USER_MEM_CTRL_BR_NUM,                   -- パートナー会員管理番号枝番
    ADMIT_STATUS_DIVISION,                          -- 承認状況区分
    APPLY_START_DATE_TIME,                          -- 適用開始日時
    APPLY_END_DATE_TIME,                            -- 適用終了日時
    INSERT_USER_ID,                                 -- 登録者ID
    INSERT_DATE_TIME,                               -- 登録日時
    UPDATE_USER_ID,                                 -- 最終更新者ID
    UPDATE_DATE_TIME,                               -- 最終更新日時
    DELETED_FLG,                                    -- 削除フラグ
    DELETED_DATE,                                   -- 論理削除日
    VERSION                                         -- バージョン番号
FROM
    PARTNER_MEM_SERVICE_INFO                        -- パートナー会員サービス情報
WHERE
    (
        (
        PARTNER_REGIST_CTRL_NUM = :memberControlNumber     -- パートナー登録者会員管理番号
        AND PARTNER_REGIST_CTRL_BR_NUM = :memCtrlNumBrNum  -- パートナー登録者会員管理番号枝番
        )
        OR
        (
        PARTNER_USER_MEM_CTRL_NUM = :memberControlNumber   -- パートナー会員管理番号
        AND PARTNER_USER_MEM_CTRL_BR_NUM = :memCtrlNumBrNum-- パートナー会員管理番号枝番
        )
    )
    AND DELETED_FLG = '0'                                  -- 削除フラグ(0:未削除)
FOR UPDATE

------------------------------------------------------------------------------------------------------
--パートナー会員サービス情報排他制御用SQL
------------------------------------------------------------------------------------------------------
SELECT_PARTNER_MEM_SERVICE_INFO =
SELECT
    PARTNER_MEM_SERVICE_CTRL_ID,                    -- パートナー会員サービス管理ID
    PARTNER_REGIST_CTRL_NUM,                        -- パートナー登録者会員管理番号
    PARTNER_REGIST_CTRL_BR_NUM,                     -- パートナー登録者会員管理番号枝番
    PARTNER_USER_MEM_CTRL_NUM,                      -- パートナー会員管理番号
    PARTNER_USER_MEM_CTRL_BR_NUM,                   -- パートナー会員管理番号枝番
    ADMIT_STATUS_DIVISION,                          -- 承認状況区分
    APPLY_START_DATE_TIME,                          -- 適用開始日時
    APPLY_END_DATE_TIME,                            -- 適用終了日時
    INSERT_USER_ID,                                 -- 登録者ID
    INSERT_DATE_TIME,                               -- 登録日時
    UPDATE_USER_ID,                                 -- 最終更新者ID
    UPDATE_DATE_TIME,                               -- 最終更新日時
    DELETED_FLG,                                    -- 削除フラグ
    DELETED_DATE,                                   -- 論理削除日
    VERSION                                         -- バージョン番号
FROM
    PARTNER_MEM_SERVICE_INFO                        -- パートナー会員サービス情報
WHERE
    PARTNER_MEM_SERVICE_CTRL_ID = :partnerMemServiceCtrlId   --パートナー会員サービス管理ID
    AND DELETED_FLG = '0'                                    -- 削除フラグ(0:未削除)
FOR UPDATE

------------------------------------------------------------------------------------------------------
-- アプリ会員情報・パートナー会員サービス情報
------------------------------------------------------------------------------------------------------
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
FOR UPDATE