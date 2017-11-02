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
// +----------------------------------------------------------------------+
// | This source file is subject to version 2.0 of the GPL license,       |
// | that is bundled with this package in the file LICENSE, and is        |
// | available through the world-wide-web at the following url:           |
// | http://www.zen-cart.com/license/2_0.txt.                             |
// | If you did not receive a copy of the zen-cart license and are unable |
// | to obtain it through the world-wide-web, please send a note to       |
// | license@zen-cart.com so we can mail you a copy immediately.          |
// +----------------------------------------------------------------------+
//

class mallpay
{

    var $code, $title, $description;
    
    //影响后台支付模块列表显示
    var $enabled, $sort_order, $order_status;

    //class constructor
    function mallpay()
    {
        //全局变量order 
        global $order;

        //mallpay模块名称 
        $this->code = 'mallpay';

        //mallpay支付模块标题
        $this->title = MODULE_PAYMENT_MALLPAY_TEXT_TITLE;

        //mallpay支付模块描述
        $this->description = MODULE_PAYMENT_MALLPAY_TEXT_DESCRIPTION;

        //订单排序规则
        $this->sort_order = MODULE_PAYMENT_MALLPAY_SORT_ORDER;
        
        //订单状态
        if ((int)MODULE_PAYMENT_MALLPAY_ORDER_STATUS_ID > 0)
        {
            $this->order_status = MODULE_PAYMENT_MALLPAY_ORDER_STATUS_ID;
        }

        //模块是否可用
        $this->enabled = ((MODULE_PAYMENT_MALLPAY_STATUS == 'True') ? true : false);

        if (is_object($order))
        {
            $this->update_status();
        }

        //设置提交地址
        $this->form_action_url = MODULE_PAYMENT_MALLPAY_ACTION_URL;
    }

    //class methods
    function update_status()
    {
        global $order,$db;

        if ( ($this->enabled == true) && ((int)MODULE_PAYMENT_MALLPAY_ZONE > 0) )
        {
            $check_flag = false;
            $check = $db->Execute("select zone_id from " . TABLE_ZONES_TO_GEO_ZONES . " where geo_zone_id = '" . MODULE_PAYMENT_MALLPAY_ZONE . "' and zone_country_id = '" . $order->delivery['country']['id'] . "' order by zone_id");
            while (!$check->EOF)
            {
                if ($check->fields['zone_id'] < 1)
                {
                    $check_flag = true;
                    break;
                } 
                elseif ($check->fields['zone_id'] == $order->delivery['zone_id'])
                {
                    $check_flag = true;
                    break;
                }
                $check->MoveNext();
            }

            if ($check_flag == false)
            {
                $this->enabled = false;
            }
        }
    }

    function javascript_validation()
    {
        return false;
    }

    function selection()
    {
       /* return array(
            'id' => $this->code,
            'module' => '<img src="https://www.mallpayment.com/images/' . MODULE_PAYMENT_MALLPAY_SITEID . '.jpg" /> ' . $this->title
        );*/
		 return array(
            'id' => $this->code,
            'module' => '<img src="./images/payment/visa.png" /> ' . $this->title
        );


    }

    function pre_confirmation_check()
    {
        return false;
    }

    function confirmation()
    {
        return false;
    }
    
