--make directory for dev
@./execsql/directory/DIR_DATA_PUMP.sql &1

--make tablespace
@./execsql/tablespace/TS_APP_APM_DAT_U01.sql
@./execsql/tablespace/TS_APP_APM_IND_U01.sql

--make profile
@./execsql/profile/prof_apl.sql
@./execsql/profile/prof_usr.sql

--make user
@./execsql/user/opal_apldba.sql opal_apldba
@./execsql/user/opal_apltrn.sql opal_apltrn
@./execsql/user/opal_aplmvw.sql opal_aplmvw

--make user for dev
@./execsql/user/test_master.sql test_master

--make role
@./execsql/role/role_apl.sql
@./execsql/role/role_dat.sql
@./execsql/role/role_mvw.sql
@./execsql/role/role_objsel.sql
@./execsql/role/role_objupd.sql

--grant role
@./execsql/grant_role/gra_opal_apldba.sql
@./execsql/grant_role/gra_opal_apltrn.sql
@./execsql/grant_role/gra_opal_aplmvw.sql

--grant user
@./execsql/grant_user/gra_opal_apltrn.sql
@./execsql/grant_user/gra_opal_aplmvw.sql

