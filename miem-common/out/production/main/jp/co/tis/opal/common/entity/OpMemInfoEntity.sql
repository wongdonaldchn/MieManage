------------------------------------------------------------------------------------------------------
--OP会員情報排他制御用SQL
------------------------------------------------------------------------------------------------------
SELECT_OP_MEM_INFO =
SELECT
    MEMBER_CONTROL_NUMBER,                          -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                            -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                            -- OP番号
    PITAPA_EXPIRATION_DATE,                         -- PiTaPa有効期限
    OLD_PITAPA_EXPIRATION_DATE,                     -- 更新前PiTaPa有効期限
    CARD_TYPE,                                      -- カード種類
    BIRTHDATE,                                      -- 生年月日
    SEX_CODE,                                       -- 性別コード
    TELEPHONE_NUMBER,                               -- 自宅電話番号
    CELLPHONE_NUMBER,                               -- 携帯電話番号
    POSTCODE,                                       -- 郵便番号
    SERVICE_CATEGORY,                               -- サービス種別
    REGIST_STATION_1,                               -- 登録駅1
    REGIST_STATION_2,                               -- 登録駅2
    RELATIONSHIP_CODE,                              -- 続柄コード
    OSAKA_PITAPA_WITHDRAW_FLAG,                     -- OP退会フラグ
    INSERT_USER_ID,                                 -- 登録者ID
    INSERT_DATE_TIME,                               -- 登録日時
    UPDATE_USER_ID,                                 -- 最終更新者ID
    UPDATE_DATE_TIME,                               -- 最終更新日時
    DELETED_FLG,                                    -- 削除フラグ
    DELETED_DATE,                                   -- 論理削除日
    VERSION                                         -- バージョン番号
FROM
    OP_MEM_INFO                                     -- OP会員情報
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber    -- 会員管理番号
    AND MEM_CTRL_NUM_BR_NUM = :memCtrlNumBrNum      -- 会員管理番号枝番
    AND OSAKA_PITAPA_WITHDRAW_FLAG = '0'            -- OP退会フラグ(0:OP未退会)
    AND DELETED_FLG = '0'                           -- 削除フラグ(0:未削除)
FOR UPDATE
