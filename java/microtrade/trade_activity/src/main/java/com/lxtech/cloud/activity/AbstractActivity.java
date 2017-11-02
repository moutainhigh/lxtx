package com.lxtech.cloud.activity;

public abstract class AbstractActivity {
	protected String user_id;
	
	public AbstractActivity(String user_id) {
		this.user_id = user_id;
	}
	
	public abstract void handleActivity();
}
