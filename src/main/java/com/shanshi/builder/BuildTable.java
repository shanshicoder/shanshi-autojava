package com.shanshi.builder;

import com.shanshi.bean.FieldInfo;
import com.shanshi.bean.TableInfo;
import com.shanshi.constant.PropertiesConstant;
import com.shanshi.constant.SqlTypeConstant;
import com.shanshi.utils.JsonUtils;
import com.shanshi.utils.PropertiesUtils;
import com.shanshi.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class BuildTable {
    private static Connection connection = null;
    private static final Logger log = LoggerFactory.getLogger(BuildTable.class);

    private static final String SQL_SHOW_TABLE_STATUS = "show table status;";
    private static final String SQL_SHOW_ALL_FIELDS = "SHOW FULL FIELDS FROM %s;";
    private static final String SQL_SHOW_ALL_INDEX = "SHOW INDEX FROM %s";

    static {
        //  连接数据库
        String driverName = PropertiesUtils.getProperty("db.driver.name");
        String url = PropertiesUtils.getProperty("db.url");
        String username = PropertiesUtils.getProperty("db.username");
        String password = PropertiesUtils.getProperty("db.password");

        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            log.error("数据库链接异常:{}",e);
        }
    }

    /**
     * 读取mysql数据库中的表
     */
    public static List<TableInfo> getTables(){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<TableInfo> tableInfoList = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(SQL_SHOW_TABLE_STATUS);
            resultSet = preparedStatement.executeQuery();

            log.info("resultSet:{}",resultSet);

            while (resultSet.next()){
                String tableName = resultSet.getString("Name");
                String tableComment = resultSet.getString("Comment");
                log.info("name:{}, comment:{}",tableName,tableComment);

                //  构建表信息对象
                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                if(PropertiesConstant.IGNORE_TABLE_PREFIX){
                    tableName = tableName.substring(tableName.indexOf("_") + 1);
                }
                String beanName = switchFirstLetterUpper(tableName,true);
                tableInfo.setBeanName(beanName);
                tableInfo.setBeanParamName(beanName + PropertiesConstant.BEAN_PARAM_SUFFIX);
                tableInfo.setTableComment(tableComment);
                log.info("tableInfo:{}",tableInfo);

                readFieldInfo(tableInfo);
                readKeyIndexMap(tableInfo);
                log.info("表信息：{}", JsonUtils.switchObjectToJson(tableInfo));
                tableInfoList.add(tableInfo);
            }
        } catch (SQLException e) {
            log.error("sql语句执行出现异常:{}",e);
        } finally {
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return tableInfoList;
    }

    /**
     * 读取表的每个字段的信息
     * @param tableInfo 保存了表信息的对象
     * @return
     */
    public static void readFieldInfo(TableInfo tableInfo){
        PreparedStatement preparedStatement = null;
        ResultSet fieldInfoSet = null;
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        List<FieldInfo> extendFieldInfoList = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(String.format(SQL_SHOW_ALL_FIELDS,tableInfo.getTableName()));
            fieldInfoSet = preparedStatement.executeQuery();
            boolean haveDate = false;
            boolean haveDateTime = false;
            boolean haveBigDecimal = false;

            while (fieldInfoSet.next()){
                FieldInfo fieldInfo = new FieldInfo();

                String fieldName = fieldInfoSet.getString("Field");
                String propertyName = switchFirstLetterUpper(fieldName,false);
                log.info("fieldName:{}, propertyName:{}",fieldName,propertyName);
                String sqlType = fieldInfoSet.getString("Type");
                if(sqlType.contains("(")){
                    sqlType = sqlType.substring(0,sqlType.indexOf("("));
                }
                String javaType = switchSqlTypeToJavaType(sqlType);
                String extra = fieldInfoSet.getString("Extra");
                String key = fieldInfoSet.getString("Key");
                String comment = fieldInfoSet.getString("Comment");

                if(javaType.equals("String")){
                    FieldInfo newFieldInfo = new FieldInfo();
                    newFieldInfo.setFiledName(fieldName);
                    newFieldInfo.setPropertyName(propertyName+PropertiesConstant.BEAN_PROPERTY_SUFFIX_FUZZY);
                    newFieldInfo.setSqlType(sqlType);
                    newFieldInfo.setJavaType("String");
                    extendFieldInfoList.add(newFieldInfo);
                }

                if(javaType.equals("Date")){
                    FieldInfo newFieldInfo = new FieldInfo();
                    newFieldInfo.setFiledName(fieldName);
                    newFieldInfo.setPropertyName(propertyName + PropertiesConstant.BEAN_PROPERTY_SUFFIX_TIME_START);
                    newFieldInfo.setSqlType(sqlType);
                    newFieldInfo.setJavaType("String");
                    extendFieldInfoList.add(newFieldInfo);

                    newFieldInfo = new FieldInfo();
                    newFieldInfo.setFiledName(fieldName);
                    newFieldInfo.setPropertyName(propertyName + PropertiesConstant.BEAN_PROPERTY_SUFFIX_TIME_END);
                    newFieldInfo.setSqlType(sqlType);
                    newFieldInfo.setJavaType("String");
                    extendFieldInfoList.add(newFieldInfo);
                }


                fieldInfo.setFiledName(fieldName);
                fieldInfo.setPropertyName(propertyName);
                fieldInfo.setSqlType(sqlType);
                fieldInfo.setJavaType(javaType);
                fieldInfo.setFiledComment(comment);
                fieldInfo.setAutoIncrement(extra.contains("auto_increment"));
                fieldInfoList.add(fieldInfo);

                //补充tableInfo对象的属性信息
                if(sqlType.contains("date")){
                    haveDate = true;
                }
                if(sqlType.contains("datetime")){
                    haveDateTime = true;
                }
                if(sqlType.contains("bigdecimal")){
                    haveBigDecimal = true;
                }
            }
            tableInfo.setHaveDate(haveDate);
            tableInfo.setHaveDateTime(haveDateTime);
            tableInfo.setHaveBigDecimal(haveBigDecimal);
            tableInfo.setFieldList(fieldInfoList);
            tableInfo.setExtendFieldList(extendFieldInfoList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(fieldInfoSet != null){
                try {
                    fieldInfoSet.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 读表的索引信息
     * @param tableInfo
     */
    public static void readKeyIndexMap(TableInfo tableInfo){
        PreparedStatement preparedStatement = null;
        ResultSet keyIndexSet = null;
        try {
            preparedStatement = connection.prepareStatement(String.format(SQL_SHOW_ALL_INDEX,tableInfo.getTableName()));
            keyIndexSet = preparedStatement.executeQuery();

            Map<String,FieldInfo> cacheMap = new HashMap<>();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                cacheMap.put(fieldInfo.getFiledName(), fieldInfo);
            }

            log.info("cacheMap:{}",cacheMap);

            while (keyIndexSet.next()){
                Integer nonUnique = keyIndexSet.getInt("Non_unique");
                String columnName = keyIndexSet.getString("Column_name");
                String keyName = keyIndexSet.getString("Key_name");
                if(nonUnique == 1){
                    continue;
                }
                List<FieldInfo> keyIndexList = tableInfo.getKeyIndexMap().get(keyName);

                log.info("keyIndexList:{}",keyIndexList);

                if(keyIndexList == null){
                    keyIndexList = new ArrayList<>();
                    tableInfo.getKeyIndexMap().put(keyName,keyIndexList);
                }

                if(cacheMap.containsKey(columnName)){
                    keyIndexList.add(cacheMap.get(columnName));
                }

                log.info("KeyIndexMap:{}",tableInfo.getKeyIndexMap());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(keyIndexSet != null){
                try {
                    keyIndexSet.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    /**
     * 将表名以下划线分隔的每个单词首字母转换为大写
     * @param field 表名
     * @param isSwitchFirstWord 是否将以下划线分隔的第一个单词首字母转换为大写
     */
    private static String switchFirstLetterUpper(String field, Boolean isSwitchFirstWord){
        StringBuilder sb = new StringBuilder();
        String[] splits = field.split("_");
        log.info("splits: {}", splits);
        sb.append(isSwitchFirstWord ? StringUtils.switchWordFirstLetterToUpper(splits[0]) : splits[0]);
        for (int i = 1; i < splits.length; i++){
            sb.append(StringUtils.switchWordFirstLetterToUpper(splits[i]));
        }
        return sb.toString();
    }

    /**
     * 将sql中的类型转换为java中的类型
     * @param sqlType
     * @return
     */
    private static String switchSqlTypeToJavaType(String sqlType){
        if(ArrayUtils.contains(SqlTypeConstant.SQL_DATE_TIME_TYPE,sqlType)){
            return "Date";
        }else if(ArrayUtils.contains(SqlTypeConstant.SQL_DATE_TYPE,sqlType)){
            return "Date";
        }else if(ArrayUtils.contains(SqlTypeConstant.SQL_INTEGER_TYPE,sqlType)){
            return "Integer";
        } else if (ArrayUtils.contains(SqlTypeConstant.SQL_LONG_TYPE, sqlType)) {
            return "Long";
        } else if (ArrayUtils.contains(SqlTypeConstant.SQL_DECIMAL_TYPE,sqlType)) {
            return "BigDecimal";
        } else if (ArrayUtils.contains(SqlTypeConstant.SQL_STRING_TYPE,sqlType)) {
            return "String";
        }else {
            throw new RuntimeException("当前类型不存在:"+sqlType);
        }
    }

    public static void main(String[] args) {
        getTables();
    }
}
