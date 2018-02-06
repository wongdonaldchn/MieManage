CREATE TABLE LOGIN_HISTORY_INFORMATION (
  LOGIN_HISTORY_ID NUMBER(10) NOT NULL ,
  APPLICATION_MEMBER_ID NUMBER(10) NOT NULL ,
  LOGIN_ID VARCHAR2(16 CHAR) NOT NULL ,
  LOGIN_DATE_TIME TIMESTAMP NOT NULL ,
  APPLICATION_ID VARCHAR2(50 CHAR) NOT NULL ,
  DEVICE_ID VARCHAR2(7 CHAR),
  INSERT_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  INSERT_DATE_TIME TIMESTAMP NOT NULL ,
  UPDATE_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  UPDATE_DATE_TIME TIMESTAMP NOT NULL ,
  DELETED_FLG CHAR(1 CHAR) DEFAULT 0  NOT NULL ,
  DELETED_DATE CHAR(8 CHAR)
)
TABLESPACE TS_APP_APM_DAT_U01
/
