<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="applicationnd.wap.xhtml+xml;charset= UTF-8" />
        <meta http-equiv="expires" content="2678400" />
        <meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
        <meta name="keywords" content="" />
        <meta name="description" content="" />
        <title>个人中心</title>
        <link href="<c:url value='/styles/main.css?t=${version}' />" type="text/css" rel="stylesheet" />
        <link href="<c:url value='/styles/geren.css?t=${version}' />" type="text/css" rel="stylesheet" />
        <script src="<c:url value='/scripts/prepare.js?t=${version}' />" type="text/javascript"></script>
        <script type="text/javascript"><c:set var="basePath" value="${pageContext.request.contextPath}" />
</script>
    </head>
    <body style="background-color: white;">
    <input type="hidden" id="wxid" name="wxid" value="${user.wxid}"/>
    	<%@include file="/WEB-INF/jsp/head/common.jsp"%>
        <!--个人中心-->
        <div class="personal">
            <div class="per_top">
                <div class="boxflex">
                    <div class="img-wrap"><img class="userimage" src="${user.headimgurl}" /></div>
                    <div class="box_flex_1">
                    	<div class="p_zichan" style="color:#333;font-size: 16px;font-weight: 700;">${user.wxnm}</div>
<%--                         <div class="p_zichan">资产：<span id="total-asset">${user.allBalance}</span>元</div> --%>
                    </div>
                    <div class="btncenter-withdraw-wrap">
                      <div class="recharge" id="exchangeHis" ><a>兑换历史</a></div>
                        <div class="withdraw" id="exchange"><a>兑换</a></div>
                    </div>
                </div>
                <div class="boxflex cash-wrap">
                    <div class="box_flex_1">
                        <div class="p_zijin" >
                        		<div id="recharge" style="height:40px;line-height:40px;overflow:hidden;"><a >充值</a></div>
<%-- 	                        <div class="key" id="able-cash">${user.balance}</div> --%>
<!-- 	                        <div class="value">可用资金</div> -->
                        </div>
                    </div>
                    <div class="box_flex_1">
                        <div class="p_zijin">
	                        <div class="key" id="used-cash">${user.carryAmount}</div>
	                        <div class="value">我的金币</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="center-list-wrap">
                <ul>
                    <li class="table" data-index="0">
                        <i class="icon icon-operrec table-cell"></i>
                        <span class="table-cell title-text">挖宝操作轨迹</span>
                        <span class="earrow earrow-right table-cell"></span>
                    </li>
                    <li class="table" data-index="1">
                        <i class="icon icon-income table-cell"></i>
                        <span class="table-cell title-text">出入金记录</span>
                        <span class="earrow earrow-right table-cell"></span>
                    </li>
<!--                     <li class="table" data-index="3"> -->
<!--                         <i class="icon icon-setting table-cell"></i> -->
<!--                         <span class="table-cell title-text">个人设置</span> -->
<!--                         <span class="earrow earrow-right table-cell"></span> -->
<!--                     </li> -->
                    <!-- <li class="table" data-index="4">
                        <i class="icon-manage table-cell"></i>
                        <span class="table-cell title-text">经纪人</span>
                        <span class="earrow earrow-right table-cell"></span>
                    </li> -->
                </ul>
            </div>
		</div>
   	 	<div style="text-align: center;margin-top: 20px;">
        	<c:if test="${user.headimgurl==''}">
        		<span style="color:red;">请先扫码关注</span><br/><img width="80%;" src="${shellCode}"/>
        	</c:if>
    	</div>
        <div style="text-align: center;margin-top: 20px;">
        	<div style="padding:0px;margin:0px;font-size: 14px;">「我的金币」可以兑换成「九州购」所需资金，<br/>点击右上方「游戏金兑换」按钮进行兑换。<br/>具体参见服务中心帮助文档<br/><span style="color:gray;">联系客服<br/>微信：jzwy_kefu&nbsp;&nbsp;&nbsp;QQ：3467586457</span></div>
    	</div>
        <script src="<c:url value='/scripts/main.js?t=${version}' />" type="text/javascript"></script>
        <script type="text/javascript">
            (function(root){
//                 root.ServerAndGoodListData =$.splitServerAndGoodList('{"Servers":[{"ServerId":13,"ServerIP":"114.55.63.183","ServerPort":8502,"ServerName":"点差宝155web行情2","ServerType":3,"ServerParam":""},{"ServerId":6,"ServerIP":"112.124.47.55","ServerPort":9501,"ServerName":"点差宝查询中心1","ServerType":2,"ServerParam":""},{"ServerId":8,"ServerIP":"112.124.9.90","ServerPort":8502,"ServerName":"点差宝155web行情1","ServerType":3,"ServerParam":""},{"ServerId":9,"ServerIP":"112.124.9.90","ServerPort":80,"ServerName":"点差宝充值提现地址1","ServerType":4,"ServerParam":""}],"Merchs":[{"MarketId":17000,"MerchCode":"BU","Name":"刚玉","UnitNum":1,"ShowUnit":"吨","Dec":0,"Margin":"10,100,500","FreezeTime":60,"Point":"5,13|7,11|11,7.5","MaxBuyNum":6},{"MarketId":17000,"MerchCode":"AG","Name":"银基合金","UnitNum":1,"ShowUnit":"千克","Dec":0,"Margin":"10,100,500","FreezeTime":60,"Point":"4,13|6,11|10,7.5","MaxBuyNum":6},{"MarketId":17000,"MerchCode":"CU","Name":"中金铜","UnitNum":1,"ShowUnit":"吨","Dec":0,"Margin":"10,100,500","FreezeTime":60,"Point":"30,13|60,11|100,7.5","MaxBuyNum":6}],"SecKey":"I8PZHGV7BACA3xwodpfdHwyQsMLICJ_NVzJHBbVKIvJCLlfu2K","Orgs":[]}');
				root.accountId = $("#wxid").val();
                //root.usermanager="http://lianzhihan.zjwlce.cn/pointbroker/index.html?openID=&chaturl=http://http://lianzhihan.zjwlce.cn/pointchat/Chat/ChatPushServlet/pointchat/Chat/ChatQrCodeServlet"
            }(this));
        </script>
<%--         <script src="<c:url value='/scripts/cgmanage.js?t=${version}' />" type="text/javascript"></script> --%>
        <script src="<c:url value='/scripts/start.js?t=${version}' />" type="text/javascript"></script>
        <script src="<c:url value='/scripts/usercenter.js?t=${version}' />" type="text/javascript"></script>
        <script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>
        <script src="<c:url value='/scripts/share.js?t=${version}' />" type="text/javascript"></script>
        <script type="text/javascript" src="https://www.miliduobao.com/style/mobile/js/jquery-1.8.3.min.js"></script>
        <script type="text/javascript" src="http://pingjs.qq.com/h5/stats.js" name="MTAH5" sid="500049057" ></script>
        <script src="<c:url value='/scripts/util.js' />"></script>
        <script>
        $(document).ready(function() {
        	var path = global_util.getAppPath();
        	$("#recharge").click(function() {
        		//alert(path);
        		window.location = path + "/shop/${user.wxid}";
        	});
        	
        	$("#exchange").click(function() {
//         		window.location = "http://game.juzizou.com/plaza/wx/duobaoredirect/transferfromgame";
        		window.location = path + "/checkIssubscribe/${user.wxid}";
        	});
        	$("#exchangeHis").click(function() {
        		window.location = "http://game.juzizou.com/plaza/wx/duobaoredirect/cardexchangetasks";
        	});
        });
        </script>
    </body>
</html>