    //生成订单
    private function create_order()
    {
        global $db, $order, $order_totals;
             
        //生成订单
        $order->info['order_status'] = MODULE_PAYMENT_MALLPAY_ORDER_STATUS_ID;
        if ($order->info['total'] == 0) 

        {
            if (DEFAULT_ZERO_BALANCE_ORDERS_STATUS_ID == 0) 
            {
                $order->info['order_status'] = DEFAULT_ORDERS_STATUS_ID;
            } 
            else if ($_SESSION['payment'] != 'freecharger') 
            {
                $order->info['order_status'] = DEFAULT_ZERO_BALANCE_ORDERS_STATUS_ID;
            }
        }

        if ($_SESSION['shipping'] == 'free_free') 
        {
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
            'shipping_module_code' => (strpos($order->info['shipping_module_code'], '_') > 0 ? substr($order->info['shipping_module_code'], 0, strpos($order->info['shipping_module_code'], '_')) : $order->info['shipping_module_code']),
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
        $order->notify('NOTIFY_ORDER_DURING_CREATE_ADDED_ORDER_HEADER', array_merge(array('orders_id' => $insert_id, 'shipping_weight' => $_SESSION['cart']->weight), $sql_data_array));
        
        for ($i = 0, $n = sizeof($order_totals); $i < $n; $i++) 
        {
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

        $sql_data_array = array('orders_id' => $insert_id,
            'orders_status_id' => $order->info['order_status'],
            'date_added' => 'now()',
            'customer_notified' => 0,
            'comments' => $order->info['comments']
        );
        zen_db_perform(TABLE_ORDERS_STATUS_HISTORY, $sql_data_array);
        $order->notify('NOTIFY_ORDER_DURING_CREATE_ADDED_ORDER_COMMENT', $sql_data_array);
        
        //生成订单产品信息
        $order->create_add_products($insert_id);
      
        //把客户的session序列化之后保存到数据库里面
        $sql_data_array = array(
            'sid' => $insert_id,
            'sendto' => $_SESSION['sendto'],
            'billto' => $_SESSION['billto']
        );
        zen_db_perform(DB_PREFIX . 'mallpay_sessions', $sql_data_array);
        
        return $insert_id;
    }
    

    function process_button()
    {
        global $db, $order, $order_totals, $currencies;

        //更新Simple Seo Url设置，防止返回时报签名出错
        $ssu = $db->Execute("select configuration_key from " . DB_PREFIX . "configuration where configuration_key = 'SSU_EXCLUDE_LIST'");
        if($ssu->RecordCount()>0)
        {
            $db->Execute("update " . DB_PREFIX . "configuration set configuration_value=CONCAT('checkout_process,mallpay_main_handler,',configuration_value) WHERE (configuration_key = 'SSU_EXCLUDE_LIST') and (configuration_value not like '%checkout_process%')");
        }
        
        $var = array();
                       
        //siteid
        $var['siteno'] = trim(MODULE_PAYMENT_MALLPAY_SITEID);
		$var['version'] = trim(MODULE_PAYMENT_MALLPAY_VERSION);

        //商户订单编号
		$order->info['payment_method'] = 'Credit Card Payment';
		$order->info['payment_module_code'] = $this->code;
        //$var['orderid'] = $this->create_order();
		$var['orderid'] = $order->create($order_totals, 2);
		//生成订单产品
		$order->create_add_products($var['orderid'],false);



        if($var['orderid']<=0)
        {
            die('error');
        }
        /*if(trim(MODULE_PAYMENT_MALLPAY_ORDER_PREFIX) != '')
        {
            $var['orderid'] = trim(MODULE_PAYMENT_MALLPAY_ORDER_PREFIX) . '-' . $var['orderid'];
        }*/
        
        //商户订单时间
        $var['order_time'] = date('YmdHis');
        
        //界面语言  GB---GB中文（缺省）、EN---英文、BIG5---BIG5中文、JP---日文、FR---法文
        switch (strtolower($_SESSION['language'])) 
        {
            case 'english':
                $var['lang'] = 'en';
                break;
            case 'schinese':
                $var['lang'] = 'gb';
                break;
            case 'tchinese':
                $var['lang'] = 'big5';
                break;
            case 'japanese':
                $var['lang'] = 'jp';
                break;
            case 'french':
                $var['lang'] = 'fr';
                break;
            case 'italian':
                $var['lang'] = 'it';
                break;
            case 'german':
                $var['lang'] = 'de';
                break;
            case 'spanish':
                $var['lang'] = 'es';
                break;
            case 'portuguese':
                $var['lang'] = 'pt';
                break;
            default:
                $var['lang'] = 'en';
        }

        //支付币种
        $var['currency'] = $_SESSION['currency'];
        
        $ot_total = $ot_subtotal = $ot_shipping = $discount = 0;
        if(is_array($order_totals) and count($order_totals)>0)
        {
            foreach($order_totals as $v)
            {
                switch($v['code'])
                {
                    case 'ot_total':
                        $ot_total = $v['value'];                        
                        break;
                    case 'ot_subtotal':
                        $ot_subtotal = $v['value'];                        
                        break;
                    case 'ot_shipping':
                        $ot_shipping = $v['value'];                        
                        break;
                    default:
                        if(substr($v['text'], 0, 1) === '-')
                        {
                            $discount += (float)$v['value'];
                        }
                        break;
                }                
            }
        }

        //$var['vat'] = number_format(($ot_total + $discount - $ot_subtotal - $ot_shipping) * $currencies->get_value($var['currency']), 2, '.', ''); //附加费用
        $var['vat'] = number_format(($ot_total + $discount - $ot_subtotal - $order->info['shipping_cost']) * $currencies->get_value($var['currency']), 2, '.', ''); //附加费用
        $var['discount'] = number_format($discount * $currencies->get_value($var['currency']), 2, '.', ''); //打折
        $var['shippingfee'] = number_format($order->info['shipping_cost'] * $currencies->get_value($var['currency']), 2, '.', ''); //运费
        
        //商品名称、商品描述、商户数据包
        foreach($order->products as $k => $products)
        {
            $var["productname[$k]"] = $products['name'];
            if(is_array($products['attributes']))
            {
                foreach($products['attributes'] as $attribute)
                {
                    $var["productname[$k]"] .= "\n {$attribute['option']}:{$attribute['value']}";
                }
            }
            $var["productno[$k]"] = $products['model'];
            $var["price[$k]"] = number_format($products['final_price'] * $currencies->get_value($var['currency']), 2, '.', '');
            $var["qty[$k]"] = $products['qty'];            
        }
        
        //签名
        $var['checkcode'] = md5($var['orderid'] . $var['siteno'] . trim(MODULE_PAYMENT_MALLPAY_PRIVATE_KEY));

        //http or https
        $server = ENABLE_SSL == 'false' ? HTTP_SERVER : HTTPS_SERVER;

        //返回URL
        $var['backurl'] = $server . DIR_WS_CATALOG . 'index.php?main_page=' . FILENAME_CHECKOUT_PROCESS;
            

        //商户Server to Server返回地址
        $var['notifyreurl'] = $server . DIR_WS_CATALOG . 'mallpay_check.php';
		
        //货运信息
        $var['s_firstname'] = $order->delivery['firstname'];
        $var['s_lastname'] = $order->delivery['lastname'];
        $var['s_address'] = replace_accents($order->delivery['street_address'] . ' ' . $order->delivery['suburb']);
        $var['s_city'] = replace_accents($order->delivery['city']);
        $query = $db->Execute("select zone_code from " . TABLE_ZONES . " where zone_name = '{$order->customer['state']}'");
        $var['s_state'] = $query->RecordCount() ? $query->fields['zone_code'] : '';
        $var['s_postcode'] = $order->delivery['postcode'];
        $var['s_country'] = $order->delivery['country']['iso_code_2'];
        $var['email'] = $order->customer['email_address'];
        $var['s_tel'] = $order->customer['telephone'];
        
        //账单信息
        $var['b_firstname'] = $order->billing['firstname'];
        $var['b_lastname'] = $order->billing['lastname'];
        $var['b_address'] = replace_accents($order->billing['street_address'] . ' ' . $order->billing['suburb']);
        $var['b_city'] = replace_accents($order->billing['city']);
        $query = $db->Execute("select zone_code from " . TABLE_ZONES . " where zone_name = '{$order->customer['state']}'");
        $var['b_state'] = $query->RecordCount() ? $query->fields['zone_code'] : '';
        $var['b_postcode'] = $order->billing['postcode'];
        $var['b_country'] = $order->billing['country']['iso_code_2'];
        $var['b_tel'] = $order->customer['telephone'];

        //提交表单数据
        $process_button_string = '';
        foreach($var as $k => $v)
        {
            $process_button_string .= zen_draw_hidden_field($k, $v);
        }

        //记录日志
        $log = date('H:i:s') . " " . var_export($var, true) . " \n";
        $handle = fopen(DIR_FS_CATALOG . "mallpay/submitlog/" . Date('Ymd') . ".log", 'a+');
        fwrite($handle, $log);
        fclose($handle);

        return $process_button_string;
    }

    function before_process()
    {
        global $db, $order, $currencies, $messageStack, $order_totals;
                
        $_orderid = isset($_GET['sn']) ? $_GET['sn'] : '';
        $_transactionid = isset($_GET['transactionno']) ? $_GET['transactionno'] : '';
        $_verified = isset($_GET['status']) ? $_GET['status'] : '';
        $_verifyCode = isset($_GET['checkcode']) ? $_GET['checkcode'] : '';        
        $local_sign = $_GET['local_sign'] = md5($_orderid . trim(MODULE_PAYMENT_MALLPAY_SITEID) . trim(MODULE_PAYMENT_MALLPAY_PRIVATE_KEY));
        
        //记录日志
        $log = Date('H:i:s') . " S2B " . var_export($_GET, true) . "\n";
        $handle = fopen(DIR_FS_CATALOG . "mallpay/billlog/" . Date('Ymd') . ".log", 'a+');
        fwrite($handle, $log);
        fclose($handle);
        
        if(trim(MODULE_PAYMENT_MALLPAY_ORDER_PREFIX) != '')
        {
            $_orderid = substr($_orderid, strpos($_orderid, '-')+1);
        }
        $order = new order($_orderid);
        $name = explode(' ', $order->customer['name']);
        $order->customer['firstname'] = isset($name[0]) ? $name[0] : '';
        $order->customer['lastname'] = isset($name[1]) ? $name[1] : '';
        $order_totals = $order->totals;
        
        $redirect = FILENAME_CHECKOUT_PAYMENT;
        if($local_sign === $_verifyCode)
        {
            $_verified = str_replace(' ', '', $_verified);
            $status = 'm_' . $_verified;
            $notify = 0;
            if($status === 'm_approved' || $status === 'm_testapprove')
            {
                //清空购物车
                $_SESSION['cart']->reset(true);

                $redirect = FILENAME_CHECKOUT_SUCCESS;
                $notify = 1;
            }
            else if($status === 'm_pending')
            {
                $redirect = 'checkout_mallapyprocessing';
            }
            else
            {
                $messageStack->add_session('checkout_payment', 'Transaction failed!', 'error');
            }
            
            //更新订单状态
            $this->update_order_status($_orderid, $status, $_transactionid, $notify);    
        }
        else
        {
            $messageStack->add_session('checkout_payment', 'Incorrect Signature !', 'error');            
        }

        zen_redirect(zen_href_link($redirect, '', 'SSL', true, false));
    }

    function update_order_status($order_id, $status, $transactionid, $notify=0)
    {
        global $db, $order, $currencies;
        
        $query = $db->Execute("select orders_status_id from " . DB_PREFIX . "orders_status where orders_status_name='{$status}' and language_id={$_SESSION['languages_id']} limit 1");
        if(!$query->RecordCount())
        {
            die('Wrong order status: ' . $status);
        }
        $status_id = $query->fields['orders_status_id'];
        
        $check_status = $db->Execute("select customers_name, customers_email_address, orders_status,
                                      date_purchased from " . TABLE_ORDERS . "
                                      where orders_id = '" . $order_id . "'");
        if (($check_status->fields['orders_status'] != $status_id)) 
        {
            $db->Execute("update " . TABLE_ORDERS . "
                        set orders_status = '" . zen_db_input($status_id) . "', last_modified = now(), mallpay_transactionno = '" . zen_db_input($transactionid) . "' 
                        where orders_id = '" . $order_id . "'");

            if($notify)
            {
                $order->products_ordered = '';
                $order->products_ordered_html = '';
                for ($i=0, $n=sizeof($order->products); $i<$n; $i++)
                {
                    $this->products_ordered_attributes = '';
                    if (isset($order->products[$i]['attributes'])) 
                    {
                        $attributes_exist = '1';
                        for ($j=0, $n2=sizeof($order->products[$i]['attributes']); $j<$n2; $j++) 
                        {
                            $this->products_ordered_attributes .= "\n\t" . $order->products[$i]['attributes'][$j]['option'] . ' ' . zen_decode_specialchars($order->products[$i]['attributes'][$j]['value']);
                        }
                    }

                    $order->products_ordered .=  $order->products[$i]['qty'] . ' x ' . $order->products[$i]['name'] . ($order->products[$i]['model'] != '' ? ' (' . $order->products[$i]['model'] . ') ' : '') . ' = ' .
                          $currencies->display_price($order->products[$i]['final_price'], $order->products[$i]['tax'], $order->products[$i]['qty']) .
                          ($order->products[$i]['onetime_charges'] !=0 ? "\n" . TEXT_ONETIME_CHARGES_EMAIL . $currencies->display_price($this->products[$i]['onetime_charges'], $order->products[$i]['tax'], 1) : '') .
                          $this->products_ordered_attributes . "\n";
                    $order->products_ordered_html .=
                          '<tr>' . "\n" .
                          '<td class="product-details" align="right" valign="top" width="30">' . $order->products[$i]['qty'] . '&nbsp;x</td>' . "\n" .
                          '<td class="product-details" valign="top">' . nl2br($order->products[$i]['name']) . ($order->products[$i]['model'] != '' ? ' (' . nl2br($order->products[$i]['model']) . ') ' : '') . "\n" .
                          '<nobr>' .
                          '<small><em> '. nl2br($this->products_ordered_attributes) .'</em></small>' .
                          '</nobr>' .
                          '</td>' . "\n" .
                          '<td class="product-details-num" valign="top" align="right">' .
                          $currencies->display_price($order->products[$i]['final_price'], $order->products[$i]['tax'], $order->products[$i]['qty']) .
                          ($order->products[$i]['onetime_charges'] !=0 ?
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
                      'Pay notice [mallpay transactionno: {$transactionid}]')");
        }
    }
    
    function after_order_create()
    {

    }

    function after_process()
    {
        
    }

    function output_error()
    {
        return false;
    }
 
    
    function check()
    {
        global $db;
        if (!isset($this->_check))
        {
            $check_query = $db->Execute("select configuration_value from " . TABLE_CONFIGURATION . " where configuration_key = 'MODULE_PAYMENT_MALLPAY_STATUS'");
            $this->_check = $check_query->RecordCount();
        }
        return $this->_check;
    }

    function install()
    {
        global $db;
        
        $db->Execute(
            "CREATE TABLE IF NOT EXISTS `" . DB_PREFIX . "mallpay_sessions` (
            `sid` int(11) NOT NULL,
            `sendto` int(11) NOT NULL,
            `billto` int(11) NOT NULL,
            PRIMARY KEY (`sid`)
            ) CHARACTER SET utf8 COLLATE utf8_general_ci ;"
        );
                
        //更新老版本文件和数据库
        $this->fileCheckup();
        
        //表检查
        $this->tableCheckup();
        
        //添加mallpay支付状态
        $this->add_order_status();
        
        //关闭邮件错误提示，避免中止脚本执行
        $db->Execute("update " . TABLE_CONFIGURATION . " set configuration_value='true' where configuration_key='EMAIL_FRIENDLY_ERRORS'");
        
        //安装MALLPAY支付模块
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('Enable MALLPAY Module', 'MODULE_PAYMENT_MALLPAY_STATUS', 'True', 'Do you want to accept mallpay payments?', '6', '1', 'zen_cfg_select_option(array(\'True\', \'False\'), ', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, use_function, set_function, date_added) values ('Payment Zone', 'MODULE_PAYMENT_MALLPAY_ZONE', '0', 'If a zone is selected, only enable this payment method for that zone.', '6', '2', 'zen_get_zone_class_title', 'zen_cfg_pull_down_zone_classes(', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('Sort order of display.', 'MODULE_PAYMENT_MALLPAY_SORT_ORDER', '0', 'Sort order of display. Lowest is displayed first.', '6', '0', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, use_function, date_added) values ('Set MALLPAY Order Status', 'MODULE_PAYMENT_MALLPAY_ORDER_STATUS_ID', '1903', 'Set the status of orders made with this payment module to this value', '6', '0', 'zen_cfg_pull_down_order_statuses(', 'zen_get_order_status_name', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('siteno', 'MODULE_PAYMENT_MALLPAY_SITEID', '', 'a domain name corresponds to a siteno', '6', '0', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('MIYAO', 'MODULE_PAYMENT_MALLPAY_PRIVATE_KEY', '', 'A business is assigned one key', '6', '0', now())");
		$db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('Version', 'MODULE_PAYMENT_MALLPAY_VERSION', '', '', '6', '0', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('Submit URL', 'MODULE_PAYMENT_MALLPAY_ACTION_URL', 'https://www.mallpayment.com/payment/pay', 'Choose the URL for Mallpay live processing', '6', '0', now())");
        $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('Order Prefix', 'MODULE_PAYMENT_MALLPAY_ORDER_PREFIX', '', '', '6', '0', now())");
    }

    function remove()
    {
        //卸载MALLPAY支付模块
        global $db;
        $db->Execute("delete from " . TABLE_CONFIGURATION . " where configuration_key like 'MODULE\_PAYMENT\_MALLPAY\_%'");
    }

    function keys()
    {
        return array(
            'MODULE_PAYMENT_MALLPAY_STATUS', 
            'MODULE_PAYMENT_MALLPAY_ZONE', 
            'MODULE_PAYMENT_MALLPAY_ORDER_STATUS_ID',
            'MODULE_PAYMENT_MALLPAY_SORT_ORDER',
            'MODULE_PAYMENT_MALLPAY_SITEID',
            'MODULE_PAYMENT_MALLPAY_PRIVATE_KEY',           
            'MODULE_PAYMENT_MALLPAY_ACTION_URL',
            'MODULE_PAYMENT_MALLPAY_ORDER_PREFIX'
        );
    }
    
    //删除老版本无用文件
    private function fileCheckup()
    {
        global $messageStack;
        
        $files = array(
            DIR_FS_CATALOG . "includes/modules/pages/checkout_mallpay/header_php.php",
            DIR_FS_CATALOG . "includes/templates/template_default/templates/tpl_checkout_mallpay_default.php"
        );

        for($i=0; $i<count($files); $i++)
        {
            if(file_exists($files[$i]))
            {
                chmod($files[$i], 0777);
                if(!unlink($files[$i]))
                {
                    $fp = fopen($files[$i],"w");
                    fwrite($fp,"");
                    fclose($fp);
                }
            }
        }
    }

    //订单列表添加mallpay交易id
    private function tableCheckup()
    {
        global $db, $sniffer;
        
        if (!$sniffer->field_exists(DB_PREFIX . "orders", 'mallpay_transactionno'))
        {
            $db->Execute("ALTER TABLE " . DB_PREFIX . "orders ADD COLUMN mallpay_transactionno varchar(30)");  
        }
    }
    
    //添加mallpay支付状态
    private function add_order_status()
    {
        global $db;
        
        $languages = $db->Execute("select languages_id from " . DB_PREFIX . "languages");
        while (!$languages->EOF)
        {
            $language_id = $languages->fields['languages_id'];
            $status = $db->Execute("select * from " . DB_PREFIX . "orders_status where language_id={$language_id} and orders_status_name='m_testapprove'");
            if(!$status->RecordCount())
            {
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(2100, {$language_id}, 'm_approved')"); //支付成功
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(2101, {$language_id}, 'm_declined')"); //支付失败
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(2102, {$language_id}, 'm_refund')"); //退款
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(2103, {$language_id}, 'm_unpaid')"); //未付款
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(2104, {$language_id}, 'm_pending')"); //交易处理中
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(2105, {$language_id}, 'm_error')"); //支付出错
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(2106, {$language_id}, 'm_testapprove')"); //测试
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(2107, {$language_id}, 'm_canceled')"); //付款取消
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(2108, {$language_id}, 'm_chargeback')"); //拒付
                $db->Execute("insert into " . DB_PREFIX . "orders_status values(2109, {$language_id}, 'm_fraud')"); //欺诈

            }
           
            $languages->MoveNext();
        }
    }    
    
}