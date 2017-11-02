package org.takeback.mvc.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.takeback.core.app.ApplicationController;
import org.takeback.core.dictionary.DictionaryController;
import org.takeback.core.organ.OrganController;
import org.takeback.core.role.RoleController;
import org.takeback.core.schema.SchemaController;
import org.takeback.core.user.UserController;
import org.takeback.mvc.controller.core.SessionListener;
import org.takeback.util.exp.ExpressionProcessor;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static org.takeback.mvc.ResponseUtils.createBody;

@RestController
@RequestMapping("test")
public class TestController {

	@RequestMapping("reload")
	public void reload(){
		ApplicationController.instance().reloadAll();
		UserController.instance().reloadAll();
		RoleController.instance().reloadAll();
		OrganController.instance().reloadAll();
		DictionaryController.instance().reloadAll();
		SchemaController.instance().reloadAll();
//		SmsTemplates.reload();
		System.out.println("reload all done.");
	}

	@RequestMapping("onlines")
	public void createSession(HttpServletResponse response){
		try {
			PrintWriter printWriter = response.getWriter();
			Map<String, Object> users = SessionListener.getUsers();
			int onlines = users.size();
			int loginUsers = 0;
			for(Object obj: users.values()){
				if(!SessionListener.Anonymous.equals(obj)){
					loginUsers++;
				}
			}
			printWriter.println("onlines: " + onlines + "\tloginOnlines: " + loginUsers + "\tanonymous: " + (onlines-loginUsers));
			printWriter.println(users.toString());
		} catch (Exception e){}
	}


	@RequestMapping(value = "exp", method = RequestMethod.POST, headers = "content-type=application/json")
	public Map<String, Object> test1(@RequestBody Map<String, ?> body, String sign) {
		List<?> exp = (List<?>) body.get("cnd");
		System.out.println(sign);
		String a = ExpressionProcessor.instance().toString(exp);
		return createBody(a);
	}

}