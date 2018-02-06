CREATE TABLE OPAL_APLDBA.MAIL_PACK_DELIVER_INFO (
  MAIL_PACK_DELIVER_ID NUMBER(10) NOT NULL ,
  OPAL_PROCESS_ID CHAR(8 CHAR) NOT NULL ,
  DELIVER_TYPE CHAR(1 CHAR) NOT NULL ,
  TEMPLATE_ID CHAR(10 CHAR) NOT NULL ,
  DELIVER_DATE TIMESTAMP,
  DELIVER_FILE_NAME CHAR(35 CHAR) NOT NULL ,
  MAIL_DELIVER_STATUS CHAR(1 CHAR) DEFAULT 1  NOT NULL ,
  DELIVER_SERVICE_MAIL_ID NUMBER(10),
  INSERT_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  INSERT_DATE_TIME TIMESTAMP NOT NULL ,
  UPDATE_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  UPDATE_DATE_TIME TIMESTAMP NOT NULL ,
  DELETED_FLG CHAR(1 CHAR) DEFAULT 0  NOT NULL ,
  DELETED_DATE CHAR(8 CHAR)
)
TABLESPACE TS_APP_APM_DAT_U01
/
GRANT SELECT ON OPAL_APLDBA.MAIL_PACK_DELIVER_INFO TO role_objsel
/
GRANT INSERT ON OPAL_APLDBA.MAIL_PACK_DELIVER_INFO TO role_objupd
/
GRANT UPDATE ON OPAL_APLDBA.MAIL_PACK_DELIVER_INFO TO role_objupd
/
GRANT DELETE ON OPAL_APLDBA.MAIL_PACK_DELIVER_INFO TO role_objupd
/
CREATE PUBLIC SYNONYM MAIL_PACK_DELIVER_INFO FOR OPAL_APLDBA.MAIL_PACK_DELIVER_INFO
/
