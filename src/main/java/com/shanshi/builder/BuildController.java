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

public class BuildController {
    public static final Logger logger = LoggerFactory.getLogger(BuildController.class);
    public static void execute(TableInfo tableInfo){
        File directory = new File(PropertiesConstant.PATH_CONTROLLER);
        if(!directory.exists()){
            directory.mkdirs();
        }
        File file = new File(PropertiesConstant.PATH_CONTROLLER,tableInfo.getBeanName()+"Controller.java");
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


            String serviceBeanName = StringUtils.switchWordFirstLetterToLower(tableInfo.getBeanName()) + "Service";
            String beanName = tableInfo.getBeanName();
            bw.write("package " + PropertiesConstant.PACKAGE_CONTROLLER_RELATIVE + ";");
            bw.newLine();

            bw.write("import " + PropertiesConstant.PACKAGE_PO_RELATIVE + "." + beanName + ";");
            bw.newLine();
            bw.write("import "+PropertiesConstant.PACKAGE_SERVICE_RELATIVE +"."+ beanName + "Service;");
            bw.newLine();
            bw.write("import "+PropertiesConstant.PACKAGE_QUERY_RELATIVE +"."+ beanName + "Query;");
            bw.newLine();
            bw.write("import "+PropertiesConstant.PACKAGE_ENTITY_VO_RELATIVE+".ResponseVO;");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.write("import jakarta.annotation.Resource;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestMapping;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RestController;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestBody;");
            bw.newLine();
            bw.newLine();
            bw.write(BuildComment.buildClassComment(tableInfo.getTableComment() + "控制层"));
            bw.write("@RestController(\""+StringUtils.switchWordFirstLetterToLower(beanName)+"Controller\")");
            bw.newLine();
            bw.write("@RequestMapping(\""+StringUtils.switchWordFirstLetterToLower(beanName)+"\")");
            bw.newLine();
            bw.write("public class " + beanName + "Controller extends ABaseController {");
            bw.newLine();
            bw.newLine();

            bw.write("\t@Resource");
            bw.newLine();
            bw.write("\tprivate " + beanName +"Service "+ serviceBeanName + ";");
            bw.newLine();
            bw.newLine();

            bw.write(BuildComment.buildMethodComment("根据条件分页查询"));
            bw.write("\t@RequestMapping(\"loadDataList\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO loadDataList("+tableInfo.getBeanParamName() + " query){");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO("+serviceBeanName+".findListByPage(query));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();


            bw.write(BuildComment.buildMethodComment("新增"));
            bw.write("\t@RequestMapping(\"add\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO add("+beanName+" bean){");
            bw.newLine();
            bw.write("\t\t"+serviceBeanName+".add(bean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            bw.write(BuildComment.buildMethodComment("批量新增"));
            bw.write("\t@RequestMapping(\"addBatch\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO addBatch(@RequestBody List<"+beanName+"> listBean){");
            bw.newLine();
            bw.write("\t\t"+serviceBeanName+".addBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            bw.write(BuildComment.buildMethodComment("批量新增/修改"));
            bw.write("\t@RequestMapping(\"addOrUpdateBatch\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO addOrUpdateBatch(List<"+beanName+"> listBean){");
            bw.newLine();
            bw.write("\t\t"+serviceBeanName+".addOrUpdateBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();


            for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                StringBuilder methodName = new StringBuilder();
                StringBuilder comment = new StringBuilder();
                StringBuilder params = new StringBuilder();
                StringBuilder trueParams = new StringBuilder();
                Integer index = 0;
                for (FieldInfo fieldInfo : fieldInfoList) {
                    if(index != 0){
                        trueParams.append(",");
                        params.append(",");
                        comment.append(",");
                        methodName.append("And");
                    }
                    index++;
                    comment.append(fieldInfo.getPropertyName());
                    methodName.append(StringUtils.switchWordFirstLetterToUpper(fieldInfo.getPropertyName()));
                    params.append(fieldInfo.getJavaType() +" " +fieldInfo.getPropertyName());
                    trueParams.append(fieldInfo.getPropertyName());
                }
                bw.write(BuildComment.buildMethodComment("根据" + comment + "查询对象"));
                bw.write("\t@RequestMapping(\"get"+tableInfo.getBeanName()+"By"+methodName+"\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO get"+tableInfo.getBeanName()+"By"+methodName+"("+params+"){");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO("+serviceBeanName+".get"+beanName+"By"+methodName+"("+trueParams+"));");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                bw.write(BuildComment.buildMethodComment("根据" + comment + "修改"));
                bw.write("\t@RequestMapping(\"update"+tableInfo.getBeanName()+"By"+methodName+"\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO update"+tableInfo.getBeanName()+"By"+methodName+"("+tableInfo.getBeanName() +" bean, "+params+"){");
                bw.newLine();
                bw.write("\t\t"+serviceBeanName + ".update"+beanName+"By"+methodName+"(bean, "+trueParams+");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                bw.write(BuildComment.buildMethodComment("根据" + comment + "删除"));
                bw.write("\t@RequestMapping(\"delete"+tableInfo.getBeanName()+"By"+methodName+"\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO delete"+tableInfo.getBeanName()+"By"+methodName+"("+params+"){");
                bw.newLine();
                bw.write("\t\t"+serviceBeanName + ".delete"+beanName+"By"+methodName+"("+trueParams+");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("\t}");
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
