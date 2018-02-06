------------------------------------------------------------------------------------------------------
-- PiTaPa利用実績情報取込バッチ(B138A011Action)
------------------------------------------------------------------------------------------------------
-- PiTaPa利用実績一時情報登録
INSERT_PITAPA_USE_RES_TEMP_INFO =
INSERT INTO
    PITAPA_USE_RES_TEMP_INFO            -- PiTaPa利用実績一時情報
    (
    MEMBER_CONTROL_NUMBER,              -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                -- 会員管理番号枝番
    PITAPA_USE_YEAR_MONTH,              -- PiTaPaご利用年月
    PLAN_CODE,                          -- プランコード
    MEMBER_UNIT_PAY_TOTAL,              -- 会員単位支払合計
    MEMBER_UNIT_PAY_TOTAL_TOTAL,        -- 会員単位支払合計の合計
    DETAIL_BOOK_POST_CHARGE,            -- 明細書発送手数料
    SHOP_DE_POINT_DISCOUNT,             -- ショップdeポイント割引
    ACCOUNT_UNIT_PAY_TOTAL,             -- 口座単位支払合計
    REGIST_STA_USE_APPLY_MONEY,         -- 登録駅ご利用・適用金額
    REGIST_STA_USE_DIS_MONEY,           -- 登録駅ご利用・割引後金額
    NOT_REGIST_STA_USE_APPLY_MONEY,     -- 登録駅外ご利用・適用金額
    NOT_REGIST_STA_USE_DIS_MONEY,       -- 登録駅外ご利用・割引後金額
    NOT_REGIST_USE_APPLY_MONEY,         -- 非登録型ご利用・適用金額
    NOT_REGIST_USE_DIS_MONEY,           -- 非登録型ご利用・割引後金額
    OTHER_RAILWAY_BUS_USE,              -- その他鉄道バスご利用
    PITAPA_SHOPPING,                    -- PiTaPaショッピング
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
    :memControlNum,                     -- 会員管理番号
    :memControlBrNum,                   -- 会員管理番号枝番
    :pitapaUseYearMonth,                -- PiTaPaご利用年月
    :planCode,                          -- プランコード
    :memberUnitPayTotal,                -- 会員単位支払合計
    :memberUnitPayTotalTotal,           -- 会員単位支払合計の合計
    :detailBookPostCharge,              -- 明細書発送手数料
    :shopDePointDiscount,               -- ショップdeポイント割引
    :accountUnitPayTotal,               -- 口座単位支払合計
    :registStaUseApplyMoney,            -- 登録駅ご利用・適用金額
    :registStaUseDisMoney,              -- 登録駅ご利用・割引後金額
    :notRegistStaUseApplyMoney,         -- 登録駅外ご利用・適用金額
    :notRegistStaUseDisMoney,           -- 登録駅外ご利用・割引後金額
    :notRegistUseApplyMoney,            -- 非登録型ご利用・適用金額
    :notRegistUseDisMoney,              -- 非登録型ご利用・割引後金額
    :otherRailwayBusUse,                -- その他鉄道バスご利用
    :pitapaShopping,                    -- PiTaPaショッピング
    :processedFlag,                     -- 処理済フラグ
    :insertUserId,                      -- 登録者ID
    :insertDateTime,                    -- 登録日時
    :updateUserId,                      -- 最終更新者ID
    :updateDateTime,                    -- 最終更新日時
    :deletedFlg,                        -- 削除フラグ
    :deletedDate                        -- 論理削除日
    )