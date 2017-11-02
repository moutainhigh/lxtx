package com.lxtx.fb.wrap;

import com.lxtx.fb.helper.CategoryHelper;
import com.lxtx.fb.helper.FilterWordsHelper;
import com.lxtx.fb.helper.LanguagesHelper;
import com.lxtx.fb.helper.ProxyHelper;
import com.lxtx.fb.helper.UaHelper;
import com.lxtx.fb.pojo.User;
import com.qlzf.commons.handler.GenericWrap;

public class UserWrap implements GenericWrap<User>{

	@Override
	public void init() {
		
	}

	@Override
	public void wrapBeforeDbOp(User t) {
		
	}

	@Override
	public void wrapAfterDbOp(User user) {
		
		long proxyId = user.getProxyId();
		user.setProxy(proxyHelper.get(proxyId));
		
		String country = user.getCountry();
		user.setLanguages(languagesHelper.get(country));
		
		user.setFilterWords(filterWordsHelper.get(country));
		
		user.setCategory(categoryHelper.get(user.getCategoryId()));
		
		user.setUa(uaHelper.get(user.getUaId()));
	}

	//ioc
	private ProxyHelper proxyHelper;
	
	private LanguagesHelper languagesHelper;

	private FilterWordsHelper filterWordsHelper;
	
	private CategoryHelper categoryHelper;
	
	private UaHelper uaHelper;
	
	public void setProxyHelper(ProxyHelper proxyHelper) {
		this.proxyHelper = proxyHelper;
	}

	public void setLanguagesHelper(LanguagesHelper languagesHelper) {
		this.languagesHelper = languagesHelper;
	}

	public void setFilterWordsHelper(FilterWordsHelper filterWordsHelper) {
		this.filterWordsHelper = filterWordsHelper;
	}

	public void setCategoryHelper(CategoryHelper categoryHelper) {
		this.categoryHelper = categoryHelper;
	}

	public void setUaHelper(UaHelper uaHelper) {
		this.uaHelper = uaHelper;
	}
	
	
}
