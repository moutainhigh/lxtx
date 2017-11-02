<?php
defined('G_IN_SYSTEM')or exit('No permission resources.');
System::load_app_class('base','member','no');
System::load_app_fun('my','go');
System::load_app_fun('user','go');
System::load_sys_fun('send');
System::load_sys_fun('user');
class home extends base {
	public function __construct(){
		parent::__construct();
		if(ROUTE_A!='userphotoup' and ROUTE_A!='singphotoup'){
			if(!$this->userinfo){
				if(!$uid && !$_GET['wxid']){
					if (strpos($_SERVER['HTTP_USER_AGENT'], 'MicroMessenger') !== false) {
						header("Location: ".WEB_PATH."/api/wxlogin");exit;
					}
				}
				if(!isset($_GET['wxid'])){
					//_messagemobile("请登录",WEB_PATH."/mobile/user/login",3);
					header("Location: ".WEB_PATH."/mobile/user/login");exit;
				}else{
					$wxid = $_GET['wxid'];
					if (empty($wxid)){
						//_messagemobile("请登录",WEB_PATH."/mobile/user/login",3);
						header("Location: ".WEB_PATH."/mobile/user/login");exit;
					}
					$mem=$this->db->GetOne("select * from `@#_member_band` where `b_code`='".$wxid."'");
					if (empty($mem)){
						//_messagemobile("请登录",WEB_PATH."/mobile/user/login",3);
						header("Location: ".WEB_PATH."/mobile/user/login");exit;
					}
					$this->userinfo=$member=$this->db->GetOne("select * from `@#_member` where `uid`='".$mem['b_uid']."'");
					_setcookie("uid",_encrypt($member['uid']),60*60*24*7);
					_setcookie("ushell",_encrypt(md5($member['uid'].$member['password'].$member['mobile'].$member['email'])),60*60*24*7);
					$backurl = $_GET['backurl'];
					if ($backurl) {
						generate_log_weixin("now redirect to path:".$backurl);
						header("Location: ".WEB_PATH.$backurl);
						exit();	
					}
				}
			} else {
				$backurl = $_GET['backurl'];
				if ($backurl) {
					generate_log_weixin("now redirect to path:".$backurl);
					header("Location: ".WEB_PATH.$backurl);
					exit();	
				}				
			}
		}
		/*
		if(!$this->userinfo['mobile']){
			header("location:".WEB_PATH."/mobile/user/mobile/");exit;
		}
		*/
		$this->db = System::load_sys_class('model');
	}

	
	public function init(){
	  $webname=$this->_cfg['web_name'];
		$member=$this->userinfo;
		$title="我的用户中心";
		//$quanzi=$this->db->GetList("select * from `@#_quanzi_tiezi` order by id DESC LIMIT 5");
		if(!empty($member['headimg'])){
			$member['img'] = $member['headimg'];
		}else{
			$member['img'] = G_UPLOAD_PATH.'/'.$member['img'];
		}
	 //获取用户等级  用户新手  用户小将==
	  $memberdj=$this->db->GetList("select * from `@#_member_group`");
	  $jingyan=$member['jingyan'];
	  if(!empty($memberdj)){
	     foreach($memberdj as $key=>$val){
		    if($jingyan>=$val['jingyan_start'] && $jingyan<=$val['jingyan_end']){
		    		   $member['yungoudj']=$val['name'];

			}
		}
	}
	$uid=_getcookie('uid');
	require_once("system/modules/mobile/jssdk.php");

         $wechat= $this->db->GetOne("select * from `@#_wechat_config` where id = 1");

        $jssdk = new JSSDK($wechat['appid'],$wechat['appsecret']);

        //$signPackage = $jssdk->GetSignPackage();

    //increse the page counter    
    $cache = System::load_sys_class('mcache');
    $cache->increment("mobile_home_init");    
	include templates("mobile/user","index");
}

public function invite(){

        $webname=$this->_cfg['web_name'];

        $member=$this->userinfo;

        $title="我的用户中心";

        $uid=_getcookie('uid');

        //$quanzi=$this->db->GetList("select * from `@#_quanzi_tiezi` order by id DESC LIMIT 5");

        //获取夺宝等级  夺宝新手  夺宝小将==

        $memberdj=$this->db->GetList("select * from `@#_member_group`");

        $wechat= $this->db->GetOne("select * from `@#_wechat_config` where id = 1");

        $jingyan=$member['jingyan'];

        if(!empty($memberdj)){

            foreach($memberdj as $key=>$val){

                if($jingyan>=$val['jingyan_start'] && $jingyan<=$val['jingyan_end']){

                    $member['yungoudj']=$val['name'];

                }

            }

        }

        require_once("system/modules/mobile/jssdk.php");

         $wechat= $this->db->GetOne("select * from `@#_wechat_config` where id = 1");

        $jssdk = new JSSDK($wechat['appid'],$wechat['appsecret']);

        $signPackage = $jssdk->GetSignPackage();
        include templates("mobile/user","invite");

    }	


	public function inviteshare(){

		$member=$this->userinfo;

		require_once("system/modules/mobile/jssdk.php");

		 $wechat= $this->db->GetOne("select * from `@#_wechat_config` where id = 1");

		$jssdk = new JSSDK($wechat['appid'],$wechat['appsecret']);

		$signPackage = $jssdk->GetSignPackage();

		include templates("mobile/user","inviteshare");

	}

	public function shareinc(){

		$uid = empty($_POST['f']) ? 0 : $_POST['f'];

		$share=$this->db->GetList("select * from `@#_wxch_cfg`");

		if($uid<1){

			echo 5;die;

		}

		if(!$share[11]['cfg_value']){

			echo 1;die;

		}else{

			$info = $this->db->GetOne("SELECT * FROM `@#_share` WHERE `uid` ='$uid' LIMIT 1");

			if(empty($info)){

				$mon = empty($share[12]['cfg_value']) ? 0 : $share[12]['cfg_value'];

				$time = time();

				$q1 = $this->db->Query("UPDATE `@#_member` SET  `money` =`money`+$mon WHERE `uid` = $uid");

				$q2 = $this->db->Query("INSERT INTO `@#_share` SET  `time` ='$time' , `uid` ='$uid'");

				if($q1>0 && $q2>0){

					echo 2;die;

				}else{

					echo 3;die;

				}

			}else{

				echo 4;die;

			}
		}

	}

	//夺宝记录
	public function userbuylist(){
	   $webname=$this->_cfg['web_name'];
		$mysql_model=System::load_sys_class('model');
		$member=$this->userinfo;
		$uid = $member['uid'];
		$title="夺宝记录";					
		//$record=$mysql_model->GetList("select * from `@#_member_go_record` where `uid`='$uid' ORDER BY `time` DESC");
		$user_dizhi = $mysql_model->GetOne("select * from `@#_member_dizhi` where `uid` = '$uid'");
		include templates("mobile/user","userbuylist");
	}

	//夺宝记录详细

	public function userbuydetail(){

	    $webname=$this->_cfg['web_name'];

		$mysql_model=System::load_sys_class('model');

		$member=$this->userinfo;

		$title="夺宝详情";

		$crodid=intval($this->segment(4));

		$record=$mysql_model->GetOne("select * from `@#_member_go_record` where `id`='$crodid' and `uid`='$member[uid]' LIMIT 1");		

		if($crodid>0){

			include templates("member","userbuydetail");

		}else{

			echo _messagemobile("页面错误",WEB_PATH."/member/home/userbuylist",3);

		}

	}

