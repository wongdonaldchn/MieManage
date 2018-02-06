CREATE TABLE OPAL_APLDBA.CODE_PATTERN (
  "ID" CHAR(8 CHAR) NOT NULL ,
  "VALUE" VARCHAR2(5 CHAR) NOT NULL ,
  PATTERN01 CHAR(1 CHAR) NOT NULL ,
  PATTERN02 CHAR(1 CHAR),
  PATTERN03 CHAR(1 CHAR),
  PATTERN04 CHAR(1 CHAR),
  PATTERN05 CHAR(1 CHAR),
  INSERT_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  INSERT_DATE_TIME TIMESTAMP NOT NULL ,
  UPDATE_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  UPDATE_DATE_TIME TIMESTAMP NOT NULL ,
  DELETED_FLG CHAR(1 CHAR) DEFAULT 0  NOT NULL ,
  DELETED_DATE CHAR(8 CHAR)
)
TABLESPACE TS_APP_APM_DAT_U01
/
GRANT SELECT ON OPAL_APLDBA.CODE_PATTERN TO role_objsel
/
GRANT INSERT ON OPAL_APLDBA.CODE_PATTERN TO role_objupd
/
GRANT UPDATE ON OPAL_APLDBA.CODE_PATTERN TO role_objupd
/
GRANT DELETE ON OPAL_APLDBA.CODE_PATTERN TO role_objupd
/
CREATE PUBLIC SYNONYM CODE_PATTERN FOR OPAL_APLDBA.CODE_PATTERN
/