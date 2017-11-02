package com.lxtx.service.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxtx.model.CloudWxServiceProvider;
import com.lxtx.service.user.UserService;

@Service
public class GeneralCache {
	@Autowired
	private UserService userService;
	
	private Map<Integer, CloudWxServiceProvider> wxServiceMap = new HashMap<Integer, CloudWxServiceProvider>();
	
	private Map<String, CloudWxServiceProvider> wxServiceMapByOrigin = new HashMap<String, CloudWxServiceProvider>();
	
	public CloudWxServiceProvider getWxServiceProviderByOrigin(String origin) {
		if (wxServiceMapByOrigin.containsKey(origin)) {
			return wxServiceMapByOrigin.get(origin);
		} else {
			CloudWxServiceProvider serviceProvider = this.userService.getServiceProviderByOrigin(origin);
			if (serviceProvider != null) {
				wxServiceMapByOrigin.put(origin, serviceProvider);
				return serviceProvider;
			} else {
				return null;
			}
		}
	}
	
	public CloudWxServiceProvider getWxServiceProviderById(Integer wxId) {
		if (wxServiceMap.containsKey(wxId)) {
			return wxServiceMap.get(wxId);
		} else {
			CloudWxServiceProvider serviceProvider = this.userService.getServiceProviderById(wxId);
			if (serviceProvider != null) {
				wxServiceMap.put(wxId, serviceProvider);
				return serviceProvider;
			} else {
				return null;
			}
		}
	}
	
	public void clearCache() {
		this.wxServiceMap.clear();
	}
}
