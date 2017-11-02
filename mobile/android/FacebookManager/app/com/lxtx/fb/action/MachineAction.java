package com.lxtx.fb.action;

import java.util.List;

import com.lxtx.fb.helper.CountryHelper;
import com.lxtx.fb.pojo.Country;
import com.lxtx.fb.pojo.Machine;
import com.lxtx.fb.service.MachineService;
import com.qlzf.commons.dao.Page;
import com.qlzf.commons.dao.PageCondition;

public class MachineAction extends BaseManageAction{
	
	public String list(){
		
		String country = servletRequest.getParameter("country");
		int status = Integer.parseInt(servletRequest.getParameter("status"));
		
		if(condition == null){
			condition = new PageCondition(1, 50);
		}
		
		Page<Machine> pageMachine = machineService.pageMachine(country, status, condition);
		
		servletRequest.setAttribute("pageMachine", pageMachine);
		servletRequest.setAttribute("country", country);
		servletRequest.setAttribute("status", status);
		
		return PAGE_LIST;
	}
	
	public String edit(){
		
		
		
		
		return PAGE_EDIT;
	}
	
	public String add(){
		
		
		
		
		return PAGE_ADD;
	}
	
	//param
	private PageCondition condition;

	public PageCondition getCondition() {
		return condition;
	}

	public void setCondition(PageCondition condition) {
		this.condition = condition;
	}

	public List<Country> getCountryList(){
		return countryHelper.list();
	}
	
	//ioc
	private MachineService machineService;

	private CountryHelper countryHelper;
	
	public void setMachineService(MachineService machineService) {
		this.machineService = machineService;
	}

	public void setCountryHelper(CountryHelper countryHelper) {
		this.countryHelper = countryHelper;
	}

	
}
