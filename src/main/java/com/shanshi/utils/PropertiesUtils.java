package com.shanshi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 读取配置文件
 */
public class PropertiesUtils {
    //  新建一个properties对象
    private static final Properties properties = new Properties();
    private static final Map<String,String> propMap = new ConcurrentHashMap<>();

    static {
        InputStream is = null;
        try {
            is = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");
            properties.load(is);
            //  获取键的集合，使用迭代器遍历
            Iterator<Object> iterator = properties.keySet().iterator();
            while (iterator.hasNext()){
                 String key = (String) iterator.next();
                 propMap.put(key,properties.getProperty(key));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static String getProperty(String key){
        return propMap.get(key);
    }

    public static void main(String[] args) {
        System.out.println(getProperty("db.url"));
    }
}
