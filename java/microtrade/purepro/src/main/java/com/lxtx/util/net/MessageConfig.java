package com.lxtx.util.net;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class MessageConfig {
  private Properties properties;
  
  private static MessageConfig config = null;
  
  private MessageConfig() {
    this.properties = new Properties();
    try {
      //properties.load(MessageConfig.class.getResourceAsStream("/config/message.properties"));
    	properties.load(new InputStreamReader(MessageConfig.class.getResourceAsStream("/config/message.properties"), "UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static MessageConfig getInstance() {
    if (config == null) {
      config = new MessageConfig();
    }
    return config;
  }
  
  public String get(String property) {
    return MessageConfig.getInstance().properties.getProperty(property);
  }

}
