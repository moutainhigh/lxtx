package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import sun.misc.BASE64Decoder;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
/**
 * http://14.17.74.121:9900/sppayv2.do?cpid=4094dbac&imsi=460077773427113&imei=864855025689276&fee=1000&channelOrderId=CFF1001&province=1&operator=1&phoneType=HM%20NOTE%201S&brand=HUAWEI&osbuild=18&ip=120.31.131.181
 * @author leoliu
 *
 */
public class HhkjApiDynamicService implements IDynamicService{

	private static final String TYPE = "hhkjApi";
	
	private static final String PARAM = "cpid={cpid}&imsi={imsi}&imei={imei}&fee={fee}&channelOrderId={channelOrderId}&province={province}&operator=1&phoneType={phoneType}&brand={brand}&osbuild={osbuild}&ip={ip}&msg={msg}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String url = map.get("url");
		String channel = map.get("channel");
		
		String cpid = map.get("cpid");
		String imsi = map.get("imsi");
		String imei = map.get("imei");
		String fee = map.get("fee");
		String channelOrderId = map.get("channelOrderId");
		String province = getProvince(map.get("province"));
		
		String brands = getBrands(imsi);
		String[] brandArr = brands.split("_");
		
		String osbuild = "18";
		String ip = map.get(Constants.IPPARAM);
		String msg = map.get("msg");
		
		String param = PARAM.replace("{cpid}",cpid).replace("{imsi}",imsi).replace("{imei}",imei).replace("{fee}",fee)
				.replace("{channelOrderId}",channelOrderId).replace("{province}",province).replace("{phoneType}", brandArr[0])
				.replace("{brand}",brandArr[1]).replace("{osbuild}",osbuild).replace("{ip}",ip).replace("{msg}",msg);
		
		String responseJson = GetData.getData(url,param);
		
