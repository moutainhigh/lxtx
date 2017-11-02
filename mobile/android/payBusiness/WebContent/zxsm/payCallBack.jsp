<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<body>
<div align="center">
订单号：<%=request.getParameter("orderId") %>
<%
if("true".equals(request.getParameter("succ"))){
%>
<img src="./images/success.png">
<%}else{ %>
失败
<%} %>
</div>
</body>
<html>