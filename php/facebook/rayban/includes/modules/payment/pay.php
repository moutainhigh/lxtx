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
//  $Id: pay.php 001 $
//
class pay {
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
    function pay() {
        global $order;
        $this->code = 'pay';
        if ($_GET['main_page'] != '') {
            $this->title = MODULE_PAYMENT_PAY_TEXT_CATALOG_TITLE; // Payment Module title in Catalog
            
        } else {
            $this->title = MODULE_PAYMENT_PAY_TEXT_ADMIN_TITLE; // Payment Module title in Admin
            
        }
        $this->description = MODULE_PAYMENT_PAY_TEXT_DESCRIPTION;
        $this->sort_order = MODULE_PAYMENT_PAY_SORT_ORDER;
        $this->enabled = ((MODULE_PAYMENT_PAY_STATUS == 'True') ? true : false);
        if ((int)MODULE_PAYMENT_PAY_ORDER_STATUS_ID > 0) {
            $this->order_status = MODULE_PAYMENT_PAY_ORDER_STATUS_ID;
        }
        if (is_object($order)) $this->update_status();
        $this->form_action_url = MODULE_PAYMENT_PAY_HANDLER;
    }
	
    // class methods
    function update_status() {
        global $order, $db;
        if (($this->enabled == true) && ((int)MODULE_PAYMENT_PAY_ZONE > 0)) {
            $check_flag = false;
            $check_query = $db->Execute("select zone_id from " . TABLE_ZONES_TO_GEO_ZONES . " where geo_zone_id = '" . MODULE_PAYMENT_PAY_ZONE . "' and zone_country_id = '" . $order->billing['country']['id'] . "' order by zone_id");
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
            'module' => MODULE_PAYMENT_PAY_TEXT_CATALOG_LOGO,
            'icon' => MODULE_PAYMENT_PAY_TEXT_CATALOG_LOGO
        );
    }
	
    function pre_confirmation_check() {
        return false;
    }
	
