<?php
error_reporting(0);
$action = $_GET['action'];
$total = "0";
require(DIR_WS_LANGUAGES . 'english/modules/order_total/ot_coupon.php');
switch ($action){
	case 'rc': //remove coupon
		unset($_POST['dc_redeem_code']);
		unset($_SESSION['cc_id']);
		_show_msg(TEXT_REMOVE_REDEEM_COUPON);
		exit;
		break;
	case 'cc': //check coupon
		$_POST['dc_redeem_code'] = $_REQUEST['v'];
		_collect_coupon_posts();
		break;
	case 'ce': //check email
		$msg = '';
		if (!$_SESSION['customer_id']) {
			$email = trim($_REQUEST['e']);
			$sql = "SELECT COUNT(*) AS total " .
						 "FROM " . TABLE_CUSTOMERS . " " .
						 "WHERE customers_email_address = '" . zen_db_input($email) . "' " .
						 "AND COWOA_account != 1";
			$result = $db->Execute($sql);
			if ($result->fields['total'] > 0)	$msg = ENTRY_EMAIL_ADDRESS_ERROR_EXISTS;
		}
		_show_msg($msg);
		exit;
		break;

	case "CustShowPrice":
	case "custshowprice":
		$_POST['shipping'] = 'table_table';

		$req_country_id = $_REQUEST['selected_country_id'];
		if ($req_country_id) {
			$_SESSION['selected_country_id'] = $req_country_id;
		}
		
		$_SESSION['shipping'] = $_POST['shipping'];
		$total_weight = $_SESSION['cart']->show_weight();
		$total_count = $_SESSION['cart']->count_contents();		
		require(DIR_WS_CLASSES . 'shipping.php');
		$shipping_modules = new shipping($_SESSION['shipping']);
		list($module, $method) = explode('_', $_SESSION['shipping']);
		if ( is_object($$module) || ($_SESSION['shipping'] == 'free_free') ) {
			if ($_SESSION['shipping'] == 'free_free') {
				$quote[0]['methods'][0]['title'] = "Shipping";
				$quote[0]['methods'][0]['cost'] = '0';
			} else {
				$quote = $shipping_modules->quote($method, $module);
			}
		
			if (isset($quote['error'])) {
				$_SESSION['shipping'] = '';
			} else {
				if ( (isset($quote[0]['methods'][0]['title'])) && (isset($quote[0]['methods'][0]['cost'])) ) {
					$_SESSION['shipping'] = array('id' => $_SESSION['shipping'],
																				'title' => (($free_shipping == true) ?  $quote[0]['methods'][0]['title'] : $quote[0]['module']/* . ' (' . $quote[0]['methods'][0]['title'] . ')'*/),
																				'cost' => $quote[0]['methods'][0]['cost']);
				}
			}
		} else {
			$_SESSION['shipping'] = false;
		}
		
		$_SESSION['payment'] = $_REQUEST['payment'];
		
		require(DIR_WS_CLASSES . 'order_total.php');
		$order_total_modules = new order_total;
		$order_total_modules->collect_posts();
		$order_total_modules->pre_confirmation_check();
		
		require(DIR_WS_CLASSES . 'order.php');
		$order = new order;
		
		$order_total_modules->process();
		
		$string = '<table cellpadding="0" cellspacing="2">' .
		'<thead><tr><th colspan="2">Detailed Info</th></tr></thead>' .
		$order_total_modules->output(true) .
		'</table>';
		break;
	default:
		$string = "No Record!";
}

_show_msg($string);
exit;

function _show_msg($message){ 
	echo $message;
}

