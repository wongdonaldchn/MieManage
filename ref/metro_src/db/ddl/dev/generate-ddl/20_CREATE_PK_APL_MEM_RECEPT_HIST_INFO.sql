ALTER TABLE APL_MEM_RECEPT_HIST_INFO
  ADD CONSTRAINT PK_APL_MEM_RECEPT_HIST_INFO PRIMARY KEY
  (
    APPLICATION_MEMBER_ID
  )
  USING INDEX (
    CREATE UNIQUE INDEX PK_APL_MEM_RECEPT_HIST_INFO ON APL_MEM_RECEPT_HIST_INFO
    (
      APPLICATION_MEMBER_ID
    )
    TABLESPACE TS_APP_APM_IND_U01
  )
/