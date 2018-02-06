------------------------------------------------------------------------------------------------------
-- パートナー乗車適用日情報作成バッチ(B124A01Action)
------------------------------------------------------------------------------------------------------

--乗車適用日登録上限回数を取得するSQL
SELECT_RIDE_UPPER_LIMIT_TIMES=
SELECT
    UPPER_LIMIT_TIMES                                                          --上限回数
FROM
    RIDE_UPPER_LIMIT_TIMES                                                     --乗車適用日登録上限回数
WHERE
    RIDE_APPLY_YEAR_MONTH = :rideApplyYearMonth                                --乗車適用年月
    AND SERVICE_DIVISION = :serviceDivision                                    --サービス区分
    AND DELETED_FLG = '0'                                                      --削除フラグ(0:未削除)

--パートナー乗車適用日情報を取得するSQL
SELECT_PARTNER_RIDE_APPLY_DATE =
SELECT
    PMSI.PARTNER_MEM_SERVICE_CTRL_ID,                                          --パートナー会員サービス管理ID
    PMSI.PARTNER_REGIST_CTRL_NUM,                                              --パートナー登録者会員管理番号
    PMSI.PARTNER_REGIST_CTRL_BR_NUM,                                           --パートナー登録者会員管理番号枝番
    PMSI.PARTNER_USER_MEM_CTRL_NUM,                                            --パートナー会員管理番号
    PMSI.PARTNER_USER_MEM_CTRL_BR_NUM,                                         --パートナー会員管理番号枝番
    PRAD.USER_CHOOSE_DIVISION,                                                 --ユーザ選択区分
    PRAD.RIDE_APPLY_DATE                                                       --乗車適用日
FROM
    PARTNER_MEM_SERVICE_INFO PMSI                                              --パートナー会員サービス情報
INNER JOIN
    PARTNER_RIDE_APPLY_DATE PRAD                                               --パートナー乗車適用日
ON
    PMSI.PARTNER_MEM_SERVICE_CTRL_ID = PRAD.PARTNER_MEM_SERVICE_CTRL_ID        --パートナー会員サービス管理ID
WHERE
    (PMSI.ADMIT_STATUS_DIVISION = :admitStatusDivision                         --登録状況区分(1:登録)
    OR PMSI.APPLY_END_DATE_TIME >= to_timestamp(:applyDateTime,'RRRR-MM-DD HH24:MI:SS.FF')      --適用終了日時
    )
    AND PMSI.APPLY_START_DATE_TIME < to_timestamp(:applyDateTime,'RRRR-MM-DD HH24:MI:SS.FF')    --適用開始日時
    AND PMSI.DELETED_FLG = '0'                                                 --削除フラグ(0:未削除)
    AND PRAD.RIDE_APPLY_YEAR_MONTH = :rideApplyYearMonth                       --乗車適用年月
    AND PRAD.DELETED_FLG = '0'                                                 --削除フラグ(0:未削除)
ORDER BY PMSI.PARTNER_MEM_SERVICE_CTRL_ID ASC,                                --パートナー会員サービス管理ID
    PRAD.USER_CHOOSE_DIVISION DESC,                                            --ユーザ選択区分
    PRAD.RIDE_APPLY_DATE DESC                                                  --パートナー乗車適用日