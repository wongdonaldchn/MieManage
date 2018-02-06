------------------------------------------------------------------------------------------------------
-- メール配信情報登録共通コンポーネント
------------------------------------------------------------------------------------------------------
--メール個別配信情報TBLにメール配信情報を登録する。
INSERT_MAIL_LITE_DELIVER_INFO =
INSERT INTO
    MAIL_LITE_DELIVER_INFO							--メール個別配信情報
    (
    MAIL_LITE_DELIVER_ID,							--メール個別配信ID
    OPAL_PROCESS_ID,								--処理ID
    DELIVER_TYPE,									--配信タイプ
    APPLICATION_MEMBER_ID,							--アプリ会員ID
    MAIL_ADDRESS,									--メールアドレス
    TEMPLATE_ID,									--テンプレートID
    VARIABLE_ITEM_VALUE_1,							--差し込み項目01
    VARIABLE_ITEM_VALUE_2,							--差し込み項目02
    VARIABLE_ITEM_VALUE_3,							--差し込み項目03
    VARIABLE_ITEM_VALUE_4,							--差し込み項目04
    VARIABLE_ITEM_VALUE_5,							--差し込み項目05
    VARIABLE_ITEM_VALUE_6,							--差し込み項目06
    VARIABLE_ITEM_VALUE_7,							--差し込み項目07
    VARIABLE_ITEM_VALUE_8,							--差し込み項目08
    VARIABLE_ITEM_VALUE_9,							--差し込み項目09
    VARIABLE_ITEM_VALUE_10,							--差し込み項目10
    DELIVER_DATE,									--配信日時
    MAIL_DELIVER_STATUS,							--メール配信状況
    INSERT_USER_ID,									--登録者ID
    INSERT_DATE_TIME,								--登録日時
    UPDATE_USER_ID,									--最終更新者ID
    UPDATE_DATE_TIME,								--最終更新日時
    DELETED_FLG,									--削除フラグ
    DELETED_DATE									--論理削除日
    )
    VALUES
    (
    :mailLiteDeliverId,								--メール個別配信ID
    :opalProcessId,									--処理ID
    :deliverType,									--配信タイプ
    :applicationMemberId,							--アプリ会員ID
    :mailAddress,									--メールアドレス
    :templateId,									--テンプレートID
    :variableItemValue1,							--差し込み項目01
    :variableItemValue2,							--差し込み項目02
    :variableItemValue3,							--差し込み項目03
    :variableItemValue4,							--差し込み項目04
    :variableItemValue5,							--差し込み項目05
    :variableItemValue6,							--差し込み項目06
    :variableItemValue7,							--差し込み項目07
    :variableItemValue8,							--差し込み項目08
    :variableItemValue9,							--差し込み項目09
    :variableItemValue10,							--差し込み項目10
    :deliverDate,									--配信日時
    :mailDeliverStatus,								--メール配信状況
    :insertUserId,									--登録者ID
    :insertDateTime,								--登録日時
    :updateUserId,									--最終更新者ID
    :updateDateTime,								--最終更新日時
    :deletedFlg,									--削除フラグ
    :deletedDate									--論理削除日
    )

--メール一括配信情報TBLにメール配信情報を登録する。
INSERT_MAIL_PACK_DELIVER_INFO =
INSERT INTO
    MAIL_PACK_DELIVER_INFO							--メール一括配信情報
    (
    MAIL_PACK_DELIVER_ID, 							--メール一括配信ID
    OPAL_PROCESS_ID,								--処理ID
    DELIVER_TYPE,									--配信タイプ
    TEMPLATE_ID,									--テンプレートID
    DELIVER_DATE,									--配信日時
    DELIVER_FILE_NAME,								--配信ファイル名称
    MAIL_DELIVER_STATUS,							--メール配信状況
    INSERT_USER_ID,									--登録者ID
    INSERT_DATE_TIME,								--登録日時
    UPDATE_USER_ID,									--最終更新者ID
    UPDATE_DATE_TIME,								--最終更新日時
    DELETED_FLG,									--削除フラグ
    DELETED_DATE									--論理削除日
    )
    VALUES
    (
    :mailPackDeliverId,								--メール一括配信ID
    :opalProcessId,									--処理ID
    :deliverType,									--配信タイプ
    :templateId,									--テンプレートID
    :deliverDate,									--配信日時
    :deliverFileName,								--配信ファイル名称
    :mailDeliverStatus,								--メール配信状況
    :insertUserId,									--登録者ID
    :insertDateTime,								--登録日時
    :updateUserId,									--最終更新者ID
    :updateDateTime,								--最終更新日時
    :deletedFlg,									--削除フラグ
    :deletedDate									--論理削除日
    )

