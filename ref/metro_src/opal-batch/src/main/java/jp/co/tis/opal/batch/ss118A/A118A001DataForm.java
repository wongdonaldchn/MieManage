package jp.co.tis.opal.batch.ss118A;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * 主なご利用駅情報のバリデーションに使用するフォームクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A118A001DataForm {

    /**
     * 会員管理番号
     */
    @Domain("memCtrlNum")
    @Required(message = "{M000000001}")
    private String memberControlNumber;

    /**
     * 会員管理番号枝番
     */
    @Domain("memrCtrlBrNum")
    @Required(message = "{M000000001}")
    private String memrCtrlBrNum;

    /**
     * 登録駅1
     */
    @Domain("stationId")
    private String registStation1;

    /**
     * 登録駅2
     */
    @Domain("stationId")
    private String registStation2;

    /**
     * 登録駅3
     */
    @Domain("stationId")
    private String registStation3;

    /**
     * 登録駅4
     */
    @Domain("stationId")
    private String registStation4;

    /**
     * 登録駅5
     */
    @Domain("stationId")
    private String registStation5;

    /**
     * 会員管理番号を返します。
     *
     * @return 会員管理番号
     */
    public String getMemberControlNumber() {
        return memberControlNumber;
    }

    /**
     * 会員管理番号を設定します。
     *
     * @param memberControlNumber
     *            会員管理番号
     */
    public void setMemberControlNumber(String memberControlNumber) {
        this.memberControlNumber = memberControlNumber;
    }

    /**
     * 会員管理番号枝番を返します。
     *
     * @return 会員管理番号枝番
     */
    public String getMemrCtrlBrNum() {
        return memrCtrlBrNum;
    }

    /**
     * 会員管理番号枝番を設定します。
     *
     * @param memrCtrlBrNum
     *            会員管理番号枝番
     */
    public void setMemrCtrlBrNum(String memrCtrlBrNum) {
        this.memrCtrlBrNum = memrCtrlBrNum;
    }

    /**
     * 登録駅1を返します。
     *
     * @return 登録駅1
     */
    public String getRegistStation1() {
        return registStation1;
    }

    /**
     * 登録駅1を設定します。
     *
     * @param registStation1
     *            登録駅1
     */
    public void setRegistStation1(String registStation1) {
        this.registStation1 = registStation1;
    }

    /**
     * 登録駅2を返します。
     *
     * @return 登録駅2
     */
    public String getRegistStation2() {
        return registStation2;
    }

    /**
     * 登録駅2を設定します。
     *
     * @param registStation2
     *            登録駅2
     */
    public void setRegistStation2(String registStation2) {
        this.registStation2 = registStation2;
    }

    /**
     * 登録駅3を返します。
     *
     * @return 登録駅3
     */
    public String getRegistStation3() {
        return registStation3;
    }

    /**
     * 登録駅3を設定します。
     *
     * @param registStation3
     *            登録駅3
     */
    public void setRegistStation3(String registStation3) {
        this.registStation3 = registStation3;
    }

    /**
     * 登録駅4を返します。
     *
     * @return 登録駅4
     */
    public String getRegistStation4() {
        return registStation4;
    }

    /**
     * 登録駅4を設定します。
     *
     * @param registStation4
     *            登録駅4
     */
    public void setRegistStation4(String registStation4) {
        this.registStation4 = registStation4;
    }

    /**
     * 登録駅5を返します。
     *
     * @return 登録駅5
     */
    public String getRegistStation5() {
        return registStation5;
    }

    /**
     * 登録駅5を設定します。
     *
     * @param registStation5
     *            登録駅5
     */
    public void setRegistStation5(String registStation5) {
        this.registStation5 = registStation5;
    }
}
