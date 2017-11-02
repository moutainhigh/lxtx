package com.lxtx.util.tool;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.Key;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CryptoUtil {

	private Key keySpec;
	private String ALGO;

	/**
	 * Default constructor.
	 */
	public CryptoUtil() {
		/*
		 * 3DES http://www.w3.org/2001/04/xmlenc#tripledes-cbc
		 */
		byte[] secretKey;
		try {
			 secretKey = new BASE64Decoder().decodeBuffer("SE9MWUhFSklOR0FOR0BDSVRJQ1MuQ09N"); 
			 keySpec = new SecretKeySpec(secretKey, "DESede"); 
			 ALGO = "tripledes/CBC/PKCS5Padding";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Method generated to support implementation of operation "encrypt" defined
	 * for WSDL port type named "KPDMSCryptoService".
	 * 
	 * Please refer to the WSDL Definition for more information on the type of
	 * input, output and fault(s).
	 * 
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public String encrypt(String input) throws NoSuchAlgorithmException,
			NoSuchPaddingException, KeyException, IllegalBlockSizeException,
			BadPaddingException {
		String inputString = input;
		byte[] in = inputString.getBytes();

		Cipher cipher = Cipher.getInstance(ALGO);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);

		byte[] encryptedBytes = cipher.doFinal(in);
		byte[] iv = cipher.getIV();

		int newByteLen = encryptedBytes.length + iv.length;
		byte[] concatBytes = new byte[newByteLen];
		System.arraycopy(iv, 0, concatBytes, 0, iv.length);
		System.arraycopy(encryptedBytes, 0, concatBytes, iv.length,
				encryptedBytes.length);

		String encrypted = new BASE64Encoder().encode(concatBytes);
		return encrypted;
	}

	/**
	 * Method generated to support implementation of operation "decrypt" defined
	 * for WSDL port type named "KPDMSCryptoService".
	 * 
	 * Please refer to the WSDL Definition for more information on the type of
	 * input, output and fault(s).
	 * 
	 * @throws Exception
	 */
	public String decrypt(String encryptedString) throws Exception {
		Cipher cipher;
		try {
			byte[] p = new BASE64Decoder().decodeBuffer(encryptedString);
			byte[] iv = new byte[8];
			byte[] part2 = new byte[p.length - 8];

			System.arraycopy(p, 0, iv, 0, 8);

			System.arraycopy(p, 8, part2, 0, p.length - 8);

			System.out
					.println("iv= " + iv.toString() + " length= " + iv.length);

			// configure IVspec
			IvParameterSpec ivps = new IvParameterSpec(iv);

			// init cipher
			cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivps);

			// decrypt
			return new String(cipher.doFinal(part2));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error decrypting :" + e.getMessage(), e);
		}
	}
	
	public static void main(String[] args){
		CryptoUtil instance = new CryptoUtil();
		try {
			// 密码就是password, result就是加密后的结果
			String result = instance.encrypt("password");
			System.out.println(result);
			// System.out.println(instance.decrypt(result));
			
			//如果是通过HTTP Get的方式构造URL，则需要对加密的密码进行URI编码
			//因为Base64中出现的+/等在URI中是有特殊含义，需要转义。
			//如果是通过WebService方式Post的话就不需要URI编码。
			String finalPassword = URLEncoder.encode(result, "iso-8859-1");
			System.out.println(finalPassword);
			String preurl = "http://10.23.115.100:855/xml/auth/authenticate?userid=researchadmin&password=";
			String requestUrl = preurl + finalPassword;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
