package com.lxtx.model;

import java.io.Serializable;
import java.util.Date;

public class CloudHumanControlConfig implements Serializable {
    /**
     * cloud_human_control_config.id
     * @ibatorgenerated 2017-02-14 15:04:18
     */
    private Integer id;

    /**
     * cloud_human_control_config.status (状态 0：待执行 1：已执行)
     * @ibatorgenerated 2017-02-14 15:04:18
     */
    private Integer status;

    /**
     * cloud_human_control_config.chnno
     * @ibatorgenerated 2017-02-14 15:04:18
     */
    private String chnno;

    /**
     * cloud_human_control_config.order_money (单笔金额:10、100、500)
     * @ibatorgenerated 2017-02-14 15:04:18
     */
    private Integer orderMoney;

    /**
     * cloud_human_control_config.order_num (订单数)
     * @ibatorgenerated 2017-02-14 15:04:18
     */
    private Integer orderNum;

    /**
     * cloud_human_control_config.need_run_count (需执行次数)
     * @ibatorgenerated 2017-02-14 15:04:18
     */
    private Integer needRunCount;

    /**
     * cloud_human_control_config.runed_count (已执行次数)
     * @ibatorgenerated 2017-02-14 15:04:18
     */
    private Integer runedCount;

    /**
     * cloud_human_control_config.uid
     * @ibatorgenerated 2017-02-14 15:04:18
     */
    private Integer uid;

    /**
     * cloud_human_control_config.run_time (执行时间)
     * @ibatorgenerated 2017-02-14 15:04:18
     */
    private Date runTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getChnno() {
        return chnno;
    }

    public void setChnno(String chnno) {
        this.chnno = chnno;
    }

    public Integer getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(Integer orderMoney) {
        this.orderMoney = orderMoney;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getNeedRunCount() {
        return needRunCount;
    }

    public void setNeedRunCount(Integer needRunCount) {
        this.needRunCount = needRunCount;
    }

    public Integer getRunedCount() {
        return runedCount;
    }

    public void setRunedCount(Integer runedCount) {
        this.runedCount = runedCount;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Date getRunTime() {
        return runTime;
    }

    public void setRunTime(Date runTime) {
        this.runTime = runTime;
    }
}