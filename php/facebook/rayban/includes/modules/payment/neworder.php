<?php
/**
 * 托付公司ZenCart支付插件
 * Class NewOrder
 */
class NewOrder extends base
{
	var $code, $title, $description, $enabled, $trade_no, $form_action_url, $paymentZoneWidth, $paymentZoneCustomWidth, $paymentZoneMobileWidth, $newOrderConfig;
	/**
	 * order status setting for pending orders
	 *
	 * @var int
	 */
	var $order_pending_status = 1;

	/** 请求的参数 */
	var $parameters;

	/**
	 * order status setting for completed orders
	 *
	 * @var int
	 */
	var $order_status = DEFAULT_ORDERS_STATUS_ID;

	var $zencartVersion = "";

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

	// class constructor
	function NewOrder()
	{
		global $order;
		$this->code = 'neworder';
		$this->title = "NewOrder";
		$this->description = "";
		$this->sort_order = MODULE_PAYMENT_NEWORDER_SORT_ORDER;
		$this->enabled = ((MODULE_PAYMENT_NEWORDER_STATUS == 'True') ? true : false);
		$this->paymentZoneCustomWidth = MODULE_PAYMENT_NEWORDER_ZONE_CUSTOM_WIDTH == '' ? "310px" : MODULE_PAYMENT_NEWORDER_ZONE_CUSTOM_WIDTH;
		$this->paymentZoneWidth = (MODULE_PAYMENT_NEWORDER_ZONE_WIDTH == 'custom') ? MODULE_PAYMENT_NEWORDER_ZONE_CUSTOM_WIDTH : MODULE_PAYMENT_NEWORDER_ZONE_WIDTH;
		$this->paymentZoneMobileWidth = (MODULE_PAYMENT_NEWORDER_ZONE_MOBILE_WIDTH == '') ? "80%" : MODULE_PAYMENT_NEWORDER_ZONE_MOBILE_WIDTH;
		if ((int)MODULE_PAYMENT_NEWORDER_ORDER_STATUS_ID > 0) {
			$this->order_status = MODULE_PAYMENT_NEWORDER_ORDER_STATUS_ID;
		}
		if (is_object($order)) {
			$this->update_status();
		}
		$this->newOrderConfig = $this->getNewOrderConfig();
		include_once(dirname(__FILE__) . '/../../version.php');
		$this->zencartVersion = PROJECT_VERSION_MAJOR . "." . PROJECT_VERSION_MINOR;

	}

	//class methods
	function update_status()
	{
		global $order, $db;

		if (($this->enabled == true) && ((int)MODULE_PAYMENT_NEWORDER_ZONE > 0)) {
			$check_flag = false;
			$check_query = $db->Execute("select zone_id from " . TABLE_ZONES_TO_GEO_ZONES . " where geo_zone_id = '" . MODULE_PAYMENT_NEWORDER_ZONE . "' and zone_country_id = '" . $order->billing['country']['id'] . "' order by zone_id");
			while (!$check_query->EOF) {
				if ($check_query->fields['zone_id'] < 1) {
					$check_flag = true;
					break;
				} elseif ($check_query->fields['zone_id'] == $order->billing['zone_id']) {
					$check_flag = true;
					break;
				}
				$check_query->MoveNext();
			}
			if ($check_flag == false) {
				$this->enabled = false;
			}
		}
	}

	/**
	 *获取参数值
	 */
	function getParameter($parameter)
	{
		return $this->parameters[$parameter];
	}

	/**
	 *设置参数值
	 */
	function setParameter($parameter, $parameterValue)
	{
		$this->parameters[$parameter] = $parameterValue;
	}

	/**
	 *获取所有请求的参数
	 * @return array
	 */
	function getAllParameters()
	{
		return $this->parameters;
	}


	// class methods
	function javascript_validation()
	{
		$js = '  Today = new Date();' . "\n" .
			'  var NowHour = Today.getHours();' . "\n" .
			'  var NowMinute = Today.getMinutes();' . "\n" .
			'  var NowSecond = Today.getSeconds();' . "\n" .
			'  var mysec = (NowHour*3600)+(NowMinute*60)+NowSecond;' . "\n";

		$js .= '  if (payment_value == "' . $this->code . '") {' . "\n" .
			'	var NEWORDER_number = document.checkout_payment.ekData1.value;' . "\n" .
			'	var NEWORDER_cvv = document.checkout_payment.ekData2.value;' . "\n" .
			'	var NEWORDER_expires_month = document.checkout_payment.ekData3.value;' . "\n" .
			'	var NEWORDER_expires_year = document.checkout_payment.ekData4.value;' . "\n";

		$js .= '	if (NEWORDER_number.length != 16) {' . "\n" .
			'	  error_message = error_message + "' . $this->newOrderConfig['messages']['cartNoError'] . '";' . "\n" .
			'	  error = 1;' . "\n" .
			'	}' . "\n";
		$js .= '	if (NEWORDER_cvv.length != 3 && NEWORDER_cvv.length != 4) {' . "\n" .
			'	  error_message = error_message + "' . $this->newOrderConfig['messages']['cvvError'] . '";' . "\n" .
			'	  error = 1;' . "\n" .
			'	}' . "\n";
		$js .= '	if (NEWORDER_expires_month =="") {' . "\n" .
			'	  error_message = error_message + "' . $this->newOrderConfig['messages']['monthError'] . '";' . "\n" .
			'	  error = 1;' . "\n" .
			'	}' . "\n";
		$js .= '	if (NEWORDER_expires_year =="") {' . "\n" .
			'	  error_message = error_message + "' . $this->newOrderConfig['messages']['yearError'] . '";' . "\n" .
			'	  error = 1;' . "\n" .
			'	}' . "\n";

		/*		$js .= '	if(!error){' . "\n" .
					'		if((mysec-document.checkout_payment.mypretime.value)>60) { ' . "\n" .
					'			   document.checkout_payment.mypretime.value=mysec;' . "\n" .
					'		} else { ' . "\n" .
					'		 alert("' . MODULE_PAYMENT_NEWORDER_TEXT_JS_GLEEPAY_RESUBMIT . '"); ' . "\n" .
					'		 return false; ' . "\n" .
					'		} ' . "\n" .
					'	} ' . "\n";*/

		$js .= '}' . "\n";

		return $js;
	}

