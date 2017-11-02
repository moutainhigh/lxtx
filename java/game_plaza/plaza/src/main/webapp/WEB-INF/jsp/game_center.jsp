<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<meta name="viewport" content="width=device-width,inital-scale=1.0,minimum-scale=0.5,maximum-scale=2.0,width=640,user-scalable=no">
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

<title>游戏大厅</title>
    <link href="<c:url value='/statics/game_center/sample.css?t=${version}' />" rel="stylesheet" type="text/css">
    <script src="<c:url value='/statics/game_center/hm.js?t=${version}' />"></script><script language="javascript" src="<c:url value='/statics/game_center/jquery-1.10.2.min.js' />"></script>
    <script language="javascript" src="<c:url value='/statics/game_center/jquery.cookie.js' />"></script>
    <script language="javascript" src="<c:url value='/statics/game_center/ajaxsubmit.js?t=${version}' />"></script>
    <script src="<c:url value='/statics/game_center/jweixin-1.0.0.js' />"></script>
    
    <link href="<c:url value='/statics/game_center/datum.css?t=${version}' />" rel="stylesheet" type="text/css">
    <link href="<c:url value='/statics/game_center/xcConfirm.css?t=${version}' />" rel="stylesheet" type="text/css">
    <link href="<c:url value='/statics/game_center/index.css?t=${version}' />" rel="stylesheet" type="text/css">
    <link href="<c:url value='/statics/game_center/vip.css?t=${version}' />" rel="stylesheet" type="text/css">

    
</head>

<body>

    <script src="<c:url value='/statics/game_center/jquery.SuperSlide.2.1.1.js' />"></script>
    <script language="javascript">
    	getUserInfo();
	    function getUserInfo(){
	    	$.ajax({
	            type: "POST",
	            url:  '${basePath}/getUserInfo/${user.wxid}',
	            dataType: 'json',
	            error: function (request) {
	                console.log('error');
	            },
	            success: function (data) {
	                if (data.code == 0) {
	                   $("#carryAmount").html(data.data.carryAmount);
	                }
	            }
	        });
	    }
	    $(function () {
	    	$(".vipreturn").click(function(){
	    		sign();
	    	});
	    })
    	
		function sign(){
           var url = "${basePath}/getFreeAmount/${user.id}";
       	   closeDiv();
       	   $.ajax({
				type: "POST",
				url:  url,
				dataType: 'json',
				error: function (request) {
	          	},
	          	success: function (data) {
		          	if(data.code==0){
			    	   	alert("成功领取"+data.money+"金币。")
			    	   	getUserInfo();
		          	}else{
		          		console.log("领取失败。");
		          	}
		    	   	setTimeout(function(){
		    		   checkNotice();
		    	   	},5000)
	            }
   	        });
		}
		
		function showDiv(){
            document.getElementById('popDiv').style.display='block';
            document.getElementById('bg').style.display='block';
        }

        function closeDiv(){
            document.getElementById('popDiv').style.display='none';
            document.getElementById('bg').style.display='none';
        }

    	setTimeout(function(){
			//每天免费领取金币
    		$.ajax({
	            type: "POST",
	            url:  '${basePath}/checkFreeAmount/${user.id}',
	            dataType: 'json',
	            error: function (request) {
	            },
	            success: function (data) {
	            	if(data.checkFree=='1'){
	            		$("#freeCount").html(data.gSendFree.freeCount);
	            		$(".boxmoney").html(data.gSendFree.money+"金币");
	            		showDiv();
					}else{
						checkNotice();
					}
	            }
	        });
		},1000)
		function checkNotice(){
	    	$.ajax({
	            type: "POST",
	            url:  '${basePath}/checkNotice',
	            dataType: 'json',
	            error: function (request) {
	                console.log('error');
	            },
	            success: function (data) {
	            	if(data.needNotice=='1'){
						var notice = data.notice;
						window.wxc.xcConfirm(notice.content,{title:notice.title})
					}
	            }
	        });
    	}
/*if(window.orientation!=0){
        var obj=document.getElementById('orientation');
        alert('横屏内容太少啦，请使用竖屏观看！');
        obj.style.display='block';
}

window.onorientationchange=function(){ 
var obj=document.getElementById('orientation');

if(window.orientation==0){
                obj.style.display='none';
        }else
        {
                alert('横屏内容太少啦，请使用竖屏观看！');
                obj.style.display='block';
        }
}; */
</script>
<style type="text/css">
section .prompt{
	height:46px;
	line-height:46px;
	background:#16171f;
	overflow:hidden;
	margin-top:5px;
}
section .prompt span{
	float:left;
	margin:10px 0 10px 12px;
	width:32px;
	height:28px;
}
section .prompt p{
	float:right;
	max-width:515px;
	margin-right:22px;
	font-size:24px;
	color:#FFFFFF;
}
.txtScroll-top{ width:550px;  overflow:hidden; position:relative;}
.txtScroll-top .infoList li{ height:46px; line-height:46px;   }

