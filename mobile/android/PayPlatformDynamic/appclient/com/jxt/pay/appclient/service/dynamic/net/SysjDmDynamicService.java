package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;

/**
 * @author leoliu
 *
 */
public class SysjDmDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(SysjDmDynamicService.class);
	
	private static final String TYPE = "sysjdm";//手游世纪动漫渠道
	
	private static final String PARAM1 = "partnerId={partnerId}&appId={appId}&channelId={channelId}&appFeeId={appFeeId}&fee={fee}&imsi={imsi}&imei={imei}&os_info={os_info}&os_model={os_model}&net_info={net_info}&extra={extra}&timestamp={timestamp}&client_ip={client_ip}&base64={base64}";
	
	private static final String RESULTCODE_SUCC = "0000";
	
	@Override
	public String getType() {
		return TYPE;
	}


	public static String getDate(SimpleDateFormat formatter) {
		 return formatter.format(new Date());
	}
	
	
	public static String gettime(){
		String time = ""+System.currentTimeMillis()/1000;
		return time;
	}
	
	
	@Override
	/**
	 * url app pid money key 
	 */
	public String dynamic(Map<String, String> map) {
		
		String url = map.get("url");
		
		String xml = null;
		
		if(url != null && url.length() > 0){
			
			
			String partnerId = map.get("partnerId");
			String appId = map.get("appId");
			String channelId = map.get("channelId");
			String imsi = map.get("imsi");		
			String imei = map.get("imei");
			String appFeeId = map.get("appFeeId");
			String fee = map.get("fee");
			String os_info = "4.1";
			String os_model = getBrands(imsi).split("_")[0];
			String net_info = "WIFI";
			String extra  = map.get("extra");
			String timestamp = gettime();
			String client_ip = map.get(Constants.IPPARAM);
			String base64 = map.get("base64");
			
			String param = PARAM1.replace("{partnerId}", partnerId).replace("{appId}", appId).replace("{channelId}", channelId)
					.replace("{imsi}",imsi).replace("{imei}",imei).replace("{appFeeId}",appFeeId).replace("{fee}",fee)
					.replace("{os_info}", os_info).replace("{os_model}", os_model).replace("{net_info}", net_info)
					.replace("{extra}", extra).replace("{timestamp}", timestamp)
					.replace("{client_ip}", client_ip).replace("{base64}", base64);
			
			
			
			String responseJson = GetData.getData(url,param);
//			String responseJson = new PostData().PostData(param.getBytes(), url);
			logger.info("-----"+responseJson);
			xml = parseFirst(map,responseJson);

			if(xml == null){
				xml = DynamicUtils.parseError("598");//获取失败	
			}
		}
		return xml;
	}
	
	private String parseFirst(Map<String, String> map,String responseJson){
		if(responseJson != null && responseJson.length() > 0){
			try{
				int pos = responseJson.indexOf("{");
			
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				
				if(jo.has("resultCode")){
					String resultCode = jo.getString("resultCode");
					
					logger.info("parse first result code : "+resultCode);
					
					if(RESULTCODE_SUCC.equals(resultCode)){
						String type = jo.getString("type");
						
						if("0".equals(type)){
							
							
							String cmd = jo.getString("cmd");
							String port = jo.getString("port");
							
							String smsContent;
							try {
								smsContent = CommonUtil.base64Decode(cmd);					
								
								Sms sms = new Sms();
								sms.setSmsDest(port);
								sms.setSmsContent(smsContent);
								sms.setSuccessTimeOut(2);
								
								return XstreamHelper.toXml(sms).toString();
							} catch (UnsupportedEncodingException e) {
								// TODO 自动生成的 catch 块
								e.printStackTrace();
							} catch (IOException e) {
								// TODO 自动生成的 catch 块
								e.printStackTrace();
							}
						}
						
					}else{
						return DynamicUtils.parseError(resultCode);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private static final String[] brands = new String[]{"COLCH-C_COLCH-C","Coolpad_8720","Coolpad_Coolpad5200S","Coolpad_Coolpad5217","Coolpad_Coolpad5892","Coolpad_Coolpad7232","Coolpad_Coolpad7269","Coolpad_Coolpad7270","Coolpad_Coolpad7270_W00","Coolpad_Coolpad7295A","Coolpad_Coolpad7295C","Coolpad_Coolpad7296","Coolpad_Coolpad7620L","Coolpad_CoolPad8012","Coolpad_Coolpad8017","Coolpad_Coolpad8021","Coolpad_CoolPad8070","Coolpad_Coolpad8076D","Coolpad_Coolpad8079","Coolpad_Coolpad8089","Coolpad_Coolpad8122","Coolpad_Coolpad8198T","Coolpad_Coolpad8297","Coolpad_Coolpad8297-T01","Coolpad_Coolpad8297D","Coolpad_Coolpad8297W","Coolpad_Coolpad8670","Coolpad_Coolpad8702","Coolpad_Coolpad8705","Coolpad_Coolpad8720Q","Coolpad_Coolpad8729","Coolpad_Coolpad8730L","Coolpad_Coolpad8750","Coolpad_Coolpad9190L","Coolpad_CoolpadY76","ctyon_C8589","DATANG_I518_T5","DOOV_DOOVT20","DOOV_DOOVT90","EBEST_EBESTT7","FADAR_FDTE5","feimiao_FMEET6","G901_G901","generic_x86_AndroidSDKbuiltforx86","GiONEE_E3","GiONEE_E3T","GiONEE_E5","GiONEE_E6mini","GiONEE_F301","GiONEE_GN106","GiONEE_GN108","GiONEE_GN135","GiONEE_GN150","GiONEE_GN151","GiONEE_GN160T","GiONEE_GN180","GiONEE_GN700W","GiONEE_GN706L","GiONEE_GN708W","gionee_GN800","GiONEE_GN9000","GiONEE_GN9000L","GiONEE_GN9001","GiONEE_GN9005","GiONEE_T1","GiONEE_V182","GiONEE_V183","GiONEE_V185","GiONEE_V188S","GiONEE_X805","GO_NX","Haipai_HaipaiNobleH6","Hisense_E601M","Hisense_HisenseE602M","Hisense_HS-E260T","Hisense_HS-E860","HISENSE_HS-E912","Hisense_HS-I630M","Hisense_HS-T959S1","Hisense_HS-X68T","hisense_HS-X8T","Honor_Che2-TL00","Honor_Che2-UL00","Honor_G621-TL00","Honor_HONORH30-L01M","Honor_HONORH30-L02","htccn_chs_cmcc_HTCA9188","htccn_chs_cmcc_HTCT528t","htccn_chs_cu_HTC606w","htccn_chs_HTCA510e","htccn_chs_HTCP510e","htccn_chs_HTCT320e","htc_asia_hk_HTC_One","htc_HTC609d","htc_HTC7088","htc_HTCD610t","htc_HTCD816v","htc_HTCD820mt","htc_HTCM8St","htc_HTCM8t","Huawei_C8817D","Huawei_G620-L75","Huawei_H30-C00","Huawei_H30-T00","Huawei_H60-L01","Huawei_H60-L03","Huawei_H60-L11","HUAWEI_Hol-T00","HUAWEI_Hol-U10","Huawei_Huawei","Huawei_HUAWEIC199","Huawei_HUAWEIC8813","Huawei_HUAWEIC8813D","Huawei_HUAWEIC8813Q","Huawei_HUAWEIC8815","Huawei_HUAWEIC8816","Huawei_HUAWEIC8817L","Huawei_HUAWEIG520-0000","Huawei_HUAWEIG6-C00","Huawei_HUAWEIG610-C00","Huawei_HUAWEIG610-T00","Huawei_HUAWEIG610-T11","Huawei_HUAWEIG610-U00","Huawei_HUAWEIG660-L075","Huawei_HUAWEIG7-TL00","Huawei_HUAWEIG730-C00","Huawei_HUAWEIG730-L075","Huawei_HUAWEIHN3-U01","Huawei_HUAWEIMT7-CL00","Huawei_HUAWEIP7-L00","Huawei_HUAWEIP7-L07","Huawei_HUAWEIT8833","Huawei_HUAWEIT8951","Huawei_HUAWEIY220T","Huawei_HUAWEIY310-5000","Huawei_HUAWEIY310-T10","HUAWEI_HUAWEIY325-T00","HUAWEI_HUAWEIY500-T00","HUAWEI_HUAWEIY511-T00","HUAWEI_HUAWEIY516-T00","HUAWEI_HUAWEIY518-T00","HUAWEI_HUAWEIY523-L076","HUAWEI_HUAWEIY535D-C00","Huawei_HUAWEIY635-CL00","Huawei_SONIC","infocus_InFocusM310","iPhone_MG482ZP/A","IUSAI_IUSAIUS6","K-Touch_K-TouchU83t","K-Touch_K-TouchW95","KINGSUN_EF68","KINGSUN_EF68A","KINGSUN_KINGSUN-S5Q","KliTON_KliTONI818","KONKA_K3","KONKA_K33","KONKA_L823","KONKA_V713","koobee_H3","laaboo_laabooT7","Laimi_Lm1","LANJIXING_XK100","Lenovo_IdeaTabS6000-F","Lenovo_LenovoA238t","Lenovo_LenovoA288t","Lenovo_LenovoA298t","Lenovo_LenovoA3000-H","Lenovo_LenovoA308t","Lenovo_LenovoA318t","Lenovo_LenovoA320t","Lenovo_LenovoA355e","Lenovo_LenovoA360t","Lenovo_LenovoA378t","Lenovo_LenovoA380t","Lenovo_LenovoA390t","Lenovo_LenovoA398t","Lenovo_LenovoA560","Lenovo_LenovoA628t","Lenovo_LenovoA630t","Lenovo_LenovoA656","Lenovo_LenovoA678t","Lenovo_LenovoA766","Lenovo_LenovoA788t","Lenovo_LenovoA798t","Lenovo_LenovoA800","Lenovo_LenovoA820","Lenovo_LenovoA820t","Lenovo_LenovoA850","Lenovo_LenovoK30-T","Lenovo_LenovoP700","Lenovo_LenovoP780","Lenovo_LenovoS650","Lenovo_LenovoS810t","Lenovo_LenovoS820","Lenovo_LenovoS858t","Lenovo_LenovoS860","Lenovo_LenovoS898t+","Lenovo_LenovoX2-TO","Lenovo_LNV-LenovoA370e","Lenovo_LNV-LenovoA380e","LEPHONE_TD506","LINGMI_M003","LM_Lovme-X15","LOVME_X5","LTE4G_LTE4G","M!_NOTE","MALATA_MALATATD95","Meizu_M040","Meizu_m1","Meizu_m1note","Meizu_M353","Meizu_M355","Meizu_M9","Meizu_MX4","MILANG_S2","MILIN_MILIN","MIU_MIUC2","MP_MP-9200","MT6515M_SPHSonHsdroid","MT6599_BZ618","MTK_MASTONEG15","MYTEL-U88M_MYTEL-U88M","nubia_NX507J","null_UMI-R1(TD)","OPPO_1107","OPPO_N5117","OPPO_N5207","OPPO_R2017","OPPO_R6007","OPPO_R7007","OPPO_R8007","OPPO_R805","OPPO_R811","OPPO_R815T","OPPO_R817T","OPPO_R819T","OPPO_R8207","OPPO_R823T","OPPO_R829T","OPPO_R831S","OPPO_R831T","OPPO_T703","OPPO_U701T","OPPO_U705T","OPPO_U707T","OPPO_X9007","OPPO_X903","OPPO_X909","OPSSON_IUSAIUS12","Philips_PhilipsW832","qcom_K-TouchTouch2","qcom_K-TouchTouch3","QOBO_X7","Q_Note_Q_Note","samsng_SM-W2014","samsung_GT-I8150","samsung_GT-I8160","samsung_GT-I8250","samsung_GT-I8268","samsung_GT-I9000","samsung_GT-I9003","samsung_GT-I9008","samsung_GT-I9082i","samsung_GT-I9100","samsung_GT-I9118","samsung_GT-I9128","samsung_GT-I9128V","samsung_GT-I9152P","samsung_GT-I9158V","samsung_GT-I9260","samsung_GT-I9268","samsung_GT-I9300","samsung_GT-I9300I","samsung_GT-I9308","samsung_GT-I9500","samsung_GT-I9508","samsung_GT-N7100","samsung_GT-N7102","samsung_GT-P5220","samsung_GT-P7510","samsung_GT-S5360","samsung_GT-S5830","samsung_GT-S6818","samsung_GT-S7278U","samsung_GT-S7562C","samsung_GT-S7568","samsung_GT-S7568I","samsung_GT-S7572","samsung_GT-S7898","samsung_SCH-I679","samsung_SCH-I699","samsung_SCH-I739","samsung_SCH-I779","samsung_SCH-I829","samsung_SCH-I869","samsung_SCH-I879","samsung_SCH-I959","samsung_SCH-P709E","samsung_SCH-W2013","samsung_SCH-W999","samsung_SHV-E160S","samsung_SM-A3000","samsung_SM-A5000","samsung_SM-A7000","samsung_SM-C101","samsung_SM-G3502","samsung_SM-G3508I","samsung_SM-G3556D","samsung_SM-G3559","samsung_SM-G3588V","samsung_SM-G3589W","samsung_SM-G3608","samsung_SM-G3812","samsung_SM-G3818","samsung_SM-G5108Q","samsung_SM-G5308W","samsung_SM-G5309W","samsung_SM-G7108V","samsung_SM-G7508Q","samsung_SM-G9006V","samsung_SM-G9008V","samsung_SM-G9008W","samsung_SM-G9009D","samsung_SM-G900H","samsung_SM-G900L","samsung_SM-G9098","samsung_SM-N7506V","samsung_SM-N7508V","samsung_SM-N9002","samsung_SM-N9006","samsung_SM-N9008","samsung_SM-N9009","samsung_SM-N9100","samsung_SM-T111","samsung_SM-T211","samsung_SM-T310","samsung_SM-T311","samsung_SM-W2014","SEMC_LT26i","SENAP_N18","Sony_S39h","SOTAIL_S98","sprd_DESAYTS808","sprd_K-TouchT619+","sprd_T-smartG58","Spreadtrum_Android","Spreadtrum_HS-T929","Spreadtrum_M3","Spreadtrum_SP6825C","SUNVAN_SUNVANS899C","T-smart_T-smartD28X","TaiwanMobile_A6S","tangwei_TW168","TCL_TCL-P306C","TCL_TCLP301M","TCL_TCLS720T","TCT_TCLJ926T","TCT_TCL_A966","Teclast_G18mini(C5B9)","Teclast_P90(H3U6)","ThL_ThLT1","UIMI(Z)_UIMI3","UIMI4_UIMI4","UIMI_UMI3","UIMI_UMI3(W)","uniscope_C8589","UNISTAR_UNISTAR-U1","UNITONE_UNC-E6","UOOGOU_UOOGOU","UTime_UTime_U6","V015-M66_V015-M66","V983_V983","vivo_vivoE3","vivo_vivoS12","vivo_vivoS6T","vivo_vivoS7i(t)","vivo_vivoS7t","vivo_vivoX1St","vivo_vivoX3L","vivo_vivoX3SW","vivo_vivoX3V","vivo_vivoX5L","vivo_vivoX5MaxL","vivo_vivoX5SL","vivo_vivoX710L","vivo_vivoY11","vivo_vivoY11iT","vivo_vivoY13","vivo_vivoY13L","vivo_vivoY13T","vivo_vivoY15T","vivo_vivoY17T","vivo_vivoY18L","vivo_vivoY19t","vivo_vivoY20T","vivo_vivoY22","vivo_vivoY22iL","vivo_vivoY22L","vivo_vivoY23L","vivo_vivoY27","vivo_vivoY29L","vivo_vivoY3t","vivo_vivoY613","vivo_vivoY622","vivo_vivoY913","Vmi_V8","WANMI_M007","WellPhone_WP-G6A-TD","wishway_W702","X1A_ZXTX1A","Xiaomi_2013022","Xiaomi_2013023","Xiaomi_2014011","Xiaomi_2014501","Xiaomi_2014811","Xiaomi_gucci","Xiaomi_HM1S","Xiaomi_HMNOTE1LTE","Xiaomi_HMNOTE1TD","Xiaomi_HMNOTE1W","Xiaomi_MI2","Xiaomi_MI2S","Xiaomi_MI3","Xiaomi_MI3C","Xiaomi_MI3W","Xiaomi_MI4LTE","Xiaomi_MI4W","Xiaomi_MiBOX2","Xiaomi_MINOTELTE","XMi_X6088","YK_F7","YUKE_YUKE","yulong_Coolpad8720L","YUSUN_LA2-L1","YUSUN_LA4-L","YUSUN_T30","YUYI_YUYI-D901","ZOObII_M3A","ZTE_N918St","ZTE_ZTEG717C","ZTE_ZTEG719C","ZTE_ZTEN880E","ZTE_ZTEQ505T","ZTE_ZTEQ802T","ZTE_ZTEU790","ZTE_ZTEU795","ZTE_ZTEU807","ZTE_ZTEU817","ZTE_ZTEU819","ZTE_ZTEU880s","ZTE_ZTEV889M"};
	
	private static String getBrands(String imsi){
		return brands[new Random().nextInt(brands.length)];
	}
	
	
	public static void main(String[] args){
		test1();
	}

	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();		
		
		map.put("url", "http://pay.sdk.new.5isy.com/center/getCommand.ashx");
		map.put("type","sysjdm");
		map.put("partnerId","2031");
		map.put("appId","2112");
		map.put("channelId", "3017");
		map.put("appFeeId", "2973");
		map.put("fee", "1000");
		map.put("imsi","460001123143655");
		map.put("imei", "867376023133651");
		map.put("extra", "13B101a3124214");
		map.put(Constants.IPPARAM, "123.113.108.47");
		map.put("base64", "1");
		
		logger.info(new SysjDmDynamicService().dynamic(map));
	}

	
}
