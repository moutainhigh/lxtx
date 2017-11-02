<?php
/**
 * no_account header_php.php
 *
 * @package page
 * @copyright Copyright 2003-2006 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: J_Schilz for Integrated COWOA - 14 April 2007
 */

// This should be first line of the script:
$zco_notifier->notify('NOTIFY_HEADER_START_NO_ACCOUNT');
$err_log = zen_db_prepare_input($_REQUEST['err_log']);
if(isset($err_log) && $err_log == 1) {
  $messageStack->add('no_account', TEXT_LOGIN_ERROR);
}

require(DIR_WS_MODULES . zen_get_module_directory('require_languages.php'));
include(DIR_WS_MODULES . zen_get_module_directory('quick_checkout.php'));
require_once(DIR_WS_CLASSES . 'http_client.php'); 
require(DIR_WS_CLASSES . 'order.php');
$order = new order;
require(DIR_WS_CLASSES . 'order_total.php');
$order_total_modules = new order_total;
$fec_order_total_enabled = true;

//if there is nothing in the customers cart, redirect them to the shopping cart page
if ($_SESSION['cart']->count_contents() <= 0) {
  zen_redirect(zen_href_link(FILENAME_TIME_OUT));
}

// check if customer already logged in
if (isset($_SESSION['customer_id'])) zen_redirect(zen_href_link(FILENAME_CHECKOUT, '', 'SSL'));

// Validate Cart for checkout
$_SESSION['valid_to_checkout'] = true;
$_SESSION['cart']->get_products(true);
if ($_SESSION['valid_to_checkout'] == false) {
  $messageStack->add('header', ERROR_CART_UPDATE, 'error');
  zen_redirect(zen_href_link(FILENAME_SHOPPING_CART));
}

if (!$_SESSION['sendto']) {
  $_SESSION['sendto'] = $_SESSION['customer_default_address_id'];
} else {
// verify the selected shipping address
  $check_address_query = "SELECT count(*) AS total
                          FROM   " . TABLE_ADDRESS_BOOK . "
                          WHERE  customers_id = :customersID
                          AND    address_book_id = :addressBookID";

  $check_address_query = $db->bindVars($check_address_query, ':customersID', $_SESSION['customer_id'], 'integer');
  $check_address_query = $db->bindVars($check_address_query, ':addressBookID', $_SESSION['sendto'], 'integer');
  $check_address = $db->Execute($check_address_query);

  if ($check_address->fields['total'] != '1') {
    $_SESSION['sendto'] = $_SESSION['customer_default_address_id'];
    $_SESSION['shipping'] = '';
  }
}

// if no billing destination address was selected, use the customers own address as default
if (!$_SESSION['billto']) {
  $_SESSION['billto'] = $_SESSION['customer_default_address_id'];
} else {
  // verify the selected billing address
  $check_address_query = "SELECT count(*) AS total FROM " . TABLE_ADDRESS_BOOK . "
                          WHERE customers_id = :customersID
                          AND address_book_id = :addressBookID";

  $check_address_query = $db->bindVars($check_address_query, ':customersID', $_SESSION['customer_id'], 'integer');
  $check_address_query = $db->bindVars($check_address_query, ':addressBookID', $_SESSION['billto'], 'integer');
  $check_address = $db->Execute($check_address_query);

  if ($check_address->fields['total'] != '1') {
    $_SESSION['billto'] = $_SESSION['customer_default_address_id'];
    $_SESSION['payment'] = '';
  }
}

// check if shipping address should be displayed
if (FEC_SHIPPING_ADDRESS == 'true') $shippingAddressCheck = true;
// check if the copybilling checkbox should be checked
if (FEC_COPYBILLING == 'true') $shippingAddress = true; else $shippingAddress = false;          

$comments = $_SESSION['comments'];

// check if country field should be hidden
$numcountries = zen_get_countries();
if (sizeof($numcountries) <= 1) {
  $selected_country = $numcountries[0]['countries_id'];
  $disable_country = true; 
} else {
  $disable_country = false;
}

$breadcrumb->add(NAVBAR_TITLE);

// This should be last line of the script:
$zco_notifier->notify('NOTIFY_HEADER_END_NO_ACCOUNT');
?>