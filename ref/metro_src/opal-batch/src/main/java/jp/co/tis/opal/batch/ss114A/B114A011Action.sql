------------------------------------------------------------------------------------------------------
-- OP会員情報取込バッチ(B114A011Action)
------------------------------------------------------------------------------------------------------

-- OP会員一時情報登録SQL
INSERT_OP_MEM_TEMP_INFO =
INSERT INTO
    OP_MEM_TEMP_INFO                     -- OP会員一時情報
    (
    MEMBER_CONTROL_NUMBER,               -- 会員管理番号
    MEM_CTRL_NUM_BR_NUM,                 -- 会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                 -- OP番号
    DATA_RELATE_DIVISION,                -- データ連携区分
    PITAPA_EXPIRATION_DATE,              -- PiTaPa有効期限
    CARD_TYPE,                           -- カード種類
    BIRTHDATE,                           -- 生年月日
    SEX_CODE,                            -- 性別コード
    TELEPHONE_NUMBER,                    -- 自宅電話番号
    CELLPHONE_NUMBER,                    -- 携帯電話番号
    POSTCODE,                            -- 郵便番号
    SERVICE_CATEGORY,                    -- サービス種別
    REGIST_STATION_1,                    -- 登録駅1
    REGIST_STATION_2,                    -- 登録駅2
    RELATIONSHIP_CODE,                   -- 続柄コード
    PROCESSED_FLAG,                      -- 処理済フラグ
    INSERT_USER_ID,                      -- 登録者ID
    INSERT_DATE_TIME,                    -- 登録日時
    UPDATE_USER_ID,                      -- 最終更新者ID
    UPDATE_DATE_TIME,                    -- 最終更新日時
    DELETED_FLG,                         -- 削除フラグ
    DELETED_DATE                         -- 論理削除日
    )
    VALUES
    (
    :memberControlNumber,                -- 会員管理番号
    :memrCtrlBrNum,                      -- 会員管理番号枝番
    :osakaPitapaNumber,                  -- OP番号
    :dataRelateDivision,                 -- データ連携区分
    :pitapaExpirationDate,               -- PiTaPa有効期限
    :cardType,                           -- カード種類
    :birthdate,                          -- 生年月日
    :sexCode,                            -- 性別コード
    :telephoneNumber,                    -- 自宅電話番号
    :cellphoneNumber,                    -- 携帯電話番号
    :postcode,                           -- 郵便番号
    :serviceCategory,                    -- サービス種別
    :registStation1,                     -- 登録駅1
    :registStation2,                     -- 登録駅2
    :relationshipCode,                   -- 続柄コード
    :processedFlag,                      -- 処理済フラグ(0:未処理)
    :insertUserId,                       -- 登録者ID
    :insertDateTime,                     -- 登録日時
    :updateUserId,                       -- 最終更新者ID
    :updateDateTime,                     -- 最終更新日時
    :deletedFlg,                         -- 削除フラグ(0:未削除)
    :deletedDate                         -- 論理削除日
    )
