------------------------------------------------
-- アプリ会員本人確認API
------------------------------------------------
-- アプリ会員情報取得
SELECT_APL_MEM_INFO=
SELECT
    APPLICATION_MEMBER_ID,                                                  -- アプリ会員ID
    OSAKA_PITAPA_NUMBER                                                     -- OP番号
FROM
    APL_MEM_INFO                                                            -- アプリ会員情報TBL
WHERE
    BIRTHDATE = :birthdate                                                  -- アプリ会員情報TBL.生年月日 = アプリ会員本人確認要求電文.生年月日
    AND SEX_CODE = :sexCode                                                 -- アプリ会員情報TBL.性別コード = アプリ会員本人確認要求電文.性別コード
    AND MAIL_ADDRESS = :mailAddress                                         -- アプリ会員情報TBL.メールアドレス = アプリ会員本人確認要求電文.メールアドレス
    AND APPLICATION_MEMBER_STATUS_CODE IN(:opAuthAplMem,:notOpMem)          -- アプリ会員情報TBL.アプリ会員状態コード IN ( "A", "D")
    AND DELETED_FLG = '0'                                                   -- アプリ会員情報TBL.削除フラグ = "0"(未削除)