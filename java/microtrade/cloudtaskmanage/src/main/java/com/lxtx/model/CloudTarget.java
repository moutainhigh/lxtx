package com.lxtx.model;

import java.io.Serializable;

public class CloudTarget implements Serializable {
    /**
     * cloud_target.id
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    private Integer id;

    /**
     * cloud_target.cname (中文名)
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    private String cname;

    /**
     * cloud_target.name (英文缩写)
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    private String name;

    /**
     * cloud_target.limit_1
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    private Integer limit1;

    /**
     * cloud_target.limit_2
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    private Integer limit2;

    /**
     * cloud_target.limit_3
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    private Integer limit3;

    /**
     * cloud_target.current_index
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    private Integer currentIndex;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLimit1() {
        return limit1;
    }

    public void setLimit1(Integer limit1) {
        this.limit1 = limit1;
    }

    public Integer getLimit2() {
        return limit2;
    }

    public void setLimit2(Integer limit2) {
        this.limit2 = limit2;
    }

    public Integer getLimit3() {
        return limit3;
    }

    public void setLimit3(Integer limit3) {
        this.limit3 = limit3;
    }

    public Integer getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(Integer currentIndex) {
        this.currentIndex = currentIndex;
    }
}