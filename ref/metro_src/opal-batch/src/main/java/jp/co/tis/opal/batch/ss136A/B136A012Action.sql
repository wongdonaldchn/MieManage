------------------------------------------------------------------------------------------------------
-- マイル失効バッチ(B136A012Action)
------------------------------------------------------------------------------------------------------

-- マイル失効対象一時情報データ取得するSQL
SELECT_MILE_INV_OBJ_TEMP_INFO =
SELECT
    AMI.APPLICATION_MEMBER_ID,                  --アプリ会員ID
    AMI.APPLICATION_ID,                         --アプリID
    AMI.DEVICE_ID,                              --デバイスID
    AMI.MAIL_ADDRESS,                           --メールアドレス
    AMI.APPLICATION_MEMBER_STATUS_CODE,         --アプリ会員状態コード
    AMI.MAIL_DELIVER_STATUS_DIVISION,           --メール配信状態区分
    MIOTI.INVALID_OBJECT_MILE_AMOUNT,           --失効対象マイル数
    MIOTI.OSAKA_PITAPA_NUMBER,                  --OP番号
    MIOTI.MEMBER_CONTROL_NUMBER,                --会員管理番号
    MIOTI.MEM_CTRL_NUM_BR_NUM                   --会員管理番号枝番
FROM
    MILE_INV_OBJ_TEMP_INFO MIOTI                --マイル失効対象一時情報
INNER JOIN
    APL_MEM_INFO AMI                            --アプリ会員情報
ON
    MIOTI.APPLICATION_MEMBER_ID = AMI.APPLICATION_MEMBER_ID    --アプリ会員ID
WHERE
    MIOTI.PROCESSED_FLAG = '0'                  --処理済フラグ(0:未処理)
    AND MIOTI.DELETED_FLG = '0'                 --削除フラグ(0:未削除)
    AND AMI.DELETED_FLG = '0'                   --削除フラグ(0:未削除)
ORDER BY
    APPLICATION_MEMBER_ID                       --アプリ会員ID

-- マイル残高情報論理削除するSQL
UPDATE_MILE_BALANCE_DEL =
UPDATE
    MILE_BALANCE_INFO                           --マイル残高情報
SET
    UPDATE_USER_ID = :updateUserId,             --最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,         --最終更新日時
    DELETED_FLG = :deletedFlg,                  --削除フラグ(1:削除済)
    VERSION = VERSION + 1                       --バージョン番号
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId    --アプリ会員ID
    AND OBJECT_YEAR_MONTH BETWEEN :mileInvalidFromMonth AND :mileInvalidToMonth     --対象年月
    AND DELETED_FLG = '0'                      --削除フラグ(0:未削除)

-- マイル履歴登録するSQL
INSERT_MILE_HISTORY_INFORMATION =
INSERT INTO
    MILE_HISTORY_INFORMATION                   --マイル履歴登録
    (
    MILE_HISTORY_ID,                           --マイル履歴ID
    APPLICATION_MEMBER_ID,                     --アプリ会員ID
    MILE_ADD_SUB_RCPT_NUM,                     --マイル加算減算受付番号
    MILE_CATEGORY_CODE,                        --マイル種別コード
    MILE_AMOUNT,                               --マイル数
    MILE_HISTORY_REGIST_DATE,                  --マイル履歴登録日
    INSERT_USER_ID,                            --登録者ID
    INSERT_DATE_TIME,                          --登録日時
    UPDATE_USER_ID,                            --最終更新者ID
    UPDATE_DATE_TIME,                          --最終更新日時
    DELETED_FLG,                               --削除フラグ
    DELETED_DATE                               --論理削除日
    )
VALUES
    (
    :mileHistoryId,                            --マイル履歴ID
    :applicationMemberId,                      --アプリ会員ID
    :mileAddSubRcptNum,                        --マイル加算減算受付番号
    :mileCategoryCode,                         --マイル種別コード
    :invalidObjectMileAmount,                  --マイル数
    :mileHistoryRegistDate,                    --マイル履歴登録日
    :insertUserId,                             --登録者ID
    :insertDateTime,                           --登録日時
    :updateUserId,                             --最終更新者ID
    :updateDateTime,                           --最終更新日時
    :deletedFlg,                               --削除フラグ
    :deletedDate                               --論理削除日
    )

-- マイル移行一時情報登録するSQL
INSERT_MILE_TRANS_TEMP_INFO =
INSERT INTO
    MILE_TRANS_TEMP_INFO                       --マイル移行一時情報
    (
    MEMBER_CONTROL_NUMBER,                     --会員管理番号
    MEM_CTRL_NUM_BR_NUM,                       --会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                       --OP番号
    TRANSITION_MILE_AMOUNT,                    --移行マイル数
    MILE_TRANSITION_DIVISION,                  --マイル移行区分
    INSERT_USER_ID,                            --登録者ID
    INSERT_DATE_TIME,                          --登録日時
    UPDATE_USER_ID,                            --最終更新者ID
    UPDATE_DATE_TIME,                          --最終更新日時
    DELETED_FLG,                               --削除フラグ
    DELETED_DATE                               --論理削除日
    )
VALUES(
    :memberControlNumber,                      --会員管理番号
    :memCtrlNumBrNum,                          --会員管理番号枝番
    :osakaPitapaNumber,                        --OP番号
    :invalidObjectMileAmount,                  --移行マイル数
    :mileTransitionDivision,                   --マイル移行区分
    :insertUserId,                             --登録者ID
    :insertDateTime,                           --登録日時
    :updateUserId,                             --最終更新者ID
    :updateDateTime,                           --最終更新日時
    :deletedFlg,                               --削除フラグ
    :deletedDate                               --論理削除日
    )

-- マイル失効対象一時情報更新するSQL
UPDATE_MILE_INV_OBJ_DEL =
UPDATE
    MILE_INV_OBJ_TEMP_INFO                     --マイル失効対象一時情報
SET
    UPDATE_USER_ID = :updateUserId,            --最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,        --最終更新日時
    PROCESSED_FLAG = :processedFlag             --処理済フラグ(1:処理済)
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId    --アプリ会員ID
