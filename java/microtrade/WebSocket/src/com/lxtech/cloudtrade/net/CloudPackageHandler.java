package com.lxtech.cloudtrade.net;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.lxtech.cloudtrade.cache.GlobalCacheUtil;
import com.lxtech.cloudtrade.db.CloudTargetStatHandler;
import com.lxtech.cloudtrade.db.HqDataHandler;
import com.lxtech.cloudtrade.db.model.CloudTargetIndexStat;
import com.lxtech.cloudtrade.db.model.IndexMinuteData;
import com.lxtech.cloudtrade.db.model.KLineData;
import com.lxtech.cloudtrade.util.CrawlerConfig;
import com.lxtech.cloudtrade.util.JsonUtil;
import com.lxtech.cloudtrade.util.TimeUtil;
import com.lxtech.cloudtrade.websocket.WebSocketMessageInbound;

//TODO,replace some hard-coded sections
public class CloudPackageHandler {
	private final static Logger logger = LoggerFactory.getLogger(CloudPackageHandler.class);
//	private int connId;
//	private String name;
	private WebSocketMessageInbound webSocketConn;
	
	private static final String BTC_LABEL = "BTC";
	
	private static final String LTC_LABEL = "LTC";
	
	public CloudPackageHandler(WebSocketMessageInbound webSocketConn) {
		this.webSocketConn = webSocketConn;
	}

	public String getResponsePackage(CloudNetPackage request) {
		CloudPackageHeader header = request.getHeader();
		int msgType = Integer.valueOf(header.getType());
		String id = header.getId(); //using same msgType, id is used to pass parameters, like 5-min kline/15min kline or 30min kline
		//CloudPackageHeader.generateResponseHeader();
		
		String responseBody = getResponseBody(msgType, id, request.getPackageBody());
		String responseHeader = CloudPackageHeader.generateResponseHeader(responseBody.length(), msgType, id);
		String response = responseHeader + CloudNetPackage.PACKAGE_SPLITTER + responseBody;
		logger.debug("for request:"+request.getHeader().getType() + " response is:" + response);
		System.out.println("for request:"+request.getHeader().getType() + " response is:" + response);
		return response;
	}

	private static int getTimes(String target) {
		if (target.equals(BTC_LABEL)) {
			return 1; 
		} else if(target.equals(LTC_LABEL)) {
			return 1;
		} else {
			return 1;
		}
	}
	
