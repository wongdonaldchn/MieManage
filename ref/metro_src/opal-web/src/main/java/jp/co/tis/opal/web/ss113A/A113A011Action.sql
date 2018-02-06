------------------------------------------------
-- アプリ会員情報取得API(A113A011Action)
------------------------------------------------

--アプリ会員情報・OP会員情報取得用SQL
SELECT_APL_MEM_AND_OP_MEM_INFO =
SELECT
    AMI.MEMBER_CONTROL_NUMBER,                                -- 会員管理番号
    AMI.MEM_CTRL_NUM_BR_NUM,                                  -- 会員管理番号枝番
    AMI.APPLICATION_ID,                                       -- アプリID
    AMI.DEVICE_ID,                                            -- デバイスID
    AMI.LOGIN_ID,                                             -- ログインID
    AMI.BIRTHDATE BIRTHDATE_APL,                              -- 生年月日
    AMI.SEX_CODE SEXCODE_APL,                                 -- 性別コード
    AMI.MAIL_ADDRESS,                                         -- メールアドレス
    AMI.RECOMMEND_USE_ACCEPT_FLAG,                            -- レコメンド利用承諾可否フラグ
    AMI.ENQUETE_1,                                            -- アンケート1
    AMI.ENQUETE_2,                                            -- アンケート2
    AMI.ENQUETE_3,                                            -- アンケート3
    AMI.ENQUETE_4,                                            -- アンケート4
    AMI.ENQUETE_5,                                            -- アンケート5
    AMI.ENQUETE_6,                                            -- アンケート6
    AMI.ENQUETE_7,                                            -- アンケート7
    AMI.ENQUETE_8,                                            -- アンケート8
    AMI.ENQUETE_9,                                            -- アンケート9
    AMI.ENQUETE_10,                                           -- アンケート10
    AMI.MAIN_USE_STATION_1,                                   -- 主なご利用駅1
    AMI.MAIN_USE_STATION_2,                                   -- 主なご利用駅2
    AMI.MAIN_USE_STATION_3,                                   -- 主なご利用駅3
    AMI.MAIN_USE_STATION_4,                                   -- 主なご利用駅4
    AMI.MAIN_USE_STATION_5,                                   -- 主なご利用駅5
    AMI.DAY_OFF_1, 				        	         	   	  -- 休日1
    AMI.DAY_OFF_2, 				        	         		  -- 休日2
    AMI.APPLICATION_MEMBER_STATUS_CODE,                       -- アプリ会員状態コード
    OMI.OSAKA_PITAPA_NUMBER,                                  --OP番号
    OMI.CARD_TYPE,                                            --カード種類
    OMI.BIRTHDATE BIRTHDATE_OP,                               --生年月日
    OMI.SEX_CODE SEXCODE_OP,                                  --性別コード
    OMI.SERVICE_CATEGORY,                                     --サービス種別
    OMI.REGIST_STATION_1,                                     --登録駅1
    OMI.REGIST_STATION_2,                                     --登録駅2
    MUSI.THIS_TIME_REGIST_STATION_1,                          --今回登録駅1
    MUSI.THIS_TIME_REGIST_STATION_2,                          --今回登録駅2
    MUSI.THIS_TIME_REGIST_STATION_3,                          --今回登録駅3
    MUSI.THIS_TIME_REGIST_STATION_4,                          --今回登録駅4
    MUSI.THIS_TIME_REGIST_STATION_5                           --今回登録駅5
FROM
    APL_MEM_INFO AMI                                          --アプリ会員情報
LEFT JOIN
    OP_MEM_INFO OMI                                           --OP会員情報
ON
    AMI.MEMBER_CONTROL_NUMBER = OMI.MEMBER_CONTROL_NUMBER     --会員管理番号
    AND AMI.MEM_CTRL_NUM_BR_NUM = OMI.MEM_CTRL_NUM_BR_NUM     --会員管理番号枝番
    AND OMI.OSAKA_PITAPA_WITHDRAW_FLAG = '0'                  --OP退会フラグ(0:未退会)
    AND OMI.DELETED_FLG = '0'                                 --削除フラグ(0:未削除)
LEFT JOIN
    MAIN_USE_STA_INFO MUSI                                    --主なご利用駅情報
ON
    OMI.MEMBER_CONTROL_NUMBER = MUSI.MEMBER_CONTROL_NUMBER    --会員管理番号
    AND OMI.MEM_CTRL_NUM_BR_NUM = MUSI.MEM_CTRL_NUM_BR_NUM    --会員管理番号枝番
    AND MUSI.DELETED_FLG = '0'                                --削除フラグ(0:未削除)
WHERE
    AMI.APPLICATION_MEMBER_ID = :applicationMemberId           --アプリ会員ID
    AND AMI.APPLICATION_MEMBER_STATUS_CODE IN (:statusA,:statusD)  --アプリ会員状態コード
    AND AMI.DELETED_FLG = '0'                                 --削除フラグ(0:未削除)

--家族会員サービス登録有無チェック用SQL
SELECT_FAMILY_MEM_INFO_COUNT =
SELECT
    COUNT(1) AS FAMILY_MEM_SERVICE_CNT                --家族会員サービスレコード数
FROM
    FAMILY_MEM_SERVICE_INFO                           --家族会員サービス情報
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber      --会員管理番号
    AND REGIST_STATUS_DIVISION = :registStatusDivision       --登録状況区分
    AND DELETED_FLG = '0'                            --削除フラグ(0:未削除)

--家族会員の存在チェック用SQL
SELECT_FAMILY_MEMBER_NUMBER =
SELECT
    COUNT(1) AS FAMILY_MEM_CNT                       --家族会員数
FROM
    APL_MEM_INFO                                      --アプリ会員情報
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber      --会員管理番号
    AND APPLICATION_MEMBER_ID <> :applicationMemberId     --アプリ会員ID
    AND OSAKA_PITAPA_AUTHENTICATE_FLAG = '0'         --OP認証フラグ(0:OP未認証)
    AND OSAKA_PITAPA_WITHDRAW_FLAG = '0'             --OP退会フラグ(0:未退会)
    AND DELETED_FLG = '0'                            --削除フラグ(0:未削除)

--パートナー会員サービス登録有無チェック用SQL
SELECT_PARTNER_MEM_SERVICE_INFO_COUNT =
SELECT
    COUNT(1) AS PARTNER_MEM_SERVICE_CNT              --パートナー会員サービスレコード数
FROM
    PARTNER_MEM_SERVICE_INFO                         --パートナー会員サービス情報
WHERE
    (
     (PARTNER_REGIST_CTRL_NUM = :memberControlNumber  --パートナー登録者会員管理番号
      AND PARTNER_REGIST_CTRL_BR_NUM = :memCtrlNumBrNum   --パートナー登録者会員管理番号枝番
     )
     OR
     (PARTNER_USER_MEM_CTRL_NUM = :memberControlNumber  --パートナー会員管理番号
      AND PARTNER_USER_MEM_CTRL_BR_NUM = :memCtrlNumBrNum   --パートナー会員管理番号枝番
     )
    )
    AND ADMIT_STATUS_DIVISION = :admitStatusDivision        --承認状況区分:1(承認済み)
    AND DELETED_FLG = '0'                            --削除フラグ(0:未削除)

