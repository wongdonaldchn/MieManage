------------------------------------------------------------------------------------------------------
-- PiTaPa利用実績情報登録バッチ(B138A012Action)
------------------------------------------------------------------------------------------------------
-- PiTaPa利用実績一時情報取得
SELECT_PITAPA_USE_RES_TEMP_INFO =
SELECT
    MEMBER_CONTROL_NUMBER,                             -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                               -- 会員管理番号枝番
    PITAPA_USE_YEAR_MONTH,                             -- PiTaPaご利用年月
    PLAN_CODE,                                         -- プランコード
    MEMBER_UNIT_PAY_TOTAL,                             -- 会員単位支払合計
    MEMBER_UNIT_PAY_TOTAL_TOTAL,                       -- 会員単位支払合計の合計
    DETAIL_BOOK_POST_CHARGE,                           -- 明細書発送手数料
    SHOP_DE_POINT_DISCOUNT,                            -- ショップdeポイント割引
    ACCOUNT_UNIT_PAY_TOTAL,                            -- 口座単位支払合計
    REGIST_STA_USE_APPLY_MONEY,                        -- 登録駅ご利用　適用金額
    REGIST_STA_USE_DIS_MONEY,                          -- 登録駅ご利用　割引後金額
    NOT_REGIST_STA_USE_APPLY_MONEY,                    -- 登録駅外ご利用　適用金額
    NOT_REGIST_STA_USE_DIS_MONEY,                      -- 登録駅外ご利用　割引後金額
    NOT_REGIST_USE_APPLY_MONEY,                        -- 非登録型ご利用　適用金額
    NOT_REGIST_USE_DIS_MONEY,                          -- 非登録型ご利用　割引後金額
    OTHER_RAILWAY_BUS_USE,                             -- その他鉄道バスご利用
    PITAPA_SHOPPING                                    -- PiTaPaショッピング
FROM
    PITAPA_USE_RES_TEMP_INFO                           -- PiTaPa利用実績一時情報
WHERE
    PROCESSED_FLAG = '0'                               -- PiTaPa利用実績一時情報TBL.処理済フラグ = "0"(未処理)
    AND DELETED_FLG = '0'                              -- PiTaPa利用実績一時情報TBL.削除フラグ = "0"(未削除)
ORDER BY
    MEMBER_CONTROL_NUMBER ASC,                         -- PiTaPa利用実績一時情報TBL.会員管理番号(昇順)
    MEM_CTRL_NUM_BR_NUM ASC,                           -- PiTaPa利用実績一時情報TBL.会員管理番号枝番(昇順)
    PITAPA_USE_YEAR_MONTH ASC                          -- PiTaPa利用実績一時情報TBL.PiTaPaご利用年月(昇順)

-- PiTaPa利用実績情報登録
INSERT_PITAPA_USE_RES_INFO =
INSERT INTO
    PITAPA_USE_RES_INFO                                -- PiTaPa利用実績情報
    (
    MEMBER_CONTROL_NUMBER,                             -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                               -- 会員管理番号枝番
    PITAPA_USE_YEAR_MONTH,                             -- PiTaPaご利用年月
    PLAN_CODE,                                         -- プランコード
    MEMBER_UNIT_PAY_TOTAL,                             -- 会員単位支払合計
    MEMBER_UNIT_PAY_TOTAL_TOTAL,                       -- 会員単位支払合計の合計
    DETAIL_BOOK_POST_CHARGE,                           -- 明細書発送手数料
    SHOP_DE_POINT_DISCOUNT,                            -- ショップdeポイント割引
    ACCOUNT_UNIT_PAY_TOTAL,                            -- 口座単位支払合計
    REGIST_STA_USE_APPLY_MONEY,                        -- 登録駅ご利用　適用金額
    REGIST_STA_USE_DIS_MONEY,                          -- 登録駅ご利用　割引後金額
    NOT_REGIST_STA_USE_APPLY_MONEY,                    -- 登録駅外ご利用　適用金額
    NOT_REGIST_STA_USE_DIS_MONEY,                      -- 登録駅外ご利用　割引後金額
    NOT_REGIST_USE_APPLY_MONEY,                        -- 非登録型ご利用　適用金額
    NOT_REGIST_USE_DIS_MONEY,                          -- 非登録型ご利用　割引後金額
    OTHER_RAILWAY_BUS_USE,                             -- その他鉄道バスご利用
    PITAPA_SHOPPING,                                   -- PiTaPaショッピング
    INSERT_USER_ID,                                    -- 登録者ID
    INSERT_DATE_TIME,                                  -- 登録日時
    UPDATE_USER_ID,                                    -- 最終更新者ID
    UPDATE_DATE_TIME,                                  -- 最終更新日時
    DELETED_FLG,                                       -- 削除フラグ
    DELETED_DATE                                       -- 論理削除日
    )
VALUES
    (
    :memControlNum,                                    -- 会員管理番号
    :memControlBrNum,                                  -- 会員管理番号枝番
    :pitapaUseYearMonth,                               -- PiTaPaご利用年月
    :planCode,                                         -- プランコード
    :memberUnitPayTotal,                               -- 会員単位支払合計
    :memberUnitPayTotalTotal,                          -- 会員単位支払合計の合計
    :detailBookPostCharge,                             -- 明細書発送手数料
    :shopDePointDiscount,                              -- ショップdeポイント割引
    :accountUnitPayTotal,                              -- 口座単位支払合計
    :registStaUseApplyMoney,                           -- 登録駅ご利用　適用金額
    :registStaUseDisMoney,                             -- 登録駅ご利用　割引後金額
    :notRegistStaUseApplyMoney,                        -- 登録駅外ご利用　適用金額
    :notRegistStaUseDisMoney,                          -- 登録駅外ご利用　割引後金額
    :notRegistUseApplyMoney,                           -- 非登録型ご利用　適用金額
    :notRegistUseDisMoney,                             -- 非登録型ご利用　割引後金額
    :otherRailwayBusUse,                               -- その他鉄道バスご利用
    :pitapaShopping,                                   -- PiTaPaショッピング
    :insertUserId,                                     -- 登録者ID
    :insertDateTime,                                   -- 登録日時
    :updateUserId,                                     -- 最終更新者ID
    :updateDateTime,                                   -- 最終更新日時
    :deletedFlg,                                       -- 削除フラグ
    :deletedDate                                       -- 論理削除日
    )

-- PiTaPa利用実績一時情報更新
UPDATE_PITAPA_USE_RES_TEMP_INFO =
UPDATE
    PITAPA_USE_RES_TEMP_INFO                           -- PiTaPa利用実績一時情報
SET
    PROCESSED_FLAG = :processedFlag,                   -- 処理済フラグ
    UPDATE_USER_ID = :updateUserId,                    -- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime                 -- 最終更新日時
WHERE
    MEMBER_CONTROL_NUMBER = :memControlNum             -- PiTaPa利用実績一時情報TBL.会員管理番号 = 3.6のNo1で取得した会員管理番号
    AND MEM_CTRL_NUM_BR_NUM = :memControlBrNum         -- PiTaPa利用実績一時情報TBL.会員管理番号枝番 = 3.6のNo1で取得した会員管理番号枝番
    AND PITAPA_USE_YEAR_MONTH = :pitapaUseYearMonth    -- PiTaPa利用実績一時情報TBL.PiTaPaご利用年月 = 3.6のNo1で取得したPiTaPaご利用年月