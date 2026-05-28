package com.shanshi.builder;

import com.shanshi.bean.FieldInfo;
import com.shanshi.bean.TableInfo;
import com.shanshi.constant.PropertiesConstant;
import com.shanshi.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

public class BuildService {
    public static final Logger logger = LoggerFactory.getLogger(BuildService.class);

    public static void execute(TableInfo tableInfo){
        File directory = new File(PropertiesConstant.PATH_SERVICE);
        if(!directory.exists()){
            directory.mkdirs();
        }
        File file = new File(PropertiesConstant.PATH_SERVICE,tableInfo.getBeanName()+"Service.java");
        if (!file.exists()){
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
            osw = new OutputStreamWriter(out);
            bw = new BufferedWriter(osw);


            bw.write("package " + PropertiesConstant.PACKAGE_SERVICE_RELATIVE + ";");
            bw.newLine();

            bw.write("import " + PropertiesConstant.PACKAGE_PO_RELATIVE + "." + tableInfo.getBeanName()+";");
            bw.newLine();
            bw.write("import " + PropertiesConstant.PACKAGE_QUERY_RELATIVE + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + PropertiesConstant.PACKAGE_ENTITY_VO_RELATIVE + ".PageResultVO;");
            bw.newLine();
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.newLine();
            bw.write(BuildComment.buildClassComment(tableInfo.getTableComment() + "业务接口"));
            bw.write("public interface " + tableInfo.getBeanName() + "Service {");
            bw.newLine();
            bw.newLine();
            bw.write(BuildComment.buildMethodComment("根据条件查询列表"));
            bw.write("\tList<"+tableInfo.getBeanName()+"> findListByParam("+tableInfo.getBeanParamName() + " param);");
            bw.newLine();
            bw.newLine();
            bw.write(BuildComment.buildMethodComment("根据条件查询数量"));
            bw.write("\tInteger findCountByParam(" + tableInfo.getBeanParamName() + " param);");
            bw.newLine();
            bw.newLine();
            bw.write(BuildComment.buildMethodComment("分页查询"));
            bw.write("\tPageResultVO<"+tableInfo.getBeanName()+"> findListByPage(" + tableInfo.getBeanParamName() + " param);");
            bw.newLine();
            bw.newLine();

            bw.write(BuildComment.buildMethodComment("新增"));
            bw.write("\tInteger add("+tableInfo.getBeanName()+" bean);");
            bw.newLine();
            bw.newLine();
            bw.write(BuildComment.buildMethodComment("批量新增"));
            bw.write("\tInteger addBatch(List<"+tableInfo.getBeanName()+"> listBean);");
            bw.newLine();
            bw.newLine();

            bw.write(BuildComment.buildMethodComment("批量新增/修改"));
            bw.write("\tInteger addOrUpdateBatch(List<"+tableInfo.getBeanName()+"> listBean);");
            bw.newLine();
            bw.newLine();


            for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                StringBuilder methodName = new StringBuilder();
                StringBuilder comment = new StringBuilder();
                StringBuilder params = new StringBuilder();
                Integer index = 0;
                for (FieldInfo fieldInfo : fieldInfoList) {
                    if(index != 0){
                        params.append(",");
                        comment.append(",");
                        methodName.append("And");
                    }
                    index++;
                    comment.append(fieldInfo.getPropertyName());
                    methodName.append(StringUtils.switchWordFirstLetterToUpper(fieldInfo.getPropertyName()));
                    params.append(fieldInfo.getJavaType() +" " +fieldInfo.getPropertyName());

                }
                bw.write(BuildComment.buildMethodComment("根据" + comment + "查询对象"));
                bw.write("\t"+tableInfo.getBeanName()+" get"+tableInfo.getBeanName()+"By"+methodName+"("+params+");");
                bw.newLine();
                bw.newLine();

                bw.write(BuildComment.buildMethodComment("根据" + comment + "修改"));
                bw.write("\tInteger update"+tableInfo.getBeanName()+"By"+methodName+"("+tableInfo.getBeanName() +" bean, "+params+");");
                bw.newLine();
                bw.newLine();

                bw.write(BuildComment.buildMethodComment("根据" + comment + "删除"));
                bw.write("\tInteger delete"+tableInfo.getBeanName()+"By"+methodName+"("+params+");");
                bw.newLine();
                bw.newLine();
            }



            bw.write("}");
            bw.newLine();



        } catch (IOException e) {
            logger.info("创建service失败：{}",e);
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
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

}
