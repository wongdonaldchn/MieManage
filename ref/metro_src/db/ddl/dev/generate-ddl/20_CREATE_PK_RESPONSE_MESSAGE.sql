ALTER TABLE RESPONSE_MESSAGE
  ADD CONSTRAINT PK_RESPONSE_MESSAGE PRIMARY KEY
  (
    MESSAGE_ID
  )
  USING INDEX (
    CREATE UNIQUE INDEX PK_RESPONSE_MESSAGE ON RESPONSE_MESSAGE
    (
      MESSAGE_ID
    )
    TABLESPACE TS_APP_APM_IND_U01
  )
/