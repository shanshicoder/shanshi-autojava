package com.shanshi.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StringUtils {
    public static String switchWordFirstLetterToUpper(String field){
        StringBuilder sb = new StringBuilder();
        if(org.apache.commons.lang3.StringUtils.isEmpty(field)){
            return field;
        }
        sb.append(field.substring(0,1).toUpperCase());
        sb.append(field.substring(1));
        return sb.toString();
    }
    public static String switchWordFirstLetterToLower(String field){
        StringBuilder sb = new StringBuilder();
        if(org.apache.commons.lang3.StringUtils.isEmpty(field)){
            return field;
        }
        sb.append(field.substring(0,1).toLowerCase());
        sb.append(field.substring(1));
        return sb.toString();
    }

//    public static void checkParam(Object param){
//        try {
//            Field[] fields = param.getClass().getDeclaredFields();
//            boolean notEmpty = false;
//            for(Field field : fields){
//                String methodName = "get" + StringUtils.switchWordFirstLetterToUpper(field.getName());
//                Method method = param.getClass().getMethod(methodName);
//                Object object = method.invoke(param);
//                if(
//                        object != null
//                        && object instanceof String
//                        && StringUtils.isEmpty(object.toString())
//                        || object != null
//                        && !(object instanceof String)){
//                    notEmpty = true;
//                    break;
//                }
//            }
//            if(!notEmpty){
//                throw new BusinessException("多参数更新、删除、必须有非空条件");
//            }
//        } catch (BusinessException e) {
//            throw e;
//        } catch (Exception e){
//            e.printStackTrace();
//            throw new BusinessException("校验参数是否为空失败");
//        }
//    }
    public static boolean isEmpty(String str){
        if(str == null || "".equals(str) || "null".equals(str) || "\u0000".equals(str)){
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }

}
