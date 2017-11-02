package com.lxtx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CloudFundHistory implements Serializable {
	/**
	 * cloud_fund_history.id
	 * 
	 * @ibatorgenerated 2016-12-06 18:56:58
	 */
	private Integer id;

	/**
	 * cloud_fund_history.uid
	 * 
	 * @ibatorgenerated 2016-12-06 18:56:58
	 */
	private Integer uid;

	private String wxnm;
	private String chnno;

	/**
	 * cloud_fund_history.amount
	 * 
	 * @ibatorgenerated 2016-12-06 18:56:58
	 */
	private BigDecimal amount;

	/**
	 * cloud_fund_history.time
	 * 
	 * @ibatorgenerated 2016-12-06 18:56:58
	 */
	private Date time;

	/**
	 * cloud_fund_history.wx_trade_no (微信订单号)
	 * 
	 * @ibatorgenerated 2016-12-06 18:56:58
	 */
	private String wxTradeNo;

	/**
	 * cloud_fund_history.status (订单处理结果1表示成功，0表示失败)
	 * 
	 * @ibatorgenerated 2016-12-06 18:56:58
	 */
	private Integer status;

	/**
	 * cloud_fund_history.notify_status
	 * 
	 * @ibatorgenerated 2016-12-06 18:56:58
	 */
	private Integer notifyStatus;

	/**
	 * cloud_fund_history.type (1:充值,2:提现,其他:调)
	 * 
	 * @ibatorgenerated 2016-12-06 18:56:58
	 */
	private Integer type;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (time != null) {
			return sdf.format(time);
		}
		return "";
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getWxTradeNo() {
		return wxTradeNo;
	}

	public void setWxTradeNo(String wxTradeNo) {
		this.wxTradeNo = wxTradeNo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getNotifyStatus() {
		return notifyStatus;
	}

	public void setNotifyStatus(Integer notifyStatus) {
		this.notifyStatus = notifyStatus;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getWxnm() {
		return wxnm;
	}

	public void setWxnm(String wxnm) {
		this.wxnm = wxnm;
	}

	public String getChnno() {
		return chnno;
	}

	public void setChnno(String chnno) {
		this.chnno = chnno;
	}

}