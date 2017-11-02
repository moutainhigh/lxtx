package com.lxtx.controller.pub;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lxtx.controller.BaseController;
import com.lxtx.service.UserService;
import com.lxtx.util.AjaxJson;
import com.lxtx.util.ErrorCode;

@Controller
@RequestMapping(value = "/public/user")
public class PublicUserController extends BaseController {
	@Autowired
	private UserService userService;

	@RequestMapping(value = "changePwdByUserName")
	@ResponseBody
	public Object changePwdByUserName(HttpServletRequest request,
			String uCode, String oldPwd, String password) {
		AjaxJson json = new AjaxJson();
		try {
			json = userService.updatePwdByUCode(request, uCode, oldPwd,
					password);
		} catch (Exception e) {
			json.setCode(ErrorCode.ERROR);
			json.setData("密码转换或数据库操作错误！");
			e.printStackTrace();
			return json;
		}
		return json;
	}
}
