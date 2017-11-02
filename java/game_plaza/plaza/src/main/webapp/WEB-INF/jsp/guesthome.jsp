<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="expires" content="2678400" />
<meta name="format-detection" content="telephone=no" />
<meta name="viewport"
	content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
<meta name="keywords" content="" />
<meta name="description" content="" />
<title>云平台</title>
<link href="<c:url value='/styles/main.css?t=${version}' />"
	rel="stylesheet" />
<link href="<c:url value='/styles/jiaoyi.css?t=${version}' />"
	rel="stylesheet" />
<script src="<c:url value='/scripts/jquery.js?t=${version}' />"></script>
<script src="<c:url value='/scripts/guest_prepare.js?t=${version}' />"></script>
<script type="text/javascript">
	<c:set var="basePath" value="${pageContext.request.contextPath}" />
</script>
<style type="text/css">
.pop-box {
	z-index: 9999; /*这个数值要足够大，才能够显示在最上层*/
	margin-bottom: 3px;
	display: none;
	position: absolute;
}

.pop-box h4 {
	color: #FFF;
	cursor: default;
	height: 18px;
	font-size: 14px;
	font-weight: bold;
	text-align: left;
	padding-left: 8px;
	padding-top: 4px;
	padding-bottom: 2px;
	background: url("../images/header_bg.gif") repeat-x 0 0;
}

.pop-box-body {
	clear: both;
	margin: 4px;
	padding: 2px;
}

.mask {
	position: absolute;
	top: 0px;
	left: 0px;
	filter: Alpha(Opacity = 60);
}

#next, #previous {
	padding-top:-25px;
	width: 50px;
	height: 50px;
	margin: auto;
	position: absolute;
	top: 47%;
}
#next img, #previous img{
	width: 40px;
	height: 40px;
}
</style>

