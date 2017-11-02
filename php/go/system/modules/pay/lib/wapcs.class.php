<?php
header("Content-Type:text/html;charset=UTF-8");

//include dirname(__FILE__) . DIRECTORY_SEPARATOR . "weixin" . DIRECTORY_SEPARATOR . "WxPayPubHelper.php";

class wapcs
{
	private $config;

	public function config($config = null)
	{
		$this->config = $config;
	}

	public function send_pay()
	{	
		$jspath = G_TEMPLATES_STYLE;
		$actionpath = $this->config['SubmitOrderUrl'];//G_WEB_PATH.'/index.php/pay/'.$this->config['type'].'_url/submitorder/';		
		$def_url = '<html>
		<head>
		<meta charset="utf-8">
		<script type="text/javascript" src="' . $jspath . '/js/jquery-1.8.3.min.js"></script>
		</head>
		<form action="'.$actionpath.'" method="post" id="orderForm">
			<input name="orderId" id="_orderId5" value="' . $this->config["code"] . '" type="hidden">
			<input id="_fee5" name="price" value="' . $this->config["money"] . '" type="hidden">
			<input name="desc" value="jifei" type="hidden">
			<input type="hidden" name="payType" id="_payType" value="zfb"/>
			<input name="callBackUrl" value="'.$this->config["ReturnUrl"].'" type="hidden">
			<input name="notifyUrl" value="'.$this->config["NotifyUrl"].'" type="hidden">
		</form>
		<body>
		正在充值...
		<script language="javascript">
		window.onload = function(){
			try{
			 	if(window.apk && window.apk.alipay && typeof(window.apk.alipay) == "function"){
			 		window.apk.alipay("' . $this->config["code"] . '", ' . $this->config['money'] . ');
				}  else {
					$("#orderForm").submit();
				}
			}catch(e){
			   	//alert("支付错误，请联系管理员");
				window.location.href="' . $this->config['ErrorUrl'] . '";
			}
		}
		function callback(ret){
			var status = ret == "success" ? "1" : "0";
			window.location.href=" ' . $this->config['CallbackUrl'] . '?status=" + status;
		}
		</script>
		</body>
		<html>';
		echo $def_url;
		exit;
	}
}