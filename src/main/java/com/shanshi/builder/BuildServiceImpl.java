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

public class BuildServiceImpl {
    public static final Logger logger = LoggerFactory.getLogger(BuildService.class);

    public static void execute(TableInfo tableInfo){
        File directory = new File(PropertiesConstant.PATH_SERVICE_IMPL);
        if(!directory.exists()){
            directory.mkdirs();
        }
        File file = new File(PropertiesConstant.PATH_SERVICE_IMPL,tableInfo.getBeanName()+"ServiceImpl.java");
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


            String mapperBeanName = StringUtils.switchWordFirstLetterToLower(tableInfo.getBeanName()) + "Mapper";
            String beanName = tableInfo.getBeanName();
            bw.write("package " + PropertiesConstant.PACKAGE_SERVICE_IMPL_RELATIVE + ";");
            bw.newLine();

            bw.write("import " + PropertiesConstant.PACKAGE_PO_RELATIVE + "." + beanName + ";");
            bw.newLine();
            bw.write("import " + PropertiesConstant.PACKAGE_QUERY_RELATIVE + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + PropertiesConstant.PACKAGE_ENTITY_VO_RELATIVE + ".PageResultVO;");
            bw.newLine();
            bw.write("import " + PropertiesConstant.PACKAGE_SERVICE_RELATIVE + "." + beanName +"Service;");
            bw.newLine();
            bw.write("import " + PropertiesConstant.PACKAGE_MAPPER_RELATIVE + "." + StringUtils.switchWordFirstLetterToUpper(mapperBeanName) + ";");
            bw.newLine();
            bw.write("import "+PropertiesConstant.PACKAGE_ENUMS_RELATIVE+".PageSizeEnum;");
            bw.newLine();
            bw.write("import "+PropertiesConstant.PACKAGE_QUERY_RELATIVE+".SimplePage;");
            bw.newLine();
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.write("import org.springframework.stereotype.Service;");
            bw.newLine();
            bw.write("import jakarta.annotation.Resource;");
            bw.newLine();
            bw.newLine();
            bw.write(BuildComment.buildClassComment(tableInfo.getTableComment() + "业务接口实现"));
            bw.write("@Service(\""+StringUtils.switchWordFirstLetterToLower(beanName)+"Service\")");
            bw.newLine();
            bw.write("public class " + beanName + "ServiceImpl implements "+beanName+"Service {");
            bw.newLine();
            bw.newLine();

            bw.write("\t@Resource");
            bw.newLine();
            bw.write("\tprivate " + StringUtils.switchWordFirstLetterToUpper(mapperBeanName) + "<"+beanName+"," + tableInfo.getBeanParamName() + "> " + mapperBeanName + ";");
            bw.newLine();

            bw.write(BuildComment.buildMethodComment("根据条件查询列表"));
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic List<"+beanName+"> findListByParam("+tableInfo.getBeanParamName() + " query){");
            bw.newLine();
            bw.write("\t\treturn this."+mapperBeanName+".selectList(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            bw.write(BuildComment.buildMethodComment("根据条件查询数量"));
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer findCountByParam(" + tableInfo.getBeanParamName() + " query){");
            bw.newLine();
            bw.write("\t\treturn this."+mapperBeanName+".selectCount(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            bw.write(BuildComment.buildMethodComment("分页查询方法"));
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic PageResultVO<"+beanName+"> findListByPage(" + tableInfo.getBeanParamName() + " query){");
            bw.newLine();
            bw.write("\t\tint count = this.findCountByParam(query);");
            bw.newLine();
            bw.write("\t\tint pageSize = query.getPageSize() == null ? PageSizeEnum.SIZE_5.getSize() : query.getPageSize();");
            bw.newLine();
            bw.write("\t\tSimplePage page = new SimplePage(query.getPageNo(),count,pageSize);");
            bw.newLine();
            bw.write("\t\tquery.setSimplePage(page);");
            bw.newLine();
            bw.write("\t\tList<"+tableInfo.getBeanName()+"> list = this.findListByParam(query);");
            bw.newLine();
            bw.write("\t\tPageResultVO<"+tableInfo.getBeanName()+"> result = new PageResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);");
            bw.newLine();
            bw.write("\t\treturn result;");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            bw.write(BuildComment.buildMethodComment("新增"));
            bw.write("\tpublic Integer add("+beanName+" bean){");
            bw.newLine();
            bw.write("\t\treturn this."+mapperBeanName+".insert(bean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            bw.write(BuildComment.buildMethodComment("批量新增"));
            bw.write("\tpublic Integer addBatch(List<"+beanName+"> listBean){");
            bw.newLine();
            bw.write("\t\tif(listBean == null || listBean.isEmpty()){");
            bw.newLine();
            bw.write("\t\t\treturn 0;");
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.write("\t\treturn this."+mapperBeanName+".insertBatch(listBean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            bw.write(BuildComment.buildMethodComment("批量新增/修改"));
            bw.write("\tpublic Integer addOrUpdateBatch(List<"+beanName+"> listBean){");
            bw.newLine();
            bw.write("\t\tif(listBean == null || listBean.isEmpty()){");
            bw.newLine();
            bw.write("\t\t\treturn 0;");
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.write("\t\treturn this."+mapperBeanName+".insertOrUpdateBatch(listBean);");
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
                bw.write("\tpublic "+tableInfo.getBeanName()+" get"+tableInfo.getBeanName()+"By"+methodName+"("+params+"){");
                bw.newLine();
                bw.write("\t\treturn this."+mapperBeanName+".selectBy"+methodName+"("+trueParams+");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                bw.write(BuildComment.buildMethodComment("根据" + comment + "修改"));
                bw.write("\tpublic Integer update"+tableInfo.getBeanName()+"By"+methodName+"("+tableInfo.getBeanName() +" bean, "+params+"){");
                bw.newLine();
                bw.write("\t\treturn this."+mapperBeanName+".updateBy"+methodName+"(bean, "+trueParams+");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                bw.write(BuildComment.buildMethodComment("根据" + comment + "删除"));
                bw.write("\tpublic Integer delete"+tableInfo.getBeanName()+"By"+methodName+"("+params+"){");
                bw.newLine();
                bw.write("\t\treturn this."+mapperBeanName+".deleteBy"+methodName+"("+trueParams+");");
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
