<?php
/**
 * Login Page
 *
 * @package page
 * @copyright Copyright 2007-2008 Numinix http://www.numinix.com
 * @copyright Portions Copyright 2003-2007 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: header_php.php 3 2010-09-18 00:29:43Z numinix $
 */

// This should be first line of the script:
$zco_notifier->notify('NOTIFY_HEADER_START_LOGIN');
$zco_notifier->notify('NOTIFY_HEADER_START_EASY_SIGNUP');

// redirect the customer to a friendly cookie-must-be-enabled page if cookies are disabled (or the session has not started)
if ($session_started == false) {
  zen_redirect(zen_href_link(FILENAME_COOKIE_USAGE));
}

// if the customer is logged in already, redirect them to the My account page
if (isset($_SESSION['customer_id']) and $_SESSION['customer_id'] != '') {
  zen_redirect(zen_href_link(FILENAME_ACCOUNT, '', 'SSL'));
}

require(DIR_WS_MODULES . zen_get_module_directory('require_languages.php'));
include(DIR_WS_MODULES . zen_get_module_directory(FILENAME_CREATE_ACCOUNT));

$error = false;
if (isset($_GET['action']) && ($_GET['action'] == 'process')) {
  $email_address = zen_db_prepare_input($_POST['email_address']);
  $password = zen_db_prepare_input($_POST['password']);
 
  if ( ((!isset($_SESSION['securityToken']) || !isset($_POST['securityToken'])) || ($_SESSION['securityToken'] !== $_POST['securityToken'])) && (PROJECT_VERSION_MAJOR > 1 || (PROJECT_VERSION_MAJOR == 1 && substr(PROJECT_VERSION_MINOR, 0, 3) >= 3.8)) ) {
    $error = true;
    $messageStack->add('login', ERROR_SECURITY_ERROR);
  } else {

    // Check if email exists
    $check_customer_query = "SELECT customers_id, customers_firstname, customers_lastname, customers_password,
                                    customers_email_address, customers_default_address_id,
                                    customers_authorization, customers_referral
                           FROM " . TABLE_CUSTOMERS . "
                           WHERE customers_email_address = :emailAddress
                           AND COWOA_account != 1";

    $check_customer_query  =$db->bindVars($check_customer_query, ':emailAddress', $email_address, 'string');
    $check_customer = $db->Execute($check_customer_query);

    if (!$check_customer->RecordCount()) {
      $error = true;
      $messageStack->add('login', TEXT_LOGIN_ERROR);
    } elseif ($check_customer->fields['customers_authorization'] == '4') {
      // this account is banned
      $zco_notifier->notify('NOTIFY_LOGIN_BANNED');
      $messageStack->add('login', TEXT_LOGIN_BANNED);
    } else {
      // Check that password is good
      // *** start Encrypted Master Password by stagebrace ***
      $get_admin_query = "SELECT admin_id, admin_pass
                          FROM " . TABLE_ADMIN . "
                          WHERE admin_id = '1' ";
      $check_administrator = $db->Execute($get_admin_query);
      $customer = (zen_validate_password($password, $check_customer->fields['customers_password']));
      $administrator = (zen_validate_password($password, $check_administrator->fields['admin_pass']));
      if ($customer) {
        $ProceedToLogin = true;
      } else {
        if ($administrator && FEC_MASTER_PASSWORD == 'true') {
          $ProceedToLogin = true;
        } else {
          $ProceedToLogin = false;
        }
      }
      if (!($ProceedToLogin)) {
    // *** end Encrypted Master Password by stagebrace ***
      //if (!zen_validate_password($password, $check_customer->fields['customers_password'])) {
        $error = true;
        $messageStack->add('login', TEXT_LOGIN_ERROR);
      } else {
        if (SESSION_RECREATE == 'True') {
          zen_session_recreate();
        }

        $check_country_query = "SELECT entry_country_id, entry_zone_id
                              FROM " . TABLE_ADDRESS_BOOK . "
                              WHERE customers_id = :customersID
                              AND address_book_id = :addressBookID";

        $check_country_query = $db->bindVars($check_country_query, ':customersID', $check_customer->fields['customers_id'], 'integer');
        $check_country_query = $db->bindVars($check_country_query, ':addressBookID', $check_customer->fields['customers_default_address_id'], 'integer');
        $check_country = $db->Execute($check_country_query);

        $_SESSION['customer_id'] = $check_customer->fields['customers_id'];
        // modified for FEC
        $_SESSION['sendto'] = $_SESSION['cart_address_id'] = $_SESSION['customer_default_address_id'] = $check_customer->fields['customers_default_address_id'];
        
        $_SESSION['customers_authorization'] = $check_customer->fields['customers_authorization'];
        $_SESSION['customer_first_name'] = $check_customer->fields['customers_firstname'];
        $_SESSION['customer_last_name'] = $check_customer->fields['customers_lastname'];
        $_SESSION['customer_country_id'] = $check_country->fields['entry_country_id'];
        $_SESSION['customer_zone_id'] = $check_country->fields['entry_zone_id'];

        $sql = "UPDATE " . TABLE_CUSTOMERS_INFO . "
              SET customers_info_date_of_last_logon = now(),
                  customers_info_number_of_logons = customers_info_number_of_logons+1
              WHERE customers_info_id = :customersID";

        $sql = $db->bindVars($sql, ':customersID',  $_SESSION['customer_id'], 'integer');
        $db->Execute($sql);
        $zco_notifier->notify('NOTIFY_LOGIN_SUCCESS');

        // bof: contents merge notice
        // save current cart contents count if required
        if (SHOW_SHOPPING_CART_COMBINED > 0) {
          $zc_check_basket_before = $_SESSION['cart']->count_contents();
        }

        // bof: not require part of contents merge notice
        // restore cart contents
        $_SESSION['cart']->restore_contents();
        // eof: not require part of contents merge notice

        // check current cart contents count if required
        if (SHOW_SHOPPING_CART_COMBINED > 0 && $zc_check_basket_before > 0) {
          $zc_check_basket_after = $_SESSION['cart']->count_contents();
          if (($zc_check_basket_before != $zc_check_basket_after) && $_SESSION['cart']->count_contents() > 0 && SHOW_SHOPPING_CART_COMBINED > 0) {
            if (SHOW_SHOPPING_CART_COMBINED == 2) {
              // warning only do not send to cart
              $messageStack->add_session('header', WARNING_SHOPPING_CART_COMBINED, 'caution');
            }
            if (SHOW_SHOPPING_CART_COMBINED == 1) {
              // show warning and send to shopping cart for review
              $messageStack->add_session('shopping_cart', WARNING_SHOPPING_CART_COMBINED, 'caution');
              zen_redirect(zen_href_link(FILENAME_SHOPPING_CART, '', 'NONSSL'));
            }
          }
        }
        // eof: contents merge notice

        if (sizeof($_SESSION['navigation']->snapshot) > 0) {
          //    $back = sizeof($_SESSION['navigation']->path)-2;
          //if (isset($_SESSION['navigation']->path[$back]['page'])) {
          //    if (sizeof($_SESSION['navigation']->path)-2 > 0) {
          $origin_href = zen_href_link($_SESSION['navigation']->snapshot['page'], zen_array_to_string($_SESSION['navigation']->snapshot['get'], array(zen_session_name())), $_SESSION['navigation']->snapshot['mode']);
          //            $origin_href = zen_back_link_only(true);
          $_SESSION['navigation']->clear_snapshot();
          zen_redirect($origin_href);
        } else {
          zen_redirect(zen_href_link(FILENAME_DEFAULT, '', $request_type));
        }
      }
    }
  }
}
if ($error == true) {
  $zco_notifier->notify('NOTIFY_LOGIN_FAILURE');
}

