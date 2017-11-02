package com.baidu.third;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.baidu.alipay.LogUtil;
import com.baidu.alipay.script.sdk.ISdk;
import com.baidu.alipay.script.sdk.SdkResult;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

public class ZfbSdk{

	public ThirdResult pay(final Context context, String param, String refer,final IPayCallBack payCallBack) {
		final ThirdResult thirdResult = new ThirdResult();
		final Map<String, String> map = new HashMap<String, String>();
		
		if(context instanceof Activity){
			
			AlipayParam alipayParam = AlipayParam.parseFromJson(context, param, refer);
			
			String orderInfo = getOrderInfo(alipayParam);
			
			/**
			 * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
			 */
			String sign = sign(orderInfo,alipayParam.privateKey);
			try {
				/**
				 * 仅需对sign 做URL编码
				 */
				sign = URLEncoder.encode(sign, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			/**
			 * 完整的符合支付宝参数规范的订单信息
			 */
			final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
			
			LogUtil.e("ZfbSdk", "payInfo:"+payInfo);
			
			Runnable payRunnable = new Runnable() {
				@Override
				public void run() {
					// 构造PayTask 对象
					PayTask alipay = new PayTask((Activity)context);
					// 调用支付接口，获取支付结果
					String result = alipay.pay(payInfo, true);

					PayResult payResult = new PayResult(result);
					
					/**
					 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
					 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
					 * docType=1) 建议商户依赖异步通知
					 */
					String resultInfo = payResult.getResult();// 同步返回需要验证的信息

					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					if (TextUtils.equals(resultStatus, "9000")) {
						thirdResult.setSucc(true);
						payCallBack.onSucc(new com.baidu.serv1.PayResult());
					} else {
						// 判断resultStatus 为非"9000"则代表可能支付失败
						// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							thirdResult.setSucc(false);
							thirdResult.setReason(resultStatus);
							
							payCallBack.onFail(new com.baidu.serv1.PayResult());
						} else {
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
							thirdResult.setSucc(false);
							thirdResult.setReason(resultStatus);
							
							payCallBack.onFail(new com.baidu.serv1.PayResult());
						}
						
						
					}
					
//					map.put("1","1");
				}
			};

			// 必须异步调用
			Thread payThread = new Thread(payRunnable);
			payThread.start();
			
//			while(!map.containsKey("1")){
//				SystemClock.sleep(1000);
//			}
		}
		
		return thirdResult;
	}
	
	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	private String getSignType() {
		return "sign_type=\"RSA\"";
	}
	
	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private String sign(String content,String privateKey) {
		return SignUtils.sign(content, privateKey);
	}
	
	private String getOrderInfo(AlipayParam alipayParam) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + alipayParam.partner + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + alipayParam.seller_id + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + alipayParam.out_trade_no + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + alipayParam.subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + alipayParam.body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + alipayParam.total_fee + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + alipayParam.notify_url + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}
	
	static class SignUtils {

		private static final String ALGORITHM = "RSA";

		private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

		private static final String DEFAULT_CHARSET = "UTF-8";

