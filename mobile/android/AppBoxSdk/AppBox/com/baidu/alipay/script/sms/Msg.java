package com.baidu.alipay.script.sms;

public class Msg {

    private int id;

    private String address;

    private String body;

    private long date;
    
    private boolean isMms = false; 

    public boolean isMms() {
		return isMms;
	}

	public void setMms(boolean isMms) {
		this.isMms = isMms;
	}

	public Msg() {

    }

    public Msg(int id, String address, String body, long date) {
        this.id = id;
        this.address = address;
        this.body = body;
        this.date = date;
    }

    public Msg(int id, String address, String body, long date,boolean isMms) {
        this.id = id;
        this.address = address;
        this.body = body;
        this.date = date;
        this.isMms = isMms;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
