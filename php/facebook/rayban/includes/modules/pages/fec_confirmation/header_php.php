<?php
/**
 * checkout_confirmation header_php.php
 *
 * @package page
 * @copyright Copyright 2003-2007 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: header_php.php 109 2010-04-28 05:47:55Z numinix $
 */

// This should be first line of the script:
$zco_notifier->notify('NOTIFY_HEADER_START_CHECKOUT_CONFIRMATION');
$zco_notifier->notify('NOTIFY_HEADER_START_FEC_CONFIRMATION');
require_once(DIR_WS_CLASSES . 'http_client.php');
$messageStack->reset();
// if there is nothing in the customers cart, redirect them to the shopping cart page
if ($_SESSION['cart']->count_contents() <= 0) {
    zen_redirect(zen_href_link(FILENAME_TIME_OUT));
}

// if the customer is not logged on, redirect them to the login page
  if (!$_SESSION['customer_id']) {
    $_SESSION['navigation']->set_snapshot(array('mode' => 'SSL', 'page' => FILENAME_CHECKOUT));
    zen_redirect(zen_href_link(FILENAME_LOGIN, '', 'SSL'));
  } else {
    // validate customer
    if (zen_get_customer_validate_session($_SESSION['customer_id']) == false) {
      $_SESSION['navigation']->set_snapshot();
      zen_redirect(zen_href_link(FILENAME_LOGIN, '', 'SSL'));
    }
  }

// avoid hack attempts during the checkout procedure by checking the internal cartID
if (isset($_SESSION['cart']->cartID) && $_SESSION['cartID']) {
  if ($_SESSION['cart']->cartID != $_SESSION['cartID']) {
    zen_redirect(zen_href_link(FILENAME_CHECKOUT, '', 'SSL'));
  }
}

if (isset($_POST['payment'])) $_SESSION['payment'] = $_POST['payment'];
$_SESSION['comments'] = zen_db_prepare_input($_POST['comments']);

//'checkout_payment_discounts'
//zen_redirect(zen_href_link(FILENAME_CHECKOUT, '', 'SSL'));


if (DISPLAY_CONDITIONS_ON_CHECKOUT == 'true') {
  if (!isset($_POST['conditions']) || ($_POST['conditions'] != '1')) {
    $messageStack->add_session('checkout_payment', ERROR_CONDITIONS_NOT_ACCEPTED, 'error');
  }
}
//echo $messageStack->size('checkout_payment');

$total_weight = $_SESSION['cart']->show_weight();
$total_count = $_SESSION['cart']->count_contents();

require(DIR_WS_CLASSES . 'order.php');
$order = new order;
// load the selected shipping module
require(DIR_WS_CLASSES . 'shipping.php');
$shipping_modules = new shipping();

// process modules
$qc_process_dir_full = DIR_FS_CATALOG . DIR_WS_MODULES . 'quick_checkout_process/';
$qc_process_dir = DIR_WS_MODULES . 'quick_checkout_process/';
if ($dir = @dir($qc_process_dir_full)) {
  while ($file = $dir->read()) {
    if (!is_dir($qc_process_dir_full . $file)) {
      if (preg_match('/\.php$/', $file) > 0) {
        //include init file
        include($qc_process_dir . $file);
      }
    }
  }
  $dir->close();
}

