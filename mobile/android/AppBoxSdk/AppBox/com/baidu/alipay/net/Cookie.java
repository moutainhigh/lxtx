package com.baidu.alipay.net;

public class Cookie {

	private String name;
	
	private String value;
	
	private String domain;
	
	private String path;
	
	private String httpOnly;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
		
	public String getHttpOnly() {
		return httpOnly;
	}

	public void setHttpOnly(String httpOnly) {
		this.httpOnly = httpOnly;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append(name).append("=").append(value);
//		sb.append("; ").append("path=").append(path);
//		
//		if(httpOnly != null){
//			sb.append("; HttpOnly");
//		}
		
		return sb.toString();
	}
	
	public static Cookie parse(String ss){
		Cookie cookie = new Cookie();
		
		String[] arr = ss.split("; ");
		
//		for(String s : arr){
//			if(s.contains("HttpOnly")){
//				cookie.setHttpOnly("1");
//			}else{
//				String[] arr1 = s.split("=");
//				
//				if(arr1[0].equalsIgnoreCase("Path")){
//					cookie.setPath(arr1[1]);
//				}else if(arr1[0].equalsIgnoreCase("Domain") || arr1[0].equals("Expires")){//Domain=211.136.165.53; Expires=Sat, 06-Feb-2016 15:06:25 GMT
//					
//				}
//				else{
//					cookie.setName(arr1[0]);
//					cookie.setValue(arr1[1]);
//				}
//			}
//		}
		
		for(int i = 0 ; i < arr.length ; i ++){
			String s = arr[i];
			
			if(s.contains("HttpOnly")){
				cookie.setHttpOnly("1");
			}else{
				String[] arr1 = s.split("=");
				
				if(i == 0){
					cookie.setName(arr1[0]);
					cookie.setValue(arr1[1]);
				}else{
					if(arr1[0].equalsIgnoreCase("Path")){
						cookie.setPath(arr1[1]);
					}else if(arr1[0].equalsIgnoreCase("Domain")){
						cookie.setDomain(arr1[1]);
					}
				}
			}
		}
		
		return cookie;
	}
	
	public String getKey(){
		return domain+"_"+name+"_"+path;
	}
}