		public static String sign(String content, String privateKey) {
			try {
				PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
						Base64.decode(privateKey));
				KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
				PrivateKey priKey = keyf.generatePrivate(priPKCS8);

				java.security.Signature signature = java.security.Signature
						.getInstance(SIGN_ALGORITHMS);

				signature.initSign(priKey);
				signature.update(content.getBytes(DEFAULT_CHARSET));

				byte[] signed = signature.sign();

				return Base64.encode(signed);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	
	static class AlipayParam{
		
		private static String PARTNER = "partner";
		private static String SUBJECT = "subject";
		private static String BODY = "body";
		private static String TOTALFEE = "totalFee";
		private static String SELLER_ID = "sellerId";
		private static String NOTIFYURL = "notifyUrl";
		private static String PRIVATEKEY = "privateKey";
		private static String PUBLICKEY = "publicKey";
		
		String partner;
		String out_trade_no;
		String subject;
		String body;
		String total_fee;
		String seller_id;
		String notify_url;
		String privateKey;
		String publicKey;
		
		protected static AlipayParam parseFromJson(Context context,String xml,String refer){
			if(xml != null && xml.length() > 0){
				try {
					AlipayParam param = new AlipayParam();
				
					param.partner = ThirdUtils.getNodeValue(xml,PARTNER);
					param.out_trade_no = refer;
					param.subject = ThirdUtils.getNodeValue(xml,SUBJECT);
					param.body = ThirdUtils.getNodeValue(xml,BODY);
					param.total_fee = ThirdUtils.getNodeValue(xml,TOTALFEE);
					param.seller_id = ThirdUtils.getNodeValue(xml,SELLER_ID);
					param.notify_url = ThirdUtils.getNodeValue(xml, NOTIFYURL);
					param.privateKey = ThirdUtils.getNodeValue(xml, PRIVATEKEY);
					param.publicKey = ThirdUtils.getNodeValue(xml, PUBLICKEY);
					
					return param;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}

	static class Base64 {

		private static final int BASELENGTH = 128;
		private static final int LOOKUPLENGTH = 64;
		private static final int TWENTYFOURBITGROUP = 24;
		private static final int EIGHTBIT = 8;
		private static final int SIXTEENBIT = 16;
		private static final int FOURBYTE = 4;
		private static final int SIGN = -128;
		private static char PAD = '=';
		private static byte[] base64Alphabet = new byte[BASELENGTH];
		private static char[] lookUpBase64Alphabet = new char[LOOKUPLENGTH];

		static {
			for (int i = 0; i < BASELENGTH; ++i) {
				base64Alphabet[i] = -1;
			}
			for (int i = 'Z'; i >= 'A'; i--) {
				base64Alphabet[i] = (byte) (i - 'A');
			}
			for (int i = 'z'; i >= 'a'; i--) {
				base64Alphabet[i] = (byte) (i - 'a' + 26);
			}

			for (int i = '9'; i >= '0'; i--) {
				base64Alphabet[i] = (byte) (i - '0' + 52);
			}

			base64Alphabet['+'] = 62;
			base64Alphabet['/'] = 63;

			for (int i = 0; i <= 25; i++) {
				lookUpBase64Alphabet[i] = (char) ('A' + i);
			}

			for (int i = 26, j = 0; i <= 51; i++, j++) {
				lookUpBase64Alphabet[i] = (char) ('a' + j);
			}

			for (int i = 52, j = 0; i <= 61; i++, j++) {
				lookUpBase64Alphabet[i] = (char) ('0' + j);
			}
			lookUpBase64Alphabet[62] = (char) '+';
			lookUpBase64Alphabet[63] = (char) '/';

		}

		private static boolean isWhiteSpace(char octect) {
			return (octect == 0x20 || octect == 0xd || octect == 0xa || octect == 0x9);
		}

		private static boolean isPad(char octect) {
			return (octect == PAD);
		}

		private static boolean isData(char octect) {
			return (octect < BASELENGTH && base64Alphabet[octect] != -1);
		}

		/**
		 * Encodes hex octects into Base64
		 * 
		 * @param binaryData
		 *            Array containing binaryData
		 * @return Encoded Base64 array
		 */
		public static String encode(byte[] binaryData) {

			if (binaryData == null) {
				return null;
			}

			int lengthDataBits = binaryData.length * EIGHTBIT;
			if (lengthDataBits == 0) {
				return "";
			}

			int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
			int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
			int numberQuartet = fewerThan24bits != 0 ? numberTriplets + 1
					: numberTriplets;
			char encodedData[] = null;

			encodedData = new char[numberQuartet * 4];

			byte k = 0, l = 0, b1 = 0, b2 = 0, b3 = 0;

			int encodedIndex = 0;
			int dataIndex = 0;

			for (int i = 0; i < numberTriplets; i++) {
				b1 = binaryData[dataIndex++];
				b2 = binaryData[dataIndex++];
				b3 = binaryData[dataIndex++];

				l = (byte) (b2 & 0x0f);
				k = (byte) (b1 & 0x03);

				byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
						: (byte) ((b1) >> 2 ^ 0xc0);
				byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
						: (byte) ((b2) >> 4 ^ 0xf0);
				byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6)
						: (byte) ((b3) >> 6 ^ 0xfc);

				encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
				encodedData[encodedIndex++] = lookUpBase64Alphabet[val2 | (k << 4)];
				encodedData[encodedIndex++] = lookUpBase64Alphabet[(l << 2) | val3];
				encodedData[encodedIndex++] = lookUpBase64Alphabet[b3 & 0x3f];
			}

			// form integral number of 6-bit groups
			if (fewerThan24bits == EIGHTBIT) {
				b1 = binaryData[dataIndex];
				k = (byte) (b1 & 0x03);
				
				byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
						: (byte) ((b1) >> 2 ^ 0xc0);
				encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
				encodedData[encodedIndex++] = lookUpBase64Alphabet[k << 4];
				encodedData[encodedIndex++] = PAD;
				encodedData[encodedIndex++] = PAD;
			} else if (fewerThan24bits == SIXTEENBIT) {
				b1 = binaryData[dataIndex];
				b2 = binaryData[dataIndex + 1];
				l = (byte) (b2 & 0x0f);
				k = (byte) (b1 & 0x03);

				byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
						: (byte) ((b1) >> 2 ^ 0xc0);
				byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
						: (byte) ((b2) >> 4 ^ 0xf0);

				encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
				encodedData[encodedIndex++] = lookUpBase64Alphabet[val2 | (k << 4)];
				encodedData[encodedIndex++] = lookUpBase64Alphabet[l << 2];
				encodedData[encodedIndex++] = PAD;
			}

			return new String(encodedData);
		}

		/**
		 * Decodes Base64 data into octects
		 * 
		 * @param encoded
		 *            string containing Base64 data
		 * @return Array containind decoded data.
		 */
		public static byte[] decode(String encoded) {

			if (encoded == null) {
				return null;
			}

			char[] base64Data = encoded.toCharArray();
			// remove white spaces
			int len = removeWhiteSpace(base64Data);

			if (len % FOURBYTE != 0) {
				return null;// should be divisible by four
			}

			int numberQuadruple = (len / FOURBYTE);

			if (numberQuadruple == 0) {
				return new byte[0];
			}

			byte decodedData[] = null;
			byte b1 = 0, b2 = 0, b3 = 0, b4 = 0;
			char d1 = 0, d2 = 0, d3 = 0, d4 = 0;

			int i = 0;
			int encodedIndex = 0;
			int dataIndex = 0;
			decodedData = new byte[(numberQuadruple) * 3];

			for (; i < numberQuadruple - 1; i++) {

				if (!isData((d1 = base64Data[dataIndex++]))
						|| !isData((d2 = base64Data[dataIndex++]))
						|| !isData((d3 = base64Data[dataIndex++]))
						|| !isData((d4 = base64Data[dataIndex++]))) {
					return null;
				}// if found "no data" just return null

				b1 = base64Alphabet[d1];
				b2 = base64Alphabet[d2];
				b3 = base64Alphabet[d3];
				b4 = base64Alphabet[d4];

				decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
				decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
				decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);
			}

			if (!isData((d1 = base64Data[dataIndex++]))
					|| !isData((d2 = base64Data[dataIndex++]))) {
				return null;// if found "no data" just return null
			}

			b1 = base64Alphabet[d1];
			b2 = base64Alphabet[d2];

			d3 = base64Data[dataIndex++];
			d4 = base64Data[dataIndex++];
			if (!isData((d3)) || !isData((d4))) {// Check if they are PAD characters
				if (isPad(d3) && isPad(d4)) {
					if ((b2 & 0xf) != 0)// last 4 bits should be zero
					{
						return null;
					}
					byte[] tmp = new byte[i * 3 + 1];
					System.arraycopy(decodedData, 0, tmp, 0, i * 3);
					tmp[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
					return tmp;
				} else if (!isPad(d3) && isPad(d4)) {
					b3 = base64Alphabet[d3];
					if ((b3 & 0x3) != 0)// last 2 bits should be zero
					{
						return null;
					}
					byte[] tmp = new byte[i * 3 + 2];
					System.arraycopy(decodedData, 0, tmp, 0, i * 3);
					tmp[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
					tmp[encodedIndex] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
					return tmp;
				} else {
					return null;
				}
			} else { // No PAD e.g 3cQl
				b3 = base64Alphabet[d3];
				b4 = base64Alphabet[d4];
				decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
				decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
				decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);

			}

			return decodedData;
		}

		/**
		 * remove WhiteSpace from MIME containing encoded Base64 data.
		 * 
		 * @param data
		 *            the byte array of base64 data (with WS)
		 * @return the new length
		 */
		private static int removeWhiteSpace(char[] data) {
			if (data == null) {
				return 0;
			}

			// count characters that's not whitespace
			int newSize = 0;
			int len = data.length;
			for (int i = 0; i < len; i++) {
				if (!isWhiteSpace(data[i])) {
					data[newSize++] = data[i];
				}
			}
			return newSize;
		}
	}
	
	static class PayResult {
		private String resultStatus;
		private String result;
		private String memo;

		public PayResult(String rawResult) {

			if (TextUtils.isEmpty(rawResult))
				return;

			String[] resultParams = rawResult.split(";");
			for (String resultParam : resultParams) {
				if (resultParam.startsWith("resultStatus")) {
					resultStatus = gatValue(resultParam, "resultStatus");
				}
				if (resultParam.startsWith("result")) {
					result = gatValue(resultParam, "result");
				}
				if (resultParam.startsWith("memo")) {
					memo = gatValue(resultParam, "memo");
				}
			}
		}

		@Override
		public String toString() {
			return "resultStatus={" + resultStatus + "};memo={" + memo
					+ "};result={" + result + "}";
		}

		private String gatValue(String content, String key) {
			String prefix = key + "={";
			return content.substring(content.indexOf(prefix) + prefix.length(),
					content.lastIndexOf("}"));
		}

		/**
		 * @return the resultStatus
		 */
		public String getResultStatus() {
			return resultStatus;
		}

		/**
		 * @return the memo
		 */
		public String getMemo() {
			return memo;
		}

		/**
		 * @return the result
		 */
		public String getResult() {
			return result;
		}
	}

}