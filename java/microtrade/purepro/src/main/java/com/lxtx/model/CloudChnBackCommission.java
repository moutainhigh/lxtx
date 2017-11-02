package com.lxtx.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CloudChnBackCommission implements Serializable {
    /**
     * cloud_chn_back_commission.id
     * @ibatorgenerated 2016-11-13 21:35:01
     */
    private Integer id;

    /**
     * cloud_chn_back_commission.chnno
     * @ibatorgenerated 2016-11-13 21:35:01
     */
    private String chnno;

    /**
     * cloud_chn_back_commission.commission
     * @ibatorgenerated 2016-11-13 21:35:01
     */
    private BigDecimal commission;

    /**
     * cloud_chn_back_commission.user_count (新增用户量)
     * @ibatorgenerated 2016-11-13 21:35:01
     */
    private Integer userCount;

    /**
     * cloud_chn_back_commission.day
     * @ibatorgenerated 2016-11-13 21:35:01
     */
    private Integer day;

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

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }
}