package com.jxt.synch.handler;

import java.util.Calendar;

import com.jxt.synch.pojo.DuohSync;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class DuohSyncHandler extends SimpleIbatisEntityHandler<DuohSync>{

	public void save(DuohSync duohSync){
		
		String channel = duohSync.getChannel();
		
		String prefix = "a";
		
		int pos = channel.indexOf(prefix);
		
		if(pos > 0){
			String channelCode = channel.substring(0,pos);
			long mobileTaskId = Long.parseLong(channel.substring(pos+1));
			
			duohSync.setChannelCode(channelCode);
			duohSync.setMobileTaskId(mobileTaskId);
			duohSync.setCreateDay(getDayInt());
		}
		
		insert(duohSync);
	}
	
	private int getDayInt(){
		
		Calendar cal = Calendar.getInstance();
		
		return cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH);
	}
	
}
