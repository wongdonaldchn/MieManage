------------------------------------------------
-- マイル残高取得API(A131A011Action)
------------------------------------------------

-- 使用可能マイル数（当年度3月末日までの有効）取得用SQL
SELECT_THIS_YEAR_MILE_BALANCE =
SELECT
	NVL(SUM(MILE_BALANCE), 0) AS SUM_MILE_BALANCE							-- 当年度使用可能マイル残高
FROM MILE_BALANCE_INFO														-- マイル残高情報
WHERE APPLICATION_MEMBER_ID = :applicationMemberId							-- アプリ会員ID
	AND OBJECT_YEAR_MONTH BETWEEN :beginYearMonth AND :endYearMonth		-- 対象年月（当年度開始年月～当年度終了年月）
	AND MILE_BALANCE > 0													-- マイル残高
	AND DELETED_FLG = '0'													-- 削除フラグ＝"0"(未削除)

-- 使用可能マイル数（来年度3月末日までの有効）取得用SQL
SELECT_NEXT_YEAR_MILE_BALANCE =
SELECT
	NVL(SUM(MILE_BALANCE), 0) AS SUM_MILE_BALANCE							-- 当年度使用可能マイル残高
FROM MILE_BALANCE_INFO														-- マイル残高情報
WHERE APPLICATION_MEMBER_ID = :applicationMemberId							-- アプリ会員ID
	AND OBJECT_YEAR_MONTH BETWEEN :beginYearMonth AND :endYearMonth		-- 対象年月（来年度開始年月～来年度終了年月）
	AND MILE_BALANCE > 0													-- マイル残高
	AND DELETED_FLG = '0'													-- 削除フラグ＝"0"(未削除)

-- アプリ会員情報取得
SELECT_APL_MEM_INFO =
SELECT
    APPLICATION_MEMBER_ID													-- アプリ会員ID
FROM
    APL_MEM_INFO															-- アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId							-- アプリ会員情報TBL.アプリ会員ID = マイル残高取得要求電文.アプリ会員ID
    AND DELETED_FLG = '0'													-- アプリ会員情報TBL.削除フラグ = "0"(未削除)
    AND APPLICATION_MEMBER_STATUS_CODE IN (:statusA,:statusD)				-- アプリ会員情報TBL.アプリ会員状態コード IN ( "A", "D")