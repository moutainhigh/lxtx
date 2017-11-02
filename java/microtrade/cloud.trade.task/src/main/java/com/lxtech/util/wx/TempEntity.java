package com.lxtech.util.wx;

public class TempEntity {
	private TemplateValue first;
	private TemplateValue keyword1;
	private TemplateValue keyword2;
	private TemplateValue keyword3;
	private TemplateValue keyword4;
	private TemplateValue keyword5;
	private TemplateValue remark;

	public TemplateValue getKeyword1() {
		return keyword1;
	}

	public void setKeyword1(String value, String color) {
		this.keyword1 = new TemplateValue(value, color);
	}

	public TemplateValue getKeyword2() {
		return keyword2;
	}

	public void setKeyword2(String value, String color) {
		this.keyword2 = new TemplateValue(value, color);
	}

	public TemplateValue getFirst() {
		return first == null ? new TemplateValue() : first;
	}

	public void setFirst(String value, String color) {
		this.first = new TemplateValue(value, color);
	}

	public TemplateValue getKeyword3() {
		return keyword3;
	}

	public void setKeyword3(String value, String color) {
		this.keyword3 = new TemplateValue(value, color);
	}
	
	public void setKeyword5(String value, String color) {
		this.keyword5 = new TemplateValue(value, color);
	}

	public TemplateValue getKeyword4() {
		return keyword4;
	}
	
	public TemplateValue getKeyword5() {
		return keyword5;
	}

	public void setKeyword4(String value, String color) {
		this.keyword4 = new TemplateValue(value, color);
	}

	public TemplateValue getRemark() {
		return remark;
	}

	public void setRemark(String value, String color) {
		this.remark = new TemplateValue(value, color);
	}
}
