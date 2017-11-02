package com.lxtx.fb.wrap;

import com.lxtx.fb.helper.AdHelper;
import com.lxtx.fb.helper.AlipayAccountHelper;
import com.lxtx.fb.pojo.AdSet;
import com.qlzf.commons.handler.GenericWrap;

public class AdSetWrap implements GenericWrap<AdSet>{

	@Override
	public void init() {
		
	}

	@Override
	public void wrapBeforeDbOp(AdSet t) {
		
	}

	@Override
	public void wrapAfterDbOp(AdSet adSet) {
		
		int alipayAccountId = adSet.getAlipayAccountId();
		if(alipayAccountId > 0){
			adSet.setAlipayAccount(alipayAccountHelper.get(alipayAccountId));
		}
		
		adSet.setAdList(adHelper.list(adSet.getId()));
		
	}
	
	//ioc
	private AdHelper adHelper;
	
	private AlipayAccountHelper alipayAccountHelper;

	public void setAdHelper(AdHelper adHelper) {
		this.adHelper = adHelper;
	}

	public void setAlipayAccountHelper(AlipayAccountHelper alipayAccountHelper) {
		this.alipayAccountHelper = alipayAccountHelper;
	}
	
	

}
