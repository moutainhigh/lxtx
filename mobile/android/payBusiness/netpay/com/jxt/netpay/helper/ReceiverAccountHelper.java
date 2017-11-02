package com.jxt.netpay.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.InitializingBean;

import com.jxt.netpay.handler.ReceiverAccountHandler;
import com.jxt.netpay.pojo.ReceiverAccount;

public class ReceiverAccountHelper implements InitializingBean{

	public Map<Integer, List<ReceiverAccount>> map = new HashMap<Integer, List<ReceiverAccount>>();
	
	public ReceiverAccount getReceiverAccount(Integer paymentMethod){
		List<ReceiverAccount> list = map.get(paymentMethod);
		
		if(list != null && list.size() > 0){
			int size = list.size();
			
			return list.get(new Random().nextInt(size));
		}
		
		return null;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		new Thread(){
			public void run(){
				while(true){
					try{
						initList();
						
						Thread.sleep(1000 * 60 * 5);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}.start();
		
	}

	private void initList(){
		
		List<ReceiverAccount> list = receiverAccountHandler.listValid();
		
		if(list != null && list.size() > 0){
			 Map<Integer, List<ReceiverAccount>> _map = new HashMap<Integer, List<ReceiverAccount>>();
			
			 for(ReceiverAccount ra : list){
				 
				 List<ReceiverAccount> _list = _map.get(ra.getPaymentMethodId());
				 
				 if(_list == null){
					 _list = new ArrayList<ReceiverAccount>();
					 _map.put(ra.getPaymentMethodId(), _list);
				 }
				 
				 _list.add(ra);
			 }
			 
			 this.map.clear();
			 this.map = _map;
		}
	}
	
	//IOC
	private ReceiverAccountHandler receiverAccountHandler;

	public void setReceiverAccountHandler(
			ReceiverAccountHandler receiverAccountHandler) {
		this.receiverAccountHandler = receiverAccountHandler;
	}
	
}
