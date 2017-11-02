<?php
defined('G_IN_SYSTEM') or exit('No permission resources.');
ini_set("display_errors", "OFF");
//include dirname(__FILE__) . '/lib/weixin.class.php';

class wapwx_url extends SystemAction
{
	private $out_trade_no;

	public function __construct()
	{
		$this->db = System::load_sys_class('model');
	}

	public function qiantai()
	{	
		$path = G_HTTP.G_HTTP_HOST;
		$path .= str_replace($_SERVER['PATH_INFO'],'',$_SERVER['SCRIPT_NAME']);
		$status = empty($_GET['status']) ? '' : $_GET['status'];

		generate_log("qiantai callback, status is :".$status);
		generate_log("in qiantai, cookie is ".$_COOKIE['Cartlist']);
		if($status == '1'){
			generate_log("now cookie is:".$_COOKIE['Cartlist']." out_trade_no is:".$this->out_trade_no);

			$trade_no = $_GET['orderId'];
			$dingdaninfo = $this->db->GetOne("select * from `@#_member_addmoney_record` where `code` = '$trade_no'");
			if (empty($dingdaninfo['scookies'])) {
				if (empty($_COOKIE['Cartlist'])) {
					_messagemobile("充值成功",$path . "/mobile/home/userrecharge");	
				} else {
					_messagemobile("充值成功",$path . "/mobile/cart/pay");	
				}
			} else {
				$_COOKIE['Cartlist'] = '';
				_setcookie('Cartlist', NULL);				
				_messagemobile("支付成功",$path . "/mobile/home/userbuylist");	
			}

			exit();
		}else{
			_messagemobile("支付失败",$path . "/mobile/home/userrecharge");
			exit();
		}
	}

	public function submitorder() {
		$appId = "100002";//商户号
		$payType = $_REQUEST["payType"];//支付方式
		$orderId = $_REQUEST["orderId"];//商户流水号或订单号
		$price = floatval($_REQUEST["price"]);//订单金额
		$desc = $_REQUEST["desc"];//商品名称
		
		$callBackUrl = $_REQUEST["callBackUrl"];//通知商户页面端地址
		$callBackUrl = str_replace("&","%26", $callBackUrl);//callBackUrl.replace("&","%26");
		
		//$notifyUrl = "http://115.28.52.43:9020/pay/synch/netpay/jxtPayNotify.do";//服务器底层通知地址
		$notifyUrl = $_REQUEST['notifyUrl'];
		
		$appKey = "9205FB8024659A4E3B61F36CE61DA8B4";///////////商户密钥（KEY）(注意更改)
		
		$md5 = $appId.$orderId.$payType.$price.".0".$appKey;//MD5签名格式
		generate_log_product($md5);
		$sign = md5($md5);//计算MD5值
		
		$payUrl="http://pay.enanea.com/paycenter/appclient/getUrl.do";//API收银台支付Post请求地址
		
		//response.sendRedirect(payUrl+"?appId="+appId+"&orderId="+orderId+"&payType="+payType+"&price="+price+"&desc="+desc+"&callBackUrl="+callBackUrl+"&notifyUrl="+notifyUrl+"&sign="+sign);
		$targetUrl = $payUrl."?appId=".$appId."&orderId=".$orderId."&payType=".$payType."&price=".$price."&desc=".$desc."&callBackUrl=".$callBackUrl."&notifyUrl=".$notifyUrl."&sign=".$sign;
		generate_log_product($targetUrl);
		header('Location: ' . $targetUrl);
		exit;
	}

	public function houtai()
	{
		generate_log_callback("in wapwx_url.action.php."." houtai()");
		$sign = empty($_GET['sign'])? '' : $_GET['sign'];

		if((empty($_REQUEST['out_trade_no']) && empty($_REQUEST['orderId']))||empty($_REQUEST['status'])){
			echo '缺少参数';
			exit();
		}

		$sign = empty($_REQUEST['sign'])? '' : $_REQUEST['sign'];
		$out_trade_no = $_REQUEST['orderId'];
		if (empty($out_trade_no)) { //from apk
			$out_trade_no = $_REQUEST['out_trade_no'];
			$trade_status = $_REQUEST['status'];
			generate_log_product("getting houtai callback parameters.");
			generate_log_product($out_trade_no." ".$trade_status);
			if (!empty($trade_status) && ($trade_status == 'success') ) {
				$status = '1';
			} else {
				$status = '0';
			}
		} else {
			$status = empty($_REQUEST['status'])? '' : $_REQUEST['status'];			
		}
		generate_log_product("wx notify:".$out_trade_no."  ".$status);

		//if(empty($sign) || empty($out_trade_no) || empty($status)){
		//	echo '缺少参数';
		//	exit();
		//}
		
		if ($status == '1') {
			$this->out_trade_no = $out_trade_no;
			$ret = $this->wx_chuli();
			ob_clean();
			echo 'true';
			exit();
		} else {
			ob_clean();
			echo 'false';
			exit();
		}
	}

