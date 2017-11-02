<?php

defined('G_IN_SYSTEM')or exit("no");
class wxloginwap extends SystemAction {
	
	private $qc;
	private $db;
	private $qq_openid;
	
	//wexin登录
	public function init(){
		$web_path = G_WEB_PATH.'/index.php/api/wxloginweb';
		$jspath = G_TEMPLATES_STYLE;
		$def_url = '<html>
		<head>
		<meta charset="utf-8">
		<script type="text/javascript" src="' . $jspath . '/js/jquery-1.8.3.min.js"></script>
		</head>
		<body>
		正在登陆...
		<script language="javascript">
		window.onload = function(){
			try{
			 	if(window.apk.wxlogin && typeof(window.apk.wxlogin) == "function"){
			 		window.apk.wxlogin();
				}
			}catch(e){
			   	//alert("登陆错误，请联系管理员");
				window.location.href="' . $web_path . '/error";
				
			}
			function payCallback(ret){
				window.location.href="' . $web_path . '/callback?status=" + ret.status + "&openid=" + ret.openid + "&nickname=" + ret.nickname + "&headimgurl=" + ret.headimgurl;
			}
			
		}
		</script>
		</body>
		<html>';
		echo $def_url;
		exit();
	}
	//wexin回调
	public function callback(){
	//var_dump($_GET);die;
		$status = empty($_GET['status']) ? '' : $_GET['status'];
		$openid = empty($_GET['openid']) ? '' : $_GET['openid'];
		$nickname = empty($_GET['nickname']) ? '' : $_GET['nickname'];
		$headimgurl = empty($_GET['headimgurl']) ? '' : $_GET['headimgurl'];
		if($status!='success' || empty($openid)) _messagemobile('授权失败',WEB_PATH.'/mobile/user/login');
		$user_info = array('openid' => $openid, 'nickname' => $nickname, 'headimgurl' => $headimgurl);
		$this->qc = $user_info;
		$weixin_openid = $user_info['openid'];
		if(empty($weixin_openid)){
			_messagemobile("信息获取失败，请返回后重新操作!",WEB_PATH.'/mobile/user/login');
			exit();
		}
		$this->qq_openid = $weixin_openid;
		$this->db = System::load_sys_class("model");
		$go_user_info = $this->db->GetOne("select * from `@#_member_band` where `b_code` = '$weixin_openid' LIMIT 1");
		if(!$go_user_info){
			$this->qq_add_member();
		}else{
			$uid = intval($go_user_info['b_uid']);
			$this->qq_set_member($uid,'login_bind');
		}
	}

	private function qq_add_member(){
		generate_log_callback("in wxloginwap.action.php");
		$go_user_info = $this->qc;
		$member_db=System::load_app_class('base','member');
		$memberone=$member_db->get_user_info();
		if($memberone){
			$go_user_id = $memberone['uid'];
			$qq_openid    = $this->qq_openid;
			$go_user_time = time();
			$this->db->Query("INSERT INTO `@#_member_band` (`b_uid`, `b_type`, `b_code`, `b_time`) VALUES ('$go_user_id', 'weixin', '$qq_openid', '$go_user_time')");
			_messagemobile("微信绑定成功",G_WEB_PATH);
			exit();
		}
		
		$go_user_time = time();
		if(!$go_user_info)$go_user_info=array('nickname'=>'QU'.$go_user_time.rand(0,9));
		$go_y_user = $this->db->GetOne("select * from `@#_member` where `username` = '$go_user_info[nickname]' LIMIT 1");
		if($go_y_user)$go_user_info['nickname'] .= rand(0,9);
		$go_user_name = $go_user_info['nickname'];
		$go_user_img  = 'photo/member.jpg';
		$go_user_himg  = $go_user_info['headimgurl'];
		$go_user_pass = md5('123456');
		$qq_openid    = $this->qq_openid;
		$this->db->Autocommit_start();
		$code=abs(intval(_encrypt(_getcookie("code"),'DECODE')));
		
		if($code>0){
			$decode = $code;
		}else{
			$decode = 0;
		}

		$cookie_data = $_COOKIE['data'];
		generate_log_callback("cookie data :" .$cookie_data);

		$q1 = $this->db->Query("INSERT INTO `@#_member` (`username`,`password`,`img`,`yaoqing`,`headimg`, `reg_key`, `wxid`,`time`) VALUES ('$go_user_name','$go_user_pass','$go_user_img','$decode','$go_user_himg', '$cookie_data', '$qq_openid','$go_user_time')");
		$go_user_id = $this->db->insert_id();
		$q2 = $this->db->Query("INSERT INTO `@#_member_band` (`b_uid`, `b_type`, `b_code`, `b_time`) VALUES ('$go_user_id', 'weixin', '$qq_openid', '$go_user_time')");
		// 查询用户注册
		if($q1 && $q2){
			$this->db->Autocommit_commit();
			$this->qq_set_member($go_user_id,'add');

		}else{
			$this->db->Autocommit_rollback();
			_messagemobile("登录失败!",WEB_PATH.'/mobile/user/login');
			exit();
		}
		
	}

	private function qq_set_member($uid=null,$type='bind_add_login'){	
		
		$member_db=System::load_app_class('base','member');
		$memberone=$member_db->get_user_info();
		if($memberone){
			_messagemobile("该微信号已绑定！",WEB_PATH.'/mobile/user/login');
			exit();
		}
		$member = $this->db->GetOne("select uid,password,mobile,email from `@#_member` where `uid` = '$uid' LIMIT 1");		
		$_COOKIE['uid'] = null;
		$_COOKIE['ushell'] = null;
		$_COOKIE['UID'] = null;
		$_COOKIE['USHELL'] = null;	
		$s1 = _setcookie("uid",_encrypt($member['uid']),60*60*24*7);			
		$s2 = _setcookie("ushell",_encrypt(md5($member['uid'].$member['password'].$member['mobile'].$member['email'])),60*60*24*7);
		if($s1 && $s2){
			header("location:".WEB_PATH.'/mobile/home');
		}else{
			_messagemobile("登录失败请检查cookie!",WEB_PATH.'/mobile/user/login');
			exit();
		}		
	}
	
	public function wx_set_config(){
		System::load_app_class("admin",G_ADMIN_DIR,'no');
		$objadmin = new admin();		
		$config = System::load_app_config("connect");
		if(isset($_POST['dosubmit'])){
			$wx_off = intval($_POST['type']);
			$wx_id = $_POST['id'];
			$wx_key = $_POST['key'];
			$config['weixin'] = array("off"=>$wx_off,"id"=>$wx_id,"key"=>$wx_key);
			$html = var_export($config,true);
			$html = "<?php return ".$html."; ?>";
			$path =  dirname(__FILE__).'/lib/connect.ini.php';
			if(!is_writable($path)) _message('Please chmod  connect.ini.php  to 0777 !');
			$ok=file_put_contents($path,$html);
			_message("配置更新成功!");
			exit();
		}
		$config = $config['weixin'];		
		include $this->tpl(ROUTE_M,'wx_set_config');
	}
	
	public function error() {
		_messagemobile('请下载APP登陆',WEB_PATH.'/mobile/user/login');
		exit();
	}

	
}

?>