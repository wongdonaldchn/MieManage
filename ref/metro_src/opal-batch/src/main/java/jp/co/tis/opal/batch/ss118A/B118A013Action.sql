------------------------------------------------------------------------------------------------------
--移動情報差分チェック(B118A013Action)
------------------------------------------------------------------------------------------------------
-- 主なご利用駅情報・アプリ会員情報
SELECT_MAIN_USE_STA_TEMP_INFO=
SELECT
    APL.APPLICATION_MEMBER_ID,                                                                      -- アプリ会員ID
    APL.APPLICATION_ID,                                                                             -- アプリID
    APL.DEVICE_ID                                                                                   -- デバイスID
FROM
    MAIN_USE_STA_INFO STA                                                                           -- 主なご利用駅情報
INNER JOIN
    APL_MEM_INFO APL                                                                                -- アプリ会員情報
ON
    STA.MEMBER_CONTROL_NUMBER = APL.MEMBER_CONTROL_NUMBER                                            -- 会員管理番号
    AND STA.MEM_CTRL_NUM_BR_NUM = APL.MEM_CTRL_NUM_BR_NUM                                           -- 会員管理番号枝番
WHERE
    (NVL(STA.LAST_TIME_REGIST_STATION_1,' ') <> NVL(STA.THIS_TIME_REGIST_STATION_1,' ')                 -- 主なご利用駅情報TBL.前回登録駅1 ≠ 主なご利用駅情報TBL.今回登録駅1
    OR NVL(STA.LAST_TIME_REGIST_STATION_2,' ') <> NVL(STA.THIS_TIME_REGIST_STATION_2,' ')               -- 主なご利用駅情報TBL.前回登録駅2 ≠ 主なご利用駅情報TBL.今回登録駅2
    OR NVL(STA.LAST_TIME_REGIST_STATION_3,' ') <> NVL(STA.THIS_TIME_REGIST_STATION_3,' ')               -- 主なご利用駅情報TBL.前回登録駅3 ≠ 主なご利用駅情報TBL.今回登録駅3
    OR NVL(STA.LAST_TIME_REGIST_STATION_4,' ') <> NVL(STA.THIS_TIME_REGIST_STATION_4,' ')               -- 主なご利用駅情報TBL.前回登録駅4 ≠ 主なご利用駅情報TBL.今回登録駅4
    OR NVL(STA.LAST_TIME_REGIST_STATION_5,' ') <> NVL(STA.THIS_TIME_REGIST_STATION_5,' '))              -- 主なご利用駅情報TBL.前回登録駅5 ≠ 主なご利用駅情報TBL.今回登録駅5
    AND STA.MOVE_INFO_RIN_DATE = :moveInfoRinDate                                                   -- 移動情報取込日付
    AND STA.DELETED_FLG = '0'                                                                       -- 削除フラグ = "0"
    AND APL.APPLICATION_MEMBER_STATUS_CODE = :applicationMemberStatusCode                           -- アプリ会員情報TBL.アプリ会員状態コード = "A"（OP認証済のアプリ会員）
    AND APL.DELETED_FLG = '0'                                                                       -- アプリ会員情報TBL.削除フラグ = "0"(未削除)
ORDER BY
    STA.MEMBER_CONTROL_NUMBER ASC,                                                                  -- 主なご利用駅情報TBL.会員管理番号(昇順)
    STA.MEM_CTRL_NUM_BR_NUM ASC                                                                     -- 主なご利用駅情報TBL.会員管理番号枝番(昇順)
