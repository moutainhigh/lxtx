package com.lxtech.cloud.util.algorithm;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.db.CloudOrderHandler;
import com.lxtech.cloud.db.model.CloudOrder;

public class IndexChangeAdjuster {
	private final static Logger logger = LoggerFactory.getLogger(IndexChangeAdjuster.class);
	//BU and AG
	private static final int SMALL_ADJUST_VAL = 2;
	//CU
	private static final int LARGE_ADJUST_VAL = 10;
	
	public static int calculateNewIndex(String subject, int currentIndex) {
		List<CloudOrder> orderList = null;
		try {
			orderList = CloudOrderHandler.getUnprocessedOrderList(subject);
			if (orderList.size() == 0) { //no order, directly return
				return currentIndex;
			}
			
			logger.info("now calculate new index for "+subject+" , order count is:" + orderList.size());
			int adjustSize = getAdjustSize(subject);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return currentIndex;
	}
	
	private static int getAdjustSize(String subject) {
		if (subject.equals("CU")) {
			return LARGE_ADJUST_VAL;
		} else {
			return SMALL_ADJUST_VAL;
		}
	}
}

