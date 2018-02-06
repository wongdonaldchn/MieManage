CREATE TABLE REQUEST (
  REQUEST_ID CHAR(10 CHAR) NOT NULL ,
  REQUEST_NAME VARCHAR2(100 CHAR) NOT NULL ,
  SERVICE_AVAILABLE CHAR(1 CHAR) NOT NULL ,
  INSERT_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  INSERT_DATE_TIME TIMESTAMP NOT NULL ,
  UPDATE_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  UPDATE_DATE_TIME TIMESTAMP NOT NULL ,
  DELETED_FLG CHAR(1 CHAR) DEFAULT 0  NOT NULL ,
  DELETED_DATE CHAR(8 CHAR),
  "VERSION" NUMBER(10) DEFAULT 0  NOT NULL 
)
TABLESPACE TS_APP_APM_DAT_U01
/