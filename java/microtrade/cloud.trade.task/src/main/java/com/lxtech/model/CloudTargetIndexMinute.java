package com.lxtech.model;

import java.io.Serializable;
import java.util.Date;

public class CloudTargetIndexMinute implements Serializable {
    /**
     * cloud_target_index_minute.id (标的名称)
     * @ibatorgenerated 2016-11-10 16:45:45
     */
    private Integer id;

    /**
     * cloud_target_index_minute.subject (标的名称，如BU)
     * @ibatorgenerated 2016-11-10 16:45:45
     */
    private String subject;

    /**
     * cloud_target_index_minute.dtime (数据时间)
     * @ibatorgenerated 2016-11-10 16:45:45
     */
    private Date dtime;

    /**
     * cloud_target_index_minute.idx
     * @ibatorgenerated 2016-11-10 16:45:45
     */
    private Integer idx;

    /**
     * cloud_target_index_minute.timeindex (时间偏移量)
     * @ibatorgenerated 2016-11-10 16:45:45
     */
    private Integer timeindex;

    /**
     * cloud_target_index_minute.day
     * @ibatorgenerated 2016-11-10 16:45:45
     */
    private String day;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getDtime() {
        return dtime;
    }

    public void setDtime(Date dtime) {
        this.dtime = dtime;
    }

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    public Integer getTimeindex() {
        return timeindex;
    }

    public void setTimeindex(Integer timeindex) {
        this.timeindex = timeindex;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}