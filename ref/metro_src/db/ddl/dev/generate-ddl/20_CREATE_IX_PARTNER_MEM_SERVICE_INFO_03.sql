CREATE INDEX IX_PARTNER_MEM_SERVICE_INFO_03 ON PARTNER_MEM_SERVICE_INFO
(
  APPLY_START_DATE_TIME,
  APPLY_END_DATE_TIME
)
TABLESPACE TS_APP_APM_IND_U01
/
