package jp.co.tis.opal.batch.ss153A;

import java.util.HashMap;
import java.util.Map;

import nablarch.core.dataformat.DataRecord;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.FileBatchAction;
import nablarch.fw.launcher.CommandLine;

import jp.co.tis.opal.common.constants.OpalCodeConstants;

/**
 * B153A012:エラーメールアドレス情報更新のアクションクラ。
 *
 * @author 陳
 * @since 1.0
 */
public class B153A012Action extends FileBatchAction {

    /** ファイル名 */
    private static final String FILE_ID = "A153A001";

    /** フォーマットファイル名(拡張子除く) */
    private static final String FORMAT_ID = "A153A001";

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B153A012";

    /** エラーメールアドレス情報ファイルの出力先ディレクトリ */
    private static final String OUTPUT_PATH = "mailIn";

    /** 入力データ件数(エラーメールアドレス情報データレコード) */
    private int inputDataCount;

    /** 出力データ件数(アプリ会員情報)(更新) */
    private int updateAplMemInfoCount;

    /** 一時的エラー発生回数上限 */
    private int mailTemporaryErrorMaxAvaileCount;

    /**
     * 入力データファイル名の取得。
     *
     * @return ファイル名
     */
    @Override
    public String getDataFileName() {
        return FILE_ID;
    }

    /**
     * フォーマットファイル名の取得。
     *
     * @return フォーマットファイル名（拡張子除く）
     */
    @Override
    public String getFormatFileName() {
        return FORMAT_ID;
    }

    /**
     * 入力ファイル配置先の論理名を返す。
     *
     * @return 入力ファイル配置先の論理名
     */
    @Override
    public String getDataFileDirName() {
        return OUTPUT_PATH;
    }

    /**
     * 初期化処理。
     *
     * @param command
     *            コマンドライン引数
     * @param context
     *            実行コンテキスト
     */
    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {

        // 出力データ件数(アプリ会員情報)(更新)
        this.updateAplMemInfoCount = 0;
        // 入力データ件数(エラーメールアドレス情報データレコード)
        this.inputDataCount = 0;
        // 一時的エラー発生回数上限
        this.mailTemporaryErrorMaxAvaileCount = Integer
                .parseInt(SystemRepository.getString("mail_temporary_error_max_availe_count"));
    }

    /**
     * ヘッダレコードの処理。
     *
     * @param inputData
     *            一行分のデータ
     * @param ctx
     *            実行コンテキスト
     * @return 結果オブジェクト
     */
    public Result doHeader(DataRecord inputData, ExecutionContext ctx) {
        return new Result.Success();
    }

    /**
     * データのバリデーションと登録を行う。
     * <p/>
     *
     * @param inputData
     *            一行分のデータ
     * @param ctx
     *            実行コンテキスト
     * @return 結果オブジェクト
     */
    public Result doData(DataRecord inputData, ExecutionContext ctx) {

        if (!StringUtil.isNullOrEmpty(inputData.getString("mail_address"))) {

            // 入力データ件数(エラーメールアドレス情報データレコード)をカウントアップする
            this.inputDataCount++;
            // 永続的エラー発生回数
            int permanentErrorCount = Integer.parseInt(inputData.getString("permanent_error"));
            // 原因不明エラー発生回数
            int unknownErrorCount = Integer.parseInt(inputData.getString("unknown_error"));
            // 一時的エラー回数
            int temporaryErrorCount = Integer.parseInt(inputData.getString("temporary_error"));
            // 配信停止要否判定を行う
            if (permanentErrorCount > 0 || unknownErrorCount > 0
                    || temporaryErrorCount > this.mailTemporaryErrorMaxAvaileCount) {

                // アプリ会員情報TBLを更新する
                updateAplMemInfo(inputData);
            }
        }
        return new Result.Success();
    }

    /**
     * 事後処理。
     * <p/>
     * 成功件数をロギングする。
     *
     * @param result
     *            結果オブジェクト
     * @param context
     *            実行コンテキスト
     */
    @Override
    protected void terminate(Result result, ExecutionContext context) {

        // 入力データ件数(エラーメールアドレス情報データレコード)
        writeLog("MB153A0102", Integer.valueOf(this.inputDataCount));
        // 出力データ件数(アプリ会員情報)(更新)
        writeLog("MB153A0103", Integer.valueOf(this.updateAplMemInfoCount));
    }

    /**
     * アプリ会員情報更新処理
     *
     * @param inputData
     *            入力データ
     */
    private void updateAplMemInfo(DataRecord inputData) {

        // アプリ会員情報更新用のSQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // メール配信状態区分
        condition.put("mailDeliverStatusDivision",
                OpalCodeConstants.MailDeliverStatusDivision.MAIL_DELIVER_STATUS_DIVISION_1);
        // 最終更新者ID
        condition.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // メールアドレス
        condition.put("mailAddress", inputData.getString("mail_address"));

        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_APL_MEM_INFO");
        // 出力データ件数(アプリ会員情報)(更新)をカウントアップする。
        this.updateAplMemInfoCount += statement.executeUpdateByMap(condition);
    }

}
