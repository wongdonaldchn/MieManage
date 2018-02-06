------------------------------------------------------------------------------------------------------
-- マイル計算共通コンポーネント
------------------------------------------------------------------------------------------------------

--マイル残高情報を検索するSQL(加算用)
SELECT_MILE_BALANCE_INFO_ADD =
SELECT
    MILE_BALANCE                                     --マイル残高
FROM
    MILE_BALANCE_INFO                                --マイル残高情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId     --アプリ会員ID
    AND OBJECT_YEAR_MONTH = :objectYearMonth        --対象年月
    AND DELETED_FLG = '0'                           --削除フラグ(0:未削除)

--マイル残高情報を登録するSQL
INSERT_MILE_BALANCE_INFO_ADD =
INSERT INTO
    MILE_BALANCE_INFO                          --マイル残高情報
    (
    APPLICATION_MEMBER_ID,                     --アプリ会員ID
    OBJECT_YEAR_MONTH,                         --対象年月
    MILE_BALANCE,                              --マイル残高
    INSERT_USER_ID,                            --登録者ID
    INSERT_DATE_TIME,                          --登録日時
    UPDATE_USER_ID,                            --最終更新者ID
    UPDATE_DATE_TIME,                          --最終更新日時
    DELETED_FLG,                               --削除フラグ
    DELETED_DATE,                              --論理削除日
    VERSION                                    --バージョン番号
    )
VALUES
    (
    :applicationMemberId,                      --アプリ会員ID
    :objectYearMonth,                          --対象年月
    :mileBalance,                              --マイル残高
    :insertUserId,                             --登録者ID
    :insertDateTime,                           --登録日時
    :updateUserId,                             --最終更新者ID
    :updateDateTime,                           --最終更新日時
    :deletedFlg,                               --削除フラグ
    :deletedDate,                              --論理削除日
    :version                                   --バージョン番号
    )

--マイル残高情報を更新するSQL
UPDATE_MILE_BALANCE_INFO =
UPDATE
    MILE_BALANCE_INFO                           --マイル残高情報
SET
    MILE_BALANCE = :mileBalance,                --マイル残高
    UPDATE_USER_ID = :updateUserId,             --最終更新者ID
    UPDATE_DATE_TIME = :updateDateTime,         --最終更新日時
    VERSION = VERSION + 1                       --バージョン番号
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId    --アプリ会員ID
    AND OBJECT_YEAR_MONTH = :objectYearMonth       --対象年月

-- マイル履歴登録するSQL
INSERT_MILE_HISTORY_INFORMATION =
INSERT INTO
    MILE_HISTORY_INFORMATION                   --マイル履歴登録
    (
    MILE_HISTORY_ID,                           --マイル履歴ID
    APPLICATION_MEMBER_ID,                     --アプリ会員ID
    MILE_ADD_SUB_RCPT_NUM,                     --マイル加算減算受付番号
    MILE_CATEGORY_CODE,                        --マイル種別コード
    MILE_AMOUNT,                               --マイル数
    MILE_HISTORY_REGIST_DATE,                  --マイル履歴登録日
    INSERT_USER_ID,                            --登録者ID
    INSERT_DATE_TIME,                          --登録日時
    UPDATE_USER_ID,                            --最終更新者ID
    UPDATE_DATE_TIME,                          --最終更新日時
    DELETED_FLG,                               --削除フラグ
    DELETED_DATE                               --論理削除日
    )
VALUES
    (
    :mileHistoryId,                            --マイル履歴ID
    :applicationMemberId,                      --アプリ会員ID
    :mileAddSubRcptNum,                        --マイル加算減算受付番号
    :mileCategoryCode,                         --マイル種別コード
    :mileAmount,                               --マイル数
    :mileHistoryRegistDate,                    --マイル履歴登録日
    :insertUserId,                             --登録者ID
    :insertDateTime,                           --登録日時
    :updateUserId,                             --最終更新者ID
    :updateDateTime,                           --最終更新日時
    :deletedFlg,                               --削除フラグ
    :deletedDate                               --論理削除日
    )

--マイル残高情報を検索するSQL(減算用)
SELECT_MILE_BALANCE_INFO_SUB =
SELECT
    MILE_BALANCE,                                    --マイル残高
    OBJECT_YEAR_MONTH                                --対象年月
FROM
    MILE_BALANCE_INFO                                --マイル残高情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId     --アプリ会員ID
    AND OBJECT_YEAR_MONTH >= :objectYearMonth        --対象年月
    AND MILE_BALANCE > 0                             --マイル残高
    AND DELETED_FLG = '0'                           --削除フラグ(0:未削除)
ORDER BY
    OBJECT_YEAR_MONTH                                --対象年月

