package com.shanshi.builder;

import com.shanshi.bean.FieldInfo;
import com.shanshi.bean.TableInfo;
import com.shanshi.constant.PropertiesConstant;
import com.shanshi.constant.SqlTypeConstant;
import com.shanshi.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuildMapperXml {
    public static Logger logger = LoggerFactory.getLogger(BuildMapperXml.class);
    public static final String BASE_RESULT_MAP = "base_result_map";
    public static final String BASE_QUERY_RESULT_LIST = "base_query_result_list";
    public static final String BASE_QUERY_CONDITION = "base_query_condition";
    public static final String EXTEND_QUERY_CONDITION = "extend_query_condition";
    public static final String QUERY_CONDITION = "query_condition";

    public static void execute(TableInfo tableInfo){
        File directory = new File(PropertiesConstant.PATH_MAPPER_XML);
        if(!directory.exists()){
            directory.mkdirs();
        }
        String mapperXmlName = tableInfo.getBeanName() + PropertiesConstant.MAPPER_SUFFIX + ".xml";
        File file = new File(PropertiesConstant.PATH_MAPPER_XML, mapperXmlName);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        OutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out, "utf-8");
            bw = new BufferedWriter(osw);

            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >");
            bw.newLine();
            bw.write(String.format("<mapper namespace=\"%s\">",PropertiesConstant.PACKAGE_MAPPER_RELATIVE + "." +tableInfo.getBeanName() + PropertiesConstant.MAPPER_SUFFIX));
            bw.newLine();

            bw.write("\t<!--实体映射-->\n");
            bw.write(String.format("\t<resultMap id=\"%s\" type=\"%s\">\n",BASE_RESULT_MAP, PropertiesConstant.PACKAGE_PO_RELATIVE + "." + tableInfo.getBeanName()));
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            List<FieldInfo> primaryList = keyIndexMap.get("PRIMARY");
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String resultOrId = "result";
                bw.write("\t\t<!--" + fieldInfo.getFiledComment() + "-->\n");
                if(primaryList.size() == 1 && fieldInfo.getPropertyName().equals(primaryList.get(0).getPropertyName())){
                    resultOrId = "id";
                }
                bw.write(String.format("\t\t<%s column=\"%s\" property=\"%s\" />\n",resultOrId,fieldInfo.getFiledName(),fieldInfo.getPropertyName()));
            }
            bw.write("\t</resultMap>\n");
            bw.newLine();

            //通用查询结果列
            bw.write("\t<!--通用查询结果列-->\n");
            bw.write(String.format("\t<sql id=\"%s\">\n\t\t",BASE_QUERY_RESULT_LIST));
            Integer index = 0;
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if(index != 0){
                    bw.write(",");
                }
                bw.write(fieldInfo.getFiledName());
                index++;
            }
            bw.newLine();
            bw.write("\t</sql>\n");
            bw.newLine();

            bw.write("\t<!--基础查询条件-->\n");
            bw.write(String.format("\t<sql id=\"%s\">\n",BASE_QUERY_CONDITION));
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String eptStrQuery = "";
                if(ArrayUtils.contains(SqlTypeConstant.SQL_STRING_TYPE,fieldInfo.getSqlType())){
                    eptStrQuery = String.format("and query.%s!=''",fieldInfo.getPropertyName());
                }
                bw.write(String.format("\t\t<if test=\"query.%s != null %s\">\n",fieldInfo.getPropertyName(),eptStrQuery));
                bw.write(String.format("\t\t\tand %s = #{query.%s}\n",fieldInfo.getFiledName(),fieldInfo.getPropertyName()));
                bw.write("\t\t</if>\n");
            }
            bw.write("\t</sql>\n");
            bw.newLine();

            bw.write("\t<!--扩展的查询条件-->\n");
            bw.write(String.format("\t<sql id=\"%s\">\n", EXTEND_QUERY_CONDITION));
            for (FieldInfo fieldInfo : tableInfo.getExtendFieldList()) {
                if(ArrayUtils.contains(SqlTypeConstant.SQL_STRING_TYPE,fieldInfo.getSqlType())){
                    bw.write(String.format("\t\t<if test=\"query.%s != null and query.%s != ''\">\n",fieldInfo.getPropertyName(),fieldInfo.getPropertyName()));
                    bw.write(String.format("\t\t\tand %s like concat('%%',#{query.%s},'%%') \n",fieldInfo.getFiledName(),fieldInfo.getPropertyName()));
                    bw.write("\t\t</if>\n");
                }else{
                    bw.write(String.format("\t\t<if test=\"query.%s != null and query.%s != ''\">\n",fieldInfo.getPropertyName(),fieldInfo.getPropertyName()));
                    if(fieldInfo.getPropertyName().endsWith("Start")){
                        bw.write(String.format("\t\t\t<![CDATA[ and %s >= str_to_date(#{query.%s},'%%Y-%%m-%%d')]]> \n",fieldInfo.getFiledName(),fieldInfo.getPropertyName()));
                    }else{
                        bw.write(String.format("\t\t\t<![CDATA[ and %s < date_sub(str_to_date(#{query.%s}, '%%Y-%%m-%%d'), interval -1 day)]]> \n",fieldInfo.getFiledName(),fieldInfo.getPropertyName()));
                    }
                    bw.write("\t\t</if>\n");
                }
            }
            bw.write("\t</sql>\n");
            bw.newLine();

            //  查询条件
            bw.write("\t<!--查询条件-->\n");
            bw.write(String.format("\t<sql id=\"%s\">\n",QUERY_CONDITION));
            bw.write("\t\t<where>\n");
            bw.write(String.format("\t\t\t<include refid=\"%s\" /> \n",BASE_QUERY_CONDITION));
            bw.write(String.format("\t\t\t<include refid=\"%s\" /> \n",EXTEND_QUERY_CONDITION));
            bw.write("\t\t</where>\n");
            bw.write("\t</sql>\n");
            bw.newLine();

            //  查询集合
            bw.write("\t<!--查询集合-->\n");
            bw.write("\t<select id=\"selectList\" resultMap=\"" + BASE_RESULT_MAP + "\">\n");
            bw.write("\t\tSELECT \n");
            bw.write("\t\t<include refid=\"" + BASE_QUERY_RESULT_LIST + "\" /> \n");
            bw.write("\t\tFROM " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<include refid=\"" + QUERY_CONDITION + "\" /> \n");
            bw.write("\t\t<if test=\"query.orderBy != null\">\n");
            bw.write("\t\t\torder by ${query.orderBy}\n");
            bw.write("\t\t</if>\n");
            bw.write("\t\t<if test=\"query.simplePage != null\">\n");
            bw.write("\t\t\tlimit #{query.simplePage.start}, #{query.simplePage.end}\n");
            bw.write("\t\t</if>\n");
            bw.write("\t</select>\n");
            bw.newLine();

            //  查询数量
            bw.write("\t<!--查询数量-->\n");
            bw.write("");
            bw.write("\t<select id=\"selectCount\" resultType=\"java.lang.Integer\">\n");
            bw.write("\t\tSELECT count(1) FROM " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<include refid=\"" + QUERY_CONDITION + "\" />\n");
            bw.write("\t</select>\n");
            bw.newLine();

            //  单条插入
            bw.write("\t<!--插入-->\n");
            bw.write("\t<insert id=\"insert\" parameterType=\""+PropertiesConstant.PACKAGE_PO_RELATIVE + "." + tableInfo.getBeanName()+"\">\n");
            FieldInfo autoIncrement = null;
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if(fieldInfo.getAutoIncrement() && fieldInfo.getAutoIncrement() != null){
                    autoIncrement = fieldInfo;
                    break;
                }
            }
            if(autoIncrement != null){
                bw.write("\t\t<selectKey keyProperty=\"bean."+autoIncrement.getPropertyName()+"\" resultType=\""+autoIncrement.getJavaType()+"\" order=\"AFTER\">\n");
                bw.write("\t\t\tSELECT LAST_INSERT_ID()\n");
                bw.write("\t\t</selectKey>\n");
            }
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean."+fieldInfo.getPropertyName() + " != null\">\n");
                bw.write("\t\t\t\t" + fieldInfo.getFiledName() + ",\n");
                bw.write("\t\t\t</if>\n");
            }
            bw.write("\t\t</trim>\n");
            bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n");
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean."+fieldInfo.getPropertyName() + " != null\">\n");
                bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},\n");
                bw.write("\t\t\t</if>\n");
            }
            bw.write("\t\t</trim>\n");
            bw.write("\t</insert>\n");
            bw.newLine();

            //  插入或更新
            bw.write("\t<!--插入或更新-->\n");
            bw.write("\t<insert id=\"insertOrUpdate\" parameterType=\""+PropertiesConstant.PACKAGE_PO_RELATIVE + "." + tableInfo.getBeanName()+"\">\n");
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean."+fieldInfo.getPropertyName() + " != null\">\n");
                bw.write("\t\t\t\t" + fieldInfo.getFiledName() + ",\n");
                bw.write("\t\t\t</if>\n");
            }
            bw.write("\t\t</trim>\n");
            bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n");
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean."+fieldInfo.getPropertyName() + " != null\">\n");
                bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},\n");
                bw.write("\t\t\t</if>\n");
            }
            bw.write("\t\t</trim>\n");
            bw.write("\t\tON DUPLICATE key update\n");
            bw.write("\t\t<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\">\n");
            Map<String,String> keyTempMap = new HashMap<>();
            for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                for (FieldInfo fieldInfo : fieldInfoList) {
                    keyTempMap.put(fieldInfo.getFiledName(), fieldInfo.getFiledName());
                }
            }
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (keyTempMap.get(fieldInfo.getFiledName()) != null){
                    continue;
                }
                bw.write("\t\t\t<if test=\"bean."+fieldInfo.getPropertyName() + " != null\">\n");
                bw.write("\t\t\t\t" + fieldInfo.getFiledName() + " = VALUES("+fieldInfo.getFiledName()+"),\n");
                bw.write("\t\t\t</if>\n");
            }
            bw.write("\t\t</trim>\n");
            bw.write("\t</insert>\n");
            bw.newLine();

            //  批量插入
            bw.write("\t<!--批量插入-->\n");
            bw.write("\t<insert id=\"insertBatch\" parameterType=\""+PropertiesConstant.PACKAGE_PO_RELATIVE + "." + tableInfo.getBeanName()+"\">\n");
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t\t(");
            Integer index2 = 0;
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if(fieldInfo.getAutoIncrement()){
                    continue;
                }
                if(index2 != 0){
                    bw.write(",");
                }
                bw.write(fieldInfo.getFiledName());
                index2++;
            }
            bw.write(")\n");
            bw.write("\t\tVALUES\n");
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\">\n");
            bw.write("\t\t\t(");
            Integer index3 = 0;
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if(fieldInfo.getAutoIncrement()){
                    continue;
                }
                if(index3 != 0){
                    bw.write(",");
                }
                bw.write("#{item."+fieldInfo.getPropertyName()+"}");
                index3++;
            }
            bw.write(")\n");
            bw.write("\t\t</foreach>\n");
            bw.write("\t</insert>\n");
            bw.newLine();

            //  批量插入或更新
            bw.write("\t<!--批量插入或更新-->\n");
            bw.write("\t<insert id=\"insertOrUpdateBatch\" parameterType=\""+PropertiesConstant.PACKAGE_PO_RELATIVE + "." + tableInfo.getBeanName()+"\">\n");
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t\t(");
            Integer index4 = 0;
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if(fieldInfo.getAutoIncrement()){
                    continue;
                }
                if(index4 != 0){
                    bw.write(",");
                }
                bw.write(fieldInfo.getFiledName());
                index4++;
            }
            bw.write(")\n");
            bw.write("\t\tVALUES\n");
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\">\n");
            bw.write("\t\t\t(");
            Integer index5 = 0;
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if(fieldInfo.getAutoIncrement()){
                    continue;
                }
                if(index5 != 0){
                    bw.write(",");
                }
                bw.write("#{item."+fieldInfo.getPropertyName()+"}");
                index5++;
            }
            bw.write(")\n");
            bw.write("\t\t</foreach>\n");
            bw.write("\t\ton DUPLICATE key update\n");
            StringBuilder sb = new StringBuilder();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                sb.append("\t\t"+fieldInfo.getFiledName() + " = VALUES(" + fieldInfo.getFiledName() + "),\n");
            }
            String s = sb.substring(0,sb.lastIndexOf(","));
            bw.write(s);
            bw.newLine();
            bw.write("\t</insert>\n");
            bw.newLine();


            Set<Map.Entry<String, List<FieldInfo>>> entrySet = keyIndexMap.entrySet();
            for (Map.Entry<String, List<FieldInfo>> entry : entrySet) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                StringBuilder key = new StringBuilder();
                StringBuilder conditionParam = new StringBuilder();
                sb.setLength(0);
                for (int i = 0; i < fieldInfoList.size(); i++){
                    FieldInfo fieldInfo = fieldInfoList.get(i);
                    sb.append(StringUtils.switchWordFirstLetterToUpper(fieldInfo.getPropertyName()));
                    key.append(fieldInfo.getPropertyName());
                    conditionParam.append(fieldInfoList.get(i).getFiledName()+ " = #{" + fieldInfoList.get(i).getPropertyName() +"}");
                    if(i != fieldInfoList.size() - 1){
                        sb.append("And");
                        key.append(",");
                        conditionParam.append(" and ");
                    }
                }
                bw.write("\t<!--根据" + key + "查询-->\n");
                bw.write("\t<select id=\"selectBy" + sb + "\" resultMap=\""+BASE_RESULT_MAP+"\">\n");
                bw.write("\t\tSELECT\n");
                bw.write("\t\t<include refid=\"" + BASE_QUERY_RESULT_LIST + "\" />\n");
                bw.write("\t\tFROM " + tableInfo.getTableName() +"\n");
                bw.write("\t\tWHERE " + conditionParam + "\n");
                bw.write("\t</select>\n");
                bw.newLine();

                bw.write("\t<!--根据" + key + "更新-->\n");
                bw.write("\t<update id=\"updateBy" + sb + "\" parameterType=\""+PropertiesConstant.PACKAGE_PO_RELATIVE+"."+tableInfo.getBeanName()+"\">\n");
                bw.write("\t\tUPDATE " + tableInfo.getTableName() +"\n");
                bw.write("\t\t<set>\n");
                for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                    bw.write("\t\t\t<if test=\"bean."+fieldInfo.getPropertyName()+" != null\">\n");
                    bw.write("\t\t\t\t"+fieldInfo.getFiledName()+" = #{bean."+fieldInfo.getPropertyName()+"},\n");
                    bw.write("\t\t\t</if>\n");
                }
                bw.write("\t\t</set>\n");
                bw.write("\t\tWHERE " + conditionParam + "\n");
                bw.write("\t</update>\n");
                bw.newLine();
                bw.newLine();


                bw.write("\t<!--根据" + key + "删除-->\n");
                bw.write("\t<delete id=\"deleteBy" + sb + "\">\n");
                bw.write("\t\tDELETE FROM " + tableInfo.getTableName() +"\n");
                bw.write("\t\tWHERE " + conditionParam + "\n");
                bw.write("\t</delete>\n");
                bw.newLine();
            }


            bw.write("</mapper>\n");
            bw.flush();

        } catch (IOException e) {
            logger.error("构建MapperXml失败");
            throw new RuntimeException(e);
        } finally {
            if(bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(osw != null){
                try {
                    osw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(out != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
