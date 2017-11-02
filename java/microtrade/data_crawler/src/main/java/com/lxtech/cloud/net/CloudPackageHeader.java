package com.lxtech.cloud.net;

import com.google.common.base.Splitter;

/**
 * 
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
	private String session;
	private String stime;

	/**
	 * parse response header
	 * @param headerSource
	 * @return
	 */
	public static CloudPackageHeader parse(String headerSource) {
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
		CloudPackageHeader header = CloudPackageHeader.parse("1,7706,7706,152,5,0,0,0,0,0,2016-11-5 9:54:23");
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

}
