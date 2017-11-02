<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="applicationnd.wap.xhtml+xml;charset= UTF-8" />
        <meta http-equiv="expires" content="2678400" />
		<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
		<meta name="keywords" content="" />
		<meta name="description" content="" />
		<title>云平台-个人设置</title>
		<link href="<c:url value='/styles/main.css?t=${version}' />" type="text/css" rel="stylesheet" />
		<link href="<c:url value='/styles/geren.css?t=${version}' />" type="text/css" rel="stylesheet" />
		<script src="<c:url value='/scripts/prepare.js?t=${version}' />" type="text/javascript"></script>
		<script type="text/javascript">
			<c:set var="basePath" value="${pageContext.request.contextPath}" />
		</script>
	</head>
	
	<body>
		<%@include file="/WEB-INF/jsp/head/common.jsp"%>
		<div class="forget">
		    <div class="center-list-wrap">
                <ul>
                	<c:if test="${checkPwd!=1}">
	                    <li class="table bottom-wrap" data-index="0">
	                        <div class="content-wrap table-cell">
	                            <div class="title">修改密码</div>
	                            <div class="title-tip">为了您的资金安全，请妥善保管您的密码</div>
	                        </div>
	                        <div class="table-cell" style="padding-bottom: 40px;"><span class="earrow earrow-right"></span></div>
	                    </li>
                    </c:if>
                    <li class="table bottom-wrap" data-index="1">
                        <div class="content-wrap table-cell">
                            <div class="title"><span>验证手机</span><span id="mobile" style="padding-left: 0.5em; color: #1d84d4; font-size: 13px;"></span></div>
                            <div class="title-tip">若您的验证手机丢失或停用，请立即更换</div>
                        </div>
                        <div class="table-cell" style="padding-bottom: 40px;"><span class="earrow earrow-right"></span></div>
                    </li>
                </ul>
            </div>
		</div>
		<script src="<c:url value='/scripts/main.js?t=${version}' />" type="text/javascript"></script>
		<script src="<c:url value='/scripts/setting.js?t=${version}' />" type="text/javascript"></script>

        <script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>
        <script src="<c:url value='/scripts/share.js' />" type="text/javascript"></script>
        <script type="text/javascript" src="http://pingjs.qq.com/h5/stats.js" name="MTAH5" sid="500049057" ></script>
        <script type="text/javascript">
            (function(root){
                //root.ShareHost = "http://ruinuo.zjwlce.cn/pointchat/Chat/ChatPushServlet"; 
                //initShare();
            }(this));
        </script>
	</body>
</html>