CREATE TABLE RIDE_UPPER_LIMIT_TIMES (
  RIDE_APPLY_YEAR_MONTH CHAR(6 CHAR) NOT NULL ,
  SERVICE_DIVISION CHAR(1 CHAR) NOT NULL ,
  UPPER_LIMIT_TIMES NUMBER(2) NOT NULL ,
  INSERT_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  INSERT_DATE_TIME TIMESTAMP NOT NULL ,
  UPDATE_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  UPDATE_DATE_TIME TIMESTAMP NOT NULL ,
  DELETED_FLG CHAR(1 CHAR) DEFAULT 0  NOT NULL ,
  DELETED_DATE CHAR(8 CHAR)
)
TABLESPACE TS_APP_APM_DAT_U01
/