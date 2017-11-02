package com.lxtx.general;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class Test {

	 public  Object jsonReaderTest() {
	        // 这里的Json放到string中，所以加上了转译
	        String jsonSource = "{\"ErrCode\":0,\"Message\":\"OK\",\"Data\":[[\"HistoryID\",\"TradeID\",\"AccountID\",\"OrgID\",\"BrokerID\",\"Direction\",\"Volume\",\"Used\",\"OpenPrice\",\"Point\",\"OpenTime\",\"ClosePrice\",\"CloseType\",\"Fee\",\"Earn\",\"CloseTime\",\"FundVersion\",\"MatchAccountId\",\"ProductId\",\"MarketCode\",\"ProductCode\"],[\"5357481\",\"4674150\",\"308084\",\"1936\",\"116873\",\"S\",\"2\",\"10.00\",\"31419.00\",\"30\",\"2016-10-28 15:24:15\",\"31456.00\",\"3\",\"2.60\",\"-17.40\",\"2016-10-28 15:32:40\",\"13\",\"21047\",\"12\",\"17000\",\"CU\"],[\"5356435\",\"4668210\",\"308084\",\"1936\",\"116873\",\"B\",\"1\",\"10.00\",\"31383.00\",\"30\",\"2016-10-28 15:10:45\",\"31419.00\",\"2\",\"1.30\",\"8.70\",\"2016-10-28 15:23:43\",\"11\",\"21047\",\"12\",\"17000\",\"CU\"],[\"5347074\",\"4665327\",\"308084\",\"1936\",\"116873\",\"S\",\"1\",\"10.00\",\"3266.00\",\"5\",\"2016-10-28 15:02:10\",\"3261.00\",\"2\",\"1.30\",\"8.70\",\"2016-10-28 15:06:08\",\"9\",\"21047\",\"10\",\"17000\",\"BU\"],[\"5332185\",\"4647325\",\"308084\",\"1936\",\"116873\",\"B\",\"2\",\"10.00\",\"31427.00\",\"30\",\"2016-10-28 13:01:49\",\"31390.00\",\"3\",\"2.60\",\"-17.40\",\"2016-10-28 13:43:56\",\"7\",\"21047\",\"12\",\"17000\",\"CU\"],[\"5331782\",\"4647040\",\"308084\",\"1936\",\"116873\",\"B\",\"1\",\"10.00\",\"3275.00\",\"5\",\"2016-10-28 12:56:41\",\"3270.00\",\"3\",\"1.30\",\"-8.70\",\"2016-10-28 13:38:31\",\"6\",\"21047\",\"10\",\"17000\",\"BU\"],[\"5330825\",\"4647108\",\"308084\",\"1936\",\"116873\",\"B\",\"1\",\"10.00\",\"3276.00\",\"5\",\"2016-10-28 12:57:59\",\"3271.00\",\"3\",\"1.30\",\"-8.70\",\"2016-10-28 13:36:24\",\"5\",\"21047\",\"10\",\"17000\",\"BU\"]]}";

	        Gson gson = new Gson();
	        JsonReader reader = new JsonReader(new StringReader(jsonSource));
	        reader.setLenient(true);
	        return gson.fromJson(reader, Object.class);
	 }
	 
	 public static void main(String[] args) {
		 /*Test test = new Test();
		 Object obj = test.jsonReaderTest();
		 Map map = (Map)obj;
		 Object o = map.get("Data");
		 System.out.println(o.toString());*/
		 
		 /*String token = "12121221";
		 String timestamp = "ajflwe";
		 String nouce = "fw9e9fwe9";
		 
		  String[] strArr = {token, timestamp, nouce};
		  Arrays.sort(strArr);
		  for (String str : strArr) {
			  System.out.println(str);
			  
		  }*/
		 
		 String money = "1";
		 double d = (double)(Double.valueOf(money)/100);
		 System.out.println(d);
		 BigDecimal bd = new BigDecimal((double)(Integer.valueOf(money)/100));
		 System.out.println(bd);
	}
}
