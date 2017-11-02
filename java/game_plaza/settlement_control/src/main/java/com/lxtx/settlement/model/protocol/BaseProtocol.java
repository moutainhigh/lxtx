package com.lxtx.settlement.model.protocol;

public class BaseProtocol {

	private int protocol;

	public BaseProtocol() {
		this(0);
	}

	public BaseProtocol(int protocol) {
		super();
		this.protocol = protocol;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
}