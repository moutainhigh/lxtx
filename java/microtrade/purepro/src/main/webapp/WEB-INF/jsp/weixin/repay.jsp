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
		<title>提现</title>
		<link href="<c:url value='/payment/styles/main.css?t=${version}' />" type="text/css" rel="stylesheet" />
		<link href="<c:url value='/payment/styles/zichan.css?t=${version}' />" type="text/css" rel="stylesheet" />
		<script type="text/javascript" src="<c:url value='/scripts/jquery.js'/>" ></script>
        <script type="text/javascript" src="<c:url value='/scripts/util.js?t=${version}' />" ></script>
        <script type="text/javascript" src="<c:url value='/scripts/prepare.js?t=${version}' />" ></script>
		<script type="text/javascript">
			<c:set var="basePath" value="${pageContext.request.contextPath}" />
		</script>
	</head>
	<body>
		<%@include file="/WEB-INF/jsp/head/common.jsp"%>
	    <div class="recharge-box">
            <div class="accounts-wrap">
                <div class="image-wrap"><img src="${userInfo.headimgurl}" /></div>
                <div class="userinfo">
                    <div class="name">${userInfo.wxnm}</div>
                    <div class="asset-wrap">可提现金额：<span class="asset-cantake">0</span>元</div>
                </div>
            </div>
            
            <div class="payway-wrap">
                <ul class="money-wrap">
                    <li class="table">
                        <span class="table-cell key">提现金额</span>
                    </li>
                </ul>
                <script type="text/javascript">
                    var isIOS = !!navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), input = document.createElement("input");
                    (function(root){
                        input.className = "money table-cell", input.maxLength = 12, input.placeholder = "请输入提现金额";
                        if(isIOS){
                            input.type = "num";
                        }else{
                            input.type = "tel";
                        }
                        document.querySelector(".money-wrap li").appendChild(input);
                        input.focus();
                    }(this));
                    
                    $(document).ready(function() {
                    	$(".money").eq(0).val("");
                    	$(".money").eq(0).focus();
                    });
                </script>
                <div class="fee-tip"></div> 
                <div class="txway-wrap">
                    <div class="payway-title">提现方式</div>
                    <div class="payway-content">
                        <ul>
                            <li class="table">
                                <div class="table-cell symbol-wrap"><i class="icon icon-wx"></i></div>
                                <div class="table-cell">微信提现</div>
                                <div class="table-cell choose-wrap"><i class="choose choose-full">&#10003;</i></div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <a href="javascript:void(0);" id="f_but" class="yebz_but disable" >立即提现</a>
        </div>
		
		<script src="<c:url value='/scripts/main.js?t=${version}' />" type="text/javascript"></script>
	    
	    <script type="text/javascript">
            (function(root){
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
                var tap = "click";
				doStart();
                //提现方式
                    /*
                    * 0 微信提现
                    */
                var supportWeiXin = '10';
                var perfee = 0.01, status = 1, sysunionpay = undefined, sysbankinfo = undefined;
                function doStart(){
                	//可提现金额
                    $(".asset-cantake").text($.formatNumber('${userInfo.balance}', 2));
                	//手续费
                    $(".fee-tip").text("最小提现金额为10元，手续费" + (perfee=2) + "元/笔");
                    addEventListons();
				}
             	// 添加事件监听
                function addEventListons(){
                	
                    // 输入提现金额
                    $(".money").on("input", function(e){
                    	
                        var cantake = Number(document.querySelector(".asset-cantake").innerText);
                        
                        // 处理格式化价格
                        var $target = $(e.target), price = $target.val(), inputEnd = e.target.selectionEnd;
                        if(inputEnd === 0){inputEnd = 1;}      
                        if(price[inputEnd-1] == "."){
                            if($target.val().split("\.").length > 2){
                                $target.val(price.substring(0, inputEnd-1) + price.substring(inputEnd));
                            }
                        }else if(!/^[0-9]$/.test(price[inputEnd-1])){
                            $target.val(price.substring(0, inputEnd-1) + price.substring(inputEnd));
                        }else{
                            if(price.indexOf(".") > -1 && ((price.length - price.indexOf(".") - 1) > 2)){
                                $target.val(price.substring(0, inputEnd-1) + price.substring(inputEnd));
                            }
                        } 
                        var value = $target.val();
                        $target.val(value = value.replace(/[^\d\.]/g,""));
                        if(Number(this.value) > cantake){
                            this.value = cantake.toFixed(2);
                        }
                        
                    });
                    
                    $(".money").on("blur", function(){
                    	checkfromWX();
                    });
                   
                    // 点击提现
                    $("#f_but").off(tap).on(tap,function(e){
                    	if($(this).is(".disable")){
                            return;
                        }
                    	if(checkfromWX()){
	                    	e.preventDefault();
	                    	$('#f_but').addClass("disable");
	                    	wxTX();
                    	}
                    })
                    	
                }
                var checkbtntx = false;
                function wxTX(){
                	if(checkbtntx){
                		return;
                	}
                	checkbtntx = true;
                	if(global_util.validRepayTime()){
						checkbtntx = false;
						layer.msg("抱歉：请在工作日进行充值或提现！",{time:3e3},function(){})
					}else{
	                    /* if (typeof WeixinJSBridge == "undefined"){
	                        if( document.addEventListener ){
	                            document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
	                        }else if (document.attachEvent){
	                            document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
	                            document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
	                        }
	                     }else{
	                         onBridgeReady();
	                     } */
						 onBridgeReady();
					}
                }

                function onBridgeReady(){
                	layer.msg("正在提交申请.....");
                	$.judgeLoginStatus(function(){
	                	var path = global_util.getAppPath();
	                	$.get(path + "/wx/repay", {txnAmt: Math.round(Number($.trim($('.money').val()))*100)}, function(data) {
	                		checkbtntx=false;
	                		if (data.code == '-1') { //error
	                			layer.msg("提现未成功["+data.message+"]", {time: 3e3}, function(index){
									layer.close(index);
									//set focus
									$(".money").eq(0).val("");
									setTimeout(function(){
										$(".money").eq(0)[0].click();
									}, 2000);
								});
	                		} else {
								layer.msg("提现成功, 资金将在1个工作日内到账！"+data.message, {time: 3e3}, function(index){
									layer.close(index);
									if(isInWeiXin){
										//window.history.back();
										window.location = path + "/user/enterUserCenter";
									}else{
										window.close();
									}
								});	                			
	                		}
	                	});
                	
                    /*$.toRequestData({
                        //url: http://zhongjin.zjwlce.cn:80/payment/Page/WeiXinDFServlet?status=jsPay",
                        url : , 
                        data: { 
							
						},
                        sucBack: function(data){
                            checkbtntx=false;
							layer.msg("提现成功, 资金将在1个工作日内到账！", {time: 2000}, function(index){
								layer.close(index);
								if(isInWeiXin){
									window.history.back();
								}else{
									window.close();
								}
							});
                        },
                        errBack: function(msg){
                            layer.msg(msg);
                            checkbtntx=false;
                        }
                    });*/
                	})
                }  

                function checkfromWX(){
                   var txnAmt = $.trim($(".money").val()), 
                       validcode = $.trim($("#validcode").val()),  
                       msg = "";
                   
                    $('#f_but').addClass("disable");
                    if(txnAmt.length==0){
                        msg = "请输入提现金额";
                     	layer.msg(msg);
                     	return false;
                    } else if(Number(txnAmt)<10){
                    	msg = "最小提现金额为10元";
                        layer.msg(msg);
                        return false;
                    } else if(Number(txnAmt)<=perfee){
                        msg = "余额不足，提现金额需大于手续费";
                     	layer.msg(msg);
                     	return false;
                    }else{
                        $('#f_but').removeClass("disable");
                    }
                    return true;
                }
            }(this));
        </script>
		<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>
        <script type="text/javascript" src="http://pingjs.qq.com/h5/stats.js" name="MTAH5" sid="500049057" ></script> 
	</body>
</html>