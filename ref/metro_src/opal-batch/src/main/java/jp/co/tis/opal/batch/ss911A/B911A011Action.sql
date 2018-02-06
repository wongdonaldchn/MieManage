----------------------------------------------
-- 開局フラグ（サービス提供可否状態）取得
----------------------------------------------
GET_SERVICE_AVAILABLE =
SELECT
    SERVICE_AVAILABLE                    -- サービス提供可否状態
FROM
    BATCH_REQUEST                        -- バッチリクエスト
WHERE
    REQUEST_ID = :targetRequestId        -- リクエストID

----------------------------------------------
-- プロセス停止フラグ更新
----------------------------------------------
UPDATE_HALT_FLG =
UPDATE
    BATCH_REQUEST                        -- バッチリクエスト
SET
    PROCESS_HALT_FLG = :status,          -- プロセス停止フラグ
    UPDATE_USER_ID = :updateUserId,      -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime   -- 最終更新日時
WHERE
    REQUEST_ID = :targetRequestId        -- リクエストID
