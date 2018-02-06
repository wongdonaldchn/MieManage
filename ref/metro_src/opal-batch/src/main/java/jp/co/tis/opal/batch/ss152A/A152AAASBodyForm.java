package jp.co.tis.opal.batch.ss152A;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * プッシュ通知指示結果応答電文フォームクラス。
 *
 * @author 曹
 * @since 1.0
 */
public class A152AAASBodyForm {

    /**
     * ステータス
     */
    @Domain("status")
    @Required(message = "{M000000001}")
    private String status;

    /**
     * id
     */
    @Domain("id")
    private String id;

    /**
     * ステータスを返します。
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * ステータスを設定します。
     *
     * @param status
     *            ステータス
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * idを返します。
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * idを設定します。
     *
     * @param id
     *            id
     */
    public void setId(String id) {
        this.id = id;
    }

}
