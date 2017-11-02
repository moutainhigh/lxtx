package com.lxtech.cloud.db;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.db.model.CloudTargetIndexChange;
import com.lxtech.cloud.db.model.CloudTargetIndexMinute;
import com.lxtech.cloud.main.WebSocketEndpoint;
import com.lxtech.cloud.net.KestrelConnector;
import com.lxtech.cloud.util.JsonUtil;
import com.lxtech.cloud.util.TimeUtil;

public class IndexChangeHandler {
	
	private final static Logger logger = LoggerFactory.getLogger(IndexChangeHandler.class);
	
	private static Map<String, CloudTargetIndexChange> indexMap = new HashMap<String, CloudTargetIndexChange>();

	public static int saveMinuteData(CloudTargetIndexMinute minData) throws SQLException {
		// 将数据源传递给QueryRunner，QueryRunner内部通过数据源获取数据库连接
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into cloud_target_index_minute(`subject`, `dtime`, `idx`, `timeindex`, `day`) values(?,?,?,?,?)";
		Object params[] = {minData.getSubject(), minData.getDtime(), minData.getIdx(), minData.getTimeindex(), minData.getDay() };
		return qr.update(sql, params);
	}
	
	public static synchronized int saveData(CloudTargetIndexChange change) throws SQLException {
		CloudTargetIndexChange savedChange = indexMap.get(change.getSubject());
		if (savedChange != null && change.getTime().getTime() <= savedChange.getTime().getTime()) {//the change already exists
			return -1;
		}
		
		//cache the change
		indexMap.put(change.getSubject(), change);
		
		// 将数据源传递给QueryRunner，QueryRunner内部通过数据源获取数据库连接
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into cloud_target_index_change(`subject`, `time`, `index`) values(?,?,?)";
		Object params[] = { change.getSubject(), change.getTime(), change.getIndex() };
		qr.update(sql, params);
		//update new index
		CloudTargetHandler.updateCloudTargetIndex(change.getSubject(), change.getIndex());
		
		//now check whether the minute data needs to be saved
		if (savedChange != null) {
			Timestamp oldTime = savedChange.getTime();
			Timestamp newTime = change.getTime();
			if((oldTime.getMinutes() != newTime.getMinutes())) {
				//minute time needs to be saved
				CloudTargetIndexMinute minData = new CloudTargetIndexMinute();
				int hours = oldTime.getHours();
				int timeindex = 0;
				String day = "";
				if (hours < 9) { //time between 0 ~ 9 should be recorded as the data for yesterday
					timeindex = 960 + hours * 60 + oldTime.getMinutes();
					day = TimeUtil.getDay(new Date(System.currentTimeMillis() - 86400 * 1000));
				} else {
					timeindex = (hours - 8)*60 + oldTime.getMinutes();
					day = TimeUtil.getDay(new Date(System.currentTimeMillis()));
				}
				
				minData.setTimeindex(timeindex);
				minData.setDay(day);
				minData.setIdx((int)savedChange.getIndex());
				minData.setSubject(savedChange.getSubject());
				minData.setDtime(savedChange.getTime());
				IndexChangeHandler.saveMinuteData(minData);
				
				String template = "Save minute data for %s, timeindex is :%s, index is %s, day is %s";
				logger.info(String.format(template, minData.getSubject(), minData.getTimeindex(), minData.getIdx(), minData.getDay()) );
			}
		}
		
		return 1;
	}
	
	public static void importMinData(String jsonData) throws SQLException {
		Map<String, Object> obj = (Map<String, Object>)JsonUtil.convertStringToObject(jsonData);
		List list= (List)obj.get("Trend");
		String subject = (String)obj.get("Code");
		Date currentDay = TimeUtil.parseDate((String)obj.get("Day"));
		
		for (int i=1; i<list.size(); i++) {
			List dataList = (List)list.get(i);
			//System.out.println(dataList.get(0) + "" + dataList.get(1));
			int timeindex = ((Double)dataList.get(0)).intValue();
			int idx = ((Double)dataList.get(1)).intValue();
			String day = TimeUtil.getDay(new Date());
			CloudTargetIndexMinute minData = new CloudTargetIndexMinute();
			minData.setDay(day);
			minData.setDtime(new Timestamp(currentDay.getTime() + (480 + timeindex)*60*1000));
			minData.setIdx(idx);
			minData.setSubject(subject);
			minData.setTimeindex(timeindex);
			IndexChangeHandler.saveMinuteData(minData);
		}
	}
	
