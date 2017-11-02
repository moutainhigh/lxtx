package com.jxt.netpay.thread;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import com.jxt.netpay.handler.PaymentLogHandler;
import com.jxt.netpay.pojo.PaymentLog;
import com.jxt.netpay.thread.utils.DataUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

public class NotifyService{

	private static Logger logger = Logger.getLogger(NotifyService.class);
	
	private static boolean running = false;
	
	public void exec(){
		if(!running){
			running = true;
			
			realExec();
			
			running = false;
		}
	}
	
	private void realExec(){
		
		//1、从数据库获取待处理列表 dealStatus = 0
		
		//获取dealStatus＝0的所有数据
		List<PaymentLog> list= paymentLogHandler.queryPayByDealStatus(0);
		//2、轮询列表，获取通知地址，httpclient调用
		HttpClient httpclient = new DefaultHttpClient();
		
		if(list!=null&&list.size()>0){
			logger.info(list.size()+" data need to deal");
			for(PaymentLog plog : list){
				//如果地址和orderId都正常，那边就调用接口
				if(StringUtils.hasText(plog.getNotifyUrl()) && plog.getOrderId() != null && plog.getOrderId().length() > 0){
					
					String notifyUrl = URLDecoder.decode(plog.getNotifyUrl());
					
					String url = notifyUrl+"?orderId="+plog.getOrderId()+"&stat="+plog.getStatus()+"&transaction="+plog.getTransactionNumber();
					
					url = url.replace(" ","");
					
					HttpPost httppost = new HttpPost(url);
					
					try {
						HttpResponse response = httpclient.execute(httppost);
						//处理接口返回状态
						String statusCode = DataUtils.convertStreamToString(response.getEntity().getContent());
						
						logger.info("response of "+plog.getOrderId()+" : "+statusCode);
						
						if(DataUtils.strToBoolean(statusCode)){
							plog.setDealStatus(1);
							paymentLogHandler.updateDealStatus(plog);
						}
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}else{
			logger.info("no data need to deal");
		}
		
	}
	
	//
	private PaymentLogHandler paymentLogHandler;

	public void setPaymentLogHandler(PaymentLogHandler paymentLogHandler) {
		this.paymentLogHandler = paymentLogHandler;
	}
}