	//获得的商品

	public function orderlist(){

	    $webname=$this->_cfg['web_name'];

		$member=$this->userinfo;

		$title="获得的商品";

		//$record=$this->db->GetList("select * from `@#_member_go_record` where `uid`='".$member['uid']."' and `huode`>'10000000' ORDER BY id DESC");				

		include templates("mobile/user","orderlist");

	}

	//账户管理

	public function userbalance(){

	    $webname=$this->_cfg['web_name'];

		$member=$this->userinfo;

		$title="账户记录";

		$account=$this->db->GetList("select * from `@#_member_account` where `uid`='$member[uid]' and `pay` = '账户' ORDER BY time DESC");

         		$czsum=0;

         		$xfsum=0;

		if(!empty($account)){ 

			foreach($account as $key=>$val){

			  if($val['type']==1){

				$czsum+=$val['money'];		  

			  }else{

				$xfsum+=$val['money'];		  

			  }		

			} 		

		}

		include templates("mobile/user","userbalance");

	}

	public function cardexchangetasks() {
		$webname=$this->_cfg['web_name'];
		$member=$this->userinfo;
		$title="电话卡兑现记录";
		$uid = $member['uid'];
		$mobile = $member['mobile'];
		$day = date('Ymd', time());
		$token = md5($mobile.$day.'jishoubao');

		$newtasks = $this->db->GetOne("select count(*) as count from `@#_goods_card_task` where uid = $uid and (status = 0  or status = 1)");
		$newtaskcount =  $newtasks['count'];

		$donetasks = $this->db->GetOne("select count(*) as count from `@#_goods_card_task` where uid = $uid and status = 2");
		$donetaskcount = $donetasks['count'];

		$newtasks = $this->db->GetOne("select sum(amount) as summ from `@#_goods_card_task` where uid = $uid and (status = 0 or status = 1)"); 
		$newtaskamount = $newtasks['summ'];

		$donetasks = $this->db->GetOne("select sum(amount) as summ from `@#_goods_card_task` where uid = $uid and status = 2"); 
		$donetaskamount = $donetasks['summ'];

		include templates("mobile/user", "usertasks");		
	}

	public function getnewtasks() {
		$member=$this->userinfo;
		$uid = $member['uid'];

		$tasklist = $this->db->GetList("select * from `@#_goods_card_task` where uid = $uid and (status = 0 or status = 1 or status = 3) order by id desc");
		foreach ($tasklist as $key => $value) {
			$value['crt_time'] = date('Y-m-d H:i:s', $value['create_time']);
			$status = "未处理";
			if ($value['status'] == 0) {
				$status = "未处理";	
			} else if ($value['status'] == 1) {
				$status = "处理中";	
			} else if ($value['status'] == 3) {
				$status = "处理失败";	
			}
			$value['status'] =  $status;
			$tasklist[$key] = $value;
		}
		echo json_encode($tasklist);
		die();		
	}

	public function getdonetasks() {
		$member=$this->userinfo;
		$uid = $member['uid'];

		$tasklist = $this->db->GetList("select * from `@#_goods_card_task` where uid = $uid and status = 2 order by id desc");
		foreach ($tasklist as $key => $value) {
			$value['crt_time'] = date('Y-m-d H:i:s', $value['create_time']);
			$value['fin_time'] = date('Y-m-d H:i:s', $value['finish_time']);
			$value['status'] =  $value['status'] == 0 ? '未处理' : '处理中';
			$tasklist[$key] = $value;
		}
		echo json_encode($tasklist);
		die();				
	}

	public function usercards() {
	    $webname=$this->_cfg['web_name'];
		$member=$this->userinfo;
		$title="电话卡兑换记录";
		$uid = $member['uid'];
		$mobile = $member['mobile'];

		$newcards = $this->db->GetOne("select count(*) as count from `@#_goods_card_item` where uid = $uid and status = 0");
		$newcardcount =  $newcards['count'];

		$usedcards = $this->db->GetOne("select count(*) as count from `@#_goods_card_item` where uid = $uid and status = 1");
		$usedcardcount = $usedcards['count'];

		$newcards = $this->db->GetOne("select sum(amount) as summ from `@#_goods_card_item` where uid = $uid and status = 0"); 
		$newcardsamount = $newcards['summ'];

		$usedcards = $this->db->GetOne("select sum(amount) as summ from `@#_goods_card_item` where uid = $uid and status = 1"); 
		$usedcardsamount = $usedcards['summ'];

		include templates("mobile/user", "usercards");
	}

	public function getnewcards() {
		$member=$this->userinfo;
		$uid = $member['uid'];

		$cardlist = $this->db->GetList("select * from `@#_goods_card_item` where uid = $uid and status = 0 order by id desc");
		foreach ($cardlist as $key => $value) {
			$value['crt_time'] = date('Y-m-d H:i:s', $value['create_time']);
			$value['amount'] = $value['amount'] / 1.2;			  
			$cardlist[$key] = $value;

		//	echo $value['crt_time'];
		}
		echo json_encode($cardlist);
		die();
	}

	public function getusedcards() {
		$member=$this->userinfo;
		$uid = $member['uid'];

		$cardlist = $this->db->GetList("select * from `@#_goods_card_item` where uid = $uid and status = 1 order by id desc");
		foreach ($cardlist as $key => $value) {
			$value['amount'] = $value['amount'] / 1.2;
			$value['crt_time'] = date('Y-m-d H:i:s', $value['create_time']);
			$value['valid_time'] = date('Y-m-d H:i:s', $value['validate_time']);
			$cardlist[$key] = $value;
		}
		echo json_encode($cardlist);
		die();
	}


	public function userrecharge(){
		// if ($_REQUEST['clearcart'] == '1') {
		// 	$_COOKIE['Cartlist'] = '';
		// 	_setcookie('Cartlist', "", 0, "/");
		// 	generate_log_product("now cookie is:".$_COOKIE['Cartlist']);			
		// }

	    $webname=$this->_cfg['web_name'];

		$member=$this->userinfo;

		$title="账户充值";

		$paylist = $this->db->GetList("SELECT * FROM `@#_pay` where `pay_start` = '1' AND pay_mobile = 1");
		generate_log_product("in userrecharge, get cookie:"._getcookie("Cartlist"));
		

		include templates("mobile/user","recharge");

	}

	public function userqiandao(){

		$webname=$this->_cfg['web_name'];

		$member=$this->userinfo;

		$uid = $member['uid'];

		$qiandao = $this->db->GetOne("SELECT * FROM `@#_qiandao` where  `uid` = $uid");

		include templates("mobile/user","userqiandao");

	}



