<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
	<script type="text/javascript" src="https://www.miliduobao.com/style/mobile/js/jquery-1.8.3.min.js"></script>
	<script src="<c:url value='/scripts/mustache.js'/>"></script>
	<link rel="stylesheet" href="https://www.miliduobao.com/style/mobile/css/nmwp.css?a=17" />
	<script type="text/javascript">
		<c:set var="basePath" value="${pageContext.request.contextPath}" />
	</script>
	<title>挖宝抢宝-打劫金币</title>
	<script type="text/javascript">
		$(function () {
			var height_v = $(window).height() - $(".img_top")[0].height;
			var width_v = $(window).width() - $(".img_top")[0].height;
			console.log();
			$('.position1').css('top', '0');
			$('.position1').css('left', width_v * 0.08 + 'px');

			$('.position2').css('top', '0');
			$('.position2').css('right', width_v * 0.06 + 'px');

			$('.position3').css('top', height_v * 0.16 + 'px');
			$('.position3').css('left', width_v * 0.02 + 'px');

			$('.position4').css('top', height_v * 0.30 + 'px');
			$('.position4').css('left', width_v * 0.38 + 'px');

			$('.position5').css('top', height_v * 0.18 + 'px');
			$('.position5').css('right', width_v * 0.03 + 'px');

			$('.position6').css('top', height_v * 0.39 + 'px');
			$('.position6').css('left', width_v * 0.03 + 'px');

			$('.position7').css('top', height_v * 0.42 + 'px');
			$('.position7').css('right', width_v * 0.00 + 'px');

			$('.position8').css('top', height_v * 0.53 + 'px');
			$('.position8').css('left', width_v * 0.40 + 'px');

			$('.position9').css('top', height_v * 0.66 + 'px');
			$('.position9').css('left', width_v * 0.04 + 'px');

			$('.position0').css('top', height_v * 0.68 + 'px');
			$('.position0').css('right', width_v * 0.04 + 'px');
		});
	</script>
