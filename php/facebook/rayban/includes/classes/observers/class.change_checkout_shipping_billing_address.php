<?php
if (!defined('IS_ADMIN_FLAG')) {
  die('Illegal Access');
}

class change_checkout_shipping_billing_address extends base {
	
	function change_checkout_shipping_billing_address() {
		global $zco_notifier;
		$zco_notifier->attach($this, array('NOTIFY_ORDER_INVOICE_CONTENT_READY_TO_SEND'));
	}
	
	function update(&$class, $eventID, $paramArray) {
		global $db;
		if(isset($_SESSION['new_billing_address']) && $_SESSION['new_billing_address'] != 0) {
			$db->Execute('DELETE FROM '.TABLE_ADDRESS_BOOK.' WHERE customers_id="'.$_SESSION['customer_id'].'" AND address_book_id="'.$_SESSION['new_billing_address'].'"');
			unset($_SESSION['new_billing_address']);
		}
		if(isset($_SESSION['new_shipping_address']) && $_SESSION['new_shipping_address'] != 0) {
			$db->Execute('DELETE FROM '.TABLE_ADDRESS_BOOK.' WHERE customers_id="'.$_SESSION['customer_id'].'" AND address_book_id="'.$_SESSION['new_shipping_address'].'"');
			unset($_SESSION['new_shipping_address']);
		}
	}
}