<?php
//
// +----------------------------------------------------------------------+
// |zen-cart Open Source E-commerce                                       |
// +----------------------------------------------------------------------+
// | Copyright (c) 2003 The zen-cart developers                           |
// |                                                                      |
// | http://www.zen-cart.com/index.php                                    |
// |                                                                      |
// | Portions Copyright (c) 2003 osCommerce                               |
// |                                                                      |
// |   DevosC, Developing open source Code                                |
// |   Copyright (c) 2004 DevosC.com                                      |
// +----------------------------------------------------------------------+
// | This source file is subject to version 2.0 of the GPL license,       |
// | that is bundled with this package in the file LICENSE, and is        |
// | available through the world-wide-web at the following url:           |
// | http://www.zen-cart.com/license/2_0.txt.                             |
// | If you did not receive a copy of the zen-cart license and are unable |
// | to obtain it through the world-wide-web, please send a note to       |
// | license@zen-cart.com so we can mail you a copy immediately.          |
// +----------------------------------------------------------------------+
//  $Id: tpay.php 001 $
//
class tpay {
    var $code, $title, $description, $enabled;
    /**
     * order status setting for pending orders
     *
     * @var int
     */
    var $order_pending_status = 1;
    /**
     * order status setting for completed orders
     *
     * @var int
     */
    var $order_status = DEFAULT_ORDERS_STATUS_ID;
    // class constructor
    function tpay() {
        global $order;
        $this->code = 'tpay';
        if ($_GET['main_page'] != '') {
            $this->title = MODULE_PAYMENT_TPAY_TEXT_CATALOG_TITLE; // Payment Module title in Catalog
            
        } else {
            $this->title = MODULE_PAYMENT_TPAY_TEXT_ADMIN_TITLE; // Payment Module title in Admin
            
        }
        $this->description = MODULE_PAYMENT_TPAY_TEXT_DESCRIPTION;
        $this->sort_order = MODULE_PAYMENT_TPAY_SORT_ORDER;
        $this->enabled = ((MODULE_PAYMENT_TPAY_STATUS == 'True') ? true : false);
        if ((int)MODULE_PAYMENT_TPAY_ORDER_STATUS_ID > 0) {
            $this->order_status = MODULE_PAYMENT_TPAY_ORDER_STATUS_ID;
        }
        if (is_object($order)) $this->update_status();
        $this->form_action_url = MODULE_PAYMENT_TPAY_HANDLER;
    }
    // class methods
    function update_status() {
        global $order, $db;
        if (($this->enabled == true) && ((int)MODULE_PAYMENT_TPAY_ZONE > 0)) {
            $check_flag = false;
            $check_query = $db->Execute("select zone_id from " . TABLE_ZONES_TO_GEO_ZONES . " where geo_zone_id = '" . MODULE_PAYMENT_TPAY_ZONE . "' and zone_country_id = '" . $order->billing['country']['id'] . "' order by zone_id");
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
    function javascript_validation() {
        return false;
    }
    function selection() {
        return array(
            'id' => $this->code,
            'module' => MODULE_PAYMENT_TPAY_TEXT_CATALOG_LOGO,
            'icon' => MODULE_PAYMENT_TPAY_TEXT_CATALOG_LOGO
        );
    }
    function pre_confirmation_check() {
        return false;
    }
    function confirmation() {
        return array(
            'title' => MODULE_PAYMENT_TPAY_TEXT_DESCRIPTION
        );
    }
    function getip() {
        if (isset($_SERVER['HTTP_X_FORWARDED_FOR'])) {
            $online_ip = $_SERVER['HTTP_X_FORWARDED_FOR'];
        } elseif (isset($_SERVER['HTTP_CLIENT_IP'])) {
            $online_ip = $_SERVER['HTTP_CLIENT_IP'];
        } else {
            $online_ip = $_SERVER['REMOTE_ADDR'];
        }
        return $online_ip;
    }
    function szComputeMD5Hash($input) {
        $md5hex = md5($input);
        $len = strlen($md5hex) / 2;
        $md5raw = "";
        for ($i = 0; $i < $len; $i++) {
            $md5raw = $md5raw . chr(hexdec(substr($md5hex, $i * 2, 2)));
        }
        $keyMd5 = base64_encode($md5raw);
        return $keyMd5;
    }
    function szComputeSHA1Hash($input) {
        $md5hex = sha1($input);
        $len = strlen($md5hex) / 2;
        $md5raw = "";
        for ($i = 0; $i < $len; $i++) {
            $md5raw = $md5raw . chr(hexdec(substr($md5hex, $i * 2, 2)));
        }
        $keyMd5 = base64_encode($md5raw);
        return $keyMd5;
    }
    //生成订单
    private function create_order() {
        global $db, $order, $order_totals;
        $order->info['order_status'] = MODULE_PAYMENT_TPAY_ORDER_STATUS_ID;
        if ($order->info['total'] == 0) {
            if (DEFAULT_ZERO_BALANCE_ORDERS_STATUS_ID == 0) {
                $order->info['order_status'] = DEFAULT_ORDERS_STATUS_ID;
            } else if ($_SESSION['payment'] != 'freecharger') {
                $order->info['order_status'] = DEFAULT_ZERO_BALANCE_ORDERS_STATUS_ID;
            }
        }
        if ($_SESSION['shipping'] == 'free_free') {
            $order->info['shipping_module_code'] = $_SESSION['shipping'];
        }
        $sql_data_array = array(
            'customers_id' => $_SESSION['customer_id'],
            'customers_name' => $order->customer['firstname'] . ' ' . $order->customer['lastname'],
            'customers_company' => $order->customer['company'],
            'customers_street_address' => $order->customer['street_address'],
            'customers_suburb' => $order->customer['suburb'],
            'customers_city' => $order->customer['city'],
            'customers_postcode' => $order->customer['postcode'],
            'customers_state' => $order->customer['state'],
            'customers_country' => $order->customer['country']['title'],
            'customers_telephone' => $order->customer['telephone'],
            'customers_email_address' => $order->customer['email_address'],
            'customers_address_format_id' => $order->customer['format_id'],
            'delivery_name' => $order->delivery['firstname'] . ' ' . $order->delivery['lastname'],
            'delivery_company' => $order->delivery['company'],
            'delivery_street_address' => $order->delivery['street_address'],
            'delivery_suburb' => $order->delivery['suburb'],
            'delivery_city' => $order->delivery['city'],
            'delivery_postcode' => $order->delivery['postcode'],
            'delivery_state' => $order->delivery['state'],
            'delivery_country' => $order->delivery['country']['title'],
            'delivery_address_format_id' => $order->delivery['format_id'],
            'billing_name' => $order->billing['firstname'] . ' ' . $order->billing['lastname'],
            'billing_company' => $order->billing['company'],
            'billing_street_address' => $order->billing['street_address'],
            'billing_suburb' => $order->billing['suburb'],
            'billing_city' => $order->billing['city'],
            'billing_postcode' => $order->billing['postcode'],
            'billing_state' => $order->billing['state'],
            'billing_country' => $order->billing['country']['title'],
            'billing_address_format_id' => $order->billing['format_id'],
            'payment_method' => 'Credit Card Payment', //邮件中显示的支付方式名称
            'payment_module_code' => $this->code,
            'shipping_method' => $order->info['shipping_method'],
            'shipping_module_code' => (strpos($order->info['shipping_module_code'], '_') > 0 ? substr($order->info['shipping_module_code'], 0, strpos($order->info['shipping_module_code'], '_')) : $order->info['shipping_module_code']) ,
            'coupon_code' => $order->info['coupon_code'],
            'cc_type' => $order->info['cc_type'],
            'cc_owner' => $order->info['cc_owner'],
            'cc_number' => $order->info['cc_number'],
            'cc_expires' => $order->info['cc_expires'],
            'date_purchased' => 'now()',
            'orders_status' => $order->info['order_status'],
            'order_total' => $order->info['total'],
            'order_tax' => $order->info['tax'],
            'currency' => $order->info['currency'],
            'currency_value' => $order->info['currency_value'],
            'ip_address' => $_SESSION['customers_ip_address'] . ' - ' . $_SERVER['REMOTE_ADDR']
        );
        zen_db_perform(TABLE_ORDERS, $sql_data_array);
        $insert_id = $db->Insert_ID();
        $order->notify('NOTIFY_ORDER_DURING_CREATE_ADDED_ORDER_HEADER', array_merge(array(
            'orders_id' => $insert_id,
            'shipping_weight' => $_SESSION['cart']->weight
        ) , $sql_data_array));
        for ($i = 0, $n = sizeof($order_totals); $i < $n; $i++) {
            $sql_data_array = array(
                'orders_id' => $insert_id,
                'title' => $order_totals[$i]['title'],
                'text' => $order_totals[$i]['text'],
                'value' => (is_numeric($order_totals[$i]['value'])) ? $order_totals[$i]['value'] : '0',
                'class' => $order_totals[$i]['code'],
                'sort_order' => $order_totals[$i]['sort_order']
            );
            zen_db_perform(TABLE_ORDERS_TOTAL, $sql_data_array);
            $order->notify('NOTIFY_ORDER_DURING_CREATE_ADDED_ORDERTOTAL_LINE_ITEM', $sql_data_array);
        }
        $sql_data_array = array(
            'orders_id' => $insert_id,
            'orders_status_id' => $order->info['order_status'],
            'date_added' => 'now()',
            'customer_notified' => 0,
            'comments' => $order->info['comments']
        );
        zen_db_perform(TABLE_ORDERS_STATUS_HISTORY, $sql_data_array);
        $order->notify('NOTIFY_ORDER_DURING_CREATE_ADDED_ORDER_COMMENT', $sql_data_array);
        //生成订单产品信息
        $order->create_add_products($insert_id);
        return $insert_id;
    }
	
    function process_button() {
        global $db, $order, $order_totals, $currencies;
        $MD5key = MODULE_PAYMENT_TPAY_MD5KEY; // MD5私钥
        $v_mid = MODULE_PAYMENT_TPAY_SELLER; // 商户编号
        $v_ymd = date("Ymd"); // 订单产生日期
        $v_amount1 = number_format(($order->info['total']) * $currencies->get_value($order->info['currency']) , 2, '.', ''); //金额
        $v_amount = $v_amount1 * 100;
        $v_url = zen_href_link(FILENAME_CHECKOUT_PROCESS, '', 'SSL'); //返回地址
        $query = $db->Execute("select orders_status_id from " . DB_PREFIX . "orders_status where orders_status_name='Tpayunpaid' and language_id={$_SESSION['languages_id']} limit 1");
        if (!$query->RecordCount()) {
            die('Wrong order status: ' . $status);
        }
        $status_id = $query->fields['orders_status_id'];
        $array = $db->Execute("select * from " . TABLE_ORDERS . " where orders_status='" . $status_id . "' and payment_module_code = 'tpay' and

	customers_email_address = '" . $order->customer['email_address'] . "' and currency = '" . $order->info['currency'] . "' and order_total = '" . $order->info['total'] . "' order by orders_id desc limit 0, 1");
        if ($array->fields['orders_id']) {
            $v_oid = $array->fields['orders_id']; // 订单编号
            
        } else {
            //$v_oid = $this->create_order(); // 订单编号
			$order->info['payment_method'] = 'Credit Card Payment';
			$order->info['payment_module_code'] = $this->code;
            $v_oid = $order->create($order_totals, 2);
			$order->create_add_products($v_oid, false);
            
        }
        //$orderID = $v_mid . '-' . $v_oid . '-' . $v_ymd;
		$orderID = $v_oid;
        $v_orderstatus = '1'; // 商户配货状态，0为未配齐，1为已配齐

        switch($order->info['currency']) {
			case 'USD': $v_moneytype = '840'; break;
			case 'EUR': $v_moneytype = '978'; break;
			case 'GBP': $v_moneytype = '826'; break;
			case 'ARS': $v_moneytype = '032'; break;
			case 'AUD': $v_moneytype = '036'; break;
			case 'BRL': $v_moneytype = '986'; break;
			case 'CAD': $v_moneytype = '124'; break;
			case 'CHF': $v_moneytype = '756'; break;
			case 'CLF':
			case 'CLP': $v_moneytype = '990'; break;
			case 'CNY':
			case 'RMB': $v_moneytype = '156'; break;
			case 'COP': $v_moneytype = '170'; break;
			case 'DKK': $v_moneytype = '208'; break;
			case 'HKD': $v_moneytype = '344'; break;
			case 'IDR': $v_moneytype = '360'; break;
			case 'INR': $v_moneytype = '356'; break;
			case 'ILS': $v_moneytype = '376'; break;
			case 'JPY': $v_moneytype = '392'; break;
			case 'KRW': $v_moneytype = '410'; break;
			case 'MOP': $v_moneytype = '446'; break;
			case 'MXN': $v_moneytype = '484'; break;
			case 'MYR': $v_moneytype = '458'; break;
			case 'NLG': $v_moneytype = '528'; break;
			case 'NOK': $v_moneytype = '578'; break;
			case 'NZD': $v_moneytype = '554'; break;
			case 'PEN': $v_moneytype = '604'; break;
			case 'PHP': $v_moneytype = '608'; break;
			case 'RUB': $v_moneytype = '643'; break;
			case 'SEK': $v_moneytype = '752'; break;
			case 'SGD': $v_moneytype = '702'; break;
			case 'TWD': $v_moneytype = '901'; break;
			case 'ULU':
			case 'UYU': $v_moneytype = '858'; break;
			case 'VEF': $v_moneytype = '862'; break;
			case 'ZAR': $v_moneytype = '710'; break;
			default: $v_moneytype = ''; break;
		}

        $v_rcvname = $v_mid; // 收货人姓名，统一用商户编号的值代替
        $v_ordername = $order->billing['lastname'] . $order->billing['firstname']; // 订货人姓名
        $v_rcvaddr = $order->billing['street_address'] . $order->billing['city'] . $order->billing['country']['title']; // 收货人地址
        $bcity = $order->billing['city'];
        $v_rcvpost = $order->billing['postcode']; // 收货人邮政编码
        $email = $order->customer['email_address']; //收货人Email
        $telephone = $order->customer['telephone']; //收货人电话
        $signMsgVal = $MD5key . $v_mid . $orderID . $v_amount . $v_moneytype;
        if (MODULE_PAYMENT_TPAY_OSTYPE == 'MD5') {
            $v_md5info = $this->szComputeMD5Hash($signMsgVal, "O");
        } else {
            $v_md5info = $this->szComputeSHA1Hash($signMsgVal);
        }
        $v_ipaddress = $this->getip();
        $v_txntype = '01';
        $process_button_string = zen_draw_hidden_field('AcctNo', $v_mid) . zen_draw_hidden_field('OrderID', $orderID) . zen_draw_hidden_field('BAddress', $v_rcvaddr) . zen_draw_hidden_field('PostCode', $v_rcvpost) . zen_draw_hidden_field('Amount', $v_amount) . zen_draw_hidden_field('CurrCode', $v_moneytype) . zen_draw_hidden_field('HashValue', $v_md5info) . zen_draw_hidden_field('IPAddress', $v_ipaddress) . zen_draw_hidden_field('TxnType', $v_txntype) . zen_draw_hidden_field('BCity', $bcity) . zen_draw_hidden_field('Email', $email) . zen_draw_hidden_field('telephone', $telephone);
        return $process_button_string;
    }
    function before_process() {
        global $_POST, $order, $currencies, $messageStack;
        $AcctNo = $_POST["Par1"];
        $OrderID = $_POST["Par2"];
        $PGTxnID = $_POST["Par3"];
        $RespCode = $_POST["Par4"];
        $RespMsg = $_POST["Par5"];
        $Amount = $_POST["Par6"];
        $HashValue = $_POST["HashValue"];
        //$v_tempdate = explode('-', $OrderID);
        //MD5私钥
        $MD5key = MODULE_PAYMENT_TPAY_MD5KEY;
        $signMsgVal = $MD5key . $AcctNo . $OrderID . $PGTxnID . $RespCode . $RespMsg . $Amount;
        if (MODULE_PAYMENT_TPAY_OSTYPE == 'MD5') {
            $SignValue = $this->szComputeMD5Hash($signMsgVal, 'O');
        } else {
            $SignValue = $this->szComputeSHA1Hash($signMsgVal);
        }
        $this->v_oid = $OrderID;//$v_tempdate[1];
        $this->v_amount = $Amount / 100;
        if ($HashValue == $SignValue) {
            $status = 'Tpayapproved';
            $notify = 0;
            if ($RespCode == '00') {
                $_SESSION['cart']->reset(true);
                $notify = 1;
                $this->update_order_status($OrderID/*$v_tempdate[1]*/, $status, $PGTxnID, $notify);
                zen_redirect(zen_href_link(FILENAME_CHECKOUT_SUCCESS, '', 'SSL', true, false));
            } else if ($RespCode == 'OK') {
                $_SESSION['cart']->reset(true);
                $notify = 1;
                $this->update_order_status($OrderID/*$v_tempdate[1]*/, $status, $PGTxnID, $notify);
                zen_redirect(zen_href_link(FILENAME_CHECKOUT_SUCCESS, '', 'SSL', true, false));
            } else {
                $status = 'Tpaydeclined';
                $this->update_order_status($OrderID/*$v_tempdate[1]*/, $status, $PGTxnID, $notify);
                $messageStack->add_session('checkout_payment', 'The transaction fails', 'error');
                zen_redirect(zen_href_link(FILENAME_CHECKOUT_PAYMENT, '', 'SSL', true, false));
            }
        } else {
            $messageStack->add_session('checkout_payment', 'Validation failure 1', 'error');
            zen_redirect(zen_href_link(FILENAME_CHECKOUT_PAYMENT, '', 'SSL', true, false));
        }
    }
    function after_process() {
    }
    function update_order_status($order_id, $status, $transactionid, $notify = 0) {
        global $db, $order, $currencies;
        $query = $db->Execute("select orders_status_id from " . DB_PREFIX . "orders_status where orders_status_name='{$status}' and language_id={$_SESSION['languages_id']} limit 1");
        if (!$query->RecordCount()) {
            die('Wrong order status: ' . $status);
        }
        $status_id = $query->fields['orders_status_id'];
        $this->order_status = $status_id;
        $check_status = $db->Execute("select customers_name, customers_email_address, orders_status,

                                      date_purchased from " . TABLE_ORDERS . "

                                      where orders_id = '" . (int)$order_id . "'");
        if (($check_status->fields['orders_status'] != $status_id)) {
            $db->Execute("update " . TABLE_ORDERS . "

                        set orders_status = '" . zen_db_input($status_id) . "', last_modified = now()

                        where orders_id = '" . (int)$order_id . "'");
            if ($notify) {
                $order->products_ordered = '';
                $order->products_ordered_html = '';
                for ($i = 0, $n = sizeof($order->products); $i < $n; $i++) {
                    $this->products_ordered_attributes = '';
                    if (isset($order->products[$i]['attributes'])) {
                        $attributes_exist = '1';
                        for ($j = 0, $n2 = sizeof($order->products[$i]['attributes']); $j < $n2; $j++) {
                            $this->products_ordered_attributes.= "\n\t" . $order->products[$i]['attributes'][$j]['option'] . ' ' . zen_decode_specialchars($order->products[$i]['attributes'][$j]['value']);
                        }
                    }
                    $order->products_ordered.= $order->products[$i]['qty'] . ' x ' . $order->products[$i]['name'] . ($order->products[$i]['model'] != '' ? ' (' . $order->products[$i]['model'] . ') ' : '') . ' = ' . $currencies->display_price($order->products[$i]['final_price'], $order->products[$i]['tax'], $order->products[$i]['qty']) . ($order->products[$i]['onetime_charges'] != 0 ? "\n" . TEXT_ONETIME_CHARGES_EMAIL . $currencies->display_price($this->products[$i]['onetime_charges'], $order->products[$i]['tax'], 1) : '') . $this->products_ordered_attributes . "\n";
                    $order->products_ordered_html.= '<tr>' . "\n" . '<td class="product-details" align="right" valign="top" width="30">' . $order->products[$i]['qty'] . '&nbsp;x</td>' . "\n" . '<td class="product-details" valign="top">' . nl2br($order->products[$i]['name']) . ($order->products[$i]['model'] != '' ? ' (' . nl2br($order->products[$i]['model']) . ') ' : '') . "\n" . '<nobr>' . '<small><em> ' . nl2br($this->products_ordered_attributes) . '</em></small>' . '</nobr>' . '</td>' . "\n" . '<td class="product-details-num" valign="top" align="right">' . $currencies->display_price($order->products[$i]['final_price'], $order->products[$i]['tax'], $order->products[$i]['qty']) . ($order->products[$i]['onetime_charges'] != 0 ? '</td></tr>' . "\n" . '<tr><td class="product-details">' . nl2br(TEXT_ONETIME_CHARGES_EMAIL) . '</td>' . "\n" . '<td>' . $currencies->display_price($order->products[$i]['onetime_charges'], $order->products[$i]['tax'], 1) : '') . '</td></tr>' . "\n";
                }
                $order->send_order_email($order_id, 2);
            }
            $db->Execute("insert into " . TABLE_ORDERS_STATUS_HISTORY . "

                      (orders_id, orders_status_id, date_added, customer_notified, comments)

                      values ('" . zen_db_input($order_id) . "',

                      '" . zen_db_input($status_id) . "',

                      now(),

                      '" . zen_db_input($notify) . "',

                      'Pay notice [tpay transactionid: {$transactionid}]')");
        }
    }
    function output_error() {
        return false;
    }
    function check() {
        global $db;
        if (!isset($this->_check)) {
            $check_query = $db->Execute("select configuration_value from " . TABLE_CONFIGURATION . " where configuration_key = 'MODULE_PAYMENT_TPAY_STATUS'");
            $this->_check = $check_query->RecordCount();
        }
        return $this->_check;
    }
    function install() {
        global $db, $language, $module_type;
        $this->add_order_status();
        if (!defined('MODULE_PAYMENT_TPAY_TEXT_CONFIG_1_1')) include (DIR_FS_CATALOG_LANGUAGES . $_SESSION['language'] . '/modules/' . $module_type . '/' . $this->code . '.php');
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_1_1 . "', 'MODULE_PAYMENT_TPAY_STATUS', 'True', '" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_1_2 . "', '6', '0', 'zen_cfg_select_option(array(\'True\', \'False\'), ', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_2_1 . "', 'MODULE_PAYMENT_TPAY_SELLER', '888', '" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_2_2 . "', '6', '2', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_3_1 . "', 'MODULE_PAYMENT_TPAY_MD5KEY', 'test', '" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_3_2 . "', '6', '4', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_4_1 . "', 'MODULE_PAYMENT_TPAY_MONEYTYPE', 'USD', '" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_4_2 . "', '6', '4', 'zen_cfg_select_option(array( \'USD\',\'CNY\',\'GBP\',\'EUR\',\'JPY\',\'HKD\',\'NLG\',\'DKK\',\'NOK\',\'NZD\'), ', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_5_1 . "', 'MODULE_PAYMENT_TPAY_OSTYPE', 'MD5', '" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_5_2 . "', '6', '4', 'zen_cfg_select_option(array(\'MD5\', \'SHA1\'), ', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, use_function, set_function, date_added) values ('" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_6_1 . "', 'MODULE_PAYMENT_TPAY_ZONE', '0', '" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_6_2 . "', '6', '6', 'zen_get_zone_class_title', 'zen_cfg_pull_down_zone_classes(', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, use_function, date_added) values ('" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_7_1 . "', 'MODULE_PAYMENT_TPAY_ORDER_STATUS_ID', '803', '" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_7_2 . "', '6', '10', 'zen_cfg_pull_down_order_statuses(', 'zen_get_order_status_name', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_8_1 . "', 'MODULE_PAYMENT_TPAY_SORT_ORDER', '0', '" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_8_2 . "', '6', '12', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_9_1 . "', 'MODULE_PAYMENT_TPAY_HANDLER', './pay.php', '" . MODULE_PAYMENT_TPAY_TEXT_CONFIG_9_2 . "', '6', '18', '', now())");
    }
    function remove() {
        global $db;
        $db->Execute("delete from " . TABLE_CONFIGURATION . " where configuration_key LIKE  'MODULE_PAYMENT_TPAY%'");
    }
    function keys() {
        return array(
            'MODULE_PAYMENT_TPAY_STATUS',
            'MODULE_PAYMENT_TPAY_SELLER',
            'MODULE_PAYMENT_TPAY_MD5KEY',
            'MODULE_PAYMENT_TPAY_ZONE',
            'MODULE_PAYMENT_TPAY_MONEYTYPE',
            'MODULE_PAYMENT_TPAY_OSTYPE',
            'MODULE_PAYMENT_TPAY_ORDER_STATUS_ID',
            'MODULE_PAYMENT_TPAY_SORT_ORDER',
            'MODULE_PAYMENT_TPAY_HANDLER'
        );
    }
    //添加rp支付状态
    private function add_order_status() {
        global $db;
        $languages = $db->Execute("select languages_id from " . DB_PREFIX . "languages");
        while (!$languages->EOF) {
            $language_id = $languages->fields['languages_id'];
            $status = $db->Execute("select * from " . DB_PREFIX . "orders_status where language_id={$language_id} and orders_status_name='Tpayapproved'");
            if (!$status->RecordCount()) {
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(700, {$language_id}, 'Tpayapproved')"); //支付成功
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(701, {$language_id}, 'Tpaydeclined')"); //支付失败
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(702, {$language_id}, 'Tpayrefund')"); //退款
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(703, {$language_id}, 'Tpayunpaid')"); //未付款
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(704, {$language_id}, 'Tpaypending')"); //交易处理中
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(705, {$language_id}, 'Tpayerror')"); //支付出错
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(706, {$language_id}, 'Tpaytestapprove')"); //测试
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(707, {$language_id}, 'Tpaycanceled')"); //付款取消
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(708, {$language_id}, 'Tpaychargeback')"); //拒付
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(709, {$language_id}, 'Tpayfraud')"); //欺诈
                
            }
            $languages->MoveNext();
        }
    }
}
?>