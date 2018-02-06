package jp.co.tis.opal.batch.ss911A;

import java.util.Map;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.NoInputDataBatchAction;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.results.TransactionAbnormalEnd;

/**
 * バッチ処理（常駐バッチ）開閉局制御バッチアクション。
 *
 * バッチ処理（常駐バッチ）の開局・閉局を制御するため、<br/>
 * バッチリクエストテーブルの開局フラグ（サービス提供可否状態）を更新する。
 *
 * @author Wataru Ito
 * @since 1.0
 *
 */
public class B911A021Action extends NoInputDataBatchAction {

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B911A021";

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
        String targetRequestId = command.getParamMap().get(REQUEST_KEY);

        // 開局フラグがコマンドラインオプションに設定されていない場合、例外をスローする。
        if (StringUtil.isNullOrEmpty(status)) {
            throw new TransactionAbnormalEnd(110, "AB911A0201");
        }

        // リクエストIDがコマンドラインオプションに設定されていない場合、例外をスローする。
        if (StringUtil.isNullOrEmpty(targetRequestId)) {
            throw new TransactionAbnormalEnd(110, "AB911A0201");
        }

        // 開局フラグに"0" "1"以外が指定された場合、例外をスローする。
        if (!status.equals("0") && !status.equals("1")) {
            throw new TransactionAbnormalEnd(111, "AB911A0202");
        }

        ctx.setSessionScopedVar(SESSION_SCOPE_KEY, command);
    }

    /**
     * 本処理。<br/>
     * バッチリクエストテーブルの開局フラグ（サービス提供可否状態）を更新する。
     *
     * @param ctx
     *            実行コンテキスト
     * @return 処理結果
     */
    @Override
    public Result handle(ExecutionContext ctx) {

        CommandLine command = ctx.getSessionScopedVar(SESSION_SCOPE_KEY);

        Map<String, String> sqlMap = command.getParamMap();

        // DBに登録する値を設定する。
        // 最終更新者ID
        sqlMap.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        sqlMap.put("updateDateTime", SystemTimeUtil.getDateTimeString());

        // 開局フラグ（サービス提供可否状態）更新。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_SERVICE_AVAILABLE");
        int updateCount = statement.executeUpdateByMap(sqlMap);

        // 更新件数が0件の場合、例外をスローする。
        if (updateCount == 0) {
            if (sqlMap.get(STATUS_KEY).equals("0")) {
                throw new TransactionAbnormalEnd(112, "AB911A0203", sqlMap.get(REQUEST_KEY));
            } else {
                throw new TransactionAbnormalEnd(113, "AB911A0204", sqlMap.get(REQUEST_KEY));
            }
        }

        return new Result.Success();
    }

}
