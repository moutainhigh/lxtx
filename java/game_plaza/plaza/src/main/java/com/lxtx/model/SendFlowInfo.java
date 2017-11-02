package com.lxtx.model;

import java.io.Serializable;
import java.util.Date;

public class SendFlowInfo implements Serializable {
    /**
     * send_flow_info.id
     * @ibatorgenerated 2017-04-11 11:24:39
     */
    private Integer id;

    /**
     * send_flow_info.mobile
     * @ibatorgenerated 2017-04-11 11:24:39
     */
    private String mobile;

    /**
     * send_flow_info.time
     * @ibatorgenerated 2017-04-11 11:24:39
     */
    private Date time;

    /**
     * send_flow_info.status (0:未送 1:已送)
     * @ibatorgenerated 2017-04-11 11:24:39
     */
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}