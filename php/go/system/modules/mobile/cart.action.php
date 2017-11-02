  <?php
  defined ('G_IN_SYSTEM') or exit ( 'No permission resources.' );
  System::load_app_class ( 'base', 'member', 'no' );
  System::load_app_fun ( 'user', 'go' );
  System::load_app_fun("pay","pay");
	System::load_sys_fun("user");
	System::load_app_class("tocode","pay",'no');

  class cart extends base {
	 private $Cartlist;
	  private $Cartlist_jf;
	  public function __construct() {
	  	  generate_log_product("in constructor, cookie is:"._getcookie('Cartlist'));
		  $this->Cartlist = _getcookie('Cartlist');
		  generate_log_product("in constructor".$this->Cartlist);
		  $this->Cartlist_jf = _getcookie('Cartlist_jf');
		  $this->db = System::load_sys_class("model");
	  }
  
	  //购物车商品列表
	  public function cartlist() {
		  $webname = $this->_cfg ['web_name'];
		  // echo "<pre>";
		  // print_r($Mcartlist);
		  $this->Cartlist = _getcookie('Cartlist');
		  
		  generate_log_callback("in cartlist, value is:".$this->Cartlist);
		  $Mcartlist = json_decode ( stripslashes ( $this->Cartlist ), true );		  
		  //$Mcartlist = array();

		  $shopids = '';
		  if (is_array ( $Mcartlist )) {
			  foreach ( $Mcartlist as $key => $val ) {
				  $shopids .= intval ( $key ) . ',';
			  }
			  $shopids = str_replace ( ',0', '', $shopids );
			  $shopids = trim ( $shopids, ',' );
		  }
		  generate_log_callback("shopids is:".$shopids);
		  // echo $shopids;
		  $shoplist = array ();
		  if ($shopids != NULL) {
			$shoparr = $this->db->GetList ( "SELECT * FROM `@#_shoplist` where `id` in($shopids)", array ("key" => "id" ) );}
		  if (! empty ( $shoparr )) {
			  foreach ( $shoparr as $key => $val ) {
				  if ($val ['q_end_time'] == '' || $val ['q_end_time'] == NULL) {
					  $shoplist [$key] = $val;
					//  $Mcartlist [$val ['id']] = array();						   
					  $Mcartlist [$val ['id']] ['num'] = $Mcartlist[$val ['id']] ['num'];
					  $Mcartlist [$val ['id']] ['shenyu'] = $val ['shenyurenshu'];
					  $Mcartlist [$val ['id']] ['money'] = $val ['yunjiage'];
					  $Mcartlist [$val ['id']] ['sun'] = $val ['yunjiage']*$Mcartlist [$val ['id']] ['num'];
				  } else {
				  	unset($Mcartlist [$val ['id']]);
				  }
			  }
			  $filteredCartList = json_encode ( $Mcartlist );
			  generate_log_callback("filtered cart list is: ".$filteredCartList);
			  _setcookie ( 'Cartlist', $filteredCartList, '' );
		  } else {
		  	  _setcookie ( 'Cartlist', '', '' );
		  }
		  
		  $MoenyCount = 0;
		  $Cartshopinfo = '{';
		  if (count ( $shoplist ) >= 1) {
			  foreach ( $Mcartlist as $key => $val ) {
				  $key = intval ( $key );
				  if (isset ( $shoplist [$key] )) {
					  $shoplist [$key] ['cart_gorenci'] = $val ['num'] ? $val ['num'] : 1;
					  $MoenyCount += $shoplist [$key] ['yunjiage'] * $shoplist [$key] ['cart_gorenci'];
					  $shoplist [$key] ['cart_xiaoji'] = substr ( sprintf ( "%.3f", $shoplist [$key] ['yunjiage'] * $val ['num'] ), 0, - 1 );
					  $shoplist [$key] ['cart_shenyu'] = $shoplist [$key] ['zongrenshu'] - $shoplist [$key] ['canyurenshu'];
					  $Cartshopinfo .= "'$key':{'shenyu':" . $shoplist [$key] ['cart_shenyu'] . ",'num':" . $val ['num'] . ",'money':" . $shoplist [$key] ['yunjiage'] . "},";
				  }
			  }
		  }
		  
		  $shop = 0;
		  
		  if (! empty ( $shoplist )) {
			  $shop = 1;
		  }
		  // echo "<pre>";
		  // print_r($Mcartlist);
		  $MoenyCount = substr ( sprintf ( "%.3f", $MoenyCount ), 0, - 1 );
		  $Cartshopinfo .= "'MoenyCount':$MoenyCount}";
		  include templates ( "mobile/cart", "cartlist" );
	  }
	//购物车商品列表
	public function jf_cartlist(){
        $webname=$this->_cfg['web_name'];
		$Mcartlist=json_decode(stripslashes($this->Cartlist_jf),true);
		$shopids='';
		if(is_array($Mcartlist)){
			foreach($Mcartlist as $key => $val){
				$shopids.=intval($key).',';
			}
			$shopids=str_replace(',0','',$shopids);
			$shopids=trim($shopids,',');
		}
		$shoplist=array();
		if($shopids!=NULL){
			$shoparr=$this->db->GetList("SELECT * FROM `@#_jf_shoplist` where `id` in($shopids)",array("key"=>"id"));
		}
		if(!empty($shoparr)){
		  foreach($shoparr as $key=>$val){
		    if($val['q_end_time']=='' || $val['q_end_time']==NULL){
			   $shoplist[$key]=$val;
			   $Mcartlist[$val['id']]['num']=$Mcartlist[$val['id']]['num'];
			   $Mcartlist[$val['id']]['shenyu']=$val['shenyurenshu'];
			   $Mcartlist[$val['id']]['money']=$val['yunjiage'];
			}
		  }
		}

		$MoenyCount=0;
		$Cartshopinfo='{';
		if(count($shoplist)>=1){
			foreach($Mcartlist as $key => $val){
					$key=intval($key);
					if(isset($shoplist[$key])){
						$shoplist[$key]['cart_gorenci']=$val['num'] ? $val['num'] : 1;
						$MoenyCount+=$shoplist[$key]['yunjiage']*$shoplist[$key]['cart_gorenci'];
						$shoplist[$key]['cart_xiaoji']=substr(sprintf("%.3f",$shoplist[$key]['yunjiage']*$val['num']),0,-1);
						$shoplist[$key]['cart_shenyu']=$shoplist[$key]['zongrenshu']-$shoplist[$key]['canyurenshu'];
						$Cartshopinfo.="'$key':{'shenyu':".$shoplist[$key]['cart_shenyu'].",'num':".$val['num'].",'money':".$shoplist[$key]['yunjiage']."},";
					}
			}
		}

		$shop=0;

		if(!empty($shoplist)){
		   $shop=1;
		}
		$MoenyCount=substr(sprintf("%.3f",$MoenyCount),0,-1);
		$Cartshopinfo.="'MoenyCount':$MoenyCount}";
		include templates("mobile/cart","jf_cartlist");
	}
	  
	  // 支付界面
	  public function pay() {
		  $webname = $this->_cfg ['web_name'];
		  parent::__construct ();
		  if (! $member = $this->userinfo) {
			  header ( "location: " . WEB_PATH . "/mobile/user/login" );
		  }
		  //generate_log_callback("cartlist:".stripslashes ( $this->Cartlist ));

		  $Mcartlist = json_decode ( stripslashes ( $this->Cartlist ), true );
		  $shopids = '';
		  if (is_array ( $Mcartlist )) {
			  foreach ( $Mcartlist as $key => $val ) {
				  $shopids .= intval ( $key ) . ',';
			  }
			  $shopids = str_replace ( ',0', '', $shopids );
			  $shopids = trim ( $shopids, ',' );
		  }
		  
		  $shoplist = array ();
		  if ($shopids != NULL) {
			  $shoplist = $this->db->GetList ( "SELECT * FROM `@#_shoplist` where `id` in($shopids) and `q_status` = 0", array (
					  "key" => "id" 
			  ) );
		  }

		  //generate_log_callback("cartlist count:".count($shoplist));

		  $MoenyCount = 0;
		  if (count ( $shoplist ) >= 1) {
			  foreach ( $Mcartlist as $key => $val ) {
				  $key = intval ( $key );
				  if (isset ( $shoplist [$key] )) {
					  $shoplist [$key] ['cart_gorenci'] = $val ['num'] ? $val ['num'] : 1;
					  $MoenyCount += $shoplist [$key] ['yunjiage'] * $shoplist [$key] ['cart_gorenci'];
					  $shoplist [$key] ['cart_xiaoji'] = substr ( sprintf ( "%.3f", $shoplist [$key] ['yunjiage'] * $val ['num'] ), 0, - 1 );
					  $shoplist [$key] ['cart_shenyu'] = $shoplist [$key] ['zongrenshu'] - $shoplist [$key] ['canyurenshu'];
				  }
			  }
			  $shopnum = 0; // 表示有商品
		  } else {
			  _setcookie ( 'Cartlist', NULL );
			  // _message("购物车没有商品!",WEB_PATH);
			  $shopnum = 1; // 表示没有商品
		  }
		  
		  // 总支付价格
		  $MoenyCount = substr ( sprintf ( "%.3f", $MoenyCount ), 0, - 1 );
		  // 会员余额
		  $Money = $member ['money'];
		  // 商品数量
		  $shoplen = count ( $shoplist );
		  
		  $fufen = System::load_app_config ( "user_fufen", '', 'member' );
		  if ($fufen ['fufen_yuan']) {
			  $fufen_dikou = intval ( $member ['score'] / $fufen ['fufen_yuan'] );
		  } else {
			  $fufen_dikou = 0;
		  }
		  $paylist = $this->db->GetList("SELECT * FROM `@#_pay` where `pay_start` = '1' AND pay_mobile = 1");
		  
		  session_start ();
		  $_SESSION ['submitcode'] = $submitcode = uniqid ();
		  include templates ( "mobile/cart", "payment" );
	  }
	  
	//支付界面
	public function jf_pay(){
        $webname=$this->_cfg['web_name'];
		parent::__construct();
		if(!$member=$this->userinfo){
		  header("location: ".WEB_PATH."/mobile/user/login");
		}
		$Mcartlist=json_decode(stripslashes($this->Cartlist_jf),true);
		$shopids='';
		if(is_array($Mcartlist)){
			foreach($Mcartlist as $key => $val){
				$shopids.=intval($key).',';
			}
			$shopids=str_replace(',0','',$shopids);
			$shopids=trim($shopids,',');

		}

		$shoplist=array();
		if($shopids!=NULL){
			$shoplist=$this->db->GetList("SELECT * FROM `@#_jf_shoplist` where `id` in($shopids)",array("key"=>"id"));
		}
		$MoenyCount=0;
		if(count($shoplist)>=1){
			foreach($Mcartlist as $key => $val){
					$key=intval($key);
					if(isset($shoplist[$key])){
						$shoplist[$key]['cart_gorenci']=$val['num'] ? $val['num'] : 1;
						$MoenyCount+=$shoplist[$key]['yunjiage']*$shoplist[$key]['cart_gorenci'];
						$shoplist[$key]['cart_xiaoji']=substr(sprintf("%.3f",$shoplist[$key]['yunjiage']*$val['num']),0,-1);
						$shoplist[$key]['cart_shenyu']=$shoplist[$key]['zongrenshu']-$shoplist[$key]['canyurenshu'];
					}
			}
			$shopnum=0;  //表示有商品
		}else{
			_setcookie('Cartlist_jf',NULL);
			//_message("购物车没有商品!",WEB_PATH);
			$shopnum=1; //表示没有商品
		}

		//总支付价格
		$MoenyCount=substr(sprintf("%.3f",$MoenyCount),0,-1);
		//会员余额
		$Money=$member['money'];
		//商品数量
		$shoplen=count($shoplist);

		$fufen = System::load_app_config("user_fufen",'','member');
		if($fufen['fufen_yuan']){
			$fufen_dikou = intval($member['score'] / $fufen['fufen_yuan']);

		}else{
			$fufen_dikou = 0;
		}
		$paylist = $this->db->GetList("select * from `@#_pay` where `pay_start` = '1'");

		session_start();
		$_SESSION['submitcode'] = $submitcode = uniqid();
		include templates("mobile/cart","jf_payment");
	}
	  
	  // 开始支付
	  public function paysubmit() {
	  	$pay_start_time = getTimeInMilliSec();
		  $webname = $this->_cfg ['web_name'];
		  header ( "Cache-control: private" );
		  parent::__construct ();
		  if (! $this->userinfo) {
			  header ( "location: " . WEB_PATH . "/mobile/user/login" );
			  exit ();
		  }
		  
		  session_start ();
		  
		  
		  $checkpay = $this->segment ( 4 ); // 获取支付方式 fufen money bank
		  $banktype = $this->segment ( 5 ); // 获取选择的银行 CMBCHINA ICBC CCB
		  $money = $this->segment ( 6 ); // 获取需支付金额																					
		  $fufen = $this->segment ( 7 ); // 获取积分
		  $submitcode1 = $this->segment ( 8 ); // 获取SESSION
		  
		  $uid = $this->userinfo ['uid'];
		  
		  
		  if (! empty ( $submitcode1 )) {
			  if (isset ( $_SESSION ['submitcode'] )) {
				  $submitcode2 = $_SESSION ['submitcode'];
			  } else {
				  $submitcode2 = null;
			  }
			  if ($submitcode1 == $submitcode2) {
				  unset ( $_SESSION ["submitcode"] );
			  } else {
				  $WEB_PATH = WEB_PATH;
				  _messagemobile ( "请不要重复提交...<a href='{$WEB_PATH}/mobile/cart/cartlist' style='color:#22AAFF'>返回购物车</a>查看" );
				  exit ();
			  }
		  } else {
  // 			$WEB_PATH = WEB_PATH;
  // 			_messagemobile ( "正在返回购物车...<a href='{$WEB_PATH}/mobile/cart/cartlist' style='color:#22AAFF'>返回购物车</a>查看" );
		  }
		  
		
		  $zhifutype = $this->db->GetOne ( "select * from `@#_pay` where `pay_class` = 'alipay' " );
		  if (! $zhifutype) {
			  _messagemobile ( "手机支付只支持易宝,请联系站长开通！" );
		  }
		  
		  $pay_checkbox = false;
		  $pay_type_bank = false;
		  $pay_type_id = false;
		  
		  if ($checkpay == 'money') {
			  $pay_checkbox = true;
		  }
		  
		  if ($banktype != 'nobank') {
			  $pay_type_id = $banktype;
		  }
		  
		  if (! empty ( $zhifutype )) {
			  $pay_type_bank = $zhifutype ['pay_class'];
		  }
		  
		 
		  if (! $pay_type_id) {
			  if ($checkpay != 'fufen' && $checkpay != 'money')
				  _messagemobile ( "选择支付方式" );
		  }
		  
		  /**
		   * ***********
		   * start
		   * ***********
		   */
		$pay=System::load_app_class('pay','pay');
 //修改支付每次都要使用积分问题 lq 2014-12-01
        //$pay->fufen = $fufen;
		$pay->fufen = $checkpay=='fufen'?$fufen:0;
		$pay->pay_type_bank = $pay_type_bank;
		generate_log_product("in cart action: ".$uid."   ".$pay_type_id."  ".$pay_type_bank);
		$ok = $pay->init($uid,$pay_type_id,'go_record');	//云购商品
		generate_log_product("go result:".$ok);
		if($ok != 'ok'){
			_setcookie('Cartlist',NULL);
			_messagemobile("购物车没有商品请<a href='".WEB_PATH."/mobile/cart/cartlist' style='color:#22AAFF'>返回购物车</a>查看");
		  }
		  
		  $check = $pay->go_pay ( $pay_checkbox );
		  if (! $check) {
			  _messagemobile ( "订单添加失败,请<a href='" . WEB_PATH . "/mobile/cart/cartlist' style='color:#22AAFF'>返回购物车</a>查看" );
		  }
		  if ($check) {
		  	$timeCostInMilli = getTimeInMilliSec() - $pay_start_time;
		  	//generate_log("Pay submit cost time: ".$timeCostInMilli);
			  // 成功
			  header ( "location: " . WEB_PATH . "/mobile/cart/paysuccess" );
		  } else {
			  // 失败
			  _setcookie ( 'Cartlist', NULL );
			  header ( "location: " . WEB_PATH . "/mobile/mobile" );
		  }
		  exit ();
	  }
	  
	//开始支付
	public function jf_paysubmit(){
		$webname=$this->_cfg['web_name'];
		header("Cache-control: private");
		parent::__construct();
		if(!$this->userinfo){
		  header("location: ".WEB_PATH."/mobile/user/login");
		  exit;
		}
		session_start();

		// if(isset($_POST['submitcode'])) {
		// 	if(isset($_SESSION['submitcode'])){
		// 		$submitcode = $_SESSION['submitcode'];
		// 	}else{
		// 		$submitcode = null;
		// 	}
		// 	if($_POST['submitcode'] == $submitcode){
		// 		unset($_SESSION["submitcode"]);
		// 	}else{
		// 		_message("请不要重复提交...",WEB_PATH.'/mobile/cart/jf_cartlist');
		// 	}
		// }else{
		// 	_message("正在返回购物车...",WEB_PATH.'/mobile/cart/jf_cartlist');
		// }

		$uid = $this->userinfo['uid'];

		$pay_checkbox=!empty($_POST['moneycheckbox']) ? intval($_POST['moneycheckbox']) : 0;
		$shop_score=!empty($_POST['shop_score']) ? intval($_POST['shop_score']) : 0;
		$jf_use_num=!empty($_POST['jf_use_num']) ? intval($_POST['jf_use_num']) : 0;

		if(!$pay_checkbox && !$shop_score){
			_message("请选择支付方式",WEB_PATH.'/mobile/cart/jf_cartlist');
		}

		if($pay_checkbox){
			$payact = 'zh';
		}
		if($shop_score){
			$payact = 'jf';
		}
		if($shop_score && $pay_checkbox){
			$payact = 'all';
		}


		$pay=System::load_app_class('pay','pay');

		$pay->fufen = $shop_score ? $jf_use_num : 0;
		$pay->pay_type_bank = 0;
		$ok = $pay->init($uid,$payact,'jf_go_record');	//云购商品
		if($ok != 'ok'){
			$_COOKIE['Cartlist_jf'] = NULL;
			_setcookie("Cartlist_jf",null);
			_messagemobile("购物车没有商品请<a href='".WEB_PATH."/mobile/cart/jf_cartlist' style='color:#22AAFF'>返回购物车</a>查看");
		}

		$check = $pay->jf_go_pay($pay_checkbox);
		if($check + 100 == 0){
			_messagemobile("账户余额不足以支付，请<a href='".WEB_PATH."/mobile/home/userrecharge' style='color:#22AAFF'>充值</a>");
		}
		if(!$check){
			_messagemobile("订单添加失败,请<a href='".WEB_PATH."/mobile/cart/jf_cartlist' style='color:#22AAFF'>返回购物车</a>查看");
		}
		if($check){
			//成功
			header("location: ".WEB_PATH."/mobile/cart/jf_paysuccess");
		}else{
			//失败
			$_COOKIE['Cartlist_jf'] = NULL;
			_setcookie("Cartlist_jf",null);
			header("location: ".WEB_PATH."/mobile/mobile");
		}
		exit;
	}

	//成功页面
	public function paysuccess(){
	    $webname=$this->_cfg['web_name'];
		_setcookie('Cartlist',NULL);
		include templates("mobile/cart","paysuccess");
	}

	//成功页面
	public function paycancel(){
	    $webname=$this->_cfg['web_name'];
		include templates("mobile/cart","paycancel");
	}

	//成功页面
	public function jf_paysuccess(){
	    $webname=$this->_cfg['web_name'];
		$_COOKIE['Cartlist_jf'] = NULL;
		_setcookie("Cartlist_jf",null);
		include templates("mobile/cart","jf_paysuccess");
	}
	  
	  // 充值
	  public function addmoney() {
	  	  generate_log_product("in add money.");
		  parent::__construct ();
		  $webname = $this->_cfg ['web_name'];
		  $money = $this->segment ( 4 ); // 获取充值金额
		  $pay_id = $this->segment ( 5 ); // 获取选择的支付方式
		
		  if (! $this->userinfo) {
			  header ( "location: " . WEB_PATH . "/mobile/user/login" );
			  exit ();
		  }
		  
		  $payment = $this->db->GetOne ( "select * from `@#_pay` where `pay_id` = ".$pay_id );
		  
		  
		  if (! $payment) {
			  _messagemobile ( "对不起，没有您所选择的支付方式！" );
		  }
		  
		  if (! empty ( $payment )) {
			  $pay_type_bank = $payment ['pay_class'];
		  }

		  generate_log_product("pay id:".$pay_id." pay type bank:".$pay_type_bank);
		  $pay_type_id = $pay_id;
  // 		$pay_type_bank=isset($_POST['pay_bank']) ? $_POST['pay_bank'] : false;
  // 		$pay_type_id=isset($_POST['account']) ? $_POST['account'] : false;
  // 		$money=intval($_POST['money']);
		  $uid = $this->userinfo ['uid'];
		  $pay = System::load_app_class ( 'pay', 'pay' );
		  $pay->pay_type_bank = $pay_type_bank;
		  $ok = $pay->init ( $uid, $pay_type_id, 'addmoney_record', $money );
  
		  generate_log_product("ok :".$ok);

		  if ($ok === 'not_pay') {
			  _messagemobile ( "未选择支付平台" );
		  }
	  }

	private function markTaskFinished($taskId) {
		$this->db->Query("update `@#_async_task` set over = 1 where `id` = $taskId");
	}

	public function test123() {
		echo json_encode(array("result" => 1));
	}

	//this action handles lottery drawing and new item creation
	public function operation_after_payment() {
		generate_log_callback("in operation_after_payment.");
		$startTime = getTimeInMilliSec();
		$shopId = $_REQUEST['shop_id'];
		$taskId = $_REQUEST['task_id'];
		$resultArr = array();
		$this->db->Autocommit_start();
		$task = $this->db->GetOne("select * from `@#_async_task` where `id` = ".intval($taskId)." for update");

		if (!shopId) {
			$resultArr['code'] = -1;
			//mark the task as finished
			$this->markTaskFinished($taskId);
			$this->db->Autocommit_commit();
			echo json_encode($resultArr);
		} else {
			$shop = $this->db->GetOne("select `id`, `sid`, `cateid`, `brandid`, `title`, `money`, `yunjiage`, `zongrenshu`, `canyurenshu`, `shenyurenshu`, `qishu`, `maxqishu`, `codes_table`, `xsjx_time`, `renqi`, `time`, `order`, `zhiding`, `sgid` from `@#_shoplist` where `id` = '$shopId'");
			if (!$shop) {
				$this->markTaskFinished($taskId);
				$this->db->Autocommit_commit();
				$resultArr['code'] = -1;
				echo json_encode($resultArr);				
			} else {
				$sid = $shop['sid'];
				//if the assigned product isn't the newest product, by pass it
				$goodsInfo = $this->db->GetOne("select `id` from `@#_shoplist` where `sid` = '$sid' order by id desc limit 1");
				if ($goodsInfo['id'] > intval($shopId)) {
					$resultArr['code'] = -1;
					$resultArr['msg'] = "not the latest goods of sid".$sid;
					$this->markTaskFinished($taskId);
					$this->db->Autocommit_commit();					
					echo json_encode($resultArr);
				} else {
					if ($task['over'] == 1) {
						//already handled
						$resultArr['code'] = -1;
						$resultArr['msg'] = "task already executed.";
						$this->db->Autocommit_rollback();
						echo json_encode($resultArr);															
					} else {
						generate_log_product("not finished task".$taskId." shopid is: ".$shopId);
						$timea = getTimeInMilliSec();
						generate_log_callback("before pay_insert_shop.");
						$query_insert = pay_insert_shop($shop,'add');
						generate_log_product("pay insert shop result:".$query_insert);
						//$query_insert = false;
						$costa = getTimeInMilliSec() - $timea;
						generate_log_product("for goods".$shop['id']." cost time: ".$costa." milli seconds." );
						if(!$query_insert){
							$resultArr['code'] = -1;
							$resultArr['msg'] = "failed to handled the goods";
							$this->db->Autocommit_rollback();
							echo json_encode($resultArr);									
						}else{
							$this->db->Query("update `@#_async_task` set over = 1 where `id` = $taskId");
							$resultArr['code'] = 1;
							$this->db->Autocommit_commit();
							$costtime = getTimeInMilliSec()- $startTime;
							generate_log_callback("callback cost ".$costtime." milliseconds");
							echo json_encode($resultArr);									
						}											
					}
				}
			}
		}
	}
  }
  ?>
