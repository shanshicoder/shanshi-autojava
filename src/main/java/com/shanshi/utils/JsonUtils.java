package com.shanshi.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtils {
    public static String switchObjectToJson(Object o){
        if(o == null){
            return null;
        }
        String jsonString = JSONObject.toJSONString(o, SerializerFeature.DisableCircularReferenceDetect);
        return jsonString;
    }
}