	public function qiandao(){

		$member=$this->userinfo;

		$uid = $member['uid'];

		$t = time();

		$start = mktime(0,0,0,date("m",$t),date("d",$t),date("Y",$t));

		$end = mktime(23,59,59,date("m",$t),date("d",$t),date("Y",$t));

		//查询上次签到时间信息

		$qiandao = $this->db->GetOne("SELECT * FROM `@#_qiandao` where  `uid` = $uid");

		if(empty($qiandao)){

			$this->db->Query("INSERT INTO `@#_qiandao` SET `time` = $t, `uid` = $uid,`sum` = 1, `lianxu` = 1");

			//签到送100福分，同时送1元钱

			$this->db->Query("UPDATE `@#_member` SET `score` = `score`+100, `money` =`money`+0 WHERE `uid` = $uid");

			_messagemobile("签到成功，初次签到，系统会赠送您100福分！同时积分还可以兑换现金哦",WEB_PATH."/mobile/home/userqiandao");

		}

		if($qiandao['time']>0){

			if($qiandao['time']>$start && $qiandao['time']<$end){

				_messagemobile("今天已经签到过了",WEB_PATH."/mobile/home/userqiandao");

			}else{

				$this->db->Query("UPDATE `@#_qiandao` SET `time` = $t, `uid` =$uid, `sum` =`sum`+1  where uid=$uid");

				$this->db->Query("UPDATE `@#_member` SET `score` = `score`+100 WHERE `uid` = $uid");

				//判断是否连续

				if($t - $qiandao['time']>2 && $t - $qiandao['time']<172798 &&  $qiandao['time']>($start-86400)){

					$this->db->Query("UPDATE `@#_qiandao` SET `lianxu`  =`lianxu` +1 where `uid` = $uid");

				}else{

					$this->db->Query("UPDATE `@#_qiandao` SET `lianxu` = 1 where `uid`= $uid");

				}
				_messagemobile("签到成功，坚持签到有积分赠送的哦！同时积分还可以兑换现金哦",WEB_PATH."/mobile/home/userqiandao");
			}
		}else{
			_messagemobile("签到错误",WEB_PATH."/mobile/home/userqiandao");
		}
	}

	public function useraddress(){
		$webname=$this->_cfg['web_name'];
		$member=$this->userinfo;
		$uid = $member['uid'];
		$t = time();
		if($_POST['submit']){
			extract($_POST);
			if(empty($sheng) || empty($shi) || empty($xian)){
				_messagemobile("地市信息不能为空",WEB_PATH."/mobile/home/address");
			}
			$jiedao1 = preg_replace( "@<script(.*?)</script>@is", "", $jiedao );
			$jiedao = $jiedao1;
			if(empty($jiedao)){
				_messagemobile("街道地址包含特殊字符",WEB_PATH."/mobile/home/address");
			}
			if(empty($qq) || empty($youbian) || empty($shouhuoren) || empty($mobile)){
				_messagemobile("qq 或者 邮编 收货人 电话 不能为空",WEB_PATH."/mobile/home/address");
			}
			$q1 = $this->db->Query("INSERT INTO `@#_member_dizhi` SET `time` = $t, `uid` = $uid, `sheng` = '$sheng', `shi` = '$shi', `xian` = '$xian', `jiedao` = '$jiedao',`youbian` = $youbian, `shouhuoren`= '$shouhuoren', `mobile`= '$mobile', `qq`= '$qq', `shifoufahuo` = $shifoufahuo");
			if($q1){
				_messagemobile("地址添加成功",WEB_PATH."/mobile/home/address");
			}else{
				_messagemobile("地址添加失败",WEB_PATH."/mobile/home/address");
			}
		}else{
			_messagemobile("添加失败",WEB_PATH."/mobile/home/address");
		}
	}



	public function address(){
		$webname=$this->_cfg['web_name'];
		$member=$this->userinfo;
		$uid = $member['uid'];
		$dizhi = $this->db->GetList("SELECT * FROM `@#_member_dizhi` where  `uid` = $uid");
		include templates("mobile/user","address");

	}
	public function updateddress(){
		$id=intval($this->segment(4));

		$t = time();

		if($id){

			extract($_POST);

			if(empty($sheng) || empty($shi) || empty($xian)){

				_messagemobile("地市信息不能为空",WEB_PATH."/mobile/home/address");

			}

			$jiedao1 = preg_replace( "@<script(.*?)</script>@is", "", $jiedao );
			$jiedao = $jiedao1;
			if(empty($jiedao)){
				_messagemobile("街道地址包含特殊字符",WEB_PATH."/mobile/home/address");
			}

			if(empty($qq) || empty($youbian) || empty($shouhuoren) || empty($mobile)){

				_messagemobile("qq 或者 邮编 收货人 电话 不能为空",WEB_PATH."/mobile/home/address");
			}

			$q1 = $this->db->Query("UPDATE `@#_member_dizhi` SET `time` = $t, `sheng` = '$sheng', `shi` = '$shi', `xian` = '$xian', `jiedao` = '$jiedao',`youbian` = $youbian, `shouhuoren`= '$shouhuoren', `mobile`= '$mobile', `qq`= '$qq', `shifoufahuo` = $shifoufahuo WHERE `id`= $id");

			if($q1){

				_messagemobile("地址修改成功",WEB_PATH."/mobile/home/address");

			}else{

				_messagemobile("地址修改失败",WEB_PATH."/mobile/home/address");

			}

			



		}else{

			_messagemobile("修改失败",WEB_PATH."/mobile/home/address");

		}

	}



	public function deladdress(){

		$id=intval($this->segment(4));

		if($id){

			$q1 = $this->db->Query("DELETE FROM `@#_member_dizhi`  WHERE `id`= $id");

			if($q1){

				_messagemobile("删除成功",WEB_PATH."/mobile/home/address");

			}else{

				_messagemobile("删除失败",WEB_PATH."/mobile/home/address");

			}

		}else{

			_messagemobile("删除失败",WEB_PATH."/mobile/home/address");

		}

	}



	//设为默认
	public function setaddress(){

		$id=intval($this->segment(4));

		if($id){

			$q1 = $this->db->Query("UPDATE `@#_member_dizhi` SET `default` = 'Y' WHERE `id`= $id");

			$q2 = $this->db->Query("UPDATE `@#_member_dizhi` SET `default` = 'N' WHERE `id` != $id");

			if($q1 && $q2){

				_messagemobile("设置成功",WEB_PATH."/mobile/home/address");

			}else{

				_messagemobile("设置失败",WEB_PATH."/mobile/home/address");

			}

		}else{
			_messagemobile("设置失败",WEB_PATH."/mobile/home/address");
		}
	}

	private function getbalance($server, $openid){
		$url = $server."/yun/wx/querybalance/".$openid;
		$ch2 = curl_init($url);
		curl_setopt($ch2, CURLOPT_RETURNTRANSFER, 1);
		$html = curl_exec($ch2);
		$result = json_decode($html);
		return  $result->balance;
	}

	private function updategamebalancesource($server, $openid, $source, $amount) {
		$url = $server."/plaza/wx/moneyout/".$openid."/".$source."?amount=".$amount;
		$ch2 = curl_init($url);
		curl_setopt($ch2, CURLOPT_RETURNTRANSFER, 1);
		$html = curl_exec($ch2);
		$result = json_decode($html);
		return  $result;
	}

	private function updatetradebalance($server, $openid, $amount) {
		$url = $server."/yun/wx/moneyout/".$openid."?amount=".$amount;
		$ch2 = curl_init($url);
		curl_setopt($ch2, CURLOPT_RETURNTRANSFER, 1);
		$html = curl_exec($ch2);
		$result = json_decode($html);
		return  $result;
	}

