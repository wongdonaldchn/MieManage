--------------------------------------------------------------------------------------
-- マイル集計(B132A011Action)
--------------------------------------------------------------------------------------

-- 集計対象アプリ会員情報取得
SELECT_APPLICATION_MEM_ID =
SELECT
    AMI.APPLICATION_MEMBER_ID
FROM
    APL_MEM_INFO AMI												-- アプリ会員情報TBL
WHERE
    NOT EXISTS
        (
        SELECT
            MSI.APPLICATION_MEMBER_ID                               -- アプリ会員ID
        FROM
            MILE_SUMMARY_INFORMATION MSI							-- マイル集計情報TBL
        WHERE
            MSI.APPLICATION_MEMBER_ID = AMI.APPLICATION_MEMBER_ID	-- アプリ会員ID
            AND MSI.MILE_SUM_YEAR_MONTH = :mileSummaryYearMonth		-- マイル集計年月
        )
ORDER BY
    AMI.APPLICATION_MEMBER_ID										-- アプリ会員ID

-- マイル種別集計情報取得
SELECT_CATEGORY_TOTAL_MILE =
SELECT
    MILE_CATEGORY_CODE,												-- マイル種別コード
    NVL(SUM(MILE_AMOUNT), 0) AS MILE_TOTAL							-- マイル合計
FROM
    MILE_HISTORY_INFORMATION										-- マイル履歴情報TBL
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId					-- アプリ会員ID
    AND MILE_HISTORY_REGIST_DATE LIKE :mileSummaryYearMonth		-- マイル集計年月
    AND DELETED_FLG = '0'											-- 削除フラグ
GROUP BY
    MILE_CATEGORY_CODE												-- マイル種別コード

-- 獲得マイル・合計取得
SELECT_GET_TOTAL_MILE =
SELECT
    SUM(MILE_TOTAL) AS ACQUIRE_MILE_TOTAL					-- 獲得マイル・合計
FROM
    MILE_CATEGORY_SUM_INFO											-- マイル種別集計情報TBL
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId					-- アプリ会員ID
    AND MILE_SUM_YEAR_MONTH = :mileSummaryYearMonth					-- マイル集計年月
    AND MILE_CATEGORY_CODE LIKE :mileCategoryCode					-- マイル種別コード（先頭1桁 = "A"）
    AND DELETED_FLG = '0'											-- 削除フラグ

-- 使用マイル・合計取得
SELECT_USE_TOTAL_MILE =
SELECT
    SUM(MILE_TOTAL) AS ACQUIRE_MILE_TOTAL					-- 使用マイル・合計
FROM
    MILE_CATEGORY_SUM_INFO											-- マイル種別集計情報TBL
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId					-- アプリ会員ID
    AND MILE_SUM_YEAR_MONTH = :mileSummaryYearMonth					-- マイル集計年月
    AND MILE_CATEGORY_CODE LIKE :mileCategoryCode					-- マイル種別コード（先頭1桁 = "S"）
    AND DELETED_FLG = '0'											-- 削除フラグ

-- 前月末マイル残高取得
SELECT_LAST_MONTH_TOTAL_MILE =
SELECT
    THIS_MONTH_END_MILE_BALANCE                                                         -- 当月末マイル残高
FROM
    MILE_SUMMARY_INFORMATION											                    -- マイル集計情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId					            -- アプリ会員ID
    AND MILE_SUM_YEAR_MONTH = :lastMileSummaryYearMonth			-- マイル集計年月
    AND DELETED_FLG = '0'											                                -- 削除フラグ

-- マイル種別集計情報登録
INSERT_MILE_CATEGORY_SUMMARY_INFO =
INSERT INTO
    MILE_CATEGORY_SUM_INFO											-- マイル種別集計情報
    (
    APPLICATION_MEMBER_ID,											-- アプリ会員ID
    MILE_SUM_YEAR_MONTH,											-- マイル集計年月
    MILE_CATEGORY_CODE,												-- マイル種別コード
    MILE_TOTAL,														-- マイル合計
    INSERT_USER_ID,													-- 登録者ID
    INSERT_DATE_TIME,												-- 登録日時
    UPDATE_USER_ID,													-- 最終更新者ID
    UPDATE_DATE_TIME,												-- 最終更新日時
    DELETED_FLG,													-- 削除フラグ
    DELETED_DATE													-- 論理削除日
    )
VALUES
    (
    :applicationMemberId,											-- アプリ会員ID
    :mileSumYearMonth,												-- マイル集計年月
    :mileCategoryCode,												-- マイル種別コード
    :mileTotal,														-- マイル合計
    :insertUserId,													-- 登録者ID
    :insertDateTime,												-- 登録日時
    :updateUserId,													-- 最終更新者ID
    :updateDateTime,												-- 最終更新日時
    :deletedFlg,													-- 削除フラグ
    :deletedDate													-- 論理削除日
    )

-- マイル集計情報登録
INSERT_MILE_SUMMARY_INFO =
INSERT INTO
    MILE_SUMMARY_INFORMATION										-- マイル集計情報
    (
    APPLICATION_MEMBER_ID,											-- アプリ会員ID
    MILE_SUM_YEAR_MONTH,											-- マイル集計年月
    ACQUIRE_MILE_TOTAL,												-- 獲得マイル合計
    USE_MILE_TOTAL,													-- 使用マイル合計
    LAST_MONTH_END_MILE_BALANCE,									-- 前月末マイル残高
    THIS_MONTH_END_MILE_BALANCE,									-- 当月末マイル残高
    INSERT_USER_ID,													-- 登録者ID
    INSERT_DATE_TIME,												-- 登録日時
    UPDATE_USER_ID,													-- 最終更新者ID
    UPDATE_DATE_TIME,												-- 最終更新日時
    DELETED_FLG,													-- 削除フラグ
    DELETED_DATE													-- 論理削除日
    )
VALUES
    (
    :applicationMemberId,											-- アプリ会員ID
    :mileSumYearMonth,												-- マイル集計年月
    :acquireMileTotal,												-- 獲得マイル合計
    :useMileTotal,													-- 使用マイル合計
    :lastMonthEndMileBalance,										-- 前月末マイル残高
    :thisMonthEndMileBalance,										-- 当月末マイル残高
    :insertUserId,													-- 登録者ID
    :insertDateTime,												-- 登録日時
    :updateUserId,													-- 最終更新者ID
    :updateDateTime,												-- 最終更新日時
    :deletedFlg,													-- 削除フラグ
    :deletedDate													-- 論理削除日
    )
