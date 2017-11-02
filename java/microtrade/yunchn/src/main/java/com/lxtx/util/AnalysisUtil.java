package com.lxtx.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;

/**
 * @author hecm
 *
 */
public class AnalysisUtil {


    /**
     * jsonToMap
     * 
     * @param String
     * @return
     */
    public static Map<String, Object> parserToMap(JSON s) {
        Map<String, Object> map = new HashMap<String, Object>();
        JSONObject json = JSONObject.fromObject(s);
        Iterator keys = json.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = json.get(key).toString();
            if (value.startsWith("{") && value.endsWith("}")) {
                parserToMap(JSONObject.fromObject(value));
            } else {
                map.put(key.toLowerCase(), value);
            }

        }
        return map;
    }
    public static Map<String, Object> parserToMap(String s) {
        Map<String, Object> map = new HashMap<String, Object>();
        JSONObject json = JSONObject.fromObject(s);
        Iterator keys = json.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = json.get(key).toString();
            if (value.startsWith("{") && value.endsWith("}")) {
                parserToMap(JSONObject.fromObject(value));
            } else {
                map.put(key.toLowerCase(), value);
            }

        }
        return map;
    }


    


    public static void transMap2Bean2(Map<String, Object> map, Object obj) {
        if (map == null || obj == null) {
            return;
        }
        try {
            BeanUtils.populate(obj, map);
        } catch (Exception e) {
            System.out.println("transMap2Bean2 Error " + e);
        }
    }

    // Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean
    public static void transMap2Bean(Map<String, Object> map, Object obj) {

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName().toLowerCase();
                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    if (property.getPropertyType() == Integer.class
                            || property.getPropertyType() == int.class) {
                        Integer tt = Integer.parseInt((String) value);
                        setter.invoke(obj, tt);
                    } else {
                        setter.invoke(obj, value);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("transMap2Bean Error " + e);
        }
        return;
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    public static <T> List<T> json2list(String jsonString, Class<T> pojoClass) {
      JSONArray jsonArray = JSONArray.fromObject(jsonString);
      return JSONArray.toList(jsonArray, pojoClass);
    }

}
