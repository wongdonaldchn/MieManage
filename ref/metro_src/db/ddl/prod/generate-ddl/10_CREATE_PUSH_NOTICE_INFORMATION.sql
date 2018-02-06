CREATE TABLE OPAL_APLDBA.PUSH_NOTICE_INFORMATION (
  PUSH_NOTICE_ID NUMBER(10) NOT NULL ,
  OPAL_PROCESS_ID CHAR(8 CHAR) NOT NULL ,
  DELIVER_DIVISION CHAR(1 CHAR) NOT NULL ,
  DELIVER_TYPE CHAR(1 CHAR) NOT NULL ,
  TEMPLATE_ID CHAR(10 CHAR) NOT NULL ,
  DELIVER_DATE_TIME CHAR(19 CHAR),
  PUSH_NOTICE_DISTINGUISH_ID NUMBER(10),
  PROCESSED_FLAG CHAR(1 CHAR) DEFAULT 0  NOT NULL ,
  INSERT_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  INSERT_DATE_TIME TIMESTAMP NOT NULL ,
  UPDATE_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  UPDATE_DATE_TIME TIMESTAMP NOT NULL ,
  DELETED_FLG CHAR(1 CHAR) DEFAULT 0  NOT NULL ,
  DELETED_DATE CHAR(8 CHAR)
)
TABLESPACE TS_APP_APM_DAT_U01
/
GRANT SELECT ON OPAL_APLDBA.PUSH_NOTICE_INFORMATION TO role_objsel
/
GRANT INSERT ON OPAL_APLDBA.PUSH_NOTICE_INFORMATION TO role_objupd
/
GRANT UPDATE ON OPAL_APLDBA.PUSH_NOTICE_INFORMATION TO role_objupd
/
GRANT DELETE ON OPAL_APLDBA.PUSH_NOTICE_INFORMATION TO role_objupd
/
CREATE PUBLIC SYNONYM PUSH_NOTICE_INFORMATION FOR OPAL_APLDBA.PUSH_NOTICE_INFORMATION
/
