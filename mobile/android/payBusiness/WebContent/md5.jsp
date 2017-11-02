<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.qlzf.commons.helper.MD5Encrypt,com.tenpay.util.*" %>
<%
String s = "appid=wxdb14619259b870ec&body=1323&mch_id=1367426002&nonce_str=CA793D8B79C1B6665CF109D6077A8277&notify_url=http://115.28.52.43:9020/pay/synch/netpay/wxNotifyPhp.do&out_trade_no=21241421241&spbill_create_ip=0:0:0:0:0:0:0:1&total_fee=1&trade_type=APP&key=e997a95bd10da33b8b970d610f53a587";

//FD4E72E975C7389BC402A9B899AF898D

out.println(MD5Encrypt.MD5Encode(s).toUpperCase());

out.println("<br/>");

out.println(MD5Util.MD5Encode(s,"utf-8").toUpperCase());
%>
