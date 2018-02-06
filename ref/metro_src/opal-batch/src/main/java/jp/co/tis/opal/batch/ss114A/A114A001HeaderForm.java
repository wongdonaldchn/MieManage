package jp.co.tis.opal.batch.ss114A;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * OP会員情報のバリデーションに使用するフォームクラス。
 *
 * @author 趙
 * @since 1.0
 */
public class A114A001HeaderForm {

    /**
     * ファイルID
     */
    @Required(message = "{M000000001}")
    private String fileId;

    /**
     * 処理日付
     */
    @Domain("date")
    @Required(message = "{M000000001}")
    private String dataDate;

    /**
     * ファイルIDを返します。
     *
     * @return ファイルID
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * ファイルIDを設定します。
     *
     * @param fileId
     *            ファイルID
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    /**
     * 処理日付を返します。
     *
     * @return 処理日付
     */
    public String getDataDate() {
        return dataDate;
    }

    /**
     * 処理日付を設定します。
     *
     * @param dataDate
     *            処理日付
     */
    public void setDataDate(String dataDate) {
        this.dataDate = dataDate;
    }

}
