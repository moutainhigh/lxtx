package com.neo.web;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.neo.entity.Adset;
import com.neo.entity.FbUser;
import com.neo.entity.FilterWords;
import com.neo.entity.Task;
import com.neo.entity.User;
import com.neo.logic.Constants;
import com.neo.logic.WebTool;
import com.neo.service.AdsetService;
import com.neo.service.FbUserService;
import com.neo.service.PageService;
import com.neo.service.TaskService;

@Controller
public class TaskController {

	@Resource
	private TaskService taskService;
	@Resource
	private PageService pageService;
	@Resource
	private FbUserService userService;
	@Resource
	private AdsetService adsetService;
	
	private Map<String, FilterWords> wordsMap;
	
    @RequestMapping("/task/list")
    public String list(Model model, Long operatorId, HttpServletRequest req) {
    	if (operatorId == null) {
    		User user = (User)WebTool.getSessionValue(req, Constants.SESSION_USER);
    		if (user != null) {
    			operatorId = user.getId();
    		}
    	}
    	
    	if (operatorId != null) {
            List<Task> tasks=taskService.getTaskList(operatorId);
            model.addAttribute("tasks", tasks.stream().filter(this::checkTask).map(this::convertTask) .collect(Collectors.toList()));
            return "task/list";
    	} else {
    		return "error";
    	}
    }
    
    private Task convertTask(Task task) {
    	String taskType = "";
    	switch (task.getType()) {
		case 2:
			taskType = "settings";
			break;
		case 3:
			taskType = "page_creation";
			break;
		case 5:
			taskType = "ad_creation";
			break;
		case 6:
			taskType = "ad_appeal";
			break;
		case 7:
			taskType = "add_money";
			break;

		default:
			break;
		}
    	task.setTaskType(taskType);
    	return task;
    }
    
    private boolean checkTask(Task task) {
    	int type = task.getType();
    	if (type >= 2 && type <= 7) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    @RequestMapping("/task/toExec")
    public String toAdd(Model model, long taskId) {
    	Task task = this.taskService.getTaskById(taskId);
    	if (task != null) {
    		int taskType = task.getType();
    		
    		Object dataObj = null;
    		String dataName = "";
    		String forwardPath = "";
    		
    		switch (taskType) {
			case Constants.TASK_SETTING:
				forwardPath = "task/fUserSetting";
				dataObj = this.userService.getFbUserById(task.getFbUser().getId());
				dataName = "fbUser";
				break;
				
			case Constants.TASK_AD_CREATE:
				forwardPath = "task/adset";
				Long adId = Long.valueOf(task.getData());
				dataObj = this.adsetService.getAdsetById(BigInteger.valueOf(adId));
				dataName = "adset";
				break;
				
			case Constants.TASK_PAGE_CREATE:
				forwardPath = "task/page";
				Long pageId = Long.valueOf(task.getData());
				dataObj = this.pageService.getPageById(BigInteger.valueOf(pageId));
				dataName = "page";
				if (wordsMap == null) {
					wordsMap = this.taskService.fetchAllFilterWords();
				}
				model.addAttribute("words", wordsMap.get(task.getCountry()).getWords());
				break;
			
			case Constants.TASK_AD_CHECK:
				forwardPath = "task/adcheck";
				break;
				
			default:
				break;
			}
    		
    		if (dataObj != null)
    		{
    			model.addAttribute(dataName, dataObj);
    		}
    		model.addAttribute("task", task);
    		model.addAttribute("taskId", taskId);
    		model.addAttribute("operatorId", task.getCsUserId());
    		return forwardPath;
    	}
    	
    	//this is the error page
        return "user/userAdd";
    }
    
    @RequestMapping("/task/saveAdset")
    public String saveAdset(Model model, HttpServletRequest request) {
    	Integer status = Integer.valueOf(WebTool.getReqValue(request, "statusval"));
    	if (status == null) {
    		return "error";
    	}
    	
    	Integer taskId = Integer.valueOf(WebTool.getReqValue(request, "taskId"));
    	Task task = this.taskService.getTaskById(taskId);
    	int taskType = task.getType();
    	FbUser user = task.getFbUser();
    	Date today = new Date();
    	Long dayNum = WebTool.getDayNum(today);
    	Long taskNum = user.getTaskNum();
    	
    	switch (status) {
		case 1:
			if (taskType == 5) { //adset
				String pixel = WebTool.getReqValue(request, "pixel");
				user.setPixel(pixel);
				user.setAdDay(dayNum);
				user.setStatus(Constants.USER_STATUS_AD);
			} else if (taskType == 3) { //page
				String pageurl = WebTool.getReqValue(request, "pageurl");
				user.setPageUrl(pageurl);
				user.setPageDay(dayNum);
				user.setStatus(Constants.USER_STATUS_PAGE);
			} else if (taskType == 2) { //setting
				user.setStatus(Constants.USER_STATUS_SETTING);
				user.setSettingDay(dayNum);
			} else if (taskType == 6) {//check ad status
				int adStatus = Integer.valueOf(WebTool.getReqValue(request, "adstatusval"));
				if (adStatus == 0) { //we need to appeal
					user.setStatus(Constants.USER_STATUS_APPEAL);
					user.setAdFlagContactDay(dayNum);
					Long adFlagDay = user.getAdFlagDay();
					if (adFlagDay == null || adFlagDay.longValue() == 0l) {
						user.setAdFlagDay(dayNum);
					}
				} else { //pass
					user.setAdPassDay(dayNum);
					user.setStatus(Constants.USER_STATUS_PASS);
				}
			}
			if (taskNum != null && taskNum.longValue() > 0) {
				user.setTaskNum(taskNum - 1); 
			}
			userService.saveFbUser(user);
			break;
		case -1:
			task.setStatus(status);
			break;
		case -2:
			task.setStatus(status);
			break;
		case -99:
			task.setStatus(status);
			task.setErrorReason(WebTool.getReqValue(request, "reason_desc"));
			break;

		default:
			break;
		}
    	
    	if (status == -1 || status == -2) {
    		user.setStatus(Long.valueOf(status.longValue()));
    		user.setInvalidDay(dayNum);
    		if (taskNum != null && taskNum.longValue() > 0) {
				user.setTaskNum(taskNum - 1); 
			}
    		userService.saveFbUser(user);
    	}
    	
    	this.taskService.updateTaskStatusAndReason(task.getId(), status, task.getErrorReason());
    	return "redirect:/task/list";
    }
}
