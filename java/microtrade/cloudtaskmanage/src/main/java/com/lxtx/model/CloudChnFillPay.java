package com.lxtx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CloudChnFillPay implements Serializable {
    /**
     * cloud_chn_fill_pay.id
     * @ibatorgenerated 2016-11-19 17:25:09
     */
    private Integer id;

    /**
     * cloud_chn_fill_pay.chnno
     * @ibatorgenerated 2016-11-19 17:25:09
     */
    private String chnno;

    /**
     * cloud_chn_fill_pay.all_amount (渠道用户总资产)
     * @ibatorgenerated 2016-11-19 17:25:09
     */
    private BigDecimal allAmount;

    /**
     * cloud_chn_fill_pay.fill_amount (时段充值总金额)
     * @ibatorgenerated 2016-11-19 17:25:09
     */
    private BigDecimal fillAmount;

    /**
     * cloud_chn_fill_pay.repay_amount (时段提现总金额)
     * @ibatorgenerated 2016-11-19 17:25:09
     */
    private BigDecimal repayAmount;

    /**
     * cloud_chn_fill_pay.add_user (时段新增用户量)
     * @ibatorgenerated 2016-11-19 17:25:09
     */
    private Integer addUser;

    /**
     * cloud_chn_fill_pay.chn_commission (时段渠道手续费基数)
     * @ibatorgenerated 2016-11-19 17:25:09
     */
    private BigDecimal chnCommission;

    /**
     * cloud_chn_fill_pay.commission
     * @ibatorgenerated 2016-11-19 17:25:09
     */
    private BigDecimal commission;

    /**
     * cloud_chn_fill_pay.order_count
     * @ibatorgenerated 2016-11-19 17:25:09
     */
    private Integer orderCount;

    /**
     * cloud_chn_fill_pay.date
     * @ibatorgenerated 2016-11-19 17:25:09
     */
    private Date date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChnno() {
        return chnno;
    }

    public void setChnno(String chnno) {
        this.chnno = chnno;
    }

    public BigDecimal getAllAmount() {
        return allAmount;
    }

    public void setAllAmount(BigDecimal allAmount) {
        this.allAmount = allAmount;
    }

    public BigDecimal getFillAmount() {
        return fillAmount;
    }

    public void setFillAmount(BigDecimal fillAmount) {
        this.fillAmount = fillAmount;
    }

    public BigDecimal getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(BigDecimal repayAmount) {
        this.repayAmount = repayAmount;
    }

    public Integer getAddUser() {
        return addUser;
    }

    public void setAddUser(Integer addUser) {
        this.addUser = addUser;
    }

    public BigDecimal getChnCommission() {
        return chnCommission;
    }

    public void setChnCommission(BigDecimal chnCommission) {
        this.chnCommission = chnCommission;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}