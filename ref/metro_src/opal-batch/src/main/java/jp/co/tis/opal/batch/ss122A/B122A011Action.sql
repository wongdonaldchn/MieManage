------------------------------------------------------------------------------------------------------
-- 家族乗車適用日情報作成バッチ(B122A01Action)
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

--家族乗車適用日情報を取得するSQL
SELECT_FAMILY_RIDE_APPLY_DATE =
SELECT
    FMSI.MEMBER_CONTROL_NUMBER,                                                 --会員管理番号
    FRAD.USER_CHOOSE_DIVISION,                                                  --ユーザ選択区分
    FRAD.RIDE_APPLY_DATE                                                        --乗車適用日
FROM
    FAMILY_MEM_SERVICE_INFO FMSI                                                --家族会員サービス情報
INNER JOIN
    FAMILY_RIDE_APPLY_DATE FRAD                                                 --家族乗車適用日
ON
    FMSI.FAMILY_MEM_SERVICE_CTRL_ID = FRAD.FAMILY_MEM_SERVICE_CTRL_ID           --家族会員サービス管理ID
WHERE
    (FMSI.REGIST_STATUS_DIVISION = :registStatusDivision                         --登録状況区分(1:登録)
    OR FMSI.APPLY_END_DATE_TIME >= to_timestamp(:applyDateTime,'RRRR-MM-DD HH24:MI:SS.FF')      --適用終了日時
    )
    AND FMSI.APPLY_START_DATE_TIME < to_timestamp(:applyDateTime,'RRRR-MM-DD HH24:MI:SS.FF')    --適用開始日時
    AND FMSI.DELETED_FLG = '0'                                                  --削除フラグ(0:未削除)
    AND FRAD.RIDE_APPLY_YEAR_MONTH = :rideApplyYearMonth                        --乗車適用年月
    AND FRAD.DELETED_FLG = '0'                                                  --削除フラグ(0:未削除)
ORDER BY FMSI.MEMBER_CONTROL_NUMBER ASC,                                       --会員管理番号
    FRAD.USER_CHOOSE_DIVISION DESC,                                             --ユーザ選択区分
    FRAD.RIDE_APPLY_DATE DESC                                                   --家族乗車適用日