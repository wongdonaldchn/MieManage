set trims on
set feed off
set head off

-- table�폜SQL�쐬
spool ./execSql/drop_table.sql
select 'drop table ' || owner || '.' || table_name || ' cascade constraints purge;' from all_tables where owner in ('OPAL','OPAL_APLDBA','OPAL_APLTRN') and table_name not like '%$%';
spool off

-- procedure�폜SQL
spool ./execSql/drop_procedure.sql
select 'drop procedure ' || owner || '.' || object_name || ';' from all_procedures where owner in ('OPAL','OPAL_APLDBA','OPAL_APLTRN');
spool off

-- sequence�폜SQL�쐬
spool ./execSql/drop_sequence.sql
select 'drop sequence ' || sequence_owner || '.' || sequence_name || ';' from all_sequences where sequence_owner in ('OPAL','OPAL_APLDBA','OPAL_APLTRN');
spool off

-- synonym�폜SQL�쐬
spool ./execSql/drop_synonym.sql
select 'drop public synonym ' || synonym_name || ';' from all_synonyms where table_owner in ('OPAL','OPAL_APLDBA','OPAL_APLTRN');
spool off

-- �폜
set feed on
set head on
@./execSql/drop_procedure.sql
@./execSql/drop_sequence.sql
@./execSql/drop_table.sql
@./execSql/drop_synonym.sql