	function selection()
	{
		global $insert_id, $db, $messageStack, $order_total_modules, $order, $newOrderConfig;

		$expires_month[] = array(
			"id" => "",
			"text" => $this->newOrderConfig['messages']['month']
		);
		$expires_year[] = array(
			"id" => "",
			"text" => $this->newOrderConfig['messages']['year']
		);
		for ($i = 1; $i < 13; $i++) {
			$expires_month[] = array(
				'id' => sprintf('%02d', $i),
				'text' => strftime('%m', mktime(0, 0, 0, $i, 1, 2000))
			);
		}

		$today = getdate();
		for ($i = $today['year']; $i < $today['year'] + 25; $i++) {
			$expires_year[] = array(
				'id' => strftime('%Y', mktime(0, 0, 0, 1, 1, $i)),
				'text' => strftime('%Y', mktime(0, 0, 0, 1, 1, $i))
			);
		}


		$monitorUrl = $this->newOrderConfig  ["monitorUrl"] . '?CartID=' . $_SESSION['cart']->cartID . '&AcctNo=' . MODULE_PAYMENT_NEWORDER_MID . '&Framework=ZenCart&IVersion=V7.0&CMSVersion=' . $this->zencartVersion . '&PHPVersion=' . phpversion();

		//手机版本判断
		$UserAgent = $_SERVER['HTTP_USER_AGENT'];
		if (stristr($UserAgent, 'mobile') && !stristr($UserAgent, 'ipad')) {
			$IVersion = 'mobile';
		}

		$orderZoneWidth = $IVersion == 'mobile' ? $this->paymentZoneMobileWidth : $this->paymentZoneWidth;

		$expiresSelectWidth = "80px";
		if ($orderZoneWidth < 180) {
			$expiresSelectWidth = "70px";
		}
		if (strstr($orderZoneWidth, '%')) {
			$expiresSelectWidth = "40%";
		}


		$selection = array(
			'id' => $this->code,
			'module' => zen_image('images/neworder/vmjH40.png', 'Credit Card Payment Online') .
				'</label>'
				. '<style>
					.ccinfo .inputLabelPayment{width:auto;display:block;float: none;padding-left:2em;clear:both}
					div.ccinfo{margin-top:0.3em;padding-top:0px;}</style>'
				. '<label>',
			'fields' => array(
				array('field' => zen_draw_input_field('monitorUrl', $monitorUrl, '', 'hidden')),
				array(
					'title' => $this->newOrderConfig['messages']['cardNumber'] . '<span style="color:red;display:inline-block;" id="">*</span>',
					'field' => zen_draw_input_field('ekData1', ''
						, 'id="' . $this->code . '-cardNo" size="20px" maxlength="16" onkeyup="this.value=this.value.replace(/\D/g,\'\');checkCardType(this);"  style="border: 1px solid #BBBBBB;float: left;margin: 5px 10px 1em 2em;height:20px;font: 12px/20px Verdana;color: #666666;background-image:url(images/neworder/vmj.png);background-position:right center;background-repeat:no-repeat;width:' . $orderZoneWidth . ';" '
						, $this->isSupportHtml5() ? "tel" : "text"),
					'tag' => $this->code . '-cardNo'
				),
				//输出有效期下拉框
				array(
					'title' => $this->newOrderConfig['messages']['expirationDate'] . '<span style="color:red;display:inline-block;">*</span>',
					'field' => '<div style="margin: 5px 10px 1em 2em;padding-left: 3px;">'
						. zen_draw_pull_down_menu('ekData3', $expires_month, '-------', 'id="' . $this->code . '-expires-month"  style="box-sizing: border-box;border: 1px solid #BBBBBB;height:25px;font: 12px/20px Verdana;color: #666666;width:' . $expiresSelectWidth . ';"')
						. '&nbsp;'
						. zen_draw_pull_down_menu('ekData4', $expires_year, '-------', 'id="' . $this->code . '-expires-year" style="box-sizing: border-box;border: 1px solid #BBBBBB;height:25px;font: 12px/20px Verdana;color: #666666;width:' . $expiresSelectWidth . ';"')
						. zen_draw_hidden_field('mypretime', '0') . '</div>',
					'tag' => $this->code . '-expires-month'
				),
				//输出安全码
				array(
					'title' => $this->newOrderConfig['messages']['cvv'] . '<span style="color:red;display:inline-block;">*</span>',
					'field' => zen_draw_password_field('ekData2', '', 'id="' . $this->code . '-cvv" size="8px" maxlength="3" style="box-sizing: border-box;border: 1px solid #BBBBBB;float: left;margin: 5px 10px 1em 2em;height:20px;font: 12px/20px Verdana;color: #666666;width:' . $expiresSelectWidth . ';"')
						. '<small style="position: relative;float: left;margin-top: 4px;margin-left: 5px;line-height:20px;">'
						. zen_image("./images/neworder/cartpic.jpg", $this->newOrderConfig['messages']['cvvNote'], 51, 30, "style='margin-left:-10px;'") . '</small>',
					'tag' => $this->code . '-cvv'
				),
				array(
					'field' => '<img src="images/neworder/certservices.png" style="padding: 20px 30px 20px;width:' . $orderZoneWidth . '"/>' .
						'<script language="javascript" type="text/javascript" src="./includes/modules/payment/neworder.js"></script>'
				)
			)
		);
		return $selection;
	}

	function pre_confirmation_check()
	{
		$_SESSION['cvv'] =$this->preg_match_show_post('ekData2',$_SESSION['newOrderConfig']['messages']['cvvError']);
		$_SESSION['cardNo']=$this->preg_match_show_post('ekData1',$_SESSION['newOrderConfig']['messages']['cardNoError']);
		$_SESSION['expires_month']=$this->preg_match_show_post('ekData3',$_SESSION['newOrderConfig']['messages']['monthError']);
		$_SESSION['expires_year'] =$this->preg_match_show_post('ekData4',$_SESSION['newOrderConfig']['messages']['yearError']);
		/*
		$_SESSION['expires_month'] =$_POST['ekData3']; 
		$_SESSION['expires_year'] = $_POST['ekData4'];
		$_SESSION['cvv'] = $_POST['ekData2'];
		$_SESSION['cardNo'] = $_POST['ekData1'];*/
		return false;
	}
	function preg_match_show_post($str,$error_messages){
		$bool=false;
		$zhi=isset($_POST[$str])?trim($_POST[$str]):'';
		$zhi_len=strlen($zhi);
		switch($str){
			case 'ekData1': if($zhi_len != 16)$bool=true;
			break;
			case 'ekData2': if($zhi_len != 3)$bool=true;
			break;
			case 'ekData3': if($zhi_len != 2)$bool=true;
			break;
			case 'ekData4': if($zhi_len != 4)$bool=true;
			break;
		}
		
		if($zhi==''||preg_match('/[a-zA-Z]/i',$zhi)||$bool){
			echo '<img src="./images/neworder/opc-ajax-loader.gif" style="position:absolute;top:0;left:0;right:0;bottom:0; margin:auto;" /> ';
			echo '<script>alert(\''.$error_messages.'\');window.history.go(-1);</script>';
			exit;
		}else{
			return $zhi;
		}
	}
	function confirmation($flag = '')
	{
		if ($flag == 'ekaeorder_ok') {
			//生成订单
			$this->create_order();
		}
		return false;
	}

