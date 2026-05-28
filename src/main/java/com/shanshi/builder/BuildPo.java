package com.shanshi.builder;

import com.shanshi.bean.FieldInfo;
import com.shanshi.bean.TableInfo;
import com.shanshi.constant.PropertiesConstant;
import com.shanshi.constant.SqlTypeConstant;
import com.shanshi.utils.DateUtils;
import com.shanshi.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;

public class BuildPo {
    public static void execute(TableInfo tableInfo){
        File directory = new File(PropertiesConstant.PATH_PO);
        if(!directory.exists()){
            directory.mkdirs();
        }
        File file = new File(PropertiesConstant.PATH_PO,tableInfo.getBeanName() + ".java");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        OutputStream out = null;
        OutputStreamWriter outsw = null;
        BufferedWriter bw = null;

        try {
            out = new FileOutputStream(PropertiesConstant.PATH_PO + "/" + tableInfo.getBeanName() + ".java");
            outsw = new OutputStreamWriter(out);
            bw = new BufferedWriter(outsw);

            //  生成包路径
            bw.write("package " + PropertiesConstant.PACKAGE_PO_RELATIVE + ";");
            bw.newLine();
            bw.newLine();
            //  导包
            bw.write("import java.io.Serializable;\n");
            if(tableInfo.getHaveDate() || tableInfo.getHaveDateTime()){
                bw.write("import java.util.Date;\n");
                bw.write(PropertiesConstant.BEAN_DATE_SERIALIZE_CLASS + ";");
                bw.newLine();
                bw.write(PropertiesConstant.BEAN_DATE_DESERIALIZE_CLASS + ";");
                bw.newLine();

                bw.write("import " + PropertiesConstant.PACKAGE_ENUMS_RELATIVE + ".DateTimePatternEnum;");
                bw.newLine();
                bw.write("import " + PropertiesConstant.PACKAGE_UTILS_RELATIVE + ".DateUtil;");
                bw.newLine();
            }
            if(tableInfo.getHaveBigDecimal()){
                bw.write("import java.math.BigDecimal;\n");
            }
            Boolean haveJsonIgnore = false;
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if(ArrayUtils.contains(PropertiesConstant.IGNORE_BEAN_TOJSON_FIELD.split(","),fieldInfo.getPropertyName())){
                    haveJsonIgnore = true;
                }
            }
            if(haveJsonIgnore){
                bw.write(PropertiesConstant.IGNORE_BEAN_TOJSON_CLASS + ";");
                bw.newLine();
            }
            bw.newLine();
            bw.newLine();
            //  生成类注释
            bw.write(BuildComment.buildClassComment(tableInfo.getTableComment()));
            bw.write("public class " + tableInfo.getBeanName() + " implements Serializable {");
            bw.newLine();
            //  生成属性
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write(BuildComment.buildPropertyComment(fieldInfo.getFiledComment()));
                if(ArrayUtils.contains(PropertiesConstant.IGNORE_BEAN_TOJSON_FIELD.split(","),fieldInfo.getPropertyName())){
                    bw.write("\t" + PropertiesConstant.IGNORE_BEAN_TOJSON_EXPRESSION);
                    bw.newLine();
                }
                if(ArrayUtils.contains(SqlTypeConstant.SQL_DATE_TYPE,fieldInfo.getSqlType())){
                    bw.write(String.format("\t" + PropertiesConstant.BEAN_DATE_SERIALIZE_EXPRESSION, DateUtils.YYYY_MM_DD_LINE));
                    bw.newLine();
                    bw.write(String.format("\t" + PropertiesConstant.BEAN_DATE_DESERIALIZE_EXPRESSION, DateUtils.YYYY_MM_DD_LINE));
                    bw.newLine();
                }
                if(ArrayUtils.contains(SqlTypeConstant.SQL_DATE_TIME_TYPE,fieldInfo.getSqlType())){
                    bw.write(String.format("\t" + PropertiesConstant.BEAN_DATE_SERIALIZE_EXPRESSION, DateUtils.DATE_TIME_F));
                    bw.newLine();
                    bw.write(String.format("\t" + PropertiesConstant.BEAN_DATE_DESERIALIZE_EXPRESSION, DateUtils.DATE_TIME_F));
                    bw.newLine();
                }
                bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();
            }
            //  生成getter，setter
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\tpublic void set" + StringUtils.switchWordFirstLetterToUpper(fieldInfo.getPropertyName()) + " ("+fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() +") " + "{" );
                bw.newLine();
                bw.write("\t\tthis."+fieldInfo.getPropertyName() + " = "+ fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();

                bw.write("\tpublic " + fieldInfo.getJavaType() + " get" + StringUtils.switchWordFirstLetterToUpper(fieldInfo.getPropertyName()) + " () " + "{" );
                bw.newLine();
                bw.write("\t\treturn this."+fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
            }

            //  生成toString
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic String toString() {");
            bw.newLine();
            StringBuffer sb = new StringBuffer();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String propertyName = fieldInfo.getPropertyName();
                if(ArrayUtils.contains(SqlTypeConstant.SQL_DATE_TIME_TYPE,fieldInfo.getSqlType())){
                    propertyName = "DateUtil.formatDate(" + propertyName + ", DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())";
                }
                if(ArrayUtils.contains(SqlTypeConstant.SQL_DATE_TYPE,fieldInfo.getSqlType())){
                    propertyName = "DateUtil.formatDate(" + propertyName + ", DateTimePatternEnum.YYYY_MM_DD.getPattern())";
                }
                sb.append("\"" + fieldInfo.getPropertyName() + "=\"");
                sb.append(" + ");
                sb.append("(" + fieldInfo.getPropertyName() + " == null ? \"空\" : " + propertyName + ")");
                sb.append(" + ");
                sb.append("\"" + ", ");
                sb.append("\"");
                sb.append(" + ");
            }
            String toString = sb.toString().substring(0, sb.lastIndexOf(",")-4);
            bw.write("\t\treturn " + toString + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.write("}");
            bw.newLine();


        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(outsw != null){
                try {
                    outsw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) {
        TableInfo tableInfo = new TableInfo();
        System.out.println(tableInfo.toString());
    }
}