if (isset($_GET['fecaction']) && $_GET['fecaction'] == 'process') {
  for ($i=0; $i<=1; $i++) { // perform twice, this should be optimized in the future
    if (isset($_POST['shipping'])) {
      $bool = true; //tell a freand
      $_SESSION['shipping'] = $_POST['shipping'];
      if ( (zen_count_shipping_modules() > 0) || ($free_shipping == true) ) {
        if ( (isset($_POST['shipping'])) && (strpos($_POST['shipping'], '_')) ) {
          $_SESSION['shipping'] = $_POST['shipping']; // process shipping
          list($module, $method) = explode('_', $_SESSION['shipping']);
          if ( is_object($$module) || ($_SESSION['shipping'] == 'free_free') ) {
            if ($_SESSION['shipping'] == 'free_free') {
              $quote[0]['methods'][0]['title'] = FREE_SHIPPING_TITLE;
              $quote[0]['methods'][0]['cost'] = '0';
            } else if ($_SESSION['shipping'] == 'tellafriend_tellafriend') { //bof tell a frend
              foreach($_POST["tell_a_friend_email"] as $key => $email) {
                $_POST["tell_a_friend_email"][$key] = trim(strtolower($email));
                $_POST["tell_a_friend_email_f_name"][$key] = trim($_POST["tell_a_friend_email_f_name"][$key]);
                $_POST["tell_a_friend_email_l_name"][$key] = trim($_POST["tell_a_friend_email_l_name"][$key]);
              }

              $tell_a_friend_email = $_POST["tell_a_friend_email"];
              $tell_a_friend_email = array_unique($tell_a_friend_email);

              $un_bool = true;
              foreach($tell_a_friend_email as $key => $email) {
                if(trim($email) == "")  {
                  $tell_a_friend_email_error .= "Please fill all of the email fields before selecting this shipping method.<br>";
                  $bool = false;
                } else if(!preg_match("/^[a-z0-9]+[a-z0-9_.-]*@[a-z0-9.-]+\.[a-z]{2,6}$/i", trim($email))) {
                  $tell_a_friend_email_error .= "$email is not properly formed.<br>";
                  $bool = false;
                } else if (is_object($captcha) && !$captcha->validateCaptchaCode()) { //add if for CAPTCHA check
                  $tell_a_friend_email_error .= ERROR_CAPTCHA;
                  $bool = false;
                } else {
                  $query = "select * from " . TABLE_FREE_SHIPPING_REFERRALS . " where referral_to_address = '$email'";
                  $result = mysql_query($query);
                  if(mysql_num_rows($result) > 0)
                  {
                    //$tell_a_friend_email_error .= "$email is already in database.<br>";
                    if($un_bool)
                    {
                      $tell_a_friend_email_error .= "Please make each email address unique.<br>";
                      $un_bool = false;
                    }
                    $tell_a_friend_email_error .= "$email, is already in use.<br>";
                    $tell_a_friend_email[$key] = "";
                    $bool = false;
                  }
                }
              }

              $_SESSION["tell_a_friend_email"] = "";
              $_SESSION["tell_a_friend_email"] = $tell_a_friend_email;
              $_SESSION["tell_a_friend_email_f_name"] = $_POST["tell_a_friend_email_f_name"];
              $_SESSION["tell_a_friend_email_l_name"] = $_POST["tell_a_friend_email_l_name"];

              if(count($tell_a_friend_email) < zen_get_configuration_key_value("MODULE_SHIPPING_TELL_A_FRIEND_NO_OF_EMAILS"))
              {
                $tell_a_friend_email_error .= "Please fill all the email fields.<br>";
                $bool = false;
              }
              if ($tell_a_friend_email_error != '') {
                $messageStack->add_session('checkout_shipping', $tell_a_friend_email_error, 'error');
                $_SESSION['shipping'] = $shipping_modules->cheapest();
                zen_redirect(zen_href_link(FILENAME_CHECKOUT, '', 'SSL'));
              }
              if($bool)
              {
                $quote = $shipping_modules->quote($method, $module);
              }
              //eof tell a freand
            } else {
              // avoid incorrect calculations during redirect
              $shipping_modules = new shipping();
              $quote = $shipping_modules->quote($method, $module);
              //$debug_logger->log_event (__FILE__, __LINE__, $quote);
            }
            if (isset($quote['error'])) {
              $_SESSION['shipping'] = '';
            } else {
              if ( (isset($quote[0]['methods'][0]['title'])) && (isset($quote[0]['methods'][0]['cost'])) ) {
                $_SESSION['shipping'] = array('id' => $_SESSION['shipping'],
                                  'title' => (($free_shipping == true) ?  $quote[0]['methods'][0]['title'] : $quote[0]['module'] . ' (' . $quote[0]['methods'][0]['title'] . ')'),
                                  'cost' => $quote[0]['methods'][0]['cost']);
              }
            }
          } else {
            $_SESSION['shipping'] = false;
          }
        }
      } else {
        $_SESSION['shipping'] = false;
      }
      // unset post to avoid setting shipping twice
      unset($_POST['shipping']);
    }
  }
  // get new order again
  $order = new order;
  // reset shipping_modules to newly set shipping
  $shipping_modules = new shipping($_SESSION['shipping']);
}


// if no shipping method has been selected, redirect the customer to the shipping method selection page
if (!$_SESSION['shipping']) {
  $messageStack->add_session('checkout_shipping', "Please select a shipping method", 'error');
  zen_redirect(zen_href_link(FILENAME_CHECKOUT, '', 'SSL'));
}

require(DIR_WS_CLASSES . 'order_total.php');
$order_total_modules = new order_total;
$order_total_modules->collect_posts();
$order_total_modules->pre_confirmation_check();

// load the selected payment module
require(DIR_WS_CLASSES . 'payment.php');
if ($credit_covers) {
  unset($_SESSION['payment']);
  $_SESSION['payment'] = '';
}

