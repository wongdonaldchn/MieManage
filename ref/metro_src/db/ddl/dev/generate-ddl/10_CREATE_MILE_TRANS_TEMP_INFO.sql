CREATE TABLE MILE_TRANS_TEMP_INFO (
  MEMBER_CONTROL_NUMBER CHAR(10 CHAR) NOT NULL ,
  MEM_CTRL_NUM_BR_NUM CHAR(3 CHAR) NOT NULL ,
  OSAKA_PITAPA_NUMBER CHAR(10 CHAR) NOT NULL ,
  TRANSITION_MILE_AMOUNT NUMBER(7) DEFAULT 0  NOT NULL ,
  MILE_TRANSITION_DIVISION CHAR(1 CHAR) DEFAULT 1  NOT NULL ,
  INSERT_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  INSERT_DATE_TIME TIMESTAMP NOT NULL ,
  UPDATE_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  UPDATE_DATE_TIME TIMESTAMP NOT NULL ,
  DELETED_FLG CHAR(1 CHAR) DEFAULT 0  NOT NULL ,
  DELETED_DATE CHAR(8 CHAR)
)
TABLESPACE TS_APP_APM_DAT_U01
/
