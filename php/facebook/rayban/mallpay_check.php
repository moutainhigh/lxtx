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

require('includes/application_top.php');

if(!defined('MODULE_PAYMENT_MALLPAY_SITEID')){
	define('MODULE_PAYMENT_MALLPAY_SITEID', MODULE_PAYMENT_MALLPAY_M_SITEID);
	define('MODULE_PAYMENT_MALLPAY_PRIVATE_KEY', MODULE_PAYMENT_MALLPAY_M_PRIVATE_KEY);
	define('MODULE_PAYMENT_MALLPAY_ORDER_PREFIX', MODULE_PAYMENT_MALLPAY_M_ORDER_PREFIX);	
}

$_order_sn = isset($_REQUEST['_sn']) ? $_REQUEST['_sn'] : '';
$_version = isset($_REQUEST['_version']) ? $_REQUEST['_version'] : '';
$_currency = isset($_REQUEST['_currency']) ? $_REQUEST['_currency'] : '';
$_amount = isset($_REQUEST['_amount']) ? $_REQUEST['_amount'] : '';
$_transactionid = isset($_REQUEST['_transactionno']) ? $_REQUEST['_transactionno'] : '';
$_verified = isset($_REQUEST['_status']) ? $_REQUEST['_status'] : '';
$_verifyCode = isset($_REQUEST['_checkcode']) ? $_REQUEST['_checkcode'] : '';
$local_sign = $_REQUEST['local_sign'] = md5($_order_sn . trim(MODULE_PAYMENT_MALLPAY_SITEID) . trim(MODULE_PAYMENT_MALLPAY_PRIVATE_KEY));

//¼־
$log = Date('H:i:s') . " S2S " . var_export($_REQUEST, true) . "\n";
$handle = fopen(DIR_FS_CATALOG . "mallpay/billlog/" . Date('Ymd') . ".log", 'a+');
fwrite($handle, $log);
fclose($handle);

if($local_sign !== $_verifyCode)
{
    die('Incorrect Signature!'); 
}

require(DIR_WS_LANGUAGES . $_SESSION['language'] . '/checkout_process.php');

require(DIR_WS_CLASSES . 'order.php');
if(trim(MODULE_PAYMENT_MALLPAY_ORDER_PREFIX) != '')
{
    $_order_sn = substr($_order_sn, strpos($_order_sn, '-')+1);
}
$order = new order($_order_sn);
$_SESSION['customer_id'] = $order->customer['id'];
$name = explode(' ', $order->customer['name']);
$order->customer['firstname'] = isset($name[0]) ? $name[0] : '';
$order->customer['lastname'] = isset($name[1]) ? $name[1] : '';
$order_totals = $order->totals; 

$_SESSION['payment'] = $order->info['payment_module_code'];
require(DIR_WS_CLASSES . 'payment.php');
$payment_modules = new payment($_SESSION['payment']);

require(DIR_WS_CLASSES . 'shipping.php');
$shipping_modules = new shipping($order->info['shipping_module_code']);
 
$_verified = str_replace(' ', '', $_verified);
$status = 'm_' . $_verified;
$notify = 0;

if($status === 'm_approved' || $status === 'm_testapprove')
{
    $session = $db->Execute("select sendto, billto from " . DB_PREFIX . "mallpay_sessions where sid = " . zen_db_input($_order_sn));   
    if(!$session->RecordCount())
    {
        die('order error');
    }
    $_SESSION['sendto'] = $session->fields['sendto'];
    $_SESSION['billto'] = $session->fields['billto'];
    
    $notify = 1;
    //չﳵ
    $_SESSION['cart']->reset(true);
}

//¶״̬
if(isset($payment_modules->paymentClass) && is_object($payment_modules->paymentClass))
{
	$payment_modules->paymentClass->update_order_status($_order_sn, $status, $_transactionid, $notify);
}
else
{
	$$_SESSION['payment']->update_order_status($_order_sn, $status, $_transactionid, $notify);  //1.2.X汾
}

die('prok');
?>