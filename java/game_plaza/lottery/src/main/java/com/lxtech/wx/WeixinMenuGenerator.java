package com.lxtech.wx;

import java.sql.SQLException;
import java.util.List;

import com.lxtech.dbmodel.CloudWeixinShell;
import com.lxtech.handler.CloudWeixinShellDao;

public class WeixinMenuGenerator {
	public static void generateMenuInBatch() {
		try {
			List<CloudWeixinShell> shellList = CloudWeixinShellDao.getShellList();
			for (CloudWeixinShell shell : shellList) {
				generateMenu(shell);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void generateMenu(CloudWeixinShell shell) {
		WeixinMenuTest.generateMenu(shell.getApp_id(), shell.getApp_secret(), shell.getApp_name(), shell.getChnno(),
				shell.getDomain());
	}

	public static void main(String[] args) {
		WeixinMenuGenerator.generateMenuInBatch();
	}
}