	//plaza is the context path for game applications
	private function getGameBalance($server, $openid) {
		$url = $server."/plaza/wx/querybalance/".$openid;
		$ch2 = curl_init($url);
		curl_setopt($ch2, CURLOPT_RETURNTRANSFER, 1);
		$html = curl_exec($ch2);
		$result = json_decode($html);
		return  $result->balance;	
	}

	private function getGameBalanceSource($server, $openid, $source) {
		$url = $server."/plaza/wx/querybalance/".$openid."/".$source;
		$ch2 = curl_init($url);
		curl_setopt($ch2, CURLOPT_RETURNTRANSFER, 1);
		$html = curl_exec($ch2);
		$result = json_decode($html);
		return  $result->balance;	
	}

	private function updategamebalance($server, $openid, $amount) {
		$url = $server."/plaza/wx/moneyout/".$openid."?amount=".$amount;
		$ch2 = curl_init($url);
		curl_setopt($ch2, CURLOPT_RETURNTRANSFER, 1);
		$html = curl_exec($ch2);
		$result = json_decode($html);
		return  $result;
	}	

	public function generateCardInfo() {
		$timestr = microtime(true)*10000;
		$cardno = $timestr.rand(10000, 99999);		
		$cardpass = md5($cardno."lxtech".rand(10,99));
		return array("no" => $cardno, "pass" => $cardpass);
	}

	public function generateTransferDesc($amount) {
		if ($amount< 10) {
			return "请输入大于10的数";
		}

		$amount = 10 *intval($amount / 10);

		$desc = "可兑换";
		$thousands = intval($amount/1000);
		if ($thousands > 0) {
			//$this->createCards($uid, 1000, $thousands, $cardarr['1000']);
			$desc = $desc."1000元电话卡".$thousands."张;";
		} 
		$hundreds = $amount%1000;
		if ($hundreds > 500) {
			//$this->createCards($uid, 500, 1, $cardarr['500']);
			$desc = $desc."500元电话卡一张;";
			$hundreds -= 500;
		}

		$onehundreds = intval($hundreds/100);
		if ($onehundreds > 0) {
			$desc = $desc."100元电话卡".$onehundreds."张;";
		}

		$fiftys = $hundreds%100;
		if ($fiftys >= 50) {
		//	$this->createCards($uid, 50, 1, $cardarr['50']);
			$desc = $desc."50元电话卡一张;";
			$tens = intval(($fiftys - 50)/10);
			if ($tens > 0) {
				$desc = $desc."10元电话卡".$tens."张;";
		//		$this->createCards($uid, 10, $tens, $cardarr['10']);	
			}
		} else {
			$tens = intval($fiftys/10);
			if($tens > 0) {
				$desc = $desc."10元电话卡".$tens."张;";	
			}
			//$this->createCards($uid, 10, $tens, $cardarr['10']);
		}	
		$netvalue = $amount*0.97;
		return $desc."通过寄售宝可兑换".$netvalue."元并转入支付宝。";	
	}

	public function getexchangedesc() {
		$amount = intval($_REQUEST['amount']);
		$resArr = array("desc" => $this->generateTransferDesc($amount));
		echo json_encode($resArr);
	}

	//从游戏帐户把钱转到云购帐户
	public function transferfromgame() {
		generate_log_weixin("123");
		$uid = $this->userinfo['uid'];	
		$uinfo = $this->db->GetOne("select `money`, `mobile` from `@#_member` where uid = $uid");

		if(!$uinfo['mobile']) {
			header("Location:".WEB_PATH."/mobile/user/mobile?backurl=/mobile/home/transferfromgame");exit;	
		}

		$balance = $uinfo['money'];

		//echo $balance;
		$gamebalance = 0;
		$transferamount = 0;
		$reg_key = $this->userinfo['reg_key'];
		$t = time();
		$serverAddr = "";
		$user_code = "";
		//the source site from which we will reduce the balance
		//such as redpacket
		$sourcesite = $_REQUEST['source'];
		//echo $sourcesite;
		generate_log_weixin("in transferfromgame".$sourcesite);

		if (strlen($reg_key) == 4) {
			$channel = intval($reg_key);
			//if ($channel >= 3000 && $channel < 4000) {
				//3000 is for all the channels between 3000[included] and 4000[not included], these players are from the game plaza
				$serverIntegration = $this->db->GetOne("select * from `@#_cloudtrade_integration` where `chnno` = '3000'");
				$bindInfo = $this->db->GetOne("SELECT * from `@#_member_band` where `b_uid` = $uid");

				if ($serverIntegration && $bindInfo) {
					$serverAddr = $serverIntegration['server_addr'];
					$user_code = $bindInfo['b_code'];
					if ($sourcesite) {
					//	$gamebalance = 0;
						$gamebalance = $this->getGameBalanceSource($serverAddr, $user_code, $sourcesite);	
					} else {
						$gamebalance = $this->getGameBalance($serverAddr, $user_code);	
					}
					
					$transferamount = 10 * (intval($gamebalance/10));
					$transferDesc = $this->generateTransferDesc($transferamount);
				}
			//}
		}

		$mobile = $this->userinfo['mobile'];
		$day = date('Ymd', time());
		$token = md5($mobile.$day.'jishoubao');

		if ($_POST['submit1']) {
			$money = $_POST['money'];

			if ($money > ($transferamount)) {
				//$total = intval($availableBalance/$price);
				_messagemobile("最多只能兑换".$transferamount."游戏币",WEB_PATH."/mobile/home/transferfromgame");
			} else {
				//convert money into cards
				$amountForGame = ($money > $transferamount) ? $transferamount : $money;
				//减少游戏币
				if ($sourcesite) {
					$result = $this->updategamebalancesource($serverAddr, $user_code, $sourcesite, doubleval($amountForGame));	
					//$result = $this->updategamebalance($serverAddr, $user_code, doubleval($amountForGame));	
				} else {
					$result = $this->updategamebalance($serverAddr, $user_code, doubleval($amountForGame));	
				}

				generate_log_weixin("update game balance.".$result->code);
				$transferredDone = true;
				if ($result->code == 0) {
					$this->db->Autocommit_start();
					$up_q1= $this->db->Query("UPDATE `@#_member` SET `money` = `money` + $amountForGame where  `uid` = $uid");
					generate_log_weixin("UPDATE `@#_member` SET `money` = `money` + $amountForGame where  `uid` = $uid");
					$up_q3= $this->db->Query("INSERT INTO `@#_member_account`  SET `uid`= $uid, `type` = 1, `pay`= '兑换', `content`= '从游戏帐户扣除{$amountForGame}游戏币转成云购币', `money` = $amountForGame ,`time` = $t");
					if($up_q1 && $up_q3){
						$this->db->Autocommit_commit();
					} else {
						$transferredDone = false;
						//todo 
						//if this operation failed, the money should be recharged
					}
					
					if ($transferredDone) {
						//$coins = $amountForGame*10000;
						//_messagemobile("成功将".$coins."枚游戏币转云购币",WEB_PATH."/mobile/home/convertgamebalance");	
						$this->db->Query("UPDATE `@#_member` SET `money` = `money` - $amountForGame where  `uid` = $uid");
						$this->convertBalanceToCards($amountForGame, $uid);
						if ($this->createAliTask($uid, $mobile)) {
							$day = date('Ymd', time());
							$token = md5($mobile.$day.'jishoubao');
							//echo "http://jishou.sino518.com/sale/exchange/apply?mobile=$mobile&day=$day&token=$token";
							ob_start();
							header("Location:http://jishou.sino518.com/sale/exchange/apply?mobile=$mobile&day=$day&token=$token&source=$sourcesite");
							exit;	
						} else {
							_messagemobile("操作失败",WEB_PATH."/mobile/home/transferfromgame");			
						}
					} else {
						_messagemobile("转帐失败",WEB_PATH."/mobile/home/transferfromgame");		
					}				
				} else {
					//failed 
					_messagemobile("从游戏大厅入账失败",WEB_PATH."/mobile/home/transferfromgame");	
				}

			}	
		}

		include templates("mobile/user","transferfromgame");
	}

