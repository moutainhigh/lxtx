package com.lxtx.model;

import java.io.Serializable;

public class CUser implements Serializable {
    /**
     * c_user.id
     * @ibatorgenerated 2016-11-27 18:26:53
     */
    private Integer id;

    /**
     * c_user.u_code (登录名)
     * @ibatorgenerated 2016-11-27 18:26:53
     */
    private String uCode;

    /**
     * c_user.u_name
     * @ibatorgenerated 2016-11-27 18:26:53
     */
    private String uName;

    /**
     * c_user.pwd
     * @ibatorgenerated 2016-11-27 18:26:53
     */
    private String pwd;

    /**
     * c_user.u_type
     * @ibatorgenerated 2016-11-27 18:26:53
     */
    private Integer uType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getuCode() {
        return uCode;
    }

    public void setuCode(String uCode) {
        this.uCode = uCode;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Integer getuType() {
        return uType;
    }

    public void setuType(Integer uType) {
        this.uType = uType;
    }
}