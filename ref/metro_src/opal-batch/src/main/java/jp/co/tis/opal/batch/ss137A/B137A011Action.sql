------------------------------------------------------------------------------------------------------
-- マイル調整バッチ(B137A011Action)
------------------------------------------------------------------------------------------------------

-- マイル調整指示情報取得するSQL
SELECT_MILE_ADJUST_INSTR_INFO =
SELECT
    AMI.APPLICATION_MEMBER_ID,                        --アプリ会員ID
    AMI.APPLICATION_ID,                               --アプリID
    AMI.DEVICE_ID,                                    --デバイスID
    AMI.MAIL_ADDRESS,                                 --メールアドレス
    AMI.APPLICATION_MEMBER_STATUS_CODE,               --アプリ会員状態コード
    AMI.MAIL_DELIVER_STATUS_DIVISION,                 --メール配信状態区分
    MAII.MILE_ADJUST_INSTR_ID,                        --マイル調整指示ID
    MAII.MILE_CATEGORY_CODE,                          --マイル種別コード
    MAII.ADJUST_MILE_AMOUNT                           --調整マイル数
FROM
    MILE_ADJUST_INSTR_INFO MAII                       --マイル調整指示情報
INNER JOIN
    APL_MEM_INFO AMI                                  --アプリ会員情報
ON
    MAII.APPLICATION_MEMBER_ID = AMI.APPLICATION_MEMBER_ID      --アプリ会員ID
WHERE
    MAII.MILE_ADJUST_INSTR_DATE = :mileAdjustInstrDate    --マイル調整指示日
    AND MAII.MILE_ADJUST_STATUS_DIVI = :mileAdjustStatusDivi    --未調整のレコードが対象
    AND MAII.DELETED_FLG = '0'                           --削除フラグ(0:未削除)
    AND AMI.DELETED_FLG = '0'                            --削除フラグ(0:未削除)
ORDER BY
    MAII.APPLICATION_MEMBER_ID,                           --アプリ会員ID
    MAII.MILE_ADJUST_INSTR_ID                             --マイル調整指示ID

-- マイル残高合計取得するSQL
SELECT_SUM_MILE_BALANCE =
SELECT
    SUM(MILE_BALANCE) SUM_MILE_BALANCE              --マイル残高合計
FROM
    MILE_BALANCE_INFO                               --マイル残高情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId    --アプリ会員ID
    AND OBJECT_YEAR_MONTH >= :objectYearMonth      --対象年月
    AND MILE_BALANCE > 0                            --マイル残高
    AND DELETED_FLG = '0'                           --削除フラグ(0:未削除)
