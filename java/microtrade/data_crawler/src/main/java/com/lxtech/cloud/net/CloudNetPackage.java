package com.lxtech.cloud.net;

/**
 * Data structure for the package sent over the wire
 * @author wangwei
 *
 */
public class CloudNetPackage {
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

}
