package com.lxtx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CloudRakeBack implements Serializable {
    /**
     * cloud_rake_back.id
     * @ibatorgenerated 2016-11-02 14:54:28
     */
    private Integer id;

    /**
     * cloud_rake_back.uid
     * @ibatorgenerated 2016-11-02 14:54:28
     */
    private Integer uid;

    /**
     * cloud_rake_back.commission (该笔订单手续费)
     * @ibatorgenerated 2016-11-02 14:54:28
     */
    private BigDecimal commission;

    /**
     * cloud_rake_back.broker_commission (返佣金额)
     * @ibatorgenerated 2016-11-02 14:54:28
     */
    private BigDecimal brokerCommission;

    /**
     * cloud_rake_back.wx_nm (用户昵称)
     * @ibatorgenerated 2016-11-02 14:54:28
     */
    private String wxNm;

    /**
     * cloud_rake_back.subjectnm (商品名称)
     * @ibatorgenerated 2016-11-02 14:54:28
     */
    private String subjectnm;

    /**
     * cloud_rake_back.clear_tm (平仓日期)
     * @ibatorgenerated 2016-11-02 14:54:28
     */
    private Date clearTm;

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

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getBrokerCommission() {
        return brokerCommission;
    }

    public void setBrokerCommission(BigDecimal brokerCommission) {
        this.brokerCommission = brokerCommission;
    }

    public String getWxNm() {
        return wxNm;
    }

    public void setWxNm(String wxNm) {
        this.wxNm = wxNm;
    }

    public String getSubjectnm() {
        return subjectnm;
    }

    public void setSubjectnm(String subjectnm) {
        this.subjectnm = subjectnm;
    }

    public Date getClearTm() {
        return clearTm;
    }

    public void setClearTm(Date clearTm) {
        this.clearTm = clearTm;
    }
}