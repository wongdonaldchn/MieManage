CREATE TABLE PITAPA_USE_RES_TEMP_INFO (
  MEMBER_CONTROL_NUMBER CHAR(10 CHAR) NOT NULL ,
  MEM_CTRL_NUM_BR_NUM CHAR(3 CHAR) NOT NULL ,
  PITAPA_USE_YEAR_MONTH CHAR(6 CHAR) NOT NULL ,
  PLAN_CODE CHAR(6 CHAR) NOT NULL ,
  MEMBER_UNIT_PAY_TOTAL NUMBER(11) NOT NULL ,
  MEMBER_UNIT_PAY_TOTAL_TOTAL NUMBER(11) NOT NULL ,
  DETAIL_BOOK_POST_CHARGE NUMBER(11) NOT NULL ,
  SHOP_DE_POINT_DISCOUNT NUMBER(11) NOT NULL ,
  ACCOUNT_UNIT_PAY_TOTAL NUMBER(11) NOT NULL ,
  REGIST_STA_USE_APPLY_MONEY NUMBER(11) NOT NULL ,
  REGIST_STA_USE_DIS_MONEY NUMBER(11) NOT NULL ,
  NOT_REGIST_STA_USE_APPLY_MONEY NUMBER(11) NOT NULL ,
  NOT_REGIST_STA_USE_DIS_MONEY NUMBER(11) NOT NULL ,
  NOT_REGIST_USE_APPLY_MONEY NUMBER(11) NOT NULL ,
  NOT_REGIST_USE_DIS_MONEY NUMBER(11) NOT NULL ,
  OTHER_RAILWAY_BUS_USE NUMBER(11) NOT NULL ,
  PITAPA_SHOPPING NUMBER(11) NOT NULL ,
  PROCESSED_FLAG CHAR(1 CHAR) DEFAULT 0 ,
  INSERT_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  INSERT_DATE_TIME TIMESTAMP NOT NULL ,
  UPDATE_USER_ID VARCHAR2(20 CHAR) NOT NULL ,
  UPDATE_DATE_TIME TIMESTAMP NOT NULL ,
  DELETED_FLG CHAR(1 CHAR) DEFAULT 0  NOT NULL ,
  DELETED_DATE CHAR(8 CHAR)
)
TABLESPACE TS_APP_APM_DAT_U01
/
