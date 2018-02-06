package jp.co.tis.opal.batch.ss138A;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * PiTaPa利用実績情報取込のバリデーションに使用するフォームクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A138A001HeaderForm {

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
     * 処理時刻
     */
    @Domain("time")
    @Required(message = "{M000000001}")
    private String dataTime;

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

    /**
     * 処理時刻を返します。
     *
     * @return 処理時刻
     */
    public String getDataTime() {
        return dataTime;
    }

    /**
     * 処理時刻を設定します。
     *
     * @param dataTime
     *            処理時刻
     */
    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

}