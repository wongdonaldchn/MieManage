----------------------------------------------
-- 開局フラグ（サービス提供可否状態）更新
----------------------------------------------
UPDATE_SERVICE_AVAILABLE =
UPDATE
    BATCH_REQUEST                        -- バッチリクエスト
SET
    SERVICE_AVAILABLE = :status,         -- サービス提供可否状態
    UPDATE_USER_ID = :updateUserId,      -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime   -- 最終更新日時
WHERE
    REQUEST_ID = :targetRequestId        -- リクエストID
