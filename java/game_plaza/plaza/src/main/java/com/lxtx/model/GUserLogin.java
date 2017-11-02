package com.lxtx.model;

import java.io.Serializable;
import java.util.Date;

public class GUserLogin implements Serializable {
    /**
     * g_user_login.id
     * @ibatorgenerated 2017-01-06 13:02:49
     */
    private Integer id;

    /**
     * g_user_login.uid
     * @ibatorgenerated 2017-01-06 13:02:49
     */
    private Integer uid;

    /**
     * g_user_login.cookie
     * @ibatorgenerated 2017-01-06 13:02:49
     */
    private String cookie;

    /**
     * g_user_login.login_time
     * @ibatorgenerated 2017-01-06 13:02:49
     */
    private Date loginTime;

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

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}