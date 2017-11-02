package com.lxtx.fb.task.util;

public class OpData{

	public String id;

	public int point;// -1:first 0:any 1:last

	public OpFunc func;

	public long sleep; 
	
	public String pre;

	public OpData(){
		
	}
	
	public OpData(String id, int point, OpFunc func, long sleep, String pre){
		this.id = id;
		this.point = point;
		this.func = func;
		this.sleep = sleep;
		this.pre = pre;
	}

	
}
