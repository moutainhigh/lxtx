package com.lxtx.fb.task.service;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

import com.lxtx.fb.handler.UserHandler;
import com.lxtx.fb.helper.UaHelper;
import com.lxtx.fb.pojo.Ua;
import com.lxtx.fb.pojo.User;
import com.lxtx.fb.task.util.CommonUtil;

public class AccountService {

	private String workDir = "C:/Users/thinkpad/Desktop/海外投放/账号/work/";
	
	public void exec(){
		
		File dir = new File(workDir);
		
		File[] subDirArr = dir.listFiles();
		
		if(subDirArr != null && subDirArr.length > 0){
			for(File subDir : subDirArr){
				String country = subDir.getName();
				
				File file = null;
				File dir1 = null;
				
				File[] subDir1Arr = subDir.listFiles();
				
				if(subDir1Arr != null && subDir1Arr.length > 0){
					for(File f : subDir1Arr){
						if(f.isFile()){
							file = f;
						}else{
							dir1 = f;
						}
					}
					
					if(file != null && dir1 != null){
						try {
							
							String contents = FileUtils.readFileToString(file, "utf-8");
							
							if(contents != null && contents.length() > 0){
								String[] arr = contents.split("\r\n");
								
								for(String ss : arr){
									String[] arr1 = ss.split("\t");
									
									if(arr1.length >= 5){
										String userName = arr1[0];
										String userPass = arr1[1];
										
										File cookieFile = new File(dir1.getAbsolutePath()+File.separator+userName+".txt");
										
										if(cookieFile.exists()){
											String cookies = FileUtils.readFileToString(cookieFile, "utf-8").replace("\r\n", "");
											
											int pos = cookies.indexOf("[");
											
											cookies = cookies.substring(pos);
											
											User user = new User();
											user.setCreateDay(CommonUtil.getDay(0));
											user.setStatus(0);
											user.setCookies(cookies);
											user.setUserName(userName);
											user.setUserPass(userPass);
											user.setCountry(country);
											
											Ua ua = uaHelper.getOne();
											if(ua != null){
												user.setUaId(ua.getId());
											}
											
											userHandler.insert(user);
										}else{
											System.out.println("no cookie file : "+userName);
										}
									}
								}
							}
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
			}
			
			for(File subDir : subDirArr){
				try {
					FileUtils.deleteDirectory(subDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//ioc
	private UserHandler userHandler;
	
	private UaHelper uaHelper;

	public void setUserHandler(UserHandler userHandler) {
		this.userHandler = userHandler;
	}

	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}

	public void setUaHelper(UaHelper uaHelper) {
		this.uaHelper = uaHelper;
	}
	
	
}
