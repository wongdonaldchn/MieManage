package jp.co.tis.opal.batch.ss911A;

import java.util.HashMap;
import java.util.Map;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.connection.AppDbConnection;
import nablarch.core.db.connection.DbConnectionContext;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.NoInputDataBatchAction;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.results.TransactionAbnormalEnd;

/**
 * Webサービス開閉局制御バッチアクション。
 *
 * Webサービスの開局・閉局を制御するため、<br/>
 * リクエストテーブルの開局フラグ（サービス提供可否状態）を更新する。
 *
 * @author Wataru Ito
 * @since 1.0
 *
 */
public class B911A031Action extends NoInputDataBatchAction {

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B911A031";

    /** 開局フラグを設定するコマンドラインオプションのキー名 */
    private static final String STATUS_KEY = "status";

    /** リクエストパスを設定するコマンドラインオプションのキー名 */
    private static final String REQUEST_KEY = "targetRequestId";

    /** セッションスコープ内におけるコマンドラインオブジェクトのキー名 */
    private static final String SESSION_SCOPE_KEY = "command";

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

        // 開局フラグがコマンドラインオプションに設定されていない場合、例外をスローする。
        if (StringUtil.isNullOrEmpty(status)) {
            throw new TransactionAbnormalEnd(110, "AB911A0301");
        }

        // 開局フラグに"0" "1"以外が指定された場合、例外をスローする。
        if (!status.equals("0") && !status.equals("1")) {
            throw new TransactionAbnormalEnd(111, "AB911A0302");
        }

        ctx.setSessionScopedVar(SESSION_SCOPE_KEY, command);
    }

    /**
     * 本処理。<br/>
     * リクエストテーブルの開局フラグ（サービス提供可否状態）を更新する。
     *
     * @param ctx
     *            実行コンテキスト
     * @return 処理結果
     */
    @Override
    public Result handle(ExecutionContext ctx) {

        CommandLine command = ctx.getSessionScopedVar(SESSION_SCOPE_KEY);
        Map<String, String> commandMap = command.getParamMap();

        // 処理対応リクエストIDを取得
        String[] targetRequestIdList = null;
        String targetRequestId = commandMap.get(REQUEST_KEY);
        if (!StringUtil.isNullOrEmpty(targetRequestId)) {
            targetRequestIdList = commandMap.get(REQUEST_KEY).split(",", 0);
        }

        // DBに登録する値を設定
        Map<String, Object> sqlMap = new HashMap<String, Object>();
        // status
        sqlMap.put(STATUS_KEY, commandMap.get(STATUS_KEY));
        // リクエストID
        sqlMap.put(REQUEST_KEY, targetRequestIdList);
        // 最終更新者ID
        sqlMap.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        sqlMap.put("updateDateTime", SystemTimeUtil.getDateTimeString());

        // 開局フラグ（サービス提供可否状態）更新。
        AppDbConnection connection = DbConnectionContext.getConnection();
        ParameterizedSqlPStatement statement = connection.prepareParameterizedSqlStatementBySqlId(
                this.getClass().getName() + "#UPDATE_SERVICE_AVAILABLE", sqlMap);
        int updateCount = statement.executeUpdateByMap(sqlMap);

        // 更新件数が0件の場合、例外をスローする。
        if (updateCount == 0 || (targetRequestIdList != null && targetRequestIdList.length != updateCount)) {
            if (sqlMap.get(STATUS_KEY).equals("0")) {
                throw new TransactionAbnormalEnd(112, "AB911A0303");
            } else {
                throw new TransactionAbnormalEnd(113, "AB911A0304");
            }
        }

        return new Result.Success();
    }

}
