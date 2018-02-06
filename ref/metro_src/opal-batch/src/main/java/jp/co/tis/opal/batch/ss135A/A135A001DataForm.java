package jp.co.tis.opal.batch.ss135A;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * 乗車マイル情報取込のバリデーションに使用するフォームクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A135A001DataForm {

    /**
     * 会員管理番号
     */
    @Domain("memCtrlNum")
    @Required(message = "{M000000001}")
    private String memCtrlNum;

    /**
     * 会員管理番号枝番
     */
    @Domain("memrCtrlBrNum")
    @Required(message = "{M000000001}")
    private String memCtrlNumBrNum;

    /**
     * 対象年月
     */
    @Domain("yearMonth")
    @Required(message = "{M000000001}")
    private String objectYearMonth;

    /**
     * OP番号
     */
    @Domain("opNum")
    private String opNum;

    /**
     * マイル種別コード
     */
    @Domain("mileCategoryCode")
    @Required(message = "{M000000001}")
    private String mileCategoryCode;

    /**
     * 乗車マイル数
     */
    @Domain("mileAmount")
    @Required(message = "{M000000001}")
    private String rideMileAmount;

    /**
     * 会員管理番号を返します。
     *
     * @return 会員管理番号
     */
    public String getMemCtrlNum() {
        return memCtrlNum;
    }

    /**
     * 会員管理番号を設定します。
     *
     * @param memCtrlNum
     *            会員管理番号
     */
    public void setMemCtrlNum(String memCtrlNum) {
        this.memCtrlNum = memCtrlNum;
    }

    /**
     * 会員管理番号枝番を返します。
     *
     * @return 会員管理番号枝番
     */
    public String getMemCtrlNumBrNum() {
        return memCtrlNumBrNum;
    }

    /**
     * 会員管理番号枝番を設定します。
     *
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     */
    public void setMemCtrlNumBrNum(String memCtrlNumBrNum) {
        this.memCtrlNumBrNum = memCtrlNumBrNum;
    }

    /**
     * 対象年月を返します。
     *
     * @return 対象年月
     */
    public String getObjectYearMonth() {
        return objectYearMonth;
    }

    /**
     * 対象年月を設定します。
     *
     * @param objectYearMonth
     *            対象年月
     */
    public void setObjectYearMonth(String objectYearMonth) {
        this.objectYearMonth = objectYearMonth;
    }

    /**
     * OP番号を返します。
     *
     * @return OP番号
     */
    public String getOpNum() {
        return opNum;
    }

    /**
     * OP番号を設定します。
     *
     * @param opNum
     *            OP番号
     */
    public void setOpNum(String opNum) {
        this.opNum = opNum;
    }

    /**
     * マイル種別コードを返します。
     *
     * @return マイル種別コード
     */
    public String getMileCategoryCode() {
        return mileCategoryCode;
    }

    /**
     * マイル種別コードを設定する
     *
     * @param mileCategoryCode
     *            マイル種別コード
     */
    public void setMileCategoryCode(String mileCategoryCode) {
        this.mileCategoryCode = mileCategoryCode;
    }

    /**
     * 乗車マイル数を返します。
     *
     * @return 乗車マイル数
     */
    public String getRideMileAmount() {
        return rideMileAmount;
    }

    /**
     * 乗車マイル数を設定する
     *
     * @param rideMileAmount
     *            乗車マイル数
     */
    public void setRideMileAmount(String rideMileAmount) {
        this.rideMileAmount = rideMileAmount;
    }

}