</head>
<style>

	body, html {
		font-size:   14px;
		font-family: Microsoft Yahei, Arial, sans-serif;
		background:  #f6f6d7;
	}

	.wrap {
		width:      100%;
		background: #f6f6d7;
		font-size:  14px;
	}

	.wrap .header {
		overflow: hidden;
		width:    100%;
	}

	.wrap .header .top {
		position: relative;
	}

	.wrap .header .top .img {
		font-size: 0;
	}

	.wrap .header .top .img img {
		width: 100%;
	}

	.wrap .header .top .top1 {
		position: absolute;
		top:      0;
		left:     5%;
	}

	.wrap .header .top .top1 span {
		height:      25px;
		line-height: 25px;
		display:     block;
		font-size:   14px;
		color:       #fff;
	}

	.wrap .header .top .top1 font {
		color: #e7141a;
	}

	.wrap .header .top .top2 {
		position: absolute;
		top:      23%;
		right:    10%;
		width:    80%
	}

	.wrap .header .top .top2 .djs {
		float:        right;
		margin-right: 40px;
	}

	.wrap .header .top .top2 .djs p {
		color:     #D91D37;
		font-size: 26px;
	}

	.wrap .header .top .top2 .djs font {
		color:       #fff;
		font-size:   30px;
		line-height: 40px;
		font-weight: bold;
	}

	.wrap .header .top .top2 .djs font span {
		color: #fff;
	}

	.wrap .header .top .top2 .djs font b {
		color: #fff;
	}

	.wrap .header .top .top2 .djs i {
		font-size:   15px;
		font-style:  normal;
		color:       #fff;
		font-weight: 900;
		line-height: 40px;
	}

	.wrap .header .top .top2 .wycy {
		float:      right;
		width:      20%;
		margin-top: 5px;
	}

	.wrap .header .top .top2 .wycy a {
		width:   100%;
		display: block;
	}

	.wrap .header .top .top2 .wycy img {
		width: 100%;
	}
	.wrap .header .top .top2 .back_center {
		float:      left;
		width:      22%;
		margin-top: 5px;
	}

	.wrap .header .top .top2 .back_center a {
		width:           100%;
		height:          25px;
		line-height:     25px;
		text-align:      center;
		display:         block;
		float:           right;
		margin-left:     4%;
		background:      url(https://www.miliduobao.com/style/mobile/images/nmwp/ind_17.png) no-repeat;
		color:           #b15b00;
		background-size: 100% 100%;
		font-size:       14px;
	}

	.wrap .header .top .top2 .back_center img {
		width: 100%;
	}

	.wrap .header .top .top3 {
		position:    absolute;
		bottom:      12%;
		width:       82%;
		left:        50%;
		margin-left: -41%;
	}

	.wrap .header .top .top3 .le {
		float:           left;
		background:      rgba(255, 255, 255, 0.5);
		border-radius:   5px;
		padding:         0 6px;
		height:          25px;
		background-size: 100% 100%;
		line-height:     25px;
	}

	.wrap .header .top .top3 .ri {
		float: right;
		width: 41%;
	}

	.wrap .header .top .top3 span {
		display:     block;
		float:       left;
		font-size:   10px;
		line-height: 25px;
		color:       #a40c0c;
	}

	.wrap .header .top .top3 font {
		display:     block;
		float:       left;
		font-size:   12px;
		color:       #f12f2f;
		font-weight: bold;
		line-height: 25px;
	}

	.wrap .header .top .top3 a {
		width:           46%;
		height:          25px;
		line-height:     25px;
		text-align:      center;
		display:         block;
		float:           right;
		margin-left:     4%;
		background:      url(https://www.miliduobao.com/style/mobile/images/nmwp/ind_17.png) no-repeat;
		color:           #b15b00;
		background-size: 100% 100%;
		font-size:       11px;
	}

	.wrap .header .bott {
		width:    100%;
		overflow: hidden;
	}

	.wrap .header .bott .bj1 {
		width:     100%;
		font-size: 0;
	}

	.wrap .header .bott .bj1 img {
		width: 100%;
	}

	.wrap .header .bott .cont {
		width:  88%;
		margin: 0 auto;
	}

	.wrap .header .bott .gg {
		width:           80%;
		height:          25px;
		float:           left;
		background:      url(https://www.miliduobao.com/style/mobile/images/nmwp/ind_19.png) no-repeat;
		background-size: 100% 100%;
		font-size:       0;
	}

	.wrap .header .bott .gz {
		width:           16%;
		float:           right;
		height:          25px;
		background:      url(https://www.miliduobao.com/style/mobile/images/nmwp/ind_20.png) no-repeat;
		background-size: 100% 100%;
		font-size:       0;
	}

	.wrap .header .bott .gz a {
		display:     block;
		text-align:  center;
		height:      25px;
		line-height: 25px;
		font-size:   10px;
		color:       #f2f200;
	}

	.wrap .mains {
		width:      88%;
		margin:     20px auto;
		background: #f6f6d7;
		clear:      both;
	}

	.wrap .mains .dw {
		background: #f6f6d7;
		position:   relative;
	}

	.wrap .mains .dw .li {
		position: absolute;
		width:    40%;
	}

	.wrap .mains .dw .li .partake {
		display: block;
	}

	.wrap .mains .dw .li .partake img {
		width:     100%;
		max-width: 190px;
	}

	.wrap .mains .dw .position1 {
		top:  0;
		left: 6%;
	}

	.wrap .mains .dw .position2 {
		top:   0;
		right: 6%;
	}

	.wrap .mains .dw .position3 {
		top:  70px;
		left: 0%;
	}

	.wrap .mains .dw .position4 {
		top:  120px;
		left: 30%;
	}

	.wrap .mains .dw .position5 {
		top:   70px;
		right: 0%;
	}

	.wrap .mains .dw .position6 {
		top:  170px;
		left: 0%;
	}

	.wrap .mains .dw .position7 {
		top:   170px;
		right: 0%;
	}

	.wrap .mains .dw .position8 {
		top:  220px;
		left: 30%;
	}

	.wrap .mains .dw .position9 {
		top:  270px;
		left: 0%;
	}

	.wrap .mains .dw .position0 {
		top:   270px;
		right: 0%;
	}

	.noticTipTxt {
		color:       #fff;
		height:      25px;
		line-height: 25px;
		overflow:    hidden;
		margin:      0 0 0 20px;
	}

	.noticTipTxt li {
		height:      25px;
		line-height: 25px;
	}

	.noticTipTxt a {
		color:           #fff;
		font-size:       10px;
		text-decoration: none;
	}


</style>
<style type="text/css">
	.bodysy {
		width:      100%;
		height:     100%;
		background: rgba(0, 0, 0, 0.5);
		position:   fixed;
		display:    none;
		z-index:    3;
	}

	.xzs {
		width:         80%;
		display:       none;
		background:    #FFFFFF;
		border-radius: 5px;
		border-radius: 10px;
	}

	.xzs span {
		color:           #FFFFFF;
		font-weight:     bold;
		text-align:      center;
		display:         block;
		overflow:        hidden;
		padding:         2% 0px;
		background:      #FFFFFF;
		font-size:       18px;
		background:      url("https://www.miliduobao.com/style/mobile/images/nmwp/nmwp_22.png");
		background-size: cover;
	}

	.xzs .a1 {
		display:         block;
		height:          30px;
		line-height:     30px;
		text-align:      center;
		text-decoration: none;
		float:           left;
	}

	.xzs .a2 {
		display:         block;
		height:          30px;
		line-height:     30px;
		text-align:      center;
		text-decoration: none;
		float:           left;
	}

	.news {
		position: fixed;
		display:  block;
		z-index:  9999;
		left:     10%;
		top:      30%;
	}

	.transcts {
		display:           block;
		animation:         hint-show 0.5s;
		-webkit-animation: hint-show 0.5s;
	}

	@keyframes hint-show {
		0% {
			display:   none;
			transform: scale(0.8);
			opacity:   0;
		}
		1% {
			display:   block;
			transform: scale(0.8);
			opacity:   0;
		}
		75% {
			transform: scale(1);
			opacity:   1;
		}
	}

	@-webkit-keyframes hint-show {
		0% {
			display:           none;
			-webkit-transform: scale(0.8);
			opacity:           0;
		}
		1% {
			display:           block;
			-webkit-transform: scale(0.8);
			opacity:           0;
		}
		75% {
			-webkit-transform: scale(1);
			opacity:           1;
		}
	}

	.none {
		display: none;
	}
</style>
<script>
	var URL_PATH = 'http://api.miliduobao.com';
	function on_input_c() {
//        if ($('#buy_num').val() == '') {
//            $('.zhishao').show();
//            setTimeout(function () {
//                $('.zhishao').hide();
//            },2000);
//            $('#buy_num').val(1);
//        }
		$('#buy_num').val($('#buy_num').val());
		$('#total').html($('#buy_num').val() * 10);
		if ($('#buy_num').val() >= 500) {
			$('.zuiduo').show();
			setTimeout(function () {
				$('.zuiduo').hide();
			}, 2000);
			$('#buy_num').val(500);
			$('#total').html(500 * 10);
		}
		if ($('#buy_num').val().length > 3) {
			$('#buy_num').val(500);
			$('#total').html(500 * 10);
		}
	}

	function close1() {
		$('.xzs').removeClass('news');
		$('.bodys').hide();
		$('.xzs').removeClass('transcts');
	}
	function recharge() {
//		window.location.href = "recharge?ME=0&type=2";
		var path = global_util.getAppPath();
		window.location = path + "/shop/${user.wxid}";
	}

	$(function () {
		$('.ccc').blur(function () {
			var v = $(this).val();
			if (v < 1) {
//                alert('单次购买次数最少1次');
				$('.zhishao').show();
				setTimeout(function () {
					$('.zhishao').hide();
				}, 2000);
				$('#buy_num').val(1);
				$('#total').text(1 * 10);
			}
			if (v == '') {
//                alert('单次购买次数最少1次');
				$('.zhishao').show();
				setTimeout(function () {
					$('.zhishao').hide();
				}, 2000);
//                $(this).val(1);
				$('#buy_num').val(1);
				$('#total').text(1 * 10);
			}
		});
	});
</script>

    <span style="font-family:SimSun;font-size:14px;" id="bgaudio-apan">
        <!--<audio autoplay="autoplay" loop="loop" id="bgaudio" preload="preload">-->
		<!--<source src="https://www.miliduobao.com/style/mobile/music/guoan.mp3" type="audio/mpeg">-->
		<!--</audio>-->
    </span>


<div class="xzs news transcts none zuiduo" style="border: 1px solid #D9D9D9;height: 40px;line-height: 40px;position: absolute;top: 35%;left: 20%;z-index: 999; width: 60%">
	<p style="text-align: center;line-height: 40px; margin-top: 0px;padding: 0">单次购买次数最多500次</p>
</div>
<div class="xzs news transcts none zhishao" style="border: 1px solid #D9D9D9;height: 40px;line-height: 40px;position: absolute;top: 35%;left: 20%;z-index: 999;width: 60%">
	<p style="text-align: center;line-height: 40px; margin-top: 0px; padding: 0">单次购买次数最少1次</p>
</div>
<style>
	#addcont .xzs .neir {
		height:          80px;
		background:      url("https://www.miliduobao.com/style/mobile/images/nmwp/nmwp_19.png") right no-repeat;
		margin:          10px 10%;
		background-size: contain;
	}

	#addcont .xzs .neir p {
		width:       100px;
		line-height: 30px;
		font-size:   17px;
		padding-top: 14px;
		color:       #666666
	}

	#addcont .xzs .dibu {
		height:  60px;
		padding: 10px 10%
	}

	#addcont .xzs .dibu .a1 {
		width:           45%;
		background:      url("https://www.miliduobao.com/style/mobile/images/nmwp/nmwp_20.png") no-repeat;
		float:           left;
		background-size: 100% 100%;
	}

	#addcont .xzs .dibu .a2 {
		width:           45%;
		background:      url("https://www.miliduobao.com/style/mobile/images/nmwp/nmwp_21.png") no-repeat;
		float:           right;
		background-size: 100% 100%;
	}

	#addcont .xzs .dibu .a3 {
		width:           60%;
		background:      url("https://www.miliduobao.com/style/mobile/images/nmwp/nmwp_24.png") no-repeat;
		background-size: 100% 100%;
		display:         block;
		height:          30px;
		line-height:     30px;
		text-align:      center;
		text-decoration: none;
		margin-left:     20%;
	}

	/* "变灰"效果*/
	.disableHref {
		cursor:          default;
		color:           #232323;
		text-decoration: none;
		background:      #BDBDBD;
	}
