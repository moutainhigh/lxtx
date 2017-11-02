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
<%-- <link rel="apple-touch-icon" href="<c:url value='/statics/game_center/touch-icon-iphone.png' />"> --%>
<%-- <link rel="apple-touch-icon" sizes="76x76" href="<c:url value='/statics/game_center/touch-icon-ipad.png' />"> --%>
<%-- <link rel="apple-touch-icon" sizes="120x120" href="<c:url value='/statics/game_center/touch-icon-iphone-retina.png' />"> --%>
<%-- <link rel="apple-touch-icon" sizes="152x152" href="<c:url value='/statics/game_center/touch-icon-ipad-retina.png' />"> --%>
<script type="text/javascript">
	<c:set var="basePath" value="${pageContext.request.contextPath}" />
</script>

<title>充值中心</title>
<link href="<c:url value='/statics/shop/sample.css?t=${version}' />" rel="stylesheet"
	type="text/css">
<script src="<c:url value='/statics/shop/hm.js?t=${version}' />"></script>
<script language="javascript"
	src="<c:url value='/statics/shop/jquery-1.10.2.min.js' />"></script>
<script language="javascript"
	src="<c:url value='/statics/shop/jquery.cookie.js' />"></script>
<script language="javascript"
	src="<c:url value='/statics/shop/ajaxsubmit.js' />"></script>
<script src="<c:url value='/statics/shop/jweixin-1.0.0.js' />"></script>

<link href="<c:url value='/statics/shop/wayward.css?t=${version}' />"
	rel="stylesheet" type="text/css">
<link href="<c:url value='/statics/shop/pay.css?t=${version}' />" rel="stylesheet"
	type="text/css">
	<style type="text/css">
		.duihuan{
		    position: absolute;
		    right: 20px;
		    top: 0;
		    font-size: 30px;
		    color: #50515a;
		    line-height: 89px;
		    vertical-align: middle;
		}
		.duihuan img{
		    margin: -2px 16px 0 12px;
   			vertical-align: middle;
		}
	</style>
</head>

<body>
	<input type="hidden" id="source" name="source" value="${source}" />
	<input type="hidden" id="config" name="config" value="${config}" />
	<script
		src="<c:url value='/statics/shop/jquery.SuperSlide.2.1.1.js' />"></script>
	<script src="<c:url value='/scripts/util.js?t=${version}' />"></script>
	<script type="text/javascript">
		var host = window.location.host;
		var ctx="http://"+ host + '<%=request.getContextPath()%>';
		function showDiv(gold, money) {
			$('#gold').html(gold);
			$('#money').html(money);
			$('#popDiv').show();
			$('#bg').show();
		}
		var chackbut = true;  //避免重复提交
		function gotopay() {
			if(chackbut){
                chackbut=false;
				wxPay();
			}
		}

		// 微信支付
		function wxPay() {
			//         	if(global_util.validRepayTime()){
			//					layer.msg("抱歉：请在工作日进行充值或提现！",{time:2e3},function(){})
			//				}else{
			if (typeof WeixinJSBridge == "undefined") {
				if (document.addEventListener) {
					document.addEventListener('WeixinJSBridgeReady',
							onBridgeReady, false);
				} else if (document.attachEvent) {
					document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
					document
							.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
				}
			} else {
				onBridgeReady();
			}
			//				}
		}

		function onBridgeReady(){
        	$.get(ctx+"/wx/shake_fill", {money: $('#money').html() * 100, source : $("#source").val(),config:$("#config").val() }, function(response) {
        		var data = response.data;
       			var dataMap = response.data;
                 $("#appId").val(dataMap.appId);
                 $("#orderId").val(dataMap.orderId);
                 //$("#payType").val(dataMap.payType);
                 //$("#price").val(dataMap.price);
                 $("#price").val(dataMap.price);
                 $("#callBackUrl").val(dataMap.callBackUrl);
                 $("#notifyUrl").val(dataMap.notifyUrl);
                 $("#sign").val(dataMap.sign);
                 $("#checkout").submit();
        	});
        }

		function closeDiv() {
			$('#popDiv').hide();
			$('#bg').hide();
		}
	</script>
	<style>
.txtScroll-top {
	width: 525px;
	overflow: hidden;
	position: relative;
}

.txtScroll-top .infoList li {
	height: 46px;
	line-height: 46px;
}

.clean {
	width: 525px !important;
	overflow: visible !important;
	margin: 0 !important;
	border-radius: 0 !important;
	background: none !important;
}

