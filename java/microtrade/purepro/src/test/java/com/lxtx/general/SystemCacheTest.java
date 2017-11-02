package com.lxtx.general;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.lxtx.dao.CloudSystemConfigMapper;
import com.lxtx.model.CloudOrder;
import com.lxtx.model.CloudSystemConfig;
import com.lxtx.model.CloudUser;
import com.lxtx.service.cache.SystemConfigCache;
import com.lxtx.service.order.OrderService;
import com.lxtx.service.user.UserService;
import com.lxtx.util.Constant;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试  
@ContextConfiguration
({"/spring-mvc-test.xml"})
public class SystemCacheTest {
	@Autowired
	private SystemConfigCache systemConfigCache;
	
	@Autowired
	private CloudSystemConfigMapper systemConfigMapper;
	
	@Autowired
	private OrderService orderService;
	@Autowired
	private UserService userService;
	
	private HttpServletRequest request;
    private HttpSession session;
	
    private CloudUser user;
    
	@Before
	public void before() {
		request = mock(HttpServletRequest.class);
		session  = mock(HttpSession.class);
		
		user = userService.selectByWxid("oHttkwri9Xr4uGHyoM-_fhj4JQbA");
		when(session.getAttribute(Constant.SESSION_USER)).thenReturn(user);
		when(request.getSession()).thenReturn(session);
		
		Cache<String, String> cache = CacheBuilder.newBuilder()
				// 设置cache的初始大小为10，要合理设置该值
				.initialCapacity(10)
				// 设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
				.concurrencyLevel(5)
				// 设置cache中的数据在写入之后的存活时间为10秒
				.expireAfterWrite(1, TimeUnit.SECONDS)
				// 构建cache实例
				.build();
		
		OrderService.setupCache(cache);
	}
	
	@Test
	@Ignore
	public void testSaveAndLoadCache() {
		String property = "order.tag.initial_orders";
		
		CloudSystemConfig config = systemConfigMapper.selectByProperty(property);
		if (config == null) {
			CloudSystemConfig record = new CloudSystemConfig();
			record.setProperty(property);
			record.setValue("10");
			systemConfigMapper.insert(record);
		}
		
		config = systemConfigCache.get(property);
		Assert.assertNotNull(config);
	}
	
	@Test
	@Ignore
	public void testCreateOrders() throws Exception {
		for (int i = 0; i < 100; i++) {
			orderService.createOrder(request, generateRandomOrder(user));
			Thread.sleep(2 * 1000);
		}
	}
	
	private CloudOrder generateRandomOrder(CloudUser user) {
		CloudOrder order = new CloudOrder();
		order.setChnno(user.getChnno());
		order.setUid(user.getId());
		Random rand = new Random();
		order.setOrderIndex(23000 + rand.nextInt(20));
		order.setSubject("BTC");
		order.setOrderTime(new Date());
		order.setMoney(new BigDecimal(100));
		order.setAmount(1);
		order.setLimit(80);
		order.setDirection(1);
		order.setStatus(0);
		
		return order;
	}
	
}