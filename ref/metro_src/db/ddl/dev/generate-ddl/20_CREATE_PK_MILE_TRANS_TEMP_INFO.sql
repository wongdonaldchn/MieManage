ALTER TABLE MILE_TRANS_TEMP_INFO
  ADD CONSTRAINT PK_MILE_TRANS_TEMP_INFO PRIMARY KEY
  (
    MEMBER_CONTROL_NUMBER,
    MEM_CTRL_NUM_BR_NUM
  )
  USING INDEX (
    CREATE UNIQUE INDEX PK_MILE_TRANS_TEMP_INFO ON MILE_TRANS_TEMP_INFO
    (
      MEMBER_CONTROL_NUMBER,
      MEM_CTRL_NUM_BR_NUM
    )
    TABLESPACE TS_APP_APM_IND_U01
  )
/