.clean a {
	background: none !important;
	width: 525px !important;
	height: 46px !important;
	text-align: center !important;
	float: none !important;
	margin: 0 !important;
}
</style>
	</style>
	<div class="paybox" id="popDiv" style="display: none;">
		<div class="paytitle">
			<p>选择支付方式</p>
		</div>
		<div class="close" onclick="closeDiv();">
			<img src="<c:url value='/statics/shop/pay_06.png' />" alt="">
		</div>
		<div class="paybody">
			<ul>
				<li onclick="gotopay()"><p>
						<img src="<c:url value='/statics/shop/pay_13.png' />" alt="">
					</p>
					<p>微信支付</p></li>
			</ul>
		</div>
		<div class="payfoot">
			<p>
				<span>购买：</span><span id="gold" style="color: #FFFFFF;">60,000</span>金币<span
					class="sleft">价格：</span><span id="money" style="color: #FFFFFF;">6</span>元
			</p>
		</div>
	</div>
	<div id="bg" class="paybg" style="display: none;"></div>


	<div class="overall">
		<header>
			<a class="return"
<%-- 				href="${basePath}/game_center/${user.wxid}/${user.chnno}"><img --%>
				href="javascript:history.go(-1)"><img
				src="<c:url value='/statics/shop/left.png' />">返回</a> <span>充值</span>
				
<!-- 				<a class="duihuan" href="http://game.juzizou.com/plaza/wx/duobaoredirect/transferfromgame">兑换<img -->
				<a class="duihuan" href="${basePath}/checkIssubscribe/${user.wxid}?source=${source}">兑换<img
				src="<c:url value='/statics/shop/right.png' />"></a>
		</header>
		<section>
			<div class="back"
				style="background: #15161e url(<c:url value='/statics/shop/back2.png' />);">
				<div class="integral">
					<div class="prompt">
						<span style="margin: 10px 12px;"><img
							src="<c:url value='/statics/shop/prompt.png' />"></span>
						<!---->
						<!--<p><marquee width="586px;">欢迎来到乐玩九州中心，为了给您更好的游戏体验，建议您关注我们的微信公众号“乐玩九州”！</marquee></p>-->
						<!---->

						<div class="txtScroll-top">
							<div class="bd">
								<div class="tempWrap"
									style="overflow: hidden; position: relative; height: 46px">
									<ul class="infoList"
										style="height: 230px; position: relative; padding: 0px; margin: 0px; top: -79.7658px;">
										<li class="clean clone" style="height: 46px;"></li>
										<li class="clean" style="height: 46px;"><a href=""
											style="color: #fff; font-size: 23px;">欢迎来到乐玩九州中心，为了给您更好的游戏体验，建议您关注我们的微信公众号“乐玩九州”！</a></li>
										<li class="clean" style="height: 46px;"></li>
										<li class="clean" style="height: 46px;"></li>
										<li class="clean clone" style="height: 46px;"><a href=""
											style="color: #fff; font-size: 23px;">欢迎来到乐玩九州中心，为了给您更好的游戏体验，建议您关注我们的微信公众号“乐玩九州”！</a></li>
									</ul>
								</div>
							</div>
						</div>

						<script type="text/javascript">
							jQuery(".txtScroll-top").slide({
								titCell : ".hd ul",
								mainCell : ".bd ul",
								autoPage : true,
								effect : "topLoop",
								autoPlay : true,
								vis : 1,
								delayTime : 700
							});
						</script>

					</div>
				</div>
				<ul class="another">
					<!--未达成的按钮标签为 class="dark" img里的路径改变-->