</style>
<div id="addcont" style="position: absolute;top: 20px;left: 50%;z-index: 999999; ">
</div>

<div class="bodys"></div>
<div class="buycont">
	<div class="tle">
		<span>选择参与人次</span>
		<p>(您选择的号码是&nbsp; <font id="partake_number" style=" color: #DA0605; font-weight: 900;"></font> ，10元一次！)</p>
		<a charset="col" onclick="col()"></a>
	</div>
	<div class="number">
		<a class="num-btn btn-minus" onclick="minus()" data-pro="minus" href="javascript:void(0);">-</a>
		<input id="buy_num" oninput="on_input_c()" class="num-input ccc" type="number" max="100" data-id="1" data-max="50" value="1" />
		<a class="num-btn btn-plus" onclick="plus()" data-pro="plus" href="javascript:void(0);">+</a>
	</div>
	<div class="list">
		<input class="inp one" type="text" value="5" onclick="inp(this.value)" readonly="readonly" />
		<input class="inp" type="text" value="10" onclick="inp(this.value)" readonly="readonly" />
		<input class="inp" type="text" value="20" onclick="inp(this.value)" readonly="readonly" />
		<input class="inp" type="text" value="50" onclick="inp(this.value)" readonly="readonly" />
	</div>
	<div class="but">
		<input id="token" type="hidden" value="ff17e0ebb4ada5d216827014fc96b09a" />
		<input id="periods" type="hidden" value="20161222078" />
		<input id="period" type="hidden" value="" />
		<input id="wxid" type="hidden" value="<c:out value='${user.wxid}'/>" />
		<span>共<font id="total">1</font>万金币</span>
		<a class="confirm" onclick="confirm()">确定</a>
	</div>
