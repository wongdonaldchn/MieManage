ALTER TABLE MILE_CATEGORY_SUM_INFO
  ADD CONSTRAINT PK_MILE_CATEGORY_SUM_INFO PRIMARY KEY
  (
    APPLICATION_MEMBER_ID,
    MILE_SUM_YEAR_MONTH,
    MILE_CATEGORY_CODE
  )
  USING INDEX (
    CREATE UNIQUE INDEX PK_MILE_CATEGORY_SUM_INFO ON MILE_CATEGORY_SUM_INFO
    (
      APPLICATION_MEMBER_ID,
      MILE_SUM_YEAR_MONTH,
      MILE_CATEGORY_CODE
    )
    TABLESPACE TS_APP_APM_IND_U01
  )
/
