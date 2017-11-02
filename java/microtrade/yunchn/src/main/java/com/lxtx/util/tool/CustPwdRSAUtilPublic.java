package com.lxtx.util.tool;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.misc.BASE64Decoder;

@SuppressWarnings("restriction")
public class CustPwdRSAUtilPublic {

	private static CustPwdRSAUtilPublic uniqueInstance = null;

	private static Cipher cipher;

	private static RSAPublicKey publicKey;

	private static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDrA8ghgVw6gaWeqK5rXpmLxg2f"
			+ "\r" + "krlP1uWnTr2naiAe2mQC6nDVQTChJApNvzRQhIdh4nrljsy5apl524ZRSZiXA3pf" + "\r"
			+ "zvKwst6C8NuHRIJ8lFZ5MuOi61AnNXJbCb4ziwziyBid5pLF2Vv18dokRckCzVR3" + "\r" + "n096bz6MJhQtXez3YQIDAQAB"
			+ "\r";

	private CustPwdRSAUtilPublic() throws Exception {

		loadPublicKey(DEFAULT_PUBLIC_KEY);
		cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

	}

	public static synchronized CustPwdRSAUtilPublic getInstance() throws Exception {
		if (uniqueInstance == null) {
			uniqueInstance = new CustPwdRSAUtilPublic();
		}
		return uniqueInstance;
	}

	/**
	 * 从字符串中加载公钥
	 * 
	 * @param publicKeyStr
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	private void loadPublicKey(String publicKeyStr) throws Exception {
		BASE64Decoder base64Decoder = new BASE64Decoder();
		byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
		publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
	}

	public synchronized String encryptPWD(String pwd) throws Exception {
		byte[] output = cipher.doFinal(pwd.getBytes());

		// 返回字符串
		return bytes2HexString(output);
	}

	private static String bytes2HexString(byte[] bytes) {
		String hs = null;
		if (bytes != null) {
			final int size = bytes.length;
			if (size > 0) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < size; i++) {
					String tmp = (java.lang.Integer.toHexString(bytes[i] & 0XFF));
					if (tmp.length() == 1) {
						sb.append("0" + tmp);
					} else {
						sb.append(tmp);
					}
				}
				hs = sb.toString().toUpperCase();
			}
		}
		return hs;
	}

	public static void main(String[] args) {
		CustPwdRSAUtilPublic rsaEncrypt = null;
		try {
			rsaEncrypt = CustPwdRSAUtilPublic.getInstance();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			// 测试字符串
			String encryptStr = "112233";
			String encryptedStr = rsaEncrypt.encryptPWD(encryptStr);
			System.out.println(encryptedStr);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}
}
