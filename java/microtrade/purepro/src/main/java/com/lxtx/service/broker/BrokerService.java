package com.lxtx.service.broker;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxtx.dao.CloudBrokerMapper;
import com.lxtx.dao.CloudRakeBackMapper;
import com.lxtx.model.CloudBroker;
import com.lxtx.model.CloudRakeBack;
import com.lxtx.util.StringUtil;

@Service
public class BrokerService {
	@Autowired
	private CloudBrokerMapper cloudBrokerMapper;
	@Autowired
	private CloudRakeBackMapper cloudRakeBackMapper;

	public CloudBroker selectBrokerInfoByUserId(Integer brokerId) {
		return cloudBrokerMapper.selectBrokerInfoByUserId(brokerId);
	}

	public void saveBroker(CloudBroker applyBroker) {
		cloudBrokerMapper.insertSelective(applyBroker);
	}

	public List<CloudRakeBack> selectRateBackByDate(Integer brokerId, String start, String end, Integer id) {
		if (StringUtils.isEmpty(start)) {
			start = end = StringUtil.formatDate(new Date(), "yyyy-MM-dd");
		}
		return cloudRakeBackMapper.selectRateBackByDate(brokerId, start, end, id);
	}

}
