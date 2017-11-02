package com.lxtech.wx;

import java.sql.SQLException;
import java.util.List;

import com.lxtech.dbmodel.CloudWeixinShell;
import com.lxtech.handler.CloudWeixinShellDao;

public class MajiangMenuGenerator {
	public static void generateMenuInBatch() {
		try {
			List<CloudWeixinShell> shellList = CloudWeixinShellDao.getMajiangShellList();
			for (CloudWeixinShell shell : shellList) {
				if(shell.getMenutype().intValue() == 0){
					//无活动，无红包
					generateMenuNoAtivity(shell);
				}else if(shell.getMenutype().intValue() == 1){
					//有活动，无红包
					generateMenuHasActivityAndNoRed(shell);
				}else{
					//有活动，有红包
					generateMenuHasActivity(shell);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void generateMenuHasActivityAndNoRed(CloudWeixinShell shell) {
		WeixinMenuTest.generateMajiangHasActivityAndNoRedMenu(shell.getCore_app_id(), shell.getApp_id(), shell.getApp_secret(),
				shell.getDomain(), shell.getApp_name(), shell.getDownurl(),shell.getId(),shell.getManageurl());
	}

	private static void generateMenuHasActivity(CloudWeixinShell shell) {
		WeixinMenuTest.generateMajiangMenu(shell.getCore_app_id(), shell.getApp_id(), shell.getApp_secret(),
				shell.getDomain(), shell.getApp_name(), shell.getDownurl(),shell.getId(),shell.getManageurl());
	}
	
	private static void generateMenuNoAtivity(CloudWeixinShell shell) {
		WeixinMenuTest.generateMajiangNoActivityMenu(shell.getCore_app_id(), shell.getApp_id(), shell.getApp_secret(),
				shell.getDomain(), shell.getApp_name(), shell.getDownurl(),shell.getId(),shell.getManageurl());
	}

	public static void main(String[] args) {
		MajiangMenuGenerator.generateMenuInBatch();
	}
}