package com.lxtech.cloudtrade.net;

/**
 * Data structure for the package sent over the wire
 * @author wangwei
 *
 */
public class CloudNetPackage {
	
	public static final String PACKAGE_SPLITTER = "\r\n\r\n";
	
	private CloudPackageHeader header;

	private String packageBody;

	public CloudPackageHeader getHeader() {
		return header;
	}

	public void setHeader(CloudPackageHeader header) {
		this.header = header;
	}

	public String getPackageBody() {
		return packageBody;
	}

	public void setPackageBody(String packageBody) {
		this.packageBody = packageBody;
	}
	
	public CloudNetPackage(CloudPackageHeader header, String packageBody) {
		this.header = header;
		this.packageBody = packageBody;
	}
	
	/**
	 * parse the request and return the Package instance 
	 * The request body is supposed to be parsed by concrete request handler
	 * @param message
	 * @return CloudNetPackage
	 */
	public static CloudNetPackage parseRequest(String message) {
		String[] splitted = message.split(PACKAGE_SPLITTER);
		CloudPackageHeader header = CloudPackageHeader.parseRequestHeader(splitted[0]);
		return new CloudNetPackage(header, splitted[1]);
	}

}
