------------------------------------------------
-- OP会員情報取得API
------------------------------------------------
SELECT_OP_MEM_INFO=
SELECT
    OMI.MEMBER_CONTROL_NUMBER,               -- 会員管理番号
    OMI.MEM_CTRL_NUM_BR_NUM,                 -- 会員管理番号枝番
    OMI.OSAKA_PITAPA_NUMBER,                 -- OP番号
    OMI.PITAPA_EXPIRATION_DATE,              -- PiTaPa有効期限
    OMI.CARD_TYPE,                           -- カード種類
    OMI.BIRTHDATE,                           -- 生年月日
    OMI.SEX_CODE,                            -- 性別コード
    OMI.TELEPHONE_NUMBER,                    -- 自宅電話番号
    OMI.CELLPHONE_NUMBER,                    -- 携帯電話番号
    OMI.POSTCODE,                            -- 郵便番号
    OMI.SERVICE_CATEGORY,                    -- サービス種別
    OMI.REGIST_STATION_1,                    -- 登録駅1
    OMI.REGIST_STATION_2,                    -- 登録駅2
    OMI.RELATIONSHIP_CODE,                   -- 続柄コード
    MUSI.THIS_TIME_REGIST_STATION_1,         -- 今回登録駅1
    MUSI.THIS_TIME_REGIST_STATION_2,         -- 今回登録駅2
    MUSI.THIS_TIME_REGIST_STATION_3,         -- 今回登録駅3
    MUSI.THIS_TIME_REGIST_STATION_4,         -- 今回登録駅4
    MUSI.THIS_TIME_REGIST_STATION_5          -- 今回登録駅5
FROM
    OP_MEM_INFO OMI                          -- OP会員情報TBL
LEFT JOIN
    MAIN_USE_STA_INFO MUSI                   -- 主なご利用駅情報TBL
ON
    OMI.MEMBER_CONTROL_NUMBER = MUSI.MEMBER_CONTROL_NUMBER   -- OP会員情報TBL.会員管理番号 = 主なご利用駅情報TBL.会員管理番号
    AND OMI.MEM_CTRL_NUM_BR_NUM = MUSI.MEM_CTRL_NUM_BR_NUM   -- OP会員情報TBL.会員管理番号枝番 = 主なご利用駅情報TBL.会員管理番号枝番
WHERE
    OMI.OSAKA_PITAPA_NUMBER = :osakaPitapaNumber             -- OP会員情報TBL.OP番号 = OP会員情報取得要求電文.OP番号
    AND OMI.DELETED_FLG = '0'                                -- OP会員情報TBL.削除フラグ = "0"(未削除)
    AND OMI.OSAKA_PITAPA_WITHDRAW_FLAG = '0'                 -- OP会員情報TBL.OP退会フラグ = "0"(OP未退会)