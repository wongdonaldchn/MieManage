------------------------------------------------
-- アプリ会員情報更新API(A113A021Action)
------------------------------------------------

--アプリ会員情報更新用SQL
UPDATE_APL_MEM_INFO =
UPDATE
    APL_MEM_INFO													-- アプリ会員情報
SET
    BIRTHDATE = :birthdate,											-- 生年月日
    SEX_CODE = :sexCode,											-- 性別コード
    RECOMMEND_USE_ACCEPT_FLAG = :recommendUseAcceptFlag,			-- レコメンド利用承諾可フラグ
    ENQUETE_1 = :enquete1,											-- アンケート1
    ENQUETE_2 = :enquete2,											-- アンケート2
    ENQUETE_3 = :enquete3,											-- アンケート3
    ENQUETE_4 = :enquete4,											-- アンケート4
    ENQUETE_5 = :enquete5,											-- アンケート5
    ENQUETE_6 = :enquete6,											-- アンケート6
    ENQUETE_7 = :enquete7,											-- アンケート7
    ENQUETE_8 = :enquete8,											-- アンケート8
    ENQUETE_9 = :enquete9,											-- アンケート9
    ENQUETE_10 = :enquete10,										-- アンケート10
    MAIN_USE_STATION_1 = :mainUseStation1,							-- 主なご利用駅1
    MAIN_USE_STATION_2 = :mainUseStation2,							-- 主なご利用駅2
    MAIN_USE_STATION_3 = :mainUseStation3,							-- 主なご利用駅3
    MAIN_USE_STATION_4 = :mainUseStation4,							-- 主なご利用駅4
    MAIN_USE_STATION_5 = :mainUseStation5,							-- 主なご利用駅5
    DAY_OFF_1 = :dayOff1,											-- 休日1
    DAY_OFF_2 = :dayOff2,											-- 休日2
    UPDATE_USER_ID = :updateUserId,									-- 最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,								-- 最終更新日時
    VERSION = VERSION +1											-- バージョン番号
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId					-- アプリ会員情報TBL.アプリ会員ID = 取得されたアプリ会員ID
    AND DELETED_FLG = :deletedFlg									-- アプリ会員情報TBL.削除フラグ = "0"(未削除)
    AND APPLICATION_MEMBER_STATUS_CODE IN (:statusA, :statusD)		-- アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員),"D"(OP非会員))
