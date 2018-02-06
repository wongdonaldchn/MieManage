ALTER TABLE OPAL_APLTRN.MILE_INV_OBJ_TEMP_INFO
  ADD CONSTRAINT PK_MILE_INV_OBJ_TEMP_INFO PRIMARY KEY
  (
    APPLICATION_MEMBER_ID
  )
  USING INDEX (
    CREATE UNIQUE INDEX OPAL_APLTRN.PK_MILE_INV_OBJ_TEMP_INFO ON OPAL_APLTRN.MILE_INV_OBJ_TEMP_INFO
    (
      APPLICATION_MEMBER_ID
    )
    TABLESPACE TS_APP_APM_IND_U01
  )
/