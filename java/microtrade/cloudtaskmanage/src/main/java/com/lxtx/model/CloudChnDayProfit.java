package com.lxtx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CloudChnDayProfit implements Serializable {
    /**
     * cloud_chn_day_profit.id
     * @ibatorgenerated 2016-11-27 21:30:59
     */
    private Integer id;

    /**
     * cloud_chn_day_profit.chnno
     * @ibatorgenerated 2016-11-27 21:30:59
     */
    private String chnno;

    /**
     * cloud_chn_day_profit.profit (盈利)
     * @ibatorgenerated 2016-11-27 21:30:59
     */
    private BigDecimal profit;

    /**
     * cloud_chn_day_profit.loss (亏损)
     * @ibatorgenerated 2016-11-27 21:30:59
     */
    private BigDecimal loss;

    /**
     * cloud_chn_day_profit.commission (手续费)
     * @ibatorgenerated 2016-11-27 21:30:59
     */
    private BigDecimal commission;

    /**
     * cloud_chn_day_profit.coupon_commission (用券手续费)
     * @ibatorgenerated 2016-11-27 21:30:59
     */
    private BigDecimal couponCommission;

    /**
     * cloud_chn_day_profit.add_user (新增用户量)
     * @ibatorgenerated 2016-11-27 21:30:59
     */
    private Integer addUser;

    /**
     * cloud_chn_day_profit.order_count (平仓订单数量)
     * @ibatorgenerated 2016-11-27 21:30:59
     */
    private Integer orderCount;

    /**
     * cloud_chn_day_profit.chn_commission (渠道手续费)
     * @ibatorgenerated 2016-11-27 21:30:59
     */
    private BigDecimal chnCommission;

    /**
     * cloud_chn_day_profit.date
     * @ibatorgenerated 2016-11-27 21:30:59
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

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public BigDecimal getLoss() {
        return loss;
    }

    public void setLoss(BigDecimal loss) {
        this.loss = loss;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getCouponCommission() {
        return couponCommission;
    }

    public void setCouponCommission(BigDecimal couponCommission) {
        this.couponCommission = couponCommission;
    }

    public Integer getAddUser() {
        return addUser;
    }

    public void setAddUser(Integer addUser) {
        this.addUser = addUser;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getChnCommission() {
        return chnCommission;
    }

    public void setChnCommission(BigDecimal chnCommission) {
        this.chnCommission = chnCommission;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}