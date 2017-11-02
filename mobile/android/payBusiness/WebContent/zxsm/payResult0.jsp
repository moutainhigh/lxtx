<%@page import="java.net.URLDecoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB2312" />
<title>微信支付</title>
<meta name="description" content="微信支付" />
<meta name="keywords" content="微信支付" />
<script src="js/jquery-1.4.2.min.js" type="text/javascript"></script>
<link href="css/style.css" type="text/css" rel="stylesheet" />

	<script type="text/javascript">
	   var timer;
		$(function(){
			var handler = function(){
				var tradeId = $('#tradeId').val();
				$.getJSON("../appclient/query.do?id="+tradeId,function(msg){
					if(msg.status != 0){
						clearInterval(timer);
						
						if(msg.status == -2){
							alert("订单查询失败");
						}else{
							document.location.href=msg.callBackUrl+"?succ="+(msg.status==1?"true":"false")+"&orderId="+msg.orderId;		
						}
					}
				});
			}
			timer = setInterval(handler , 5000);
		});
	</script>
</head>
<body>
<div align="center" bgcolor="#666666">
   <div>
<!--扫描代码-->
	<input type="hidden" id="tradeId"  value="<%=request.getParameter("tradeId") %>"/>
      <div class="s-con" id="codem">
	   <div class="m26">
               <h1>订单提交成功，请您尽快付款！</h1>
               <div class="num"><span class="color1 ml16" style="font-size:15px;">订单号：<label id="out_trade_no" class="orange"><%=request.getParameter("out_trade_no") %></label></span><span class="color1 ml16">请您在提交订单后 <span class="orange">5分钟</span> 内完成支付，否则订单会自动取消。</span></div>
         </div>
         <div class="title"><span class="color1 ml16" style="font-size:15px;">商品名称：<label class="orange"><%=request.getParameter("body")%></label></span></br>
         <span class="color1 ml16" style="font-size:15px;">订单金额：<label class="orange"><%=Integer.parseInt(request.getParameter("total_fee"))/100 %></label>元</span>
		 
		 
		 </div>
         <div class="scan">
         <%if("1".equals(request.getParameter("status"))){ %>
         	<img src="<%=request.getParameter("code_img_url") %>"/>
         <%}else{ %>
         	发生错误
         <%} %>	
         </div>
         <div class="question">
            <div class="new"><a href="http://www.zhifuka.net/gateway/weifutong/bind.html" target="_blank">微信支付如何绑定银行卡?</a></div>
         </div>
      </div>
<!--扫描代码结束-->
<!--底部代码-->
      <div class="s-foot">深圳市威富通科技有限公司    粤ICP备13065478号-1</div>
<!--底部代码结束-->
   </div>
</div>
</body>
</html>