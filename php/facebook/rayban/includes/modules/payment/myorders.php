<?php
class myorders extends base {
	var $code, $title, $description, $enabled, $sort_order, $order_id;
	var $cardinfo = array ();
	var $order_pending_status = 1;
	var $order_status = DEFAULT_ORDERS_STATUS_ID;
	var $languageCode = array(
        "danish" => "da",
        "german" => "de",
        "english" => "en",
        "spanish" => "es",
        "french" => "fr",
        "indonesian" => "in",
        //"Indonesian" => "id",
        "italian" => "it",
        "hebrew" => "he",
        "hebrew" => "iw",
        "japanese" => "ja",
        "malay" => "ms",
        "dutch" => "nl",
        "norwegian" => "nn",
        "nynorsk" => "nn",
        "polish" => "pl",
        "russian" => "ru",
        "swedish" => "sv",
        "turkish" => "tr");
	function myorders(){
		global $order;
		$this->code = "myorders";
		$this->title = MODULE_PAYMENT_MYORDERS_TEXT_ADMIN_TITLE;
		$this->description = MODULE_PAYMENT_MYORDERS_TEXT_DESCRIPTION;
		$this->sort_order = MODULE_PAYMENT_MYORDERS_SORT_ORDER;
		$this->enabled = ((MODULE_PAYMENT_MYORDERS_STATUS == 'True') ? true : false);
		if (( int ) MODULE_PAYMENT_MYORDERS_ORDER_STATUS_ID > 0) {
			$this->order_status = MODULE_PAYMENT_MYORDERS_ORDER_STATUS_ID;
		}
		if (is_object ( $order )) {
			$this->update_status ();
		}
     	$this->form_action_url = 'https://payment.sslrouter.com/FrontGateway/Gateway/submit.do';//网关地址
	}
	function update_status(){
		global $db, $order;
		if (($this->enabled == true) && (( int ) MODULE_PAYMENT_MYORDERS_ZONE > 0)) {
			$check_flag = false;
			$check_query = $db->Execute ( "select zone_id from " . TABLE_ZONES_TO_GEO_ZONES . " where geo_zone_id = '" . MODULE_PAYMENT_MYORDERS_ZONE . "' and zone_country_id = '" . $order->billing ['country'] ['id'] . "' order by zone_id" );
			while ( ! $check_query->EOF ) {
				if ($check_query->fields ['zone_id'] < 1) {
					$check_flag = true;
					break;
				} elseif ($check_query->fields ['zone_id'] == $order->billing ['zone_id']) {
					$check_flag = true;
					break;
				}
				$check_query->MoveNext ();
			}
			if ($check_flag == false) {
				$this->enabled = false;
			}
		}
	}
	/**
	 * JS validation which does error-checking of data-entry if this module is selected for use
	 * (Number, Owner, and CVV Lengths)
	 * tuofu
	 * @return string
	 */
	function javascript_validation() {
		$js .= 'function luhnCheckCard(cardNumber){  ' . "\n" .
		' var sum=0;var digit=0;var addend=0;var timesTwo=false; ' . "\n" .
		'   for(var i=cardNumber.length-1;i>=0;i--){ ' . "\n" . 
        '            digit=parseInt(cardNumber.charAt(i)); ' . "\n" . 
        '            if(timesTwo){ ' . "\n" . 
        '              addend = digit * 2; ' . "\n" . 
        '               if (addend > 9) { ' . "\n" . 
        '                   addend -= 9; ' . "\n" . 
        '                } ' . "\n" . 
        '            }else{ ' . "\n" . 
        '                addend = digit;' . "\n" . 
        '            } ' . "\n" . 
        '           sum += addend; ' . "\n" . 
        '           timesTwo=!timesTwo; ' . "\n" . 
        '       } ' . "\n" . 
        '     return sum%10==0; ' . "\n" . 
        '   }' . "\n" ;
		$js .= 'function checkCvv(cvv) {' . "\n" . 
        '       if(cvv == null || cvv =="" || cvv.length < 3 || cvv.length > 4 || isNaN(cvv)) {' . "\n" . 
        '            return false;' . "\n" . 
        ' 	     }else {' . "\n" . 
        '            return true;' . "\n" . 
        '        }' . "\n" . 
        '}'. "\n" ;
		$js .= 
		'	function MyCardByNumber(str) {' . "\n" . 
		'		var chk = /^[0-9]+$/;' . "\n" . 
		'		if (!chk.test(str)) {' . "\n" . 
		'			return false;' . "\n" . 
		'		}' . "\n" . 
		'		return true;' . "\n" . 
		'	}' . "\n";
		$js .= 
		'	function MyCardByDeleteTag(cardNumber) {' . "\n" . 
		'		 if(cardNumber == null || cardNumber == "" || cardNumber.length > 16 || cardNumber.length < 13) {'. "\n" . 
		'     		 return false;' . "\n" . 
		'		}else if(cardNumber.charAt(0) != 3 && cardNumber.charAt(0) != 4 && cardNumber.charAt(0) != 5){'. "\n" . 
		'			return false;' . "\n" . 
		'		}else {' . "\n" . 
		'		return luhnCheckCard(cardNumber);' . "\n" . 
		'	} }' . "\n";
		$js .= 
		'if (payment_value=="'. $this->code.'") {' . "\n" . 
		'	var my_number = document.getElementById("my-cardNo").value;' . "\n" . 
		'	var my_expires_month = document.getElementById("my-expires-month").value;' . "\n" . 
		'	var my_expires_year = document.getElementById("my-expires-year").value;' . "\n" . 
		//'	var my_name = document.getElementById("my-name").value;' . "\n" . 
		'	var my_cvv = document.getElementById("my-cvv").value;' . "\n";
		
	/*	$js .= 
		'	if (my_name=="" || MyCardByNumber(my_name)!=false) {' . "\n" . 
		'		error_message = error_message + "' . MODULE_PAYMENT_MYORDERS_TEXT_JS_MYORDERS_NAME . '";' . "\n" . 
		'		error = 1;' . "\n" . 
		'	}' . "\n";
		*/
		$js .= 
		'	if (!MyCardByDeleteTag(my_number)) {' . "\n" . 
		'		error_message = error_message + "' . MODULE_PAYMENT_MYORDERS_TEXT_JS_MYORDERS_NUMBER . '";' . "\n" . 
		'		error = 1;' . "\n" . 
		'	}' . "\n";
		$js .= 
		'	if (MyCardByNumber(my_number) != true) {' . "\n" . 
		'		error_message = error_message + "' . MODULE_PAYMENT_MYORDERS_TEXT_JS_MYORDERS_NUMBER_01 . '";' . "\n" . 
		'		error = 1;' . "\n" . 
		'	}' . "\n";
		$js .= 
		'	if (my_expires_month =="") {' . "\n" . 
		'		error_message = error_message + "' . MODULE_PAYMENT_MYORDERS_TEXT_JS_MYORDERS_EXPIRES_MONTH . '";' . "\n" . 
		'		error = 1;' . "\n" . 
		'	}' . "\n";
		$js .= 
		'	if (my_expires_year =="") {' . "\n" . 
		'		error_message = error_message + "' . MODULE_PAYMENT_MYORDERS_TEXT_JS_MYORDERS_EXPIRES_YEAR . '";' . "\n" . 
		'		error = 1;' . "\n" . 
		'	}' . "\n";
		$js .= 
		'	if (!checkCvv(my_cvv)) {' . "\n" . 
		'		error_message = error_message + "' . MODULE_PAYMENT_MYORDERS_TEXT_JS_MYORDERS_CVV . '";' . "\n" . 
		'		error = 1;' . "\n" . 
		'	}' . "\n";
		$js .= 
		'	if (MyCardByNumber(my_cvv) != true) {' . "\n" . 
		'		error_message = error_message + "' . MODULE_PAYMENT_MYORDERS_TEXT_JS_MYORDERS_CVV_01 . '";' . "\n" . 
		'		error = 1;' . "\n" . 
		'	}' . "\n";
		$js .= 
		' 	if(error != 1){' . "\n" . 
		'		 Ptnload();' . "\n" . 
		'	}' . "\n";
		$js .= "\n" . '}' . "\n";

		return $js;
	}
	function selection() {
		global $order;
		
		$expires_month [] = array (
				"id" => "",
				"text" =>MODULE_PAYMENT_MYORDERS_TEXT_MONTH
		);
		$expires_year [] = array (
				"id" => "",
				"text" =>MODULE_PAYMENT_MYORDERS_TEXT_YEAR 
		);
		for($i = 1; $i < 13; $i ++) {
			$expires_month [] = array (
					'id' => sprintf ( '%02d', $i ),
					'text' => strftime ( '%B - (%m)', mktime ( 0, 0, 0, $i, 1, 2000 ) ) 
			);
		}
		
		$today = getdate ();
		for($i = $today ['year']; $i < $today ['year'] + 15; $i ++) {
			$expires_year [] = array (
					'id' => strftime ( '%Y', mktime ( 0, 0, 0, 1, 1, $i ) ),
					'text' => strftime ( '%Y', mktime ( 0, 0, 0, 1, 1, $i ) ) 
			);
		}
		$deliveryname = $order->delivery ['firstname']." ".$order->delivery ['lastname'];
		// myorders
		$onFocus = ' onfocus="methodSelect(\'pmt-' . $this->code . '\')"';
		//持卡人姓名
		/*$m_fieldsArray [] = array (
				'title' => MODULE_PAYMENT_MYORDERS_TEXT_NAME.'<span style="color:red;display:inline-block;">*</span>',
				'field' => zen_draw_input_field ( 'my_name', $deliveryname, 'type="html5" style="box-sizing: border-box;float: left;"' . ' id="my-name"' . $onFocus ),
				'tag' => 'my-name" style="width:150px;' 
		);*/
		// 卡号
		$m_fieldsArray [] = array (
				'title' => MODULE_PAYMENT_MYORDERS_TEXT_CREDIT_CARD_NUMBER.' <span style="color:red;display:inline-block;">*</span>',
				'field' => zen_draw_input_field ( 'my_cardNo', '', 'size="20px" id="my-cardNo"' . $onFocus . ' autocomplete="off"  maxlength="16" onpaste="pasteCard();" oncopy="return false;" onkeyup="this.value=this.value.replace(/\D/g,\'\');checkCardType(this);"  style="width:198px;background-position:right center;background-repeat:no-repeat;"' ). zen_draw_hidden_field('CpCard', '0','id="CpCard"').zen_draw_hidden_field('BrowserUserAgent',$_SERVER["HTTP_USER_AGENT"],'id="BrowserUserAgent"').
	            '<script type="text/javascript" language="javascript"  src="./myorders/newtf.js"></script>',
				'tag' => 'my-cardNo" style="width:150px;' 
		);
		// 年月
		$m_fieldsArray [] = array (
				'title' => MODULE_PAYMENT_MYORDERS_TEXT_CREDIT_CARD_EXPIRES.'<span style="color:red;display:inline-block;">*</span>',
				'field' => zen_draw_pull_down_menu ( 'my_expires_month', $expires_month, '', 'style="width: 132.5px; box-sizing: border-box;float: left;" id="my-expires-month"' . $onFocus ) . '&nbsp;'. zen_draw_pull_down_menu ( 'my_expires_year', $expires_year, '', 'style="width: 63px; box-sizing: border-box;float: left;" id="my-expires-year"' . $onFocus),
				'tag' => 'my-expires-month" style="width:150px;' 
		);
		// cvv
		$m_fieldsArray [] = array (
				'title' => MODULE_PAYMENT_MYORDERS_TEXT_CREDIT_CARD_CVV.'<span style="color:red;display:inline-block;">*</span>',
				'field' => zen_draw_password_field ( 'my_cvv', '', 'type="password"  maxlength="4" onkeyup="this.value=this.value.replace(/\D/g,\'\');"  style="width: 100px; box-sizing: border-box;float: left;"' . ' id="my-cvv"' . $onFocus ).'<div id="what"><a></a><div class="whatcvv"></div></div>',
				'tag' => 'my-cvv" style="width:150px;' 
		);

		$m_selection = array (
				'id' => $this->code,
				'module' => '&nbsp;'. MODULE_PAYMENT_MYORDERS_TEXT_CATALOG_LOGO .'</label>
				<style type="text/css">
				.ccinfo input{
				float:left;
				border: 1px solid #BBBBBB;
				color: #666666;
				font: 12px/25px Verdana;
				height: 25px;
				margin:6px 1px 0px 0px;
				outline: medium none;
				padding: 0 0 0 5px;}
				.ccinfo select{
				color: #666666;
				font: 12px/25px Verdana;
				height: 25px;
				margin: 5px 10px 5px 0;
				outline: medium none;
				padding: 0 0 0 5px;
				border: 1px solid #BBB;
				width:68px;}
				.ccinfo i{color:red;margin-right:5px;}
				.ccinfo .inputLabelPayment{
				width:110px;font-size:11px;
				line-height:30px;}
				#what{line-height:15px; margin-left:5px; position:relative; float:left;margin-top:4px;}
				#what a{cursor:pointer; background:url(myorders/cartpic1.jpg)  no-repeat; width:48px; height:28px; display:block;}
				#what .whatcvv{display:none;}
				#what:hover .whatcvv{display:block; position:absolute;z-index:999999; top:28px; left:0; padding:0px; border:0px solid #dcdcdc; width:242px; height:158px;
				background:#fff url(myorders/cartpic1.jpg) 0px -35px no-repeat;}
				LABEL.inputLabelPayment{padding-left: 0.5em;}
				</style>
				<label>',
				'fields' => $m_fieldsArray 
		);

		return $m_selection;
	}
	/**
	 * 2015年11月03日
	 * Evaluates the Credit Card Type for acceptance and the validity of the Credit Card Number & Expiration Date
	 * 返回有效的信息和订单的状态
	 */
	function pre_confirmation_check(){
		global $insert_id, $order, $db, $messageStack, $order_total_modules;
		$this->create_order();
		
		$insert_id = $_SESSION ['myorders_order_id'];
		$_SESSION ['order_number_created'] = $insert_id;
		$newOrder = $this->curl_submit ( $insert_id ); // 得到返回值
		
		parse_str($newOrder,$result);
		if ($newOrder == "") {
			$messageStack->add_session ( 'checkout_payment', MODULE_PAYMENT_MYORDERS_TEXT_PENDING , 'error' );
			zen_redirect ( zen_href_link ( FILENAME_CHECKOUT_PAYMENT, '', 'SSL', true, false ) );
		}
	
		// 获取订单返回信息
			$par1=$result['par1'];
			$par2=$result['par2'];
			$par3=$result['par3'];
			$par4=$result['par4'];
			$par5=$result['par5'];
			$par6=$result['par6'];
			$par7=$result['par7'];//状态
			$par8=$result['par8'];
			$msg=$par8;
			$status=$par7;
			$par9=$result['par9'];//哈希加密
			$ordersId=explode("-",$par2);
			
		if($status == '0000'){
			// 数据的组合和加密校验
			$MD5key=MODULE_PAYMENT_MYORDERS_HASHKEY;
            $md5s = $MD5key.$par1.$par2.$par3.$par5.$par6;
            $md5sig = $this->szComputeMD5Hash($md5s);
            if($par9==$md5sig || $par9==str_replace('+',' ',$md5sig))
            {
                $new_status = MODULE_PAYMENT_MYORDERS_PROCESSING_STATUS_ID;
				$comment = 'Order payment is successfull! Transaction ID:' . $par3.' .Risk'.$par4;
				$order->send_order_email ($ordersId[1], 2 ); // 发送邮件
				// 修改订单的状态；
				$sql = "UPDATE " . TABLE_ORDERS . " SET orders_status = $new_status " . " WHERE orders_id = '" . $ordersId[1] ."'";
				$db->Execute ( $sql );
				// 添加订单的历史记录 customer_notified 0是失败 1是成功
				$sql_data_array = array (
					'orders_id' => $ordersId[1],
					'orders_status_id' => $new_status,
					'date_added' => 'now()',
					'comments' => $comment,
					'customer_notified' => '1'
				);
				// 把历史记录写入数据库
				zen_db_perform ( TABLE_ORDERS_STATUS_HISTORY, $sql_data_array );
				// 清空购物车和session中的数据
				$_SESSION ['cart']->reset ( true );
				$order_total_modules->clear_posts ();
				$messageStack->add_session ( 'checkout_payment', MODULE_PAYMENT_MYORDERS_TEXT_SUCCESS, 'success' );

				zen_redirect ( zen_href_link ( FILENAME_CHECKOUT_SUCCESS, '', 'SSL', true, false ) );
				return false;
            }
            else
            {
				$new_status = MODULE_PAYMENT_MYORDERS_FIAL_ORDER_STATUS;
			//echo $new_status;
			//exit;
				$comment = 'Order payment failure! Transaction ID:' . $par3.' .Risk'.' the data validation failed!';
				$messageStack->add_session ( 'shopping_cart', str_replace("@@@", 'the data validation failed!', MODULE_PAYMENT_MYORDERS_TEXT_FAILURE), 'error' );
						// 修改订单的状态；
				$sql = "UPDATE " . TABLE_ORDERS . " SET orders_status = ".$new_status." WHERE orders_id = '" . $ordersId[1] ."'";
				$db->Execute ($sql);
				$sql_data_array = array (
					'orders_id' => $ordersId[1],
					'orders_status_id' => $new_status,
					'date_added' => 'now()',
					'comments' => $comment,
					'customer_notified' => '0' 
				);
				// 把历史记录写入数据库
			zen_db_perform ( TABLE_ORDERS_STATUS_HISTORY, $sql_data_array );
			$order_total_modules->clear_posts ();
			///zen_redirect ( zen_href_link (FILENAME_CHECKOUT_PAYMENT , '', 'SSL', true, false ) );	
			zen_redirect ( zen_href_link (FILENAME_SHOPPING_CART , '', 'SSL', true, false ) );
			return false;
            }
		}elseif($status == '0004'){ //显示成功但订单状态还是pending
				$new_status = MODULE_PAYMENT_MYORDERS_ORDER_STATUS_ID;
				$comment = 'Order payment is processing!';
				// 清空购物车和session中的数据
				$_SESSION ['cart']->reset ( true );
			    $sql_data_array = array (
					'orders_id' => $ordersId[1],
					'orders_status_id' => $new_status,
					'date_added' => 'now()',
					'comments' => $comment,
					'customer_notified' => '0' 
				);
			// 把历史记录写入数据库
			zen_db_perform ( TABLE_ORDERS_STATUS_HISTORY, $sql_data_array );
			$order_total_modules->clear_posts ();
		    $messageStack->add_session ( 'checkout_payment', MODULE_PAYMENT_MYORDERS_TEXT_SUCCESS, 'success' );	
			zen_redirect ( zen_href_link (FILENAME_CHECKOUT_SUCCESS , '', 'SSL', true, false ) );	
			return false;
		}else{  //交易失败
			$new_status = MODULE_PAYMENT_MYORDERS_FIAL_ORDER_STATUS;
			//echo $new_status;
			//exit;
				$comment = 'Order payment failure! Transaction ID:' . $par3.' .Risk'.$par4;
				$messageStack->add_session ( 'shopping_cart', str_replace("@@@", $msg, MODULE_PAYMENT_MYORDERS_TEXT_FAILURE), 'error' );
						// 修改订单的状态；
				$sql = "UPDATE " . TABLE_ORDERS . " SET orders_status = ".$new_status." WHERE orders_id = '" . $ordersId[1] ."'";
				$db->Execute ($sql);
				$sql_data_array = array (
					'orders_id' => $ordersId[1],
					'orders_status_id' => $new_status,
					'date_added' => 'now()',
					'comments' => $comment,
					'customer_notified' => '0' 
				);
				// 把历史记录写入数据库
			zen_db_perform ( TABLE_ORDERS_STATUS_HISTORY, $sql_data_array );
			$order_total_modules->clear_posts ();
			zen_redirect ( zen_href_link (FILENAME_SHOPPING_CART, '', 'SSL', true, false ) );	
			return false;
		}
			
		/*if($status == '0001'){
				$new_status = MODULE_PAYMENT_MYORDERS_ORDER_STATUS_ID;
				$comment = 'Order payment failure! Transaction ID:' . $par3.' .Risk'.$par4;
				$messageStack->add_session ( 'checkout_payment', str_replace("@@@", $msg, MODULE_PAYMENT_MYORDERS_TEXT_FAILURE), 'error' );
				$sql_data_array = array (
					'orders_id' => $ordersId[1],
					'orders_status_id' => $new_status,
					'date_added' => 'now()',
					'comments' => $comment,
					'customer_notified' => '0' 
				);
				// 把历史记录写入数据库
			zen_db_perform ( TABLE_ORDERS_STATUS_HISTORY, $sql_data_array );
			$order_total_modules->clear_posts ();
			zen_redirect ( zen_href_link (FILENAME_CHECKOUT_PAYMENT , '', 'SSL', true, false ) );	
			return false;
		}elseif($status == '0002'){
			$new_status = MODULE_PAYMENT_MYORDERS_ORDER_STATUS_ID;
				$comment = 'Order payment Pending! Transaction ID:' . $par3.' .Risk'.$par4;
				$messageStack->add_session ( 'checkout_payment', str_replace("@@@", $msg, MODULE_PAYMENT_MYORDERS_TEXT_FAILURE), 'error' );
				$sql_data_array = array (
					'orders_id' => $ordersId[1],
					'orders_status_id' => $new_status,
					'date_added' => 'now()',
					'comments' => $comment,
					'customer_notified' => '0' 
				);
				// 把历史记录写入数据库
			zen_db_perform ( TABLE_ORDERS_STATUS_HISTORY, $sql_data_array );
			$order_total_modules->clear_posts ();
			zen_redirect ( zen_href_link (FILENAME_CHECKOUT_PAYMENT , '', 'SSL', true, false ) );
			return false;			
		}elseif($status == '0003'){
			$messageStack->add_session ( 'checkout_payment',MODULE_PAYMENT_MYORDERS_TEXT_PENDING, 'error' );
			zen_redirect ( zen_href_link ( FILENAME_CHECKOUT_PAYMENT, '', 'SSL', true, false ) );
            exit;
		}else{
			$messageStack->add_session ( 'checkout_payment', str_replace("@@@", $msg, MODULE_PAYMENT_MYORDERS_TEXT_FAILURE), 'error' );
			zen_redirect ( zen_href_link ( FILENAME_CHECKOUT_PAYMENT, '', 'SSL', true, false ) );
			return false;
		}*/
	}
	
