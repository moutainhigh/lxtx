package com.lxtx.general;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lxtx.model.vo.PersonalOrderStat;
import com.lxtx.service.order.OrderService;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试  
@ContextConfiguration
({"/spring-mvc-test.xml"})
public class TestStore {
	@Autowired
	private OrderService orderService;

	@Test
	public void testGetOrderStat() {
		PersonalOrderStat stat = orderService.queryPersonalOrderStat(5809, "BTC");
		System.out.println(stat.getTotal_amount() + " " + stat.getTotal_orders() + " " + stat.getHide_times());
	}
}
