<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="expires" content="2678400" />
	<meta name="format-detection" content="telephone=no" /> 
	<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
	<meta name="keywords" content="" />
	<meta name="description" content="" />
	<title>充值</title>
	<link href="<c:url value='/payment/styles/main.css?t=${version}' />" type="text/css" rel="stylesheet" />
	<link href="<c:url value='/payment/styles/zichan.css?t=${version}' />" type="text/css" rel="stylesheet" />
	<script type="text/javascript">
		<c:set var="basePath" value="${pageContext.request.contextPath}" />
	</script>
</head>
	<body>
		<div class="recharge-box">
			<div class="accounts-wrap">
				<div class="image-wrap"><img src="<c:out value='${userInfo.headimgurl}' />" /></div>
				<div class="userinfo">
					<div class="name">${userInfo.wxnm}</div>
				</div>
		    </div>
		    <!--充值金额-->
			<div class="money-setting-wrap">
				<div class="setting-title">充值成功,已为您账户充值${price}元.</div>
				<div class="setting-content"></div>
			</div>
		</div>
	</body>
	<script src="<c:url value='/payment/scripts/main.js?t=${version}' />" type="text/javascript"></script>
	<script src="<c:url value='/scripts/util.js?t=${version}' />" type="text/javascript"></script>
	<script>
	$(document).ready(function() {
		setTimeout(function(){
			var path = global_util.getAppPath(); 
			window.location = path + "/";
		}, 1500);		
	});
	</script>
	
</html>