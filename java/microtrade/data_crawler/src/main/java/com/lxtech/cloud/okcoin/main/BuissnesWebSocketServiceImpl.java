package com.lxtech.cloud.okcoin.main;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lxtech.cloud.db.IndexChangeHandler;
import com.lxtech.cloud.db.model.CloudTargetIndexChange;
import com.lxtech.cloud.net.KestrelConnector;
import com.lxtech.cloud.okcoin.WebSocketService;
import com.lxtech.cloud.util.GCache;
import com.lxtech.cloud.util.JsonUtil;

/**
 * 订阅信息处理类需要实现WebSocketService接口
 * @author okcoin
 *
 */
public class BuissnesWebSocketServiceImpl implements WebSocketService{
	
	public static final String CHN_LTC = "ok_sub_spotusd_ltc_ticker";
	
	public static final String CHN_BTC = "ok_sub_spotusd_btc_ticker";
	
	private Logger log = Logger.getLogger(BuissnesWebSocketServiceImpl.class);
	
	@Override
	public void onReceive(String msg){
		if (msg.contains("channel") && (msg.contains("ok_sub_spotusd_btc_ticker") || msg.contains("ok_sub_spotusd_ltc_ticker"))) {//the message we're interested in
			saveIndexData(msg);
		}
	}
	
	static class OKcoinHQResponse {
		private String ch;
		
		private String code;
		
		private long close;
		
		private long timestamp;

		public String getCh() {
			return ch;
		}

		public String getCode() {
			return code;
		}

		public long getClose() {
			return close;
		}

		public long getTimestamp() {
			return timestamp;
		}
	}
	
	private void saveIndexData(String str) {
//		KestrelConnector.enqueue(str);
		
		OKcoinHQResponse response = BuissnesWebSocketServiceImpl.parseFromJsonSource(str);
		if (response != null) {
			CloudTargetIndexChange change = new CloudTargetIndexChange();
			change.setSubject(response.getCode());
			change.setTime(new Timestamp(response.getTimestamp()));
			change.setIndex(response.getClose());
			log.info(change.getSubject() + change.getTime() + change.getIndex());
			try {
				IndexChangeHandler.saveData(change);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static OKcoinHQResponse parseFromJsonSource(String str) {
		List<Map<String, Object>> mList = (List<Map<String, Object>>) JsonUtil.convertStringToObject(str);
		Map<String, Object> map = mList.get(0);
		String channel = (String)map.get("channel");
		if (channel.equals(CHN_BTC) || channel.equals(CHN_LTC)) {
			Map<String, Object> dataMap = (Map<String, Object>)map.get("data");
			OKcoinHQResponse response = new OKcoinHQResponse();
			response.ch = channel;
			response.code = response.ch.equals(CHN_LTC) ? "LTC" : "BTC";
			//response.close = (Double)dataMap.get("close");
			if (channel.equals(CHN_LTC)) {
				response.close = (long)(((Double)dataMap.get("close")).doubleValue() * 100);
			} else {
				response.close = ((Double)dataMap.get("close")).longValue();
			}
			response.timestamp = ((Double)dataMap.get("timestamp")).longValue();
			return response;
		}
		return null;
	}	
	
	public static void main(String[] args) {
		String msg = "[{\"data\":{\"high\":288,\"vol\":836052.093,\"last\":270,\"low\":263.5,\"buy\":269.11,\"change\":-3.79,\"sell\":270,\"dayLow\":263.5,\"close\":270,\"dayHigh\":280.66,\"open\":280.65,\"timestamp\":1501309339775},\"channel\":\"ok_sub_spotusd_ltc_ticker\"}]";
		OKcoinHQResponse response = BuissnesWebSocketServiceImpl.parseFromJsonSource(msg);
		System.out.println(response.getCode() + response.getTimestamp() + response.getCh() + response.getClose());
	}
}