</div>

<script type="text/javascript">
	var qi_shu = 078;
	var token = $('#token').val();
	function confirm() {
		var period = $('#period').val();
		var partake_number = parseInt($('#partake_number').text());
		var number = parseInt($('#buy_num').val()) * 10;
		if (isNaN(number)) {
			$('.zhishao').show();
			setTimeout(function () {
				$('.zhishao').hide();
			}, 2000);
			$('.zhishao').val(1);
			$('#total').text(1 * 10);
		}
		else if (number == 0) {
			$('.zhishao').show();
			setTimeout(function () {
				$('.zhishao').hide();
			}, 2000);
		} else {
			var appPath = global_util.getAppPath();
			var period = $("#period").val();
			var url = appPath + "/lotteryorder/createorder" + '?token=' + token + '&period=' + period + '&partake_number=' + partake_number + '&number=' + number;
			$.get(url, function (data) {
				html = '';
				if (data.data == 101) {
					html += '<div class="xzs news transcts">';
					html += '<span>挖宝抢宝，打劫金币</span>';
					html += '<div class="neir">';
					html += '<p id="contents">金币不足<br/>请先<font style="color: #DA0605" id="fontcont">充值</font></p>';
					html += '</div>';
					html += '<div class="dibu">';
					html += '<a class="a1 qr1" onclick="close1();"></a>';
					html += '<a class="a2 qr1 xzs_qr1" onclick="recharge();"></a>';
					html += '</div>';
					html += '</div>';
				} else if (data.data == 102) {
					html += '<div class="xzs news transcts">';
					html += '<span>挖宝抢宝，打劫金币</span>';
					html += '<div class="neir">';
					html += '<p id="contents">暂不能购买<br/>请稍后</p>';
					html += '</div>';
					html += '<div class="dibu">';
					html += '<a class="a3 qr1 xzs_qr1" onclick="close1();"></a>';
					html += '</div>';
					html += '</div>';
				} else if (data.data == 103) {
					html += '<div class="xzs news transcts">';
					html += '<span>挖宝抢宝，打劫金币</span>';
					html += '<div class="neir">';
					html += '<p id="contents">人次错误<br/>请重新参与</p>';
					html += '</div>';
					html += '<div class="dibu">';
					html += '<a class="a3 qr1 xzs_qr1" onclick="close1();"></a>';
					html += '</div>';
					html += '</div>';
				} else if (data.data == 104) {
					html += '<div class="xzs news transcts">';
					html += '<span>挖宝抢宝，打劫金币</span>';
					html += '<div class="neir">';
					html += '<p id="contents">单个号码最<br/>多参与500次</p>';
					html += '</div>';
					html += '<div class="dibu">';
					html += '<a class="a3 qr1 xzs_qr1" onclick="close1();"></a>';
					html += '</div>';
					html += '</div>';
				} else if (data.data == 110) {
					html += '<div class="xzs news transcts">';
					html += '<span>挖宝抢宝，打劫金币</span>';
					html += '<div class="neir">';
					html += '<p id="contents">登录过期<br/>请重新登录</p>';
					html += '</div>';
					html += '<div class="dibu">';
//                    html += '<a class="a1 qr1" onclick="window.location.href = \''+URL_PATH + '/nmwp/login\'"></a>';
					html += '<a class="a3 qr1 xzs_qr1" onclick="window.location.href = \'' + URL_PATH + '/nmwp/login\'"></a>';
					html += '</div>';
					html += '</div>';
				} else if (data.data == 200) {
					html += '<div class="xzs news transcts">';
					html += '<span>挖宝抢宝，打劫金币</span>';
					html += '<div class="neir">';
					html += '<p id="contents">参与成功<br/> 祝您好运</p>';
					html += '</div>';
					html += '<div class="dibu">';
					html += '<a class="a3 qr1 xzs_qr1" onclick="close1();"></a>';
					html += '</div>';
					html += '</div>';
				} else {
					html += '<div class="xzs news transcts">';
					html += '<span>挖宝抢宝，打劫金币</span>';
					html += '<div class="neir">';
					html += '<p id="contents">参与过于<br/> 频繁</p>';
					html += '</div>';
					html += '<div class="dibu">';
					html += '<a class="a3 qr1 xzs_qr1" onclick="close1();"></a>';
					html += '</div>';
					html += '</div>';
				}

				$('#addcont').append(html);

				$('.bodys').show();
			}, 'json');

		}
	}
