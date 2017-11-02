<?php
/**
 * Page Template
 *
 * Loaded automatically by index.php?main_page=account_edit.<br />
 * Displays information related to a single specific order
 *
 * @package templateSystem
 * @copyright Copyright 2003-2006 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_account_history_info_default.php 6247 2007-04-21 21:34:47Z wilt $
 */
?>
<div class="centerColumn" id="accountHistInfo">
  <div class="order_detail">
	<h2 id="orderHistoryDetailedOrder"><?php echo HEADING_TITLE . ORDER_HEADING_DIVIDER . sprintf(HEADING_ORDER_NUMBER, $_POST['order_number']); ?></h2>
	<div class="content">
	<div class="forward"><?php echo HEADING_ORDER_DATE . ' ' . zen_date_long($order->info['date_purchased']); ?></div>
	<br class="clearBoth" />
	<table class="tabTable" border="0" width="100%" cellspacing="0" cellpadding="3" summary="Itemized listing of previous order, includes number ordered, items and prices">
		<tr class="tableHeading">
			<th scope="col" id="myAccountQuantity" align="left"><?php echo HEADING_QUANTITY; ?></th>
			<th scope="col" id="myAccountProducts"  align="left"><?php echo HEADING_PRODUCTS; ?></th>
	<?php
	  if (sizeof($order->info['tax_groups']) > 1) {
	?>
			<th scope="col" id="myAccountTax"  align="left"><?php echo HEADING_TAX; ?></th>
	<?php
	 }
	?>
			<th scope="col" id="myAccountTotal"  align="right"><?php echo HEADING_TOTAL; ?></th>
		</tr>
	<?php
	  for ($i=0, $n=sizeof($order->products); $i<$n; $i++) {
	  ?>
		<tr>
			<td class="accountQuantityDisplay"><?php echo  $order->products[$i]['qty'] . QUANTITY_SUFFIX; ?></td>
			<td class="accountProductDisplay"><?php echo '<a href="index.php?main_page=product_info&products_id=' . $order->products[$i]['id'] . '">' . $order->products[$i]['name'] . '</a>';
	
		if ( (isset($order->products[$i]['attributes'])) && (sizeof($order->products[$i]['attributes']) > 0) ) {
		  echo '<ul id="orderAttribsList">';
		  for ($j=0, $n2=sizeof($order->products[$i]['attributes']); $j<$n2; $j++) {
			echo '<li>' . $order->products[$i]['attributes'][$j]['option'] . TEXT_OPTION_DIVIDER . nl2br(zen_output_string_protected($order->products[$i]['attributes'][$j]['value'])) . '</li>';
		  }
			echo '</ul>';
		}
	?>
			</td>
	<?php
		if (sizeof($order->info['tax_groups']) > 1) {
	?>
			<td class="accountTaxDisplay"><?php echo zen_display_tax_value($order->products[$i]['tax']) . '%' ?></td>
	<?php
		}
	?>
			<td class="accountTotalDisplay"  align="right"><?php echo $currencies->format(zen_add_tax($order->products[$i]['final_price'], $order->products[$i]['tax']) * $order->products[$i]['qty'], true, $order->info['currency'], $order->info['currency_value']) . ($order->products[$i]['onetime_charges'] != 0 ? '<br />' . $currencies->format(zen_add_tax($order->products[$i]['onetime_charges'], $order->products[$i]['tax']), true, $order->info['currency'], $order->info['currency_value']) : '') ?></td>
		</tr>
	<?php
	  }
	?>
	</table>
	<div id="orderTotals">
	<?php
	  for ($i=0, $n=sizeof($order->totals); $i<$n; $i++) {
	?>
		 <b class="amount larger forward"><?php echo $order->totals[$i]['text'] ?></b>
		 <span class="lineTitle larger forward"><?php echo $order->totals[$i]['title'] ?></span>
	<br class="clearBoth" />
	<?php
	  }
	?>
	</div>
	</div>

</div>

<?php
/**
 * Used to display any downloads associated with the cutomers account
 */
  if (DOWNLOAD_ENABLED == 'true') require($template->get_template_dir('tpl_modules_downloads.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_downloads.php');
?>


<?php
/**
 * Used to loop thru and display order status information
 */
if (sizeof($statusArray)) {
?>
<div class="order_status">
  <h2 id="orderHistoryStatus" class="centerBoxHeading"><?php echo HEADING_ORDER_HISTORY; ?></h2>
  <div class="content">
	<table class="tabTable" border="0" width="100%" cellspacing="0" cellpadding="3" id="myAccountOrdersStatus" summary="Table contains the date, order status and any comments regarding the order">
		<tr class="tableHeading">
			<th scope="col" id="myAccountStatusDate" align="left"><?php echo TABLE_HEADING_STATUS_DATE; ?></th>
			<th scope="col" id="myAccountStatus" align="left"><?php echo TABLE_HEADING_STATUS_ORDER_STATUS; ?></th>
			<th scope="col" id="myAccountStatusComments" align="left"><?php echo TABLE_HEADING_STATUS_COMMENTS; ?></th>
			<th scope="col" align="right">Qucik Payment by credit card</th>
		   </tr>
	<?php
	  foreach ($statusArray as $statuses) {
	?>
							<tr>
							<td><?php echo zen_date_short($statuses['date_added']) ;?></td>
			<td><?php echo $statuses['orders_status_name']; ?></td>
			<td><?php echo (empty($statuses['comments']) ? '&nbsp;' : nl2br(zen_output_string_protected($statuses['comments']))); ?></td>
            <td width="142"><?php if (strtolower($statuses['orders_status_name']) == 'pending'||'Payment failure'||'cancel order'||'Still Pending'||'processing' &&  sizeof($statusArray)==1) echo ' <a href="quick_payment_ecpss.php?oi=' . $_POST['order_id'] . '"  target="_blank"><img src="/includes/modules/payment/ecpss/ecpss.png" alt="Qucik Payment by Ecpss" /></a>';?></td>
		 </tr>
	<?php
	  }
	?>
	</table>
  </div>
<?php } ?>
</div>

    <div class="order_shipinfo">
    <div class="content clear" style="padding-top:15px">
		<div id="myAccountShipInfo" class="floatingBox back">
		<?php
		  if ($order->delivery != false) {
		?>
		<h3><?php echo HEADING_DELIVERY_ADDRESS; ?></h3>
		<address><?php echo zen_address_format($order->delivery['format_id'], $order->delivery, 1, ' ', '<br />'); ?></address>
		<?php
		  }
		?>
		
		<?php
			if (zen_not_null($order->info['shipping_method'])) {
		?>
		<h4><?php echo HEADING_SHIPPING_METHOD; ?></h4>
		<div><?php echo $order->info['shipping_method']; ?></div>
		<?php } else { // temporary just remove these 4 lines ?>
		<div>WARNING: Missing Shipping Information</div>
		<?php
			}
		?>
		</div>
	
		<div id="myAccountPaymentInfo" class="floatingBox forward">
		<h3><?php echo HEADING_BILLING_ADDRESS; ?></h3>
		<address><?php echo zen_address_format($order->billing['format_id'], $order->billing, 1, ' ', '<br />'); ?></address>
		
		<h4><?php echo HEADING_PAYMENT_METHOD; ?></h4>
		<div><?php echo $order->info['payment_method']; ?></div>
		</div>
		<br class="clearBoth" />
	</div>
	</div>
</div>