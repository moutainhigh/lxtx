package com.lxtx.model;

import java.io.Serializable;

public class CUserChnRel implements Serializable {
    /**
     * c_user_chn_rel.id
     * @ibatorgenerated 2016-11-27 12:17:37
     */
    private Integer id;

    /**
     * c_user_chn_rel.uid
     * @ibatorgenerated 2016-11-27 12:17:37
     */
    private Integer uid;

    /**
     * c_user_chn_rel.chnno
     * @ibatorgenerated 2016-11-27 12:17:37
     */
    private String chnno;

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

    public String getChnno() {
        return chnno;
    }

    public void setChnno(String chnno) {
        this.chnno = chnno;
    }
}