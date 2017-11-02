package com.lxtx.fb.service;

import java.util.List;

import com.lxtx.fb.handler.TaskHandler;
import com.lxtx.fb.handler.UserHandler;
import com.lxtx.fb.pojo.Task;
import com.lxtx.fb.pojo.User;
import com.lxtx.fb.util.CommonUtil;
import com.lxtx.fb.util.Constants;
import com.qlzf.commons.dao.Page;
import com.qlzf.commons.dao.PageCondition;

public class TaskService {
	
	public Page<Task> pageByCsUser(int csUserId, int status, PageCondition condition){
		
		
		
		return null;
	}
	
	/**
	 * 初登操作结果
	 * @param taskId
	 * @param resultCode
	 */
	public void execTask_login(long taskId, int resultCode){
		
		Task task = taskHandler.select(taskId);
		User user = userHandler.select(task.getUserId());
		
		if(resultCode < 0){
			errUserDeal(user, resultCode);
		}else{
			user.setStatus(Constants.USERSTATUS_USERLOGIN);
			user.setLoginDay(CommonUtil.getDay(0));
			user.setLastOpTime(System.currentTimeMillis());
			
			userHandler.updateLogin(user);
		}
		
		updateTask(task);
	}
	
	/**
	 * 修改个人设置操作结果
	 * @param taskId
	 * @param resultCode
	 */
	public void execTask_setting(long taskId, int resultCode){
		
		Task task = taskHandler.select(taskId);
		User user = userHandler.select(task.getUserId());
		
		if(resultCode < 0){
			errUserDeal(user, resultCode);
		}else{
			user.setStatus(Constants.USERSTATUS_USERSETTING);
			user.setSettingDay(CommonUtil.getDay(0));
			user.setLastOpTime(System.currentTimeMillis());
			
			userHandler.updateSetting(user);
		}
		
		updateTask(task);
	}
	
	/**
	 * 设置个人专页结果
	 * @param taskId
	 * @param resultCode
	 * @param pageUrl
	 */
	public void execTask_page(long taskId, int resultCode, String pageUrl){
		
		Task task = taskHandler.select(taskId);
		User user = userHandler.select(task.getUserId());
		
		if(resultCode < 0){
			errUserDeal(user, resultCode);
		}else{
			
			user.setStatus(Constants.USERSTATUS_USERPAGE);
			user.setPageDay(CommonUtil.getDay(0));
			user.setPageUrl(pageUrl);
			user.setLastOpTime(System.currentTimeMillis());
			
			userHandler.updatePage(user);
		}
		
		updateTask(task);
	}
	
	/**
	 * 创建广告结果
	 * @param taskId
	 * @param resultCode
	 * @param pixel
	 */
	public void execTask_ad(long taskId, int resultCode, String pixel){
		Task task = taskHandler.select(taskId);
		User user = userHandler.select(task.getUserId());
		
		if(resultCode < 0){
			errUserDeal(user, resultCode);
		}else{
			user.setStatus(Constants.USERSTATUS_USERAD);
			user.setAdDay(CommonUtil.getDay(0));
			user.setPixel(pixel);
			user.setLastOpTime(System.currentTimeMillis());

			userHandler.updateAd(user);
		}
		
		updateTask(task);
	}
	
	/**
	 * 广告检测结果
	 * @param taskId
	 * @param resultCode
	 * @param adStatus
	 */
	public void execTask_adCheck(long taskId, int resultCode, int adStatus){
		
		Task task = taskHandler.select(taskId);
		User user = userHandler.select(task.getUserId());
		
		if(resultCode < 0){
			errUserDeal(user, resultCode);
		}else{
			
			switch(adStatus){
			case Constants.USERSTATUS_USERADFLAG:
				
				if(user.getStatus() < adStatus || user.getStatus() >= Constants.USERSTATUS_USERADPASS){
					user.setStatus(adStatus);
					user.setAdFlagDay(CommonUtil.getDay(0));
				}
				
				break;
			case Constants.USERSTATUS_USERADFLAGCONTACT:
				
				if(user.getStatus() < adStatus){
					user.setStatus(adStatus);
					user.setAdFlagContactDay(CommonUtil.getDay(0));
				}
				
				break;
			case Constants.USERSTATUS_USERADPASS:
				
				if(user.getStatus() < adStatus){
					user.setStatus(adStatus);
					user.setAdPassDay(CommonUtil.getDay(0));
				}
				
				break;
			case Constants.USERSTATUS_USERADFAIL:
				
				user.setStatus(Constants.USERSTATUS_USERPAGE);
				user.setLastOpTime(System.currentTimeMillis());
				
				break;
			}
			
			user.setLastOpTime(System.currentTimeMillis());
			
			userHandler.updateAdCheck(user);
		}
		
		updateTask(task);
	}
	
	/**
	 * 充值投放结果
	 * @param taskId
	 * @param resultCode
	 * @param adStatus
	 * @param money
	 */
	public void execTask_addMoney(long taskId, int resultCode, int adStatus, int money){
		
		Task task = taskHandler.select(taskId);
		User user = userHandler.select(task.getUserId());
		
		if(resultCode < 0){
			errUserDeal(user, resultCode);
		}else{
			
			switch(adStatus){
			case Constants.USERSTATUS_USERADFLAG:
				
				user.setStatus(adStatus);
				user.setAdFlagDay(CommonUtil.getDay(0));
				
				break;
			case Constants.USERSTATUS_USERADFLAGCONTACT:
				
				user.setStatus(adStatus);
				user.setAdFlagContactDay(CommonUtil.getDay(0));
				
				break;
			case Constants.USERSTATUS_USERADFAIL:
				
				user.setStatus(Constants.USERSTATUS_USERPAGE);
				
				break;
			case Constants.USERSTATUS_USERADDELIVER:
				
				user.setAdDeliveryDay(CommonUtil.getDay(0));
				
			}
			
			user.setLastOpTime(System.currentTimeMillis());
			
			userHandler.updateAdCheck(user);			
		}
		
		updateTask(task);
	}
	
	private void errUserDeal(User user, int resultCode){
		user.setStatus(resultCode);
		user.setInvalidDay(CommonUtil.getDay(0));
		
		userHandler.updateStatus(user);
	}
	
	private void updateTask(Task task){
		task.setStatus(1);
		task.setOpDay(CommonUtil.getDay(0));
		taskHandler.updateStatus(task);
	}
	
	//ioc
	private UserHandler userHandler;
	
	private TaskHandler taskHandler;

	public void setTaskHandler(TaskHandler taskHandler) {
		this.taskHandler = taskHandler;
	}
	
	
}
