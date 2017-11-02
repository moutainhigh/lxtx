package com.lxtx.general;

import com.lxtx.service.order.LotteryOrderService;

public class LotteryOrderServiceTest {
	public static void main(String[] args){
		long millis = System.currentTimeMillis();
		LotteryOrderService service = new LotteryOrderService();
		service.getPlaceOrderState(true, millis);
		
	}
}
