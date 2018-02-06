------------------------------------------------------------------------------------------------------
-- マイル失効バッチ(B136A011Action)
------------------------------------------------------------------------------------------------------

-- マイル失効対象情報取得するSQL
SELECT_MILE_INV_OBJ_INFO =
SELECT
    MBI.APPLICATION_MEMBER_ID,                     --アプリ会員ID
    SUM(MBI.MILE_BALANCE) SUM_MILE_BALANCE,        --マイル残高合計
    AMI.MEMBER_CONTROL_NUMBER,                     --会員管理番号
    AMI.MEM_CTRL_NUM_BR_NUM,                       --会員管理番号枝番
    AMI.OSAKA_PITAPA_NUMBER                        --OP番号
FROM
    APL_MEM_INFO AMI                              --アプリ会員情報
INNER JOIN
    MILE_BALANCE_INFO MBI                         --マイル残高情報
ON
    AMI.APPLICATION_MEMBER_ID = MBI.APPLICATION_MEMBER_ID      --アプリ会員ID
WHERE
    AMI.DELETED_FLG = '0'                            --削除フラグ(0:未削除)
    AND AMI.OSAKA_PITAPA_WITHDRAW_FLAG = '0'         --OP退会フラグ(0:未退会)
    AND AMI.OSAKA_PITAPA_AUTHENTICATE_FLAG = '0'     --OP認証フラグ
    AND MBI.DELETED_FLG = '0'                        --削除フラグ(0:未削除)
    AND MBI.OBJECT_YEAR_MONTH BETWEEN :mileInvalidFromMonth AND :mileInvalidToMonth      --対象年月
GROUP BY
    MBI.APPLICATION_MEMBER_ID,                   --アプリ会員ID
    AMI.MEMBER_CONTROL_NUMBER,                   --会員管理番号
    AMI.MEM_CTRL_NUM_BR_NUM,                     --会員管理番号枝番
    AMI.OSAKA_PITAPA_NUMBER                      --OP番号
ORDER BY
    MBI.APPLICATION_MEMBER_ID                    --アプリ会員ID

--マイル失効対象一時情報登録するSQL
INSERT_MILE_INV_OBJ_TEMP_INFO =
INSERT INTO
    MILE_INV_OBJ_TEMP_INFO                       --マイル失効対象一時情報
    (
    APPLICATION_MEMBER_ID,                       --アプリ会員ID
    MEMBER_CONTROL_NUMBER,                       --会員管理番号
    MEM_CTRL_NUM_BR_NUM,                         --会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                         --OP番号
    INVALID_OBJECT_MILE_AMOUNT,                  --失効対象マイル数
    PROCESSED_FLAG,                              --処理済フラグ
    INSERT_USER_ID,                              --登録者ID
    INSERT_DATE_TIME,                            --登録日時
    UPDATE_USER_ID,                              --最終更新者ID
    UPDATE_DATE_TIME,                            --最終更新日時
    DELETED_FLG,                                 --削除フラグ
    DELETED_DATE                                 --論理削除日
    )
VALUES
    (
    :applicationMemberId,                        --アプリ会員ID
    :memberControlNumber,                        --会員管理番号
    :memCtrlNumBrNum,                            --会員管理番号枝番
    :osakaPitapaNumber,                          --OP番号
    :sumMileBalance,                             --失効対象マイル数
    :processedFlag,                              --処理済フラグ
    :insertUserId,                               --登録者ID
    :insertDateTime,                             --登録日時
    :updateUserId,                               --最終更新者ID
    :updateDateTime,                             --最終更新日時
    :deletedFlg,                                 --削除フラグ
    :deletedDate                                 --論理削除日
    )
