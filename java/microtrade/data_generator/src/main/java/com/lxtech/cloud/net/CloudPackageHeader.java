package com.lxtech.cloud.net;

import com.google.common.base.Splitter;
import com.lxtech.cloud.util.TimeUtil;

/**
 * @author wangwei
 */
public class CloudPackageHeader {
	// a.Ver=3,a.Len=0,a.OrgLen=0,a.Type=1,a.ID=0,a.ComCode=0,a.EnCryCode=0,a.Binary=0,a.Encode=0,
	private String ver;
	private String len;
	private String orgLen;
	private String type;
	private String id;
	private String comCode;
	private String encryCode;
	private String binary;
	private String encode;
	//用作占位符
	private String pos;
	private String session;
	private String stime;

	public static String generateResponseHeader(int bodyLen, int messageType, String id) {
		StringBuilder sb = new StringBuilder();
		sb.append("1").append(","); //version equals 1
		sb.append(bodyLen).append(",").append(bodyLen).append(","); //len and orgLen
		sb.append(messageType).append(",");
		if (messageType == CloudPackageType.MSG_TYPE_SERPushPrice) { //250
			sb.append("0,0,0,0,0,");
			sb.append(id).append(",");
		} else {
			sb.append(id).append(",");
			sb.append("0,0,0,0,0,");			
		}

		sb.append(TimeUtil.getCurTimeGMT());
		//0,0,0,0,0,2016-11-10 7:59:35
		return sb.toString();
	}
	
	public static CloudPackageHeader parseRequestHeader(String headerSource) {
		Iterable<String> iter = Splitter.on(",").split(headerSource);
		int index = 0;
		CloudPackageHeader header = new CloudPackageHeader();

		for (String str : iter) {
			switch (index) {
			case 0:
				header.setVer(str);
				break;
			case 1:
				header.setLen(str);
				break;
			case 2:
				header.setOrgLen(str);
				break;
			case 3:
				header.setType(str);
				break;
			case 4:
				header.setId(str);
				break;
			case 5:
				header.setComCode(str);
				break;
			case 6:
				header.setEncryCode(str);
				break;
			case 7:
				header.setBinary(str);
				break;
			case 8:
				header.setEncode(str);
				break;
			case 9:
				header.setSession(str);
				break;
			case 10:
				header.setPos(str);
				break;
			case 11:
				header.setStime(str);
				break;
			default:
				break;
			}
			index++;
		}

		return header;
	}
	
	public static void main(String[] args) {
		CloudPackageHeader header = CloudPackageHeader.parseRequestHeader("3,127,0,153,2,0,0,0,0,,0,0");
		System.out.println(header.getBinary());
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getLen() {
		return len;
	}

	public void setLen(String len) {
		this.len = len;
	}

	public String getOrgLen() {
		return orgLen;
	}

	public void setOrgLen(String orgLen) {
		this.orgLen = orgLen;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getComCode() {
		return comCode;
	}

	public void setComCode(String comCode) {
		this.comCode = comCode;
	}

	public String getEncryCode() {
		return encryCode;
	}

	public void setEncryCode(String encryCode) {
		this.encryCode = encryCode;
	}

	public String getBinary() {
		return binary;
	}

	public void setBinary(String binary) {
		this.binary = binary;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getStime() {
		return stime;
	}

	public void setStime(String stime) {
		this.stime = stime;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	
	
}