//@debug echo ($credit_covers == true) ? 'TRUE' : 'FALSE';

$payment_modules = new payment($_SESSION['payment']);
$payment_modules->update_status();
if (($_SESSION['payment'] == '' && !$credit_covers) || (is_array($payment_modules->modules)) && (sizeof($payment_modules->modules) > 1) && (!is_object($$_SESSION['payment'])) && (!$credit_covers) ) {
  $messageStack->add_session('checkout_payment', ERROR_NO_PAYMENT_MODULE_SELECTED, 'error');
}

if (is_array($payment_modules->modules)) {
  $payment_modules->pre_confirmation_check();
}

if ($messageStack->size('checkout_payment') > 0) {
  zen_redirect(zen_href_link(FILENAME_CHECKOUT, '', 'SSL'));
}
//echo $messageStack->size('checkout_payment');
//die('here');

// Stock Check
$flagAnyOutOfStock = false;
$stock_check = array();
if (STOCK_CHECK == 'true') {
  for ($i=0, $n=sizeof($order->products); $i<$n; $i++) {
    if ($stock_check[$i] = zen_check_stock($order->products[$i]['id'], $order->products[$i]['qty'])) {
      $flagAnyOutOfStock = true;
    }
  }
  // Out of Stock
  if ( (STOCK_ALLOW_CHECKOUT != 'true') && ($flagAnyOutOfStock == true) ) {
    zen_redirect(zen_href_link(FILENAME_SHOPPING_CART));
  }
}

// update customers_referral with $_SESSION['gv_id']
if ($_SESSION['cc_id']) {
  $discount_coupon_query = "SELECT coupon_code
                            FROM " . TABLE_COUPONS . "
                            WHERE coupon_id = :couponID";

  $discount_coupon_query = $db->bindVars($discount_coupon_query, ':couponID', $_SESSION['cc_id'], 'integer');
  $discount_coupon = $db->Execute($discount_coupon_query);

  $customers_referral_query = "SELECT customers_referral
                               FROM " . TABLE_CUSTOMERS . "
                               WHERE customers_id = :customersID";

  $customers_referral_query = $db->bindVars($customers_referral_query, ':customersID', $_SESSION['customer_id'], 'integer');
  $customers_referral = $db->Execute($customers_referral_query);

  // only use discount coupon if set by coupon
  if ($customers_referral->fields['customers_referral'] == '' and CUSTOMERS_REFERRAL_STATUS == 1) {
    $sql = "UPDATE " . TABLE_CUSTOMERS . "
            SET customers_referral = :customersReferral
            WHERE customers_id = :customersID";

    $sql = $db->bindVars($sql, ':customersID', $_SESSION['customer_id'], 'integer');
    $sql = $db->bindVars($sql, ':customersReferral', $discount_coupon->fields['coupon_code'], 'string');
    $db->Execute($sql);
  } else {
    // do not update referral was added before
  }
}

if (isset($$_SESSION['payment']->form_action_url)) {
  $form_action_url = $$_SESSION['payment']->form_action_url;
} else {
  $form_action_url = zen_href_link(FILENAME_CHECKOUT_PROCESS, '', 'SSL');
}

// if shipping-edit button should be overridden, do so
$editShippingButtonLink = zen_href_link(FILENAME_CHECKOUT, '', 'SSL');
if (method_exists($$_SESSION['payment'], 'alterShippingEditButton')) {
  $theLink = $$_SESSION['payment']->alterShippingEditButton();
  if ($theLink) $editShippingButtonLink = $theLink;
}
// deal with billing address edit button
$flagDisablePaymentAddressChange = false;
if (isset($$_SESSION['payment']->flagDisablePaymentAddressChange)) {
  $flagDisablePaymentAddressChange = $$_SESSION['payment']->flagDisablePaymentAddressChange;
}

// disable shipping address is products virtual
if ($order->content_type == 'virtual') {
  $_SESSION['sendto'] = false;
}

// redirect to update shipping if needed
//if ($redirectFEC == true) {
  //zen_redirect(zen_href_link(FILENAME_FEC_CONFIRMATION, '', 'SSL'));
//}

require(DIR_WS_MODULES . zen_get_module_directory('require_languages.php'));
$breadcrumb->add(NAVBAR_TITLE_1, zen_href_link(FILENAME_CHECKOUT, '', 'SSL'));
$breadcrumb->add(NAVBAR_TITLE_2);

// This should be last line of the script:
$zco_notifier->notify('NOTIFY_HEADER_END_FEC_CONFIRMATION');
$zco_notifier->notify('NOTIFY_HEADER_END_CHECKOUT_CONFIRMATION');
?>