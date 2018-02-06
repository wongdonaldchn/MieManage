CREATE SEQUENCE opal_apldba.SQ_APPLICATION_MEMBER_ID
INCREMENT BY 1
START WITH 1
MAXVALUE 9999999999
NOMINVALUE
NOCYCLE 
CACHE 20
NOORDER
/
GRANT SELECT ON opal_apldba.SQ_APPLICATION_MEMBER_ID TO role_apl
/
CREATE PUBLIC SYNONYM SQ_APPLICATION_MEMBER_ID FOR opal_apldba.SQ_APPLICATION_MEMBER_ID
/
