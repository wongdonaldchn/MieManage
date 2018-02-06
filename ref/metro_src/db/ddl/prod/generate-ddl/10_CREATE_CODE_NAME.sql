CREATE TABLE OPAL_APLDBA.CODE_NAME (
  "ID" CHAR(8 CHAR) NOT NULL ,
  "VALUE" VARCHAR2(5 CHAR) NOT NULL ,
  LANG CHAR(2 CHAR) NOT NULL ,
  SORT_ORDER NUMBER(3) NOT NULL ,
  "NAME" VARCHAR2(50 CHAR) NOT NULL ,
  SHORT_NAME VARCHAR2(50 CHAR),
  OPTION01 VARCHAR2(50 CHAR),
  OPTION02 VARCHAR2(50 CHAR),
  OPTION03 VARCHAR2(50 CHAR),
  INSERT_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  INSERT_DATE_TIME TIMESTAMP NOT NULL ,
  UPDATE_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  UPDATE_DATE_TIME TIMESTAMP NOT NULL ,
  DELETED_FLG CHAR(1 CHAR) DEFAULT 0  NOT NULL ,
  DELETED_DATE CHAR(8 CHAR)
)
TABLESPACE TS_APP_APM_DAT_U01
/
GRANT SELECT ON OPAL_APLDBA.CODE_NAME TO role_objsel
/
GRANT INSERT ON OPAL_APLDBA.CODE_NAME TO role_objupd
/
GRANT UPDATE ON OPAL_APLDBA.CODE_NAME TO role_objupd
/
GRANT DELETE ON OPAL_APLDBA.CODE_NAME TO role_objupd
/
CREATE PUBLIC SYNONYM CODE_NAME FOR OPAL_APLDBA.CODE_NAME
/