------------------------------------------------
-- PiTaPa利用実績取得API
------------------------------------------------
-- アプリ会員情報取得
SELECT_APL_MEM_INFO=
SELECT
    APPLICATION_MEMBER_ID                                           -- アプリ会員ID
FROM
    APL_MEM_INFO                                                    -- アプリ会員ID情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId                    -- アプリ会員情報TBL.アプリ会員ID = PiTaPa利用実績取得要求電文.アプリ会員ID
    AND DELETED_FLG = '0'                                           -- アプリ会員情報TBL.削除フラグ = "0"(未削除)
    AND APPLICATION_MEMBER_STATUS_CODE IN (:statusA,:statusD)       -- アプリ会員情報TBL.アプリ会員状態コード IN ( "A", "D")

-- アプリ会員情報・PiTaPa利用実績情報取得
SELECT_PITAPA_USE_RES_INFO=
SELECT
    PITAPA.PLAN_CODE,                                               -- プランコード
    PITAPA.MEMBER_UNIT_PAY_TOTAL,                                   -- 会員単位支払合計
    PITAPA.MEMBER_UNIT_PAY_TOTAL_TOTAL,                             -- 会員単位支払合計の合計
    PITAPA.DETAIL_BOOK_POST_CHARGE,                                 -- 明細書発送手数料
    PITAPA.SHOP_DE_POINT_DISCOUNT,                                  -- ショップdeポイント割引
    PITAPA.ACCOUNT_UNIT_PAY_TOTAL,                                  -- 口座単位支払合計
    PITAPA.REGIST_STA_USE_APPLY_MONEY,                              -- 登録駅ご利用　適用金額
    PITAPA.REGIST_STA_USE_DIS_MONEY,                                -- 登録駅ご利用　割引後金額
    PITAPA.NOT_REGIST_STA_USE_APPLY_MONEY,                          -- 登録駅外ご利用　適用金額
    PITAPA.NOT_REGIST_STA_USE_DIS_MONEY,                            -- 登録駅外ご利用　割引後金額
    PITAPA.NOT_REGIST_USE_APPLY_MONEY,                              -- 非登録型ご利用　適用金額
    PITAPA.NOT_REGIST_USE_DIS_MONEY,                                -- 非登録型ご利用　割引後金額
    PITAPA.OTHER_RAILWAY_BUS_USE,                                   -- その他鉄道バスご利用
    PITAPA.PITAPA_SHOPPING,                                         -- PiTaPaショッピング
    APL.MEM_CTRL_NUM_BR_NUM                                         -- 会員管理番号枝番
FROM
    APL_MEM_INFO APL                                                -- アプリ会員情報
INNER JOIN
    PITAPA_USE_RES_INFO PITAPA                                      -- PiTaPa利用実績情報
ON
    APL.MEMBER_CONTROL_NUMBER = PITAPA.MEMBER_CONTROL_NUMBER        -- アプリ会員情報TBL.会員管理番号 = PiTaPa利用実績情報TBL.会員管理番号
    AND APL.MEM_CTRL_NUM_BR_NUM = PITAPA.MEM_CTRL_NUM_BR_NUM        -- アプリ会員情報TBL.会員管理番号枝番 = PiTaPa利用実績情報TBL.会員管理番号枝番
WHERE
    APL.APPLICATION_MEMBER_ID = :applicationMemberId                -- アプリ会員情報TBL.アプリ会員ID = PiTaPa利用実績取得要求電文.アプリ会員ID
    AND APL.APPLICATION_MEMBER_STATUS_CODE = :statusA               -- アプリ会員情報TBL.アプリ会員状態コード = "A"(OP認証済みのアプリ会員)
    AND APL.DELETED_FLG = '0'                                       -- アプリ会員情報TBL.削除フラグ = "0"(未削除)
    AND PITAPA.PITAPA_USE_YEAR_MONTH = :pitapaUseYearMonth          -- PiTaPa利用実績情報TBL.PiTaPaご利用年月 = PiTaPa利用実績取得要求電文.PiTaPaご利用年月
    AND PITAPA.DELETED_FLG = '0'                                    -- PiTaPa利用実績情報TBL.削除フラグ = "0"(未削除)
