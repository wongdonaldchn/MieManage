package jp.co.tis.opal.batch.ss912A;

import java.sql.Types;

import nablarch.core.db.connection.AppDbConnection;
import nablarch.core.db.connection.DbConnectionContext;
import nablarch.core.db.statement.SqlCStatement;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.NoInputDataBatchAction;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.results.TransactionAbnormalEnd;

/**
 * データクリーニングバッチアクション。
 *
 * テーブル毎に定義された削除条件に基づき、<br/>
 * 保存期日を過ぎたレコードを物理削除する。
 *
 * @author Wataru Ito
 * @since 1.0
 *
 */
public class B912A011Action extends NoInputDataBatchAction {

    /** クリーニング対象テーブル名を設定するコマンドラインオプションのキー名 */
    private static final String TARGET_TABLE = "targetTable";

    /** 削除条件コードを設定するコマンドラインオプションのキー名 */
    private static final String DELETE_CODE = "deleteCode";

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

        String targetTable = command.getParamMap().get(TARGET_TABLE);
        String deleteCode = command.getParamMap().get(DELETE_CODE);

        // クリーニング対象テーブル名がコマンドラインオプションに設定されていない場合、例外をスローする。
        if (StringUtil.isNullOrEmpty(targetTable)) {
            throw new TransactionAbnormalEnd(110, "AB912A0101");
        }

        // 削除条件コードがコマンドラインオプションに設定されていない場合、例外をスローする。
        if (StringUtil.isNullOrEmpty(deleteCode)) {
            throw new TransactionAbnormalEnd(110, "AB912A0101");
        }

        // 削除条件コードが想定外の値の場合、例外をスローする。
        if (!deleteCode.equals("B1") && !deleteCode.equals("B2") && !deleteCode.equals("B3")) {
            throw new TransactionAbnormalEnd(111, "AB912A0102");
        }

        ctx.setSessionScopedVar(SESSION_SCOPE_KEY, command);
    }

    /**
     * 本処理。<br/>
     * 保存期日を過ぎたレコードを物理削除する。
     *
     * @param ctx
     *            実行コンテキスト
     * @return 処理結果
     */
    @Override
    public Result handle(ExecutionContext ctx) {

        CommandLine command = ctx.getSessionScopedVar(SESSION_SCOPE_KEY);

        // パラメータを取得する。
        // コミット件数
        String commitCount = SystemRepository.getString("ss912A.B911A021Action.commitCount");
        // 1テーブルあたりの最大削除件数
        String maxDeleteCount = SystemRepository.getString("ss912A.B911A021Action.maxDeleteCount");

        // DbConnectionContextからデータベース接続を取得する。
        AppDbConnection connection = DbConnectionContext.getConnection();

        // SQLIDを元にストアドプロシージャ実行用のステートメントを生成する。
        SqlCStatement statement = connection
                .prepareCallBySqlId("jp.co.tis.opal.batch.ss912A.B911A021Action#DATA_CLEAN");

        // IN及びOUTパラメータを設定する。
        // テーブル名
        statement.setString(1, command.getParamMap().get(TARGET_TABLE));
        // 削除条件コード
        statement.setString(2, command.getParamMap().get(DELETE_CODE));
        // コミット件数
        statement.setInt(3, Integer.parseInt(commitCount));
        // 1テーブルあたりの最大削除件数
        statement.setInt(4, Integer.parseInt(maxDeleteCount));
        // 削除件数
        statement.registerOutParameter(5, Types.VARCHAR);

        // ストアドプロシージャを実行する。
        statement.execute();

        // OUTパラメータを取得する。
        // 削除件数
        int deleteCount = statement.getInteger(5);

        // 処理結果をログ出力する。
        writeLog("MB912A0111", Integer.toString(deleteCount));

        return new Result.Success();
    }

}
