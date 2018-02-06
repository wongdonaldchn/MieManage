CREATE TABLE APL_MEM_TEMP_INFO (
  APL_MEM_REGIST_RCPT_ID NUMBER(10) NOT NULL ,
  OSAKA_PITAPA_NUMBER CHAR(10 CHAR),
  APPLICATION_ID VARCHAR2(50 CHAR) NOT NULL ,
  DEVICE_ID VARCHAR2(7 CHAR) NOT NULL ,
  LOGIN_ID VARCHAR2(16 CHAR) NOT NULL ,
  "PASSWORD" VARCHAR2(64 CHAR) NOT NULL ,
  PASSWORD_SALT CHAR(20 CHAR) NOT NULL ,
  STRETCHING_TIMES NUMBER(1) NOT NULL ,
  BIRTHDATE CHAR(8 CHAR) NOT NULL ,
  SEX_CODE CHAR(1 CHAR) NOT NULL ,
  MAIL_ADDRESS VARCHAR2(200 CHAR) NOT NULL ,
  RECOMMEND_USE_ACCEPT_FLAG CHAR(1 CHAR) NOT NULL ,
  ENQUETE_1 CHAR(3 CHAR) NOT NULL ,
  ENQUETE_2 CHAR(3 CHAR) NOT NULL ,
  ENQUETE_3 CHAR(3 CHAR) NOT NULL ,
  ENQUETE_4 CHAR(3 CHAR) NOT NULL ,
  ENQUETE_5 CHAR(3 CHAR) NOT NULL ,
  ENQUETE_6 CHAR(3 CHAR) NOT NULL ,
  ENQUETE_7 CHAR(3 CHAR) NOT NULL ,
  ENQUETE_8 CHAR(3 CHAR) NOT NULL ,
  ENQUETE_9 CHAR(3 CHAR) NOT NULL ,
  ENQUETE_10 CHAR(3 CHAR) NOT NULL ,
  MAIN_USE_STATION_1 CHAR(3 CHAR),
  MAIN_USE_STATION_2 CHAR(3 CHAR),
  MAIN_USE_STATION_3 CHAR(3 CHAR),
  MAIN_USE_STATION_4 CHAR(3 CHAR),
  MAIN_USE_STATION_5 CHAR(3 CHAR),
  DAY_OFF_1 CHAR(1 CHAR),
  DAY_OFF_2 CHAR(1 CHAR),
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
