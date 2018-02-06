package com.donald.miem.common.idgenerator;

import java.util.Map;

import nablarch.common.idgenerator.IdFormatter;
import nablarch.common.idgenerator.IdGenerator;
import nablarch.core.db.connection.DbConnectionContext;
import nablarch.core.db.connection.TransactionManagerConnection;
import nablarch.core.db.statement.ResultSetIterator;
import nablarch.core.db.statement.SqlPStatement;

/**
 * 採番処理機能サポート
 *
 * @author 張
 * @since 1.0
 *
 */
public class SequenceIdGeneratorSupport implements IdGenerator {
    private String dbTransactionName;

    private Map<String, String> idTable;

    /**
     * idTableを返します。
     *
     * @param idTable
     *            デバイスID
     */
    public void setIdTable(Map<String, String> idTable) {
        for (Map.Entry<String, String> entry : idTable.entrySet()) {
            String name = (String) entry.getValue();
            if (name.indexOf(" ") != -1) {
                throw new IllegalArgumentException(String.format("invalid sequence name. id = %s, sequence name = %s",
                        new Object[] { entry.getKey(), name }));
            }
        }

        this.idTable = idTable;
    }

    /**
     * idTableを返します。
     */
    public SequenceIdGeneratorSupport() {
        this.dbTransactionName = "transaction";
    }

    /**
     * 引数で指定された採番対象ID内でユニークなIDを採番する。
     *
     * @param sequenceId
     *            採番対象を識別するID
     * @return 採番対象ID内でユニークな採番結果のID
     */
    public String generateId(String sequenceId) {
        return generateId(sequenceId, null);
    }

    /**
     * 引数で指定された採番対象ID内でユニークなIDを採番し、指定されたIdFormatterでフォーマットし返却する。
     *
     * @param sequenceId
     *            採番対象を識別するID
     * @param formatter
     *            採番したIDをフォーマットするIdFormatter
     * @return 採番対象ID内でユニークな採番結果のID
     */
    public String generateId(String sequenceId, IdFormatter formatter) {
        String sequenceName = this.idTable.get(sequenceId);

        TransactionManagerConnection connection = DbConnectionContext
                .getTransactionManagerConnection(this.dbTransactionName);

        SqlPStatement statement = connection
                .prepareStatement(connection.getDialect().buildSequenceGeneratorSql(sequenceName));

        ResultSetIterator rs = statement.executeQuery();
        if (!(rs.next())) {
            throw new SequenceGeneratorFailedException(sequenceName);
        }
        try {
            String id = String.valueOf(rs.getLong(1));
            if (formatter == null) {
                return id;
            }
            String str1 = formatter.format(sequenceName, id);

            return str1;
        } finally {
            rs.close();
        }
    }

    /**
     * トランザクション名を設定する。
     *
     * @param dbTransactionName
     *            トランザクション名
     */
    public void setDbTransactionName(String dbTransactionName) {
        this.dbTransactionName = dbTransactionName;
    }

    /**
     * 採番処理機能サポート
     *
     * @author 張
     * @since 1.0
     *
     */
    public static class SequenceGeneratorFailedException extends RuntimeException {
        static final long serialVersionUID = 1L;

        /**
         * idTableを返します。
         *
         * @param sequenceName
         *            採番対象名
         */
        public SequenceGeneratorFailedException(String sequenceName) {
            super("failed to get next value from sequence. sequence name=[" + sequenceName + ']');
        }
    }
}