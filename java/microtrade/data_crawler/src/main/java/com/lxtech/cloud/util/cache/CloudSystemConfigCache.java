package com.lxtech.cloud.util.cache;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.db.CloudSystemConfigHandler;
/**
 * 用于获取某标的的行情最高值
 * @author wangwei
 *
 */
public class CloudSystemConfigCache extends SystemCache<String, String>{
	private final static Logger logger = LoggerFactory.getLogger(CloudSystemConfigCache.class);
	
	private static CloudSystemConfigCache configCache = new CloudSystemConfigCache(10, 60);
	
	public CloudSystemConfigCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	@Override
	public String loadFromStore(String k) {
		try {
			String value = CloudSystemConfigHandler.readSystemConfig(k);
			logger.info("load from db."+k+" : "+value);
			return value;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static String getSystemConfig(String key) {
		return configCache.get(key);
	}
	
	public static void main(String[] args) {
//		System.out.println(CloudSystemConfigCache.getSystemConfig("ws.addr"));
//		System.out.println(CloudSystemConfigCache.getSystemConfig("ws.port"));
		System.out.println(CloudSystemConfigCache.getSystemConfig("data.copy.switch"));
	}
}