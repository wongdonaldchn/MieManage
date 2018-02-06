ALTER TABLE OPAL_APLDBA.MAIL_DELIVER_TMPL_INFO
  ADD CONSTRAINT PK_MAIL_DELIVER_TMPL_INFO PRIMARY KEY
  (
    TEMPLATE_ID
  )
  USING INDEX (
    CREATE UNIQUE INDEX OPAL_APLDBA.PK_MAIL_DELIVER_TMPL_INFO ON OPAL_APLDBA.MAIL_DELIVER_TMPL_INFO
    (
      TEMPLATE_ID
    )
    TABLESPACE TS_APP_APM_IND_U01
  )
/
