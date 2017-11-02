package com.lxtech.model;

import java.io.Serializable;

public class CloudOpenClose implements Serializable {
    /**
     * cloud_open_close.id
     * @ibatorgenerated 2016-11-13 23:33:29
     */
    private Integer id;

    /**
     * cloud_open_close.subject
     * @ibatorgenerated 2016-11-13 23:33:29
     */
    private String subject;

    /**
     * cloud_open_close.open (今开)
     * @ibatorgenerated 2016-11-13 23:33:29
     */
    private Integer open;

    /**
     * cloud_open_close.high (最高)
     * @ibatorgenerated 2016-11-13 23:33:29
     */
    private Integer high;

    /**
     * cloud_open_close.low (最低)
     * @ibatorgenerated 2016-11-13 23:33:29
     */
    private Integer low;

    /**
     * cloud_open_close.last_close (昨收)
     * @ibatorgenerated 2016-11-13 23:33:29
     */
    private Integer lastClose;

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

    public Integer getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
    }

    public Integer getHigh() {
        return high;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public Integer getLow() {
        return low;
    }

    public void setLow(Integer low) {
        this.low = low;
    }

    public Integer getLastClose() {
        return lastClose;
    }

    public void setLastClose(Integer lastClose) {
        this.lastClose = lastClose;
    }
}