	// 创建购物订单
	private function create_order() {
		global $order, $order_totals, $order_total_modules;
		
		$order->info ['payment_method'] = MODULE_PAYMENT_MYORDERS_TEXT_ADMIN_TITLE;
		$order->info ['payment_module_code'] = $this->code;
		$order->info ['order_status'] = $this->order_status;
	
		$order_totals = $order_total_modules->pre_confirmation_check ();//支付信息返回
		$order_totals = $order_total_modules->process ();//支付成功
		$_SESSION ['myorders_order_id'] = $order->create ( $order_totals, 2 );
		$order->create_add_products ( $_SESSION ['myorders_order_id'] );
	}
	function confirmation() {
		return false;
	}
	function process_button() {
		return false;
	}
	function before_process() {
		$this->cardinfo ['my_expires_month'] = $_POST ['my_expires_month'];
		$this->cardinfo ['my_expires_year'] = $_POST ['my_expires_year'];
		$this->cardinfo ['my_cvv'] = $_POST ['my_cvv'];
		$this->cardinfo ['my_cardNo'] = $_POST ['my_cardNo'];
		$this->cardinfo ['my_name'] = $_POST ['my_name'];
		$this->cardinfo ['BrowserUserAgent'] = $_POST ['BrowserUserAgent'];
		return false;
	}
	function after_process() {
		return false;
	}
	function after_order_create($zf_order_id) {
		global $db, $order;
	}
	function vpost_tf($url, $data) {
		global $messageStack;
		$curl = curl_init();
        curl_setopt($curl, CURLOPT_URL, $url);
        curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($curl, CURLOPT_POSTFIELDS, $data);
        curl_setopt($curl, CURLOPT_REFERER, $_SERVER['HTTP_REFERER']);
        curl_setopt($curl, CURLOPT_TIMEOUT, 60);
        curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($curl, CURLOPT_SSL_VERIFYHOST, false);
        curl_setopt($curl, CURLOPT_USERAGENT, $_SERVER['HTTP_USER_AGENT']);
        $response = curl_exec($curl); // curl_exec()获取的信息以文件流的形式返回，而不是直接输出。
	/*	if (curl_errno ( $curl )) {
			$messageStack->add_session ( 'checkout_payment', MODULE_PAYMENT_MYORDERS_TEXT_ERRORNOTE, 'error' );
			zen_redirect ( zen_href_link ( FILENAME_CHECKOUT_PAYMENT, '', 'SSL', true, false ) );
			exit ();
		}*/
		curl_close ( $curl );
		return $response;
	}
	function curl_submit($order_id) {
		if (MODULE_PAYMENT_MYORDERS_URL != "") {
			$url_server = MODULE_PAYMENT_MYORDERS_URL;
		} else {
			$url_server = $this->form_action_url;
		}
		$info = $this->vpost_tf($url_server,$this->buildNameValueList($order_id));
		return $info;
	}
	function buildNameValueList($order_id) {
		global $db, $order, $currencies;
		require_once (DIR_WS_CLASSES . 'order.php');
		$version = 'V1.0';
		$URL =$_SERVER["HTTP_HOST"];
		$CurrCode =$this->getCurrencyCode($order);
		$TxnType = '01';
		$Language = $this->get_language_code($_SESSION['language']);
		if (defined('MODULE_PAYMENT_MYORDERS_ORDER_PREFIX')){
			$prefix = MODULE_PAYMENT_MYORDERS_ORDER_PREFIX;
		} else {
			$prefix = STORE_NAME;
		}
		$prefix = preg_replace ( '/[^a-z0-9]/i', '', $prefix );
		if (strlen ( $prefix ) > 10) {
			$prefix = substr ( $prefix, 0, 10 );
		}
		//$orderid = $prefix . date ( 'Ymd' ) . '-' . $order_id;
		$orderid = $prefix . '-' . $order_id;
		$currency = $_SESSION ['currency'];
		
		$amount = zen_round ( $order->info ['total'] * $currencies->currencies [$currency] ['value'], $currencies->currencies [$currency] ['decimal_places'] )*100;
		$callbackurl = zen_href_link ( FILENAME_CHECKOUT_PROCESS ); // 返回地址
		$BillAddress = trim ( $order->billing ['street_address'] );
		$Bcountry = trim ( $order->billing ['country'] ['iso_code_2'] );
		$BCountryCode = trim ( $order->billing ['country'] ['iso_code_2'] );
		$Billstate = trim ( $order->billing ['state'] );
		$Billcity = trim ( $order->billing ['city'] );
		$Billemail = trim ( $order->customer ['email_address'] );
		$Billphone = trim ( $order->customer ['telephone'] );
		$Billpost = trim ( $order->billing ['postcode'] );
		//var_dump($order);
		//exit;
		$deliveryfirstname = $order->delivery ['firstname'] != '' ? $order->delivery ['firstname'] : $order->billing ['firstname'];
		$deliverylastname = $order->delivery ['lastname'] != '' ? $order->delivery ['lastname'] : $order->billing ['lastname'];
		$CName = trim ( $deliveryfirstname ) . trim ( $deliverylastname );
	
	   /*首先判断是否存在billing名*/
		$Issuer=trim ( $order->billing ['firstname'] )." ".trim ( $order->billing ['lastname'] );
		
		if(empty($Issuer)){  //不存在怎获取收货人名
			$Issuer=$CName;
		}
		
		if(empty($Issuer)){
			$Issuer="Aaron";
		}
		$strProduct = ''; // 传输的字符串
		for($i = 0; $i < sizeof ( $order->products ) && $i <= 50; $i ++) {
			$pname = $order->products [$i] ["name"];
			if ($pname == '') {
				$pname = 'Order ' . $orderid;
				break;
			}
			$strProduct = $strProduct . '&PName=' . $pname.'*'.($i + 1);
		}
		$Framework = 'zencart(4.5)';
		$BrowserUserAgent=$_POST ['BrowserUserAgent'];
		
		$md5src=MODULE_PAYMENT_MYORDERS_HASHKEY.MODULE_PAYMENT_MYORDERS_MERCHANTID.$orderid . $amount . $CurrCode;
		$signature = $this->szComputeMD5Hash($md5src);
	
		$process_button_string = 'IVersion=' . urlencode ( $version ) . '&URL=' . urlencode ( $URL ) . '&Language=' . urlencode ( $Language ) . '&AcctNo=' . urlencode ( MODULE_PAYMENT_MYORDERS_MERCHANTID ) . '&OrderID=' . urlencode ( $orderid ) .'&CurrCode=' . urlencode ( $CurrCode ) .'&CpCard=' . urlencode ($_POST ['CpCard'] ) . '&Amount=' . urlencode ( $amount ) . '&TxnType=' . urlencode ( $TxnType ) . '&callbackurl=' . urlencode ( $callbackurl ) .'&Issuer=' . urlencode ($Issuer) . '&BCountryCode=' . urlencode ( $BCountryCode ) . '&Framework=' . urlencode ( $Framework ) . $strProduct. '&BAddress=' . urlencode ( $BillAddress ) . '&Bcountry=' . urlencode ( $Bcountry ) . '&Bstate=' . urlencode ( $Billstate ) . '&BCity=' . urlencode ( $Billcity ).'&Email=' . urlencode ( $Billemail).'&Telephone=' . urlencode ( $Billphone ) . '&PostCode=' . urlencode ( $Billpost ) .'&BrowserUserAgent=' . urlencode ( $BrowserUserAgent ) .'&CName=' . urlencode ( $CName ) . '&CardPAN=' . urlencode ( $_POST ['my_cardNo'] ) . '&CVV2=' . urlencode ( $_POST ['my_cvv'] ) . '&ExpirationYear=' . urlencode ( substr($_POST ['my_expires_year'], 2) ) . '&ExpirationMonth=' . urlencode ( $_POST ['my_expires_month'] ) . '&IPAddress=' . urlencode($this->getIPaddress ()) . '&HashValue=' . urlencode ( $signature );
		return $process_button_string;
	}
	/**
     * 获取支付语言代码
     * @返回字符串
     */
    function get_language_code($language)
    {
       return $this->languageCode[strtolower($language)];
    }
	//MD5加密
    function szComputeMD5Hash($input)
    {
        $md5hex = md5($input);
        $len = strlen($md5hex) / 2;
        $md5raw = "";
        for ($i = 0; $i < $len; $i++) {
            $md5raw = $md5raw . chr(hexdec(substr($md5hex, $i * 2, 2)));
        }
        $keyMd5 = base64_encode($md5raw);
        return $keyMd5;
    }
	// 获取客户端的ip
	function getIPaddress() {
		$IPaddress = '';
		if (isset ( $_SERVER )) {
			if (isset ( $_SERVER ["HTTP_X_FORWARDED_FOR"] )) {
				$IPaddress = $_SERVER ["HTTP_X_FORWARDED_FOR"];
			} else if (isset ( $_SERVER ["HTTP_CLIENT_IP"] )) {
				$IPaddress = $_SERVER ["HTTP_CLIENT_IP"];
			} else {
				$IPaddress = $_SERVER ["REMOTE_ADDR"];
			}
		} else {
			if (getenv ( "HTTP_X_FORWARDED_FOR" )) {
				$IPaddress = getenv ( "HTTP_X_FORWARDED_FOR" );
			} else if (getenv ( "HTTP_CLIENT_IP" )) {
				$IPaddress = getenv ( "HTTP_CLIENT_IP" );
			} else {
				$IPaddress = getenv ( "REMOTE_ADDR" );
			}
		}
		$ips = explode ( ",", $IPaddress );
		return $ips [0];
	}
	function get_error() {
		$error = array (
				'title' => MODULE_PAYMENT_MYORDERS_TEXT_ERROR,
				'error' => stripslashes ( urldecode ( $_GET ['error'] ) ) 
		);
		
		return $error;
	}
	function check() {
		global $db;
		if (! isset ( $this->_check )) {
			$check_query = $db->Execute ( "select configuration_value from " . TABLE_CONFIGURATION . " where configuration_key = 'MODULE_PAYMENT_MYORDERS_STATUS'" );
			$this->_check = $check_query->RecordCount ();
		}
		return $this->_check;
	}
	function install() {
		global $db, $language, $module_type;
		if (! defined ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_1_1' )) {
			$lang_file = DIR_FS_CATALOG_LANGUAGES . $_SESSION ['language'] . '/modules/' . $module_type . '/' . $this->code . '.php';
			if (file_exists ( $lang_file )) {
				include ($lang_file);
			} else { // load default lang file
				include (DIR_FS_CATALOG_LANGUAGES . 'english' . '/modules/' . $module_type . '/' . $this->code . '.php');
			}
		}
//echo MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_1_1;
//exit;
		$db->Execute ("set names utf8");
		
		
		
		// 是否开启该模块
		$db->Execute ("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_1_1 . "', 'MODULE_PAYMENT_MYORDERS_STATUS', 'True', '" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_1_2 . "', '9', '1', 'zen_cfg_select_option(array(\'True\', \'False\'), ', now())" );
		// 交易号
		$db->Execute ("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_2_1 . "', 'MODULE_PAYMENT_MYORDERS_MERCHANTID', '', '" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_2_2 . "', '9', '2', now())" );
		// 交易密钥
		$db->Execute ("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_3_1 . "', 'MODULE_PAYMENT_MYORDERS_HASHKEY', '', '" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_3_2 . "', '9', '3', now())" );
		// 支付区域
		$db->Execute ("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, use_function, set_function, date_added) values ('" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_4_1 . "', 'MODULE_PAYMENT_MYORDERS_ZONE', '0', '" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_4_2 . "', '9', '4', 'zen_get_zone_class_title', 'zen_cfg_pull_down_zone_classes(', now())" );
		// 成功订单状态
		$db->Execute ("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, use_function, date_added) values ('" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_5_1 . "', 'MODULE_PAYMENT_MYORDERS_PROCESSING_STATUS_ID', '2', '" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_5_2 . "', '9', '5', 'zen_cfg_pull_down_order_statuses(', 'zen_get_order_status_name', now())" );
		// 初始化订单状态
		$db->Execute ("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, use_function, date_added) values ('" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_6_1 . "', 'MODULE_PAYMENT_MYORDERS_ORDER_STATUS_ID', '1', '" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_6_2 . "', '9', '6', 'zen_cfg_pull_down_order_statuses(', 'zen_get_order_status_name', now())" );
	    // 失败订单状态
		$db->Execute ("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, use_function, date_added) values ('" . '失败订单状态选择' . "', 'MODULE_PAYMENT_MYORDERS_FIAL_ORDER_STATUS', '1', '" .  '失败订单状态选择'. "', '9', '7', 'zen_cfg_pull_down_order_statuses(', 'zen_get_order_status_name', now())" );
	
		// 排序
		$db->Execute ("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_7_1 . "', 'MODULE_PAYMENT_MYORDERS_SORT_ORDER', '0', '" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_7_2 . "', '9', '8', now())" );
		// 是否有前缀
		$db->Execute ("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_8_1 . "', 'MODULE_PAYMENT_MYORDERS_ORDER_PREFIX', '', '" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_8_2 . "', '9', '9',  now())" );
		// 支付网关地址
		$db->Execute ("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_9_1 . "', 'MODULE_PAYMENT_MYORDERS_URL', '" . $this->form_action_url . "', '" . MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_9_2 . "', '9', '10', now())" );
	}
	function keys() {
		return array (
				'MODULE_PAYMENT_MYORDERS_STATUS',
				'MODULE_PAYMENT_MYORDERS_MERCHANTID',
				'MODULE_PAYMENT_MYORDERS_HASHKEY',
				'MODULE_PAYMENT_MYORDERS_ZONE',
				'MODULE_PAYMENT_MYORDERS_PROCESSING_STATUS_ID',
				'MODULE_PAYMENT_MYORDERS_ORDER_STATUS_ID',
				'MODULE_PAYMENT_MYORDERS_FIAL_ORDER_STATUS',
				'MODULE_PAYMENT_MYORDERS_SORT_ORDER',
				'MODULE_PAYMENT_MYORDERS_ORDER_PREFIX',
				'MODULE_PAYMENT_MYORDERS_URL' 
		);
	}
	function remove() {
		global $db;
		$db->Execute ( "delete from " . TABLE_CONFIGURATION . " where configuration_key LIKE  'MODULE_PAYMENT_MYORDERS%'" );
		$db->Execute ( "DROP TABLE IF EXISTS MYORDERS" );
	}
	
	function write_log($msg) {
		error_log ( date ( "[Y-m-d H:i:s]" ) . "\t" . $msg . "\r\n", 3, 'tf' . date ( "Y-m-d" ) . '.log' );
	}
	function getCurrencyCode($order)
    {
        if ($order->info['currency'] == 'ADP') {
            $v_moneytype = '020';
        } else if ($order->info['currency'] == 'AED') {
            $v_moneytype = '784';
        } else if ($order->info['currency'] == 'AFA') {
            $v_moneytype = '004';
        } else if ($order->info['currency'] == 'ALL') {
            $v_moneytype = '008';
        } else if ($order->info['currency'] == 'AMD') {
            $v_moneytype = '051';
        } else if ($order->info['currency'] == 'ANG') {
            $v_moneytype = '532';
        } else if ($order->info['currency'] == 'AOA') {
            $v_moneytype = '973';
        } else if ($order->info['currency'] == 'AON') {
            $v_moneytype = '024';
        } else if ($order->info['currency'] == 'ARS') {
            $v_moneytype = '032';
        } else if ($order->info['currency'] == 'ASF') {
            $v_moneytype = '999';
        } else if ($order->info['currency'] == 'ATS') {
            $v_moneytype = '040';
        } else if ($order->info['currency'] == 'AUD') {
            $v_moneytype = '036';
        } else if ($order->info['currency'] == 'AWG') {
            $v_moneytype = '533';
        } else if ($order->info['currency'] == 'AZM') {
            $v_moneytype = '031';
        } else if ($order->info['currency'] == 'BAM') {
            $v_moneytype = '977';
        } else if ($order->info['currency'] == 'BBD') {
            $v_moneytype = '052';
        } else if ($order->info['currency'] == 'BDT') {
            $v_moneytype = '050';
        } else if ($order->info['currency'] == 'BEF') {
            $v_moneytype = '056';
        } else if ($order->info['currency'] == 'BGL') {
            $v_moneytype = '100';
        } else if ($order->info['currency'] == 'BGN') {
            $v_moneytype = '975';
        } else if ($order->info['currency'] == 'BHD') {
            $v_moneytype = '048';
        } else if ($order->info['currency'] == 'BIF') {
            $v_moneytype = '108';
        } else if ($order->info['currency'] == 'BMD') {
            $v_moneytype = '060';
        } else if ($order->info['currency'] == 'BND') {
            $v_moneytype = '096';
        } else if ($order->info['currency'] == 'BOB') {
            $v_moneytype = '068';
        } else if ($order->info['currency'] == 'BOV') {
            $v_moneytype = '984';
        } else if ($order->info['currency'] == 'BRL') {
            $v_moneytype = '986';
        } else if ($order->info['currency'] == 'BSD') {
            $v_moneytype = '044';
        } else if ($order->info['currency'] == 'BTN') {
            $v_moneytype = '064';
        } else if ($order->info['currency'] == 'BWP') {
            $v_moneytype = '072';
        } else if ($order->info['currency'] == 'BYB') {
            $v_moneytype = '112';
        } else if ($order->info['currency'] == 'BYR') {
            $v_moneytype = '974';
        } else if ($order->info['currency'] == 'BZD') {
            $v_moneytype = '084';
        } else if ($order->info['currency'] == 'CAD') {
            $v_moneytype = '124';
        } else if ($order->info['currency'] == 'CDF') {
            $v_moneytype = '976';
        } else if ($order->info['currency'] == 'CHF') {
            $v_moneytype = '756';
        } else if ($order->info['currency'] == 'CLF') {
            $v_moneytype = '990';
        } else if ($order->info['currency'] == 'CLP') {
            $v_moneytype = '152';
        } else if ($order->info['currency'] == 'CNY') {
            $v_moneytype = '156';
        } else if ($order->info['currency'] == 'COP') {
            $v_moneytype = '170';
        } else if ($order->info['currency'] == 'CRC') {
            $v_moneytype = '188';
        } else if ($order->info['currency'] == 'CUP') {
            $v_moneytype = '192';
        } else if ($order->info['currency'] == 'CVE') {
            $v_moneytype = '132';
        } else if ($order->info['currency'] == 'CYP') {
            $v_moneytype = '196';
        } else if ($order->info['currency'] == 'CZK') {
            $v_moneytype = '203';
        } else if ($order->info['currency'] == 'DEM') {
            $v_moneytype = '280';
        } else if ($order->info['currency'] == 'DJF') {
            $v_moneytype = '262';
        } else if ($order->info['currency'] == 'DKK') {
            $v_moneytype = '208';
        } else if ($order->info['currency'] == 'DOP') {
            $v_moneytype = '214';
        } else if ($order->info['currency'] == 'DZD') {
            $v_moneytype = '012';
        } else if ($order->info['currency'] == 'ECS') {
            $v_moneytype = '218';
        } else if ($order->info['currency'] == 'ECV') {
            $v_moneytype = '983';
        } else if ($order->info['currency'] == 'EEK') {
            $v_moneytype = '233';
        } else if ($order->info['currency'] == 'EGP') {
            $v_moneytype = '818';
        } else if ($order->info['currency'] == 'ERN') {
            $v_moneytype = '232';
        } else if ($order->info['currency'] == 'ESP') {
            $v_moneytype = '724';
        } else if ($order->info['currency'] == 'ETB') {
            $v_moneytype = '230';
        } else if ($order->info['currency'] == 'EUR') {
            $v_moneytype = '978';
        } else if ($order->info['currency'] == 'FIM') {
            $v_moneytype = '246';
        } else if ($order->info['currency'] == 'FJD') {
            $v_moneytype = '242';
        } else if ($order->info['currency'] == 'FKP') {
            $v_moneytype = '238';
        } else if ($order->info['currency'] == 'FRF') {
            $v_moneytype = '250';
        } else if ($order->info['currency'] == 'GBP') {
            $v_moneytype = '826';
        } else if ($order->info['currency'] == 'GEL') {
            $v_moneytype = '981';
        } else if ($order->info['currency'] == 'GHC') {
            $v_moneytype = '288';
        } else if ($order->info['currency'] == 'GIP') {
            $v_moneytype = '292';
        } else if ($order->info['currency'] == 'GMD') {
            $v_moneytype = '270';
        } else if ($order->info['currency'] == 'GNF') {
            $v_moneytype = '324';
        } else if ($order->info['currency'] == 'GRD') {
            $v_moneytype = '300';
        } else if ($order->info['currency'] == 'GTQ') {
            $v_moneytype = '320';
        } else if ($order->info['currency'] == 'GWP') {
            $v_moneytype = '624';
        } else if ($order->info['currency'] == 'GYD') {
            $v_moneytype = '328';
        } else if ($order->info['currency'] == 'HKD') {
            $v_moneytype = '344';
        } else if ($order->info['currency'] == 'HNL') {
            $v_moneytype = '340';
        } else if ($order->info['currency'] == 'HRK') {
            $v_moneytype = '191';
        } else if ($order->info['currency'] == 'HTG') {
            $v_moneytype = '332';
        } else if ($order->info['currency'] == 'HUF') {
            $v_moneytype = '348';
        } else if ($order->info['currency'] == 'IDR') {
            $v_moneytype = '360';
        } else if ($order->info['currency'] == 'IEP') {
            $v_moneytype = '372';
        } else if ($order->info['currency'] == 'ILS') {
            $v_moneytype = '376';
        } else if ($order->info['currency'] == 'INR') {
            $v_moneytype = '356';
        } else if ($order->info['currency'] == 'IRR') {
            $v_moneytype = '364';
        } else if ($order->info['currency'] == 'ISK') {
            $v_moneytype = '352';
        } else if ($order->info['currency'] == 'ITL') {
            $v_moneytype = '380';
        } else if ($order->info['currency'] == 'JMD') {
            $v_moneytype = '388';
        } else if ($order->info['currency'] == 'JOD') {
            $v_moneytype = '400';
        } else if ($order->info['currency'] == 'JPY') {
            $v_moneytype = '392';
        } else if ($order->info['currency'] == 'KES') {
            $v_moneytype = '404';
        } else if ($order->info['currency'] == 'KGS') {
            $v_moneytype = '417';
        } else if ($order->info['currency'] == 'KHR') {
            $v_moneytype = '116';
        } else if ($order->info['currency'] == 'KMF') {
            $v_moneytype = '174';
        } else if ($order->info['currency'] == 'KPW') {
            $v_moneytype = '408';
        } else if ($order->info['currency'] == 'KRW') {
            $v_moneytype = '410';
        } else if ($order->info['currency'] == 'KWD') {
            $v_moneytype = '414';
        } else if ($order->info['currency'] == 'KYD') {
            $v_moneytype = '136';
        } else if ($order->info['currency'] == 'KZT') {
            $v_moneytype = '398';
        } else if ($order->info['currency'] == 'LAK') {
            $v_moneytype = '418';
        } else if ($order->info['currency'] == 'LBP') {
            $v_moneytype = '422';
        } else if ($order->info['currency'] == 'LKR') {
            $v_moneytype = '144';
        } else if ($order->info['currency'] == 'LRD') {
            $v_moneytype = '430';
        } else if ($order->info['currency'] == 'LSL') {
            $v_moneytype = '426';
        } else if ($order->info['currency'] == 'LTL') {
            $v_moneytype = '440';
        } else if ($order->info['currency'] == 'LUF') {
            $v_moneytype = '442';
        } else if ($order->info['currency'] == 'LVL') {
            $v_moneytype = '428';
        } else if ($order->info['currency'] == 'LYD') {
            $v_moneytype = '434';
        } else if ($order->info['currency'] == 'MAD') {
            $v_moneytype = '504';
        } else if ($order->info['currency'] == 'MDL') {
            $v_moneytype = '498';
        } else if ($order->info['currency'] == 'MGF') {
            $v_moneytype = '450';
        } else if ($order->info['currency'] == 'MKD') {
            $v_moneytype = '807';
        } else if ($order->info['currency'] == 'MMK') {
            $v_moneytype = '104';
        } else if ($order->info['currency'] == 'MNT') {
            $v_moneytype = '496';
        } else if ($order->info['currency'] == 'MOP') {
            $v_moneytype = '446';
        } else if ($order->info['currency'] == 'MRO') {
            $v_moneytype = '478';
        } else if ($order->info['currency'] == 'MTL') {
            $v_moneytype = '470';
        } else if ($order->info['currency'] == 'MUR') {
            $v_moneytype = '480';
        } else if ($order->info['currency'] == 'MVR') {
            $v_moneytype = '462';
        } else if ($order->info['currency'] == 'MWK') {
            $v_moneytype = '454';
        } else if ($order->info['currency'] == 'MXN') {
            $v_moneytype = '484';
        } else if ($order->info['currency'] == 'MXV') {
            $v_moneytype = '979';
        } else if ($order->info['currency'] == 'MYR') {
            $v_moneytype = '458';
        } else if ($order->info['currency'] == 'MZM') {
            $v_moneytype = '508';
        } else if ($order->info['currency'] == 'NAD') {
            $v_moneytype = '516';
        } else if ($order->info['currency'] == 'NGN') {
            $v_moneytype = '566';
        } else if ($order->info['currency'] == 'NIO') {
            $v_moneytype = '558';
        } else if ($order->info['currency'] == 'NLG') {
            $v_moneytype = '528';
        } else if ($order->info['currency'] == 'NOK') {
            $v_moneytype = '578';
        } else if ($order->info['currency'] == 'NPR') {
            $v_moneytype = '524';
        } else if ($order->info['currency'] == 'NZD') {
            $v_moneytype = '554';
        } else if ($order->info['currency'] == 'OMR') {
            $v_moneytype = '512';
        } else if ($order->info['currency'] == 'PAB') {
            $v_moneytype = '590';
        } else if ($order->info['currency'] == 'PEN') {
            $v_moneytype = '604';
        } else if ($order->info['currency'] == 'PGK') {
            $v_moneytype = '598';
        } else if ($order->info['currency'] == 'PHP') {
            $v_moneytype = '608';
        } else if ($order->info['currency'] == 'PKR') {
            $v_moneytype = '586';
        } else if ($order->info['currency'] == 'PLN') {
            $v_moneytype = '985';
        } else if ($order->info['currency'] == 'PLZ') {
            $v_moneytype = '616';
        } else if ($order->info['currency'] == 'PTE') {
            $v_moneytype = '620';
        } else if ($order->info['currency'] == 'PYG') {
            $v_moneytype = '600';
        } else if ($order->info['currency'] == 'QAR') {
            $v_moneytype = '634';
        } else if ($order->info['currency'] == 'ROL') {
            $v_moneytype = '642';
        } else if ($order->info['currency'] == 'RSD') {
            $v_moneytype = '941';
        } else if ($order->info['currency'] == 'RUB') {
            $v_moneytype = '810';
        } else if ($order->info['currency'] == 'RWF') {
            $v_moneytype = '646';
        } else if ($order->info['currency'] == 'SAR') {
            $v_moneytype = '682';
        } else if ($order->info['currency'] == 'SBD') {
            $v_moneytype = '090';
        } else if ($order->info['currency'] == 'SCR') {
            $v_moneytype = '690';
        } else if ($order->info['currency'] == 'SDD') {
            $v_moneytype = '736';
        } else if ($order->info['currency'] == 'SDR') {
            $v_moneytype = '000';
        } else if ($order->info['currency'] == 'SEK') {
            $v_moneytype = '752';
        } else if ($order->info['currency'] == 'SGD') {
            $v_moneytype = '702';
        } else if ($order->info['currency'] == 'SHP') {
            $v_moneytype = '654';
        } else if ($order->info['currency'] == 'SIT') {
            $v_moneytype = '705';
        } else if ($order->info['currency'] == 'SKK') {
            $v_moneytype = '703';
        } else if ($order->info['currency'] == 'SLL') {
            $v_moneytype = '694';
        } else if ($order->info['currency'] == 'SOS') {
            $v_moneytype = '706';
        } else if ($order->info['currency'] == 'SRG') {
            $v_moneytype = '740';
        } else if ($order->info['currency'] == 'STD') {
            $v_moneytype = '678';
        } else if ($order->info['currency'] == 'SVC') {
            $v_moneytype = '222';
        } else if ($order->info['currency'] == 'SYP') {
            $v_moneytype = '760';
        } else if ($order->info['currency'] == 'SZL') {
            $v_moneytype = '748';
        } else if ($order->info['currency'] == 'THB') {
            $v_moneytype = '764';
        } else if ($order->info['currency'] == 'TJR') {
            $v_moneytype = '762';
        } else if ($order->info['currency'] == 'TJS') {
            $v_moneytype = '972';
        } else if ($order->info['currency'] == 'TMM') {
            $v_moneytype = '795';
        } else if ($order->info['currency'] == 'TND') {
            $v_moneytype = '788';
        } else if ($order->info['currency'] == 'TOP') {
            $v_moneytype = '776';
        } else if ($order->info['currency'] == 'TRL') {
            $v_moneytype = '792';
        } else if ($order->info['currency'] == 'TTD') {
            $v_moneytype = '780';
        } else if ($order->info['currency'] == 'TWD') {
            $v_moneytype = '901';
        } else if ($order->info['currency'] == 'TZS') {
            $v_moneytype = '834';
        } else if ($order->info['currency'] == 'UAH') {
            $v_moneytype = '980';
        } else if ($order->info['currency'] == 'UAK') {
            $v_moneytype = '804';
        } else if ($order->info['currency'] == 'UGX') {
            $v_moneytype = '800';
        } else if ($order->info['currency'] == 'USD') {
            $v_moneytype = '840';
        } else if ($order->info['currency'] == 'USN') {
            $v_moneytype = '997';
        } else if ($order->info['currency'] == 'USS') {
            $v_moneytype = '998';
        } else if ($order->info['currency'] == 'UYU') {
            $v_moneytype = '858';
        } else if ($order->info['currency'] == 'UZS') {
            $v_moneytype = '860';
        } else if ($order->info['currency'] == 'VEB') {
            $v_moneytype = '862';
        } else if ($order->info['currency'] == 'VND') {
            $v_moneytype = '704';
        } else if ($order->info['currency'] == 'VUV') {
            $v_moneytype = '548';
        } else if ($order->info['currency'] == 'WST') {
            $v_moneytype = '882';
        } else if ($order->info['currency'] == 'XAF') {
            $v_moneytype = '950';
        } else if ($order->info['currency'] == 'XAG') {
            $v_moneytype = '961';
        } else if ($order->info['currency'] == 'XAU') {
            $v_moneytype = '959';
        } else if ($order->info['currency'] == 'XBA') {
            $v_moneytype = '955';
        } else if ($order->info['currency'] == 'XBB') {
            $v_moneytype = '956';
        } else if ($order->info['currency'] == 'XBC') {
            $v_moneytype = '957';
        } else if ($order->info['currency'] == 'XBD') {
            $v_moneytype = '958';
        } else if ($order->info['currency'] == 'XCD') {
            $v_moneytype = '951';
        } else if ($order->info['currency'] == 'XDR') {
            $v_moneytype = '960';
        } else if ($order->info['currency'] == 'XEU') {
            $v_moneytype = '954';
        } else if ($order->info['currency'] == 'XOF') {
            $v_moneytype = '952';
        } else if ($order->info['currency'] == 'XPD') {
            $v_moneytype = '964';
        } else if ($order->info['currency'] == 'XPF') {
            $v_moneytype = '953';
        } else if ($order->info['currency'] == 'XPT') {
            $v_moneytype = '962';
        } else if ($order->info['currency'] == 'XTS') {
            $v_moneytype = '963';
        } else if ($order->info['currency'] == 'XXX') {
            $v_moneytype = '999';
        } else if ($order->info['currency'] == 'YER') {
            $v_moneytype = '886';
        } else if ($order->info['currency'] == 'YUM') {
            $v_moneytype = '891';
        } else if ($order->info['currency'] == 'YUN') {
            $v_moneytype = '890';
        } else if ($order->info['currency'] == 'ZAL') {
            $v_moneytype = '991';
        } else if ($order->info['currency'] == 'ZAR') {
            $v_moneytype = '710';
        } else if ($order->info['currency'] == 'ZMK') {
            $v_moneytype = '894';
        } else if ($order->info['currency'] == 'ZRN') {
            $v_moneytype = '180';
        } else if ($order->info['currency'] == 'ZWD') {
            $v_moneytype = '716';
        }
        return $v_moneytype;

    }
}
?>