package jp.co.tis.opal.batch.ss911A;

import java.util.Map;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.NoInputDataBatchAction;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.results.TransactionAbnormalEnd;

/**
 * バッチ処理（常駐バッチ）死活制御バッチアクション。
 *
 * バッチ処理（常駐バッチ）の死活を制御するため、<br/>
 * バッチリクエストテーブルの死活制御フラグ（プロセス停止フラグ）を更新する。
 *
 * @author Wataru Ito
 * @since 1.0
 *
 */
public class B911A011Action extends NoInputDataBatchAction {

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B911A011";

    /** 停止フラグを設定するコマンドラインオプションのキー名 */
    private static final String STATUS_KEY = "status";

    /** リクエストパスを設定するコマンドラインオプションのキー名 */
    private static final String REQUEST_KEY = "targetRequestId";

    /** セッションスコープ内におけるコマンドラインオブジェクトのキー名 */
    private static final String SESSION_SCOPE_KEY = "command";

    /** 開局フラグのカラム名 */
    private static final String PROCESS_AVAIL_FLG = "SERVICE_AVAILABLE";

    /**
     * 初期処理。<br/>
     * コマンドラインオプションの妥当性を検証し、問題がなければセッションスコープにその値を設定する。
     *
     * @param command
     *            コマンドラインオプション
     * @param ctx
     *            実行コンテキスト
     */
    @Override
    protected void initialize(CommandLine command, ExecutionContext ctx) {

        String status = command.getParamMap().get(STATUS_KEY);
        String targetRequestId = command.getParamMap().get(REQUEST_KEY);

        // 停止フラグがコマンドラインオプションに設定されていない場合、例外をスローする。
        if (StringUtil.isNullOrEmpty(status)) {
            throw new TransactionAbnormalEnd(110, "AB911A0101");
        }

        // リクエストIDがコマンドラインオプションに設定されていない場合、例外をスローする。
        if (StringUtil.isNullOrEmpty(targetRequestId)) {
            throw new TransactionAbnormalEnd(110, "AB911A0101");
        }

        // 停止フラグに"0" "1"以外が指定された場合、例外をスローする。
        if (!status.equals("0") && !status.equals("1")) {
            throw new TransactionAbnormalEnd(111, "AB911A0102");
        }

        ctx.setSessionScopedVar(SESSION_SCOPE_KEY, command);
    }

    /**
     * 本処理。<br/>
     * バッチリクエストテーブルのプロセス停止フラグを更新する。
     *
     * @param ctx
     *            実行コンテキスト
     * @return 処理結果
     */
    @Override
    public Result handle(ExecutionContext ctx) {

        CommandLine command = ctx.getSessionScopedVar(SESSION_SCOPE_KEY);

        Map<String, String> sqlMap = command.getParamMap();

        // 開局フラグ（サービス提供可否状態）取得。
        ParameterizedSqlPStatement getActiveFlgStatement = getParameterizedSqlStatement("GET_SERVICE_AVAILABLE");
        SqlResultSet activeFlg = getActiveFlgStatement.retrieve(1, 1, sqlMap);

        // 取得件数が0件の場合、例外をスローする。
        if (activeFlg.size() == 0) {
            throw new TransactionAbnormalEnd(112, "AB911A0103", sqlMap.get(REQUEST_KEY));
        }

        // 開局フラグ（サービス提供可否状態）が0（閉局）ではない場合、例外をスローする。
        if (!activeFlg.get(0).getString(PROCESS_AVAIL_FLG).equals("0")) {
            throw new TransactionAbnormalEnd(113, "AB911A0104", sqlMap.get(REQUEST_KEY));
        }

        // DBに登録する値を設定する。
        // 最終更新者ID
        sqlMap.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        sqlMap.put("updateDateTime", SystemTimeUtil.getDateTimeString());

        // プロセス停止フラグ更新。
        ParameterizedSqlPStatement updateHaltFlgstatement = getParameterizedSqlStatement("UPDATE_HALT_FLG");
        updateHaltFlgstatement.executeUpdateByMap(sqlMap);

        return new Result.Success();
    }

}