<!-- 					<li> -->
<!-- 						<div class="game"> -->
<%-- 							<img src="<c:url value='/statics/shop/wgold.png' />"> --%>
<!-- 						</div> -->
<!-- 						<hgroup> -->
<!-- 							<p>10,000&nbsp;金币</p> -->
<!-- 						</hgroup> <a onclick="showDiv('10,000','1')"><span>1元</span></a> -->
<!-- 					</li> -->
					<li>
						<div class="game">
							<img src="<c:url value='/statics/shop/wgold.png' />">
						</div>
						<hgroup>
							<p>100,000&nbsp;金币</p>
						</hgroup> <a onclick="showDiv('100,000','10')"><span>10元</span></a>
					</li>
					<li>
						<div class="game">
							<img src="<c:url value='/statics/shop/wgold.png' />">
						</div>
						<hgroup>
							<p>500,000&nbsp;金币</p>
						</hgroup> <a onclick="showDiv('500,000','50')"><span>50元</span></a>
					</li>
					<li>
						<div class="game">
							<img src="<c:url value='/statics/shop/wgold.png' />">
						</div>
						<hgroup>
							<p>1,000,000&nbsp;金币</p>
						</hgroup> <a onclick="showDiv('1,000,000','100')"><span>100元</span></a>
					</li>
					<li>
						<div class="game">
							<img src="<c:url value='/statics/shop/wgold.png' />">
						</div>
						<hgroup>
							<p>5,000,000&nbsp;金币</p>
						</hgroup> <a onclick="showDiv('5,000,000','500')"><span>500元</span></a>
					</li>
					<li>
						<div class="game">
							<img src="<c:url value='/statics/shop/wgold2.png' />">
						</div>
						<hgroup>
							<p>10,000,000&nbsp;金币</p>
						</hgroup> <a onclick="showDiv('10,000,000','1000')"><span>1000元</span></a>
					</li>
					<li>
						<div class="game">
							<img src="<c:url value='/statics/shop/wgold2.png' />">
						</div>
						<hgroup>
							<p>50,000,000&nbsp;金币</p>
						</hgroup> <a onclick="showDiv('50,000,000','5000')"><span>5000元</span></a>
					</li>
					<li>
						<div class="game">
							<img src="<c:url value='/statics/shop/wgold4.png' />">
						</div>
						<hgroup>
							<p>100,000,000&nbsp;金币</p>
						</hgroup> <a onclick="showDiv('100,000,000','10000')"><span>10000元</span></a>
					</li>
				</ul>
			</div>
<!-- 				<div style="text-align: center;"> -->
<%-- 		        	<c:if test="${user.headimgurl==''||user.headimgurl=='null'||user.headimgurl==null}"> --%>
<%-- 		        		<span style="color:red;font-size: 26px;">请先扫码关注</span><br/><img width="80%;" src="${shellCode}"/> --%>
<%-- 		        	</c:if> --%>
<!-- 		    	</div> -->
		</section>
		<form id="checkout" name="checkout" method="post" action="http://pay.enanea.com/paycenter/appclient/getUrl.do">
			<input name="appId" id="appId" value="" type="hidden">
			<input name="orderId" id="orderId" value="" type="hidden">    
			<input id="price" name="price" value="" type="hidden">    
			<input name="desc" id="desc" value="jifei" type="hidden">	
			<input name="payType" id="payType" value="wxgzh" type="hidden">    
			<input name="callBackUrl" id="callBackUrl" value="" type="hidden">
			<input name="notifyUrl" id="notifyUrl" value="" type="hidden">
			<input name="sign" id="sign" value="" type="hidden">
			<input name="source" id="source" value="gamecenter" type="hidden">
		</form>

		<script>
			$(function() {
				wx.config({
					debug : false,
					appId : '',
					timestamp : '',
					nonceStr : '',
					signature : '',
					jsApiList : [
					// 所有要调用的 API 都要加到这个列表中
					'checkJsApi', 'openLocation', 'getLocation',
							'onMenuShareTimeline', 'onMenuShareAppMessage' ]
				});

				wx.ready(function() {
					wx.onMenuShareAppMessage({
						title : '',
						desc : '',
						link : '',
						imgUrl : '',
						trigger : function(res) {
							// 不要尝试在trigger中使用ajax异步请求修改本次分享的内容，因为客户端分享操作是一个同步操作，这时候使用ajax的回包会还没有返回
							//                     alert('用户点击发送给朋友');
						},
						success : function(res) {
							$.post("share", {
								'success' : 'yes'
							}, function() {

							})
						},
						cancel : function(res) {
						},
						fail : function(res) {
						}
					});

					wx.onMenuShareTimeline({
						title : '',
						link : '',
						imgUrl : '',
						trigger : function(res) {
							// 不要尝试在trigger中使用ajax异步请求修改本次分享的内容，因为客户端分享操作是一个同步操作，这时候使用ajax的回包会还没有返回
						},
						success : function(res) {
							$.post("share", {
								'success' : 'yes'
							}, function() {

							})
						},
						cancel : function(res) {
						},
						fail : function(res) {
						}
					});
				});

			})
		</script>
		<script>
			var _hmt = _hmt || [];
			(function() {
				var hm = document.createElement("script");
				hm.src = "https://hm.baidu.com/hm.js?891d2a14806d89e7ab281d30a3d3f786";
				var s = document.getElementsByTagName("script")[0];
				s.parentNode.insertBefore(hm, s);
			})();
		</script>
	 	<script type="text/javascript" src="http://pingjs.qq.com/h5/stats.js" name="MTAH5" sid="500049057" ></script> 
	</div>
</body>
</html>