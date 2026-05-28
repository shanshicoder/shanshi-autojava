package com.shanshi.builder;

import com.shanshi.bean.FieldInfo;
import com.shanshi.constant.PropertiesConstant;
import com.shanshi.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BuildBaseClass {
    public static Logger logger = LoggerFactory.getLogger(BuildBaseClass.class);

    public static void execute(){
        List<String> importPackages = new ArrayList<>();


        importPackages.add(PropertiesConstant.PACKAGE_ENUMS_RELATIVE);
        build(PropertiesConstant.PATH_ENUMS,"DateTimePatternEnum",importPackages);

        importPackages.clear();
        importPackages.add(PropertiesConstant.PACKAGE_UTILS_RELATIVE);
        build(PropertiesConstant.PATH_UTILS,"DateUtil",importPackages);

        importPackages.clear();
        importPackages.add(PropertiesConstant.PACKAGE_MAPPER_RELATIVE);
        build(PropertiesConstant.PATH_MAPPER,"BaseMapper",importPackages);

        importPackages.clear();
        importPackages.add(PropertiesConstant.PACKAGE_QUERY_RELATIVE);
        build(PropertiesConstant.PATH_QUERY,"BaseQuery",importPackages);

        importPackages.clear();
        importPackages.add(PropertiesConstant.PACKAGE_QUERY_RELATIVE);
        importPackages.add(PropertiesConstant.PACKAGE_ENUMS_RELATIVE + ".PageSizeEnum");
        build(PropertiesConstant.PATH_QUERY,"SimplePage",importPackages);

        importPackages.clear();
        importPackages.add(PropertiesConstant.PACKAGE_ENUMS_RELATIVE);
        build(PropertiesConstant.PATH_ENUMS,"PageSizeEnum",importPackages);

        importPackages.clear();
        importPackages.add(PropertiesConstant.PACKAGE_ENTITY_VO_RELATIVE);
        build(PropertiesConstant.PATH_VO,"PageResultVO",importPackages);

        importPackages.clear();
        importPackages.add(PropertiesConstant.PACKAGE_EXCEPTION_RELATIVE);
        importPackages.add(PropertiesConstant.PACKAGE_ENUMS_RELATIVE+".ResponseCodeEnum");
        build(PropertiesConstant.PATH_EXCEPTION,"BusinessException",importPackages);

        importPackages.clear();
        importPackages.add(PropertiesConstant.PACKAGE_CONTROLLER_RELATIVE);
        importPackages.add(PropertiesConstant.PACKAGE_ENTITY_VO_RELATIVE + ".ResponseVO");
        importPackages.add(PropertiesConstant.PACKAGE_ENUMS_RELATIVE + ".ResponseCodeEnum");
        importPackages.add(PropertiesConstant.PACKAGE_EXCEPTION_RELATIVE + ".BusinessException");
        build(PropertiesConstant.PATH_CONTROLLER,"ABGlobalExceptionHandlerController",importPackages);

        importPackages.clear();
        importPackages.add(PropertiesConstant.PACKAGE_ENTITY_VO_RELATIVE);
        build(PropertiesConstant.PATH_VO,"ResponseVO",importPackages);

        importPackages.clear();
        importPackages.add(PropertiesConstant.PACKAGE_CONTROLLER_RELATIVE);
        importPackages.add(PropertiesConstant.PACKAGE_ENTITY_VO_RELATIVE + ".ResponseVO");
        importPackages.add(PropertiesConstant.PACKAGE_ENUMS_RELATIVE + ".ResponseCodeEnum");
        build(PropertiesConstant.PATH_CONTROLLER,"ABaseController",importPackages);

        importPackages.clear();
        importPackages.add(PropertiesConstant.PACKAGE_ENUMS_RELATIVE);
        build(PropertiesConstant.PATH_ENUMS,"ResponseCodeEnum",importPackages);
    }

    /**
     * 从模板目录当中读取文本文件，构建并输出文件到指定目录
     * @param outputPath 文件的输出路径
     * @param filename 文件名
     * @param importPackages 需要包路径和需要导入的包，列表第一个是包路径，其他为需要导入的包
     */
    public static void build(String outputPath, String filename,List<String> importPackages){
        File directory = new File(outputPath);
        if(!directory.exists()){
            directory.mkdirs();
        }
        File file = new File(outputPath,filename+".java");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        InputStream in = null;
        InputStreamReader inr = null;
        BufferedReader br = null;

        OutputStream out = null;
        OutputStreamWriter outsw = null;
        BufferedWriter bw = null;

        try {
            //  获得模板文件的相对路径
            String templatePath = BuildBaseClass.class.getClassLoader().getResource("template/" + filename + ".txt").getPath();

            in = new FileInputStream(templatePath);
            inr = new InputStreamReader(in,"utf-8");
            br = new BufferedReader(inr);

            out = new FileOutputStream(file);
            outsw = new OutputStreamWriter(out,"utf-8");
            bw = new BufferedWriter(outsw);



            bw.write("package " + importPackages.get(0) + ";\n\n");
//          bw.write("package " + packagePath + ";\n\n");

            for (int i = 1; i < importPackages.size(); i++) {
                bw.write("import " + importPackages.get(i) + ";\n");
            }

            String content;
            while ((content = br.readLine()) != null){
                bw.write(content);
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            logger.error("读取文件出现错误：{}",e);
            throw new RuntimeException(e);
        } finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(inr != null){
                try {
                    inr.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
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
