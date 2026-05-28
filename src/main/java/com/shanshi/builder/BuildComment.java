package com.shanshi.builder;

import com.shanshi.constant.PropertiesConstant;
import com.shanshi.utils.DateUtils;
import java.util.Date;

/**
 *
 */
public class BuildComment {
    public static String buildClassComment(String classComment){
        return "/**\n" +
                " *@author: "+ PropertiesConstant.PROJECT_AUTHOR + " \n" +
                " *@description: " + classComment + "\n" +
                " *@date: " + DateUtils.formatDate(new Date(),DateUtils.YYYY_MM_DD_OBLIQUE) + "\n" +
                " */\n";
    }
    public static String buildPropertyComment(String propertyComment){
        return "\t/**\n" +
                "\t * " + propertyComment + "\n" +
                "\t */\n";
    }
    public static String buildMethodComment(String methodComment){
        return "\t/**\n" +
                "\t * " + methodComment + " \n" +
                "\t */\n";
    }
}
