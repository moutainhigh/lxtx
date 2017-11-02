package com.lxtech.cloud.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.common.base.Splitter;

public class GeneratorConfig {
  private Properties properties;
  
  private static GeneratorConfig config = null;
  
  private GeneratorConfig() {
    this.properties = new Properties();
    try {
      properties.load(GeneratorConfig.class.getResourceAsStream("/generator.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static GeneratorConfig getInstance() {
    if (config == null) {
      config = new GeneratorConfig();
    }
    return config;
  }
  
  public static String get(String property) {
    return GeneratorConfig.getInstance().properties.getProperty(property);
  }
  
  public List<Integer> getHotHours() {
	  String hotHourList = GeneratorConfig.getInstance().get("time.hot");
	  Iterable<String> strList  = Splitter.on(",").split(hotHourList);
	  List<Integer> intList = new ArrayList<Integer>();
	  for (String str : strList) {
		  intList.add(Integer.valueOf(str));
	  }
	  return intList;
  }
  

  public static void main(String[] args) {
	  List<Integer> hourList = GeneratorConfig.getInstance().getHotHours();
	  for (int i : hourList) {
		  System.out.println(i);
	  }
  }
  
}
