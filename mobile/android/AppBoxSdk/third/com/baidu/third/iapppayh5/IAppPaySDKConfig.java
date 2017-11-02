package com.baidu.third.iapppayh5;

/**
 *应用接入iAppPay云支付平台sdk集成信息 
 */
public class IAppPaySDKConfig{

	/**
	 * 应用名称：
	 * 应用在iAppPay云支付平台注册的名称
	 */
	public final static  String APP_NAME = "69色色影院";

	/**
	 * 应用编号：
	 * 应用在iAppPay云支付平台的编号，此编号用于应用与iAppPay云支付平台的sdk集成 
	 */
	public final static  String APP_ID = "3006336249";

	/**
	 * 商品编号：
	 * 应用的商品在iAppPay云支付平台的编号，此编号用于iAppPay云支付平台的sdk到iAppPay云支付平台查找商品详细信息（商品名称、商品销售方式、商品价格）
	 * 编号对应商品名称为：1
	 */
	public final static  int WARES_ID_1=1;

	/**
	 * 应用私钥：
	 * 用于对商户应用发送到平台的数据进行加密
	 */
	public final static String APPV_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIL859tp4umKHBP4vnfEXzq7N0ShqIG3\nBcYNME1lwrOAd/NGWHSirlOQ9sdaE4jAfQKddDVKoIU9aGR/wZsbMLeUhOKelOoe6c2p891MFsS8azPI\nHIxFWMtLUzcnsv5DvhHYwmF+QGcub0DYF96DI0UXYmD6V2SVgFpMSACVdZEvAgMBAAECgYACwI+ak5n5\n8DXw0XwnBhA+KqrZnU/uEctkQuit56J0pRFgIi0/nh9tkBEvVRwt9mVqBaboa739rrGf0vNdb+/RE9Ag\ncVBIxUTot/DjCVBGiRBuLsl3+P4bfQQO/bahilXCKp6CseCvu2ukCF1ULXASMkGPpB7pC0gytDkAgj+O\nWQJBANzdYxrJ4cX9F7/eMgXbX2tiIIyQ8Gi4rOXDTxjTJM/dMy6fQaMjXb6y/Nb1VvDDzrABTfwwtxSH\nYy/rr8Kz8Z0CQQCX01NfOCWeVEP6hKmQWkj+rty4W1EKKKyYlgpO5e/0SaFt9kLrxknthzJXhDfb8pYi\nQ+e8DM4wPfDlwd+Og8o7AkAOE6oqXe2e4deuqenER8wYGwP6EY7RDdwvNLiI24RAH1gQw6O6Uxm5esz0\nomRtKf21aA/hX/V7QHnI5xunrDJdAkBSJUnvtA5OiyM4sXyZGPBztCLCMM45I+a2rD+pjql00D3ipCJz\nNRccyFCJn4UloSxwH7OLQJZy7aCgf3Q4xL+vAkBLeM5In2lfHEko9+qj9RBOF732Huu3RMNAlzHTu6FG\n6KwT45xxKaY3XQcBQrN0OT2jyZT2xqYOup8GrqQpZyGj";

	/**
	 * 平台公钥：
	 * 用于商户应用对接收平台的数据进行解密
	
	 */
	public final static String PLATP_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDiosDcpE9atRSd++BQo+nfbfI+/4izFgx8UsHbbDhtLOxDE03RimIxwDOvy+96tLdhVFvw6y88ql1jVrg8oZsAh34lcCEmAHst17e/5ydAkLUPYfNEoQ6DAAlqvXOZrtjm8d3uwbJq42x/2hT5W7dhjBg2E0z4J37Q5k3/o0i9hwIDAQAB";

}