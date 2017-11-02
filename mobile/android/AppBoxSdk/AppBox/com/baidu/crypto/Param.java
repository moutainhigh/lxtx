package com.baidu.crypto;

public abstract class Param
{
	public  String CID = "";// ����ID
	public  String PID = "";// ��ƷID
	public  String FID = "";// ��Ʒ��Ӧ�İ�ID	
	public  String IMSI = "";// SIM���Ĵ���
	public  String SMSC = "";// SIM���Ķ������ĺ�13800755500
	public  String VER = "8.0.2a";// PMJΪ��8.x.xj
	public  String MobType = "";// �ֻ���ͣ���PM��ȡ
	public  String IMEI = "";// �û��ֻ���ն˴���
	public  String Interval = "0";// �´������۷�ʱ�䣬��ʼֵΪ0��PMJ�����ύ
	public  String Nxstart = "0";// �´η��ʽӿ�ʱ�䣬��ʼֵΪ0��PMJ�����ύ
	public  String APPver = "0.0.0";// APP�İ汾	
	public String MB="";
	public String SN="";
	public Param(){}
	public String CreatParam()
	{
		return "CID="+CID+"&PID="+PID+"&FID="+FID+"&IMSI="+IMSI+"&SMSC="+SMSC+
		"&VER="+VER+"&MobType="+MobType+"&IMEI="+IMEI+"&Interval="+Interval+"&NXStart="+Nxstart+
		"&MB="+MB+"&SN="+SN;
		
	}
	
}