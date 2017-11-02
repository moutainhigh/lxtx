package com.lxtx.model;

import java.io.Serializable;

public class CloudChnCode implements Serializable {
	/**
	 * cloud_chn_code.id
	 * 
	 * @ibatorgenerated 2016-11-12 16:47:45
	 */
	private Integer id;

	/**
	 * cloud_chn_code.chnno
	 * 
	 * @ibatorgenerated 2016-11-12 16:47:45
	 */
	private String chnno;

	/**
	 * cloud_chn_code.cod_url
	 * 
	 * @ibatorgenerated 2016-11-12 16:47:45
	 */
	private String codeUrl;
	
	private Integer wxProviderId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getChnno() {
		return chnno;
	}

	public void setChnno(String chnno) {
		this.chnno = chnno;
	}

	public String getCodeUrl() {
		return codeUrl;
	}

	public void setCodeUrl(String codeUrl) {
		this.codeUrl = codeUrl;
	}

	public Integer getWxProviderId() {
		return wxProviderId;
	}

	public void setWxProviderId(Integer wxProviderId) {
		this.wxProviderId = wxProviderId;
	}
}