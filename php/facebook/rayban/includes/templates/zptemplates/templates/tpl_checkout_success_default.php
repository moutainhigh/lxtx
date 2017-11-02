<?php
/**
 * Page Template
 *
 * Loaded automatically by index.php?main_page=checkout_success.<br />
 * Displays confirmation details after order has been successfully processed.
 *
 * @package templateSystem - FEC ADVANCED
 * @copyright Copyright 2007 Numinix Technology http://www.numinix.com
 * @copyright Copyright 2003-2007 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_checkout_success_default.php 62 2009-07-12 21:43:34Z numinix $
 */
?>
<div style="display:none">

</div>
<div class="backgroundOuter contInner clear" id="checkoutSuccess">

<!--bof -gift certificate- send or spend box-->
<?php
// only show when there is a GV balance
  if ($customer_has_gv_balance ) {
?>
<div id="sendSpendWrapper">
<?php require($template->get_template_dir('tpl_modules_send_or_spend.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_send_or_spend.php'); ?>
</div>
<?php
  }
?>
<!--eof -gift certificate- send or spend box-->

<h1 id="checkoutSuccessHeading"><?php echo HEADING_TITLE; ?></h1>
<div id="checkoutSuccessOrderInfo" class="box"> 
  <div class="checkHeading">Order Details:</div>
   <div class="content">
   <div id="checkoutSuccessOrderNumber"><?php echo TEXT_YOUR_ORDER_NUMBER . $orders_id; ?></div>

	<?php
	$sql = 'SELECT * FROM ' . TABLE_ORDERS_TOTAL . ' ' .
		   'WHERE orders_id = "'.$orders_id.'" ' .
		   'ORDER BY sort_order';
	$res = $db->Execute($sql);	
	$order_info .= '<ul>';
	while (!$res->EOF) {
	  $order_info .= '<li>' . $res->fields['title'] . ' ' . $res->fields['text'] . '</li>';
	  $res->MoveNext();
	}
	
	$order_info .= '</ul>';
	
	echo $order_info;
	unset($sql, $res, $order_info)
	?>
 </div> 
</div>
<?php
//if ($_SESSION['navigation']->path['3']['get']['payment'] == 'westernunion') {
if ($orders->fields['payment_module_code'] == 'westernunion') {
?>
<div class="box">
  <div class="checkHeading">Payment Info:</div>
  <div class="content">
<?php
$wn_fn = $db->Execute('SELECT `configuration_value` FROM '.TABLE_CONFIGURATION.' WHERE `configuration_key` = "MODULE_PAYMENT_WESTERNUNION_RECEIVER_FIRST_NAME"')->fields['configuration_value'];
$wn_ln = $db->Execute('SELECT `configuration_value` FROM '.TABLE_CONFIGURATION.' WHERE `configuration_key` = "MODULE_PAYMENT_WESTERNUNION_RECEIVER_LAST_NAME"')->fields['configuration_value'];
$wn_city = $db->Execute('SELECT `configuration_value` FROM '.TABLE_CONFIGURATION.' WHERE `configuration_key` = "MODULE_PAYMENT_WESTERNUNION_RECEIVER_CITY"')->fields['configuration_value'];
$wn_state = 'Fujian';
$wn_ctr = $db->Execute('SELECT `configuration_value` FROM '.TABLE_CONFIGURATION.' WHERE `configuration_key` = "MODULE_PAYMENT_WESTERNUNION_RECEIVER_COUNTRY"')->fields['configuration_value'];
$wn_zip = $db->Execute('SELECT `configuration_value` FROM '.TABLE_CONFIGURATION.' WHERE `configuration_key` = "MODULE_PAYMENT_WESTERNUNION_RECEIVER_ZIP"')->fields['configuration_value'];
?>
  First Name: <?php echo $wn_fn;?><br />
  Last Name: <?php echo $wn_ln;?><br />
  City: <?php echo $wn_city;?><br />
  State: <?php echo $wn_state;?><br />
  Country: <?php echo $wn_ctr;?><br />
  Zip Code: <?php echo $wn_zip;?><br />
  </div>
</div>
<?php } ?>

<?php if (DEFINE_CHECKOUT_SUCCESS_STATUS >= 1 and DEFINE_CHECKOUT_SUCCESS_STATUS <= 2) { ?>
<div id="checkoutSuccessMainContent" class="content">
<?php
/**
 * require the html_defined text for checkout success
 */
  require($define_page);
?>
</div>
<?php } ?>
<!--bof logoff-->
<?php if (FEC_NOACCOUNT_LOGOFF == 'true') { ?>
<div id="checkoutSuccessLogoff">
<?php
if ($_SESSION['COWOA']) {
  zen_session_destroy();
} else {
  if (isset($_SESSION['customer_guest_id'])) {
    echo TEXT_CHECKOUT_LOGOFF_GUEST;
  } elseif (isset($_SESSION['customer_id'])) {
    echo TEXT_CHECKOUT_LOGOFF_CUSTOMER;
  } ?>
<div class="buttonRow forward"><a href="<?php echo zen_href_link(FILENAME_LOGOFF, '', 'SSL'); ?>"><?php echo zen_image_button(BUTTON_IMAGE_LOG_OFF , BUTTON_LOG_OFF_ALT); ?></a></div>
<?php } ?>
</div>
<?php } ?>
<!--eof logoff-->
<br class="clearBoth" />
<!--bof -product notifications box-->
<?php
/**
 * The following creates a list of checkboxes for the customer to select if they wish to be included in product-notification
 * announcements related to products they've just purchased.
 **/
    if ($flag_show_products_notification == true && !($_SESSION['COWOA'])) {
?>
<fieldset id="csNotifications">
<legend><?php echo TEXT_NOTIFY_PRODUCTS; ?></legend>
<?php echo zen_draw_form('order', zen_href_link(FILENAME_CHECKOUT_SUCCESS, 'action=update', 'SSL')); ?>

<?php foreach ($notificationsArray as $notifications) { ?>
<?php echo zen_draw_checkbox_field('notify[]', $notifications['products_id'], true, 'id="notify-' . $notifications['counter'] . '"') ;?>
<label class="checkboxLabel" for="<?php echo 'notify-' . $notifications['counter']; ?>"><?php echo $notifications['products_name']; ?></label>
<br />
<?php } ?>
<div class="buttonRow forward"><?php echo zen_image_submit(BUTTON_IMAGE_UPDATE, BUTTON_UPDATE_ALT); ?></div>
</form>
</fieldset>
<?php
    }
?>
<!--eof -product notifications box-->



<!--bof -product downloads module-->
<?php
  if (DOWNLOAD_ENABLED == 'true') require($template->get_template_dir('tpl_modules_downloads.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_downloads.php');
?>
<!--eof -product downloads module-->

<?php if(!($_SESSION['COWOA'])) { ?> <div id="checkoutSuccessOrderLink"><?php echo TEXT_SEE_ORDERS;?></div> <?php } ?>

<div id="checkoutSuccessContactLink"><?php echo TEXT_CONTACT_STORE_OWNER;?></div>

<h3 id="checkoutSuccessThanks" class="centeredContent"><?php echo TEXT_THANKS_FOR_SHOPPING; ?></h3>
</div>