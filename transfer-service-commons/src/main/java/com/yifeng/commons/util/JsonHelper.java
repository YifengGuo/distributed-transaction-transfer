package com.yifeng.commons.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Preconditions;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

/**
 * Created by guoyifeng on 11/8/20
 */
public class JsonHelper {

    public static String toJson(Object obj, SerializerFeature... features) {
        return JSON.toJSONString(obj, features);
    }

    public static <T> T fromJson(String json, Class<T> classOfT, Feature... features) {
        try {
            return JSON.parseObject(json, classOfT, features);
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("Failed to parse json:%s into object:%s. Exception:%s", json, classOfT.getName(), e));
        }
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            return JSON.parseObject(json, typeOfT);
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("Failed to parse json:%s into object:%s. Exception:%s", json, typeOfT.toString(), e));
        }
    }

    public static String getValueByPath(String jsonString, String jsonPath) {
        if (jsonString == null || jsonPath == null) {
            return null;
        }

        String obj;
        try {
            obj = JsonPath.read(jsonString, jsonPath);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }

    public static <T> T getValueByPath(String json, String jsonPath, Class<T> tClass, T defValue) {
        T res = getValueByPath(json, jsonPath, tClass);
        if (res != null) {
            return res;
        } else {
            return defValue;
        }
    }

    public static <T> T getValueByPath(String json, String jsonPath, Class<T> tClass) {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonPath), "jsonPath is blank");
        Preconditions.checkNotNull(tClass, "tClass is blank");

        if (json == null) {
            return null;
        }

        Object obj;
        try {
            DocumentContext context = JsonPath.parse(json);
            obj = context.read(jsonPath);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "failed to eval json[%s], jsonPath[%s], tClass[%s]", json, jsonPath, tClass));
        }
        if (obj == null) {
            return null;
        }
        return tClass.cast(obj);
    }
}
