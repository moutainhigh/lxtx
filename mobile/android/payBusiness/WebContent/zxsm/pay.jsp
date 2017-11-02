<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
	<title>威富通Native扫码支付测试页面</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="css/index.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
	<script type="text/javascript">
		
		function doSubmit(){
			$('#orderId').val("106001-"+$("#fee").val()+"-"+new Date().getTime());
			$('form').submit();
		}
	</script>
</head>
<body text=#000000 bgColor="#ffffff" leftMargin=0  topMargin=4>
	<div id="main">
        <div class="cashier-nav">
            <ol>
				<li class="current">1、提交信息（Native扫码支付） </li> 
            </ol>
        </div>
        <form action="http://115.28.52.43:9001/pay/netpay/appclient/getUrl.do" method="post"  target="_blank">
        	<input type="hidden" name="baseOrder.paymentMethodId" value="3"/>
            <div id="body" style="clear:left">
                <dl class="content">
                    <dt>商户订单号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input id="orderId" name="baseOrder.orderId" value="" maxlength="32" size="30"  placeholder="长度32"/>
                        <span class="null-star">(长度32)*</span>
                        <span></span>
                    </dd>
                    <dt>总金额：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input id="fee" name="baseOrder.price" value="100"  placeholder="单位：分"/> 
                        <span class="null-star">(单位：分 整型)*</span>  
                        <span></span>
                    </dd>
                    <dt>回跳地址：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="callbackUrl" value="http://localhost:8080/pay/netpay/zxsm/payCallBack.jsp"  placeholder=""/> 
                        <span class="null-star">(单位：分 整型)*</span>  
                        <span></span>
                    </dd>
                    <dt>回调地址：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="notifyUrl" value="http://www.baidu.com"  placeholder=""/> 
                        <span class="null-star">(单位：分 整型)*</span>  
                        <span></span>
                    </dd>
                    <dd>
                        <span class="new-btn-login-sp">
                            <button class="new-btn-login" type="button" onclick="doSubmit()" style="text-align:center;">确 认</button>
                        </span>
                    </dd>
                </dl>
            </div>
		</form>
        <div id="foot">
			<ul class="foot-ul">
				<li><font class="note-help">如果您点击“确认”按钮，即表示您同意该次的执行操作。 </font></li>
				<li>
					威富通版权所有 
				</li>
			</ul>
		</div>
	</div>
</body>
</html>