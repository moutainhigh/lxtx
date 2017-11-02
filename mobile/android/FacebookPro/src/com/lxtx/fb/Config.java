package com.lxtx.fb;

public class Config {

	public static int getOpMachineId(){
		return Config.machineId;
	}
	
	private static int machineId = 1;

	public void setMachineId(int machineId) {
		Config.machineId = machineId;
	}
	
	
}
