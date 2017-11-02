<%@page import="com.jxt.pay.appclient.utils.GetData"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
String url = "http://182.92.149.179/open_gate/web_game_fee.php";
String params = "pid=BJLX&app_special=ty_pay&money=20&time=1492002697&tel=18178915835&imsi=460037751007929&imei=A10000423B4E56&iccid=89860316847912085580";

String resp = GetData.getData(url, params);

out.println(resp);
%>