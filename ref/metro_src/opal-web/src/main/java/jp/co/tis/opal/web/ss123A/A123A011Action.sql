------------------------------------------------
-- パートナー会員サービス登録API
------------------------------------------------
-- パートナー会員サービス情報取得
SELECT_PARTNER_MEM_SERVICE_INFO =
SELECT
    PARTNER_MEM_SERVICE_CTRL_ID,                                   -- パートナー会員サービス管理番号
    PARTNER_REGIST_CTRL_NUM,                                       -- パートナー登録者会員管理番号
    PARTNER_REGIST_CTRL_BR_NUM,                                    -- パートナー登録者会員管理番号枝番
    PARTNER_USER_MEM_CTRL_NUM,                                     -- パートナー会員管理番号
    PARTNER_USER_MEM_CTRL_BR_NUM                                   -- パートナー会員管理番号枝番
FROM
    PARTNER_MEM_SERVICE_INFO                                       -- パートナー会員サービス情報TBL
WHERE
    (
      (PARTNER_REGIST_CTRL_NUM = :memberControlNumber              -- パートナー会員サービス情報TBL.パートナー登録者会員管理番号 = 2.2.2（1）で取得した会員管理番号
      AND PARTNER_REGIST_CTRL_BR_NUM = :memCtrlNumBrNum           -- パートナー会員サービス情報TBL.パートナー登録者会員管理番号枝番 = 2.2.2（1）で取得した会員管理番号枝番
      )
      OR
      (PARTNER_USER_MEM_CTRL_NUM = :memberControlNumber            --パートナー会員サービス情報TBL.パートナー会員管理番号 = 2.2.2（1）で取得した会員管理番号
      AND PARTNER_USER_MEM_CTRL_BR_NUM = :memCtrlNumBrNum         --パートナー会員サービス情報TBL.パートナー会員管理番号枝番 = 2.2.2（1）で取得した会員管理番号枝番
      )
    )
    AND ADMIT_STATUS_DIVISION = :admitStatusDivision              -- パートナー会員サービス情報TBL.承認状況区分 = "1"(承認済み)
    AND DELETED_FLG = '0'                                         -- アプリ会員情報TBL.削除フラグ = "0"(未削除)

-- パートナー会員サービス情報登録
INSERT_PARTNER_MEM_SERVICE_INFO =
INSERT INTO
    PARTNER_MEM_SERVICE_INFO            -- パートナー会員サービス情報
    (
    PARTNER_MEM_SERVICE_CTRL_ID,        -- パートナー会員サービス管理ID
    PARTNER_REGIST_CTRL_NUM,            -- パートナー登録者会員管理番号
    PARTNER_REGIST_CTRL_BR_NUM,         -- パートナー登録者会員管理番号枝番
    PARTNER_USER_MEM_CTRL_NUM,          -- パートナー会員管理番号
    PARTNER_USER_MEM_CTRL_BR_NUM,       -- パートナー会員管理番号枝番
    ADMIT_STATUS_DIVISION,              -- 承認状況区分
    APPLY_START_DATE_TIME,              -- 適用開始日時
    APPLY_END_DATE_TIME,                -- 適用終了日時
    INSERT_USER_ID,                     -- 登録者ID
    INSERT_DATE_TIME,                   -- 登録日時
    UPDATE_USER_ID,                     -- 最終更新者ID
    UPDATE_DATE_TIME,                   -- 最終更新日時
    DELETED_FLG,                        -- 削除フラグ
    DELETED_DATE,                       -- 論理削除日
    VERSION                             -- バージョン番号
    )
VALUES
    (
    :partnerMemServiceCtrlId,           -- パートナー会員サービス管理ID
    :partnerRegistCtrlNum,              -- パートナー登録者会員管理番号
    :partnerRegistCtrlBrNum,            -- パートナー登録者会員管理番号枝番
    :partnerUserMemCtrlNum,             -- パートナー会員管理番号
    :partnerUserMemCtrlBrNum,           -- パートナー会員管理番号枝番
    :admitStatusDivision,               -- 承認状況区分
    :applyStartDateTime,                -- 適用開始日時
    :applyEndDateTime,                  -- 適用終了日時
    :insertUserId,                      -- 登録者ID
    :insertDateTime,                    -- 登録日時
    :updateUserId,                      -- 最終更新者ID
    :updateDateTime,                    -- 最終更新日時
    :deletedFlg,                        -- 削除フラグ(0:未削除)
    :deletedDate,                       -- 論理削除日
    :version                            -- バージョン番号
    )