    function confirmation() {
        return array(
            'title' => MODULE_PAYMENT_PAY_TEXT_DESCRIPTION
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
        $order->info['order_status'] = MODULE_PAYMENT_PAY_ORDER_STATUS_ID;
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
        $MD5key = MODULE_PAYMENT_PAY_MD5KEY; // MD5私钥
        $v_mid = MODULE_PAYMENT_PAY_SELLER; // 商户编号
        $v_ymd = date("Ymd"); // 订单产生日期
        $v_amount1 = number_format(($order->info['total']) * $currencies->get_value($order->info['currency']) , 2, '.', ''); //金额
        $v_amount = $v_amount1 * 100;
        $v_url = zen_href_link(FILENAME_CHECKOUT_PROCESS, '', 'SSL'); //返回地址
        $query = $db->Execute("select orders_status_id from " . DB_PREFIX . "orders_status where orders_status_name='payunpaid' and language_id={$_SESSION['languages_id']} limit 1");
        if (!$query->RecordCount()) {
            die('Wrong order status: ' . $status);
        }
        $status_id = $query->fields['orders_status_id'];
        $array = $db->Execute("select * from " . TABLE_ORDERS . " where orders_status='" . $status_id . "' and payment_module_code = 'pay' and
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
		
        //$orderID = $v_ymd . $v_oid;
		$orderID = $v_oid;
        $v_orderstatus = '1'; // 商户配货状态，0为未配齐，1为已配齐
		
        switch($order->info['currency']) {
			case 'USD': $v_moneytype = '840'; break;
			case 'EUR': $v_moneytype = '978'; break;
			case 'GBP': $v_moneytype = '826'; break;
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
			case 'BAM': $v_moneytype = '997'; break;
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
			case 'CNY':
			case 'RMB': $v_moneytype = '156'; break;
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
			case 'FIM': $v_moneytype = '246'; break;
			case 'FJD': $v_moneytype = '242'; break;
			case 'FKP': $v_moneytype = '238'; break;
			case 'FRF': $v_moneytype = '250'; break;
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
			case 'INR': $v_moneytype = '356'; break;
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
			case 'RUB': $v_moneytype = '643'; break;
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
			case 'USN': $v_moneytype = '997'; break;
			case 'USS': $v_moneytype = '998'; break;
			case 'ULU':
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
			default: $v_moneytype = ''; break;
		}

        $v_ordername = $order->billing['firstname'] . $order->billing['lastname']; // 收货人姓名
        $v_rcvaddr = $order->billing['street_address']; // 收货人地址
        $bcity = $order->billing['city'];
        $bstate = $order->billing['state'];
        $v_Bcountry = $order->billing['country']['title'];
        $v_rcvpost = $order->billing['postcode']; // 收货人邮政编码
        $email = $order->customer['email_address']; //收货人Email
        $telephone = $order->customer['telephone']; //收货人电话
        $v_Url = $_SERVER["HTTP_HOST"];
        $sum = sizeof($order->products);
        for ($i = 0; $i < sizeof($order->products); $i++) {
            $_SESSION["PName" . $i] = $order->products[$i]["name"];
            $_SESSION["PModel" . $i] = $order->products[$i]["model"];
        }
        $signMsgVal = $MD5key . $v_mid . $orderID . $v_amount . $v_moneytype;
        if (MODULE_PAYMENT_PAY_OSTYPE == 'MD5') {
            $v_md5info = $this->szComputeMD5Hash($signMsgVal, "O");
        } else {
            $v_md5info = $this->szComputeSHA1Hash($signMsgVal);
        }
        $v_ipaddress = $this->getip();
        $v_txntype = '01';
		
        $process_button_string = zen_draw_hidden_field('AcctNo', $v_mid) . zen_draw_hidden_field('OrderID', $orderID) . zen_draw_hidden_field('BAddress', $v_rcvaddr) . zen_draw_hidden_field('PostCode', $v_rcvpost) . zen_draw_hidden_field('Amount', $v_amount) . zen_draw_hidden_field('CurrCode', $v_moneytype) . zen_draw_hidden_field('HashValue', $v_md5info) . zen_draw_hidden_field('IPAddress', $v_ipaddress) . zen_draw_hidden_field('TxnType', $v_txntype) . zen_draw_hidden_field('BCity', $bcity) . zen_draw_hidden_field('Email', $email) . zen_draw_hidden_field('telephone', $telephone) . zen_draw_hidden_field('CName', $v_ordername) . zen_draw_hidden_field('OrderUrl', $v_Url) . zen_draw_hidden_field('Bstate', $bstate) . zen_draw_hidden_field('Bcountry', $v_Bcountry) . zen_draw_hidden_field('Sum', $sum);
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
        //MD5私钥
        $MD5key = MODULE_PAYMENT_PAY_MD5KEY;
        $signMsgVal = $MD5key . $AcctNo . $OrderID . $PGTxnID . $RespCode . $RespMsg . $Amount;
        if (MODULE_PAYMENT_PAY_OSTYPE == 'MD5') {
            $SignValue = $this->szComputeMD5Hash($signMsgVal, 'O');
        } else {
            $SignValue = $this->szComputeSHA1Hash($signMsgVal);
        }
        //$this->v_oid = substr($OrderID, 8);
        $this->v_oid = $OrderID;
        $this->v_amount = $Amount / 100;
        if ($HashValue == $SignValue) {
            $status = 'payapproved';
            $notify = 0;
            if ($RespCode == '00') {
                $_SESSION['cart']->reset(true);
                $notify = 1;
                $this->update_order_status($this->v_oid, $status, $PGTxnID, $notify);
                zen_redirect(zen_href_link(FILENAME_CHECKOUT_SUCCESS, '', 'SSL', true, false));
            } else if ($RespCode == 'OK') {
                $_SESSION['cart']->reset(true);
                $notify = 1;
                $this->update_order_status($this->v_oid, $status, $PGTxnID, $notify);
                zen_redirect(zen_href_link(FILENAME_CHECKOUT_SUCCESS, '', 'SSL', true, false));
            } else {
                $status = 'paydeclined';
                $this->update_order_status($this->v_oid, $status, $PGTxnID, $notify);
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
                      'Pay notice [pay transactionid: {$transactionid}]')");
        }
    }
	
    function output_error() {
        return false;
    }
	
    function check() {
        global $db;
        if (!isset($this->_check)) {
            $check_query = $db->Execute("select configuration_value from " . TABLE_CONFIGURATION . " where configuration_key = 'MODULE_PAYMENT_PAY_STATUS'");
            $this->_check = $check_query->RecordCount();
        }
        return $this->_check;
    }
	
    function install() {
        global $db, $language, $module_type;
        $this->add_order_status();
        if (!defined('MODULE_PAYMENT_PAY_TEXT_CONFIG_1_1')) include (DIR_FS_CATALOG_LANGUAGES . $_SESSION['language'] . '/modules/' . $module_type . '/' . $this->code . '.php');
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('" . MODULE_PAYMENT_PAY_TEXT_CONFIG_1_1 . "', 'MODULE_PAYMENT_PAY_STATUS', 'True', '" . MODULE_PAYMENT_PAY_TEXT_CONFIG_1_2 . "', '6', '0', 'zen_cfg_select_option(array(\'True\', \'False\'), ', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('" . MODULE_PAYMENT_PAY_TEXT_CONFIG_2_1 . "', 'MODULE_PAYMENT_PAY_SELLER', '888', '" . MODULE_PAYMENT_PAY_TEXT_CONFIG_2_2 . "', '6', '2', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('" . MODULE_PAYMENT_PAY_TEXT_CONFIG_3_1 . "', 'MODULE_PAYMENT_PAY_MD5KEY', 'test', '" . MODULE_PAYMENT_PAY_TEXT_CONFIG_3_2 . "', '6', '4', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('" . MODULE_PAYMENT_PAY_TEXT_CONFIG_4_1 . "', 'MODULE_PAYMENT_PAY_MONEYTYPE', 'USD', '" . MODULE_PAYMENT_PAY_TEXT_CONFIG_4_2 . "', '6', '4', 'zen_cfg_select_option(array(\'AUD\',\'CAD\',\'CNY\',\'EUR\',\'GBP\',\'USD\'), ', now())");
		/*$db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('" . MODULE_PAYMENT_PAY_TEXT_CONFIG_4_1 . "', 'MODULE_PAYMENT_PAY_MONEYTYPE', 'USD', '" . MODULE_PAYMENT_PAY_TEXT_CONFIG_4_2 . "', '6', '4', 'zen_cfg_select_option(array( \'ADP\',\'AED\',\'AFA\',\'ALL\',\'AMD\',\'ANG\',\'AOA\',\'AON\',\'ARS\',\'ASF\',\'ATS\',\'AUD\',\'AWG\',\'AZM\',\'BAM\',\'BBD\',\'BDT\',\'BEF\',\'BGL\',\'BGN\',\'BHD\',\'BIF\',\'BMD\',\'BND\',\'BOB\',\'BOV\',\'BRL\',\'BSD\',\'BTN\',\'BWP\',\'BYB\',\'BYR\',\'BZD\',\'CAD\',\'CDF\',\'CHF\',\'CLF\',\'CLP\',\'CNY\',\'COP\',\'CRC\',\'CUP\',\'CVE\',\'CYP\',\'CZK\',\'DEM\',\'DJF\',\'DKK\',\'DOP\',\'DZD\',\'ECS\',\'ECV\',\'EEK\',\'EGP\',\'ERN\',\'ESP\',\'ETB\',\'EUR\',\'FIM\',\'FJD\',\'FKP\',\'FRF\',\'GBP\',\'GEL\',\'GHC\',\'GIP\',\'GMD\',\'GNF\',\'GRD\',\'GTQ\',\'GWP\',\'GYD\',\'HKD\',\'HNL\',\'HRK\',\'HTG\',\'HUF\',\'IDR\',\'IEP\',\'ILS\',\'INR\',\'IQD\',\'IRR\',\'ISK\',\'ITL\',\'JMD\',\'JOD\',\'JPY\',\'KES\',\'KGS\',\'KHR\',\'KMF\',\'KPW\',\'KRW\',\'KWD\',\'KYD\',\'KZT\',\'LAK\',\'LBP\',\'LKR\',\'LRD\',\'LSL\',\'LTL\',\'LUF\',\'LVL\',\'LYD\',\'MAD\',\'MDL\',\'MGF\',\'MKD\',\'MMK\',\'MNT\',\'MOP\',\'MRO\',\'MTL\',\'MUR\',\'MVR\',\'MWK\',\'MXN\',\'MXV\',\'MYR\',\'MZM\',\'NAD\',\'NGN\',\'NIO\',\'NLG\',\'NOK\',\'NPR\',\'NZD\',\'OMR\',\'PAB\',\'PAB\',\'PGK\',\'PHP\',\'PKR\',\'PLN\',\'PLZ\',\'PTE\',\'PYG\',\'QAR\',\'ROL\',\'RSD\',\'RUB\',\'RWF\',\'SAR\',\'SBD\',\'SCR\',\'SDD\',\'SDP\',\'SDR\',\'SEK\',\'SGD\',\'SHP\',\'SIT\',\'SKK\',\'SLL\',\'SOS\',\'SRG\',\'STD\',\'SVC\',\'SYP\',\'SZL\',\'THB\',\'TJR\',\'TJS\',\'TMM\',\'TND\',\'TOP\',\'TRL\',\'TTD\',\'TWD\',\'TZS\',\'UAH\',\'UAK\',\'UGX\',\'USD\',\'USN\',\'USS\',\'UYU\',\'UZS\',\'VEB\',\'VND\',\'VUV\',\'WST\',\'XAF\',\'XAG\',\'XAU\',\'XBA\',\'XBB\',\'XBC\',\'XBD\',\'XCD\',\'XDR\',\'XEU\',\'XOF\',\'XPD\',\'XPF\',\'XPT\',\'XTS\',\'XXX\',\'YER\',\'YUM\',\'YUN\',\'ZAL\',\'ZAR\',\'ZMK\',\'ZRN\',\'ZWD\'), ', now())");*/
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('" . MODULE_PAYMENT_PAY_TEXT_CONFIG_5_1 . "', 'MODULE_PAYMENT_PAY_OSTYPE', 'MD5', '" . MODULE_PAYMENT_PAY_TEXT_CONFIG_5_2 . "', '6', '4', 'zen_cfg_select_option(array(\'MD5\', \'SHA1\'), ', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, use_function, set_function, date_added) values ('" . MODULE_PAYMENT_PAY_TEXT_CONFIG_6_1 . "', 'MODULE_PAYMENT_PAY_ZONE', '0', '" . MODULE_PAYMENT_PAY_TEXT_CONFIG_6_2 . "', '6', '6', 'zen_get_zone_class_title', 'zen_cfg_pull_down_zone_classes(', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, use_function, date_added) values ('" . MODULE_PAYMENT_PAY_TEXT_CONFIG_7_1 . "', 'MODULE_PAYMENT_PAY_ORDER_STATUS_ID', '1', '" . MODULE_PAYMENT_PAY_TEXT_CONFIG_7_2 . "', '6', '10', 'zen_cfg_pull_down_order_statuses(', 'zen_get_order_status_name', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('" . MODULE_PAYMENT_PAY_TEXT_CONFIG_8_1 . "', 'MODULE_PAYMENT_PAY_SORT_ORDER', '1', '" . MODULE_PAYMENT_PAY_TEXT_CONFIG_8_2 . "', '6', '12', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('" . MODULE_PAYMENT_PAY_TEXT_CONFIG_9_1 . "', 'MODULE_PAYMENT_PAY_HANDLER', './pay.php', '" . MODULE_PAYMENT_PAY_TEXT_CONFIG_9_2 . "', '6', '18', '', now())");
    }
	
    function remove() {
        global $db;
        $db->Execute("delete from " . TABLE_CONFIGURATION . " where configuration_key LIKE  'MODULE_PAYMENT_PAY%'");
    }
	
    function keys() {
        return array(
            'MODULE_PAYMENT_PAY_STATUS',
            'MODULE_PAYMENT_PAY_SELLER',
            'MODULE_PAYMENT_PAY_MD5KEY',
            'MODULE_PAYMENT_PAY_ZONE',
            'MODULE_PAYMENT_PAY_MONEYTYPE',
            'MODULE_PAYMENT_PAY_OSTYPE',
            'MODULE_PAYMENT_PAY_ORDER_STATUS_ID',
            'MODULE_PAYMENT_PAY_SORT_ORDER',
            'MODULE_PAYMENT_PAY_HANDLER'
        );
    }
	
    //添加rp支付状态
    private function add_order_status() {
        global $db;
        $languages = $db->Execute("select languages_id from " . DB_PREFIX . "languages");
        while (!$languages->EOF) {
            $language_id = $languages->fields['languages_id'];
            $status = $db->Execute("select * from " . DB_PREFIX . "orders_status where language_id={$language_id} and orders_status_name='payapproved'");
            if (!$status->RecordCount()) {
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(6870, {$language_id}, 'payapproved')"); //支付成功
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(6871, {$language_id}, 'paydeclined')"); //支付失败
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(6872, {$language_id}, 'payrefund')"); //退款
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(6873, {$language_id}, 'payunpaid')"); //未付款
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(6874, {$language_id}, 'paypending')"); //交易处理中
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(6875, {$language_id}, 'payerror')"); //支付出错
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(6876, {$language_id}, 'paytestapprove')"); //测试
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(6877, {$language_id}, 'paycanceled')"); //付款取消
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(6878, {$language_id}, 'paychargeback')"); //拒付
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(6879, {$language_id}, 'payfraud')"); //欺诈
                
            }
            $languages->MoveNext();
        }
    }
}
?>