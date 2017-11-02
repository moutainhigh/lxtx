<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>

<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="expires" content="2678400" />
<meta name="format-detection" content="telephone=no" />
<meta name="viewport"
	content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
<meta name="keywords" content="" />
<meta name="description" content="" />
<link type="text/css"
	href="<c:url value='/Style/css/public.css?t=${version}' />"
	rel="stylesheet" />
<script type="text/javascript">
	<c:set var="basePath" value="${pageContext.request.contextPath}" />
</script>
<script>
	//页面加载效果
	function loadpage() {
		
		
		var load_html = '<div class="page_load" id="page_losd" style="position:fixed; width:100%; height:100%; background:#000000; z-index:999999; top:0; left:0; bottom:0; right:0;">'
				+ '<div class="loader show">'
				+ '<div class="bg"></div>'
				+ '  <span class="text">页面加载中...</span>' + '</div></div>'
		document.write(load_html);

	}
	loadpage();
	document.onreadystatechange = loadpagecom;
	//加载状态为complete时移除loading效果
	function loadpagecom(close) {
		if (close || document.readyState == "complete") {
			var loadingMask = document.getElementById('page_losd');
			if (!loadingMask)
				return;
			loadingMask.parentNode.removeChild(loadingMask);
		}
	}
</script>
<!-- <link href="http://at.alicdn.com/t/font_1lxuy1ls3nqrrudi.css" -->
<!-- 	rel="stylesheet" type="text/css" /> -->
<script src="<c:url value='/Style/js/jquery-1.10.1.min.js' />"></script>
<script src="<c:url value='/Style/js/vue.min.js' />"></script>
<script src="<c:url value='/Style/js/public.js?t=${version}' />"></script>
<script src="<c:url value='/Style/js/layer/layer.js' />"></script>
<link type="text/css"
	href="<c:url value='/Style/js/layer/need/layer.css' />"
	rel="stylesheet" />


<title>卡券详情</title>
<link type="text/css"
	href="<c:url value='/Style/css/detail.css?t=${version}' />"
	rel="stylesheet" />
<style>
.card-coupons {
	padding: 15px;
}

.coupon-block {
	background: #dfb938;
	color: #303030;
	position: relative;
	padding: 0.8em 0;
}

.coupon-block>h3 {
	color: #303030;
	text-align: center;
	font-size: 1.2em;
}

.coupon-block .date-riqi {
	position: relative;
	box-sizing: border-box;
	padding: 0 10px;
}

.coupon-block .date-riqi>hr {
	margin: 6px 0 0 0;
	border-color: #303030
}

.coupon-block .date-riqi>div {
	background: #dfb938;
	position: absolute;
	left: 0;
	top: -5px;
	width: 80%;
	margin-left: 10%;
	text-align: center;
	font-size: 11px;
}

.moneyValue {
	font-size: 4.3em;
	font-weight: bold;
	padding: 0.5em 0 0.1em 10px;
}

.moneyValue>small {
	font-weight: normal;
	font-size: 0.3em;
	margin-left: 0.2em;
}

.coupon-block>p {
	color: #847033;
	padding-left: 12px;
	font-size: 0.9em;
}

.top-y {
	position: absolute;
	/* 	background: #000; */
	top: 0;
	width: 100%;
}

.bot-y {
	position: absolute;
	/* 	background: #000; */
	bottom: 0;
	width: 100%;
}

.coupon-block>img {
	width: 80px;
	position: absolute;
	right: 15px;
	bottom: 30px;
}

.suliang {
	margin: 12px 0;
	/* 	background: #1e1e1e; */
	line-height: 2.6em;
	font-size: 0.9em;
	padding: 0 12px;
}

.s-dai {
	position: absolute;
	z-index: 4;
	width: 52px;
	height: 52px;
	right: 152px;
	top: 2em;
	font-size:40px;
	line-height: 52px;
	text-align: center;
	color: gray;
	transform: rotate(-15deg)
}
.suliang span {
	color: #dfb938
}

.smguize {
	background: #1e1e1e;
	color: #aeaeae;
	padding: 10px;
	margin-bottom: 30px;
}

.smguize h4 {
	margin: 0;
	font-size: 1em;
	color: #aeaeae;
}

.smguize p {
	color: #6e6e6e;
	font-size: 0.9em;
	line-height: 2.2em;
}

.meue-nav {
	bottom: 0;
	/* 	background: #1e1e1e; */
	/* 	border-top: 2px solid #484848; */
	width: 100%;
	left: 0;
	height: 60px;
}

.meue-nav a {
	background: #dfb938;
	color: #000;
	display: block;
	width: 140px;
	margin: 0 auto;
	text-align: center;
	border-radius: 3px;
	line-height: 3em;
	margin-top: 7px;
}
</style>
<script type="text/javascript">
	var vue;
	$(function() {
		vue = new Vue({
			el : 'body',
			data : {
				item : null,
			},
			methods : {
				init : function() {
					this.item = window.sessionStorage.getItem("coupon");
					console.log(this.item);
					this.item = JSON.parse(this.item);
					if (this.item) {
					} else {
					}
				},
				to_home:function(){
					location.href ="${basePath}/index?wxid=${session_user.wxid}";
				}
			}
		})
		vue.init();

	})
</script>
<title></title>
</head>
<body>


	<div class="card-coupons">
		<div class="coupon-block">
			<h3>{{item.couponName}}</h3>
			<div class="date-riqi">
				<hr>
				<hr>
				<div>
					有效时间：{{item.addTime}} 至 {{item.overdueTime}}
				</div>
			</div>
			<div class="moneyValue">
				{{item.couponAmount}}<small>元</small>
			</div>
			<p>
				使用范围：
				<template v-if="true">全场使用</template>
			</p>
			<div class="top-y">&nbsp;</div>
			<div class="bot-y">&nbsp;</div>
			<img src="<c:url value='/Style/imgs/Ymoney.png'/>">
		</div>
		<div class="suliang">
			<span>1</span> 张
		</div>
		<template v-if="item.usedFlag==false">
			<div class="s-dai">
				<big>待</big>
			</div>
		</template>
		<div class="smguize">
			<h4>使用说明：</h4>
			<p>
				面额：{{item.couponAmount}}元/张；<br />如何领券：关注云平台，即可获得若干张{{item.couponAmount}}元现金券，平台会持续赠送100元现金券，赠满为止；<br />如何使用：玩家可以使用{{item.couponAmount}}元现金券购买任意产品，盈利及本金全属玩家，无需支付其它费用。<br />券的属性：<br />1.现金劵可购买全部品种产品；<br />2.现金劵有效期：5个工作日；<br />3.所有优惠活动最终解释权归【九州微云】所有。
			</p>
		</div>
	</div>

	<div class="loader">
		<div class="act">
			<div class="spinner">
				<div class="double-bounce1"></div>
				<div class="double-bounce2"></div>
			</div>
		</div>
		<div class="bg"></div>
	</div>

</body>
</html>
