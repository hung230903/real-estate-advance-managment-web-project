package com.webapp.utils;

import java.util.Map;

public class ModelMapperUtils {
    public static <T> T getObject(Map<String, String> maps, String key, Class<T> tClass) {
        Object obj = maps.getOrDefault(key, null);
        if (obj != null) {
            if (tClass.getTypeName().equals("java.lang.Long")) {
                obj = obj != "" ? Long.valueOf(obj.toString()) : null;
            } else if (tClass.getTypeName().equals("java.lang.Integer")) {
                obj = obj != "" ? Integer.valueOf(obj.toString()) : null;
            } else if (tClass.getTypeName().equals("java.lang.String")) {
                obj = obj != "" ? obj.toString() : null;
            } else if (tClass.getTypeName().equals("java.lang.Double")) {
                obj = obj != "" ? Double.valueOf(obj.toString()) : null;
            }
            return tClass.cast(obj);
        }
        return null;
    }
}
