------------------------------------------------------------------------------------------------------
-- エラーメールアドレス情報更新バッチ(B153A012Action)
------------------------------------------------------------------------------------------------------
-- アプリ会員情報更新
UPDATE_APL_MEM_INFO =
UPDATE
    APL_MEM_INFO                                                 -- アプリ会員情報
SET
    MAIL_DELIVER_STATUS_DIVISION = :mailDeliverStatusDivision,   -- メール配信状態区分
    UPDATE_USER_ID = :updateUserId,                              -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,                          -- 最終更新日時
    VERSION = VERSION + 1                                        --バージョン番号
WHERE
    MAIL_ADDRESS = :mailAddress                                  -- メールアドレス = 3.6.1で取得したメールアドレス
    AND DELETED_FLG = '0'                                        -- アプリ会員情報TBL.削除フラグ = "0"(未削除)