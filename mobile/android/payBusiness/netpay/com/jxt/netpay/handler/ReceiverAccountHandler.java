package com.jxt.netpay.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jxt.netpay.pojo.ReceiverAccount;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

/**
 * 接收账号处理类
 * @author leoliu
 *
 */
public class ReceiverAccountHandler extends SimpleIbatisEntityHandler<ReceiverAccount>{

	private static final String SQL_GETVALID = "getValid";
	private static final String SQL_LISTVALID = "listValid";
	
	/**
	 * 获取某个合作伙伴某种支付方式的信息
	 * @param partnerId
	 * @param paymentMethodId
	 * @return
	 */
	public ReceiverAccount getValid(Integer paymentMethodId){
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("paymentMethodId",paymentMethodId);
		
		return queryForObject(SQL_GETVALID, map);
	}
	
	public List<ReceiverAccount> listValid(){
		return queryForList(SQL_LISTVALID);
	}
}
