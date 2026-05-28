package com.shanshi.builder;

import com.shanshi.bean.FieldInfo;
import com.shanshi.bean.TableInfo;
import com.shanshi.constant.PropertiesConstant;
import com.shanshi.utils.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuildMapper {
    public static void execute(TableInfo tableInfo){
        File directory = new File(PropertiesConstant.PATH_MAPPER);
        if(!directory.exists()){
            directory.mkdirs();
        }

        String className = tableInfo.getBeanName() + PropertiesConstant.MAPPER_SUFFIX;
        File file = new File(PropertiesConstant.PATH_MAPPER, className + ".java");
        try{
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        OutputStream out = null;
        OutputStreamWriter outsw = null;
        BufferedWriter bw = null;

        try {
            out = new FileOutputStream(PropertiesConstant.PATH_MAPPER + "/" + className + ".java");
            outsw = new OutputStreamWriter(out);
            bw = new BufferedWriter(outsw);

            //  生成包路径
            bw.write("package " + PropertiesConstant.PACKAGE_MAPPER_RELATIVE + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import org.apache.ibatis.annotations.Param;");
            bw.newLine();
            bw.write("import org.apache.ibatis.annotations.Mapper;");
            bw.newLine();
            bw.newLine();
            //  生成类注释
            bw.write(BuildComment.buildClassComment(tableInfo.getTableComment() + "查询对象"));
            bw.write("@Mapper");
            bw.newLine();
            bw.write("public interface " + className + "<T,P> extends BaseMapper {");
            bw.newLine();

            //  生成抽象方法
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            Set<Map.Entry<String, List<FieldInfo>>> entrySet = keyIndexMap.entrySet();
            for (Map.Entry<String, List<FieldInfo>> entry : entrySet) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                StringBuilder sb = new StringBuilder();
                StringBuilder comment = new StringBuilder();
                StringBuilder params = new StringBuilder();
                for (int i = 0; i < fieldInfoList.size(); i++){
                    FieldInfo fieldInfo = fieldInfoList.get(i);
                    sb.append(StringUtils.switchWordFirstLetterToUpper(fieldInfo.getPropertyName()));
                    comment.append(fieldInfo.getPropertyName());
                    params.append("@Param(\"" + fieldInfo.getPropertyName() + "\") ");
                    params.append(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                    if(i != fieldInfoList.size() - 1){
                        sb.append("And");
                        comment.append(",");
                        params.append(", ");
                    }
                }
                String paramsStr = params.toString();
                bw.write(BuildComment.buildMethodComment("根据" + comment + "查询"));
                bw.write(String.format("\tT selectBy%s (%s);",sb,paramsStr));
                bw.newLine();
                bw.newLine();
                bw.write(BuildComment.buildMethodComment("根据" + comment + "更新"));
                bw.write(String.format("\tInteger updateBy%s (%s);",sb,"@Param(\"bean\") T t, "+paramsStr));
                bw.newLine();
                bw.newLine();
                bw.write(BuildComment.buildMethodComment("根据" + comment + "删除"));
                bw.write(String.format("\tInteger deleteBy%s (%s);",sb,paramsStr));
                bw.newLine();
                bw.newLine();
            }


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
}
