package com.baidu.alipay;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	/**
	 * 加密
	 * 
	 * @param content
	 *            需要加密的内容,如果有中文，请确保中文采用GBK编码
	 * @param key
	 *            加密密钥
	 * @return
	 */
	public static byte[] encrypt(String content, byte[] key) {
		if(content == null){
			return null;
		}
		int left = 0;
		try {
			left = content.getBytes("GBK").length % 16;
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (left != 0) {
			for (int i = left; i < 16; i++) {
				content = content + " ";
			}
		}
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance( "MD5" );
			byte[] md5Key = md.digest(key);
			SecretKeySpec keySpec = new SecretKeySpec(md5Key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");// 创建密码器
			byte[] byteContent = null;
			try {
				byteContent = content.getBytes("GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			IvParameterSpec iv = new IvParameterSpec(md5Key);//使用CBC模式，需要一个向量iv
	        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
			byte[] result = cipher.doFinal(byteContent);// 加密
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param key
	 *            解密密钥
	 * @return
	 */
	public static byte[] decrypt(byte[] content, byte[] key) {
		try {
			
			java.security.MessageDigest md = java.security.MessageDigest.getInstance( "MD5" );
			byte[] md5Key = md.digest(key);
			SecretKeySpec keySpec = new SecretKeySpec(md5Key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");// 创建密码器
			IvParameterSpec iv = new IvParameterSpec(md5Key);//使用CBC模式，需要一个向量iv
	        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
			byte[] result = cipher.doFinal(content);// 解密
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	  
}