</style>
	<div class="vipbox" id="popDiv" style="display: none">
		<div class="viptop">
			<p>金币天天送</p>
		</div>
		<div class="vipcontent">

			<div class="viptitle">
				已送<span id="freeCount"> </span>天
			</div>
			<div class="vipboxlist" style="margin: 0 auto; text-align: center">
				<div class="vipcheckbox" style="margin: 0 auto; text-align: center">
					<div class="boxtitle"></div>
					<div class="boximg">
						<p>
							<img
								src="<c:url value='/statics/game_center/vipqd_19.png" alt=""' />">
						</p>
					</div>
					<div class="boxmoney"></div>
				</div>
			</div>
		</div>
		
			<div class="vipreturn">领取奖励</div>
	</div>
	<div id="bg" class="vipbg" style="display: none;"></div>

	<div class="overall" id="orientation">
    <header>
    	<p style="float: left;margin:11px 0 0 35px;"><img src="<c:url value='/statics/game_center/gametop.png' />"></p>
        <div class="message">
        	<a href="${basePath}/user/enterUserCenter">
        		<c:if test="${user.headimgurl == ''}">
                	<img src="<c:url value='/statics/game_center/01.png' />">
        		</c:if>
        		<c:if test="${user.headimgurl != ''}">
                	<img style="width:44px;height:44px;" src="${user.headimgurl}">
        		</c:if>
                                            </a>
            <a href="${basePath}/shop/${user.wxid}" class="resources"><span><img src="<c:url value='/statics/game_center/gold.png' />"></span><text id="carryAmount">${user.carryAmount}</text><span><img src="<c:url value='/statics/game_center/001.png' />"></span></a>
        </div> 
    </header>
	<section>
      <div class="prompt"><span style="margin: 10px 12px;"><img src="<c:url value='/statics/game_center/prompt.png' />"></span>
             <div class="txtScroll-top">
                 <!---->
                     <!--<p class="marquee"><marquee width="603px;"></marquee></p>-->
                     <!---->

                 <div class="bd">
                     <div class="tempWrap" style="overflow:hidden; position:relative; height:46px"><ul class="infoList" style="height: 230px; position: relative; padding: 0px; margin: 0px; top: -46px;"><li class="clone" style="height: 46px;"></li>
                         <li style="height: 46px;"><a href="" style="color:#fff;font-size:23px;">欢迎来到乐玩九州中心，为了给您更好的游戏体验，建议您关注我们的微信公众号“乐玩九州”！</a></li>
                             <li style="height: 46px;"></li>
                             <li style="height: 46px;"></li>
                                                             <li class="clone" style="height: 46px;"><a href="" style="color:#fff;font-size:23px;">欢迎来到乐玩九州中心，为了给您更好的游戏体验，建议您关注我们的微信公众号“乐玩九州”！</a></li></ul></div>
                 </div>
             </div>

             <script type="text/javascript">
                 jQuery(".txtScroll-top").slide({titCell:".hd ul",mainCell:".bd ul",autoPage:true,effect:"topLoop",autoPlay:true,vis:1,delayTime:700});
             </script>
         </div>
    	<div class="message1">
			<c:if test="${user.chnno==9998}">
		   		<div class="gtitle"><img src="<c:url value='/statics/game_center/index_13.png' />" alt=""><span>红包娱乐场</span></div>
		   		<div class="gamelist">
		   				<a href="${basePath}/platform/redpacket"><div class="game"><img src="<c:url value='/statics/game_center/hongbao.png' />" style="margin-top:20px" alt=""><span class="gametitle">红包</span></div></a>
		   		</div>
    		</c:if>
			<div class="gtitle"><img src="<c:url value='/statics/game_center/index_13.png' />" alt=""><span>竞猜娱乐场</span></div>
            <div class="gamelist">
            	<c:if test="${user.chnno==9998}">
	                <a href="${basePath}/luxurycar/${user.wxid}"><div class="game"><img src="<c:url value='/statics/game_center/car.png' />" style="margin-top:20px" alt=""><span class="gametitle">豪车俱乐部</span></div></a>
	                <a href="${basePath}/animal/${user.wxid}"><div class="game"><img src="<c:url value='/statics/game_center/index_17.png' />" style="margin-top:10px" alt=""><span class="gametitle">全民生肖</span></div></a>
                </c:if>
