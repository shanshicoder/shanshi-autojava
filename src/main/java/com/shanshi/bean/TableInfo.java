package com.shanshi.bean;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 表的信息
 */
public class TableInfo {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表名对应的javabean名
     */
    private String beanName;
    /**
     * javabean执行sql操作参数名
     */
    private String beanParamName;
    /**
     * 表备注
     */
    private String tableComment;
    /**
     * 表的字段列表
     */
    private List<FieldInfo> fieldList;

    /**
     * 扩展表的字段
     */
    private List<FieldInfo> extendFieldList;

    /**
     * 表的索引Map集合
     * @String 表示索引名
     * @List<FieldInfo> 表示该索引对应的字段
     */
    private Map<String, List<FieldInfo>> keyIndexMap = new LinkedHashMap<>();



    /**
     * 表中是否有以下类型
     */
    private Boolean haveDate;
    private Boolean haveDateTime;
    private Boolean haveBigDecimal;

    public TableInfo() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanParamName() {
        return beanParamName;
    }

    public void setBeanParamName(String beanParamName) {
        this.beanParamName = beanParamName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public List<FieldInfo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FieldInfo> fieldList) {
        this.fieldList = fieldList;
    }

    public Map<String, List<FieldInfo>> getKeyIndexMap() {
        return keyIndexMap;
    }

    public void setKeyIndexMap(Map<String, List<FieldInfo>> keyIndexMap) {
        this.keyIndexMap = keyIndexMap;
    }

    public Boolean getHaveDate() {
        return haveDate;
    }

    public void setHaveDate(Boolean haveDate) {
        this.haveDate = haveDate;
    }

    public Boolean getHaveDateTime() {
        return haveDateTime;
    }

    public void setHaveDateTime(Boolean haveDateTime) {
        this.haveDateTime = haveDateTime;
    }

    public Boolean getHaveBigDecimal() {
        return haveBigDecimal;
    }

    public void setHaveBigDecimal(Boolean haveBigDecimal) {
        this.haveBigDecimal = haveBigDecimal;
    }
    public List<FieldInfo> getExtendFieldList() {
        return extendFieldList;
    }

    public void setExtendFieldList(List<FieldInfo> extendFieldList) {
        this.extendFieldList = extendFieldList;
    }
}
