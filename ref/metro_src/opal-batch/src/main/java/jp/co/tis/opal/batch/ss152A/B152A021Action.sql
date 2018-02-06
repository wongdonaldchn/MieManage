------------------------------------------------------------------------------------------------------
-- プッシュ通知一括指示バッチ(B152A021Action)
------------------------------------------------------------------------------------------------------

--プッシュ通知情報を取得するSQL
SELECT_PUSH_NOTICE_INFORMATION =
SELECT DISTINCT
    PNI.PUSH_NOTICE_ID,                                          --プッシュ通知ID
    PNI.OPAL_PROCESS_ID,                                         --処理ID
    PNI.DELIVER_TYPE,                                            --送信タイプ
    PNI.DELIVER_DATE_TIME,                                       --送信日時
    PNTI.FREE_WORD,                                              --フリーワード
    PNTI.SUBJECT,                                                --件名
    PNTI.BODY                                                    --本文
FROM
    PUSH_NOTICE_INFORMATION PNI                                  --プッシュ通知情報
INNER JOIN
    PUSH_NOTICE_TMPL_INFO PNTI                                   --プッシュ通知テンプレート情報
ON
    PNI.TEMPLATE_ID = PNTI.TEMPLATE_ID                           --テンプレートID
WHERE
    $if(opalProcessId) {PNI.OPAL_PROCESS_ID = :opalProcessId}    --処理ID
    AND PNI.DELIVER_DIVISION = :deliverDivision                  --送信区分:2(一括送信)
    AND PNI.PROCESSED_FLAG = '0'                                 --処理済フラグ:0(未処理)
    AND PNI.DELETED_FLG = '0'                                    --削除フラグ(0:未削除)
    AND PNTI.DELETED_FLG = '0'                                   --削除フラグ(0:未削除)
ORDER BY
    PNI.PUSH_NOTICE_ID                                            --プッシュ通知ID

--プッシュ通知送信先情報を取得するSQL
SELECT_PUSH_NOTICE_DEST_INFO =
SELECT
    APPLICATION_MEMBER_ID,                                       --アプリ会員ID
    APPLICATION_ID,                                              --アプリID
    DEVICE_ID                                                    --デバイスID
FROM
    PUSH_NOTICE_DEST_INFO                                        --プッシュ通知送信先情報
WHERE
    PUSH_NOTICE_ID = :pushNoticeId                               --プッシュ通知ID
    AND DELETED_FLG = '0'                                        --削除フラグ(0:未削除)
ORDER BY
    DEVICE_ID,                                                   --デバイスID
    SEQUENCE_MEMBER                                              --連番

--プッシュ通知情報を更新するSQL
UPDATE_PUSH_NOTICE_INFORMATION =
UPDATE
    PUSH_NOTICE_INFORMATION                                      --プッシュ通知情報
SET
    PUSH_NOTICE_DISTINGUISH_ID = :pushNoticeDistinguishId,       --プッシュ通知識別ID
    PROCESSED_FLAG = :processedFlag,                             --処理済フラグ
    DELIVER_DATE_TIME = :deliverDateTime,                        --送信日時
    UPDATE_USER_ID = :updateUserId,                              --最終更新者ID
    UPDATE_DATE_TIME = :updateDatetime                           --最終更新日時
WHERE
    PUSH_NOTICE_ID = :pushNoticeId                               --プッシュ通知ID
    AND DELETED_FLG = '0'                                        --削除フラグ(0:未削除)
