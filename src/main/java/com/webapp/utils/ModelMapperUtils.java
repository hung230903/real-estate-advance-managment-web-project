package com.webapp.utils;

import java.util.Map;

public class ModelMapperUtils {
    public static <T> T getObject(Map<String, String> maps, String key, Class<T> tClass) {
        Object obj = maps.getOrDefault(key, null);
        if (obj != null) {
            obj = switch (tClass.getTypeName()) {
                case "java.lang.Long" -> obj != "" ? Long.valueOf(obj.toString()) : null;
                case "java.lang.Integer" -> obj != "" ? Integer.valueOf(obj.toString()) : null;
                case "java.lang.String" -> obj != "" ? obj.toString() : null;
                case "java.lang.Double" -> obj != "" ? Double.valueOf(obj.toString()) : null;
                default -> obj;
            };
            return tClass.cast(obj);
        }
        return null;
    }
}
