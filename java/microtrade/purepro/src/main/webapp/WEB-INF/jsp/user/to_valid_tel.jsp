<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html>
	<head>
        <meta http-equiv="Content-Type" content="applicationnd.wap.xhtml+xml;charset= UTF-8" />
        <meta http-equiv="expires" content="2678400" />
        <meta name="format-detection" content="telephone=no" /> 
        <meta name="viewport" content="width=device-width,initial-scale=1.3, minimum-scale=1.0, maximum-scale=2.0" />
        <meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
        <meta name="keywords" content="" />
        <meta name="description" content="" />
        <title>云平台-验证手机号</title>
        <link href="<c:url value='/styles/main.css?t=${version}' />" type="text/css" rel="stylesheet" />
	    <link href="<c:url value='/styles/yanzheng.css?t=${version}' />" type="text/css" rel="stylesheet" />
        <script src="<c:url value='/scripts/prepare.js?t=${version}' />" type="text/javascript"></script>
	</head>
	<body>
		<%@include file="/WEB-INF/jsp/head/common.jsp"%>
		<div class="forget-box">
            <div class="title">为了您的账户安全，请先进行身份验证</div>
            <div class="phone-wrap table">
            	<input type="hidden" id="mobile1" value="${session_user.mobile}" />
                <div class="table-cell"><span>已绑定手机号码：</span><span id="mobile"></span></div>
                <div class="table-cell sendvalidcode-wrap"><a id="btnvalid" class="sendvalidcode active">获取验证码</a></div>
            </div>
            <div class="content-wrap">
                <input type="text" id="validcode" name="validcode" pattern="[0-9]*" maxlength="4" class="textvalue" placeholder="请输入短信验证码" />
                <p id="errorMsg" style="color:#f00;"></p>
                <a class="btn-sure">下一步</a>
            </div>
        </div>
        <script src="<c:url value='/scripts/main.js?t=${version}' />" type="text/javascript"></script>
		<script src="<c:url value='/scripts/validtel.js?t=${version}' />" type="text/javascript"></script>
        
        <script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>
        <script src="<c:url value='/scripts/share.js' />" type="text/javascript"></script>
        <script type="text/javascript" src="http://pingjs.qq.com/h5/stats.js" name="MTAH5" sid="500049057" ></script> 
	</body>
</html>