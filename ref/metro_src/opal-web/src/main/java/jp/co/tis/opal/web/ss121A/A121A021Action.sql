------------------------------------------------
-- 家族会員サービス登録API(A121A021Action)
------------------------------------------------

--アプリ会員情報取得用SQL
SELECT_APL_MEM_INFO =
SELECT
    MEMBER_CONTROL_NUMBER,                                     --会員管理番号
    MEM_CTRL_NUM_BR_NUM,                                       --会員管理番号枝番
    APPLICATION_MEMBER_STATUS_CODE                             --アプリ会員状態コード
FROM
    APL_MEM_INFO                                               --アプリ会員情報
WHERE
    APPLICATION_MEMBER_ID = :applicationMemberId               --アプリ会員ID
    AND APPLICATION_MEMBER_STATUS_CODE IN (:statusCodeA, :statusCodeD)           --アプリ会員状態コード
    AND DELETED_FLG = '0'                                      --削除フラグ(0:未削除)

--家族会員サービス登録有無チェック用SQL
SELECT_FAMILY_MEM_INFO_COUNT =
SELECT
    COUNT(1) AS FAMILY_MEM_SERVICE_CNT                         --家族会員サービスレコード数
FROM
    FAMILY_MEM_SERVICE_INFO                                    --家族会員サービス情報
WHERE
    MEMBER_CONTROL_NUMBER = :memberControlNumber               --会員管理番号
    AND REGIST_STATUS_DIVISION = :registStatusDivision         --登録状況区分
    AND DELETED_FLG = '0'                                      --削除フラグ(0:未削除)

--家族会員サービス情報登録用SQL
INSERT_FAMILY_MEM_SERVICE_INFO =
INSERT INTO
    FAMILY_MEM_SERVICE_INFO                                     --家族会員サービス情報
    (
    FAMILY_MEM_SERVICE_CTRL_ID,                                 --家族会員サービス管理ID
    MEMBER_CONTROL_NUMBER,                                      --会員管理番号
    APPLICANT_MEM_CTRL_NUM_BR_NUM,                              --申込者会員管理番号枝番
    RCPT_DATE_TIME,                                             --受付日時
    REGIST_STATUS_DIVISION,                                     --登録状況区分
    APPLY_START_DATE_TIME,                                      --適用開始日時
    APPLY_END_DATE_TIME,                                        --適用終了日時
    INSERT_USER_ID,                                             --登録者ID
    INSERT_DATE_TIME,                                           --登録日時
    UPDATE_USER_ID,                                             --最終更新者ID
    UPDATE_DATE_TIME,                                           --最終更新日時
    DELETED_FLG,                                                --削除フラグ
    DELETED_DATE,                                               --論理削除日
    VERSION                                                     --バージョン番号
    )
VALUES
    (
    :familyMemCtrlId,                                           --家族会員サービス管理ID
    :memberControlNumber,                                       --会員管理番号
    :applicantMemCtrlNumBrNum,                                  --申込者会員管理番号枝番
    :rcptDataTime,                                              --受付日時
    :registStatusDivision,                                      --登録状況区分
    :applyStartDateTime,                                        --適用開始日時
    :applyEndDateTime,                                          --適用終了日時
    :insertUserId,                                              --登録者ID
    :insertDateTime,                                            --登録日時
    :updateUserId,                                              --最終更新者ID
    :updateDateTime,                                            --最終更新日時
    :deletedFlg,                                                --削除フラグ
    :deletedDate,                                               --論理削除日
    :version                                                    --バージョン番号
    )
