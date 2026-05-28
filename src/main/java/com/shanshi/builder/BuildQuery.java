package com.shanshi.builder;

import com.shanshi.bean.FieldInfo;
import com.shanshi.bean.TableInfo;
import com.shanshi.constant.PropertiesConstant;
import com.shanshi.constant.SqlTypeConstant;
import com.shanshi.utils.DateUtils;
import com.shanshi.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BuildQuery {
    public static void execute(TableInfo tableInfo){
        File directory = new File(PropertiesConstant.PATH_QUERY);
        if(!directory.exists()){
            directory.mkdirs();
        }
        File file = new File(PropertiesConstant.PATH_QUERY,tableInfo.getBeanName() + PropertiesConstant.BEAN_PARAM_SUFFIX + ".java");
        try{
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        OutputStream out = null;
        OutputStreamWriter outsw = null;
        BufferedWriter bw = null;

        try {
            out = new FileOutputStream(PropertiesConstant.PATH_QUERY + "/" + tableInfo.getBeanName() + PropertiesConstant.BEAN_PARAM_SUFFIX + ".java");
            outsw = new OutputStreamWriter(out);
            bw = new BufferedWriter(outsw);

            //  生成包路径
            bw.write("package " + PropertiesConstant.PACKAGE_QUERY_RELATIVE + ";");
            bw.newLine();
            bw.newLine();

            //  导包
            if(tableInfo.getHaveDate() || tableInfo.getHaveDateTime()){
                bw.write("import java.util.Date;\n");
            }
            if(tableInfo.getHaveBigDecimal()){
                bw.write("import java.math.BigDecimal;\n");
            }

            //  生成类注释
            bw.write(BuildComment.buildClassComment(tableInfo.getTableComment() + "查询对象"));
            bw.write("public class " + tableInfo.getBeanName() + PropertiesConstant.BEAN_PARAM_SUFFIX + " extends BaseQuery {");
            bw.newLine();

            //  生成属性
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                //  生成属性备注
                bw.write(BuildComment.buildPropertyComment(fieldInfo.getFiledComment()));

                if(!fieldInfo.getJavaType().equals("Date")) {
                    bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                    bw.newLine();
                }

                if(fieldInfo.getJavaType().equals("String")){
                    bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName()+PropertiesConstant.BEAN_PROPERTY_SUFFIX_FUZZY + ";");
                }
                if(fieldInfo.getJavaType().equals("Date")){
                    bw.write("\tprivate String " + fieldInfo.getPropertyName() + ";");
                    bw.newLine();
                    bw.write("\tprivate String " + fieldInfo.getPropertyName() + PropertiesConstant.BEAN_PROPERTY_SUFFIX_TIME_START + ";");
                    bw.newLine();
                    bw.write("\tprivate String " + fieldInfo.getPropertyName() + PropertiesConstant.BEAN_PROPERTY_SUFFIX_TIME_END + ";");
                }
                bw.newLine();
                bw.newLine();
            }

            //  生成getter，setter
            buildGetSet(bw,tableInfo.getFieldList());
            buildGetSet(bw,tableInfo.getExtendFieldList());

            //  收尾
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


    public static void buildGetSet(BufferedWriter bw, List<FieldInfo> list) throws IOException {
        for (FieldInfo fieldInfo : list) {
            if(!fieldInfo.getJavaType().equals("Date")) {

                String propertyName = StringUtils.switchWordFirstLetterToUpper(fieldInfo.getPropertyName());
                bw.write("\tpublic void set" + propertyName + " (" + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ") " + "{");
                bw.newLine();
                bw.write("\t\tthis." + fieldInfo.getPropertyName() + " = " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();

                bw.write("\tpublic " + fieldInfo.getJavaType() + " get" + propertyName + " () " + "{");
                bw.newLine();
                bw.write("\t\treturn this." + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
            }else{
                String propertyName = StringUtils.switchWordFirstLetterToUpper(fieldInfo.getPropertyName());
                bw.write("\tpublic void set" + propertyName + " (String " + fieldInfo.getPropertyName() + ") " + "{");
                bw.newLine();
                bw.write("\t\tthis." + fieldInfo.getPropertyName() + " = " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();

                bw.write("\tpublic String get" + propertyName + " () " + "{");
                bw.newLine();
                bw.write("\t\treturn this." + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
            }
        }
    }
}
