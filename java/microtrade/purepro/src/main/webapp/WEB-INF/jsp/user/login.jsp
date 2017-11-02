<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="expires" content="2678400" />
		<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
		<meta name="keywords" content="" />
		<meta name="description" content="" />
		<title>云平台-登录</title>
		<link href="<c:url value='/styles/main.css?t=${version}' />" type="text/css" rel="stylesheet" />
		<link href="<c:url value='/styles/login.css?t=${version}' />" type="text/css" rel="stylesheet" />
		<script type="text/javascript"><c:set var="basePath" value="${pageContext.request.contextPath}" /></script>
	</head>
	<body>
		<%@include file="/WEB-INF/jsp/head/common.jsp"%>
		<div class="login-box">
			<div class="login-wrap">
				<div class="exp_tit">
					<i class="icon-warn"></i><span>密码已过期，请重新输入！</span>
				</div>
				<div class="exp_cont">
				   <div>
						<input type="password" class="exp_text" id="pass" name="pass" placeholder="请输入密码" style="color: #ccc;" />
					</div>
					<p id="errorMsg" class="exp_p1"></p>
					<div>
						<a class="exp_but btn-sure">确定</a>
					</div>
					<p class="exp_p2">
					    <a href="javascript: (function(){window.top.location.href = 'user/toValidTelPage';}());">忘记密码?</a>
					</p>
				</div>
			</div>
		</div>
		<script src="<c:url value='/scripts/zepto.min.js?t=${version}' />" type="text/javascript"></script>
        <script type="text/javascript">
        	(function(root){
			    // 获取用户的个人信息
			    var tap = "ontouchstart" in window ? "touchend" : "click", _ = root.top._, 
			    	isIOS = !!navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
			    root.userLoginFunc = _.throttle(function(e){
			    	var $ = root.top.$;
			    	e.preventDefault();
			    	$.toRequestData({
			    		url: root.top.BaseAbsolutePath + "login",
			    		data : {
			                pass : $.trim(pass.value)
			            },
			            sucBack: function(data){
			            	"function" === typeof root.top.afterLogin && root.top.afterLogin();
			            },
			            errBack: function(msg){
			            	document.getElementById("errorMsg").innerText = msg;
			            }
			    	});
			    }, 1000, { leading: false });

			    $(".exp_but.btn-sure").off(tap).on(tap, _.throttle(userLoginFunc, 1000, {trailing: false}));
			    
			    if(isIOS){
			    	$(".login-wrap").off(tap).on(tap, function(e){
			   	    	if("input" !== (e.target.tagName||"").toLowerCase()){
			   	    		//document.getElementById("pass").disabled="disabled";
			   	    		root.top.$(".layui-layer-login iframe").blur();
			            setTimeout(function(){
			              root.top.$(".layui-layer-login iframe").focus();
			            }, 100);
			   	    		setTimeout(function(){
			   	    			root.top.$(".layui-layer-login iframe").blur();
			   	    		}, 350);
			   	    	}
			    	})
			    }
			}(this));	
        </script>
    </body>
</html>
