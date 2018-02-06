------------------------------------------------------------------------------------------------------
-- 主なご利用駅一時情報作成バッチ(B118A011Action)
------------------------------------------------------------------------------------------------------
-- 主なご利用駅一時情報登録
INSERT_MAIN_USE_STAION_TEMP_INFO =
INSERT INTO
    MAIN_USE_STA_TEMP_INFO                               --主なご利用駅一時情報
    (
    MEMBER_CONTROL_NUMBER,                               --会員管理番号
    MEM_CTRL_NUM_BR_NUM,                                 --会員管理番号枝番
    REGIST_STATION_1,                                    --登録駅1
    REGIST_STATION_2,                                    --登録駅2
    REGIST_STATION_3,                                    --登録駅3
    REGIST_STATION_4,                                    --登録駅4
    REGIST_STATION_5,                                    --登録駅5
    PROCESSED_FLAG,                                      --処理済フラグ
    INSERT_USER_ID,                                      --登録者ID
    INSERT_DATE_TIME,                                    --登録日時
    UPDATE_USER_ID,                                      --最終更新者ID
    UPDATE_DATE_TIME,                                    --最終更新日時
    DELETED_FLG,                                         --削除フラグ
    DELETED_DATE                                         --論理削除日
    )
VALUES
    (
    :memberControlNumber,                                --会員管理番号
    :memrCtrlBrNum,                                      --会員管理番号枝番
    :registStation1,                                     --登録駅1
    :registStation2,                                     --登録駅2
    :registStation3,                                     --登録駅3
    :registStation4,                                     --登録駅4
    :registStation5,                                     --登録駅5
    :processedFlag,                                      --処理済フラグ
    :insertUserId,                                       --登録者ID
    :insertDateTime,                                     --登録日時
    :updateUserId,                                       --最終更新者ID
    :updateDateTime,                                     --最終更新日時
    :deletedFlag,                                        --削除フラグ
    :deletedDate                                         --論理削除日
    )