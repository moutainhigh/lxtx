package com.lxtx.model;

import java.io.Serializable;
import java.util.Date;

public class CloudCoupon implements Serializable {
    /**
     * cloud_coupon.id
     * @ibatorgenerated 2016-11-18 15:00:31
     */
    private Integer id;

    /**
     * cloud_coupon.uid
     * @ibatorgenerated 2016-11-18 15:00:31
     */
    private Integer uid;

    /**
     * cloud_coupon.coupon_type (1:现金券)
     * @ibatorgenerated 2016-11-18 15:00:31
     */
    private Integer couponType;

    /**
     * cloud_coupon.status (0:未使用 1:已使用 2:已过期)
     * @ibatorgenerated 2016-11-18 15:00:31
     */
    private Integer status;

    /**
     * cloud_coupon.add_time (生效期)
     * @ibatorgenerated 2016-11-18 15:00:31
     */
    private Date addTime;

    /**
     * cloud_coupon.overdue_time (失效日期)
     * @ibatorgenerated 2016-11-18 15:00:31
     */
    private Date overdueTime;

    /**
     * cloud_coupon.order_id
     * @ibatorgenerated 2016-11-18 15:00:31
     */
    private Integer orderId;

    /**
     * cloud_coupon.coupon_name (券名)
     * @ibatorgenerated 2016-11-18 15:00:31
     */
    private String couponName;

    /**
     * cloud_coupon.coupon_amount (金额)
     * @ibatorgenerated 2016-11-18 15:00:31
     */
    private Integer couponAmount;

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

    public Integer getCouponType() {
        return couponType;
    }

    public void setCouponType(Integer couponType) {
        this.couponType = couponType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getOverdueTime() {
        return overdueTime;
    }

    public void setOverdueTime(Date overdueTime) {
        this.overdueTime = overdueTime;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Integer getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(Integer couponAmount) {
        this.couponAmount = couponAmount;
    }
}