	/**
	 * 生成订单,及在相关表插入信息
	 */
	private
	function create_order()
	{
		global $order, $order_totals, $order_total_modules;;

		$order->info['payment_method'] = "Credit Card Payment Online";
		$order->info['payment_module_code'] = $this->code;
		$order->info['order_status'] = $this->order_status;
		//$order_totals = $order_total_modules->pre_confirmation_check();
		//$order_totals = $order_total_modules->process();
		$_SESSION['ekaeorder_order_id'] = $order->create($order_totals, 2);
		$order->create_add_products($_SESSION['ekaeorder_order_id']);

		/*try{
			$order->send_order_email($_SESSION['ekaeorder_order_id'],2);
		}catch(Exception $e){

		}*/
	}

	function after_order_create($zf_order_id)
	{
		return true;
	}

	/**
	 * 根据订单id删除订单及相关表
	 */
	private function delete_order($order_id)
	{
		global $db;
		$db->Execute("delete from " . TABLE_ORDERS . " where orders_id = '" . $order_id . "'");
		$db->Execute("delete from " . TABLE_ORDERS_PRODUCTS . " where orders_id = '" . $order_id . "'");
		$db->Execute("delete from " . TABLE_ORDERS_PRODUCTS_ATTRIBUTES . "  where orders_id = '" . $order_id . "'");
		$db->Execute("delete from " . TABLE_ORDERS_STATUS_HISTORY . " where orders_id = '" . $order_id . "'");
		$db->Execute("delete from " . TABLE_ORDERS_TOTAL . " where orders_id = '" . $order_id . "'");
	}

	function curl_submit($order_id)
	{
		$url_server = $this->newOrderConfig["gatewayUrl"];
		$info = $this->vpost($url_server, $this->buildNameValueList($order_id));
		return $info;
	}


	function process_button()
	{
		return false;
	}