</head>
<body>
	<%@include file="/WEB-INF/jsp/head/common.jsp"%>
	<div class="transaction">
		<div class="tra_ad" style="display: none;">
			<a href=""><img src="" /></a>
		</div>
		<!--账户资产-->
		<div class="boxflex assets-wrap">
			<div class="userinfo-wrap">
				<a href="javascript:void(0)" id="userCenter"><img
					class="userimage" src="${session_user.headimgurl}" />
					<p>个人中心</p> </a>
			</div>
			<div class="cash-asset box_flex_1">
				<div class="asset">
					资产<span id="userProfit" style="color: #eb7d12;">--</span>元
				</div>
				<div class="btn-withdraw-wrap">
					<div class="recharge">
						<span>充值</span>
					</div>
					<div class="withdraw">
						<span>提现</span>
					</div>
					<div class="coupon">
						<span>券</span>
					</div>
				</div>
			</div>
			<!--账户资产-金币-->
			<div class="coin-asset">
				<label>下单倒计时</label>
				<div>
					<i class="icon-clock"></i> <span class="redsymbol">00:00</span>
				</div>
			</div>
		</div>
		<!-- 商品列表 -->
		<div class="goodslist-wrap">
			<ul class="boxflex"></ul>
		</div>
		<!-- 商品详情以及走势图K线 -->
		<div class="goodinfo-wrap">
			<div class="goodinfo boxflex">
				<span class="key">昨收:</span> <span class="value closeprice">--</span>
				<span class="box_flex_1"></span> <span class="key">今开:</span> <span
					class="value openprice">--</span> <span class="box_flex_1"></span>
				<span class="key">最高:</span> <span class="value maxprice">--</span>
				<span class="box_flex_1"></span> <span class="key">最低:</span> <span
					class="value minprice">--</span>
			</div>
			<div class="graph">
				<div id="Kchartbutton">
					&nbsp;&nbsp; <img width="25px"
						src="<c:url value='/styles/images/plus.png' />" />
					&nbsp;&nbsp;&nbsp;&nbsp; <img width="25px"
						src="<c:url value='/styles/images/miuns.png' />" />
				</div>
				<div>
					<div id="chartdiv">
						<img src="<c:url value='/styles/images/loading.gif' />" />
					</div>

				</div>
			</div>
			<div class="graph-kind">
				<ul class="boxflex"></ul>
			</div>
		</div>
		<div class="deal-btn-wrap">
			<div class="table">
				<div class="table-cell btnrise-wrap">
					<label><span>看涨</span></label>
				</div>
				<div class="table-cell btndown-wrap">
					<label><span>看跌</span></label>
				</div>
			</div>
			<p>服务时间：周一~周五9:00~4:00 每日4:30~7:00暂停服务</p>
		</div>
		<div class="holdlist-wrap">
			<ul></ul>
		</div>
	</div>
	<div class="footer_cont table">
		<i class="table-cell arrow arrow-tip"></i> <span class="table-cell">
			<marquee direction="left" scrollamount="3" class="active"></marquee>
		</span>
	</div>
	<form id="form1" runat="server">
		<div id='pop-div' style="width: 100%; height: 100%;" class="pop-box">

			<div style="width: 100%; height: 100%;">
				<img src="<c:url value='/images/newbie/1.png' />"
				style="width: 100%; height: 100%;" id="guide_slide" z-index="999"/>
				<div id="previous" style="right: 86%;" z-index="1000">
					<img src="<c:url value='/images/newbie/arrowLeft.png' />" />
				</div>
				<div id="next"
					style="left: 86%;" z-index="1000">
					<img src="<c:url value='/images/newbie/arrowRight.png' />" />
				</div>


			</div>
		</div>
	</form>

	<!-- 处理用户状态 -->
	<script src="<c:url value='/scripts/main.js?t=${version}' />"></script>
	<script type="text/javascript">
		
		function layerCallback(data) {
			orderListTool.setupTimerAndList(data.orderList, data.seconds);
		}

		function popupDiv(div_id) {
			var div_obj = $("#" + div_id);
			var windowWidth = document.body.clientWidth;
			var windowHeight = document.body.clientHeight;
			var popupHeight = div_obj.height();
			var popupWidth = div_obj.width();
			//添加并显示遮罩层   
			$("<div id='mask'></div>").addClass("mask").width(windowWidth)
					.height(windowHeight).click(function() {
						hideDiv(div_id);
					}).appendTo("body").fadeIn(200);
			div_obj.css({
				"position" : "absolute"
			}).animate({
				left : 0,
				top : 0,
				opacity : "show"
			}, "slow");
		}

		function hideDiv(div_id) {
			$("#mask").remove();
			$("#" + div_id).animate({
				left : 0,
				top : 0,
				opacity : "hide"
			}, "slow");
		}

		(function(root) {
			/**
			 * 判断是否需要刷新页面
			 */
			if (Cookie.getCookie("needFreshIndex")) {
				Cookie.deleteCookie("needFreshIndex");
				location.reload();
				return;
			}
			
			root.ServerAndGoodListData = $
            .splitServerAndGoodList('{"Servers":[{"ServerId":13,"ServerIP":"115.28.151.55","ServerPort":8081,"ServerName":"点差宝155web行情2","ServerType":3,"ServerParam":""},'
					+ '{"ServerId":6,"ServerIP":"112.124.47.55","ServerPort":9501,"ServerName":"点差宝查询中心1","ServerType":2,"ServerParam":""},'
					//+ '{"ServerId":8,"ServerIP":"112.124.9.90","ServerPort":8502,"ServerName":"点差宝155web行情1","ServerType":3,"ServerParam":""},'
					+ '{"ServerId":9,"ServerIP":"112.124.9.90","ServerPort":80,"ServerName":"点差宝充值提现地址1","ServerType":4,"ServerParam":""}],'
					+ '"Merchs":[{"MarketId":17000,"MerchCode":"BU","Name":"刚玉","UnitNum":1,"ShowUnit":"吨","Dec":0,"Margin":"10,100,500","FreezeTime":60,"Point":"5,13|7,11|11,7.5","MaxBuyNum":6},'
					+ '{"MarketId":17000,"MerchCode":"AG","Name":"银基合金","UnitNum":1,"ShowUnit":"千克","Dec":0,"Margin":"10,100,500","FreezeTime":60,"Point":"4,13|6,11|10,7.5","MaxBuyNum":6},'
					+ '{"MarketId":17000,"MerchCode":"CU","Name":"中金铜","UnitNum":1,"ShowUnit":"吨","Dec":0,"Margin":"10,100,500","FreezeTime":60,"Point":"30,13|60,11|100,7.5","MaxBuyNum":6}],'
					+ '"SecKey":"I8PZHGV7BACA3xwodpfdHwyQsMLICJ_NVzJHBbVKIvJCLlfu2K","Orgs":[]}');

 			root.accountId = '${session_user.wxid}';
			// K线菜单列表 
			var ontap = "ontouchstart" in window ? "ontouchend" : "onclick";
			// 配置信息加载
			(function requestBaseInfo(root) {
				var notice = JSON
						.parse('{"ErrCode":0,"Message":"OK","Data":[["NoticeID","NoticeType","Title","Status","BeginDate","ExpiryDate","Content","CreateTime"]]}'), needonlyshowonce = true;
				if (notice.ErrCode == 0) {
					var noticedata = (root.$.ParseToJson(notice.Data) || []);
					if (noticedata.length > 0) {
						noticedata = noticedata[0];
						var saveNoticeID = (root.Cookie.getCookie("NoticeId") || "")
								.split(",")
								|| [];
						if (saveNoticeID.indexOf(noticedata.NoticeID + "::"
								+ noticedata.CreateTime) < 0
								|| !needonlyshowonce) {
							root.layer
									.open({
										type : 1,
										title : noticedata.Title,
										area : [ "90%", "auto" ],
										// closeBtn: 0,//['我知道了'], //按钮
										skin : 'layui-layer-createorder layui-layer-notice', //没有背景色
										shadeClose : true,
										content : noticedata.Content,
										cancel : function(index) {
											event.preventDefault();
											layer.close(index);
											saveNoticeID
													.push(noticedata.NoticeID
															+ "::"
															+ noticedata.CreateTime);
											root.Cookie.setCookie("NoticeId",
													saveNoticeID.join(","));
										},
										btn1 : function(index) {
											event.preventDefault();
											layer.close(index);
											saveNoticeID
													.push(noticedata.NoticeID
															+ "::"
															+ noticedata.CreateTime);
											root.Cookie.setCookie("NoticeId",
													saveNoticeID.join(","));
										}
									});
						}
					}
				}

				var marquee = JSON
						.parse('{"ErrCode":0,"Message":"OK","Data":[["ID","Title","Status","OrderNO","CreateTime"],["3","注意：微云服务是一项微成本、微时间的商品合约化电子业务，参与者应对其规则与潜在风险有了解。","\\u0001","3","2016-08-04 16:18:31"],["4","充分的认识并接受方可参与操作，凡参与操作的用户本中心一律揭示操作风险的必要性，","\\u0001","4","2016-08-04 16:23:22"],["5","并且认为您同意或接受相关风险责任，为了让您熟悉自身的风险承受能力，请仔细阅读并确认","\\u0001","5","2016-08-04 16:34:38"]]}');
				if (marquee.ErrCode == 0) {
					var data = root.$.ParseToJson(marquee.Data) || [], l = data.length, i, html = [];
					for (i = 0; i < l; i++) {
						if (data[i].Status != "0") {
							html.push(data[i].Title);
						}
					}
					$(".footer_cont marquee").html(
							html.join("&nbsp;&nbsp;&nbsp;&nbsp;"));
					$(".footer_cont marquee").off(tap).on(tap, function(e) {
						var target = e.target;
						if ($(target).is(".active")) {
							$(target).removeClass("active");
							target.stop();
						} else {
							$(target).addClass("active")
							target.start();
						}
					});
				}

				var banner = JSON
						.parse('{"ErrCode":0,"Message":"OK","Data":[["SlideID","OrgID","Title","Status","LinkUrl","LinkImageUrl","CreateTime"]]}'), bannerdata;
				if (banner.ErrCode == 0) {
					bannerdata = root.$.ParseToJson(banner.Data)[0];
					if (bannerdata) {
						$(".tra_ad a").attr("href",
								bannerdata.LinkUrl || "javascript:void(0)");
						var img = $(".tra_ad img")[0];
						img.onload = function() {
							img.onload = null;
							root.HangqingManage
									&& HangqingManage.AmChart
											.UpdateGraphHeight();
						};
						document.querySelector(".tra_ad").style.display = "block";
						img.src = bannerdata.LinkImageUrl;
					}
				}

				var sysconfig = JSON
						.parse('{"ErrCode":0,"Message":"OK","Data":[["ID","MaxBuyNum","MaxBuyTotal","MaxHoldNum","MaxLoss","FinalStatisBegin","FinalStatisEnd","FinalCalcBegin","FinalCalcEnd","MaxBuyOne","MaxBuyTwo","MaxBuyValue","IsControlRisk","MinExtract","IsTransferFee"],'
								+ '["1","3000.00","3000.00","2","30000.00","07:50","07:50","04:30","06:30","-10.00","-50.00","1.00","1","20000.00","0"]]}');
				if (sysconfig.ErrCode == 0) {
					root.sysconfigdata = root.$.ParseToJson(sysconfig.Data)[0];
				} else {
					console.log(banner.Message);
				}
			}(root));
		}(this));
	</script>

	<script src="<c:url value='/scripts/iscroll.js?t=${version}' />"></script>
	<script src="<c:url value='/scripts/achart.js?t=${version}' />"></script>
	<script src="<c:url value='/scripts/mustache.js?t=${version}' />"></script>
	<script src="<c:url value='/scripts/ordermanage.js?t=${version}' />"></script>
	<script src="<c:url value='/scripts/util.js?t=${version}' />"></script>
	<script src="<c:url value='/scripts/net2.js?t=${version}' />"></script>
	<script src="<c:url value='/scripts/hqmanage.js?t=${version}' />"></script>
	<script src="<c:url value='/scripts/start.js?t=${version}' />"></script>
	<script src="<c:url value='/scripts/index.js?t=${version}' />"></script>

	<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"
		type="text/javascript"></script>
	<script src="<c:url value='/scripts/share.js' />"></script>
	<script type="text/javascript" src="http://pingjs.qq.com/h5/stats.js"
		name="MTAH5" sid="500049057"></script>
	<script type="text/javascript">
		(function(root) {
			// 			root.ShareHost = "http://lianzhihan.zjwlce.cn/pointchat/Chat/ChatPushServlet";
			// 			initShare();
		}(this));

		$(document).ready(
				function() {
					var appath = global_util.getAppPath();
					$("#previous").hide();
					$("#guide_slide").click(
							function() {
								var imgsrc = this.src;
								//var imgidx = imgsrc.substr();
								var index1 = imgsrc.lastIndexOf("/");
								var index2 = imgsrc.lastIndexOf(".");
								var imgidx = parseInt(imgsrc.substr(index1 + 1,
										index2));
								if(imgidx >= 1){
									$("#previous").show();
								}
								if (imgidx < 8) {
									var nextImgSrc = appath + "/images/newbie/"
											+ (imgidx + 1) + ".png";
									// 									alert(nextImgSrc);
									$("#guide_slide").attr("src", nextImgSrc);
									// 									document.getElementById("#guide_slide").style.backgroundImage="url("+nextImgSrc+")";
								} else {
									hideDiv("pop-div");
									$.get(appath + "/user/finishFirstVisit",
											{}, function(data) {
												console.log(data.code);
											});
								}
							});

					$("#next").click(
							function(event) {
								event.stopPropagation(); 
								var imgsrc = $("#guide_slide")[0].src;
								var index1 = imgsrc.lastIndexOf("/");
								var index2 = imgsrc.lastIndexOf(".");
								var imgidx = parseInt(imgsrc.substr(index1 + 1,
										index2));
								if(imgidx >= 1){
									$("#previous").show();
								}
								if (imgidx < 8) {
									var nextImgSrc = appath + "/images/newbie/"
											+ (imgidx + 1) + ".png";
									// 									alert(nextImgSrc);
									$("#guide_slide").attr("src", nextImgSrc);
								} else {
									hideDiv("pop-div");
									$.get(appath + "/user/finishFirstVisit",
											{}, function(data) {
												//console.log(data.code);
												//send coupon
												setTimeout(function() {
													$.get(appath + "/user/checkcoupon", {}, function(d) {
														if (d.code == '0') {
															//noticeMsg(d);		
															showCoupon();
															setTimeout(function(){
																checkNotice();
															},10000)
														}
													});					
												}, 2000);
											});
								}
							});
					$("#previous").click(
							function(event) {
								event.stopPropagation(); 
								var imgsrc = $("#guide_slide")[0].src;
								//var imgidx = imgsrc.substr();
								var index1 = imgsrc.lastIndexOf("/");
								var index2 = imgsrc.lastIndexOf(".");
								var imgidx = parseInt(imgsrc.substr(index1 + 1,
										index2));
								if(imgidx == 2){
									$("#previous").hide();
								}
								if (imgidx >1) {
									var nextImgSrc = appath + "/images/newbie/"
											+ (imgidx - 1) + ".png";
									// 									alert(nextImgSrc);
									$("#guide_slide").attr("src", nextImgSrc);
								}else{
									$("#previous").hide();
								}
							});
					
				 	$.get(appath + "/user/getCurrentUser", {
						wxid : $("#wxid").val()
					}, function(data) {
						var d = data.data;
						if (d.firstVisit == 0) {
							popupDiv('pop-div');
						}else{
							setTimeout(function(){
								checkNotice();
							},10000)
						}
					});
				 	function checkNotice() {
						$.get(appath + "/checkNotice", {}, function(data) {
							if(data.needNotice=='1'){
								var notices = data.notice;
								$.each(notices,function(index,item){
									setTimeout(function(){
										noticeMsg(item);
									},index*30000)
								})
							}
						})
					}
				});
	</script>
</body>
</html>