	private String getResponseBody(int msgType, String parameter, String requestBody) {
		Map<String, Object> bodyMap = new HashMap<String, Object>();
		
		System.out.println("received request:" + requestBody);
		
		switch (msgType) {
		case CloudPackageType.MSG_TYPE_CRTCkLive:
			bodyMap.put("ConnectionID", this.webSocketConn.getConnId());
			break;

		case CloudPackageType.MSG_TYPE_CRTReqMtInfo:
			//{"Market":[{"Type":0,"BigMarket":17,"Market":17000,"SymbolType":2,"TimeZone":8,"OpenTime":3600,"ShowVolumeUnit":1,"TradeTime":["2016-11-8 0:0:0","2016-11-9 0:0:0","2016-11-10 0:0:0","2016-11-11 0:0:0"],"OpenCloseTime":[{"Open":60,"Close":1200}]}]}			
			List<String> days = TimeUtil.getRecentWorkdays();
			List<Map> marketList = new ArrayList<Map>();
			Map<String, Object> market = new HashMap<String, Object>();
			market.put("Type", "0");
			market.put("BigMarket", 17);
			market.put("Market", 17000);
			market.put("SymbolType", 2);
			market.put("TimeZone", 8);
			market.put("OpenTime", 3600);
			market.put("ShowVolumeUnit", 1);
			market.put("TradeTime", days);
			market.put("OpenCloseTime", new ImmutableList.Builder().add(ImmutableMap.of("Open", 60, "Close", 1200)).build());
			marketList.add(market);
			bodyMap.put("Market", marketList);
			break;
			
		case CloudPackageType.MSG_TYPE_CRTReqRealTimePrice:
			Map requestObj = (Map)JsonUtil.convertStringToObject(requestBody);
			Map<String, CloudTargetIndexStat> statMap = null;
			try {
				statMap = CloudTargetStatHandler.retrieveTargetStats();
			} catch (SQLException e2) {
				logger.error(e2.getMessage());
			}

			String targetCount = CrawlerConfig.getInstance().get("target.count");
			if (Strings.isNullOrEmpty(targetCount)) {
				targetCount = "2";
			}
			int targets = Integer.valueOf(targetCount);
			
			List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
			Map<String, Object> agMap = new HashMap<String, Object>();
			agMap.put("Market", 17000);
			agMap.put("Code", BTC_LABEL);
			agMap.put("TradeCode", BTC_LABEL);
			agMap.put("Name", "比特币");
			agMap.put("LotSize", 10);
			agMap.put("CommodityMarket", -1);
			agMap.put("Time", TimeUtil.getCurTimeGMT());
			CloudTargetIndexStat agStat = statMap.get(BTC_LABEL);
			Integer agIndex = GlobalCacheUtil.getIndexCache().get(this.webSocketConn.getName() + ":" + BTC_LABEL);
			agMap.put("Now", agIndex * getTimes(BTC_LABEL));
			
			agMap.put("Open", agStat.getOpen() * getTimes(BTC_LABEL));
			agMap.put("High", agStat.getHigh() * getTimes(BTC_LABEL));
			agMap.put("Low", agStat.getLow() * getTimes(BTC_LABEL));
			agMap.put("LastClose", agStat.getLast_close() * getTimes(BTC_LABEL));
			agMap.put("LastSettle", 18339 * getTimes(BTC_LABEL));
			mList.add(agMap);
						
			//{"Market":17000,"Code":"CU","TradeCode":"CU","Name":"铜","LotSize":10,"CommodityMarket":-1,"Time":"2016-11-10 7:59:35","Now":37452,"Open":35639,"High":37474,"Low":35574,"LastClose":35683,"LastSettle":35668}
			Map<String, Object> cuMap = new HashMap<String, Object>();
			cuMap.put("Market", 17000);
			cuMap.put("Code", LTC_LABEL);
			cuMap.put("TradeCode", LTC_LABEL);
			cuMap.put("Name", "莱特币");
			cuMap.put("LotSize", 10);
			cuMap.put("CommodityMarket", -1);
			cuMap.put("Time", TimeUtil.getCurTimeGMT());
//			cuMap.put("Now", 37452);
			Integer cuIndex = GlobalCacheUtil.getIndexCache().get(this.webSocketConn.getName() + ":" + LTC_LABEL);
			cuMap.put("Now", cuIndex * getTimes(LTC_LABEL));						
			
			CloudTargetIndexStat cuStat = statMap.get(LTC_LABEL);
			cuMap.put("Open", cuStat.getOpen() * getTimes(LTC_LABEL));
			cuMap.put("High", cuStat.getHigh() * getTimes(LTC_LABEL));
			cuMap.put("Low", cuStat.getLow() * getTimes(LTC_LABEL));
			cuMap.put("LastClose", cuStat.getLast_close() * getTimes(LTC_LABEL));
			cuMap.put("LastSettle", 190 * getTimes(LTC_LABEL));
			mList.add(cuMap);
			
			/*if (targets == 3) {
				Map<String, Object> buMap = new HashMap<String, Object>();
				buMap.put("Market", 17000);
				buMap.put("Code", "BU");
				buMap.put("TradeCode", "BU");
				buMap.put("Name", "沥青");
				buMap.put("LotSize", 10);
				buMap.put("CommodityMarket", -1);
				buMap.put("Time", TimeUtil.getCurTimeGMT());
				Double buIndex = GlobalCacheUtil.getIndexCache().get(this.webSocketConn.getName() + ":BU");
				buMap.put("Now", buIndex);
				
				CloudTargetIndexStat buStat = statMap.get("BU");
				buMap.put("Open", buStat.getOpen());
				buMap.put("High", buStat.getHigh());
				buMap.put("Low", buStat.getLow());
				buMap.put("LastClose", buStat.getLast_close());
				buMap.put("LastSettle", 2944.82);
				mList.add(buMap);	
			}*/
			
			bodyMap.put("Symbol", mList);
			break;
			
		case CloudPackageType.MSG_TYPE_CRTRequestUpdatePush:
			bodyMap.put("Ret", "1");
			break;
		
		case CloudPackageType.MSG_TYPE_CRTReqSbKLine:
			Map klineReqBodyObj = (Map)JsonUtil.convertStringToObject(requestBody);
			int klinetype = ((Double)klineReqBodyObj.get("KLineType")).intValue();
			String sub = (String)klineReqBodyObj.get("Code");
			List<KLineData> kdataList = null;
			try {
				kdataList = HqDataHandler.queryKLineData(this.webSocketConn.getName(), klinetype, sub);
			} catch (SQLException e1) {
				logger.error(e1.getMessage());
			}
			bodyMap.put("Market", 17000);
			bodyMap.put("Code", sub);
			bodyMap.put("PushFlag", 0);
			bodyMap.put("Weight", 0);
			bodyMap.put("KLineType", klinetype);
			bodyMap.put("LastOldTime", "2015-6-24 0:0:0");
			
			List<List> klineDataList = new ArrayList<List>();
			klineDataList.add(ImmutableList.builder().add("Time").add("Open").add("High").add("Low").add("Close").add("Volume").add("Amount").build());
			
			for (int i=kdataList.size() - 1; i>= 0; i--) {
				KLineData data = kdataList.get(i);
				int volume = (int)(Math.random()*3000);
				Timestamp t = data.getTime();
				t = new Timestamp(t.getTime() - 8*3600*1000);
				int times = getTimes(sub);
				klineDataList.add(ImmutableList.builder().add(t).add(data.getOpen()*times).add(data.getHigh()*times).add(data.getLow()*times).add(data.getClose()*times).add(volume).add(0).build());
			}
			
			/*for (KLineData data : kdataList) {
				int volume = (int)(Math.random()*3000);
				klineDataList.add(ImmutableList.builder().add(data.getTime()).add(data.getOpen()).add(data.getHigh()).add(data.getLow()).add(data.getClose()).add(volume).add(0).build());
			}*/
			
			bodyMap.put("KLine", klineDataList);
			break;
		case CloudPackageType.MSG_TYPE_CRTReqTrend:
//			bodyMap.put(key, value);
			Map requestBodyObj = (Map)JsonUtil.convertStringToObject(requestBody);
			try {
				String day = TimeUtil.getMostRecentWorkday(false);
				String subject = (String)requestBodyObj.get("Code");
//				List<IndexMinuteData> dataList = HqDataHandler.queryMinuteData(this.webSocketConn.getName(), day, subject);
				List<IndexMinuteData> dataList = GlobalCacheUtil.getTrendCache().get(this.webSocketConn.getName() + ":" + subject);
				//{"Market":17000,"Code":"BU","PushFlag":0,"Day":"2016-11-4 0:0:0","PrevClose":2974,"PrevSettle":2944.82,"Trend":[["Time","Price"],
				bodyMap.put("Market", 17000);
				bodyMap.put("Code", subject);
				bodyMap.put("PushFlag", 0);
				bodyMap.put("Day", day + " 0:0:0");
				//TODO, we need to calculate prev close data
				bodyMap.put("PrevClose", 2974);
				bodyMap.put("PrevSettle", 2944.82);
				List<List<Object>> trendList = new ArrayList<List<Object>>();
				trendList.add(ImmutableList.of("Time","Price"));
				Map<Integer, List> dataMap = new HashMap<Integer, List>();
				int[] arr = new int[dataList.size()];
				int arrIndex = 0;
				for (IndexMinuteData data: dataList) {
//					trendList.add(ImmutableList.of(data.getTimeindex(), data.getIdx()));
					int times = getTimes(subject);
					dataMap.put(data.getTimeindex(), ImmutableList.of(data.getTimeindex(), data.getIdx() * times));
					arr[arrIndex++] = data.getTimeindex();
				}
				Arrays.sort(arr);
				for (int i = 0; i < arr.length; i++) {
					trendList.add(dataMap.get(arr[i]));
				}
				
				bodyMap.put("Trend", trendList);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		default:
			break;
		}
	
		Gson gson = new Gson();
		return gson.toJson(bodyMap);
	}
	
	public static void main(String[] args) {
		int[] a = {23, 23023 , -12 , 23, 90};
		Arrays.sort(a);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}
}