<%--                 <a href="${basePath}/animal/${user.wxid}"><div class="game"><span class="online">在线人数:<span>135</span></span><img src="<c:url value='/statics/game_center/index_17.png' />" style="margin-top:10px" alt=""><span class="gametitle">全民生肖</span></div></a> --%>
				<a href="${basePath}/shake/${user.wxid}"><div class="game">
<!-- 				<span class="online">在线人数:<span>143</span></span> -->
				<img src="<c:url value='/statics/game_center/index_24.png' />" style="margin-top:30px" alt=""><span class="gametitle">摇摇乐</span></div></a>
				<a href="${basePath}/dig/${user.wxid}"><div class="game">
<!-- 				<span class="online">在线人数:<span>120</span></span> -->
				<img src="<c:url value='/statics/game_center/index_20.png' />" style="margin-top:30px" alt=""><span class="gametitle">全民挖宝</span></div></a>
            </div>
            <c:if test="${user.chnno==9998}">
				<div class="gtitle"><img src="<c:url value='/statics/game_center/index_13.png' />" alt=""><span>休闲娱乐场</span></div>
	    		<div class="gamelist">
	
	    				<a href="${basePath}/platform/ddzLogin"><div class="game"><img src="<c:url value='/statics/game_center/ddz.png' />" style="margin-top:20px" alt=""><span class="gametitle">斗地主</span></div></a>
	    		</div>            
   			</c:if>
        </div> 
    </section>
    <script language="javascript" src="<c:url value='/statics/game_center/xcConfirm.js?t=${version}' />"></script>
    <script>
        $(function(){
//             var msg = "";
//             if(msg != ''){
//                 alert(msg);
//             }

//             var t = 'no';
//             var url = "/index.php/task.html";
//             if(t == 'no'){
//                 window.onload = function(){
//                     var txt=  "是否完成新手引导领取任务?";
//                     window.wxc.xcConfirm(txt, window.wxc.xcConfirm.typeEnum.confirm,{onOk:function(){
//                         location.href = url;
//                     }});
//                 }
//             }
        })

    </script>
    <footer>
    <a class="j1 selected" href="${basePath}/game_center/${user.wxid}/${user.chnno}"><span style="background:url(<c:url value='/statics/game_center/1.png' />) no-repeat;background-position:center 2px;"></span><p>竞猜大厅</p></a>
    <a class="j2" href="${basePath}/shop/${user.wxid}"><span style="background:url(<c:url value='/statics/game_center/2.png' />) no-repeat;background-position:center -43px;"></span><p>充值</p></a>
<%--     <a class="j3" href="${basePath}/riches/${user.wxid}"><span style="background:url(<c:url value='/statics/game_center/3.png' />) no-repeat;background-position:center -46px;"></span><p>财富榜</p></a> --%>
    <a class="j4 " href="${basePath}/user/enterUserCenter"><span style="background:url(<c:url value='/statics/game_center/4.png' />) no-repeat;background-position:center -43px;"></span><p>个人信息</p></a>
</footer>
</div> 
<div style="display:none">
<audio id="car_audio" controls preload="preload" autoplay="autoplay" loop="loop">
<%--     <source src="<c:url value='/statics/game_center/bg.mp3' />" type="audio/mp3" /> --%>
<%--     <source src="<c:url value='/statics/game_center/bg.ogg' />" type="audio/ogg" /> --%>
</audio>
</div>


<script>
    $(function(){
        wx.config({
            debug: false,
            appId: '',
            timestamp: '',
            nonceStr: '',
            signature: '',
            jsApiList: [
            // 所有要调用的 API 都要加到这个列表中
            'checkJsApi',
            'openLocation',
            'getLocation',
            'onMenuShareTimeline',
            'onMenuShareAppMessage'
        ]
    });


        wx.ready(function () {
            wx.onMenuShareAppMessage({
                title: '',
                desc: '',
                link: '',
                imgUrl: '',
                trigger: function (res) {
                    // 不要尝试在trigger中使用ajax异步请求修改本次分享的内容，因为客户端分享操作是一个同步操作，这时候使用ajax的回包会还没有返回
//                     alert('用户点击发送给朋友');
                },
                success: function (res) {
                    $.post("share",{'success':'yes'},function(){

                    })
                },
                cancel: function (res) {
                },
                fail: function (res) {
                }
            });

            wx.onMenuShareTimeline({
                title: '',
                link: '',
                imgUrl: '',
                trigger: function (res) {
                    // 不要尝试在trigger中使用ajax异步请求修改本次分享的内容，因为客户端分享操作是一个同步操作，这时候使用ajax的回包会还没有返回
                },
                success: function (res) {
                    $.post("share",{'success':'yes'},function(){

                    })
                },
                cancel: function (res) {
                },
                fail: function (res) {
                }
            });
        });

    })
</script>
</body></html>