package com.lxtx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CloudUser implements Serializable {
    /**
     * cloud_user.id
     * @ibatorgenerated 2016-12-12 15:36:55
     */
    private Integer id;

    /**
     * cloud_user.wxid
     * @ibatorgenerated 2016-12-12 15:36:55
     */
    private String wxid;

    /**
     * cloud_user.wxnm (微信昵称)
     * @ibatorgenerated 2016-12-12 15:36:55
     */
    private String wxnm;

    /**
     * cloud_user.balance (可用资金)
     * @ibatorgenerated 2016-12-12 15:36:55
     */
    private BigDecimal balance;

    /**
     * cloud_user.mobile
     * @ibatorgenerated 2016-12-12 15:36:55
     */
    private String mobile;

    /**
     * cloud_user.password
     * @ibatorgenerated 2016-12-12 15:36:55
     */
    private String password;

    /**
     * cloud_user.contract_amount
     * @ibatorgenerated 2016-12-12 15:36:55
     */
    private BigDecimal contractAmount;

    /**
     * cloud_user.headimgurl
     * @ibatorgenerated 2016-12-12 15:36:55
     */
    private String headimgurl;

    /**
     * cloud_user.broker_id (经济人id)
     * @ibatorgenerated 2016-12-12 15:36:55
     */
    private Integer brokerId;

    /**
     * cloud_user.first_visit (是否首次登陆 0：是 1：否)
     * @ibatorgenerated 2016-12-12 15:36:55
     */
    private Integer firstVisit;

    /**
     * cloud_user.chnno
     * @ibatorgenerated 2016-12-12 15:36:55
     */
    private String chnno;

    /**
     * cloud_user.is_subscribe (关注状态 0：取消关注 1：已关注)
     * @ibatorgenerated 2016-12-12 15:36:55
     */
    private Integer isSubscribe;

    /**
     * cloud_user.crt_tm (注册时间)
     * @ibatorgenerated 2016-12-12 15:36:55
     */
    private Date crtTm;

    /**
     * cloud_user.wx_provider_id
     * @ibatorgenerated 2016-12-12 15:36:55
     */
    private Integer wxProviderId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWxid() {
        return wxid;
    }

    public void setWxid(String wxid) {
        this.wxid = wxid;
    }

    public String getWxnm() {
        return wxnm;
    }

    public void setWxnm(String wxnm) {
        this.wxnm = wxnm;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigDecimal getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(BigDecimal contractAmount) {
        this.contractAmount = contractAmount;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public Integer getFirstVisit() {
        return firstVisit;
    }

    public void setFirstVisit(Integer firstVisit) {
        this.firstVisit = firstVisit;
    }

    public String getChnno() {
        return chnno;
    }

    public void setChnno(String chnno) {
        this.chnno = chnno;
    }

    public Integer getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(Integer isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    public Date getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public Integer getWxProviderId() {
        return wxProviderId;
    }

    public void setWxProviderId(Integer wxProviderId) {
        this.wxProviderId = wxProviderId;
    }
}