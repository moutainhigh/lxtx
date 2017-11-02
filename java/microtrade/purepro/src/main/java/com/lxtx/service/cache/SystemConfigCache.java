package com.lxtx.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxtx.dao.CloudSystemConfigMapper;
import com.lxtx.model.CloudSystemConfig;

@Service
public class SystemConfigCache extends SystemCache<String, CloudSystemConfig> {
	//if we support order  
	public static final String ORDER_HIDE_ON = "order.tag.hide.on";
	//use the order index to modulo the step, if the step is 5 and the index of order is 15, then it will be hidden 
	public static final String ORDER_TAG_STEP = "order.tag.step";
	//once accumulated order amount exceeds this amount, the order should be controlled 
	public static final String ORDER_TAG_INITIAL_AMOUNT = "order.tag.initial_amount";
	//once the count of accumulated orders exceeds this threshold, the order should be controlled 
	public static final String ORDER_TAG_INITIAL_ORDERS = "order.tag.initial_orders";
	//the list of channels supporting order hiding
	public static final String ORDER_TAG_CHN_LIST = "order.tag.hide.chnlist";
	
	@Autowired
	private CloudSystemConfigMapper systemConfigMapper;

	public SystemConfigCache() {
		this(10, 60);
	}
	
	public SystemConfigCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	@Override
	public CloudSystemConfig loadFromStore(String k) {
		CloudSystemConfig config = systemConfigMapper.selectByProperty(k);
		if (config == null) {
			config = new CloudSystemConfig();
			config.setProperty(k);
			config.setValue("");
		}
		return config;
	}

}
