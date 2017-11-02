package com.baidu.serv;

import android.os.Message;

public class SelfMessage {

	private Message message;
	
	public Message getMessage(){
		return this.message;
	}
	
	public SelfMessage(int what){
		this.message = new Message();
		this.message.what = what;
	}
	
	public SelfMessage(Message message){
		this.message = message;
	}
}
