------------------------------------------------------------------------------------------------------
-- プッシュ通知情報登録共通コンポーネント
------------------------------------------------------------------------------------------------------

--プッシュ通知情報を登録するSQL
INSERT_PUSH_NOTICE_INFORMATION =
INSERT INTO
    PUSH_NOTICE_INFORMATION                        --プッシュ通知情報
    (
    PUSH_NOTICE_ID,                                --プッシュ通知ID
    OPAL_PROCESS_ID,                               --処理ID
    DELIVER_DIVISION,                              --送信区分
    DELIVER_TYPE,                                  --送信タイプ
    TEMPLATE_ID,                                   --テンプレートID
    DELIVER_DATE_TIME,                             --送信日時
    PUSH_NOTICE_DISTINGUISH_ID,                    --プッシュ通知識別ID
    PROCESSED_FLAG,                                --処理済フラグ
    INSERT_USER_ID,                                --登録者ID
    INSERT_DATE_TIME,                              --登録日時
    UPDATE_USER_ID,                                --最終更新者ID
    UPDATE_DATE_TIME,                              --最終更新日時
    DELETED_FLG,                                   --削除フラグ
    DELETED_DATE                                   --論理削除日
    )
    VALUES
    (
    :pushNoticeId,                                 --プッシュ通知ID
    :opalProcessId,                                --処理ID
    :diliverDivision,                              --送信区分
    :diliverType,                                  --送信タイプ
    :templateId,                                   --テンプレートID
    :diliverDateTime,                              --送信日時
    :pushNoticeDistinguishId,                      --プッシュ通知識別ID
    :processedFlag,                                --処理済フラグ
    :insertUserId,                                 --登録者ID
    :insertDateTime,                               --登録日時
    :updateUserId,                                 --最終更新者ID
    :updateDateTime,                               --最終更新日時
    :deletedFlg,                                   --削除フラグ
    :deletedDate                                   --論理削除日
    )

--プッシュ通知送信先情報を登録するSQL
INSERT_PUSH_NOTICE_DEST_INFO =
INSERT INTO
    PUSH_NOTICE_DEST_INFO                          --プッシュ通知送信先情報
    (
    PUSH_NOTICE_ID,                                --プッシュ通知ID
    SEQUENCE_MEMBER,                               --連番
    APPLICATION_MEMBER_ID,                         --アプリ会員ID
    APPLICATION_ID,                                --アプリID
    DEVICE_ID,                                     --デバイスID
    INSERT_USER_ID,                                --登録者ID
    INSERT_DATE_TIME,                              --登録日時
    UPDATE_USER_ID,                                --最終更新者ID
    UPDATE_DATE_TIME,                              --最終更新日時
    DELETED_FLG,                                   --削除フラグ
    DELETED_DATE                                   --論理削除日
    )
    VALUES
    (
    :pushNoticeId,                                 --プッシュ通知ID
    :sequenceMember,                               --連番
    :applicationMemberId,                          --アプリ会員ID
    :applicationId,                                --アプリID
    :deviceId,                                     --デバイスID
    :insertUserId,                                 --登録者ID
    :insertDateTime,                               --登録日時
    :updateUserId,                                 --最終更新者ID
    :updateDateTime,                               --最終更新日時
    :deletedFlg,                                   --削除フラグ
    :deletedDate                                   --論理削除日
    )