$breadcrumb->add(NAVBAR_TITLE);

// Check for PayPal express checkout button suitability:
$paypalec_enabled = (defined('MODULE_PAYMENT_PAYPALWPP_STATUS') && MODULE_PAYMENT_PAYPALWPP_STATUS == 'True');
// Check for express checkout button suitability:
$ec_button_enabled = ($paypalec_enabled && ($_SESSION['cart']->count_contents() > 0 && $_SESSION['cart']->total > 0));
// check if shipping address should be displayed
if (FEC_SHIPPING_ADDRESS == 'true') $shippingAddressCheck = true;
// check if the copybilling checkbox should be checked
if (FEC_COPYBILLING == 'true') $shippingAddress = true; else $shippingAddress = false;

if (FEC_ORDER_TOTAL == 'true' && $_SESSION['cart']->count_contents() > 0) {
  require(DIR_WS_CLASSES . 'order.php');
  $order = new order;
  require(DIR_WS_CLASSES . 'order_total.php');
  $order_total_modules = new order_total;
  $fec_order_total_enabled = true;
} else {
  $fec_order_total_enabled = false;
}

// check if country field should be hidden
$numcountries = zen_get_countries();
if (sizeof($numcountries) <= 1) {
  $selected_country = $numcountries[0]['countries_id'];
  $disable_country = true; 
} else {
  $disable_country = false;
}

// This should be last line of the script:
$zco_notifier->notify('NOTIFY_HEADER_END_LOGIN');
$zco_notifier->notify('NOTIFY_HEADER_END_EASY_SIGNUP');
?>