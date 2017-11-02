package com.baidu.crypto;

import com.baidu.crypto.*;

public class AesCrypto {

	public static byte[] encrypt(String data, byte[] key) {
		CBCBlockCipher aes = new CBCBlockCipher(new AESLightEngine());
		CipherParameters parm = new ParametersWithIV(new KeyParameter(key), key);
		aes.init(true, parm);
		byte[] in = data.getBytes();
		int in_len = in.length / aes.getBlockSize() * aes.getBlockSize();
		int out_len;
		if (in_len < in.length)
			out_len = in_len + aes.getBlockSize();
		else
			out_len = in_len;
		byte[] out = new byte[out_len];
		int i = 0;
		while (i < in_len) {
			aes.processBlock(in, i, out, i);
			i += aes.getBlockSize();
		}
		if (in_len < in.length) {
			byte[] last_block = new byte[aes.getBlockSize()];
			int j = 0;
			int count = in.length - i;
			while (j < count) {
				last_block[j] = in[i + j];
				++j;
			}
			while (j < aes.getBlockSize()) {
				last_block[j] = 0x20;
				++j;
			}
			aes.processBlock(last_block, 0, out, i);
		}
		return out;
	}

	public static byte[] decrypt(byte[] data, byte[] key) {
		byte[] out = new byte[data.length];
		try{
		CBCBlockCipher aes = new CBCBlockCipher(new AESLightEngine());
		CipherParameters parm = new ParametersWithIV(new KeyParameter(key), key);
		aes.init(false, parm);
		int i = 0;
		while (i < data.length) {
			aes.processBlock(data, i, out, i);
			
			i += aes.getBlockSize();
		}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return out;
	}
}
