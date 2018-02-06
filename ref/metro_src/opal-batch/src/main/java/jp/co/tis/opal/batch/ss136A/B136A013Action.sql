------------------------------------------------------------------------------------------------------
-- マイル失効バッチ(B136A013Action)
------------------------------------------------------------------------------------------------------

-- マイル移行一時情報取得するSQL
SELECT_MILE_TRANS_TEMP_INFO =
SELECT
    MEMBER_CONTROL_NUMBER,                     --会員管理番号
    MEM_CTRL_NUM_BR_NUM,                       --会員管理番号枝番
    OSAKA_PITAPA_NUMBER,                       --OP番号
    TRANSITION_MILE_AMOUNT,                    --移行マイル数
    MILE_TRANSITION_DIVISION                   --マイル移行区分
FROM
    MILE_TRANS_TEMP_INFO                       --マイル移行一時情報
WHERE
    DELETED_FLG = '0'                          --未削除のレコードが対象
ORDER BY
    MEMBER_CONTROL_NUMBER,                     --会員管理番号
    MEM_CTRL_NUM_BR_NUM                        --会員管理番号枝番

