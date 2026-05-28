package com.shanshi.bean;

/**
 * 表的字段信息bean
 */
public class FieldInfo {
    /**
     * 字段名
     */
    private String filedName;

    /**
     * 对应的javabean属性名
     */
    private String propertyName;

    /**
     * 字段在SQL中的类型
     */
    private String sqlType;
    /**
     * 字段在java中的类型
     */
    private String javaType;
    /**
     * 字段备注
     */
    private String filedComment;
    /**
     * 主键是否自增
     */
    private Boolean isAutoIncrement;

    public FieldInfo() {
    }

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getFiledComment() {
        return filedComment;
    }

    public void setFiledComment(String filedComment) {
        this.filedComment = filedComment;
    }

    public Boolean getAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(Boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }
}
