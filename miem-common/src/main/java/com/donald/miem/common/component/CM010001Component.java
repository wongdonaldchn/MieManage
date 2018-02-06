package com.donald.miem.common.component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.support.DbAccessSupport;
import nablarch.core.repository.SystemRepository;

import com.donald.miem.common.constants.OpalCodeConstants.MailDeliverStatus;
import com.donald.miem.common.constants.OpalDefaultConstants;
import com.donald.miem.common.utility.IdGeneratorUtil;

/**
 * 共通コンポーネント：メール配信情報登録
 *
 * @author 張
 * @since 1.0
 */
public class CM010001Component extends DbAccessSupport {

    /**
     * {@inneritDoc} メール個別配信情報TBLに、メール配信情報を登録する。
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param opalProcessId
     *            処理ID
     * @param mailAddress
     *            メールアドレス
     * @param deliverType
     *            配信タイプ
     * @param templateId
     *            テンプレートID
     * @param variableItemValues
     *            差し込み項目
     * @param deliverDate
     *            配信日時
     */
    public void insMailLiteDeliverInfo(Long applicationMemberId, String opalProcessId, String mailAddress,
            String deliverType, String templateId, List<String> variableItemValues, Date deliverDate) {

        // メール個別配信IDを採番する。(採番対象ID：1502)
        Long mailLiteDeliverId = IdGeneratorUtil.generateMailLiteDeliverId();
        // 論理削除日の算出
        String deletedDate = this.getDeletedDate("mail_lite_deliver_info_data_retention_period");

        // メール個別配信情報登録用のSQL条件を設定する。
        Map<String, Object> inputData = new HashMap<String, Object>();
        // メール個別配信ID
        inputData.put("mailLiteDeliverId", mailLiteDeliverId);
        // 処理ID
        inputData.put("opalProcessId", opalProcessId);
        // 配信タイプ
        inputData.put("deliverType", deliverType);
        // アプリ会員ID
        inputData.put("applicationMemberId", applicationMemberId);
        // メールアドレス
        inputData.put("mailAddress", mailAddress);
        // テンプレートID
        inputData.put("templateId", templateId);
        // 差し込み項目
        this.setVariableItemValuesInput(inputData, variableItemValues);
        // 配信日時
        inputData.put("deliverDate", ((deliverDate == null) ? null : this.dateToTimestamp(deliverDate)));
        // メール配信状況＝"1"(処理待ち)
        inputData.put("mailDeliverStatus", MailDeliverStatus.MAIL_DELIVER_STATUS_1);
        // 登録者ID
        inputData.put("insertUserId", opalProcessId);
        // 登録日時
        inputData.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者ID
        inputData.put("updateUserId", opalProcessId);
        // 最終更新日時
        inputData.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ
        inputData.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        inputData.put("deletedDate", deletedDate);

        // メール個別配信情報TBLにメール配信情報登録
        ParameterizedSqlPStatement insertStatement = super.getParameterizedSqlStatement(
                "INSERT_MAIL_LITE_DELIVER_INFO");
        insertStatement.executeUpdateByMap(inputData);
    }

    /**
     * 差し込み項目のSQL条件を設定する。
     *
     * @param inputData
     *            SQL条件
     * @param variableItemValues
     *            差し込み項目
     */
    private void setVariableItemValuesInput(Map<String, Object> inputData, List<String> variableItemValues) {

        int itemIndex = 1;
        for (String itemValue : variableItemValues) {
            StringBuilder key = new StringBuilder();
            key.append(String.format("variableItemValue%s", itemIndex));
            // 差し込み項目
            inputData.put(key.toString(), itemValue);
            itemIndex++;
        }

        // パラメータ.差し込み項目(0～9)、存在しない場合はNULL
        for (int index = itemIndex; index <= 10; index++) {
            StringBuilder key = new StringBuilder();
            key.append(String.format("variableItemValue%s", index));
            // 差し込み項目
            inputData.put(key.toString(), null);
        }
    }

    /**
     * 日時を取得する。
     *
     * @param date
     *            配信日時
     * @return 日時
     */
    private Timestamp dateToTimestamp(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String time = df.format(date);
        Timestamp ts = Timestamp.valueOf(time);
        return ts;
    }

    /**
     * {@inneritDoc} メール一括配信情報TBLに、メール配信情報を登録する。
     * <p/>
     *
     * @param opalProcessId
     *            処理ID
     * @param deliverType
     *            配信タイプ
     * @param templateId
     *            テンプレートID
     * @param deliverFileName
     *            配信ファイル名称
     * @param deliverDate
     *            配信日時
     */
    public void insMailPackDeliverInfo(String opalProcessId, String deliverType, String templateId,
            String deliverFileName, Date deliverDate) {

        // メール一括配信IDを採番する。(採番対象ID：1503)
        Long mailPackDeliverId = IdGeneratorUtil.generateMailPackDeliverId();
        // 論理削除日の算出
        String deletedDate = this.getDeletedDate("mail_pack_deliver_info_data_retention_period");

        // メール一括配信情報登録用のSQL条件を設定する。
        Map<String, Object> inputData = new HashMap<String, Object>();
        // メール一括配信ID
        inputData.put("mailPackDeliverId", mailPackDeliverId);
        // 処理ID
        inputData.put("opalProcessId", opalProcessId);
        // 配信タイプ
        inputData.put("deliverType", deliverType);
        // テンプレートID
        inputData.put("templateId", templateId);
        // 配信日時
        inputData.put("deliverDate", ((deliverDate == null) ? null : this.dateToTimestamp(deliverDate)));
        // 配信ファイル名称
        inputData.put("deliverFileName", deliverFileName);
        // メール配信状況
        inputData.put("mailDeliverStatus", MailDeliverStatus.MAIL_DELIVER_STATUS_1);
        // 登録者ID
        inputData.put("insertUserId", opalProcessId);
        // 登録日時
        inputData.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者ID
        inputData.put("updateUserId", opalProcessId);
        // 最終更新日時
        inputData.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ
        inputData.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        inputData.put("deletedDate", deletedDate);

        // メール一括配信情報TBLにメール配信情報登録
        ParameterizedSqlPStatement insertStatement = super.getParameterizedSqlStatement(
                "INSERT_MAIL_PACK_DELIVER_INFO");
        insertStatement.executeUpdateByMap(inputData);
    }

    /**
     * 論理削除日を導出する。
     *
     * @param settingName
     *            設定値名
     * @return 論理削除日(YYYYMMDDの形式)文字列
     */
    private String getDeletedDate(String settingName) {

        // 論理削除日の算出
        CM010004Component cM010004Component = new CM010004Component();

        // データ保持期間：システム設定ファイル(opal.config)から取得する。
        String monthSpan = SystemRepository.getString(settingName);

        // 論理削除日
        String date = cM010004Component.getDeletedDateMonthly(Integer.valueOf(monthSpan));

        return date;
    }
}
