------------------------------------------------------------------------------------------------------
-- 乗車マイル情報取込バッチ(B135A011Action)
------------------------------------------------------------------------------------------------------

-- 乗車マイル取込一時情報登録
INSERT_RIDE_MILE_RIN_TEMP_INFO =
INSERT INTO
    RIDE_MILE_RIN_TEMP_INFO             -- 乗車マイル取込一時情報
    (
    MEMBER_CONTROL_NUMBER,              -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                -- 会員管理番号枝番
    OBJECT_YEAR_MONTH,                  -- 対象年月
    RIDE_MILE_AMOUNT,                   -- 乗車マイル数
    MILE_CATEGORY_CODE,                 -- マイル種別コード
    PROCESSED_FLAG,                     -- 処理済フラグ
    INSERT_USER_ID,                     -- 登録者ID
    INSERT_DATE_TIME,                   -- 登録日時
    UPDATE_USER_ID,                     -- 最終更新者ID
    UPDATE_DATE_TIME,                   -- 最終更新日時
    DELETED_FLG,                        -- 削除フラグ
    DELETED_DATE                        -- 論理削除日
    )
VALUES
    (
    :memCtrlNum,                        -- 会員管理番号
    :memCtrlNumBrNum,                   -- 会員管理番号枝番
    :objectYearMonth,                   -- 対象年月
    :rideMileAmount,                    -- 乗車マイル数
    :mileCategoryCode,                  -- マイル種別コード
    :processedFlag,                     -- 処理済フラグ
    :insertUserId,                      -- 登録者ID
    :insertDateTime,                    -- 登録日時
    :updateUserId,                      -- 最終更新者ID
    :updateDateTime,                    -- 最終更新日時
    :deletedFlg,                        -- 削除フラグ
    :deletedDate                        -- 論理削除日
    )