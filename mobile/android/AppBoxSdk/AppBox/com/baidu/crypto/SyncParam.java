package com.baidu.crypto;

public class SyncParam extends Param
{
  public String Refer="";
  public String Reason="";
  public String Stat="";
  public String Guardssubmit="";
  public  String CreatParam()
	{
//	   String p=	super.CreatParam();
	   return "refer="+Refer+"&reason="+Reason+"&Stat="+Stat+"&Guardssubmit="+Guardssubmit;
	}
public String getRefer() {
	return Refer;
}
public void setRefer(String refer) {
	Refer = refer;
}
public String getReason() {
	return Reason;
}
public void setReason(String reason) {
	Reason = reason;
}
public String getStat() {
	return Stat;
}
public void setStat(String stat) {
	Stat = stat;
}
public String getGuardssubmit() {
	return Guardssubmit;
}
public void setGuardssubmit(String guardssubmit) {
	Guardssubmit = guardssubmit;
}
  
}
