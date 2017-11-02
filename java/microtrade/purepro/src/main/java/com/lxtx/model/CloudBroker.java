package com.lxtx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CloudBroker implements Serializable {
    /**
     * cloud_broker.id
     * @ibatorgenerated 2016-11-02 12:00:29
     */
    private Integer id;

    /**
     * cloud_broker.uid
     * @ibatorgenerated 2016-11-02 12:00:29
     */
    private Integer uid;

    /**
     * cloud_broker.name (真是姓名)
     * @ibatorgenerated 2016-11-02 12:00:29
     */
    private String name;

    /**
     * cloud_broker.inst_code (机构编码)
     * @ibatorgenerated 2016-11-02 12:00:29
     */
    private String instCode;

    /**
     * cloud_broker.crt_tm
     * @ibatorgenerated 2016-11-02 12:00:29
     */
    private Date crtTm;

    /**
     * cloud_broker.under_count (直属客户数)
     * @ibatorgenerated 2016-11-02 12:00:29
     */
    private Integer underCount;

    /**
     * cloud_broker.commission (总共赚取的佣金)
     * @ibatorgenerated 2016-11-02 12:00:29
     */
    private BigDecimal commission;

    /**
     * cloud_broker.clear_count (平仓笔数)
     * @ibatorgenerated 2016-11-02 12:00:29
     */
    private Integer clearCount;

    /**
     * cloud_broker.ext
     * @ibatorgenerated 2016-11-02 12:00:29
     */
    private String ext;

    /**
     * cloud_broker.td_code (二维码)
     * @ibatorgenerated 2016-11-02 12:00:29
     */
    private String tdCode;

    //手机号
    private String mobile;
    
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstCode() {
        return instCode;
    }

    public void setInstCode(String instCode) {
        this.instCode = instCode;
    }

    public Date getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public Integer getUnderCount() {
        return underCount;
    }

    public void setUnderCount(Integer underCount) {
        this.underCount = underCount;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public Integer getClearCount() {
        return clearCount;
    }

    public void setClearCount(Integer clearCount) {
        this.clearCount = clearCount;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getTdCode() {
        return tdCode;
    }

    public void setTdCode(String tdCode) {
        this.tdCode = tdCode;
    }

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
    
}