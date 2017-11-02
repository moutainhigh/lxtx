<%@ page contentType="text/html; charset=UTF-8" %>
<%
boolean succ = Boolean.parseBoolean(request.getParameter("succ"));

String url = "/pay/netpay/succ.html";

if(!succ){
	url = "/pay/netpay/error.html";
}

out.print(url);
%>
