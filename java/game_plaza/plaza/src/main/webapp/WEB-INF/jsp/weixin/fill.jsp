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
<%-- 	<script src="<c:url value='/payment/scripts/prepare.js?t=${version}' />" type="text/javascript"></script> --%>
	<script type="text/javascript" src="<c:url value='/scripts/util.js?t=${version}' />" ></script>
	<script type="text/javascript">
		<c:set var="basePath" value="${pageContext.request.contextPath}" />
	</script>
</head>
	<body>
		<%@include file="/WEB-INF/jsp/head/common.jsp"%>
		<div class="recharge-box">
			<div class="accounts-wrap">
				<div class="image-wrap"><img src="<c:out value='${userInfo.headimgurl}' />" /></div>
				<div class="userinfo">
					<div class="name">${userInfo.wxnm}</div>
				</div>
		    </div>
		    <!--充值金额-->
			<div class="money-setting-wrap">
				<div class="setting-title">充值金额</div>
				<div class="setting-content"></div>
			</div>
			<!--充值方式-->
			<div class="payway-wrap">
				<div class="payway-title">充值方式</div>
				<div class="payway-content">
				    <ul>
				        <li class="table">
				            <div class="table-cell symbol-wrap"><i class="icon icon-wx"></i></div>
				            <div class="table-cell">微信支付</div>
				            <div class="table-cell choose-wrap"><i class="choose choose-full">&#10003;</i></div>
				        </li>
				    </ul>
				</div>
                <div class="bankcard-wrap hidden">
                    <div class="boxflex">
                        <label class="key"><span class="redsymbol">*</span>卡号</label>
                        <input id="bankcardno" class="box_flex_1" maxlength="19" placeholder="请输入16到19位银行卡号" onpaste="return false" oncontextmenu="return false" oncopy="return false" oncut="return false">
                    </div>
                    <div style="color:red; padding: 10px 0 20px;" class="messagetip"><span>为确保您的资金安全，此银行卡将成为您在云平台中进行银联充值及提现的唯一有效卡。</span><a class="checkbank" style="text-align: right; color: #4fa9ee; float: right;">查看支持银行</a></div>
                </div>
				<a href="javascript:void(0)" class="cz_but">立即充值</a>
			</div>
			<form id="checkout" name="checkout" method="post" action="http://pay.enanea.com/paycenter/appclient/getUrl.do">
				<input name="appId" id="appId" value="" type="hidden">
				<input name="orderId" id="orderId" value="" type="hidden">    
				<input id="price" name="price" value="" type="hidden">    
				<input name="desc" id="desc" value="jifei" type="hidden">	
				<input name="payType" id="payType" value="wxgzh" type="hidden">    
				<input name="callBackUrl" id="callBackUrl" value="" type="hidden">
				<input name="notifyUrl" id="notifyUrl" value="" type="hidden">
				<input name="sign" id="sign" value="" type="hidden">
			</form>
		</div>
        <script src="<c:url value='/payment/scripts/main.js?t=${version}' />" type="text/javascript"></script>
       	<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>
        <script type="text/javascript">
        	var host = window.location.host;
        	var ctx="http://"+ host + '<%=request.getContextPath()%>';
            (function(root){
                /**
                 * 首先判断用户的状态-- 是否第一次充值
                 * 第一次充值：只能是银联充值，必须输入银行卡号
                 */
                function goback(){
                    if(isInWeiXin){
                        window.history.back();
                    }else{
                        window.close();
                    }
                }
                
                function showMsg(msg){
                    if(msg == "-9999"){
                        layer.msg("当前帐号已被禁止操作，请联系客服。");
                    }else{
                        layer.msg(msg, {
                            time: 2000
                        }, goback);
                    }
                }
                /* $.getUserInfo(function(data){
                    switch(data.IsRegister.toString()){
                        case "0": 
                            showMsg("您还没有密码，请先设置密码！");
                            break;
                        case "1": 
                            showMsg("您还没有设置手机号，请先设置手机号！");
                            break;
                        case "2":
                            if(data.loginStatus == "1"){
                                if(data.Status == "2"){
                                    showMsg("-9999");
                                }else if(data.Status == "1"){
                                    if(data.StopTrade){
                                        showMsg("-9999");
                                    }else{
                                        $(".accounts-wrap img").attr("src", data.HeadImgUrl);
                                        $(".accounts-wrap .name").text(data.NickName);
                                        doStart();
                                    }
                                }else{
                                    showMsg("-9999");
                                }
                            }else{
                                showMsg("您还没有登录，请先登录！");
                            }
                            break;
                        default: 
                            showMsg("用户状态异常！");
                            break;
                    }
                }, function(msg){
                    showMsg(msg);
                }); */
                
                doStart();

                // 处理充值档位
                (function(){
                    var money = '1,2,10,50,100,200,300,(500),1000,2000,5000', 
                        paymoney = money.split(",") || [], len = paymoney.length, html = [],
                        i = 0, moneyContainer = $(".setting-content"), moneyNum, wid, 
                        height = Math.ceil(len/5), 
                        num = Math.ceil(len / height), htmlul = [], 
                        classname = ""; 

                    for(i in paymoney){
                        classname = "";
                        if(/^\(\d+\)$/.test(paymoney[i])){
                            classname = "active";
                            paymoney[i] = paymoney[i].substring(1, paymoney[i].length - 1);
                        }
                        htmlul.push([
                            '<li class="box_flex_1"><a class="', classname, '">', 
                                parseInt(paymoney[i]), 
                            '</a></li>'
                        ].join(""));
                        if(i % num == num - 1){
                            html.push([
                                '<ul class="boxflex">', htmlul.join(""), '</ul>'
                            ].join(""));
                            htmlul = [];
                        }
                    }
                    if(htmlul.length > 0){
                        html.push([
                            '<ul class="boxflex">', htmlul.join(""), '</ul>'
                        ].join(""));
                    }
                    if(moneyContainer.html(html.join("")).find("li a.active").length < 1){
                        moneyContainer.find("li a").eq(0).addClass('active');
                    }
                    
                    var tap = "click";
                    $(".money-setting-wrap li a").off(tap).on(tap, function(e){
                        if(isNeedStopTouchend){
                            return;
                        }
                        var $target = $(e.target);
                        $(".money-setting-wrap li a.active").removeClass("active");
                        $target.addClass("active");
                    });
                    //moneyContainer.html(html.join("")).find("li a").eq(0).addClass('active');
                }());
                
                //支付方式
                /*
                * 0 微信提现， 1 银联支付
                */
                var supportUnion = '00', supportWeiXin = '10';
                (function(){
                    var wx = $(".payway-content .icon-wx").parents("li.table"),
                        yl = $(".payway-content .icon-yl").parents("li.table");
                    if(supportWeiXin == 0 || supportWeiXin == "00" || supportWeiXin == "01"){
                        wx.css("cssText","display:none!important");
                        yl.css("borderTop","none");
                        $(".txway-wrap li i.choose").removeClass("choose-full").addClass("choose-empty").html("");
                        yl.find("i.choose").removeClass("choose-empty").addClass("choose-full").html("&#10003;");
                    }
                    if(supportUnion == 0 || supportUnion == "00" || supportUnion == "01"){
                        yl.css("cssText","display:none!important");                            
                    }
                }());
                
                // 用户所有状态都正常，开始处理操作
                function doStart(){
                    // 获取系统配置信息
                    var sysunionpay = undefined, sysbankinfo = undefined;
                    
/*                     // 判断用户的是否第一次充值
                    function judgeUserRepay(){
                        var isIOS = !!navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),
                            bankinfo = sysbankinfo, bankcodestr = bankinfo.bankcard||"", isfirst = !bankcodestr || (bankinfo.isUpdate == 0);
                        if(isIOS){
                            bankcardno.type = "num";
                        }else{
                            bankcardno.type = "tel";
                        }
                        if(isfirst && sysunionpay && supportUnion != 0){ 
                            // 第一次充值只能进行银联充值
                            bankcardno.disabled = false;
                            $(".payway-content li").eq(0).css("opacity", "0.5").find("i.choose").addClass("disabled").removeClass("choose-full").addClass("choose-empty").html("");
                            $(".bankcard-wrap").removeClass("hidden");
                            $(".payway-content li").eq(1).find("i.choose").removeClass("choose-empty").addClass("choose-full").html("&#10003;");
                        }else{
                            if(!bankinfo.isUpdate || bankinfo.isUpdate == 0){
                                bankcardno.disabled = false;
                            }else{
                                bankcardno.disabled = true;
                                bankcardno.className="box_flex_1 disabled";
                                $(".messagetip").remove();
                            }
                            $(".payway-content li").eq(0).css("opacity", "1").find("i.choose").removeClass("disabled");
                        }
                        bankcardno.value=bankcodestr;
                    };
 */	
                    var tap = "click";
                    var isNeedStopTouchend = false;
                    // 选择金额
                    // 选择支付方式
                    /* $(".payway-wrap li").off(tap).on(tap, function(e){
                        if(isNeedStopTouchend){
                            return;
                        }
                        var $target = $(this).find("i.choose");
                        if($target.is(".disabled")){
                            layer.msg("第一次充值只能进行银联充值！");
                            return;
                        }
                        if($(this).index() == 0){
                            $(".bankcard-wrap").addClass("hidden");
                        }else{
                            $(".bankcard-wrap").removeClass("hidden");
                        }
                        $(".payway-wrap li i.choose").removeClass("choose-full").addClass("choose-empty").html("");
                        $target.removeClass("choose-empty").addClass("choose-full").html("&#10003;");
                    }); */
                    var chackbut = true;  //避免重复提交
                    var isNeedStopTouchend= false; 
                    // 点击充值
                    //$(".cz_but").off(tap).on(tap, _.throttle(function(e){
                    	$(".cz_but").off(tap).on(tap, function(e){
                        e.preventDefault();
                        if(isNeedStopTouchend){
                            return;
                        }
                        if(chackbut){
                            chackbut=false;
                            if($(".payway-wrap i.choose-full").parents("li").index() == 0){ // 微信充值
                                wxPay();
                            }
                        }
                    	});
 
                    // 微信支付
                    function wxPay(){
//                     	if(global_util.validRepayTime()){
//     						layer.msg("抱歉：请在工作日进行充值或提现！",{time:2e3},function(){})
//     					}else{
	                        if (typeof WeixinJSBridge == "undefined"){
	                            if( document.addEventListener ){
	                                document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
	                            }else if (document.attachEvent){
	                                document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
	                                document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
	                            }
	                         }else{
	                             onBridgeReady();
	                         }
//     					}
                    }
                   
                    function onBridgeReady(){
                    	$.get(ctx+"/wx/fill", {money: $(".money-setting-wrap li a.active").text() * 100 }, function(response) {
                    		var data = response.data;
                    		var wxpaytype = data.wxpaytype;
                    		if (wxpaytype == '1') {//wxgzh
                    			WeixinJSBridge.invoke( 'getBrandWCPayRequest', {
                                    "appId":data.appId, //公众号名称，由商户传入 
                                    "nonceStr":data.nonceStr, //随机串     
                                    "package":data.package,     
                                    "paySign":data.paySign, //微信签名 
                                    "signType":data.signType,         //微信签名方式:     
                                    "timeStamp":data.timeStamp //时间戳，自1970年以来的秒数     
                                },
                                function(res){
                                    if(res.err_msg == "get_brand_wcpay_request:ok" ){
										var isInWeiXin = true;                                    	
										setTimeout(function() {
											var path = global_util.getAppPath();
                                            window.location=path+"/";
										}, 3000);
										
                                        layer.msg("充值成功, 资金会在5分钟内到账", {time: 2000}, function(index){
                                            layer.close(index);
                                            if(isInWeiXin){
                                                //window.history.back();
                                                //var path = global_util.getAppPath();
                                                //window.location=path+"/";
                                            	/* var pack = data.package;
                                                var orderId = pack.substr(10);
                                                alert(orderId);
                        	                	var path = global_util.getAppPath();
                        	                	$.get(path + "/wx/pay_notify", {orderId: orderId, status : "1"}, function(data) {
                        	                		console.log(data);
                        	                	});
                        	                	alert("cat");
                        	                	window.location = path + "/wx/pay_callback?orderId=" + orderId; */
                                            }else{
                                                window.close();
                                            }
                                        });
                                    }else if(res.err_msg == "get_brand_wcpay_request:cancel"){
                                    }else {
                                        layer.msg("充值失败：" + JSON.stringify(res));
                                    }
                                    chackbut=true;
                                }); 
                    		} else { //private pay channel
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
                    		}
                    	});
                    }  
                }
            }(this));
        </script>
	    <script type="text/javascript" src="http://pingjs.qq.com/h5/stats.js" name="MTAH5" sid="500049057" ></script> 
	</body>
</html>
