CREATE SEQUENCE opal_apldba.SQ_LOGIN_INFO_REREGIST_RCPT_ID
INCREMENT BY 1
START WITH 1
MAXVALUE 9999999999
NOMINVALUE
NOCYCLE 
CACHE 20
NOORDER
/
GRANT SELECT ON opal_apldba.SQ_LOGIN_INFO_REREGIST_RCPT_ID TO role_apl
/
CREATE PUBLIC SYNONYM SQ_LOGIN_INFO_REREGIST_RCPT_ID FOR opal_apldba.SQ_LOGIN_INFO_REREGIST_RCPT_ID
/