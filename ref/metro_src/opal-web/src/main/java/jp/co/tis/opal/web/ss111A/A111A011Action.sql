------------------------------------------------
-- OP会員本人確認API
------------------------------------------------
SELECT_RELATIONSHIP_CODE=
SELECT
    RELATIONSHIP_CODE                                                  -- 続柄コード
FROM
    OP_MEM_INFO                                                        -- OP会員情報TBL
WHERE
    OSAKA_PITAPA_NUMBER = :osakaPitapaNumber                           -- OP会員情報TBL.OP番号 = OP会員本人確認要求電文.OP番号
    AND BIRTHDATE = :birthdate                                         -- OP会員情報TBL.生年月日 = OP会員本人確認要求電文.生年月日
    AND (TELEPHONE_NUMBER = :telephoneNumber                           -- OP会員情報TBL.自宅電話番号 = OP会員本人確認要求電文.電話番号
             OR CELLPHONE_NUMBER = :telephoneNumber)                   -- OP会員情報TBL.携帯電話番号 = OP会員本人確認要求電文.電話番号
    AND (PITAPA_EXPIRATION_DATE = :pitapaExpirationDate                -- OP会員情報TBL.PiTaPa有効期限 = OP会員本人確認要求電文.PiTaPa有効期限
            OR OLD_PITAPA_EXPIRATION_DATE = :pitapaExpirationDate)     -- OP会員情報TBL.更新前PiTaPa有効期限 = OP会員本人確認要求電文.PiTaPa有効期限
    AND DELETED_FLG = '0'                                              -- OP会員情報TBL.削除フラグ = "0"(未削除)
    AND OSAKA_PITAPA_WITHDRAW_FLAG = '0'                               -- OP会員情報TBL.OP退会フラグ = "0"(OP未退会)