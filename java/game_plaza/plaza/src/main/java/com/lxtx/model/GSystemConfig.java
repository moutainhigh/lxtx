package com.lxtx.model;

import java.io.Serializable;

public class GSystemConfig implements Serializable {
    /**
     * g_system_config.id
     * @ibatorgenerated 2017-01-20 16:23:49
     */
    private Integer id;

    /**
     * g_system_config.property
     * @ibatorgenerated 2017-01-20 16:23:49
     */
    private String property;

    /**
     * g_system_config.value
     * @ibatorgenerated 2017-01-20 16:23:49
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