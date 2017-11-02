package com.lxtx.util.tool;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtil {
  private final static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
  /**
   * @Title getImgeHexString @Description 网络图片转换成二进制字符串 @param URLName
   * 网络图片地址 @param type 图片类型 @return String 转换结果 @throws
   */
  public static String getImgeHexString(String URLName, String type) {
    byte[] data = getImageData(URLName, type);
    if (data != null) {
      return byte2hex(data);
    } else {
      return "";
    }
  }
  
  /**
   * Get the binary data array for an image by a url
   * @param URLName
   * @param type
   * @return
   */
  public static byte[] getImageData(String URLName, String type) {
    byte[] data = null;
    try {
      int HttpResult = 0; // 服务器返回的状态
      URL url = new URL(URLName); // 创建URL
      URLConnection urlconn = url.openConnection(); // 试图连接并取得返回状态码
      urlconn.connect();
      HttpURLConnection httpconn = (HttpURLConnection) urlconn;
      HttpResult = httpconn.getResponseCode();
      if (HttpResult != HttpURLConnection.HTTP_OK) // 不等于HTTP_OK则连接不成功
      {
        logger.error("failed to get response");
      } else {
        BufferedInputStream bis = new BufferedInputStream(urlconn.getInputStream());

        BufferedImage bm = ImageIO.read(bis);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bm, type, bos);
        bos.flush();
        data = bos.toByteArray();
        bos.close();
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return data;
  }
  
  /**
   * @title 根据二进制字符串生成图片
   * @param data
   *          生成图片的二进制字符串
   * @param fileName
   *          图片名称(完整路径)
   * @param type
   *          图片类型
   * @return
   */
  public static void saveImage(String data, String fileName, String type) {

    BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_BYTE_BINARY);
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    try {
      ImageIO.write(image, type, byteOutputStream);
      // byte[] date = byteOutputStream.toByteArray();
      byte[] bytes = hex2byte(data);
      System.out.println("path:" + fileName);
      RandomAccessFile file = new RandomAccessFile(fileName, "rw");
      file.write(bytes);
      file.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 反格式化byte
   * 
   * @param s
   * @return
   */
  public static byte[] hex2byte(String s) {
    byte[] src = s.toLowerCase().getBytes();
    byte[] ret = new byte[src.length / 2];
    for (int i = 0; i < src.length; i += 2) {
      byte hi = src[i];
      byte low = src[i + 1];
      hi = (byte) ((hi >= 'a' && hi <= 'f') ? 0x0a + (hi - 'a') : hi - '0');
      low = (byte) ((low >= 'a' && low <= 'f') ? 0x0a + (low - 'a') : low - '0');
      ret[i / 2] = (byte) (hi << 4 | low);
    }
    return ret;
  }

  /**
   * 格式化byte
   * 
   * @param b
   * @return
   */
  public static String byte2hex(byte[] b) {
    char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    char[] out = new char[b.length * 2];
    for (int i = 0; i < b.length; i++) {
      byte c = b[i];
      out[i * 2] = Digit[(c >>> 4) & 0X0F];
      out[i * 2 + 1] = Digit[c & 0X0F];
    }

    return new String(out);
  }
  
  public static void main(String[] args) {
    System.out.println(ImageUtil.getImgeHexString("http://lm.admin5.com/index.codeimage", "jpg"));
  }
}





