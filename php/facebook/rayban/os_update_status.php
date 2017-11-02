<?php
include('includes/application_top.php');
$slog = '_status.log';

if(!empty($_POST['update']) && !empty($_POST['order_number']) && !empty($_POST['order_status'])) {
	$log_str = "[{$_POST['order_number']}] [{$_POST['order_status']}]";
	if($_POST['order_password'] != 'diantong') { $log_str .= " [ERROR]\n"; os_log($slog, $log_str); exit; }
	
	//$sql = "SELECT `orders_status_id` FROM ` orders_status` WHERE `orders_status_name` = 'm_approved' LIMIT 1";
	$sql = "SELECT `orders_status_id` FROM ".TABLE_ORDERS_STATUS." WHERE `orders_status_name` = '{$_POST['order_status']}' LIMIT 1";
	$orders_status_id = $db->Execute($sql)->fields['orders_status_id'];
	if($orders_status_id == '') {
		$db->Execute("INSERT INTO ".TABLE_ORDERS_STATUS." (`language_id`, `orders_status_name`) VALUES ('{$_SESSION['languages_id']}', '".addslashes($_POST['order_status'])."')");
		$orders_status_id = $db->insert_ID();
	}
	
	$odsid_now = $db->Execute("SELECT `orders_status` FROM ".TABLE_ORDERS." WHERE `orders_id` = '{$_POST['order_number']}'")->fields['orders_status'];
	if($odsid_now != $orders_status_id) {
		$db->Execute("UPDATE ".TABLE_ORDERS." SET `orders_status` = $orders_status_id WHERE `orders_id` = '{$_POST['order_number']}'");
		$db->Execute("INSERT INTO ".TABLE_ORDERS_STATUS_HISTORY." (`orders_id`, `orders_status_id`, `date_added`, `customer_notified`) VALUES ('{$_POST['order_number']}', $orders_status_id, NOW(), 1)");
		$log_str .= " [update]";
	}
	os_log($slog, $log_str);
}
?>