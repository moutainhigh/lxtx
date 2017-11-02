package com.lxtech.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CloudUserLimit implements Serializable {
    /**
     * cloud_user_limit.uid
     * @ibatorgenerated 2016-11-09 14:37:12
     */
    private Integer uid;

    /**
     * cloud_user_limit.day_tran_amount (已交易额度)
     * @ibatorgenerated 2016-11-09 14:37:12
     */
    private BigDecimal dayTranAmount;

    /**
     * cloud_user_limit.day_repay_amount (已提现额度)
     * @ibatorgenerated 2016-11-09 14:37:12
     */
    private BigDecimal dayRepayAmount;

    /**
     * cloud_user_limit.day_repay_count (已提现次数)
     * @ibatorgenerated 2016-11-09 14:37:12
     */
    private Integer dayRepayCount;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public BigDecimal getDayTranAmount() {
        return dayTranAmount;
    }

    public void setDayTranAmount(BigDecimal dayTranAmount) {
        this.dayTranAmount = dayTranAmount;
    }

    public BigDecimal getDayRepayAmount() {
        return dayRepayAmount;
    }

    public void setDayRepayAmount(BigDecimal dayRepayAmount) {
        this.dayRepayAmount = dayRepayAmount;
    }

    public Integer getDayRepayCount() {
        return dayRepayCount;
    }

    public void setDayRepayCount(Integer dayRepayCount) {
        this.dayRepayCount = dayRepayCount;
    }
}