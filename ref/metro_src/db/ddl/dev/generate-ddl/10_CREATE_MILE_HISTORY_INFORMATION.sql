CREATE TABLE MILE_HISTORY_INFORMATION (
  MILE_HISTORY_ID NUMBER(10) NOT NULL ,
  APPLICATION_MEMBER_ID NUMBER(10) NOT NULL ,
  MILE_ADD_SUB_RCPT_NUM VARCHAR2(20 CHAR),
  MILE_CATEGORY_CODE CHAR(3 CHAR) NOT NULL ,
  MILE_AMOUNT NUMBER(7) DEFAULT 0  NOT NULL ,
  MILE_HISTORY_REGIST_DATE CHAR(8 CHAR) NOT NULL ,
  INSERT_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  INSERT_DATE_TIME TIMESTAMP NOT NULL ,
  UPDATE_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  UPDATE_DATE_TIME TIMESTAMP NOT NULL ,
  DELETED_FLG CHAR(1 CHAR) DEFAULT 0  NOT NULL ,
  DELETED_DATE CHAR(8 CHAR)
)
TABLESPACE TS_APP_APM_DAT_U01
/