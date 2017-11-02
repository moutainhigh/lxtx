<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="applicationnd.wap.xhtml+xml;charset= UTF-8" />
        <meta http-equiv="expires" content="2678400" />
		<meta name="format-detection" content="telephone=no" />
		<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
		<meta name="keywords" content="" />
		<meta name="description" content="" />
		<title>云平台-修改手机号</title>
		<link href="<c:url value='/styles/main.css?t=${version}' />" type="text/css" rel="stylesheet" />
		<link href="<c:url value='/styles/yanzheng.css?t=${version}' />" type="text/css" rel="stylesheet" />
		<script src="<c:url value='/scripts/prepare.js?t=${version}' />" type="text/javascript"></script>
		<script type="text/javascript">
			<c:set var="basePath" value="${pageContext.request.contextPath}" />
		</script>
	</head>
    <body>
    	<%@include file="/WEB-INF/jsp/head/common.jsp"%>
	    <div class="forget-box">
			<div class="title">修改手机号</div>
			<div class="phone-wrap table">
				<div class="table-cell">
					<input type="text" id="mobile" class="textvalue" pattern="[0-9]*"
						maxlength="11" placeholder="请输入新手机号码" />
				</div>
				<div class="table-cell sendvalidcode-wrap">
					<a id="btnvalid" class="sendvalidcode">获取验证码</a>
				</div>
			</div>
			<div class="content-wrap">
				<input type="text" id="validcode" name="validcode" pattern="[0-9]*"
					maxlength="4" class="textvalue" placeholder="请输入短信验证码" />
				<p id="errorMsg" style="color:#f00;">&nbsp;</p>
				<a class="btn-sure">确定</a>
			</div>
		</div>
		<script src="<c:url value='/scripts/main.js?t=${version}' />" type="text/javascript"></script>
        <script src="<c:url value='/scripts/altertel.js?t=${version}' />" type="text/javascript"></script>
        
	    <script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>
        <script src="<c:url value='/scripts/share.js' />" type="text/javascript"></script>
        <script type="text/javascript" src="http://pingjs.qq.com/h5/stats.js" name="MTAH5" sid="500049057" ></script>
        <script type="text/javascript">
            (function(root){
            	root.ShareHost = "${basePath}/user/userSetting"; 
            }(this));
        </script>
    </body>
</html>