------------------------------------------------------------------------------------------------------
-- 主なご利用駅情報反映(B118A012Action)
------------------------------------------------------------------------------------------------------
-- 主なご利用駅一時情報
SELECT_MAIN_USE_STAION_TEMP_INFO =
SELECT
    MEMBER_CONTROL_NUMBER,                                          --会員管理番号
    MEM_CTRL_NUM_BR_NUM,                                            --会員管理番号枝番
    REGIST_STATION_1,                                               --登録駅1
    REGIST_STATION_2,                                               --登録駅2
    REGIST_STATION_3,                                               --登録駅3
    REGIST_STATION_4,                                               --登録駅4
    REGIST_STATION_5                                                --登録駅5
FROM
    MAIN_USE_STA_TEMP_INFO                                          --主なご利用駅一時情報
WHERE
    PROCESSED_FLAG = '0'                                            --主なご利用駅一時情報TBL.処理済フラグ = "0"(未処理)
    AND DELETED_FLG = '0'                                           --主なご利用駅一時情報TBL.削除フラグ = "0"(未削除)
ORDER BY
    MEMBER_CONTROL_NUMBER ASC,                                      --主なご利用駅一時情報TBL.会員管理番号(昇順)
    MEM_CTRL_NUM_BR_NUM ASC                                         --主なご利用駅一時情報TBL.会員管理番号枝番(昇順)

-- 主なご利用駅情報
SELECT_MAIN_USE_STAION_INFO =
SELECT
    THIS_TIME_REGIST_STATION_1,                                      -- 今回登録駅1
    THIS_TIME_REGIST_STATION_2,                                      -- 今回登録駅2
    THIS_TIME_REGIST_STATION_3,                                      -- 今回登録駅3
    THIS_TIME_REGIST_STATION_4,                                      -- 今回登録駅4
    THIS_TIME_REGIST_STATION_5                                       -- 今回登録駅5
FROM
    MAIN_USE_STA_INFO                                                -- 主なご利用駅情報
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber                     -- 主なご利用駅情報TBL.会員管理番号
    AND MEM_CTRL_NUM_BR_NUM = :memCtrlNumBrNum                       -- 主なご利用駅情報TBL.会員管理番号枝番

-- 主なご利用駅情報更新
UPDATE_MAIN_USE_STAION_INFO =
UPDATE
    MAIN_USE_STA_INFO                                                -- 主なご利用駅情報
SET
    LAST_TIME_REGIST_STATION_1 = :lastTimeRegistStation1,            -- 前回登録駅1
    LAST_TIME_REGIST_STATION_2 = :lastTimeRegistStation2,            -- 前回登録駅2
    LAST_TIME_REGIST_STATION_3 = :lastTimeRegistStation3,            -- 前回登録駅3
    LAST_TIME_REGIST_STATION_4 = :lastTimeRegistStation4,            -- 前回登録駅4
    LAST_TIME_REGIST_STATION_5 = :lastTimeRegistStation5,            -- 前回登録駅5
    THIS_TIME_REGIST_STATION_1 = :thisTimeRegistStation1,            -- 今回登録駅1
    THIS_TIME_REGIST_STATION_2 = :thisTimeRegistStation2,            -- 今回登録駅2
    THIS_TIME_REGIST_STATION_3 = :thisTimeRegistStation3,            -- 今回登録駅3
    THIS_TIME_REGIST_STATION_4 = :thisTimeRegistStation4,            -- 今回登録駅4
    THIS_TIME_REGIST_STATION_5 = :thisTimeRegistStation5,            -- 今回登録駅5
    MOVE_INFO_RIN_DATE = :moveInfoRinDate,                           -- 移動情報取込日付
    UPDATE_USER_ID = :updateUserId,                                  -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,                              -- 最終更新日時
    VERSION = VERSION + 1                                            -- バージョン番号
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber                     -- 主なご利用駅情報TBL.会員管理番号
    AND MEM_CTRL_NUM_BR_NUM = :memCtrlNumBrNum                       -- 主なご利用駅情報TBL.会員管理番号枝番
    AND DELETED_FLG = :deletedFlg                                    -- 主なご利用駅情報TBL.削除フラグ

-- 主なご利用駅情報登録
INSERT_MAIN_USE_STAION_INFO =
INSERT INTO
    MAIN_USE_STA_INFO                                              --主なご利用駅情報
    (
    MEMBER_CONTROL_NUMBER,                                         --会員管理番号
    MEM_CTRL_NUM_BR_NUM,                                           --会員管理番号枝番
    LAST_TIME_REGIST_STATION_1,                                    --前回登録駅1
    LAST_TIME_REGIST_STATION_2,                                    --前回登録駅2
    LAST_TIME_REGIST_STATION_3,                                    --前回登録駅3
    LAST_TIME_REGIST_STATION_4,                                    --前回登録駅4
    LAST_TIME_REGIST_STATION_5,                                    --前回登録駅5
    THIS_TIME_REGIST_STATION_1,                                    --今回登録駅1
    THIS_TIME_REGIST_STATION_2,                                    --今回登録駅2
    THIS_TIME_REGIST_STATION_3,                                    --今回登録駅3
    THIS_TIME_REGIST_STATION_4,                                    --今回登録駅4
    THIS_TIME_REGIST_STATION_5,                                    --今回登録駅5
    MOVE_INFO_RIN_DATE,                                            --移動情報取込日付
    INSERT_USER_ID,                                                --登録者ID
    INSERT_DATE_TIME,                                              --登録日時
    UPDATE_USER_ID,                                                --最終更新者ID
    UPDATE_DATE_TIME,                                              --最終更新日時
    DELETED_FLG,                                                   --削除フラグ
    DELETED_DATE,                                                  --論理削除日
    VERSION                                                        --バージョン番号
    )
VALUES
    (
    :memberControlNumber,                                          --会員管理番号
    :memCtrlNumBrNum,                                              --会員管理番号枝番
    :lastTimeRegistStation1,                                       --前回登録駅1
    :lastTimeRegistStation2,                                       --前回登録駅2
    :lastTimeRegistStation3,                                       --前回登録駅3
    :lastTimeRegistStation4,                                       --前回登録駅4
    :lastTimeRegistStation5,                                       --前回登録駅5
    :thisTimeRegistStation1,                                       --今回登録駅1
    :thisTimeRegistStation2,                                       --今回登録駅2
    :thisTimeRegistStation3,                                       --今回登録駅3
    :thisTimeRegistStation4,                                       --今回登録駅4
    :thisTimeRegistStation5,                                       --今回登録駅5
    :moveInfoRinDate,                                              --移動情報取込日付
    :insertUserId,                                                 --登録者ID
    :insertDateTime,                                               --登録日時
    :updateUserId,                                                 --最終更新者ID
    :updateDateTime,                                               --最終更新日時
    :deletedFlag,                                                  --削除フラグ
    :deletedDate,                                                  --論理削除日
    :version                                                       --バージョン番号
    )

-- 主なご利用駅一時情報更新
UPDATE_MAIN_USE_STAION_TEMP_INFO =
UPDATE
    MAIN_USE_STA_TEMP_INFO                                         --主なご利用駅一時情報
SET
    PROCESSED_FLAG = :processedFlag,                               --処理済フラグ
    UPDATE_USER_ID = :updateUserId,                                --最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime                             --最終更新日時
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber                   --会員管理番号
    AND MEM_CTRL_NUM_BR_NUM = :memCtrlNumBrNum                     --会員管理番号枝番
