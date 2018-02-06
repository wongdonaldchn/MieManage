ALTER TABLE PITAPA_USE_RES_INFO
  ADD CONSTRAINT PK_PITAPA_USE_RES_INFO PRIMARY KEY
  (
    MEMBER_CONTROL_NUMBER,
    MEM_CTRL_NUM_BR_NUM,
    PITAPA_USE_YEAR_MONTH
  )
  USING INDEX (
    CREATE UNIQUE INDEX PK_PITAPA_USE_RES_INFO ON PITAPA_USE_RES_INFO
    (
      MEMBER_CONTROL_NUMBER,
      MEM_CTRL_NUM_BR_NUM,
      PITAPA_USE_YEAR_MONTH
    )
    TABLESPACE TS_APP_APM_IND_U01
  )
/
