package com.donald.miem.common.component;

import java.util.HashMap;
import java.util.Map;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.support.DbAccessSupport;
import nablarch.core.repository.SystemRepository;

import com.donald.miem.common.constants.OpalDefaultConstants;
import com.donald.miem.common.utility.IdGeneratorUtil;

/**
 * CM010002:プッシュ通知情報登録の共通コンポーネント
 *
 * @author 曹
 * @since 1.0
 */
public class CM010002Component extends DbAccessSupport {

    /** 処理ID */
    private String processedId;
    /** 送信区分 */
    private String diliverDivision;
    /** 送信タイプ */
    private String diliverType;
    /** テンプレートID */
    private String templateId;
    /** 送信日時 */
    private String diliverDateTime;
    /** 1ファイル当たりの宛先件数上限 */
    private int upperLimitNumber;
    /** プッシュ通知ID */
    private Long pushNoticeId;
    /** 論理削除日 */
    private String deletedDate;
    /** 連番 */
    private int sequenceMember = 0;
    /** 処理データ件数 */
    private int count = 0;

    /**
     * コンストラクタ
     *
     * @param processedId
     *            処理ID
     * @param diliverDivision
     *            送信区分
     * @param diliverType
     *            送信タイプ
     * @param templateId
     *            テンプレートID
     * @param diliverDateTime
     *            送信日時
     * @param upperLimitNumber
     *            1ファイル当たりの宛先件数上限
     * @return
     */
    public CM010002Component(String processedId, String diliverDivision, String diliverType, String templateId,
            String diliverDateTime, int upperLimitNumber) {

        // 論理削除日を導出する。
        CM010004Component cM010004Component = new CM010004Component();
        this.deletedDate = cM010004Component.getDeletedDateMonthly(
                Integer.parseInt(SystemRepository.getString("push_notice_info_data_retention_period")));
        this.processedId = processedId;
        this.diliverDivision = diliverDivision;
        this.diliverType = diliverType;
        this.templateId = templateId;
        this.diliverDateTime = diliverDateTime;
        this.upperLimitNumber = upperLimitNumber;
    }

    /**
     * {@inneritDoc} プッシュ通知情報登録。
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param applicationId
     *            アプリID
     * @param deviceId
     *            デバイスID
     * @return 処理結果Map
     */
    public Map<String, Integer> insPushNoticeInformation(Long applicationMemberId, String applicationId,
            String deviceId) {
        // 処理結果Map
        Map<String, Integer> result = new HashMap<String, Integer>();
        // プッシュ通知情報出力件数
        int pushNoticeIfoOutputCnt = 0;
        // プッシュ通知送信先情報出力件数
        int pushNoticeDestInfoOutputCnt = 0;
        // 連番
        this.sequenceMember++;
        // 処理データ件数
        this.count++;
        // クラス変数.処理件数 % クラス変数.上限件数 = 1の場合、プッシュ通知情報を登録する
        if (count % upperLimitNumber == 1) {
            // 連番
            this.sequenceMember = 1;
            // プッシュ通知ID
            this.pushNoticeId = IdGeneratorUtil.generatePushNoticeId();
            // プッシュ通知情報登録
            pushNoticeIfoOutputCnt = insertPushNoticeInformation();
        }
        // プッシュ通知送信先情報登録
        pushNoticeDestInfoOutputCnt = insertPushNoticeDestInfo(applicationMemberId, applicationId, deviceId);
        // プッシュ通知情報出力件数
        result.put("PUSH_NOTICE_INFORMATION_OUTPUT_CNT", pushNoticeIfoOutputCnt);
        // プッシュ通知送信先情報出力件数
        result.put("PUSH_NOTICE_DEST_INFO_OUTPUT_CNT", pushNoticeDestInfoOutputCnt);
        return result;
    }

    /**
     * プッシュ通知情報登録
     *
     * @return プッシュ通知情報出力件数
     */
    private int insertPushNoticeInformation() {

        // プッシュ通知情報登録条件設定
        Map<String, Object> condition = new HashMap<String, Object>();
        // プッシュ通知ID
        condition.put("pushNoticeId", pushNoticeId);
        // 処理ID
        condition.put("opalProcessId", processedId);
        // 送信区分
        condition.put("diliverDivision", diliverDivision);
        // 送信タイプ
        condition.put("diliverType", diliverType);
        // テンプレートID
        condition.put("templateId", templateId);
        // 送信日時
        condition.put("diliverDateTime", diliverDateTime);
        // プッシュ通知識別ID
        condition.put("pushNoticeDistinguishId", null);
        // 処理済フラグ
        condition.put("processedFlag", OpalDefaultConstants.PROCESSED_FLAG_0);
        // 登録者ID
        condition.put("insertUserId", processedId);
        // 登録日時
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者ID
        condition.put("updateUserId", processedId);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        condition.put("deletedDate", deletedDate);

        // プッシュ通知情報登録
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_PUSH_NOTICE_INFORMATION");
        return statement.executeUpdateByMap(condition);
    }

    /**
     * プッシュ通知送信先情報登録
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param applicationId
     *            アプリID
     * @param deviceId
     *            デバイスID
     * @return プッシュ通知送信先情報出力件数
     */
    private int insertPushNoticeDestInfo(Long applicationMemberId, String applicationId, String deviceId) {

        // プッシュ通知送信先情報登録条件設定
        Map<String, Object> condition = new HashMap<String, Object>();
        // プッシュ通知ID
        condition.put("pushNoticeId", pushNoticeId);
        // 連番
        condition.put("sequenceMember", sequenceMember);
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // アプリID
        condition.put("applicationId", applicationId);
        // デバイスID
        condition.put("deviceId", deviceId);
        // 登録者ID
        condition.put("insertUserId", processedId);
        // 登録日時
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者ID
        condition.put("updateUserId", processedId);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        condition.put("deletedDate", deletedDate);

        // プッシュ通知送信先情報登録
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_PUSH_NOTICE_DEST_INFO");
        return statement.executeUpdateByMap(condition);
    }

}