	/*public function navigateToSale() {
		$mobile = $this->userinfo['mobile'];

		if ($mobile) {
			$day = date('Ymd', time());
			$token = md5($mobile.$day.'jishoubao');
			header("http://jishou.sino518.com/sale/exchange/apply?mobile=$mobile&day=$day&token=$token");
			exit;
		} else {
			header("http://jishou.sino518.com/sale/exchange/default");
			exit;
		}
	}*/

	public function createCards($uid, $price, $count, $type) {
		generate_log_weixin("in createCards".$price."  ".$count."  ".$type);
		for ($i = 0; $i < $count; $i++) {
			$card = $this->generateCardInfo();
			$cardno = $card['no'];
			$cardpass = $card['pass'];
			$crt_time = time();
			$sql = "INSERT INTO `@#_goods_card_item` SET `uid` = $uid, `cid` = $type, `amount` = $price, `account` = '$cardno', `password` = '$cardpass', `status` = 0, `create_time` = $crt_time ";						
			generate_log_weixin($sql);			
			$this->db->Query($sql);
		}
	}

	private function convertBalanceToCards($amount, $uid) {
		$cardinfo = $this->db->GetList("select * from `@#_goods_card`");
		$cardarr = array();
		foreach ($cardinfo as $key => $value) {
			$price = intval($value['price']);
			$cardarr[$price.""] = $value['id'];
		}

		$thousands = intval($amount/1000);
		if ($thousands > 0) {
			$this->createCards($uid, 1000, $thousands, $cardarr['1000']);
		} 
		$hundreds = $amount%1000;
		if ($hundreds > 500) {
			$this->createCards($uid, 500, 1, $cardarr['500']);
			$hundreds -= 500;
		}

		$onehundreds = intval($hundreds/100);
		$this->createCards($uid, 100, $onehundreds, $cardarr['100']);
		$fiftys = $hundreds%100;
		if ($fiftys >= 50) {
			$this->createCards($uid, 50, 1, $cardarr['50']);
			$tens = intval(($fiftys - 50)/10);
			if ($tens > 0) {
				$this->createCards($uid, 10, $tens, $cardarr['10']);	
			}
		} else {
			$tens = intval($fiftys/10);
			$this->createCards($uid, 10, $tens, $cardarr['10']);
		}
	}

	private function createAliTask($uid, $mobile) {
		$newcards = $this->db->GetOne("select sum(amount) as amt from `@#_goods_card_item` where `uid` = $uid and status = 0");
		$totalvalue = $newcards['amt'];
		$realvalue = (int)($newcards['amt']);
		$netvalue = (int)($newcards['amt']*0.97);		
		
		//directly create the task for jishoubao 
		$mobile = $this->userinfo['mobile'];
		$newcards = $this->db->GetOne("select sum(amount) as amt from `@#_goods_card_item` where `uid` = $uid and status = 0");
		$totalvalue = $newcards['amt'];
		$netvalue = (double)($newcards['amt'] * 0.97);		

		$tasktype = 'alipay';//$_REQUEST['tasktype'];
		$this->db->Query("update `@#_goods_card_item` set status = 1 where uid = $uid");

		//$createtime = intval(date('YmdHis', time()));
		if ($netvalue > 0) {
			$createtime  = time();
			$chnno = $this->userinfo['reg_key'];
			if (intval($chnno) > 0) {
				$update_res = $this->db->Query("INSERT INTO `@#_goods_card_task` SET `uid` = $uid, `amount` = $netvalue, create_time = $createtime, status = 0, mobile=$mobile, `type` = '$tasktype', `chnno` = '$chnno' ");
				generate_log_weixin("INSERT INTO `@#_goods_card_task` SET `uid` = $uid, `amount` = $netvalue, create_time = $createtime, status = 0, mobile=$mobile, `type` = '$tasktype' ");
				//echo $update_res;
				if ($update_res) {
					//echo "1";
					return true;
				} else {
					return false;
				}							
			} else {
				return false;
			}
		} else {
			return false;
		}		
	}


	//convert balance to mobile recharge cards
	public function convertgamebalance() {
		$uid = $this->userinfo['uid'];
		$uinfo = $this->db->GetOne("select `money` from `@#_member` where uid = $uid");
		$balance = $uinfo['money'];
		$suggested = 10 * (int)(intval($balance)/10);

		$t = time();	

		$cardlist = $this->db->GetList("select * from `@#_goods_card`");

		if ($_POST['submit1']) {
			$amount = intval($_POST['amount']);
			if ($amount > $balance) {
				_messagemobile("输入的云购币数量不能超过帐户余额",WEB_PATH."/mobile/home/convertgamebalance");
			}
			//$number = intval($_POST['cardcount']);
			if ($amount % 10 > 0) {
				_messagemobile("输入的云购币数量应该是10的整数倍",WEB_PATH."/mobile/home/convertgamebalance");
			}

			if ($amount <= 0) {
				_messagemobile("云购币数量至少为10枚",WEB_PATH."/mobile/home/convertgamebalance");	
			}

			$this->db->Autocommit_start();
			$up_q1= $this->db->Query("UPDATE `@#_member` SET `money` = `money` - $amount where  `uid` = $uid");
			$up_q3= $this->db->Query("INSERT INTO `@#_member_account`  SET `uid`= $uid, `type` = -1, `pay`= '兑换', `content`= '从夺宝帐户扣除{$amount}夺宝币兑换成电话卡', `money` = $amount,`time` = $t");
			if($up_q1 && $up_q3){
				$this->db->Autocommit_commit();
			} else {
				_messagemobile("电话卡兑换失败",WEB_PATH."/mobile/home/convertgamebalance");	
			}

			$cardinfo = $this->db->GetList("select * from `@#_goods_card`");
			$cardarr = array();
			foreach ($cardinfo as $key => $value) {
				$price = intval($value['price']);
				$cardarr[$price.""] = $value['id'];
			}

			$thousands = intval($amount/1000);
			if ($thousands > 0) {
				$this->createCards($uid, 1000, $thousands, $cardarr['1000']);
			} 
			$hundreds = $amount%1000;
			if ($hundreds > 500) {
				$this->createCards($uid, 500, 1, $cardarr['500']);
				$hundreds -= 500;
			}

			$onehundreds = intval($hundreds/100);
			$this->createCards($uid, 100, $onehundreds, $cardarr['100']);
			$fiftys = $hundreds%100;
			if ($fiftys >= 50) {
				$this->createCards($uid, 50, 1, $cardarr['50']);
				$tens = intval(($fiftys - 50)/10);
				if ($tens > 0) {
					$this->createCards($uid, 10, $tens, $cardarr['10']);	
				}
			} else {
				$tens = intval($fiftys/10);
				$this->createCards($uid, 10, $tens, $cardarr['10']);
			}

			//_messagemobile("电话卡兑换成功。如果您未绑定手机号，则会跳转到手机号绑定界面。",WEB_PATH."/mobile/home/exchangeconfirm?total=".$amount);	
			_messagemobile("电话卡兑换成功。",WEB_PATH."/mobile/home/exchangeconfirm?total=".$amount);	
		} 	

		include templates("mobile/user","zhuanzhanggame");
	}

