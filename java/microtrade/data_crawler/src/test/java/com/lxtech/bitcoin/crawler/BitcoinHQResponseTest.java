package com.lxtech.bitcoin.crawler;

import org.junit.Test;

import com.lxtech.cloud.bitcoin.WebSocketUtils;

import junit.framework.Assert;

public class BitcoinHQResponseTest {

	@Test
	public void testParseJson() {
		String source = "{\"ch\":\"market.ltccny.detail\",\"ts\":1500895650003,\"tick\":{\"amount\":515658.3714,\"open\":284.89,\"close\":295.29,\"high\":302.70,\"ts\":1500895647000,\"id\":1500895647,\"count\":28843,\"low\":281.20,\"vol\":151572494.238620}}";
		
		WebSocketUtils.BitcoinHQResponse response = WebSocketUtils.BitcoinHQResponse.parseFromJsonSource(source);
		Assert.assertEquals(515658.3714d, response.getAmount());
		Assert.assertEquals(29529, response.getClose());
	}
	
}
