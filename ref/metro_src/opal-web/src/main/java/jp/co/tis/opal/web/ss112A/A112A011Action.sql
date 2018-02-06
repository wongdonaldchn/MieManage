------------------------------------------------
-- アプリ会員ログインAPI
------------------------------------------------
INSERT_LOGIN_HISTORY_INFO =
INSERT INTO
    LOGIN_HISTORY_INFORMATION            -- ログイン履歴情報
    (
    LOGIN_HISTORY_ID,                    -- ログイン履歴ID
    APPLICATION_MEMBER_ID,               -- アプリ会員ID
    LOGIN_ID,                            -- ログインID
    LOGIN_DATE_TIME,                     -- ログイン日時
    APPLICATION_ID,                      -- アプリID
    DEVICE_ID,                           -- デバイスID
    INSERT_USER_ID,                      -- 登録者ID
    INSERT_DATE_TIME,                    -- 登録日時
    UPDATE_USER_ID,                      -- 最終更新者ID
    UPDATE_DATE_TIME,                    -- 最終更新日時
    DELETED_FLG,                         -- 削除フラグ
    DELETED_DATE                         -- 論理削除日
    )
VALUES
    (
    :loginHistoryId,                     -- ログイン履歴ID
    :applicationMemberId,                -- アプリ会員ID
    :loginId,                            -- ログインID
    :loginDateTime,                      -- ログイン日時
    :applicationId,                      -- アプリID
    :deviceId,                           -- デバイスID
    :insertUserId,                       -- 登録者ID
    :insertDateTime,                     -- 登録日時
    :updateUserId,                       -- 最終更新者ID
    :updateDateTime,                     -- 最終更新日時
    :deletedFlg,                         -- 削除フラグ(0:未削除)
    :deletedDate                         -- 論理削除日
    )
