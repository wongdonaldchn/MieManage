package com.donald.miem.common.utility;

import nablarch.common.idgenerator.IdGenerator;
import nablarch.core.repository.SystemRepository;

/**
 * 採番処理(各採番対象IDの採番番号の取得)
 *
 * @author 張
 * @since 1.0
 *
 */
public final class IdGeneratorUtil {

    /**
     * アプリ会員登録受付ID（1～9999999999）
     *
     * @return アプリ会員登録受付ID
     */
    public static long generateAplMemRcptId() {
        IdGenerator generator = (IdGenerator) SystemRepository.getObject("oracleSequence");

        return Long.valueOf(generator.generateId("1100"));
    }

    /**
     * アプリ会員ID（1～9999999999）
     *
     * @return アプリ会員ID
     */
    public static long generateApplicationMemberId() {
        IdGenerator generator = (IdGenerator) SystemRepository.getObject("oracleSequence");

        return Long.valueOf(generator.generateId("1101"));
    }

    /**
     * ログイン履歴ID（1～9999999999）
     *
     * @return ログイン履歴ID
     */
    public static long generateLoginHistoryId() {
        IdGenerator generator = (IdGenerator) SystemRepository.getObject("oracleSequence");

        return Long.valueOf(generator.generateId("1102"));
    }

    /**
     * ログイン情報再登録受付ID（1～9999999999）
     *
     * @return ログイン情報再登録受付ID
     */
    public static long generateLoginInfoReregistRcptId() {
        IdGenerator generator = (IdGenerator) SystemRepository.getObject("oracleSequence");

        return Long.valueOf(generator.generateId("1103"));
    }

    /**
     * メールアドレス変更受付ID（1～9999999999）
     *
     * @return メールアドレス変更受付ID
     */
    public static long generateMailAddressChangeRcptId() {
        IdGenerator generator = (IdGenerator) SystemRepository.getObject("oracleSequence");

        return Long.valueOf(generator.generateId("1104"));
    }

    /**
     * パートナー会員サービス管理ID（1～9999999999）
     *
     * @return パートナー会員サービス管理ID
     */
    public static long generatePartnerMemCtrlId() {
        IdGenerator generator = (IdGenerator) SystemRepository.getObject("oracleSequence");

        return Long.valueOf(generator.generateId("1200"));
    }

    /**
     * 家族会員サービス管理ID（1～9999999999）
     *
     * @return 家族会員サービス管理ID
     */
    public static long generateFamilyMemCtrlId() {
        IdGenerator generator = (IdGenerator) SystemRepository.getObject("oracleSequence");

        return Long.valueOf(generator.generateId("1201"));
    }

    /**
     * マイル履歴ID（1～999999999999999）
     *
     * @return マイル履歴ID
     */
    public static long generateMileHistoryId() {
        IdGenerator generator = (IdGenerator) SystemRepository.getObject("oracleSequence");

        return Long.valueOf(generator.generateId("1300"));
    }

    /**
     * マイル調整指示ID（1～999999999999999）
     *
     * @return マイル調整指示ID
     */
    public static long generateMileAdjustInstrId() {
        IdGenerator generator = (IdGenerator) SystemRepository.getObject("oracleSequence");

        return Long.valueOf(generator.generateId("1301"));
    }

    /**
     * 郵送受付ID（1～9999999999）
     *
     * @return 郵送受付ID
     */
    public static long generatePostReceiptId() {
        IdGenerator generator = (IdGenerator) SystemRepository.getObject("oracleSequence");

        return Long.valueOf(generator.generateId("1400"));
    }

    /**
     * プッシュ通知ID（1～999999999999999）
     *
     * @return プッシュ通知ID
     */
    public static long generatePushNoticeId() {
        IdGenerator generator = (IdGenerator) SystemRepository.getObject("oracleSequence");

        return Long.valueOf(generator.generateId("1501"));
    }

    /**
     * メール個別配信ID（1～999999999999999）
     *
     * @return メール個別配信ID
     */
    public static long generateMailLiteDeliverId() {
        IdGenerator generator = (IdGenerator) SystemRepository.getObject("oracleSequence");

        return Long.valueOf(generator.generateId("1502"));
    }

    /**
     * メール一括配信ID（1～999999999999999）
     *
     * @return メール一括配信ID
     */
    public static long generateMailPackDeliverId() {
        IdGenerator generator = (IdGenerator) SystemRepository.getObject("oracleSequence");

        return Long.valueOf(generator.generateId("1503"));
    }
}