	/*public function createrechargetask() {
		$member=$this->userinfo;
		$uid = $member['uid'];
		$mobile = $member['mobile'];
		$newcards = $this->db->GetOne("select sum(amount) as amt from `@#_goods_card_item` where `uid` = $uid and status = 0");
		$totalvalue = $newcards['amt'];
		$netvalue = (int)($newcards['amt']*0.97);		

		$tasktype = $_REQUEST['tasktype'];
		$this->db->Query("update `@#_goods_card_item` set status = 1 where uid = $uid");

		//$createtime = intval(date('YmdHis', time()));
		if ($netvalue > 0) {
			$createtime  = time();
			$update_res = $this->db->Query("INSERT INTO `@#_goods_card_task` SET `uid` = $uid, `amount` = $netvalue, create_time = $createtime, status = 0, mobile=$mobile, `type` = '$tasktype' ");
			if ($update_res) {
				echo "1";
			} else {
				echo "-1";
			}			
		} else {
			echo "-2";
		}
	}*/

	public function listrechargetask() {
		$member=$this->userinfo;
		$uid = $member['uid'];
		$mobile = $member['mobile'];		
		include templates("mobile/user","listrechargetask");	
	}

	public function getrechargetasks() {
		$member=$this->userinfo;
		$uid = $member['uid'];
		$sql = "select * from `@#_goods_card_task` where uid = $uid order by id desc";
		$tasklist = $this->db->GetList($sql);
		echo json_encode($tasklist);
	}

/*
	public function exchangeconfirm() {
		session_start();
		if (!isset($_SESSION['jumptag'])) {
		  	$_SESSION['jumptag'] = 1;
		} else {
			unset($_SESSION['jumptag']);
			header("Location:".WEB_PATH."/mobile/home/init");exit;
		}

		$webname=$this->_cfg['web_name'];
		$member=$this->userinfo;
		$uid = $member['uid'];
		//echo $uid;
		$newcards = $this->db->GetOne("select sum(amount) as amt from `@#_goods_card_item` where `uid` = $uid and status = 0");
		$totalvalue = $newcards['amt'];
		$realvalue = (int)($newcards['amt']);
		$netvalue = (int)($newcards['amt']*0.97);		
		if (!$member['mobile']) {
			//echo "123";
			//echo WEB_PATH."/mobile/user/mobile?backurl=/mobile/home/exchangeconfirm";
			//exit;
			header("Location:".WEB_PATH."/mobile/user/mobile?backurl=/mobile/home/exchangeconfirm");exit;
		}		

		//directly create the task for jishoubao 
		$mobile = $member['mobile'];
		$newcards = $this->db->GetOne("select sum(amount) as amt from `@#_goods_card_item` where `uid` = $uid and status = 0");
		$totalvalue = $newcards['amt'];
		$netvalue = (int)($newcards['amt'] * 0.97);		

		$tasktype = 'alipay';//$_REQUEST['tasktype'];
		$this->db->Query("update `@#_goods_card_item` set status = 1 where uid = $uid");

		//$createtime = intval(date('YmdHis', time()));
		if ($netvalue > 0) {
			$createtime  = time();
			$update_res = $this->db->Query("INSERT INTO `@#_goods_card_task` SET `uid` = $uid, `amount` = $netvalue, create_time = $createtime, status = 0, mobile=$mobile, `type` = '$tasktype' ");
			//echo $update_res;
			if ($update_res) {
				//echo "1";
				$mobile = $member['mobile'];
				$day = date('Ymd', time());
				$token = md5($mobile.$day.'jishoubao');
				//echo "http://jishou.sino518.com/sale/exchange/apply?mobile=$mobile&day=$day&token=$token";
				ob_start();
				header("Location:http://jishou.sino518.com/sale/exchange/apply?mobile=$mobile&day=$day&token=$token");
				exit;
			} else {
				//echo "1";
			}			
		} else {
			//echo "123";
		}
	}*/

	public function zhuanzhang() {
		$webname=$this->_cfg['web_name'];
		$member=$this->userinfo;
		$uid = $member['uid'];
		$t = time();
		//echo $uid;
		$balance = 0;
		$serverAddr = '';
		$user_code = '';
		//查询用户余额
		$info= $this->db->GetOne("SELECT `money`, `reg_key` FROM `@#_member` where  `uid` = $uid");
		$bindInfo = $this->db->GetOne("SELECT * from `@#_member_band` where `b_uid` = $uid");
		//var_dump($bindInfo);
		if ($bindInfo) {
			//echo "hello";
			$reg_key = $info['reg_key'];
			$user_code = $bindInfo['b_code'];
			//107Q98 and 107Q99 are used for cloud trade
			if ($reg_key && $user_code && (($reg_key == '107Q98') || ($reg_key == '107Q99') )) {
				//echo $reg_key."  ".$user_code;

				$serverIntegration = $this->db->GetOne("select * from `@#_cloudtrade_integration` where `chnno` = '$reg_key'");
				//var_dump($serverIntegration);
				if ($serverIntegration) {
					$serverAddr = $serverIntegration['server_addr'];
					$balance = $this->getbalance($serverAddr, $user_code);
					//echo $balance;
				}
			}
		}

		if($_POST['submit1']){
			if($balance < $_POST['money']){
				_messagemobile("转账金额超过微盘账户余额了！",WEB_PATH."/mobile/home/zhuanzhang");
			}
			if(empty($_POST['money']) || $_POST['money']<1){
				_messagemobile("请输入转账金额，且不能小于1元",WEB_PATH."/mobile/home/zhuanzhang");
			}
			
			// 查询数据库中用户信息
			$cash = $_POST['money'];

			//firstly, update the account in cloud trade system
			$result = $this->updatetradebalance($serverAddr, $user_code, doubleval($cash));
			if ($result->code == 0) {
				$this->db->Autocommit_start();
				$up_q1= $this->db->Query("UPDATE `@#_member` SET `money` = `money`+ {$_POST['money']}  where  `uid` = $uid");
				$up_q3= $this->db->Query("INSERT INTO `@#_member_account`  SET `uid`= $uid, `type` = 1, `pay`= '账户', `content`= '从微盘资金账户转账{$cash}元进入云购帐户', `money` = $cash ,`time` = $t");
				if($up_q1 && $up_q3){
					$this->db->Autocommit_commit();
					_messagemobile("转账成功",WEB_PATH."/mobile/home/zhuanzhang");
				}else{
					$this->db->Autocommit_rollback();
					$this->error = true;
					_messagemobile("转账失败",WEB_PATH."/mobile/home/zhuanzhang");
				}				
			}
		}
		include templates("mobile/user","zhuanzhang");
	}

