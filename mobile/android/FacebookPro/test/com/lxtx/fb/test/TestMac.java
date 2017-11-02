package com.lxtx.fb.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

public class TestMac {

	public static void main(String[] args) {
        // 生成文件名
        String filePath = "mac.txt";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        // 设定起始地址, 以及数量
        printMac(filePath, "00:70:A4:00:00:00", 100000);
    }

    private static void printMac(String filePath, String start, int count) {
        start = start.replaceAll(":", "");
        try {
            File file = new File(filePath);
            FileWriter writer = new FileWriter(file, true);
            BigInteger num = new BigInteger(start, 16);
            BigInteger addNum = new BigInteger("1");
            String result = "";
            for (int i = 0; i < count; i++) {
                result = num.toString(16).toUpperCase();
                for (int j = 12 - result.length(); j > 0; j--) {
                    result = "0" + result;
                }
                writer.write(getMacAdr(result) + "\n");

                num = num.add(addNum);
            }
            writer.close();
            System.out.println("finished");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getMacAdr(String str) {
        StringBuilder result = new StringBuilder("");
        for (int i = 1; i <= 12; i++) {
            result.append(str.charAt(i - 1));
            if (i % 2 == 0) {
                result.append(":");
            }
        }
        return result.substring(0, 17);
    }
	
}
