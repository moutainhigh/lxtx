package com.lxtx.util;

import java.io.IOException;
import java.util.Properties;

public class IntegrationConfig {
	  private Properties properties;
	  
	  private static IntegrationConfig config = null;
	  
	  private IntegrationConfig() {
	    this.properties = new Properties();
	    try {
	      properties.load(IntegrationConfig.class.getResourceAsStream("/config/integration.properties"));
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  }
	  
	  public static IntegrationConfig getInstance() {
	    if (config == null) {
	      config = new IntegrationConfig();
	    }
	    return config;
	  }
	  
	  public String get(String property) {
	    return IntegrationConfig.getInstance().properties.getProperty(property);
	  }
	  
	  public static String getRedpacketHost() {
		  return IntegrationConfig.getInstance().get("redpacket.host");
	  }
	  
	  public static String getPlazaEntryHost() {
		  return IntegrationConfig.getInstance().get("plaza.entry.host");
	  }
	  
	  public static String getPlazaBodyHost() {
		  return IntegrationConfig.getInstance().get("plaza.body.host");
	  }	  

	  public static void main(String[] args) {
	    String redpacket_url = IntegrationConfig.getRedpacketHost();
	    System.out.println(redpacket_url);
	  }
	  
}
