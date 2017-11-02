package com.lxtx.cloud.pan;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lxtx.dao.CloudOrderMapper;
import com.lxtx.model.CloudOrder;
import com.lxtx.util.Constant;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试  
@ContextConfiguration(   
locations = {
		"classpath*:spring-mvc-test.xml"
})
public class DatabaseModelChangeTest {
	
	@Autowired
	private CloudOrderMapper cloudOrderMapper;
	
	@Test
	public void testListOrders() {
		List<CloudOrder> orderList = cloudOrderMapper.selectByStatus(Constant.ORDER_STAT_TREATING);
		Assert.assertEquals(0, orderList.size());
	}
}