function _collect_coupon_posts() {
	global $db, $currencies, $messageStack, $order;
	global $discount_coupon;

	// remove discount coupon by request
	if (isset($_POST['dc_redeem_code']) && strtoupper($_POST['dc_redeem_code']) == 'REMOVE') {
		unset($_POST['dc_redeem_code']);
		unset($_SESSION['cc_id']);
		_show_msg(TEXT_REMOVE_REDEEM_COUPON);
		exit;
	}

	// bof: Discount Coupon zoned always validate coupon for payment address changes
	if ($_SESSION['cc_id'] > 0) {
		$sql = "select coupon_id, coupon_amount, coupon_type, coupon_minimum_order, uses_per_coupon, uses_per_user,
						restrict_to_products, restrict_to_categories, coupon_zone_restriction, coupon_code
						from " . TABLE_COUPONS . "
						where coupon_id= :couponIDEntered
						and coupon_active='Y'";
		$sql = $db->bindVars($sql, ':couponIDEntered', $_SESSION['cc_id'], 'string');
		
		$coupon_result=$db->Execute($sql);
		
		$foundvalid = true;
		
		$check_flag = false;
		$check = $db->Execute("select zone_id, zone_country_id from " . TABLE_ZONES_TO_GEO_ZONES . " where geo_zone_id = '" . $coupon_result->fields['coupon_zone_restriction'] . "' and zone_country_id = '" . $order->billing['country']['id'] . "' order by zone_id");

		if ($coupon_result->fields['coupon_zone_restriction'] > 0) {
			while (!$check->EOF) {
				if ($check->fields['zone_id'] < 1) {
					$check_flag = true;
					break;
				} elseif ($check->fields['zone_id'] == $order->billing['zone_id']) {
					$check_flag = true;
					break;
				}
				$check->MoveNext();
			}
			$foundvalid = $check_flag;
		}
		// remove if fails address validation
		if (!$foundvalid) {
			_clear_coupon_posts();
			_show_msg(TEXT_REMOVE_REDEEM_COUPON_ZONE);
			exit;
		}
	}
	// eof: Discount Coupon zoned always validate coupon for payment address changes
	if ((isset($_POST['dc_redeem_code']) && $_POST['dc_redeem_code'] != '') || (isset($discount_coupon->fields['coupon_code']) && $discount_coupon->fields['coupon_code'] != '')) {
		// set current Discount Coupon based on current or existing
		if (isset($_POST['dc_redeem_code']) && $discount_coupon->fields['coupon_code'] == '') {
			$dc_check = $_POST['dc_redeem_code'];
		} else {
			$dc_check = $discount_coupon->fields['coupon_code'];
		}
		$sql = "select coupon_id, coupon_amount, coupon_type, coupon_minimum_order, uses_per_coupon, uses_per_user,
						restrict_to_products, restrict_to_categories, coupon_zone_restriction
						from " . TABLE_COUPONS . "
						where coupon_code= :couponCodeEntered
						and coupon_active='Y'";
		$sql = $db->bindVars($sql, ':couponCodeEntered', $dc_check, 'string');

		$coupon_result=$db->Execute($sql);
		if ($coupon_result->fields['coupon_type'] != 'G') {
			
			if ($coupon_result->RecordCount() < 1 ) {
				_show_msg(TEXT_INVALID_REDEEM_COUPON);
				_clear_coupon_posts();
				exit;
			}
			$order_total = _get_order_total();
			if ($order_total['totalFull'] < $coupon_result->fields['coupon_minimum_order']) {
				_show_msg(sprintf(TEXT_INVALID_REDEEM_COUPON_MINIMUM, $currencies->format($coupon_result->fields['coupon_minimum_order'])));
				_clear_coupon_posts();
				exit;
			}
			
			// JTD - added missing code here to handle coupon product restrictions
			// look through the items in the cart to see if this coupon is valid for any item in the cart
			$products = $_SESSION['cart']->get_products();
			$foundvalid = true;

			if ($foundvalid == true) {
				$foundvalid = false;
				for ($i=0; $i<sizeof($products); $i++) {
					if (is_product_valid($products[$i]['id'], $coupon_result->fields['coupon_id'])) {
						$foundvalid = true;
						continue;
					}
				}
			}

			if (!$foundvalid) {
				_show_msg(TEXT_INVALID_REDEEM_COUPON);
				_clear_coupon_posts();
				exit;
			}
			
			$date_query=$db->Execute("select coupon_start_date from " . TABLE_COUPONS . "
																where coupon_start_date <= now() and
																coupon_code='" . zen_db_prepare_input($dc_check) . "'");
		
			if ($date_query->RecordCount() < 1 ) {
				_show_msg(TEXT_INVALID_STARTDATE_COUPON);
				_clear_coupon_posts();
				exit;
			}
			
			$date_query=$db->Execute("select coupon_expire_date from " . TABLE_COUPONS . "
																where coupon_expire_date >= now() and
																coupon_code='" . zen_db_prepare_input($dc_check) . "'");
						
			if ($date_query->RecordCount() < 1 ) {
				_show_msg(TEXT_INVALID_FINISHDATE_COUPON);
				_clear_coupon_posts();
				exit;
			}
			
			$coupon_count = $db->Execute("select coupon_id from " . TABLE_COUPON_REDEEM_TRACK . "
																		where coupon_id = '" . (int)$coupon_result->fields['coupon_id']."'");
			
			$coupon_count_customer = $db->Execute("select coupon_id from " . TABLE_COUPON_REDEEM_TRACK . "
																						 where coupon_id = '" . $coupon_result->fields['coupon_id']."' and
																						 customer_id = '" . (int)$_SESSION['customer_id'] . "'");
															
			if ($coupon_count->RecordCount() >= $coupon_result->fields['uses_per_coupon'] && $coupon_result->fields['uses_per_coupon'] > 0) {
				_show_msg(TEXT_INVALID_USES_COUPON . $coupon_result->fields['uses_per_coupon'] . TIMES);
				_clear_coupon_posts();
				exit;
			}
			
			if ($coupon_count_customer->RecordCount() >= $coupon_result->fields['uses_per_user'] && $coupon_result->fields['uses_per_user'] > 0) {
				_show_msg(sprintf(TEXT_INVALID_USES_USER_COUPON, $dc_check) . $coupon_result->fields['uses_per_user'] . ($coupon_result->fields['uses_per_user'] == 1 ? TIME : TIMES));
				_clear_coupon_posts();
				exit;
			}
			
			if ($coupon_result->fields['coupon_type']=='S') {
				$coupon_amount = $order->info['shipping_cost'];
			} else {
				$coupon_amount = $currencies->format($coupon_result->fields['coupon_amount']) . ' ';
			}
			$_SESSION['cc_id'] = $coupon_result->fields['coupon_id'];
		}
		//if ($_POST['submit_redeem_coupon_x'] && !$_POST['gv_redeem_code']) zen_redirect(zen_href_link(FILENAME_CHECKOUT_PAYMENT, 'credit_class_error_code=' . _code . '&credit_class_error=' . urlencode(TEST_NO_REDEEM_CODE), 'SSL', true, false));
		_show_msg(TEXT_VALID_COUPON);
		exit;
	}
}

function _clear_coupon_posts() {
	unset($_POST['dc_redeem_code']);
	unset($_SESSION['cc_id']);
}

function _include_shipping() {
	return MODULE_ORDER_TOTAL_COUPON_INC_SHIPPING;
}

function _include_tax() {
	return MODULE_ORDER_TOTAL_COUPON_INC_TAX;
}

function _get_order_total() {
	require_once(DIR_WS_CLASSES . 'order.php');
	$order = new order();
	$order_total_tax = $order->info['tax'];
	$order_total = $order->info['total'];
	if (_include_shipping() != 'true') $order_total -= $order->info['shipping_cost'];
	if (_include_tax() != 'true') $order_total -= $order->info['tax'];
	$orderTotalFull = $order_total;
	$products = $_SESSION['cart']->get_products();
	for ($i=0; $i<sizeof($products); $i++) {
		if (!is_product_valid($products[$i]['id'], $_SESSION['cc_id'])) {
			$order_total -= $products[$i]['final_price'] * $products[$i]['quantity'];
			if (_include_tax == 'true') {
				$products_tax = zen_get_tax_rate($products[$i]['tax_class_id']);
				$order_total -= (zen_calculate_tax($products[$i]['final_price'], $products_tax))   * $products[$i]['quantity'];
			}
			$order_total_tax -= (zen_calculate_tax($products[$i]['final_price'], zen_get_tax_rate($products[$i]['tax_class_id'])))   * $products[$i]['quantity'];
		}
	}
	$order_total = array('totalFull'=>$orderTotalFull, 'total'=>$order_total, 'tax'=>$order_total_tax);
	return $order_total;
}
?>