package com.lxtx.fb.wrap;

import com.lxtx.fb.helper.CategoryHelper;
import com.lxtx.fb.helper.FilterWordsHelper;
import com.lxtx.fb.pojo.Page;
import com.qlzf.commons.handler.GenericWrap;

public class PageWrap implements GenericWrap<Page>{

	@Override
	public void init() {
		
	}

	@Override
	public void wrapBeforeDbOp(Page t) {
		
	}

	@Override
	public void wrapAfterDbOp(Page page) {
		
		String country = page.getCountry();
		
		page.setFilterWords(filterWordsHelper.get(country));
		
		page.setCategory(categoryHelper.get(page.getCategoryId()));
	}

	//ioc
	private FilterWordsHelper filterWordsHelper;

	private CategoryHelper categoryHelper;
	
	public void setFilterWordsHelper(FilterWordsHelper filterWordsHelper) {
		this.filterWordsHelper = filterWordsHelper;
	}

	public void setCategoryHelper(CategoryHelper categoryHelper) {
		this.categoryHelper = categoryHelper;
	}
	
	
}
