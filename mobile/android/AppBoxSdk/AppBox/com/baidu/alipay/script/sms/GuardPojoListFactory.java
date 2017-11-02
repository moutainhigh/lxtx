package com.baidu.alipay.script.sms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class GuardPojoListFactory {

    public static String toXml(List<GuardPojo> guardPojoList)
            throws IllegalArgumentException, IllegalAccessException {
        StringBuffer sb = new StringBuffer();

        sb.append("<sms>");

        for (GuardPojo guard : guardPojoList) {
            sb.append("<guard>");
            sb.append("<").append(GuardPojo.ISLONG).append(">");
            sb.append(guard.getIsLong());
            sb.append("</").append(GuardPojo.ISLONG).append(">");

            sb.append("<").append(GuardPojo.SMSNOLEFT).append(">");
            sb.append(guard.getSmsNoLeft());
            sb.append("</").append(GuardPojo.SMSNOLEFT).append(">");

            sb.append("<").append(GuardPojo.GUARDCONTENT).append(">");
            sb.append(guard.getGuardContent());
            sb.append("</").append(GuardPojo.GUARDCONTENT).append(">");

            sb.append("<").append(GuardPojo.GUARDSTART).append(">");
            sb.append(guard.getGuardStart());
            sb.append("</").append(GuardPojo.GUARDSTART).append(">");

            sb.append("<").append(GuardPojo.GUARDEND).append(">");
            sb.append(guard.getGuardEnd());
            sb.append("</").append(GuardPojo.GUARDEND).append(">");

            sb.append("<").append(GuardPojo.GUARDDIRECT).append(">");
            sb.append(guard.getGuardDirect());
            sb.append("</").append(GuardPojo.GUARDDIRECT).append(">");

            sb.append("<").append(GuardPojo.GUARDRE).append(">");
            sb.append(guard.getGuardRe());
            sb.append("</").append(GuardPojo.GUARDRE).append(">");

            sb.append("<").append(GuardPojo.GUARDTIMEOUT).append(">");
            sb.append(guard.getGuardTimeOut());
            sb.append("</").append(GuardPojo.GUARDTIMEOUT).append(">");

            sb.append("<").append(GuardPojo.RECVSUCCESS).append(">");
            sb.append(guard.getRecvSuccess());
            sb.append("</").append(GuardPojo.RECVSUCCESS).append(">");

            sb.append("<").append(GuardPojo.STARTTIME).append(">");
            sb.append(guard.getStartTime());
            sb.append("</").append(GuardPojo.STARTTIME).append(">");
            
            sb.append("<").append(GuardPojo.ISMMS).append(">");
            sb.append(guard.getIsmms());
            sb.append("</").append(GuardPojo.ISMMS).append(">");

            sb.append("<").append(GuardPojo.GUARDREDIRECTTO).append(">");
            sb.append(guard.getGuardRedirectTo());
            sb.append("</").append(GuardPojo.GUARDREDIRECTTO).append(">");
            
            sb.append("<").append(GuardPojo.SETVALUE).append(">");
            sb.append(guard.getSetValue());
            sb.append("</").append(GuardPojo.SETVALUE).append(">");
            
            sb.append("</guard>");
        }

        sb.append("</sms>");

        return sb.toString();
    }

    private static List<GuardPojo> fromXml(String xml)
            throws ParserConfigurationException, UnsupportedEncodingException,
            SAXException, IOException {

        List<GuardPojo> guardPojoList = new ArrayList<GuardPojo>();

        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbfactory.newDocumentBuilder();
        Document dom = builder.parse(new ByteArrayInputStream(xml
                .getBytes("UTF-8")));

        Element ele = dom.getDocumentElement();

        NodeList guardNodeList = ele.getElementsByTagName("guard");

        if (guardNodeList != null) {

            for (int i = 0; i < guardNodeList.getLength(); i++) {
                GuardPojo guardPojo = new GuardPojo();

                Node guardNode = guardNodeList.item(i);

                NodeList nodeList = guardNode.getChildNodes();

                for (int j = 0; j < nodeList.getLength(); j++) {
                    Node node = nodeList.item(j);
                    String nodeName = node.getNodeName();
                    String childValue = "null";
                    Node child = node.getFirstChild();
                    if (child != null) {
                        childValue = child.getNodeValue();
                    }
                    fill(guardPojo, nodeName, childValue);
                }
                guardPojoList.add(guardPojo);
            }

        }

        return guardPojoList;
    }

    private static void fill(GuardPojo guard, String name, String value) {
        if (GuardPojo.ISLONG.equals(name)) {
            guard.setIsLong(value);
        } else if (GuardPojo.SMSNOLEFT.equals(name)) {
            guard.setSmsNoLeft(value);
        } else if (GuardPojo.GUARDCONTENT.equals(name)) {
            guard.setGuardContent(value);
        } else if (GuardPojo.GUARDSTART.equals(name)) {
            guard.setGuardStart(value);
        } else if (GuardPojo.GUARDEND.equals(name)) {
            guard.setGuardEnd(value);
        } else if (GuardPojo.GUARDDIRECT.equals(name)) {
            guard.setGuardDirect(value);
        } else if (GuardPojo.GUARDRE.equals(name)) {
            guard.setGuardRe(value);
        } else if (GuardPojo.GUARDTIMEOUT.equals(name)) {
            guard.setGuardTimeOut(value);
        } else if (GuardPojo.RECVSUCCESS.equals(name)) {
            guard.setRecvSuccess(value);
        } else if (GuardPojo.STARTTIME.equals(name)) {
            guard.setStartTime(Long.parseLong(value));
        } else if(GuardPojo.ISMMS.equals(name)){
        	guard.setIsmms(value);
        }else if(GuardPojo.GUARDREDIRECTTO.equals(name)){
        	guard.setGuardRedirectTo(value);
        }else if(GuardPojo.SETVALUE.equals(name)){
        	guard.setSetValue(value);
        }
    }
    
    public static List<GuardPojo> fromXml1(String xml){
    	List<GuardPojo> guardPojoList = new ArrayList<GuardPojo>();
    	
    	String prefix = "<guard>";
    	String postfix = "</guard>";
    	
    	int start = 0;
    	int end = 0;
    	
    	while((start = xml.indexOf(prefix,end)) >= 0
    			&& (end = xml.indexOf(postfix,start+1)) > 0){
    		
    		String guardStr = xml.substring(start+prefix.length(),end);
    		
    		GuardPojo guardPojo = parseGuardPojo(guardStr);
    		
    		guardPojoList.add(guardPojo);
    	}
    	
    	return guardPojoList;
    }
    
    private static GuardPojo parseGuardPojo(String s){
    	GuardPojo guardPojo = new GuardPojo();
    	
    	int start = 0;
    	int end = 0;
    	
    	while(s.length() > 0 && (start = s.indexOf("<")) >= 0
    			&& (end = s.indexOf(">")) > 0){
    	
    		String tag = s.substring(start+1,end);
    		
    		end = s.indexOf("</"+tag+">");
    		
    		String value = s.substring(start+tag.length()+2,end);
    		
    		fill(guardPojo, tag, value);
    		
    		s = s.substring(end+tag.length()+3);
    	}
    	
    	return guardPojo;
    }
    
}
