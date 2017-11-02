package com.lxtx.util.tencent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

public class Xml2JsonUtil {
  private static final Logger logger = LoggerFactory.getLogger(Xml2JsonUtil.class);

  /**
   * 转换一个xml格式的字符串到json格式
   * 
   * @param xml
   *          xml格式的字符串
   * @return 成功返回json 格式的字符串;失败反回null
   */
  @SuppressWarnings("unchecked")
  public static JSONObject xml2JSON(InputStream is) {
    JSONObject obj = new JSONObject();
    try {
      SAXReader sb = new SAXReader();
      Document doc = sb.read(is);
      Element root = doc.getRootElement();
      obj.put(root.getName(), iterateElement(root));
      return obj;
    } catch (Exception e) {
      logger.error("传入XML后转换JSON出现错误===== Xml2JsonUtil-->xml2JSON============>>", e);
      return null;
    }
  }

  /**
   * 一个迭代方法
   * 
   * @param element
   *          : org.jdom.Element
   * @return java.util.Map 实例
   */
  @SuppressWarnings("unchecked")
  private static Map iterateElement(Element element) {
    List jiedian = element.elements();
    Element et = null;
    Map obj = new HashMap();
    List list = null;
    for (int i = 0; i < jiedian.size(); i++) {
      list = new LinkedList();
      et = (Element) jiedian.get(i);
      if (et.getTextTrim().equals("")) {
        if (et.elements().size() == 0)
          continue;
        if (obj.containsKey(et.getName())) {
          list = (List) obj.get(et.getName());
        }
        list.add(iterateElement(et));
        obj.put(et.getName(), list);
      } else {
        if (obj.containsKey(et.getName())) {
          list = (List) obj.get(et.getName());
        }
        list.add(et.getTextTrim());
        obj.put(et.getName(), list);
      }
    }
    return obj;
  }
  
  public static void main(String[] args) {
	  try {
		JSONObject obj  = Xml2JsonUtil.xml2JSON(new FileInputStream(new File("d:/a.xml")));
		JSONObject xml = (JSONObject)obj.get("xml");
		System.out.println("hello");
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
