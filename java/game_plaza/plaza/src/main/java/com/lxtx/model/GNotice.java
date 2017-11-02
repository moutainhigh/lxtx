package com.lxtx.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GNotice implements Serializable {
    /**
     * g_notice.id
     * @ibatorgenerated 2017-01-20 12:04:47
     */
    private Integer id;

    /**
     * g_notice.title
     * @ibatorgenerated 2017-01-20 12:04:47
     */
    private String title;

    /**
     * g_notice.content
     * @ibatorgenerated 2017-01-20 12:04:47
     */
    private String content;

    /**
     * g_notice.day
     * @ibatorgenerated 2017-01-20 12:04:47
     */
    private Date day;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDay() {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (day != null) {
			return sdf.format(day);
		}
		return "";
    }

    public void setDay(Date day) {
        this.day = day;
    }
}