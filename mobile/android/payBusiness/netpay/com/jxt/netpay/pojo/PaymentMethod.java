package com.jxt.netpay.pojo;

/**
 * 支付方式
 * @author leoliu
 *
 */
public class PaymentMethod {
	
	private Integer id;
	
	private String name;
	
	private Integer type;
	
	private Boolean valid;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	
	

}
