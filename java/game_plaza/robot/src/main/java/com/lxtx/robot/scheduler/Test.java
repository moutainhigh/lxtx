package com.lxtx.robot.scheduler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.lxtx.robot.dbmodel.UserData;
import com.lxtx.robot.handler.UserDataHandler;
import com.lxtx.robot.model.protocol.BaseProtocol;
import com.lxtx.robot.model.protocol.S2CProtocolLogin;
import com.lxtx.robot.model.protocol.S2CProtocolSettedChips;
import com.lxtx.robot.model.protocol.S2CUserInfo;
import com.lxtx.robot.service.ProtocolUtil;

public class Test {
	public static void main(String[] args) {
		String s = ProtocolUtil.makeLoginMessage(0);
		String jsonStr;
		{
			S2CProtocolLogin s2cProtocolLogin = new S2CProtocolLogin();
			s2cProtocolLogin.setProtocol(100);
			s2cProtocolLogin.setCode(101);
			s2cProtocolLogin.setErrMsg("add");
			List<S2CUserInfo> users = new ArrayList<>();
			s2cProtocolLogin.setUser(users);
			for (int i = 0; i < 3; i++) {
				S2CUserInfo user = new S2CUserInfo();
				user.setId(i);
				user.setName("name" + i);
				user.setChips(i * 100);
				user.setMoney(i * 1000);
				user.setReliefCount(i);
				users.add(user);
			}
			Gson gson = new Gson();
			jsonStr = gson.toJson(s2cProtocolLogin);
		}
		{
			Gson gson = new Gson();
			BaseProtocol baseProtocol = gson.fromJson(jsonStr, BaseProtocol.class);
			S2CProtocolLogin s2cProtocolLogin = gson.fromJson(jsonStr, S2CProtocolLogin.class);
			int i = 0;
		}
		{
			jsonStr = "{\"total\":{\"22\":0,\"23\":0,\"24\":0,\"25\":0,\"26\":0,\"27\":0,\"28\":0,\"29\":0,\"30\":0,\"31\":0,\"10\":0,\"32\":0,\"11\":0,\"33\":0,\"12\":0,\"34\":0,\"13\":0,\"35\":0,\"14\":0,\"15\":0,\"16\":0,\"17\":0,\"18\":0,\"19\":0,\"1\":0,\"2\":0,\"3\":0,\"4\":0,\"5\":0,\"6\":0,\"7\":0,\"8\":0,\"9\":0,\"20\":0,\"21\":0},\"single\":{\"22\":0,\"23\":0,\"24\":0,\"25\":0,\"26\":0,\"27\":0,\"28\":0,\"29\":0,\"30\":0,\"31\":0,\"10\":0,\"32\":0,\"11\":0,\"33\":0,\"12\":0,\"34\":0,\"13\":0,\"35\":0,\"14\":0,\"15\":0,\"16\":0,\"17\":0,\"18\":0,\"19\":0,\"1\":0,\"2\":0,\"3\":0,\"4\":0,\"5\":0,\"6\":0,\"7\":0,\"8\":0,\"9\":0,\"20\":0,\"21\":0},\"protocol\":2002}";
			BaseProtocol baseProtocol = ProtocolUtil.parseProtocal(jsonStr);
			for (Entry<String, Integer> entry : ((S2CProtocolSettedChips) baseProtocol).getTotal().entrySet()) {
				System.out.println("id:" + entry.getKey() + " value:" + entry.getValue());
			}
		}
		try {
			List<Integer> userIds = new ArrayList<>();
			for (int i = 0; i < 200; i++) {
				userIds.add(i);
			}
			long c = System.currentTimeMillis();
			List<UserData> userDatas = UserDataHandler.queryUserData(userIds);
			System.out.println("used:" + (System.currentTimeMillis() - c));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
