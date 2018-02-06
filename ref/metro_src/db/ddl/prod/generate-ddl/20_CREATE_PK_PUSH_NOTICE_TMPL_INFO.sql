ALTER TABLE OPAL_APLDBA.PUSH_NOTICE_TMPL_INFO
  ADD CONSTRAINT PK_PUSH_NOTICE_TMPL_INFO PRIMARY KEY
  (
    TEMPLATE_ID
  )
  USING INDEX (
    CREATE UNIQUE INDEX OPAL_APLDBA.PK_PUSH_NOTICE_TMPL_INFO ON OPAL_APLDBA.PUSH_NOTICE_TMPL_INFO
    (
      TEMPLATE_ID
    )
    TABLESPACE TS_APP_APM_IND_U01
  )
/