</script>
<body onunload="checkLeave()" onbeforeunload="checkLeave()">
<div class="wrap">
	<div class="header" style="width: 100%;height: auto">
		<div class="top">
			<div class="img"><img class="img_top" src="https://www.miliduobao.com/style/mobile/images/nmwp/ind_13.png" style="height: auto;
			width: 100%"
				/></div>
			<div class="top1">
				<span>当前期数：<font>078</font></span>
			</div>
			<div class="top2">
				<div class="back_center">
					<a href="<c:url value='${basePath}/../game_center/${session_user.wxid}/${session_user.chnno}' />" id="back_center">返回大厅 </a>
				</div>
				<div class="wycy">
					<a href="#" id="usercenter"><img src="https://www.miliduobao.com/style/mobile/images/nmwp/ind_10.png" /> </a>
				</div>
				<div class="djs">
					<p>
						<font id="leftTimeJx100"></font>
						<i id="Timecont"></i>
					</p>
				</div>
				<script type="text/javascript">
					$(document).ready(function () {
						var path = global_util.getAppPath();
						var r = Math.floor(Math.random()*1000);
						$.get(path + '/lotteryorder/placeorderstate?time='+r, {} , function(data) {
							var remain_millis = data.remain_millis;
							var sn = data.sn;
							$("#period").val(sn);

							var pre_sn = data.pre_sn;
							var pre_code = data.pre_code;
							onload_leftTime_jx(100,  remain_millis, 'mobile');	

							if (pre_code) {
								var contentDisclosed = "<span>第{{pre_sn}}期结果为：</span><font class=\"old_qishu\">{{pre_code}}</font>";
								$(".top3").eq(0).find(".le").eq(0).html(Mustache.render(contentDisclosed, data));
							} else if (pre_sn) {
								var contentDisclosed = "<span>第{{pre_sn}}期结果为：</span><font class=\"old_qishu\">正在开奖</font>";
								$(".top3").eq(0)
								.find(".le").eq(0).html(Mustache.render(contentDisclosed, data));
								loadPreCode(100, pre_sn);
							}
							if (sn) {
								$(".top1").eq(0).html("<span>当前期数：<font>" + sn + "</font></span>");
							}
						});					
					});
				</script>
			</div>
			<div class="top3">
				<div class="le">
					<span>第077期结果为：</span>
					<font class="old_qishu">99617</font>
				</div>
				<div class="ri">
					<a href="#" id="showfillpage">充值</a>
					<a href="#" id="resultlist">往期揭晓</a>
				</div>
			</div>
		</div>
		<div class="bott">
			<div class="bj1"><img src="https://www.miliduobao.com/style/mobile/images/nmwp/ind_18.png" /></div>
			<div class="cont">
				<div class="gg">
					<ul id="jsfoot02" class="noticTipTxt">
													<li>
								<a href="javascript:volid(0);" target="_blank">恭喜<font style="color: #f2f200;white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width: 50px">太监开会</font>成功抢走<font style="color: #f2f200">490</font>万金币</a>
							</li>
													<li>
								<a href="javascript:volid(0);" target="_blank">恭喜<font style="color: #f2f200;white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width: 50px">我爱吃烧白</font>成功抢走<font style="color: #f2f200">37900</font>万金币</a>
							</li>
													<li>
								<a href="javascript:volid(0);" target="_blank">恭喜<font style="color: #f2f200;white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width: 50px">萌猪猪</font>成功抢走<font style="color: #f2f200">6140</font>万金币</a>
							</li>
													<li>
								<a href="javascript:volid(0);" target="_blank">恭喜<font style="color: #f2f200;white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width: 50px">我短发发型</font>成功抢走<font style="color: #f2f200">530</font>万金币</a>
							</li>
													<li>
								<a href="javascript:volid(0);" target="_blank">恭喜<font style="color: #f2f200;white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width: 50px">微信米宝85859293</font>成功抢走<font style="color: #f2f200">200</font>万金币</a>
							</li>
													<li>
								<a href="javascript:volid(0);" target="_blank">恭喜<font style="color: #f2f200;white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width: 50px">干掉王子</font>成功抢走<font style="color: #f2f200">160</font>万金币</a>
							</li>
													<li>
								<a href="javascript:volid(0);" target="_blank">恭喜<font style="color: #f2f200;white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width: 50px">微信米宝54331638</font>成功抢走<font style="color: #f2f200">140</font>万金币</a>
							</li>
													<li>
								<a href="javascript:volid(0);" target="_blank">恭喜<font style="color: #f2f200;white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width: 50px">就想问你们6不6</font>成功抢走<font style="color: #f2f200">110</font>万金币</a>
							</li>
													<li>
								<a href="javascript:volid(0);" target="_blank">恭喜<font style="color: #f2f200;white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width: 50px">妹的，骗人的东西</font>成功抢走<font style="color: #f2f200">60</font>万金币</a>
							</li>
													<li>
								<a href="javascript:volid(0);" target="_blank">恭喜<font style="color: #f2f200;white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width: 50px">米粒职业开车手</font>成功抢走<font style="color: #f2f200">60</font>万金币</a>
							</li>
											</ul>
				</div>
				<div class="gz">
					<a href="#" id="viewrule">游戏规则</a>
				</div>
			</div>
		</div>
	</div>


	<div class="mains">
		<div class="dw">
			<div class="li position1">
				<span class="partake" onclick="partake(1)"><img src="https://www.miliduobao.com/style/mobile/images/nmwp/ind_1.png" /> </span>
			</div>
			<div class="li position2">
				<span class="partake" onclick="partake(2)"><img src="https://www.miliduobao.com/style/mobile/images/nmwp/ind_2.png" /> </span>
			</div>
			<div class="li position3">
				<span class="partake" onclick="partake(3)"><img src="https://www.miliduobao.com/style/mobile/images/nmwp/ind_3.png" /> </span>
			</div>
			<div class="li position4">
				<span class="partake" onclick="partake(4)"><img src="https://www.miliduobao.com/style/mobile/images/nmwp/ind_4.png" /> </span>
			</div>
			<div class="li position5">
				<span class="partake" onclick="partake(5)"><img src="https://www.miliduobao.com/style/mobile/images/nmwp/ind_5.png" /> </span>
			</div>
			<div class="li position6">
				<span class="partake" onclick="partake(6)"><img src="https://www.miliduobao.com/style/mobile/images/nmwp/ind_6.png" /> </span>
			</div>
			<div class="li position7">
				<span class="partake" onclick="partake(7)"><img src="https://www.miliduobao.com/style/mobile/images/nmwp/ind_7.png" /> </span>
			</div>
			<div class="li position8">
				<span class="partake" onclick="partake(8)"><img src="https://www.miliduobao.com/style/mobile/images/nmwp/ind_8.png" /> </span>
			</div>
			<div class="li position9">
				<span class="partake" onclick="partake(9)"><img src="https://www.miliduobao.com/style/mobile/images/nmwp/ind_9.png" /> </span>
			</div>
			<div class="li position0">
				<span class="partake" onclick="partake(0)"><img src="https://www.miliduobao.com/style/mobile/images/nmwp/ind_0.png" /> </span>
			</div>
		</div>
	</div>
