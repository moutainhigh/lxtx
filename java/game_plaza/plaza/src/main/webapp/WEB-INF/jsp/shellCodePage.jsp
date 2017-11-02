<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport"
	content="width=device-width,inital-scale=1.0,minimum-scale=0.5,maximum-scale=2.0,width=640,user-scalable=no">
<meta name="wap-font-scale" content="no">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta content="black" name="apple-mobile-web-app-status-bar-style">
<meta name="format-detection" content="telephone=no">
<script type="text/javascript">
	<c:set var="basePath" value="${pageContext.request.contextPath}" />
</script>

<title>兑换</title>
<script language="javascript"
	src="<c:url value='/statics/shop/jquery-1.10.2.min.js' />"></script>
<script language="javascript"
	src="<c:url value='/statics/shop/jquery.cookie.js' />"></script>
<script src="<c:url value='/statics/shop/jweixin-1.0.0.js' />"></script>
	<style type="text/css">
		.duihuan{
		    top: 0;
		    font-size: 30px;
		    color: red;
		    line-height: 89px;
		    vertical-align: middle;
		}
	</style>
</head>

<body>
	<script
		src="<c:url value='/statics/shop/jquery.SuperSlide.2.1.1.js' />"></script>
	<script src="<c:url value='/scripts/util.js?t=${version}' />"></script>
	<div style="text-align: center;">
  		<span style="color:red;font-size: 26px;">请先扫码关注</span><br/><img width="80%;" src="${shellCode}"/>
  		<br/>
		<a class="duihuan" href="${basePath}/wx/duobaoredirect1/${wxid}/transferfromgame?source=${source}">直接兑换</a>
   	</div>
</body>
</html>