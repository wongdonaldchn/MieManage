CREATE TABLE OPAL_APLDBA.PUSH_NOTICE_TMPL_INFO (
  TEMPLATE_ID CHAR(10 CHAR) NOT NULL ,
  FREE_WORD VARCHAR2(135 CHAR) NOT NULL ,
  SUBJECT VARCHAR2(80 CHAR) NOT NULL ,
  "BODY" VARCHAR2(4000 CHAR) NOT NULL ,
  INSERT_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  INSERT_DATE_TIME TIMESTAMP NOT NULL ,
  UPDATE_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  UPDATE_DATE_TIME TIMESTAMP NOT NULL ,
  DELETED_FLG CHAR(1 CHAR) DEFAULT 0  NOT NULL ,
  DELETED_DATE CHAR(8 CHAR)
)
TABLESPACE TS_APP_APM_DAT_U01
/
GRANT SELECT ON OPAL_APLDBA.PUSH_NOTICE_TMPL_INFO TO role_objsel
/
GRANT INSERT ON OPAL_APLDBA.PUSH_NOTICE_TMPL_INFO TO role_objupd
/
GRANT UPDATE ON OPAL_APLDBA.PUSH_NOTICE_TMPL_INFO TO role_objupd
/
GRANT DELETE ON OPAL_APLDBA.PUSH_NOTICE_TMPL_INFO TO role_objupd
/
CREATE PUBLIC SYNONYM PUSH_NOTICE_TMPL_INFO FOR OPAL_APLDBA.PUSH_NOTICE_TMPL_INFO
/