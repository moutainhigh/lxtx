package com.lxtech.model;

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
    private Integer limit_1;

    /**
     * cloud_target.limit_2
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    private Integer limit_2;

    /**
     * cloud_target.limit_3
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    private Integer limit_3;

    /**
     * cloud_target.current_index
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    private Integer current_index;

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

	public Integer getLimit_1() {
		return limit_1;
	}

	public void setLimit_1(Integer limit_1) {
		this.limit_1 = limit_1;
	}

	public Integer getLimit_2() {
		return limit_2;
	}

	public void setLimit_2(Integer limit_2) {
		this.limit_2 = limit_2;
	}

	public Integer getLimit_3() {
		return limit_3;
	}

	public void setLimit_3(Integer limit_3) {
		this.limit_3 = limit_3;
	}

	public Integer getCurrent_index() {
		return current_index;
	}

	public void setCurrent_index(Integer current_index) {
		this.current_index = current_index;
	}

}