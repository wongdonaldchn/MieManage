package jp.co.tis.opal.batch.ss138A;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * PiTaPa利用実績情報取込のバリデーションに使用するフォームクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A138A001DataForm {

    /**
     * 会員管理番号
     */
    @Domain("memCtrlNum")
    @Required(message = "{M000000001}")
    private String memControlNum;

    /**
     * 会員管理番号枝番
     */
    @Domain("memrCtrlBrNum")
    @Required(message = "{M000000001}")
    private String memControlBrNum;

    /**
     * PiTaPaご利用年月
     */
    @Domain("yearMonth")
    @Required(message = "{M000000001}")
    private String pitapaUseYearMonth;

    /**
     * プランコード
     */
    @Domain("planCode")
    @Required(message = "{M000000001}")
    private String planCode;

    /**
     * 会員単位支払合計
     */
    @Domain("totalMoney")
    @Required(message = "{M000000001}")
    private String memberUnitPayTotal;

    /**
     * 会員単位支払合計の合計
     */
    @Domain("totalMoney")
    @Required(message = "{M000000001}")
    private String memberUnitPayTotalTotal;

    /**
     * 明細書発送手数料
     */
    @Domain("totalMoney")
    @Required(message = "{M000000001}")
    private String detailBookPostCharge;

    /**
     * ショップdeポイント割引
     */
    @Domain("totalMoney")
    @Required(message = "{M000000001}")
    private String shopDePointDiscount;

    /**
     * 口座単位支払合計
     */
    @Domain("totalMoney")
    @Required(message = "{M000000001}")
    private String accountUnitPayTotal;

    /**
     * 登録駅ご利用・適用金額
     */
    @Domain("totalMoney")
    @Required(message = "{M000000001}")
    private String registStaUseApplyMoney;

    /**
     * 登録駅ご利用・割引後金額
     */
    @Domain("totalMoney")
    @Required(message = "{M000000001}")
    private String registStaUseDisMoney;

    /**
     * 登録駅外ご利用・適用金額
     */
    @Domain("totalMoney")
    @Required(message = "{M000000001}")
    private String notRegistStaUseApplyMoney;

    /**
     * 登録駅外ご利用・割引後金額
     */
    @Domain("totalMoney")
    @Required(message = "{M000000001}")
    private String notRegistStaUseDisMoney;

    /**
     * 非登録型ご利用・適用金額
     */
    @Domain("totalMoney")
    @Required(message = "{M000000001}")
    private String notRegistUseApplyMoney;

    /**
     * 非登録型ご利用・割引後金額
     */
    @Domain("totalMoney")
    @Required(message = "{M000000001}")
    private String notRegistUseDisMoney;

    /**
     * その他鉄道バスご利用
     */
    @Domain("totalMoney")
    @Required(message = "{M000000001}")
    private String otherRailwayBusUse;

    /**
     * PiTaPaショッピング
     */
    @Domain("totalMoney")
    @Required(message = "{M000000001}")
    private String pitapaShopping;

    /**
     * 会員管理番号を返します。
     *
     * @return 会員管理番号
     */
    public String getMemControlNum() {
        return memControlNum;
    }

    /**
     * 会員管理番号を設定します。
     *
     * @param memControlNum
     *            会員管理番号
     */
    public void setMemControlNum(String memControlNum) {
        this.memControlNum = memControlNum;
    }

    /**
     * 会員管理番号枝番を返します。
     *
     * @return 会員管理番号枝番
     */
    public String getMemControlBrNum() {
        return memControlBrNum;
    }

    /**
     * 会員管理番号枝番を設定します。
     *
     * @param memControlBrNum
     *            会員管理番号枝番
     */
    public void setMemControlBrNum(String memControlBrNum) {
        this.memControlBrNum = memControlBrNum;
    }

    /**
     * PiTaPaご利用年月を返します。
     *
     * @return PiTaPaご利用年月
     */
    public String getPitapaUseYearMonth() {
        return pitapaUseYearMonth;
    }

    /**
     * PiTaPaご利用年月を設定します。
     *
     * @param pitapaUseYearMonth
     *            PiTaPaご利用年月
     */
    public void setPitapaUseYearMonth(String pitapaUseYearMonth) {
        this.pitapaUseYearMonth = pitapaUseYearMonth;
    }

    /**
     * プランコードを返します。
     *
     * @return プランコード
     */
    public String getPlanCode() {
        return planCode;
    }

    /**
     * プランコードを設定します。
     *
     * @param planCode
     *            プランコード
     */
    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    /**
     * 会員単位支払合計を返します。
     *
     * @return 会員単位支払合計
     */
    public String getMemberUnitPayTotal() {
        return memberUnitPayTotal;
    }

    /**
     * 会員単位支払合計を設定します
     *
     * @param memberUnitPayTotal
     *            会員単位支払合計
     */
    public void setMemberUnitPayTotal(String memberUnitPayTotal) {
        this.memberUnitPayTotal = memberUnitPayTotal;
    }

    /**
     * 会員単位支払合計の合計を返します。
     *
     * @return 会員単位支払合計の合計
     */
    public String getMemberUnitPayTotalTotal() {
        return memberUnitPayTotalTotal;
    }

    /**
     * 会員単位支払合計の合計を設定します
     *
     * @param memberUnitPayTotalTotal
     *            会員単位支払合計の合計
     */
    public void setMemberUnitPayTotalTotal(String memberUnitPayTotalTotal) {
        this.memberUnitPayTotalTotal = memberUnitPayTotalTotal;
    }

    /**
     * 明細書発送手数料を返します。
     *
     * @return 明細書発送手数料
     */
    public String getDetailBookPostCharge() {
        return detailBookPostCharge;
    }

    /**
     * 明細書発送手数料を設定します
     *
     * @param detailBookPostCharge
     *            明細書発送手数料
     */
    public void setDetailBookPostCharge(String detailBookPostCharge) {
        this.detailBookPostCharge = detailBookPostCharge;
    }

    /**
     * ショップdeポイント割引を返します。
     *
     * @return ショップdeポイント割引
     */
    public String getShopDePointDiscount() {
        return shopDePointDiscount;
    }

    /**
     * ショップdeポイント割引を設定します
     *
     * @param shopDePointDiscount
     *            ショップdeポイント割引
     */
    public void setShopDePointDiscount(String shopDePointDiscount) {
        this.shopDePointDiscount = shopDePointDiscount;
    }

    /**
     * 口座単位支払合計を返します。
     *
     * @return 口座単位支払合計
     */
    public String getAccountUnitPayTotal() {
        return accountUnitPayTotal;
    }

    /**
     * 口座単位支払合計を設定します
     *
     * @param accountUnitPayTotal
     *            口座単位支払合計
     */
    public void setAccountUnitPayTotal(String accountUnitPayTotal) {
        this.accountUnitPayTotal = accountUnitPayTotal;
    }

    /**
     * 登録駅ご利用・適用金額を返します。
     *
     * @return 登録駅ご利用・適用金額
     */
    public String getRegistStaUseApplyMoney() {
        return registStaUseApplyMoney;
    }

    /**
     * 登録駅ご利用・適用金額を設定します
     *
     * @param registStaUseApplyMoney
     *            登録駅ご利用・適用金額
     */
    public void setRegistStaUseApplyMoney(String registStaUseApplyMoney) {
        this.registStaUseApplyMoney = registStaUseApplyMoney;
    }

    /**
     * 登録駅ご利用・割引後金額を返します。
     *
     * @return 登録駅ご利用・割引後金額
     */
    public String getRegistStaUseDisMoney() {
        return registStaUseDisMoney;
    }

    /**
     * 登録駅ご利用・割引後金額を設定します
     *
     * @param registStaUseDisMoney
     *            登録駅ご利用・割引後金額
     */
    public void setRegistStaUseDisMoney(String registStaUseDisMoney) {
        this.registStaUseDisMoney = registStaUseDisMoney;
    }

    /**
     * 登録駅外ご利用・適用金額を返します。
     *
     * @return 登録駅外ご利用・適用金額
     */
    public String getNotRegistStaUseApplyMoney() {
        return notRegistStaUseApplyMoney;
    }

    /**
     * 登録駅外ご利用・適用金額を設定します
     *
     * @param notRegistStaUseApplyMoney
     *            登録駅外ご利用・適用金額
     */
    public void setNotRegistStaUseApplyMoney(String notRegistStaUseApplyMoney) {
        this.notRegistStaUseApplyMoney = notRegistStaUseApplyMoney;
    }

    /**
     * 登録駅外ご利用・割引後金額を返します。
     *
     * @return 登録駅外ご利用・割引後金額
     */
    public String getNotRegistStaUseDisMoney() {
        return notRegistStaUseDisMoney;
    }

    /**
     * 登録駅外ご利用・割引後金額を設定します
     *
     * @param notRegistStaUseDisMoney
     *            登録駅外ご利用・割引後金額
     */
    public void setNotRegistStaUseDisMoney(String notRegistStaUseDisMoney) {
        this.notRegistStaUseDisMoney = notRegistStaUseDisMoney;
    }

    /**
     * 非登録型ご利用・適用金額を返します。
     *
     * @return 非登録型ご利用・適用金額
     */
    public String getNotRegistUseApplyMoney() {
        return notRegistUseApplyMoney;
    }

    /**
     * 非登録型ご利用・適用金額を設定します
     *
     * @param notRegistUseApplyMoney
     *            非登録型ご利用・適用金額
     */
    public void setNotRegistUseApplyMoney(String notRegistUseApplyMoney) {
        this.notRegistUseApplyMoney = notRegistUseApplyMoney;
    }

    /**
     * 非登録型ご利用・割引後金額を返します。
     *
     * @return 非登録型ご利用・割引後金額
     */
    public String getNotRegistUseDisMoney() {
        return notRegistUseDisMoney;
    }

    /**
     * 非登録型ご利用・割引後金額を設定します
     *
     * @param notRegistUseDisMoney
     *            非登録型ご利用・割引後金額
     */
    public void setNotRegistUseDisMoney(String notRegistUseDisMoney) {
        this.notRegistUseDisMoney = notRegistUseDisMoney;
    }

    /**
     * その他鉄道バスご利用を返します。
     *
     * @return その他鉄道バスご利用
     */
    public String getOtherRailwayBusUse() {
        return otherRailwayBusUse;
    }

    /**
     * その他鉄道バスご利用を設定します
     *
     * @param otherRailwayBusUse
     *            その他鉄道バスご利用
     */
    public void setOtherRailwayBusUse(String otherRailwayBusUse) {
        this.otherRailwayBusUse = otherRailwayBusUse;
    }

    /**
     * PiTaPaショッピングを返します。
     *
     * @return PiTaPaショッピング
     */
    public String getPitapaShopping() {
        return pitapaShopping;
    }

    /**
     * PiTaPaショッピングを設定します
     *
     * @param pitapaShopping
     *            PiTaPaショッピング
     */
    public void setPitapaShopping(String pitapaShopping) {
        this.pitapaShopping = pitapaShopping;
    }

}
