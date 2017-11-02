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
<meta name="keywords" content="点微科技手机版软件,银·行家,点微科技,手机炒银,贵金属" />
<meta name="description" content="纵览行情，明确策略，实时专家互动，知晓天下财经" />
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
<link href="<c:url value='/Style/css/font_1lxuy1ls3nqrrudi.css' />"
	rel="stylesheet" type="text/css" />
<script src="<c:url value='/Style/js/jquery-1.10.1.min.js' />"></script>
<script src="<c:url value='/Style/js/vue.min.js' />"></script>
<script src="<c:url value='/Style/js/public.js?t=${version}' />"></script>
<script src="<c:url value='/Style/js/layer/layer.js?t=${version}' />"></script>
<link type="text/css"
	href="<c:url value='/Style/js/layer/need/layer.css' />"
	rel="stylesheet" />


<title>我的优惠券</title>
<link type="text/css"
	href="<c:url value='/Style/css/detail.css?t=${version}' />"
	rel="stylesheet" />
<style>
.card-coupons {
	
}

.card-coupons>div a {
	width: 33.3%
}

.card-coupons>.voucher-box {
	padding: 0 15px;
}

.card-coupons>.voucher-box li {
	background: #ab3c3c;
	box-sizing: border-box;
	padding: 0.4em 0 0.4em 0.8em;
	width: 100%;
	border-left: 1.2em solid #dfb938;
	margin-top: 20px;
	position: relative;
	color: #dfb938;
	overflow: hidden;
}

.card-coupons>.voucher-box li>span {
	color: #dfb938;
	font-size: 0.9em;
	display: block;
	height: 20px;
}

.card-coupons>.voucher-box li>.price {
	margin: 1em 0 0.6em 0;
}

.card-coupons>.voucher-box li>.price>b {
	font-size: 3.9em;
	margin-right: 0.1em;
}

.card-coupons>.voucher-box li>p {
	font-size: 0.8em;
	line-height: 1.5em;
}

.right-name {
	position: absolute;
	z-index: 1;
	width: 40px;
	right: 17px;
	top: 0;
	border-left: dashed #d3d3d3 1px;
	height: 100%;
	text-align: center;
	font-size: 1.3em;
	box-sizing: border-box;
	padding-top: 1.7em;
	line-height: 1.2em;
	padding-left: 8px;
}

.y-uan {
	position: absolute;
	z-index: 2;
	width: 42px;
	height: 42px;
	right: -28px;
	top: 0;
	background: #fff;
	border-radius: 50px;
	margin-top: 2.8em;
}

.s-liang {
	position: absolute;
	z-index: 3;
	width: 52px;
	height: 52px;
	right: 72px;
	top: 1em;
	border: #CBC935 solid 2px;
	border-radius: 100%;
	line-height: 52px;
	text-align: center;
	color: #fff8a6;
	transform: rotate(-15deg)
}

.s-dai {
	position: absolute;
	z-index: 4;
	width: 52px;
	height: 52px;
	right: 152px;
	top: 1em;
	font-size:40px;
	line-height: 52px;
	text-align: center;
	color: #fff8a6;
	transform: rotate(-15deg)
}

.gray {
	-webkit-filter: grayscale(100%);
	-moz-filter: grayscale(100%);
	-ms-filter: grayscale(100%);
	-o-filter: grayscale(100%);
	filter: grayscale(100%);
	filter: gray;
}

.bottom_more {
	margin: 15px;
/* 	background: #303030; */
}
</style>
<script type="text/javascript">
	var vue;
	$(function() {
		vue = new Vue({
			el : 'body',
			data : {
				index : 0,
				items : [ '未使用', '已使用', '已过期' ],
				page_all : null,
				page : 0,
				list : null,
			},
			methods : {
				init : function() {
					this.nextPage();
				},
				nextPage : function() {
					var self = this;
					var pagesize = 10;
					loadshow();
					postMsg('${basePath}/coupon/getCoupon', {
// 						"pageindex" : this.page,
// 						"pagesize" : pagesize,
						"type" : this.index
					}, function(data) {
						loadhide();
// 						self.page_all = Math.ceil(data.totals / pagesize);
						if (self.list == null || self.list.length == 0) {
							self.list = data.List;
// 						} else {
// 							self.list = self.list.concat(data.List)
						}

// 						self.page++;
					})
				},
				tab : function(index) {
					this.page = 0;
					this.index = index;
					this.list = null;
					this.nextPage();
				},
				getLocalTime : function(str) {
					return getLocalTime(str)
				},
				to_detail : function(obj) {//页面跳转
					obj.status = this.index;
					window.sessionStorage.coupon = JSON.stringify(obj);
					if(obj.couponType==1){
						location.href = "${basePath}/coupon/couponDetail";
					}else{
						location.href = "${basePath}/coupon/couponRateDetail";
					}
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
		<div class="head-menu uk-block  uk-block-default">
			<a v-for="item in items" v-bind:class="{ 'active': index==$index }"
				@click="tab($index)" href="#">{{ item }}</a>
		</div>
		<div class="voucher-box">
			<ul >
				<div v-for="item in list">
<!-- 				<li v-for="item in list" @click="to_detail(item)"><span><template -->
					<li v-bind:class="{'gray':index!=0||item.usedFlag==false}"  @click="to_detail(item)">
						<template v-if="item.couponType==2">
							<div class="price">
								<b>{{item.rate}}</b>折
							</div>
							<p>
								使用范围：满 {{item.useAmount}} 元使用
							</p>
						</template>
						<template v-if="item.couponType==1">
							<div class="price">
								<b>{{item.couponAmount}}</b>元
							</div>
							<p>
								使用范围：全场使用
							</p>
						</template>
						<p>
							有效时间：{{item.addTime}} 至 {{item.overdueTime}}
						</p>
						<div class="right-name">{{item.couponName}}</div>
						<div class="y-uan"></div>
						<div class="s-liang">
							<b>1</b><small>张</small>
						</div>
						<template v-if="item.usedFlag==false">
							<div class="s-dai">
								<big>待</big>
							</div>
						</template>
					</li>
				</div>
			</ul>
		</div>
		<a class="bottom_no_more" v-if="page_all<=page">没有更多了~</a>
		<a class="bottom_more" @click="nextPage()" v-if="page_all>page">加载更多</a>
	</div>


	<div class="loader">
		<div class="act">
			<div class="spinner">
				<div class="double-bounce1" ></div>
				<div class="double-bounce2"></div>
			</div>
		</div>
		<div class="bg"></div>
	</div>

</body>
</html>
