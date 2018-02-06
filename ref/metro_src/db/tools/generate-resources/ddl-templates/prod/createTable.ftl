CREATE TABLE <#if entity.schema??>${entity.schema}.</#if>${entity.name} (
<#foreach column in entity.columnList>
  ${column.name} ${column.dataType}<#if column.length != 0>(${column.length}<#if column.scale != 0>,${column.scale}</#if><#if lengthSemantics==LengthSemantics.CHAR && (column.dataType == "VARCHAR2" || column.dataType == "CHAR")> CHAR</#if>)</#if><#if column.isArray()> ARRAY</#if><#if column.defaultValue?has_content> DEFAULT ${column.defaultValue} </#if><#if !column.isNullable()> NOT NULL </#if><#if column_has_next>,</#if>
</#foreach>
)
TABLESPACE TS_APP_APM_DAT_U01
/
GRANT SELECT ON <#if entity.schema??>${entity.schema}.</#if>${entity.name} TO role_objsel
/
GRANT INSERT ON <#if entity.schema??>${entity.schema}.</#if>${entity.name} TO role_objupd
/
GRANT UPDATE ON <#if entity.schema??>${entity.schema}.</#if>${entity.name} TO role_objupd
/
GRANT DELETE ON <#if entity.schema??>${entity.schema}.</#if>${entity.name} TO role_objupd
/
CREATE PUBLIC SYNONYM ${entity.name} FOR <#if entity.schema??>${entity.schema}.</#if>${entity.name}
/
