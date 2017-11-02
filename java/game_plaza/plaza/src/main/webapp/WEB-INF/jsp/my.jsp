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

<title>个人信息</title>
    <link href="<c:url value='/statics/my/sample.css?t=${version}' />" rel="stylesheet" type="text/css">
    <script src="<c:url value='/statics/my/hm.js?t=${version}' />"></script><script language="javascript" src="<c:url value='/statics/my/jquery-1.10.2.min.js' />"></script>
    <script language="javascript" src="<c:url value='/statics/my/jquery.cookie.js' />"></script>
    <script language="javascript" src="<c:url value='/statics/my/ajaxsubmit.js?t=${version}' />"></script>
    <script src="<c:url value='/statics/my/jweixin-1.0.0.js' />"></script>
    
    <link href="<c:url value='/statics/my/datum.css?t=${version}' />" rel="stylesheet" type="text/css">

    
</head>

<body>


<div class="overall">
	<section>
    	<div class="account">
        	<div class="information">
            	<div class="install">
                	<a href="" class="install_l">
                	<c:if test="${user.headimgurl == ''}">
                	<img src="<c:url value='/statics/my/07.png' />">
        		</c:if>
        		<c:if test="${user.headimgurl != ''}">
                	<img style="width:74px;height:74px;" src="${user.headimgurl}">
        		</c:if>
                      <hgroup>
                        	<h3>${user.wxnm}</h3>
                        </hgroup>
                    </a>
<!--                     <span> -->
<%--                     	<a href="http://user.802game.com/index.php/info.html"><img src="<c:url value='/statics/my/02.png' />"></a> --%>
<%--                         <a href="http://user.802game.com/index.php/config.html"><img src="<c:url value='/statics/my/03.png' />"></a> --%>
<!--                     </span> -->
                </div>
                <div class="wealth">
                	<a href="">
                    	<span>
                        	<p class="attribute"><img src="<c:url value='/statics/my/coin.png' />">身上携带金币</p>
                            <p class="amount" id="carryAmount"><script>document.write(commafy('${user.carryAmount}'))</script></p>
                    	</span>
                    </a>
                    <a href="javascript:;">
                        <span>	
                            <p class="attribute"><img src="<c:url value='/statics/my/gold.png' />">银行金币</p>
                            <p class="amount" id="bankBalance"><script>document.write(commafy('${user.bankBalance}'))</script></p>
                    	</span>
                    </a>
                </div>
            </div>
            <div class="save">
            	<a href="${basePath}/shop/${user.wxid}"><p><img src="<c:url value='/statics/my/recharge.png' />">充值</p></a>
            </div>
        </div>
        <div class="detailed" style="margin-bottom:100px;">
        	<ul>
<!--             	<li> -->
<%--                 	<a href="http://user.802game.com/index.php/mylogs.html"><div class="detailed_logo"><img src="<c:url value='/statics/my/04.png' />"></div>竞猜记录<span><img src="<c:url value='/statics/my/right.png' />"></span></a> --%>
<%--                     <a href="http://user.802game.com/index.php/userlogs.html"><div class="detailed_logo"><img src="<c:url value='/statics/my/05.png' />"></div>账户明细<span><img src="<c:url value='/statics/my/right.png' />"></span></a> --%>
<%--                     <a href="http://user.802game.com/index.php/editpwd.html"><div class="detailed_logo"><img src="<c:url value='/statics/my/05.png' />"></div>修改密码<span><img src="<c:url value='/statics/my/right.png' />"></span></a>                </li> --%>
<!--                 <li> -->
<%--                 	<a href="http://user.802game.com/index.php/msg.html"><div class="detailed_logo"><img src="<c:url value='/statics/my/06.png' />"></div>站内消息<span><img src="<c:url value='/statics/my/right.png' />"></span></a> --%>
<%--                 	<a href="http://user.802game.com/index.php/kefu.html"><div class="detailed_logo"><img src="<c:url value='/statics/my/06.png' />"></div>联系客服<span><img src="<c:url value='/statics/my/right.png' />"></span></a> --%>
<!--                 </li> -->
            </ul>
        </div>
    </section>
    <footer>
    <a class="j1" href="${basePath}/game_center/${user.wxid}/${user.chnno}"><span style="background:url(<c:url value='/statics/game_center/1.png' />) no-repeat;background-position:center -40px;"></span><p>竞猜大厅</p></a>
    <a class="j2" href="${basePath}/shop/${user.wxid}"><span style="background:url(<c:url value='/statics/game_center/2.png' />) no-repeat;background-position:center -43px;"></span><p>充值</p></a>
<%--     <a class="j3" href="${basePath}/riches/${user.wxid}"><span style="background:url(<c:url value='/statics/game_center/3.png' />) no-repeat;background-position:center -46px;"></span><p>财富榜</p></a> --%>
    <a class="j4 selected" href="${basePath}/my/${user.wxid}"><span style="background:url(<c:url value='/statics/game_center/4.png' />) no-repeat;background-position:center 0px;"></span><p>个人信息</p></a>
</footer>
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
        
// 		$("#carryAmount").html(commafy('${user.carryAmount}'));
// 		$("#bankBalance").html(commafy('${user.bankBalance}'));

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
<script>
var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "https://hm.baidu.com/hm.js?891d2a14806d89e7ab281d30a3d3f786";
  var s = document.getElementsByTagName("script")[0]; 
  s.parentNode.insertBefore(hm, s);
})();
</script>
</body></html>