	public function zhuanzhang_old(){
		$webname=$this->_cfg['web_name'];
		$member=$this->userinfo;
		$uid = $member['uid'];
		$t = time();
		//查询用户余额
		$info= $this->db->GetOne("SELECT `money` FROM `@#_member` where  `uid` = $uid");
		if($_POST['submit1']){
			if($_POST['txtBankName'] != $_POST['txtBankName1']){
				_messagemobile("两次的用户信息不一致，请重新输入！",WEB_PATH."/mobile/home/zhuanzhang");
			}
			if($info['money']< $_POST['money']){
				_messagemobile("账户余额超过转账金额了！",WEB_PATH."/mobile/home/zhuanzhang");
			}
			if(empty($_POST['money']) || $_POST['money']<1){
				_messagemobile("请输入转账金额，且不能小于1元",WEB_PATH."/mobile/home/zhuanzhang");
			}
			// 查询数据库中用户信息
			$to_user = $_POST['txtBankName'];
			$to_info= $this->db->GetOne("SELECT * FROM `@#_member` where  `mobile` = '{$to_user}' OR `email` = '{$to_user}'");
			$cash = $_POST['money'];
			if(empty($to_info)){
				_messagemobile("用户不存在！请核对后在操作",WEB_PATH."/mobile/home/zhuanzhang");
			}
			$this->db->Autocommit_start();
				$up_q1= $this->db->Query("UPDATE `@#_member` SET `money` = `money`- {$_POST['money']}  where  `uid` = $uid");
				$up_q2= $this->db->Query("UPDATE `@#_member` SET `money` = `money`+{$_POST['money']}  where  `uid` = {$to_info['uid']}");
				$up_q3= $this->db->Query("INSERT INTO `@#_member_account`  SET `uid`= $uid, `type` = -1, `pay`= '账户', `content`= '给用户{$to_info['mobile']}转账{$cash}元', `money` = $cash ,`time` = $t");
				$up_q4= $this->db->Query("INSERT INTO `@#_member_account`  SET `uid`= {$to_info['uid']}, `type` = -1, `pay`= '账户', `content`= '获得用户{$member['mobile']}转账{$cash}元', `money` = $cash ,`time` = $t");
			if($up_q1 && $up_q2 && $up_q3 && $up_q4){
				$this->db->Autocommit_commit();
				_messagemobile("转账成功",WEB_PATH."/mobile/home/zhuanzhang");
			}else{
				$this->db->Autocommit_rollback();
				$this->error = true;
				_messagemobile("转账失败",WEB_PATH."/mobile/home/zhuanzhang");
			}	
		}
		include templates("mobile/user","zhuanzhang_old");
	}
	/**
	 * 转盘抽奖
	 */
	public function choujiang(){
		$webname=$this->_cfg['web_name'];
		$member=$this->userinfo;
		$uid = $member['uid'];
		$name = $member['username'];
		include templates("mobile/user","choujiang");
	}
	public function submit(){
		$webname=$this->_cfg['web_name'];
		$member=$this->userinfo;
		$uid = $member['uid'];
		$row =  $this->db->GetOne("SELECT * FROM `@#_member`  WHERE  `uid` = $uid");
		$score = $row['score'];
		if(intval($score/200)<1){
			$res = array(
				'ok' => true,
				'round'=>0,
				'left' => 0,
				'desc' =>'您的抽奖次数已经使用完！',
			);
			echo json_encode($res);die;	
		}else{
			//扣除积分
			$q1= $this->db->Query("UPDATE `@#_member` SET `score` = `score`- 200  where  `uid` = $uid");
			$lefts = $score - 200;
			if($q1){
				$left = intval($score/200)-1;
				$res = array(
					'ok' => true,
					'round'=>0,
					'left' => $left,
					'desc' =>'真遗憾，您没有中奖哦！剩余福分'.$lefts,
				);
			echo json_encode($res);die;	
			}else{
				$left = intval($score/200);
				$res = array(
					'ok' => true,
					'round'=>0,
					'left' => $left,
					'desc' =>'抽奖出错！请联系管理员',
			);
			echo json_encode($res);die;
			}
		}
	}
	/**
	 * 摇一摇红包
	 */
	public function yaohongbao(){
		$webname=$this->_cfg['web_name'];
		$member=$this->userinfo;
		$uid = $member['uid'];
		$name = $member['username'];
		include templates("mobile/user","yaohongbao");
	}
	//晒单
	public function singlelist(){
		 $webname=$this->_cfg['web_name'];
		include templates("mobile/user","singlelist");
	}	
	//添加晒单
	public function postsinglebk(){
		$member=$this->userinfo;
		$uid=_getcookie('uid');
		$ushell=_getcookie('ushell');
		$title="添加晒单";		
		if(isset($_POST['submit'])){
			if($_POST['title']==null) _messagemobile("标题不能为空");	
			if($_POST['content']==null) _messagemobile("内容不能为空");	
			if(empty($_POST['file_up'])){
				_messagemobile("图片不能为空");
			}
			$pic=$_POST['file_up'];
			$pics = explode(';',$pic);
			$src=trim($pics[0]);
			$size=getimagesize(G_UPLOAD_PATH."/".$src);
			$width=220;
			$height=$size[1]*($width/$size[0]);
			$thumbs=tubimg($src,$width,$height);				
			$uid=$this->userinfo;
			$sd_userid=$uid['uid'];
			$sd_shopid=$_POST['shopid'];
			$sd_title=$_POST['title'];
			$sd_thumbs="shaidan/".$thumbs;
			$sd_content=$_POST['content'];
			$sd_photolist=$pic;
			$sd_time=time();			
			$sd_ip = _get_ip_dizhi();						
			$qishu= $this->db->GetOne("select `qishu`, `id` from `@#_shoplist` where `id`='$sd_shopid'");
			$qs = $qishu['qishu'];
			$ids = $qishu['id'];
			$this->db->Query("INSERT INTO `@#_shaidan`(`sd_userid`,`sd_shopid`,`sd_qishu`,`sd_ip`,`sd_title`,`sd_thumbs`,`sd_content`,`sd_photolist`,`sd_time`)VALUES ('$sd_userid','$ids','$qs','$sd_ip','$sd_title','$sd_thumbs','$sd_content','$sd_photolist','$sd_time')");
			_messagemobile("晒单分享成功",WEB_PATH."/mobile/home/singlelist");
		}
		$recordid=intval($this->segment(4));
		if($recordid>0){
			$shaidan=$this->db->GetOne("select * from `@#_member_go_record` where `id`='$recordid'");	
			$shopid=$shaidan['id'];
			include templates("mobile/user","postsingle");
		}else{
			_messagemobile("页面错误");
		}
	}

