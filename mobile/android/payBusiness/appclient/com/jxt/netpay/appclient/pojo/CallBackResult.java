package com.jxt.netpay.appclient.pojo;

/**
 * 支付/操作 结果
 * @author leoliu
 *
 */
public class CallBackResult {

	private boolean succ = false;
	
	private String msg = "";

	private Long traderNo = 0l;

	public CallBackResult(){
		
	}
	
	public CallBackResult(boolean succ,String msg,Long orderId){
		this.succ = succ;
		this.msg = msg;
		this.traderNo = orderId;
	}

	public boolean isSucc() {
		return succ;
	}

	public void setSucc(boolean succ) {
		this.succ = succ;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Long getTradeNo() {
		return traderNo;
	}

	public void setTraderNo(Long orderId) {
		this.traderNo = orderId;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{traderNo:").append(traderNo);
		sb.append(",succ:").append(succ);
		sb.append(",msg:").append(msg).append("}");
		
		return sb.toString();
	}
}
