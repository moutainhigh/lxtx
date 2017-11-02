package com.lxtx.fb.task.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.lxtx.fb.handler.TaskHandler;
import com.lxtx.fb.handler.UserHandler;
import com.lxtx.fb.helper.ActiveHelper;
import com.lxtx.fb.helper.AdSetHelper;
import com.lxtx.fb.helper.AlipayAccountHelper;
import com.lxtx.fb.helper.FlagContactHelper;
import com.lxtx.fb.helper.PageHelper;
import com.lxtx.fb.helper.UserHelper;
import com.lxtx.fb.pojo.Active;
import com.lxtx.fb.pojo.AdSet;
import com.lxtx.fb.pojo.AlipayAccount;
import com.lxtx.fb.pojo.FlagContact;
import com.lxtx.fb.pojo.Page;
import com.lxtx.fb.pojo.Proxy;
import com.lxtx.fb.pojo.Task;
import com.lxtx.fb.pojo.User;
import com.lxtx.fb.task.service.FbService.ServiceResult;
import com.lxtx.fb.task.util.Constants;

public class TaskService {

	private static Logger logger = Logger.getLogger(TaskService.class);
	
	public void exec(){
		
		while(true) {
			try {
				List<Task> taskList = taskHandler.listUnDeal();
				
				if(taskList != null && taskList.size() > 0){
					for(Task task : taskList){
						execOne(task);
					}
					
					Thread.sleep(1000);
				}else {
					break;
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void execOne(Task task){
		
		if(filter(task)){
			int type = task.getType();
			
			switch(type){
			case Constants.TASKTYPE_LOGIN://login
				execLogin(task);
				break;
			case Constants.TASKTYPE_SETTING://setting
				execSetting(task);
				break;
			case Constants.TASKTYPE_PAGE://page
				execPage(task);
				break;
			case Constants.TASKTYPE_PAGEACTIVE://pageActive
				execPageActive(task);
				break;
			case Constants.TASKTYPE_AD://ad
				execAd(task);
				break;
			case Constants.TASKTYPE_CHECKAD://checkAd
				execCheckAd(task);
				break;
			case Constants.TASKTYPE_FLAGCONTACT://flagContact
				execFlagContact(task);
				break;
			case Constants.TASKTYPE_ADDMONEY://addMoney
				execAddMoney(task);
				break;
			default:
				
				break;
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			if(task.getStatus() < 0){
				taskHandler.updateStatus(task);
			}
		}
	}
	
	private boolean filter(Task task){
		
		User user = userHandler.select(task.getUserId());
		
		int status = filterStatus(user, task);
		
		if(status == -1 || status == 1){
			logger.info("filter status fail:"+task.getUserId()+";"+task.getType());
			
			if(status == -1){
				task.setStatus(-1);
			}
			
			return false;
		}else{
			Proxy proxy = user.getProxy();
			
			if(proxy != null){
				boolean valid = ipTimeService.checkIp(proxy.getIp());
				
				if(!valid){
					logger.info("filter ip time fail.."+proxy.getIp()+";"+task.getId());
				}
				
				return valid;
			}
			
			return true;
		}
	}
	
	private int filterStatus(User user, Task task){
		
		int userStatus = user.getStatus();
		
		if(userStatus >= 0){		
			int type = task.getType();
			
			switch(userStatus){
			case Constants.USERSTATUS_ORI:
				if(type == Constants.TASKTYPE_LOGIN || type == Constants.TASKTYPE_SETTING || type == Constants.TASKTYPE_PAGE){
					return 0;
				}else{
					return 1;
				}
			case Constants.USERSTATUS_LOGIN:
				if(type == Constants.TASKTYPE_LOGIN){
					return -1;
				}else if(type == Constants.TASKTYPE_SETTING || type == Constants.TASKTYPE_PAGE){
					return 0;
				}else{
					return 1;
				}
			case Constants.USERSTATUS_SETTING:
				if(type == Constants.TASKTYPE_LOGIN || type == Constants.TASKTYPE_SETTING){
					return -1;
				}else if(type == Constants.TASKTYPE_PAGE){
					return 0;
				}else{
					return 1;
				}
			case Constants.USERSTATUS_PAGE:
				if(type == Constants.TASKTYPE_LOGIN || type == Constants.TASKTYPE_SETTING || type == Constants.TASKTYPE_PAGE){
					return -1;
				}else if(type == Constants.TASKTYPE_PAGEACTIVE || type == Constants.TASKTYPE_AD){
					return 0;
				}else{
					return -1;
				}
			case Constants.USERSTATUS_AD:
				if(type == Constants.TASKTYPE_LOGIN || type == Constants.TASKTYPE_SETTING || type == Constants.TASKTYPE_PAGE || type == Constants.USERSTATUS_AD){
					return -1;
				}else if(type == Constants.TASKTYPE_PAGEACTIVE || type == Constants.TASKTYPE_CHECKAD){
					return 0;
				}else{
					return 1;
				}
			case Constants.USERSTATUS_ADFLAG:
				if(type == Constants.TASKTYPE_LOGIN || type == Constants.TASKTYPE_SETTING || type == Constants.TASKTYPE_PAGE || type == Constants.USERSTATUS_AD){
					return -1;
				}else if(type == Constants.TASKTYPE_PAGEACTIVE || type == Constants.TASKTYPE_FLAGCONTACT || type == Constants.TASKTYPE_CHECKAD){
					return 0;
				}else{
					return 1;
				}
			case Constants.USERSTATUS_ADFLAGCONTACT:
				if(type == Constants.TASKTYPE_LOGIN || type == Constants.TASKTYPE_SETTING || type == Constants.TASKTYPE_PAGE || type == Constants.USERSTATUS_AD){
					return -1;
				}else if(type == Constants.TASKTYPE_PAGEACTIVE || type == Constants.TASKTYPE_FLAGCONTACT || type == Constants.TASKTYPE_CHECKAD){
					return 0;
				}else{
					return 1;
				}
			case Constants.USERSTATUS_ADPASS:
				if(type == Constants.TASKTYPE_LOGIN || type == Constants.TASKTYPE_SETTING || type == Constants.TASKTYPE_PAGE || type == Constants.USERSTATUS_AD || type == Constants.TASKTYPE_CHECKAD){
					return -1;
				}else if(type == Constants.TASKTYPE_PAGEACTIVE || type == Constants.TASKTYPE_ADDMONEY){
					return 0;
				}else{
					return 1;
				}
			case Constants.USERSTATUS_ADDELIVERY:
				if(type == Constants.TASKTYPE_LOGIN || type == Constants.TASKTYPE_SETTING || type == Constants.TASKTYPE_PAGE || type == Constants.USERSTATUS_AD || type == Constants.TASKTYPE_CHECKAD){
					return -1;
				}else if(type == Constants.TASKTYPE_PAGEACTIVE || type == Constants.TASKTYPE_ADDMONEY){
					return 0;
				}else{
					return 1;
				}
			}
		}else{
			task.setStatus(-1);
		}
		
		return -1;
		
	}
	
	private void execAddMoney(Task task){
		
		User user = userHandler.select(task.getUserId());
		AlipayAccount alipayAccount = alipayAccountHelper.getOne();
		FlagContact flagContact = flagContactHelper.getOne();
		
		ServiceResult result = fbService.addMoney(user, alipayAccount, flagContact);
		
		updateTaskStatus(task, result);
		
		if(result.isSucc()){
			userHandler.updateAdDelivery(user);
		}else{
			if(user.getStatus() < Constants.USERSTATUS_ORI){
				userHandler.updateInvalid(user);
			}
		}
	}
	
	private void execFlagContact(Task task){
		
		User user = userHandler.select(task.getUserId());
		FlagContact flagContact = flagContactHelper.getOne();
		
		ServiceResult result = fbService.flagContact(user, flagContact);
		
		updateTaskStatus(task, result);
		
		if(result.isSucc()){
			user.setStatus(Constants.USERSTATUS_ADFLAGCONTACT);
		}else{
			if(user.getStatus() < Constants.USERSTATUS_ORI){
				userHandler.updateInvalid(user);
			}
		}
	}
	
	private void execCheckAd(Task task){
		
		User user = userHandler.select(task.getUserId());
		FlagContact flagContact = flagContactHelper.getOne();
		
		ServiceResult result = fbService.checkAd(user, flagContact);
		
		updateTaskStatus(task, result);
		
		userHandler.updateAdFlag(user);
		
		if(user.getStatus() < Constants.USERSTATUS_ORI){
			userHandler.updateInvalid(user);
		}
		
	}
	
	private void execAd(Task task){
		
		User user = userHandler.select(task.getUserId());
		
		AdSet adSet = adSetHelper.get(Long.parseLong(task.getData()));
		
		ServiceResult result = fbService.ad(user, adSet);
		
		updateTaskStatus(task, result);
		
		if(user.getPixel() != null && user.getPixel().length() > 0){
			userHandler.updatePixel(user);
		}
		
		if(result.isSucc()){
			if(user.getStatus() < Constants.USERSTATUS_AD){
				user.setStatus(Constants.USERSTATUS_AD);
				userHandler.updateStatus(user);
			}
		}else{
			if(user.getStatus() < Constants.USERSTATUS_ORI){
				userHandler.updateInvalid(user);
			}
		}
	}
	
	private void execPageActive(Task task){
		
		User user = userHandler.select(task.getUserId());
		
		Active active = activeHelper.getOne();
		
		ServiceResult result = fbService.pageActive(user, active);
		
		updateTaskStatus(task, result);
	}
	
	private void execPage(Task task){
		
		User user = userHandler.select(task.getUserId());
		
		String data = task.getData();
		
		if(user.getStatus() < Constants.USERSTATUS_SETTING){
			AlipayAccount alipayAccount = alipayAccountHelper.getOne();
			ServiceResult result = fbService.setting(user);
			
			if(result.isSucc()){
				userHandler.updateSetting(user);
			}else{
				updateTaskStatus(task, result);
				
				if(user.getStatus() < Constants.USERSTATUS_ORI){
					userHandler.updateInvalid(user);
				}	
				
				return;				
			}
		}
		
		AlipayAccount alipayAccount = alipayAccountHelper.getOne();
		Page page = pageHelper.get(Long.parseLong(data));
		
		ServiceResult result = fbService.page(user, page, alipayAccount);
		
		updateTaskStatus(task, result);
		
		if(result.isSucc()){
			if(user.getStatus() < Constants.USERSTATUS_PAGE){
				user.setCategoryId(page.getCategoryId());
				userHandler.updatePage(user);
			}
		}else{
			if(user.getStatus() < Constants.USERSTATUS_ORI){
				userHandler.updateInvalid(user);
			}
		}
	}
	
	private void execSetting(Task task){
		
		User user = userHandler.select(task.getUserId());
		
		ServiceResult result = fbService.setting(user);
		
		updateTaskStatus(task, result);
		
		if(result.isSucc()){
			userHandler.updateSetting(user);
		}else{
			if(user.getStatus() < Constants.USERSTATUS_ORI){
				userHandler.updateInvalid(user);
			}
		}
	}
	
	/**
	 * 执行登陆任务
	 * @param task
	 */
	private void execLogin(Task task){
		
		long userId = task.getUserId();
		
		User user = userHandler.select(userId);
		
		ServiceResult result = fbService.login(user);
		
		updateTaskStatus(task, result);
		
		if(result.isSucc()){
			if(user.getStatus() <= Constants.USERSTATUS_ORI){
				user.setStatus(Constants.USERSTATUS_LOGIN);
				userHandler.updateLogin(user);
			}
		}else{
			userHandler.updateInvalid(user);
		}
	}
	
	private void updateTaskStatus(Task task, ServiceResult result){
		
		task.setStatus(result.isSucc() ? 1 : -1);
		taskHandler.updateStatus(task);
		
	}
	
	//ioc
	private TaskHandler taskHandler;
	
	private UserHandler userHandler;
	
	private FbService fbService;
	
	private UserHelper userHelper;
	
	private PageHelper pageHelper;
	
	private ActiveHelper activeHelper;
	
	private AdSetHelper adSetHelper;
	
	private FlagContactHelper flagContactHelper;
	
	private IpTimeService ipTimeService;
	
	private AlipayAccountHelper alipayAccountHelper;
	
	public void setFbService(FbService fbService) {
		this.fbService = fbService;
	}

	public void setTaskHandler(TaskHandler taskHandler) {
		this.taskHandler = taskHandler;
	}

	public void setUserHandler(UserHandler userHandler) {
		this.userHandler = userHandler;
	}

	public void setUserHelper(UserHelper userHelper) {
		this.userHelper = userHelper;
	}

	public void setPageHelper(PageHelper pageHelper) {
		this.pageHelper = pageHelper;
	}

	public void setActiveHelper(ActiveHelper activeHelper) {
		this.activeHelper = activeHelper;
	}

	public void setAdSetHelper(AdSetHelper adSetHelper) {
		this.adSetHelper = adSetHelper;
	}

	public void setFlagContactHelper(FlagContactHelper flagContactHelper) {
		this.flagContactHelper = flagContactHelper;
	}

	public void setIpTimeService(IpTimeService ipTimeService) {
		this.ipTimeService = ipTimeService;
	}

	public void setAlipayAccountHelper(AlipayAccountHelper alipayAccountHelper) {
		this.alipayAccountHelper = alipayAccountHelper;
	}
	
	
}
