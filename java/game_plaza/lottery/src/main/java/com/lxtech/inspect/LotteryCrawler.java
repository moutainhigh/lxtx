package com.lxtech.inspect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.lxtech.dbmodel.LotteryCqsscData;
import com.lxtech.dbmodel.LotteryOrder;
import com.lxtech.dbmodel.LotteryOrder.LotteryOrderState;
import com.lxtech.dbmodel.LotterySettlementRecord;
import com.lxtech.handler.LotteryCqsscDataHandler;
import com.lxtech.handler.LotteryOrderHandler;
import com.lxtech.handler.LotterySettlementRecordHandler;
import com.lxtech.handler.UserDataHandler;

public class LotteryCrawler {

	private static final int CODE_SUM_COUNT = 10;
	private static final int[] WIN_COUNT_MULTIS = { 0, 2, 3, 4, 8, 12 };// 猜中球的翻倍倍数

	private HttpClient client = new HttpClient();
	private final static Logger logger = LoggerFactory.getLogger(LotteryCrawler.class);

	public LotteryCrawler() {
		client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		client.getParams().setParameter("http.protocol.single-cookie-header", true);
	}

	public static void main(String[] args) {
		new LotteryCrawler().mainLogic();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void mainLogic() {
		String day = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		HttpMethod getxml = new GetMethod(
				"http://baidu.lecai.com/lottery/draw/sorts/ajax_get_draw_data.php?lottery_type=200&date=" + day);
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<LotteryCqsscData> newDatas = new ArrayList<LotteryCqsscData>();
		try {
			getxml.setRequestHeader("Referer",
					"http://baidu.lecai.com/lottery/draw/sorts/cqssc.php?phase=20161226037&agentId=5591");
			client.executeMethod(getxml);
			InputStream in = getxml.getResponseBodyAsStream();
			String content = readFromInputStream(in);
			
			Gson gson = new Gson();
			Map<?, ?> json = gson.fromJson(content, Map.class);
//			System.out.println(json);
//			logger.info(content);
			Map<?, ?> data_1 = (Map<?, ?>) json.get("data");
			List<Map> data_2 = (List<Map>) data_1.get("data");
			for (int i = data_2.size()-1; i >-1; i--) {
				String date_num = (String) data_2.get(i).get("phase");
				String time = (String) data_2.get(i).get("time_draw");
				Map<?, ?> result = (Map<?, ?>) data_2.get(i).get("result");
				List<Map> resultArray = (List<Map>) result.get("result");
				List<String> data_3 = (List<String>) resultArray.get(0).get("data");
				StringBuffer open_code = new StringBuffer();
				
				for(String code:data_3){
					open_code.append(code+",");
				}
				open_code.deleteCharAt(open_code.length()-1);
				
				LotteryCqsscData lcd = new LotteryCqsscData();
				lcd.setDate(Integer.valueOf(date_num.substring(0, 8)));
				lcd.setOpen_time(sdf2.parse(time));
				lcd.setOpen_code(open_code.toString());
				lcd.setSerial_number(Integer.valueOf(date_num.substring(8)));
//				logger.info("num:"+lcd.getSerial_number()+"\tdate:"+lcd.getDate()+"\topen_code:"+lcd.getOpen_code()+"\topen_time:"+lcd.getOpen_time());
				int isSuccess = LotteryCqsscDataHandler.insertORupdate(lcd);
				
				if (isSuccess > 0) {
					newDatas.add(lcd);
				}
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}finally{
			getxml.releaseConnection();
		}
//		this.settlement(newDatas);
	}


	public void settlement(List<LotteryCqsscData> datas) {
		Date currentDate = new Date();
		for (LotteryCqsscData data : datas) {
			try {
				List<LotteryOrder> orders = LotteryOrderHandler.query(data.getDate(), data.getSerial_number(),
						LotteryOrderState.LOS_DEFAULT);
				int orderCount = orders.size();
				if(orderCount <= 0){
					continue;
				}
				int[] winMultis = calculateWinMultis(data.getOpen_code());
				int loseCount = 0;
				long orderMoney = 0;
				long settlementMonty = 0;
				for (LotteryOrder order : orders) {
					logger.info("settlement, id:{}, userid:{}, money:{}", new Object[]{order.getUser_id(), order.getUser_id(), order.getMoney()});
					this.settlementOrder(order, winMultis);
					orderMoney += order.getMoney();
					settlementMonty += order.getSettlement_result();
					if(order.getSettlement_result() > 0){
						loseCount += 1;
					}
				}
				LotterySettlementRecord record = new LotterySettlementRecord();
				record.setData_date(data.getDate());
				record.setData_sn(data.getSerial_number());
				record.setData_open_code(data.getOpen_code());
				record.setOperator_time(currentDate);
				record.setOrder_count(orderCount);;
				record.setWin_count(orderCount - loseCount);
				record.setLose_count(loseCount);
				record.setOrder_money(orderMoney);
				record.setSettlement_money(settlementMonty);
				LotterySettlementRecordHandler.insert(record);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private int[] calculateWinMultis(String codesStr) {
		String[] codes = codesStr.split(",");
		int[] winMultis = new int[CODE_SUM_COUNT];
		for (String codeStr : codes) {
			int code = Integer.parseInt(codeStr.trim());
			winMultis[code]++;
		}
		for (int i = 0; i < winMultis.length; i++) {
			winMultis[i] = WIN_COUNT_MULTIS[winMultis[i]];
		}
		return winMultis;
	}

	private void settlementOrder(LotteryOrder order, int[] winMultis) throws SQLException {
		int settlementResult = order.getMoney() * winMultis[order.getCode()];
		order.setSettlement_result(settlementResult);
		order.setSettlement_time(new Date(System.currentTimeMillis()));
		order.setState(LotteryOrderState.LOS_FINISH.ordinal());
		int successCount = LotteryOrderHandler.updateSettlementResult(order);
		if (settlementResult > 0 && successCount > 0) {
			// update player info
			int amount = settlementResult * 10000 / 100;//settlementResult的单位是分，所以除100，1元兑换金币的倍率是10000
			logger.info("add carry amount, id:{}, amount:{}", order.getUser_id(), amount);
			UserDataHandler.addCarryAmount(order.getUser_id(), amount);
		}
	}

	public String readFromInputStream(InputStream in) {
		String result = null;
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "gbk"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + '\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		result = sb.toString();
		return result;
	}
}
