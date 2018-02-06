------------------------------------------------------------------------------------------------------
-- メール配信一括指示バッチ(B151A021Action)
------------------------------------------------------------------------------------------------------

--メール一括配信情報を取得するSQL
SELECT_MAIL_PACK_DELIVER_INFO=
SELECT
    MPDI.MAIL_PACK_DELIVER_ID,                                  --メール一括配信ID
    MPDI.DELIVER_TYPE,                                          --配信タイプ
    MPDI.TEMPLATE_ID,                                           --テンプレートID
    MPDI.DELIVER_FILE_NAME,                                     --配信ファイル名称
    MPDI.DELIVER_DATE,                                          --配信日時
    MDTI.SUBJECT,                                               --件名
    MDTI.BODY,                                                  --本文
    MDTI.FROM_ADDRESS,                                          --Fromアドレス
    MDTI.FROM_NAME                                              --From差出人
FROM
    MAIL_PACK_DELIVER_INFO MPDI                                 --メール一括配信情報
INNER JOIN
    MAIL_DELIVER_TMPL_INFO MDTI                                 --メール配信テンプレート情報
ON
    MPDI.TEMPLATE_ID = MDTI.TEMPLATE_ID                         --テンプレートID
WHERE
    MPDI.MAIL_DELIVER_STATUS = :mailDeliverStatus               --メール配信状況
    AND MPDI.DELETED_FLG = '0'                                  --削除フラグ(0:未削除)
    AND MDTI.DELETED_FLG = '0'                                  --削除フラグ(0:未削除)
ORDER BY
    MPDI.MAIL_PACK_DELIVER_ID                                   --メール一括配信ID

--メール一括配信情報を更新するSQL
UPDATE_MAIL_PACK_DELIVER_INFO=
UPDATE
    MAIL_PACK_DELIVER_INFO                                      --メール一括配信情報
SET
    MAIL_DELIVER_STATUS = :mailDeliverStatus,                   --メール配信状況
    DELIVER_SERVICE_MAIL_ID = :deliverServiceMailId,            --配信サービスメールID
    UPDATE_USER_ID = :updateUserId,                             --最終更新者ID
    UPDATE_DATE_TIME = :updateDatetime                          --最終更新日時
WHERE
    MAIL_PACK_DELIVER_ID = :mailPackDeliverId                   --メール一括配信ID