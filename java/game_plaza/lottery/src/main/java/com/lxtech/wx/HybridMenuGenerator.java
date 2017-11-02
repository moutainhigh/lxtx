package com.lxtech.wx;

import java.sql.SQLException;
import java.util.List;

import com.lxtech.dbmodel.CloudWeixinShell;
import com.lxtech.handler.CloudWeixinShellDao;

public class HybridMenuGenerator {
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
		WeixinMenuTest.generateGameMenu(shell.getApp_id(), shell.getApp_secret(), shell.getChnno());
	}

	public static void main(String[] args) {
		HybridMenuGenerator.generateMenuInBatch();
	}
}