	public function houtai_cs()
	{
		$sign = empty($_GET['sign'])? '' : $_GET['sign'];
		$out_trade_no = empty($_GET['out_trade_no'])? '' : $_GET['out_trade_no'];
		$status = empty($_GET['status'])? '' : $_GET['status'];
		if(empty($sign) || empty($out_trade_no) || empty($status)){
			echo "缺少参数";
			exit();
		}
		
		if ($status == 'success') {
			//echo json_encode(array('status' => 'successs'));die;
			$this->out_trade_no = $out_trade_no;
			$ret = $this->wx_chuli();
			echo json_encode(array('status' => 'success'));
			exit();
		} else {
			echo "失败";
			exit();
		}
	}

	private function wx_chuli()
	{
		generate_log_product("in wx_chuli");
		$pay_type = $this->db->GetOne("SELECT * from `@#_pay` where `pay_class` = 'wapwx' and `pay_start` = '1'");
		$out_trade_no = $this->out_trade_no;
		$dingdaninfo = $this->db->GetOne("select * from `@#_member_addmoney_record` where `code` = '$out_trade_no'");
		if (!$dingdaninfo) {
			return false;
		}
		if ($dingdaninfo['status'] == '已付款') {
			return '已付款';
		}

		generate_log_product("in wx_chuli 1");
		$c_money = intval($dingdaninfo['money']);
		$uid = $dingdaninfo['uid'];
		$time = time();
		$this->db->Autocommit_start();
		$up_q1 = $this->db->Query("UPDATE `@#_member_addmoney_record` SET `pay_type` = '微信支付', `status` = '已付款' where `id` = '$dingdaninfo[id]' and `code` = '$dingdaninfo[code]'");
		$up_q2 = $this->db->Query("UPDATE `@#_member` SET `money` = `money` + $c_money where (`uid` = '$uid')");
		$up_q3 = $this->db->Query("INSERT INTO `@#_member_account` (`uid`, `type`, `pay`, `content`, `money`, `time`) VALUES ('$uid', '1', '账户', '充值', '$c_money', '$time')");

		generate_log_product("in wx_chuli 2");
		if ($up_q1 && $up_q2 && $up_q3) {
			$this->db->Autocommit_commit();
		} else {
			$this->db->Autocommit_rollback();
			return '充值失败';
		}
		if (empty($dingdaninfo['scookies'])) {
			generate_log_product("in wx_chuli 3");
			return "充值完成";
		}

		$scookies = unserialize($dingdaninfo['scookies']);
		generate_log_product($dingdaninfo['scookies']);
		$pay = System::load_app_class('pay', 'pay');
		$pay->scookie = $scookies;
		$ok = $pay->init($uid, $pay_type['pay_id'], 'go_record');
		if ($ok != 'ok') {
			$_COOKIE['Cartlist'] = '';
			_setcookie('Cartlist', NULL, 0, "/");
			return '商品购买失败';
		}
		generate_log_product("before invoking go_pay(1)");
		$check = $pay->go_pay(1);
		generate_log_product("go_pay result:".$check);
		if ($check) {
			$this->db->Query("UPDATE `@#_member_addmoney_record` SET `scookies` = '1' where `code` = '$out_trade_no' and `status` = '已付款'");
			generate_log_product("clean the cartlist.");
			$_COOKIE['Cartlist'] = '';
			//_setcookie('Cartlist', NULL);
			_setcookie('Cartlist', '', 0, "/");
			generate_log_product("now cookie is:"._getcookie('Cartlist'));
			return "商品购买成功";
		} else {
			return '商品购买失败';
		}
	}

	public function error()
	{	
		$path = G_HTTP.G_HTTP_HOST;
		$path .= str_replace($_SERVER['PATH_INFO'],'',$_SERVER['SCRIPT_NAME']);
		_messagemobile("支付错误，请联系管理员",$path . "/mobile/home/userrecharge");
		exit();	
	}
}