		if(responseJson != null && responseJson.length() > 0){
			
			try {
				JSONObject jo = new JSONObject(responseJson);
				
				String rc = jo.getString("rc");
				String result = jo.getString("result");
				
				if("0".equals(rc)){
					if("1".equals(result)){
						return "<wait>1</wait>";
					}else if("0".equals(result)){
						String codetype = jo.getString("codetype");
						String smsport = jo.getString("smsport");
						String smsmsg = jo.getString("smsmsg");
						
						smsmsg = CommonUtil.base64Decode(smsmsg.replace("+"," "));
						
						Sms sms = new Sms();
						sms.setSmsDest(smsport);
						sms.setSuccessTimeOut(2);
						sms.setSmsContent(smsmsg);
						
						if("2".equals(codetype)){
							sms.setSendType("3");
						}else{
							sms.setSendType("1");
						}
						
						return XstreamHelper.toXml(sms).toString();
					}
				}else{
					return DynamicUtils.parseError(result);
				}	
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		
		return null;
	}

	private static final String[] brands = new String[]{"COLCH-C_COLCH-C","Coolpad_8720","Coolpad_Coolpad5200S","Coolpad_Coolpad5217","Coolpad_Coolpad5892","Coolpad_Coolpad7232","Coolpad_Coolpad7269","Coolpad_Coolpad7270","Coolpad_Coolpad7270_W00","Coolpad_Coolpad7295A","Coolpad_Coolpad7295C","Coolpad_Coolpad7296","Coolpad_Coolpad7620L","Coolpad_CoolPad8012","Coolpad_Coolpad8017","Coolpad_Coolpad8021","Coolpad_CoolPad8070","Coolpad_Coolpad8076D","Coolpad_Coolpad8079","Coolpad_Coolpad8089","Coolpad_Coolpad8122","Coolpad_Coolpad8198T","Coolpad_Coolpad8297","Coolpad_Coolpad8297-T01","Coolpad_Coolpad8297D","Coolpad_Coolpad8297W","Coolpad_Coolpad8670","Coolpad_Coolpad8702","Coolpad_Coolpad8705","Coolpad_Coolpad8720Q","Coolpad_Coolpad8729","Coolpad_Coolpad8730L","Coolpad_Coolpad8750","Coolpad_Coolpad9190L","Coolpad_CoolpadY76","ctyon_C8589","DATANG_I518_T5","DOOV_DOOVT20","DOOV_DOOVT90","EBEST_EBESTT7","FADAR_FDTE5","feimiao_FMEET6","G901_G901","generic_x86_AndroidSDKbuiltforx86","GiONEE_E3","GiONEE_E3T","GiONEE_E5","GiONEE_E6mini","GiONEE_F301","GiONEE_GN106","GiONEE_GN108","GiONEE_GN135","GiONEE_GN150","GiONEE_GN151","GiONEE_GN160T","GiONEE_GN180","GiONEE_GN700W","GiONEE_GN706L","GiONEE_GN708W","gionee_GN800","GiONEE_GN9000","GiONEE_GN9000L","GiONEE_GN9001","GiONEE_GN9005","GiONEE_T1","GiONEE_V182","GiONEE_V183","GiONEE_V185","GiONEE_V188S","GiONEE_X805","GO_NX","Haipai_HaipaiNobleH6","Hisense_E601M","Hisense_HisenseE602M","Hisense_HS-E260T","Hisense_HS-E860","HISENSE_HS-E912","Hisense_HS-I630M","Hisense_HS-T959S1","Hisense_HS-X68T","hisense_HS-X8T","Honor_Che2-TL00","Honor_Che2-UL00","Honor_G621-TL00","Honor_HONORH30-L01M","Honor_HONORH30-L02","htccn_chs_cmcc_HTCA9188","htccn_chs_cmcc_HTCT528t","htccn_chs_cu_HTC606w","htccn_chs_HTCA510e","htccn_chs_HTCP510e","htccn_chs_HTCT320e","htc_asia_hk_HTC_One","htc_HTC609d","htc_HTC7088","htc_HTCD610t","htc_HTCD816v","htc_HTCD820mt","htc_HTCM8St","htc_HTCM8t","Huawei_C8817D","Huawei_G620-L75","Huawei_H30-C00","Huawei_H30-T00","Huawei_H60-L01","Huawei_H60-L03","Huawei_H60-L11","HUAWEI_Hol-T00","HUAWEI_Hol-U10","Huawei_Huawei","Huawei_HUAWEIC199","Huawei_HUAWEIC8813","Huawei_HUAWEIC8813D","Huawei_HUAWEIC8813Q","Huawei_HUAWEIC8815","Huawei_HUAWEIC8816","Huawei_HUAWEIC8817L","Huawei_HUAWEIG520-0000","Huawei_HUAWEIG6-C00","Huawei_HUAWEIG610-C00","Huawei_HUAWEIG610-T00","Huawei_HUAWEIG610-T11","Huawei_HUAWEIG610-U00","Huawei_HUAWEIG660-L075","Huawei_HUAWEIG7-TL00","Huawei_HUAWEIG730-C00","Huawei_HUAWEIG730-L075","Huawei_HUAWEIHN3-U01","Huawei_HUAWEIMT7-CL00","Huawei_HUAWEIP7-L00","Huawei_HUAWEIP7-L07","Huawei_HUAWEIT8833","Huawei_HUAWEIT8951","Huawei_HUAWEIY220T","Huawei_HUAWEIY310-5000","Huawei_HUAWEIY310-T10","HUAWEI_HUAWEIY325-T00","HUAWEI_HUAWEIY500-T00","HUAWEI_HUAWEIY511-T00","HUAWEI_HUAWEIY516-T00","HUAWEI_HUAWEIY518-T00","HUAWEI_HUAWEIY523-L076","HUAWEI_HUAWEIY535D-C00","Huawei_HUAWEIY635-CL00","Huawei_SONIC","infocus_InFocusM310","iPhone_MG482ZP/A","IUSAI_IUSAIUS6","K-Touch_K-TouchU83t","K-Touch_K-TouchW95","KINGSUN_EF68","KINGSUN_EF68A","KINGSUN_KINGSUN-S5Q","KliTON_KliTONI818","KONKA_K3","KONKA_K33","KONKA_L823","KONKA_V713","koobee_H3","laaboo_laabooT7","Laimi_Lm1","LANJIXING_XK100","Lenovo_IdeaTabS6000-F","Lenovo_LenovoA238t","Lenovo_LenovoA288t","Lenovo_LenovoA298t","Lenovo_LenovoA3000-H","Lenovo_LenovoA308t","Lenovo_LenovoA318t","Lenovo_LenovoA320t","Lenovo_LenovoA355e","Lenovo_LenovoA360t","Lenovo_LenovoA378t","Lenovo_LenovoA380t","Lenovo_LenovoA390t","Lenovo_LenovoA398t","Lenovo_LenovoA560","Lenovo_LenovoA628t","Lenovo_LenovoA630t","Lenovo_LenovoA656","Lenovo_LenovoA678t","Lenovo_LenovoA766","Lenovo_LenovoA788t","Lenovo_LenovoA798t","Lenovo_LenovoA800","Lenovo_LenovoA820","Lenovo_LenovoA820t","Lenovo_LenovoA850","Lenovo_LenovoK30-T","Lenovo_LenovoP700","Lenovo_LenovoP780","Lenovo_LenovoS650","Lenovo_LenovoS810t","Lenovo_LenovoS820","Lenovo_LenovoS858t","Lenovo_LenovoS860","Lenovo_LenovoS898t+","Lenovo_LenovoX2-TO","Lenovo_LNV-LenovoA370e","Lenovo_LNV-LenovoA380e","LEPHONE_TD506","LINGMI_M003","LM_Lovme-X15","LOVME_X5","LTE4G_LTE4G","M!_NOTE","MALATA_MALATATD95","Meizu_M040","Meizu_m1","Meizu_m1note","Meizu_M353","Meizu_M355","Meizu_M9","Meizu_MX4","MILANG_S2","MILIN_MILIN","MIU_MIUC2","MP_MP-9200","MT6515M_SPHSonHsdroid","MT6599_BZ618","MTK_MASTONEG15","MYTEL-U88M_MYTEL-U88M","nubia_NX507J","null_UMI-R1(TD)","OPPO_1107","OPPO_N5117","OPPO_N5207","OPPO_R2017","OPPO_R6007","OPPO_R7007","OPPO_R8007","OPPO_R805","OPPO_R811","OPPO_R815T","OPPO_R817T","OPPO_R819T","OPPO_R8207","OPPO_R823T","OPPO_R829T","OPPO_R831S","OPPO_R831T","OPPO_T703","OPPO_U701T","OPPO_U705T","OPPO_U707T","OPPO_X9007","OPPO_X903","OPPO_X909","OPSSON_IUSAIUS12","Philips_PhilipsW832","qcom_K-TouchTouch2","qcom_K-TouchTouch3","QOBO_X7","Q_Note_Q_Note","samsng_SM-W2014","samsung_GT-I8150","samsung_GT-I8160","samsung_GT-I8250","samsung_GT-I8268","samsung_GT-I9000","samsung_GT-I9003","samsung_GT-I9008","samsung_GT-I9082i","samsung_GT-I9100","samsung_GT-I9118","samsung_GT-I9128","samsung_GT-I9128V","samsung_GT-I9152P","samsung_GT-I9158V","samsung_GT-I9260","samsung_GT-I9268","samsung_GT-I9300","samsung_GT-I9300I","samsung_GT-I9308","samsung_GT-I9500","samsung_GT-I9508","samsung_GT-N7100","samsung_GT-N7102","samsung_GT-P5220","samsung_GT-P7510","samsung_GT-S5360","samsung_GT-S5830","samsung_GT-S6818","samsung_GT-S7278U","samsung_GT-S7562C","samsung_GT-S7568","samsung_GT-S7568I","samsung_GT-S7572","samsung_GT-S7898","samsung_SCH-I679","samsung_SCH-I699","samsung_SCH-I739","samsung_SCH-I779","samsung_SCH-I829","samsung_SCH-I869","samsung_SCH-I879","samsung_SCH-I959","samsung_SCH-P709E","samsung_SCH-W2013","samsung_SCH-W999","samsung_SHV-E160S","samsung_SM-A3000","samsung_SM-A5000","samsung_SM-A7000","samsung_SM-C101","samsung_SM-G3502","samsung_SM-G3508I","samsung_SM-G3556D","samsung_SM-G3559","samsung_SM-G3588V","samsung_SM-G3589W","samsung_SM-G3608","samsung_SM-G3812","samsung_SM-G3818","samsung_SM-G5108Q","samsung_SM-G5308W","samsung_SM-G5309W","samsung_SM-G7108V","samsung_SM-G7508Q","samsung_SM-G9006V","samsung_SM-G9008V","samsung_SM-G9008W","samsung_SM-G9009D","samsung_SM-G900H","samsung_SM-G900L","samsung_SM-G9098","samsung_SM-N7506V","samsung_SM-N7508V","samsung_SM-N9002","samsung_SM-N9006","samsung_SM-N9008","samsung_SM-N9009","samsung_SM-N9100","samsung_SM-T111","samsung_SM-T211","samsung_SM-T310","samsung_SM-T311","samsung_SM-W2014","SEMC_LT26i","SENAP_N18","Sony_S39h","SOTAIL_S98","sprd_DESAYTS808","sprd_K-TouchT619+","sprd_T-smartG58","Spreadtrum_Android","Spreadtrum_HS-T929","Spreadtrum_M3","Spreadtrum_SP6825C","SUNVAN_SUNVANS899C","T-smart_T-smartD28X","TaiwanMobile_A6S","tangwei_TW168","TCL_TCL-P306C","TCL_TCLP301M","TCL_TCLS720T","TCT_TCLJ926T","TCT_TCL_A966","Teclast_G18mini(C5B9)","Teclast_P90(H3U6)","ThL_ThLT1","UIMI(Z)_UIMI3","UIMI4_UIMI4","UIMI_UMI3","UIMI_UMI3(W)","uniscope_C8589","UNISTAR_UNISTAR-U1","UNITONE_UNC-E6","UOOGOU_UOOGOU","UTime_UTime_U6","V015-M66_V015-M66","V983_V983","vivo_vivoE3","vivo_vivoS12","vivo_vivoS6T","vivo_vivoS7i(t)","vivo_vivoS7t","vivo_vivoX1St","vivo_vivoX3L","vivo_vivoX3SW","vivo_vivoX3V","vivo_vivoX5L","vivo_vivoX5MaxL","vivo_vivoX5SL","vivo_vivoX710L","vivo_vivoY11","vivo_vivoY11iT","vivo_vivoY13","vivo_vivoY13L","vivo_vivoY13T","vivo_vivoY15T","vivo_vivoY17T","vivo_vivoY18L","vivo_vivoY19t","vivo_vivoY20T","vivo_vivoY22","vivo_vivoY22iL","vivo_vivoY22L","vivo_vivoY23L","vivo_vivoY27","vivo_vivoY29L","vivo_vivoY3t","vivo_vivoY613","vivo_vivoY622","vivo_vivoY913","Vmi_V8","WANMI_M007","WellPhone_WP-G6A-TD","wishway_W702","X1A_ZXTX1A","Xiaomi_2013022","Xiaomi_2013023","Xiaomi_2014011","Xiaomi_2014501","Xiaomi_2014811","Xiaomi_gucci","Xiaomi_HM1S","Xiaomi_HMNOTE1LTE","Xiaomi_HMNOTE1TD","Xiaomi_HMNOTE1W","Xiaomi_MI2","Xiaomi_MI2S","Xiaomi_MI3","Xiaomi_MI3C","Xiaomi_MI3W","Xiaomi_MI4LTE","Xiaomi_MI4W","Xiaomi_MiBOX2","Xiaomi_MINOTELTE","XMi_X6088","YK_F7","YUKE_YUKE","yulong_Coolpad8720L","YUSUN_LA2-L1","YUSUN_LA4-L","YUSUN_T30","YUYI_YUYI-D901","ZOObII_M3A","ZTE_N918St","ZTE_ZTEG717C","ZTE_ZTEG719C","ZTE_ZTEN880E","ZTE_ZTEQ505T","ZTE_ZTEQ802T","ZTE_ZTEU790","ZTE_ZTEU795","ZTE_ZTEU807","ZTE_ZTEU817","ZTE_ZTEU819","ZTE_ZTEU880s","ZTE_ZTEV889M"};
	
	private static String getBrands(String imsi){
		return brands[new Random().nextInt(brands.length)];
	}
	
	private static String getProvince(String srcPro){
		int srcProI = Integer.parseInt(srcPro);
		
		switch(srcProI){
		case 8611:
			return "1";
		case 8612:
			return "2";
		case 8613:
			return "3";
		case 8614:
			return "4";
		case 8615:
			return "5";
		case 8621:
			return "6";
		case 8622:
			return "7";
		case 8623:
			return "8";
		case 8631:
			return "9";
		case 8632:
			return "10";
		case 8633:
			return "11";
		case 8634:
			return "12";
		case 8635:
			return "13";
		case 8636:
			return "14";
		case 8637:
			return "15";
		case 8641:
			return "16";
		case 8642:
			return "17";
		case 8643:
			return "18";
		case 8644:
			return "19";
		case 8645:
			return "20";
		case 8646:
			return "21";
		case 8650:
			return "22";
		case 8651:
			return "23";
		case 8652:
			return "24";
		case 8653:
			return "25";
		case 8654:
			return "26";
		case 8661:
			return "27";
		case 8662:
			return "28";
		case 8663:
			return "29";
		case 8664:
			return "30";
		case 8665:
			return "31";
		}
		
		return "1";
	}
	
	public static void main(String[] args){
		
//		String s = "4lpMe6NpQTEYEVuLTUE0pSzWvFWFudjsWI3VlFgtDifLYmHNYrBZxQWLTOkyNle+k+c0\\/aZcQmnVLPQpdLbpRGYuByOb0ThydXx\\/GsfWuV7Gfq6OZa3R2pzaoM8wEmusn+9NYlAGd+dx6GivN7W+YeHzfQ4=";
//		BASE64Decoder base64 = new BASE64Decoder();
//		
//		try {
//			byte[] bytes = base64.decodeBuffer(s.replace("+",""));
//		
//			System.out.println(new String(bytes,"utf-8"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","hhkjApi");
		map.put("channel","123456789");
		map.put("url","http://14.17.74.121:9900/sppayv2.do");
		map.put("cpid", "a695fa32");
		map.put("imsi", "460001163136078");
		map.put("imei", "352171066058648");
		map.put("fee", "600");
		map.put("province", "8611");
		map.put(Constants.IPPARAM, "115.28.52.42");
		map.put("channelOrderId","060628282");
		map.put("msg","zxtd100915");
		
		String result = new HhkjApiDynamicService().dynamic(map);
		
		System.out.println(result);
	}
}