</div>

</body>
<script type="text/javascript" src="https://www.miliduobao.com/style/mobile/js/scrolltext.js"></script>
<script type="text/javascript">
	function checkLeave() {
		$.get("/nmwp/android_type?type=yes");
	}
</script>
<script type="text/javascript">

	$(function () {
		// 演示二
		if (document.getElementById("jsfoot02")) {
			var scrollup = new ScrollText("jsfoot02");
			scrollup.LineHeight = 25;        //单排文字滚动的高度
			scrollup.Amount = 1;            //注意:子模块(LineHeight)一定要能整除Amount.
			scrollup.Delay = 40;           //延时
			scrollup.Start();             //文字自动滚动
			scrollup.Direction = "up";   //默认设置为文字向上滚动
		}
	});
</script>
<script src="<c:url value='/scripts/util.js' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/lottery/nmwp.js' />"></script>
<script>
$(document).ready(function() {
	var path = global_util.getAppPath();
	
	$("#showfillpage").click(function() {
		//alert(path);
// 		window.location = path + "/wx/showRefillPage";
		window.location = path + "/shop/${user.wxid}";
	});
	
	$("#resultlist").click(function() {
		window.location = path + "/lotteryorder/seelotteryresultlist";
	});
	
	$("#viewrule").click(function() {
		window.location = path + "/lotteryorder/seerule";
	});
	
	$("#usercenter").click(function() {
		window.location = path + "/user/enterUserCenter";
	});
});

</script>

</html>