	public function postsingle(){
		$member=$this->userinfo;
		$uid=$member['uid'];
		$title="添加晒单";
		$recordid=intval($this->segment(4));
		$shaidan=$this->db->GetOne("select * from `@#_member_go_record` where `shopid`='$recordid' and `uid` = '$member[uid]'");
		if(!$shaidan){
			_messagemobile("该商品您不可晒单!");
		}
		$ginfo=$this->db->GetOne("select * from `@#_shoplist` where `id`='$shaidan[shopid]' LIMIT 1");
		if(!$ginfo){
			_messagemobile("该商品已不存在!");
		}
		$shaidanyn=$this->db->GetOne("select sd_id from `@#_shaidan` where `sd_shopid`='{$ginfo['id']}' and `sd_userid` = '$member[uid]'");
		if($shaidanyn){
			_messagemobile("不可重复晒单!");
		}
		if($_POST){

			if($_POST['title']==null)_messagemobile("标题不能为空");
			if($_POST['content']==null)_messagemobile("内容不能为空");
			if(!isset($_POST['fileurl_tmp'])){
				_messagemobile("图片不能为空");
			}
			System::load_sys_class('upload','sys','no');
			$img=explode(';', $_POST['fileurl_tmp']);
			$num=count($img);
			$pic="";
			for($i=0;$i<$num;$i++){
				$img[$i] = str_replace('http://', '', $img[$i]);
				$img[$i] = str_replace($_SERVER['HTTP_HOST'], '', $img[$i]);
				$img[$i] = str_replace('/statics/uploads/', '', $img[$i]);
				$pic.=trim($img[$i]).";";
			}

			$src=trim($img[0]);
			$size=getimagesize(G_UPLOAD_PATH."/".$src);
			$width=220;
			$height=$size[1]*($width/$size[0]);
			$thumbs=tubimg($src,$width,$height);
			$sd_userid=$uid;
			$sd_shopid=intval($ginfo['id']);
			$sd_title=safe_replace($_POST['title']);
			$sd_thumbs=$src;
			$sd_content=safe_replace($_POST['content']);
			$sd_photolist=$pic;
			$sd_time=time();
			$this->db->Query("INSERT INTO `@#_shaidan`(`sd_userid`,`sd_shopid`,`sd_title`,`sd_thumbs`,`sd_content`,`sd_photolist`,`sd_time`)VALUES
			('$sd_userid','$sd_shopid','$sd_title','$sd_thumbs','$sd_content','$sd_photolist','$sd_time')");
			header("Location:".WEB_PATH."/mobile/home/singlelist");
		}

		if($recordid>0){
			$shaidan=$this->db->GetOne("select * from `@#_member_go_record` where `id`='$recordid'");
			$shopid=$shaidan['shopid'];
			include templates("mobile/user","postsingle");
		}else{
			_messagemobile("页面错误");
		}
	}
	// 晒单上传图片方法
	public function mupload(){
		$uploadDir =$_SERVER['DOCUMENT_ROOT'].'/statics/uploads/shaidan/'.date('Ymd',time()).'/';
		if(!is_dir($uploadDir)){
		 	mkdir($uploadDir,0777);				
		}
		$rand=rand(10,99).substr(microtime(),2,6).substr(time(),4,6);
		$fileTypes = array('jpg', 'jpeg', 'gif', 'png'); 
		if (!empty($_FILES)) {
			$fileParts = pathinfo($_FILES['Filedata']['name']);
			$filetype = strtolower($fileParts['extension']);
			$tempFile   = $_FILES['Filedata']['tmp_name'];
			$targetFilename = $rand.'.'.$filetype;
			if (in_array($filetype, $fileTypes)) {
				move_uploaded_file($tempFile, $uploadDir.$targetFilename);
				echo 'shaidan/'.date('Ymd',time()).'/'.$targetFilename;
			} else {
				echo 'Invalid file type.';
			}
		}
	}
	//检查图片存在否
	public function check_exists(){
		$fileurl = $_SERVER['DOCUMENT_ROOT'].'/statics/uploads/shaidan/'.date('Ymd',time()).'/'.$_POST['filename'];
		if (file_exists($fileurl)){
			echo 1;
		}else{
			echo 0;
		}
	}
	public function file_upload(){
		ini_set('display_errors', 0);
		// error_reporting(E_ALL);
		include dirname(__FILE__).DIRECTORY_SEPARATOR."lib".DIRECTORY_SEPARATOR."UploadHandler.php";
		$upload_handler = new UploadHandler();
	}
	public function singoldimg(){
		if($_POST['action']=='del'){
			$sd_id=$_POST['sd_id'];
			$oldimg=$_POST['oldimg'];
			$shaidan=$this->db->GetOne("select * from `@#_shaidan` where `sd_id`='$sd_id'");
			$sd_photolist=str_replace($oldimg.";","",$shaidan['sd_photolist']);
			$this->db->Query("UPDATE `@#_shaidan` SET sd_photolist='".$sd_photolist."' where sd_id='".$sd_id."'");
		}
	}
	public function singphotoup(){
		$mysql_model=System::load_sys_class('model');
		if(!empty($_FILES)){
			$uid=isset($_POST['uid']) ? $_POST['uid'] : NULL;
			$ushell=isset($_POST['ushell']) ? $_POST['ushell'] : NULL;
			$login=$this->checkuser($uid,$ushell);
			if(!$login){_messagemobile("上传出错");}
			System::load_sys_class('upload','sys','no');
			upload::upload_config(array('png','jpg','jpeg','gif'),1000000,'shaidan');
			upload::go_upload($_FILES['Filedata']);
			if(!upload::$ok){
				echo _messagemobile(upload::$error,null,3);
			}else{
				$img=upload::$filedir."/".upload::$filename;
				$size=getimagesize(G_UPLOAD_PATH."/shaidan/".$img);
				$max=700;$w=$size[0];$h=$size[1];
				if($w>700){
					$w2=$max;
					$h2=$h*($max/$w);
					upload::thumbs($w2,$h2,1);
				}

				echo trim("shaidan/".$img);
			}
		}
	}
	public function singdel(){
		$action=isset($_GET['action']) ? $_GET['action'] : null;
		$filename=isset($_GET['filename']) ? $_GET['filename'] : null;
		if($action=='del' && !empty($filename)){
			$filename=G_UPLOAD_PATH.'shaidan/'.$filename;
			$size=getimagesize($filename);
			$filetype=explode('/',$size['mime']);
			if($filetype[0]!='image'){
				return false;
				exit;
			}
			unlink($filename);
			exit;
		}
	}
	//晒单删除
	public function shaidandel(){
		_messagemobile("不可以删除!");
		$member=$this->userinfo;
		//$id=isset($_GET['id']) ? $_GET['id'] : "";
		$id=$this->segment(4);
		$id=intval($id);
		$shaidan=$this->db->Getone("select * from `@#_shaidan` where `sd_userid`='$member[uid]' and `sd_id`='$id'");
		if($shaidan){
			$this->db->Query("DELETE FROM `@#_shaidan` WHERE `sd_userid`='$member[uid]' and `sd_id`='$id'");
			_messagemobile("删除成功",WEB_PATH."/mobile/home/singlelist");
		}else{
			_messagemobile("删除失败",WEB_PATH."/mobile/home/singlelist");
		}
	}



	 

}