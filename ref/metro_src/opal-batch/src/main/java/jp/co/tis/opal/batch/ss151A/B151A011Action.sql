------------------------------------------------------------------------------------------------------
-- メール配信個別指示バッチ(B151A011Action)
------------------------------------------------------------------------------------------------------

--メール個別配信情報を取得するSQL
SELECT_MAIL_LITE_DELIVER_INFO=
SELECT
    MAIL_LITE_DELIVER_ID,                              --メール個別配信ID
    DELIVER_TYPE,                                      --配信タイプ
    MAIL_ADDRESS,                                      --メールアドレス
    TEMPLATE_ID,                                       --テンプレートID
    VARIABLE_ITEM_VALUE_1,                             --差し込み項目01
    VARIABLE_ITEM_VALUE_2,                             --差し込み項目02
    VARIABLE_ITEM_VALUE_3,                             --差し込み項目03
    VARIABLE_ITEM_VALUE_4,                             --差し込み項目04
    VARIABLE_ITEM_VALUE_5,                             --差し込み項目05
    VARIABLE_ITEM_VALUE_6,                             --差し込み項目06
    VARIABLE_ITEM_VALUE_7,                             --差し込み項目07
    VARIABLE_ITEM_VALUE_8,                             --差し込み項目08
    VARIABLE_ITEM_VALUE_9,                             --差し込み項目09
    VARIABLE_ITEM_VALUE_10,                            --差し込み項目10
    DELIVER_DATE,                                      --配信日時
    SUBJECT,                                           --件名
    BODY,                                              --本文
    FROM_ADDRESS,                                      --Fromアドレス
    FROM_NAME,                                         --From差出人名
    VARIABLE_ITEM_NAME_1,                              --差し込み項目名01
    VARIABLE_ITEM_NAME_2,                              --差し込み項目名02
    VARIABLE_ITEM_NAME_3,                              --差し込み項目名03
    VARIABLE_ITEM_NAME_4,                              --差し込み項目名04
    VARIABLE_ITEM_NAME_5,                              --差し込み項目名05
    VARIABLE_ITEM_NAME_6,                              --差し込み項目名06
    VARIABLE_ITEM_NAME_7,                              --差し込み項目名07
    VARIABLE_ITEM_NAME_8,                              --差し込み項目名08
    VARIABLE_ITEM_NAME_9,                              --差し込み項目名09
    VARIABLE_ITEM_NAME_10                              --差し込み項目名10
FROM
    (
    SELECT
        MLDI.MAIL_LITE_DELIVER_ID,                     --メール個別配信ID
        MLDI.DELIVER_TYPE,                             --配信タイプ
        MLDI.MAIL_ADDRESS,                             --メールアドレス
        MLDI.TEMPLATE_ID,                              --テンプレートID
        MLDI.VARIABLE_ITEM_VALUE_1,                    --差し込み項目01
        MLDI.VARIABLE_ITEM_VALUE_2,                    --差し込み項目02
        MLDI.VARIABLE_ITEM_VALUE_3,                    --差し込み項目03
        MLDI.VARIABLE_ITEM_VALUE_4,                    --差し込み項目04
        MLDI.VARIABLE_ITEM_VALUE_5,                    --差し込み項目05
        MLDI.VARIABLE_ITEM_VALUE_6,                    --差し込み項目06
        MLDI.VARIABLE_ITEM_VALUE_7,                    --差し込み項目07
        MLDI.VARIABLE_ITEM_VALUE_8,                    --差し込み項目08
        MLDI.VARIABLE_ITEM_VALUE_9,                    --差し込み項目09
        MLDI.VARIABLE_ITEM_VALUE_10,                   --差し込み項目10
        MLDI.DELIVER_DATE,                             --配信日時
        MDTI.SUBJECT,                                  --件名
        MDTI.BODY,                                     --本文
        MDTI.FROM_ADDRESS,                             --Fromアドレス
        MDTI.FROM_NAME,                                --From差出人名
        MDTI.VARIABLE_ITEM_NAME_1,                     --差し込み項目名01
        MDTI.VARIABLE_ITEM_NAME_2,                     --差し込み項目名02
        MDTI.VARIABLE_ITEM_NAME_3,                     --差し込み項目名03
        MDTI.VARIABLE_ITEM_NAME_4,                     --差し込み項目名04
        MDTI.VARIABLE_ITEM_NAME_5,                     --差し込み項目名05
        MDTI.VARIABLE_ITEM_NAME_6,                     --差し込み項目名06
        MDTI.VARIABLE_ITEM_NAME_7,                     --差し込み項目名07
        MDTI.VARIABLE_ITEM_NAME_8,                     --差し込み項目名08
        MDTI.VARIABLE_ITEM_NAME_9,                     --差し込み項目名09
        MDTI.VARIABLE_ITEM_NAME_10                     --差し込み項目名10
    FROM
        MAIL_LITE_DELIVER_INFO MLDI                    --メール個別配信情報
    INNER JOIN
        MAIL_DELIVER_TMPL_INFO MDTI                    --メール配信テンプレート情報
    ON
        MLDI.TEMPLATE_ID = MDTI.TEMPLATE_ID            --テンプレートID
    WHERE
        MLDI.MAIL_DELIVER_STATUS = :mailDeliverStatus  --メール配信状況
        AND MLDI.DELETED_FLG = '0'                     --削除フラグ(0:未削除)
        AND MDTI.DELETED_FLG = '0'                     --削除フラグ(0:未削除)
    ORDER BY
        MLDI.MAIL_LITE_DELIVER_ID                      --メール個別配信ID
    )
WHERE
    ROWNUM <= :getCount                                 --取得件数

--メール個別配信情報を更新するSQL
UPDATE_MAIL_LITE_DELIVER_INFO=
UPDATE
    MAIL_LITE_DELIVER_INFO                             --メール個別配信情報
SET
    MAIL_DELIVER_STATUS = :mailDeliverStatus,          --メール配信状況
    DELIVER_SERVICE_MAIL_ID = :deliverServiceMailId,   --配信サービスメールID
    UPDATE_USER_ID = :updateUserId,                    --最終更新者ID
    UPDATE_DATE_TIME = :updateDatetime                 --最終更新日時
WHERE
    MAIL_LITE_DELIVER_ID = :mailLiteDeliverId          --メール個別配信ID
