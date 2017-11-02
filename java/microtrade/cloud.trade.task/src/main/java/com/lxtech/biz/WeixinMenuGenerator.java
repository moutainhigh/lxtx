package com.lxtech.biz;

import java.sql.SQLException;
import java.util.List;

import com.lxtech.dao.CloudWeixinShellDao;
import com.lxtech.model.CloudWeixinShell;
import com.lxtech.util.http.WeixinMenuTest;

public class WeixinMenuGenerator {

	public static void generateMenuInBatch() {
		try {
			List<CloudWeixinShell> shellList = CloudWeixinShellDao.getShellList();
			System.out.println("for break point.");
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
                                                                                                                                                                                                                                                                              