CREATE TABLE OPAL_APLDBA.MILE_SUMMARY_INFORMATION (
  APPLICATION_MEMBER_ID NUMBER(10) NOT NULL ,
  MILE_SUM_YEAR_MONTH CHAR(6 CHAR) NOT NULL ,
  ACQUIRE_MILE_TOTAL NUMBER(7) NOT NULL ,
  USE_MILE_TOTAL NUMBER(7) NOT NULL ,
  LAST_MONTH_END_MILE_BALANCE NUMBER(7) NOT NULL ,
  THIS_MONTH_END_MILE_BALANCE NUMBER(7) NOT NULL ,
  INSERT_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  INSERT_DATE_TIME TIMESTAMP NOT NULL ,
  UPDATE_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  UPDATE_DATE_TIME TIMESTAMP NOT NULL ,
  DELETED_FLG CHAR(1 CHAR) DEFAULT 0  NOT NULL ,
  DELETED_DATE CHAR(8 CHAR)
)
TABLESPACE TS_APP_APM_DAT_U01
/
GRANT SELECT ON OPAL_APLDBA.MILE_SUMMARY_INFORMATION TO role_objsel
/
GRANT INSERT ON OPAL_APLDBA.MILE_SUMMARY_INFORMATION TO role_objupd
/
GRANT UPDATE ON OPAL_APLDBA.MILE_SUMMARY_INFORMATION TO role_objupd
/
GRANT DELETE ON OPAL_APLDBA.MILE_SUMMARY_INFORMATION TO role_objupd
/
CREATE PUBLIC SYNONYM MILE_SUMMARY_INFORMATION FOR OPAL_APLDBA.MILE_SUMMARY_INFORMATION
/