	public static void main(String[] args) {
		/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = sdf.parse("2016-11-10 22:30:00");
			sdf = new SimpleDateFormat("yyyy-M-d H:m:s");
			System.out.println(sdf.format(d));
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
		String source = "{\"Market\":17000,\"Code\":\"BU\",\"PushFlag\":0,\"Day\":\"2016-11-10 0:0:0\",\"PrevClose\":2974,\"PrevSettle\":2944.82,\"Trend\":[[\"Time\",\"Price\"],[60,2967],[61,2966],[62,2967],[65,2966],[67,2965],[71,2966],[72,2965],[73,2966],[76,2967],[77,2969],[78,2968],[80,2967],[81,2966],[82,2967],[83,2966],[94,2967],[95,2968],[97,2969],[100,2970],[101,2968],[102,2969],[103,2968],[104,2966],[107,2965],[108,2962],[110,2961],[112,2960],[113,2959],[114,2960],[116,2958],[117,2959],[118,2958],[119,2960],[123,2961],[124,2960],[125,2961],[126,2962],[127,2960],[128,2961],[130,2962],[132,2961],[134,2960],[135,2961],[136,2960],[139,2959],[140,2960],[142,2961],[143,2960],[147,2959],[148,2960],[149,2963],[150,2962],[159,2963],[160,2962],[161,2963],[162,2964],[164,2963],[166,2964],[167,2962],[168,2963],[169,2962],[172,2964],[173,2966],[177,2965],[179,2963],[180,2965],[181,2966],[183,2967],[184,2966],[185,2965],[186,2964],[188,2965],[190,2966],[191,2967],[193,2968],[195,2969],[196,2970],[197,2969],[198,2970],[199,2968],[200,2969],[203,2971],[206,2972],[207,2970],[209,2972],[210,2970],[213,2971],[215,2970],[216,2971],[218,2972],[220,2971],[222,2970],[224,2968],[225,2969],[227,2970],[230,2971],[231,2970],[232,2971],[234,2972],[238,2971],[239,2972],[243,2970],[244,2969],[246,2968],[248,2967],[249,2969],[250,2968],[255,2967],[256,2966],[257,2967],[259,2968],[261,2967],[263,2968],[264,2967],[265,2968],[269,2969],[271,2970],[273,2969],[274,2970],[275,2971],[277,2972],[278,2971],[280,2970],[283,2968],[285,2969],[286,2970],[291,2968],[294,2966],[295,2968],[299,2967],[300,2968],[301,2967],[302,2968],[305,2969],[307,2970],[310,2972],[314,2971],[315,2972],[317,2974],[320,2975],[323,2977],[324,2976],[326,2977],[328,2976],[330,2978],[331,2977],[336,2979],[337,2978],[338,2977],[340,2978],[341,2979],[343,2978],[344,2979],[346,2981],[348,2983],[353,2984],[354,2985],[355,2983],[356,2985],[357,2987],[358,2985],[360,2984],[361,2983],[364,2981],[365,2982],[366,2985],[367,2984],[368,2983],[369,2982],[370,2980],[372,2982],[373,2983],[375,2985],[376,2989],[377,2987],[378,2989],[379,2987],[380,2989],[381,2988],[382,2989],[384,2988],[385,2989],[388,2987],[389,2988],[390,2990],[391,2989],[394,2987],[397,2988],[398,2987],[400,2989],[404,2990],[405,2991],[406,2992],[407,2993],[409,2994],[410,2993],[412,2995],[413,2992],[416,2993],[420,2994],[421,2993],[422,2992],[423,2993],[425,2992],[426,2994],[427,2993],[428,2994],[429,2993],[434,2994],[436,2993],[438,2995],[440,2996],[441,2995],[442,2997],[443,2996],[444,2994],[445,2995],[447,2994],[449,2995],[452,2999],[455,2997],[456,2999],[460,3000],[462,2999],[463,3001],[468,3002],[470,3001],[471,3000],[472,2999],[473,2998],[474,2997],[475,2999],[476,3001],[477,3002],[478,3002],[479,3000]]}";
		List<String> sourceList = new ArrayList<String>();
//		sourceList.add(source);
		sourceList.add("{\"Market\":17000,\"Code\":\"AG\",\"PushFlag\":0,\"Day\":\"2016-11-10 0:0:0\",\"PrevClose\":3882,\"PrevSettle\":3888,\"Trend\":[[\"Time\",\"Price\"],[60,3909],[61,3913],[62,3912],[65,3911],[66,3908],[67,3911],[69,3915],[70,3912],[71,3915],[72,3918],[73,3923],[74,3925],[75,3923],[76,3929],[78,3927],[79,3930],[80,3933],[81,3929],[82,3927],[83,3924],[84,3929],[85,3926],[86,3925],[88,3924],[89,3927],[90,3929],[92,3927],[94,3929],[95,3922],[97,3925],[98,3926],[100,3927],[101,3923],[104,3924],[105,3923],[106,3925],[107,3927],[108,3931],[109,3933],[110,3934],[111,3931],[112,3930],[113,3929],[114,3930],[116,3929],[118,3930],[119,3929],[120,3926],[122,3925],[126,3924],[129,3925],[131,3926],[133,3927],[134,3929],[135,3925],[137,3924],[138,3923],[139,3924],[141,3925],[142,3926],[144,3927],[150,3930],[151,3931],[152,3932],[153,3929],[154,3927],[155,3931],[158,3935],[159,3934],[160,3932],[161,3934],[165,3936],[166,3938],[167,3937],[169,3935],[170,3937],[172,3936],[174,3937],[175,3935],[176,3933],[178,3931],[179,3930],[180,3927],[181,3929],[182,3927],[183,3933],[184,3934],[186,3936],[189,3935],[190,3937],[191,3939],[192,3938],[193,3939],[195,3941],[196,3940],[197,3941],[198,3942],[199,3943],[201,3940],[203,3941],[204,3939],[205,3941],[207,3940],[209,3939],[210,3940],[211,3941],[212,3942],[213,3943],[215,3942],[216,3943],[218,3941],[219,3940],[220,3941],[221,3939],[222,3937],[224,3938],[225,3937],[226,3938],[227,3936],[228,3937],[229,3935],[230,3938],[231,3936],[232,3937],[234,3936],[236,3934],[237,3937],[239,3936],[241,3934],[242,3935],[245,3936],[246,3937],[247,3935],[248,3938],[249,3937],[250,3936],[251,3935],[253,3932],[255,3930],[257,3931],[258,3930],[259,3929],[260,3927],[261,3925],[263,3926],[264,3927],[265,3926],[266,3925],[268,3927],[269,3929],[271,3930],[272,3929],[273,3930],[275,3927],[276,3925],[277,3926],[279,3925],[281,3924],[282,3922],[283,3924],[284,3922],[286,3923],[289,3924],[290,3925],[291,3926],[292,3925],[294,3923],[295,3924],[299,3926],[300,3925],[302,3924],[303,3926],[304,3927],[306,3926],[307,3929],[308,3926],[310,3925],[312,3924],[313,3925],[314,3926],[320,3929],[321,3930],[322,3929],[323,3926],[326,3929],[327,3927],[328,3929],[329,3930],[330,3934],[331,3935],[333,3934],[334,3935],[335,3933],[337,3936],[338,3935],[339,3936],[340,3938],[341,3937],[343,3936],[344,3937],[345,3938],[346,3944],[347,3946],[348,3949],[349,3951],[350,3954],[351,3949],[352,3951],[353,3954],[354,3952],[355,3950],[357,3952],[358,3954],[360,3949],[361,3950],[363,3951],[364,3950],[365,3951],[366,3950],[367,3951],[368,3954],[374,3953],[375,3954],[376,3957],[377,3961],[379,3957],[380,3956],[381,3954],[384,3953],[385,3952],[386,3949],[387,3950],[388,3951],[390,3948],[391,3949],[392,3946],[393,3952],[394,3953],[396,3956],[398,3957],[399,3956],[400,3957],[401,3956],[402,3955],[404,3952],[405,3951],[406,3952],[407,3955],[408,3954],[409,3958],[410,3960],[411,3963],[412,3960],[414,3961],[415,3960],[416,3961],[417,3964],[418,3963],[419,3966],[420,3961],[421,3963],[422,3964],[423,3960],[424,3966],[425,3967],[426,3972],[427,3968],[428,3970],[429,3969],[430,3973],[431,3976],[432,3974],[433,3975],[434,3977],[435,3971],[436,3969],[437,3970],[438,3969],[439,3966],[440,3963],[442,3967],[443,3963],[444,3964],[445,3963],[448,3962],[449,3961],[450,3964],[451,3962],[452,3961],[453,3960],[454,3961],[455,3963],[457,3964],[460,3962],[461,3961],[462,3960],[463,3958],[464,3955],[466,3954],[470,3955],[472,3953],[473,3956],[474,3957],[475,3958],[476,3957],[477,3958],[478,3959],[479,3959],[480,3959]]}");
		sourceList.add("{\"Market\":17000,\"Code\":\"CU\",\"PushFlag\":0,\"Day\":\"2016-11-10 0:0:0\",\"PrevClose\":35683,\"PrevSettle\":35668,\"Trend\":[[\"Time\",\"Price\"],[60,35625],[61,35596],[62,35610],[63,35596],[64,35589],[65,35596],[66,35581],[67,35618],[68,35806],[69,35980],[70,36046],[71,36111],[73,36154],[74,36278],[75,36459],[76,36691],[77,36937],[78,36901],[79,36908],[80,36800],[81,36807],[82,36720],[83,36705],[84,36669],[85,36647],[86,36684],[87,36647],[88,36676],[89,36669],[90,36727],[91,36756],[92,36742],[93,36720],[94,36742],[95,36785],[96,36843],[97,36945],[98,36923],[99,36887],[100,36865],[101,36778],[102,36807],[103,36800],[104,36763],[105,36756],[106,36771],[107,36785],[108,36771],[109,36763],[110,36785],[111,36763],[112,36778],[113,36792],[114,36800],[115,36778],[116,36763],[117,36771],[118,36713],[119,36698],[121,36713],[123,36720],[124,36727],[125,36720],[126,36742],[127,36720],[128,36713],[129,36720],[130,36698],[132,36705],[133,36691],[134,36676],[135,36597],[136,36611],[138,36618],[139,36655],[140,36720],[141,36698],[142,36655],[143,36633],[144,36604],[145,36633],[149,36647],[150,36640],[153,36618],[154,36597],[155,36589],[156,36597],[157,36582],[158,36575],[159,36560],[161,36546],[162,36423],[163,36444],[164,36473],[165,36481],[166,36495],[167,36423],[168,36452],[169,36423],[170,36444],[171,36481],[172,36488],[173,36546],[174,36510],[175,36524],[176,36517],[180,36524],[182,36495],[183,36502],[184,36517],[185,36502],[186,36488],[188,36473],[189,36452],[190,36473],[191,36481],[192,36466],[193,36502],[194,36473],[195,36481],[196,36473],[198,36502],[199,36488],[200,36481],[201,36495],[204,36502],[206,36488],[208,36524],[209,36502],[211,36495],[212,36510],[215,36517],[216,36502],[218,36495],[219,36481],[220,36473],[222,36459],[223,36473],[224,36488],[226,36495],[227,36502],[228,36495],[233,36517],[235,36510],[236,36524],[237,36517],[240,36524],[241,36502],[243,36495],[245,36502],[246,36517],[247,36524],[249,36517],[250,36510],[251,36546],[253,36531],[255,36539],[256,36531],[258,36553],[259,36546],[260,36539],[261,36524],[263,36531],[265,36539],[267,36531],[268,36510],[274,36502],[275,36524],[278,36546],[279,36553],[280,36546],[282,36553],[283,36568],[284,36575],[288,36582],[289,36589],[290,36604],[291,36568],[292,36560],[293,36546],[297,36539],[300,36553],[301,36560],[303,36582],[309,36575],[310,36582],[311,36575],[312,36582],[314,36604],[317,36597],[318,36611],[322,36618],[323,36640],[324,36662],[325,36669],[326,36698],[327,36720],[328,36705],[329,36698],[330,36734],[331,36763],[332,36734],[333,36698],[334,36705],[335,36713],[336,36720],[337,36705],[338,36713],[339,36720],[340,36713],[341,36720],[342,36713],[344,36749],[345,36763],[347,36800],[348,36843],[349,36821],[350,36836],[352,36829],[353,36843],[354,36821],[355,36800],[357,36814],[358,36807],[359,36785],[360,36778],[361,36792],[362,36800],[363,36785],[364,36807],[365,36829],[366,36814],[367,36807],[369,36814],[370,36800],[371,36807],[373,36800],[374,36792],[375,36785],[377,36778],[379,36785],[380,36792],[381,36785],[382,36800],[383,36814],[384,36807],[385,36814],[386,36807],[387,36814],[388,36800],[390,36821],[391,36872],[393,36850],[394,36836],[395,36850],[396,36843],[398,36858],[399,36879],[400,36894],[401,36879],[402,36887],[403,36865],[404,36843],[405,36865],[406,36858],[407,36850],[408,36836],[410,36850],[412,36858],[413,36843],[414,36865],[415,36843],[416,36836],[418,36829],[419,36850],[420,36836],[421,36858],[422,36865],[423,36872],[424,36879],[426,36872],[427,36865],[428,36858],[430,36850],[431,36821],[432,36814],[433,36821],[434,36829],[435,36865],[436,36872],[437,36858],[438,36843],[439,36850],[440,36836],[442,36843],[443,36836],[445,36821],[446,36800],[447,36771],[448,36720],[449,36698],[450,36684],[451,36691],[452,36676],[453,36684],[454,36720],[455,36713],[456,36720],[458,36800],[459,36792],[460,36771],[461,36742],[462,36727],[463,36713],[467,36756],[468,36814],[469,36800],[470,36807],[471,36814],[472,36829],[473,36923],[474,36959],[475,37314],[476,37336],[477,37322],[478,37416],[479,37510],[480,37561]]}");

		for(String s : sourceList) {
			try {
				IndexChangeHandler.importMinData(s);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}