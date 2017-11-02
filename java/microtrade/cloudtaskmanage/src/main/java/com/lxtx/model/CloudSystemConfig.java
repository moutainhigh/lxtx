package com.lxtx.model;

import java.io.Serializable;

public class CloudSystemConfig implements Serializable {
    /**
     * cloud_system_config.id
     * @ibatorgenerated 2016-10-28 18:18:15
     */
    private Integer id;

    /**
     * cloud_system_config.property
     * @ibatorgenerated 2016-10-28 18:18:15
     */
    private String property;

    /**
     * cloud_system_config.value
     * @ibatorgenerated 2016-10-28 18:18:15
     */
    private String value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}