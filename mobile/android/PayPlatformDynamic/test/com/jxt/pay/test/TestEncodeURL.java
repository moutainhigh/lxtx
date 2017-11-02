package com.jxt.pay.test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TestEncodeURL {

	public static void main(String[] args) throws UnsupportedEncodingException{
		System.out.println(URLEncoder.encode("url=http://g.fmmgame.net:8097/cmccfmm/fmmpay&type=zztFmm&mobileId=4286707&imei=865703020367424&imsi=460027216363328&channelId=270&serviceId=140820001&consumeCode=30000831362601", "utf-8"));
	}
	
}
