package com.lxtx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CloudFundHistory implements Serializable {
    /**
     * cloud_fund_history.id
     * @ibatorgenerated 2016-11-01 16:03:01
     */
    private Integer id;

    /**
     * cloud_fund_history.uid
     * @ibatorgenerated 2016-11-01 16:03:01
     */
    private Integer uid;

    /**
     * cloud_fund_history.amount
     * @ibatorgenerated 2016-11-01 16:03:01
     */
    private BigDecimal amount;
    private BigDecimal repayAmount;

    /**
     * cloud_fund_history.time
     * @ibatorgenerated 2016-11-01 16:03:01
     */
    private Date time;

    /**
     * cloud_fund_history.wx_trade_no (微信订单号)
     * @ibatorgenerated 2016-11-01 16:03:01
     */
    private String wxTradeNo;

    /**
     * cloud_fund_history.status (订单处理结果1表示成功，0表示失败)
     * @ibatorgenerated 2016-11-01 16:03:01
     */
    private Integer status;

    /**
     * cloud_fund_history.notify_status
     * @ibatorgenerated 2016-11-01 16:03:01
     */
    private Integer notifyStatus;

    /**
     * cloud_fund_history.type (1:充值,2:体现,其他:调)
     * @ibatorgenerated 2016-11-01 16:03:01
     */
    private Integer type;
    
    private String time1;

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

    public Date getTime() {
        return time;
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

	public String getTime1() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (time != null) {
			return sdf.format(time);
		}
		return "";
	}

	public void setTime1(String time1) {
		this.time1 = time1;
	}

	public BigDecimal getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}
    
    
}