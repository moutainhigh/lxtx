package com.baidu.third;

public class ThirdConstants {
	
//	public static final String ACTION_UMPAY = "com.jxt.pay.sdk.umpay.action";
//	public static final String ACTION_SKYPAY = "com.jxt.pay.sdk.skypay.action";
	
	public static final String ACTION_ALIPAY = "com.lx.pay.sdk.alipay.action";
	public static final String ACTION_WXPAY = "com.lx.pay.sdk.wxpay.action";

	public static final String ACTION_ZXPAY = "com.lx.pay.sdk.zx.action";
	
	public static final String ACTION_WFTPAY = "com.lx.pay.sdk.wft.action";
	public static final String ACTION_WFTQPAY = "com.lx.pay.sdk.wftq.action";
	
//	public static final String ACTION_HEEPAY = "com.lx.pay.sdk.hee.action";
//	
//	public static final String ACTION_NOWPAY = "com.lx.pay.sdk.now.action";
//	public static final String ACTION_NOWQPAY = "com.lx.pay.sdk.nowq.action";
//	
//	public static final String ACTION_IAPPPAY = "com.lx.pay.sdk.iapppay.action";
	
	public static final String BUNDLE_PARAM = "param";
	public static final String BUNDLE_REFER = "refer";
	
	public static final int FEETYPE_ZFB = 1;
	public static final int FEETYPE_WX = 2;
	public static final int FEETYPE_QQ = 3;
	public static final int FEETYPE_URL = 4;
	
	public static int WX_TYPE = 5;
	public static int WX_TYPE1 = 3;
	public static int WX_TYPE_ALL = 1;
	public static String WX_TYPESORT = "59";
	
//	public static final int WX_TYPE_HEEPAY = 1;
//	public static final int WX_TYPE_IAPPPAY = 2;
	public static final int WX_TYPE_HAITUNPAY = 3;
	public static final int WX_TYPE_HUANMEIPAY = 4;
	public static final int WX_TYPE_JXTPAY = 5;
//	public static final int WX_TYPE_HAITUNNATIVEPAY = 6;
	public static final int WX_TYPE_WXNATIVEPAY = 7;
	public static final int WX_TYPE_WFTNATIVEPAY = 8;
	
	public static final int WX_TYPE_WXPC = 9;
	
	public static String PARAM_ZFB = "<partner>2088421329487420</partner><subject>支付币</subject><body>支付币</body><totalFee>{totalFee}</totalFee><sellerId>tianji_lxtx@163.com</sellerId><privateKey>MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ9BzW+ZknXFwt45\nIK2kPoGU89cnGsmuHrO2rDqWUM1BYNVChD3j0CfSwGEyanMnYQJixL/9+6khL/cA\nbx1bQf4AJ/sGLI8h/BzkM8vFLLdTnGHelB+ZWMMMM3k7inG+4FO2gIO8lu833HvS\n5oKn31mctZQhbv8vS9S12lTwG+RLAgMBAAECgYEAivTgapLF/yr59+pCwB/CRlCQ\nXleJgRYDRe2K42fKwv1bn1h1iIbhVg4GvAhAZ4+hjnJdl+PllNIXRt7DFQtOTRCF\n/3Jg8meOgvznqsyMO23wM73MfSyBfJHWcVNl1RMsll6O2IAyBfMN3XyL29bmABE7\nNBGQrvJgMUCtE4dhePkCQQDRAWDoHLV0tNE/hCz/6jouOli5MdyUkp1LUA3MN6B2\nWcGdRZRmqt+VE09EEMcWeDV6wG99NfWzP83fd/4jWIV3AkEAwxDYOkX2iEzQISYL\nFSYoz23R+3K24A1Iy/2r1KW/x12NIzZvdonDGowJI8PPY1rMcYYFkrp17DR7MirK\nEWAczQJACFIGupbR/nhoUCAB7pozgL2f5JeAkYWYr3PbaMLaJ3wBQjKP6tpoljWz\nlSEZ2+IjNuTMS27HfkBPANN1EZEnlwJAXkpDKw/sloACzzMzgjqa2YGtUc1mprDl\nMm3hZH3mUPlgotfKU1NOMwPj2xzon48hafKtuPpWzCGmN17FpFaANQJBALLXTtHO\n0oXSA3sWVj5/aGVFdpE93qftH8E/K+MAkgnyQ9WE8DFYm3mP/Mso6nacCRpgF56B\nJUFM9VU2eA+Ryno=</privateKey><publicKey>MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB</publicKey><notifyUrl>http://www.cyjd1300.com:9020/pay/synch/netpay/alipayNotify.do</notifyUrl>";
	
	public static String[] PARAMS_ZFB = new String[]{PARAM_ZFB};
	
	public static final String PARAM_WFT = "<body>SPay收款</body><service>unified.trade.pay</service><version>1.0</version><mch_id>7552000042</mch_id><notify_url>http://www.cyjd1300.com:9020/pay/synch/netpay/wftNotify.do</notify_url><total_fee>{total_fee}</total_fee><limit_credit_pay>0</limit_credit_pay><key>37125718986221c1350fed398cd64450</key>";

	public static String[] PARAMS_WFTQ = new String[]{
		"<body>SPay收款</body><service>unified.trade.pay</service><version>1.0</version><mch_id>100510000006</mch_id><notify_url>http://www.cyjd1300.com:9020/pay/synch/netpay/wftNotify.do</notify_url><total_fee>{total_fee}</total_fee><limit_credit_pay>0</limit_credit_pay><key>624b989dbabd28a4ea480404</key>"
	};
	
	public static String[] PARAMS_WFT = new String[]{
		"<body>客服电话：4001558668</body><service>unified.trade.pay</service><version>1.0</version><mch_id>6532000057</mch_id><notify_url>http://www.cyjd1300.com:9020/pay/synch/netpay/wftNotify.do</notify_url><total_fee>{total_fee}</total_fee><limit_credit_pay>0</limit_credit_pay><key>4c49ed53d347063c139bc9179bb4788f</key>"
	};
	
//	public static String PARAM_NOW = "<appId>1468809057965569</appId><md5Key>mVLk84qy3RHgeOvYDHEIFDaFk4sgtupQ</md5Key><notifyUrl>http://www.cyjd1300.com:9020/pay/synch/netpay/nowNotify.do</notifyUrl><mhtOrderAmt>{total_fee}</mhtOrderAmt><mhtOrderType>01</mhtOrderType><mhtCurrencyType>156</mhtCurrencyType><mhtOrderName>客服电话：4001558668</mhtOrderName><mhtCharset>UTF-8</mhtCharset><payChannelType>13</payChannelType>";
//	
//	public static String PARAM_NOWQ = "<appId>1468809057965569</appId><md5Key>mVLk84qy3RHgeOvYDHEIFDaFk4sgtupQ</md5Key><notifyUrl>http://www.cyjd1300.com:9020/pay/synch/netpay/nowNotify.do</notifyUrl><mhtOrderAmt>{total_fee}</mhtOrderAmt><mhtOrderType>01</mhtOrderType><mhtCurrencyType>156</mhtCurrencyType><mhtOrderName>客服电话：4001558668</mhtOrderName><mhtCharset>UTF-8</mhtCharset><payChannelType>25</payChannelType>";
	
	public static String APPID_WX = "wxd9ee0ed41ae2fac0";//"wx734641b73d1621ef";
	
	public static String APPID_WX1 = "wx05dad1babea6b3c4";
	
	public static String URL_OPEN = "http://yun.chnhq.com/yun/jump.jsp";
}