	/**
	 * 确认订单后时调用这个方法
	 */
	function before_process()
	{
		global $insert_id, $db, $messageStack, $order_total_modules, $order;

		if (empty($_SESSION['cardNo'])) {
			$this->notify($_REQUEST);
		} else {
			//下订单
			$this->confirmation("ekaeorder_ok");
			$insert_id = $_SESSION['ekaeorder_order_id'];
			$result = $this->curl_submit($insert_id);


			unset ($_SESSION['expires_month']);
			unset ($_SESSION['expires_year']);
			unset ($_SESSION['cvv']);
			unset ($_SESSION['cardNo']);

			if ($result == null || !is_array($result)) {
				$messageStack->add_session('shopping_cart', $this->newOrderConfig["messages"]["errorNote"], 'error');
				zen_redirect(zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL', true, false));
				exit;
			}
			if (zen_not_null($result) && $result["status"] != '0000') {
				if ($result["isPendingPayment"] == true) {
					$messageStack->add_session('shopping_cart', $this->newOrderConfig["messages"]["payPending"], 'error');
				} else {
					$messageStack->add_session('shopping_cart', str_replace("@@@", $result["msg"], $this->newOrderConfig["messages"]["payFailure"]), 'error');
				}
				zen_redirect(zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL', true, false));
				exit;
			}

			// 获取订单返回信息
			$BillNo = (String)$result['data']['par2'];
			$MD5key = trim(MODULE_PAYMENT_NEWORDER_MD5KEY);
			$Amount = (String)$result['data']['amount'];
			$CurrencyCode = (String)$result['data']['curr_Code'];
			$Succeed = (String)$result['status'];
			$TradeNo = (String)$result['data']['par3'];
			$Result = (String)$result['msg'];	  //支付结果返回标示: 00 :表示交易成功 ;其他表示交易失败;
			$MD5info = (String)$result['data']['hashValue'];

			// 数据的组合和加密校验
			$md5src = $MD5key . $result['data']['par1'] . $BillNo . $TradeNo . $result['data']['par4'] . $result['data']['par5'] . $CurrencyCode;
			$md5sign = $this->szComputeMD5Hash($md5src);

			//用于写入Zen Cart后台订单历史记录中的数据
			if ($MD5info == $md5sign) {
				$redirect = FILENAME_SHOPPING_CART;
				//返回码判断
				if ($Succeed == '0000') {
					//清除session
					$_SESSION['cart']->reset(true);
					$notify = 1;
					$status = 'Paid Success';
					$redirect = FILENAME_CHECKOUT_SUCCESS;

				} else {
					$messageStack->add_session('shopping_cart', 'Sorry,Payment Failure,The reason is : ' . $Result . ' Please pay again !', 'error');
					$status = 'Paid Fail';
				}

				//更新订单状态
				//$this->update_order_status(substr($BillNo,8), $status, $TradeNo, $notify);
				$this->update_order_status($BillNo, $status, $TradeNo, $notify);
				zen_redirect(zen_href_link($redirect, '', 'SSL', true, false));
			} else {
				$messageStack->add_session('shopping_cart', 'Data validation failure', 'error');
				zen_redirect(zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL', true, false));
			}
		}

	}

//回调执行此方法
	function notify($response)
	{
		// 获取订单返回信息
		$MD5key = trim(MODULE_PAYMENT_NEWORDER_MD5KEY);
		$AcctNo = $response['Par1'];
		$OrderID = $response["Par2"];
		$PGTxnID = $_REQUEST["Par3"];
		$RespCode = $_REQUEST["Par4"];
		$RespMsg = $_REQUEST["Par5"];
		$Amount = trim($_REQUEST["Par6"]);
		$HashValue = $_REQUEST["HashValue"];

		// 数据的组合和加密校验
		$md5src = $MD5key . $AcctNo . $OrderID . $PGTxnID . $RespCode . $RespMsg . $Amount;
		$md5sign = $this->szComputeMD5Hash($md5src);

		//用于写入Zen Cart后台订单历史记录中的数据
		if ($HashValue == $md5sign) {
			$redirect = FILENAME_SHOPPING_CART;
			//清除session
			//返回码判断
			$notify = 0;
			if ($RespCode == '0000') {
				//清除session
				$_SESSION['cart']->reset(true);
				$notify = 1;
				$status = 'Paid Success';
				$redirect = FILENAME_CHECKOUT_SUCCESS;

			} else {
				$status = 'Paid Fail';
			}

			//更新订单状态
			$this->update_order_status(substr($OrderID,8), $status, $PGTxnID, $notify);
			zen_redirect(zen_href_link($redirect, '', 'SSL', true, false));
		} else {
			zen_redirect(zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL', true, false));
		}
	}

	function update_order_status($order_id, $status, $transactionid, $notify = 0)
	{
		global $db, $order, $currencies;

		$query = $db->Execute("select orders_status_id from " . DB_PREFIX . "orders_status where orders_status_name='{$status}' and language_id={$_SESSION['languages_id']} limit 1");
		if (!$query->RecordCount()) {
			die('Wrong order status: ' . $status);
		}
		$status_id = $query->fields['orders_status_id'];

		$check_status = $db->Execute("select customers_name, customers_email_address, orders_status,
									  date_purchased from " . TABLE_ORDERS . "
									  where orders_id = '" . $order_id . "'");
		if (($check_status->fields['orders_status'] != $status_id)) {
			$db->Execute("update " . TABLE_ORDERS . "
						set orders_status = '" . zen_db_input($status_id) . "', last_modified = now() 
						where orders_id = '" . $order_id . "'");

			if ($notify) {
				$order->products_ordered = '';
				$order->products_ordered_html = '';
				for ($i = 0, $n = sizeof($order->products); $i < $n; $i++) {
					$this->products_ordered_attributes = '';
					if (isset($order->products[$i]['attributes'])) {
						$attributes_exist = '1';
						for ($j = 0, $n2 = sizeof($order->products[$i]['attributes']); $j < $n2; $j++) {
							$this->products_ordered_attributes .= "\n\t" . $order->products[$i]['attributes'][$j]['option'] . ' ' . zen_decode_specialchars($order->products[$i]['attributes'][$j]['value']);
						}
					}

					$order->products_ordered .= $order->products[$i]['qty'] . ' x ' . $order->products[$i]['name'] . ($order->products[$i]['model'] != '' ? ' (' . $order->products[$i]['model'] . ') ' : '') . ' = ' .
						$currencies->display_price($order->products[$i]['final_price'], $order->products[$i]['tax'], $order->products[$i]['qty']) .
						($order->products[$i]['onetime_charges'] != 0 ? "\n" . TEXT_ONETIME_CHARGES_EMAIL . $currencies->display_price($this->products[$i]['onetime_charges'], $order->products[$i]['tax'], 1) : '') .
						$this->products_ordered_attributes . "\n";
					$order->products_ordered_html .=
						'<tr>' . "\n" .
						'<td class="product-details" align="right" valign="top" width="30">' . $order->products[$i]['qty'] . '&nbsp;x</td>' . "\n" .
						'<td class="product-details" valign="top">' . nl2br($order->products[$i]['name']) . ($order->products[$i]['model'] != '' ? ' (' . nl2br($order->products[$i]['model']) . ') ' : '') . "\n" .
						'<nobr>' .
						'<small><em> ' . nl2br($this->products_ordered_attributes) . '</em></small>' .
						'</nobr>' .
						'</td>' . "\n" .
						'<td class="product-details-num" valign="top" align="right">' .
						$currencies->display_price($order->products[$i]['final_price'], $order->products[$i]['tax'], $order->products[$i]['qty']) .
						($order->products[$i]['onetime_charges'] != 0 ?
							'</td></tr>' . "\n" . '<tr><td class="product-details">' . nl2br(TEXT_ONETIME_CHARGES_EMAIL) . '</td>' . "\n" .
							'<td>' . $currencies->display_price($order->products[$i]['onetime_charges'], $order->products[$i]['tax'], 1) : '') .
						'</td></tr>' . "\n";
				}

				$order->send_order_email($order_id, 2);
			}

			$db->Execute("insert into " . TABLE_ORDERS_STATUS_HISTORY . "
					  (orders_id, orders_status_id, date_added, customer_notified, comments)
					  values ('" . zen_db_input($order_id) . "',
					  '" . zen_db_input($status_id) . "',
					  now(),
					  '" . zen_db_input($notify) . "',
					  'order notice [Ekaeorder TradeNo: {$transactionid}]')");
		}
	}

	function vpost($url, $data)
	{
		$curl = curl_init();
		curl_setopt($curl, CURLOPT_URL, $url);
		curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($curl, CURLOPT_POSTFIELDS, $data);
		curl_setopt($curl, CURLOPT_REFERER, $_SERVER['HTTP_REFERER']);
		curl_setopt($curl, CURLOPT_TIMEOUT, 60);
		curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, false);
		curl_setopt($curl, CURLOPT_SSL_VERIFYHOST, false);
		curl_setopt($curl, CURLOPT_USERAGENT, $_SERVER['HTTP_USER_AGENT']);
		//curl_multi_info_read($curl);														
		$response = curl_exec($curl);
		$result = json_decode($response, TRUE);
		curl_close($curl);
		return $result;
	}

	/**
	 * 判断浏览器是否支持HTML5
	 * @return bool
	 */
	function isSupportHtml5()
	{
		$UserAgent = strtolower($_SERVER['HTTP_USER_AGENT']);
		$CanReadHTML5 = false;
		if (strpos($UserAgent, 'webkit') || strpos($UserAgent, 'firefox') || strpos($UserAgent, 'trident') || strpos($UserAgent, 'safari')) {
			$CanReadHTML5 = true;
		}
		return $CanReadHTML5;
	}

	function buildNameValueList($orders_id)
	{

		global $order, $currencies, $language;
		// 订单信息
		//$BillNo = date("Ymd").$orders_id;
		$BillNo = $orders_id;
		$MD5key = trim(MODULE_PAYMENT_NEWORDER_MD5KEY);
		$MerNo = trim(MODULE_PAYMENT_NEWORDER_MID);
		$CurrencyCode = $this->getCurrencyCode($order);
		$Amount = number_format($order->info['total'] * $currencies->get_value($order->info['currency']), 2, '.', '') * 100;
		$Freight = number_format($order->info['shipping_cost'] * $currencies->get_value($order->info['currency']), 2, '.', '');

		// 账单信息
		$BFirstName = trim($order->billing['firstname']);
		$BLastName = trim($order->billing['lastname']);
		$Email = trim($order->customer['email_address']);
		$Phone = trim($order->customer['telephone']);
		$BillZip = trim($order->billing['postcode']);
		$BillAddress = trim($order->billing['street_address'] . $order->billing['suburb']);
		$BillCity = trim($order->billing['city']);
		$BillState = trim($order->billing['state']);

		$BillCountry = $order->billing['country']['title'];
		$BillCountryCode = trim($order->billing['country']['iso_code_2']);

		// 发货信息
		$SFirstName = trim($order->delivery['firstname']);
		$SLastName = trim($order->delivery['lastname']);
		$SEmail = trim($order->delivery['email_address']);
		$SPhone = trim($order->delivery['telephone']);
		$ShipZip = trim($order->delivery['postcode']);
		$ShipAddress = trim($order->delivery["street_address"] . $order->delivery['suburb']);
		$ShipCity = trim($order->delivery['city']);
		$ShipState = trim($order->delivery['state']);
		$ShipCountry = trim($order->delivery['country']['title']);
		$ShipCountryCode = trim($order->delivery['country']['iso_code_2']);

		// 通道信息
		$Currency = '15';
		$Language = '2';
		$LangCode = $this->get_language_code($_SESSION['languages_code']);
		//$ReturnURL = zen_href_link(FILENAME_CHECKOUT_PROCESS, '', 'SSL');
		$ReturnURL = zen_href_link(FILENAME_CHECKOUT_PROCESS, '', 'SSL');
		$Remark = $order->info['comments'];

		// 货物信息
		$GoodListInfo = "";
		for ($i = 0; $i < sizeof($order->products); $i++) {
			$GoodListInfo = $GoodListInfo . $order->products[$i]["name"] . ",#" . $order->products[$i]["model"] . ";#";
		}

		$CardNo = $_SESSION['cardNo'];
		$SecurityCode = $_SESSION['cvv'];
		$CardExpireYear = $_SESSION['expires_year'];
		$CardExpireMonth = $_SESSION['expires_month'];
		$Ip = $this->getOnline_ip();
		$BroserType = $_SERVER['HTTP_USER_AGENT'];
		$BrowserLang = $_SERVER['HTTP_ACCEPT_LANGUAGE'];
		$SessionId = $_SERVER['HTTP_COOKIE'];

		// 数据的组合和加密校验
		$md5src = $MD5key . $MerNo . $BillNo . $Amount . $CurrencyCode;
		$MD5info = $this->szComputeMD5Hash($md5src);

		$cookies = '';
		foreach ($_COOKIE as $key => $val) {
			$cookies = $cookies . $key . '=' . $val . ';';
		}
		$post_data = array(
			'URL' => $_SERVER["HTTP_HOST"],
			'TxnType' => "01",
			'AcctNo' => $MerNo,
			'OrderID' =>  $BillNo,
			'CartID' => $_SESSION['cart']->cartID,
			'CardType' => $this->getCartType($CardNo),
			'CurrCode' => $CurrencyCode,
			'Amount' => $Amount,
			'IPAddress' => $Ip,
			'BAddress' => $ShipAddress,
			'Email' => $Email,
			'BCity' => $ShipCity,
			'PostCode' => $ShipZip,
			'Telephone' => $Phone,
			'HashValue' => $MD5info,
			'CName' => $BFirstName . $BLastName,
			'Bstate' => $ShipState,
			'OrderUrl' => $ReturnURL,
			'PName' => $GoodListInfo,
			'Bcountry' => $ShipCountry,
			'BCountryCode' => $ShipCountryCode,
			'CardPAN' => $CardNo,
			'CVV2' => $SecurityCode,
			'ExpirationMonth' => $CardExpireMonth,
			'ExpirationYear' => substr($CardExpireYear, 2),
			'ExpDate' => substr($CardExpireYear, 2) . $CardExpireMonth,
			'Framework' => "zencart",
			'IVersion' => "V7.0-A-200",
			'IFrame' => 1,
			'cardorder_brower' => $BroserType,
			'cardorder_brower_lang' => $BrowserLang,
			'cookies' => $cookies,
			'Language' => $LangCode,
			'CMSVersion'=> $this->zencartVersion, 
			'PHPVersion'=> phpversion()
		);
		return http_build_query($post_data, '', '&');
	}

	function after_process()
	{
		global $insert_id, $db;
		return true;
	}


	function getOnline_ip()
	{
		if(isset($_SERVER['HTTP_X_FORWARDED_FOR'])){ 
			$online_ip = $_SERVER['HTTP_X_FORWARDED_FOR']; 
		}	elseif(isset($_SERVER['HTTP_CLIENT_IP'])){ 
			$online_ip = $_SERVER['HTTP_CLIENT_IP']; 
		} elseif(isset($_SERVER['HTTP_X_REAL_IP'])){ 
			$online_ip = $_SERVER['HTTP_X_REAL_IP']; 
		} else { 
			$online_ip = $_SERVER['REMOTE_ADDR']; 
		}
		$ips = explode(",",$online_ip);
		return $ips[0];  
	}

	function output_error()
	{
		return false;
	}

	function check()
	{
		global $db;
		if (!isset($this->_check)) {
			$check_query = $db->Execute("select configuration_value from " . TABLE_CONFIGURATION . " where configuration_key = 'MODULE_PAYMENT_NEWORDER_STATUS'");
			$this->_check = $check_query->RecordCount();
		}
		return $this->_check;
	}

	function install()
	{
		global $db, $language, $module_type;
		// 增加订单初始状态(Unpaid)、支付成功状态(Success)和支付失败状态(Fail)
		$check_query = $db->Execute("select * from " . TABLE_ORDERS_STATUS . " where orders_status_id in(188,144)");
		$count = $check_query->RecordCount();
		$languages = zen_get_languages();
		if ($count >= 1) {
			$db->Execute("DELETE FROM " . TABLE_ORDERS_STATUS . " WHERE orders_status_id in(188,144,155)");
			foreach ($languages as $lang) {
				$db->Execute("insert into " . TABLE_ORDERS_STATUS . " (orders_status_id, language_id, orders_status_name) values ('" . 188 . "', '" . $lang['id'] . "', 'Paid Success')");
				$db->Execute("insert into " . TABLE_ORDERS_STATUS . " (orders_status_id, language_id, orders_status_name) values ('" . 144 . "', '" . $lang['id'] . "', 'Paid Fail')");
			}
		} else {
			foreach ($languages as $lang) {
				$db->Execute("insert into " . TABLE_ORDERS_STATUS . " (orders_status_id, language_id, orders_status_name) values ('" . 188 . "', '" . $lang['id'] . "', 'Paid Success')");
				$db->Execute("insert into " . TABLE_ORDERS_STATUS . " (orders_status_id, language_id, orders_status_name) values ('" . 144 . "', '" . $lang['id'] . "', 'Paid Fail')");
			}
		}
		$db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('Open payment mode', 'MODULE_PAYMENT_NEWORDER_STATUS', 'True', 'If you would like to open payment mode', '6', '1', 'zen_cfg_select_option(array(\'True\', \'False\'), ', now())");
		$db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('NewOrder Id', 'MODULE_PAYMENT_NEWORDER_MID', '', '', '6', '2', now())");
		$db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('NewOrder Key', 'MODULE_PAYMENT_NEWORDER_MD5KEY', '', '', '6', '3', now())");
		//支付区域宽度
		$db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, use_function, set_function, date_added) values ('Payment Zone Width', 'MODULE_PAYMENT_NEWORDER_ZONE_WIDTH', '1', ' Please choose the width of payment zone', '6', '4','','zen_draw_pull_down_menu(\"configuration[MODULE_PAYMENT_NEWORDER_ZONE_WIDTH]\",array(array(id=>\"310px\",text=>\"310px\"),array(id=>\"240px\",text=>\"240px\"),array(id=>\"180px\",text=>\"180px\"),array(id=>\"custom\",text=>\"Custom Width\")),', now())");
		$db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('Payment Zone Custom Width', 'MODULE_PAYMENT_NEWORDER_ZONE_CUSTOM_WIDTH', '', 'If you select Custom width for Payment Zone Width,you must input this.', '6', '5', now())");
		$db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('Payment Zone Mobile Width', 'MODULE_PAYMENT_NEWORDER_ZONE_MOBILE_WIDTH', '80%', 'Please input the width of payment zone for mobile.', '6', '6', now())");

		$db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, use_function, set_function, date_added) values ('Payment Zone', 'MODULE_PAYMENT_NEWORDER_ZONE', '0', 'Unchoose is all area can use payment method', '6', '14', 'zen_get_zone_class_title', 'zen_cfg_pull_down_zone_classes(', now())");
		// 订单初始状态
		$db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, use_function, date_added) values ('Order Orginal Status', 'MODULE_PAYMENT_NEWORDER_ORDER_STATUS_ID', '1', 'Please choose pending', '6', '12', 'zen_cfg_pull_down_order_statuses(', 'zen_get_order_status_name', now())"); //***** modified.*****
		$db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('Display Sequence', 'MODULE_PAYMENT_NEWORDER_SORT_ORDER', '1', 'The Sequence of payment method,the lower rank first.', '6', '6', now())");

	}

	function remove()
	{
		global $db;
		$db->Execute("delete from " . TABLE_CONFIGURATION . " where configuration_key in ('" . implode("', '", $this->keys()) . "')");
	}


	function keys()
	{
		return array(
			'MODULE_PAYMENT_NEWORDER_STATUS',
			'MODULE_PAYMENT_NEWORDER_MID',
			'MODULE_PAYMENT_NEWORDER_MD5KEY',
			'MODULE_PAYMENT_NEWORDER_ZONE',
			'MODULE_PAYMENT_NEWORDER_ZONE_WIDTH',
			'MODULE_PAYMENT_NEWORDER_ZONE_CUSTOM_WIDTH',
			'MODULE_PAYMENT_NEWORDER_ZONE_MOBILE_WIDTH',
			'MODULE_PAYMENT_NEWORDER_ORDER_STATUS_ID',
			'MODULE_PAYMENT_NEWORDER_SORT_ORDER'
		);
	}

	function arg_sort($array)
	{
		ksort($array);
		reset($array);
		return $array;
	}

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

//实现多种字符解码方式
	function charset_decode($input, $_input_charset, $_output_charset = "utf-8")
	{
		$output = "";
		if (!isset($_input_charset)) $_input_charset = $this->_input_charset;
		if ($_input_charset == $_output_charset || $input == null) {
			$output = $input;
		} elseif (function_exists("mb_convert_encoding")) {
			$output = mb_convert_encoding($input, $_output_charset, $_input_charset);
		} elseif (function_exists("iconv")) {
			$output = iconv($_input_charset, $_output_charset, $input);
		} else die("sorry, you have no libs support for charset changes.");
		return $output;
	}

	/**
	 * 获取支付语言代码
	 * @返回字符串
	 */
	function get_language_code($language)
	{
		return $this->languageCode[strtolower($language)];
	}

	/**
	 * 使用特殊字符转义字符(例如"("使用&#40 ,后面需加一个空格,否则会导致乱码)
	 * @param string string_before	// 转换前字符串
	 * @return string string_after	// 转换后字符串
	 */
	function string_replace($string_before)
	{
		$string_after = str_replace("\n", " ", $string_before);
		$string_after = str_replace("\r", " ", $string_after);
		$string_after = str_replace("\r\n", " ", $string_after);
		$string_after = str_replace("'", "&#39 ", $string_after);
		$string_after = str_replace('"', "&#34 ", $string_after);
		$string_after = str_replace("(", "&#40 ", $string_after);
		$string_after = str_replace(")", "&#41 ", $string_after);
		return $string_after;
	}

	function http_response($url, $status = null, $wait = 3)
	{
		$ch = curl_init();
		curl_setopt($ch, CURLOPT_URL, $url);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
		curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
		curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
		curl_setopt($ch, CURLOPT_USERAGENT, $_SERVER['HTTP_USER_AGENT']);
		//curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
		curl_setopt($ch, CURLOPT_REFERER, '');
		curl_setopt($ch, CURLOPT_HEADER, 0);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);

		$red = curl_exec($ch);
		curl_close($ch);
		return $red;
	}

	function getNewOrderConfig()
	{
		$url = 'https://merchant.paytos.com/CubePaymentGateway/gateway/action.PayConfigService.do?Language=';
		//$url = 'http://192.168.1.11:8080/Gateway/action.PayConfigService.do?Language=';
		$newOrderConfig = null;
		$_SESSION['newOrderConfig'] = null;
		if (!isset($_SESSION['newOrderConfig'])) {
			$lang = $this->get_language_code($_SESSION['language']);
			$result = $this->http_response($url . $lang);
			$newOrderConfig = json_decode($result, TRUE);
			if (strtoupper(CHARSET) == 'ISO-8859-1') {
				foreach ($newOrderConfig as $key => $value) {
					if (is_array($value)) {
						foreach ($value as $key_s => $value_s) {
							$newOrderConfig[$key][$key_s] = utf8_decode($value_s);
						}
					} else {
						$newOrderConfig[$key] = utf8_decode($value);
					}
				}
			}
			$_SESSION['newOrderConfig'] = $newOrderConfig;
		} else {
			$newOrderConfig = $_SESSION['newOrderConfig'];
		}

		return $newOrderConfig;
	}


	function getCartType($cartNum)
	{
		$cartType = "A";
		if ($cartNum == '') {
			return '';
		} else {
			$cartNumPrefix = substr($cartNum, 0, 2);
			switch ($cartNumPrefix) {
				case "40":
				case "41":
				case "42":
				case "43":
				case "44":
				case "45":
				case "46":
				case "47":
				case "48":
				case "49":
					$cartType = 'V';
					break;
				case "51":
				case "52":
				case "53":
				case "54":
				case "55":
					$cartType = 'M';
					break;
				case "35":
					$cartType = 'J';
					break;
				case "34":
				case "37":
					$cartType = 'A';
					break;
				case "30":
				case "36":
				case "38":
				case "39":
				case "60":
				case "64":
				case "65":
					$cartType = 'D';
					break;
			}
		}
		return $cartType;

	}

	function substr_startswith($haystack, $needle)
	{
		return substr($haystack, 0, strlen($needle)) === $needle;
	}


	function getCurrencyCode($order)
	{
		switch($order->info['currency']){
			case 'ADP': $v_moneytype = '020'; break;
			case 'AED': $v_moneytype = '784'; break;
			case 'AFA': $v_moneytype = '004'; break;
			case 'ALL': $v_moneytype = '008'; break;
			case 'AMD': $v_moneytype = '051'; break;
			case 'ANG': $v_moneytype = '532'; break;
			case 'AOA': $v_moneytype = '973'; break;
			case 'AON': $v_moneytype = '024'; break;
			case 'ARS': $v_moneytype = '032'; break;
			case 'ASF': $v_moneytype = '999'; break;
			case 'ATS': $v_moneytype = '040'; break;
			case 'AUD': $v_moneytype = '036'; break;
			case 'AWG': $v_moneytype = '533'; break;
			case 'AZM': $v_moneytype = '031'; break;
			case 'BAM': $v_moneytype = '977'; break;
			case 'BBD': $v_moneytype = '052'; break;
			case 'BDT': $v_moneytype = '050'; break;
			case 'BEF': $v_moneytype = '056'; break;
			case 'BGL': $v_moneytype = '100'; break;
			case 'BGN': $v_moneytype = '975'; break;
			case 'BHD': $v_moneytype = '048'; break;
			case 'BIF': $v_moneytype = '108'; break;
			case 'BMD': $v_moneytype = '060'; break;
			case 'BND': $v_moneytype = '096'; break;
			case 'BOB': $v_moneytype = '068'; break;
			case 'BOV': $v_moneytype = '984'; break;
			case 'BRL': $v_moneytype = '986'; break;
			case 'BSD': $v_moneytype = '044'; break;
			case 'BTN': $v_moneytype = '064'; break;
			case 'BWP': $v_moneytype = '072'; break;
			case 'BYB': $v_moneytype = '112'; break;
			case 'BYR': $v_moneytype = '974'; break;
			case 'BZD': $v_moneytype = '084'; break;
			case 'CAD': $v_moneytype = '124'; break;
			case 'CDF': $v_moneytype = '976'; break;
			case 'CHF': $v_moneytype = '756'; break;
			case 'CLF': $v_moneytype = '990'; break;
			case 'CLP': $v_moneytype = '152'; break;
			case 'CNY': $v_moneytype = '156'; break;
			case 'COP': $v_moneytype = '170'; break;
			case 'CRC': $v_moneytype = '188'; break;
			case 'CUP': $v_moneytype = '192'; break;
			case 'CVE': $v_moneytype = '132'; break;
			case 'CYP': $v_moneytype = '196'; break;
			case 'CZK': $v_moneytype = '203'; break;
			case 'DEM': $v_moneytype = '280'; break;
			case 'DJF': $v_moneytype = '262'; break;
			case 'DKK': $v_moneytype = '208'; break;
			case 'DOP': $v_moneytype = '214'; break;
			case 'DZD': $v_moneytype = '012'; break;
			case 'ECS': $v_moneytype = '218'; break;
			case 'ECV': $v_moneytype = '983'; break;
			case 'EEK': $v_moneytype = '233'; break;
			case 'EGP': $v_moneytype = '818'; break;
			case 'ERN': $v_moneytype = '232'; break;
			case 'ESP': $v_moneytype = '724'; break;
			case 'ETB': $v_moneytype = '230'; break;
			case 'EUR': $v_moneytype = '978'; break;
			case 'FIM': $v_moneytype = '246'; break;
			case 'FJD': $v_moneytype = '242'; break;
			case 'FKP': $v_moneytype = '238'; break;
			case 'FRF': $v_moneytype = '250'; break;
			case 'GBP': $v_moneytype = '826'; break;
			case 'GEL': $v_moneytype = '981'; break;
			case 'GHC': $v_moneytype = '288'; break;
			case 'GIP': $v_moneytype = '292'; break;
			case 'GMD': $v_moneytype = '270'; break;
			case 'GNF': $v_moneytype = '324'; break;
			case 'GRD': $v_moneytype = '300'; break;
			case 'GTQ': $v_moneytype = '320'; break;
			case 'GWP': $v_moneytype = '624'; break;
			case 'GYD': $v_moneytype = '328'; break;
			case 'HKD': $v_moneytype = '344'; break;
			case 'HNL': $v_moneytype = '340'; break;
			case 'HRK': $v_moneytype = '191'; break;
			case 'HTG': $v_moneytype = '332'; break;
			case 'HUF': $v_moneytype = '348'; break;
			case 'IDR': $v_moneytype = '360'; break;
			case 'IEP': $v_moneytype = '372'; break;
			case 'ILS': $v_moneytype = '376'; break;
			case 'INR': $v_moneytype = '368'; break;
			case 'IRR': $v_moneytype = '364'; break;
			case 'ISK': $v_moneytype = '352'; break;
			case 'ITL': $v_moneytype = '380'; break;
			case 'JMD': $v_moneytype = '388'; break;
			case 'JOD': $v_moneytype = '400'; break;
			case 'JPY': $v_moneytype = '392'; break;
			case 'KES': $v_moneytype = '404'; break;
			case 'KGS': $v_moneytype = '417'; break;
			case 'KHR': $v_moneytype = '116'; break;
			case 'KMF': $v_moneytype = '174'; break;
			case 'KPW': $v_moneytype = '408'; break;
			case 'KRW': $v_moneytype = '410'; break;
			case 'KWD': $v_moneytype = '414'; break;
			case 'KYD': $v_moneytype = '136'; break;
			case 'KZT': $v_moneytype = '398'; break;
			case 'LAK': $v_moneytype = '418'; break;
			case 'LBP': $v_moneytype = '422'; break;
			case 'LKR': $v_moneytype = '144'; break;
			case 'LRD': $v_moneytype = '430'; break;
			case 'LSL': $v_moneytype = '426'; break;
			case 'LTL': $v_moneytype = '440'; break;
			case 'LUF': $v_moneytype = '442'; break;
			case 'LVL': $v_moneytype = '428'; break;
			case 'LYD': $v_moneytype = '434'; break;
			case 'MAD': $v_moneytype = '504'; break;
			case 'MDL': $v_moneytype = '498'; break;
			case 'MGF': $v_moneytype = '450'; break;
			case 'MKD': $v_moneytype = '807'; break;
			case 'MMK': $v_moneytype = '104'; break;
			case 'MNT': $v_moneytype = '496'; break;
			case 'MOP': $v_moneytype = '446'; break;
			case 'MRO': $v_moneytype = '478'; break;
			case 'MTL': $v_moneytype = '470'; break;
			case 'MUR': $v_moneytype = '480'; break;
			case 'MVR': $v_moneytype = '462'; break;
			case 'MWK': $v_moneytype = '454'; break;
			case 'MXN': $v_moneytype = '484'; break;
			case 'MXV': $v_moneytype = '979'; break;
			case 'MYR': $v_moneytype = '458'; break;
			case 'MZM': $v_moneytype = '508'; break;
			case 'NAD': $v_moneytype = '516'; break;
			case 'NGN': $v_moneytype = '566'; break;
			case 'NIO': $v_moneytype = '558'; break;
			case 'NLG': $v_moneytype = '528'; break;
			case 'NOK': $v_moneytype = '578'; break;
			case 'NPR': $v_moneytype = '524'; break;
			case 'NZD': $v_moneytype = '554'; break;
			case 'OMR': $v_moneytype = '512'; break;
			case 'PAB': $v_moneytype = '590'; break;
			case 'PEN': $v_moneytype = '604'; break;
			case 'PGK': $v_moneytype = '598'; break;
			case 'PHP': $v_moneytype = '608'; break;
			case 'PKR': $v_moneytype = '586'; break;
			case 'PLN': $v_moneytype = '985'; break;
			case 'PLZ': $v_moneytype = '616'; break;
			case 'PTE': $v_moneytype = '620'; break;
			case 'PYG': $v_moneytype = '600'; break;
			case 'QAR': $v_moneytype = '634'; break;
			case 'ROL': $v_moneytype = '642'; break;
			case 'RSD': $v_moneytype = '941'; break;
			case 'RUB': $v_moneytype = '810'; break;
			case 'RWF': $v_moneytype = '646'; break;
			case 'SAR': $v_moneytype = '682'; break;
			case 'SBD': $v_moneytype = '090'; break;
			case 'SCR': $v_moneytype = '690'; break;
			case 'SDD': $v_moneytype = '736'; break;
			case 'SDP': $v_moneytype = '736'; break;
			case 'SDR': $v_moneytype = '000'; break;
			case 'SEK': $v_moneytype = '752'; break;
			case 'SGD': $v_moneytype = '702'; break;
			case 'SHP': $v_moneytype = '654'; break;
			case 'SIT': $v_moneytype = '705'; break;
			case 'SKK': $v_moneytype = '703'; break;
			case 'SLL': $v_moneytype = '694'; break;
			case 'SOS': $v_moneytype = '706'; break;
			case 'SRG': $v_moneytype = '740'; break;
			case 'STD': $v_moneytype = '678'; break;
			case 'SVC': $v_moneytype = '222'; break;
			case 'SYP': $v_moneytype = '760'; break;
			case 'SZL': $v_moneytype = '748'; break;
			case 'THB': $v_moneytype = '764'; break;
			case 'TJR': $v_moneytype = '762'; break;
			case 'TJS': $v_moneytype = '972'; break;
			case 'TMM': $v_moneytype = '795'; break;
			case 'TND': $v_moneytype = '788'; break;
			case 'TOP': $v_moneytype = '776'; break;
			case 'TRL': $v_moneytype = '792'; break;
			case 'TTD': $v_moneytype = '780'; break;
			case 'TWD': $v_moneytype = '901'; break;
			case 'TZS': $v_moneytype = '834'; break;
			case 'UAH': $v_moneytype = '980'; break;
			case 'UAK': $v_moneytype = '804'; break;
			case 'UGX': $v_moneytype = '800'; break;
			case 'USD': $v_moneytype = '840'; break;
			case 'USN': $v_moneytype = '997'; break;
			case 'USS': $v_moneytype = '998'; break;
			case 'UYU': $v_moneytype = '858'; break;
			case 'UZS': $v_moneytype = '860'; break;
			case 'VEB': $v_moneytype = '862'; break;
			case 'VND': $v_moneytype = '704'; break;
			case 'VUV': $v_moneytype = '548'; break;
			case 'WST': $v_moneytype = '882'; break;
			case 'XAF': $v_moneytype = '950'; break;
			case 'XAG': $v_moneytype = '961'; break;
			case 'XAU': $v_moneytype = '959'; break;
			case 'XBA': $v_moneytype = '955'; break;
			case 'XBB': $v_moneytype = '956'; break;
			case 'XBC': $v_moneytype = '957'; break;
			case 'XBD': $v_moneytype = '958'; break;
			case 'XCD': $v_moneytype = '951'; break;
			case 'XDR': $v_moneytype = '960'; break;
			case 'XEU': $v_moneytype = '954'; break;
			case 'XOF': $v_moneytype = '952'; break;
			case 'XPD': $v_moneytype = '964'; break;
			case 'XPF': $v_moneytype = '953'; break;
			case 'XPT': $v_moneytype = '962'; break;
			case 'XTS': $v_moneytype = '963'; break;
			case 'XXX': $v_moneytype = '999'; break;
			case 'YER': $v_moneytype = '886'; break;
			case 'YUM': $v_moneytype = '891'; break;
			case 'YUN': $v_moneytype = '890'; break;
			case 'ZAL': $v_moneytype = '991'; break;
			case 'ZAR': $v_moneytype = '710'; break;
			case 'ZMK': $v_moneytype = '894'; break;
			case 'ZRN': $v_moneytype = '180'; break;
			case 'ZWD': $v_moneytype = '716'; break;
			default:	$v_moneytype = ''; break;
		}
		return $v_moneytype;

	}

}

?>