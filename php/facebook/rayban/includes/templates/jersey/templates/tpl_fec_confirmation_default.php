<?php

/**

 * Page Template

 *

 * Loaded automatically by index.php?main_page=fec_confirmation.<br />

 * Displays final checkout details, cart, payment and shipping info details.

 *

 * @package templateSystem

 * @copyright Copyright 2007-2008 Numinix http://www.numinix.com
 * @copyright Portions Copyright 2003-2006 Zen Cart Development Team

 * @copyright Portions Copyright 2003 osCommerce

 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0

 * @version $Id: tpl_fec_confirmation_default.php 93 2009-10-06 05:11:07Z numinix $

 */

?>
<?php 
  echo '<div id="onePageText">' . FEC_CHECKOUT_CONFIRMATION_TEXT;
?>
<?php
  echo zen_draw_form('fec_confirmation', $form_action_url, 'post', 'id="fec_confirmation"');
?>
<!-- begin hidden form -->
<?php
  $class =& $_SESSION['payment'];
?>
<div class="hiddenFields" style="display:none;visbility:hidden;">
  <h3 id="checkoutConfirmDefaultPayment"><?php echo HEADING_PAYMENT_METHOD; ?></h3> 
  <h4 id="checkoutConfirmDefaultPaymentTitle"><?php echo $GLOBALS[$class]->title; ?></h4>

  <?php
    if (is_array($payment_modules->modules)) {
      if ($confirmation = $payment_modules->confirmation()) {
  ?>
  <div class="important"><?php echo $confirmation['title']; ?></div>
  <?php
      }
  ?>
  <div class="important">
  <?php
        for ($i=0, $n=sizeof($confirmation['fields']); $i<$n; $i++) {
  ?>
    <div class="back"><?php echo $confirmation['fields'][$i]['title']; ?></div>
    <div ><?php echo $confirmation['fields'][$i]['field']; ?></div>
  <?php
       }
  ?>
  </div>
  <?php
    }
  ?>

  <br class="clearBoth" />

  <?php
    if ($_SESSION['sendto'] != false) {
  ?>
  <div id="checkoutShipto" class="forward">
    <h2 id="checkoutConfirmDefaultShippingAddress"><?php echo HEADING_DELIVERY_ADDRESS; ?></h2>
    <div class="buttonRow forward"><?php echo '<a href="' . $editShippingButtonLink . '">' . zen_image_button(BUTTON_IMAGE_EDIT_SMALL, BUTTON_EDIT_SMALL_ALT) . '</a>'; ?></div>

    <address><?php echo zen_address_format($order->delivery['format_id'], $order->delivery, 1, ' ', '<br />'); ?></address>

  <?php
      if ($order->info['shipping_method']) {
  ?>
    <h3 id="checkoutConfirmDefaultShipment"><?php echo HEADING_SHIPPING_METHOD; ?></h3>
    <h4 id="checkoutConfirmDefaultShipmentTitle"><?php echo $order->info['shipping_method']; ?></h4>

  <?php
      }
  ?>
  </div>
  <?php
    }
  ?>
  <br class="clearBoth" />
  <hr />
  <?php
  // always show comments
  //  if ($order->info['comments']) {
  ?>

  <h2 id="checkoutConfirmDefaultHeadingComments"><?php echo HEADING_ORDER_COMMENTS; ?></h2>
  <div class="buttonRow forward"><?php echo  '<a href="' . zen_href_link(FILENAME_CHECKOUT_PAYMENT, '', 'SSL') . '">' . zen_image_button(BUTTON_IMAGE_EDIT_SMALL, BUTTON_EDIT_SMALL_ALT) . '</a>'; ?></div>
  <div><?php echo (empty($order->info['comments']) ? NO_COMMENTS_TEXT : nl2br(zen_output_string_protected($order->info['comments'])) . zen_draw_hidden_field('comments', $order->info['comments'])); ?></div>
  <br class="clearBoth" />
  <?php
  //  }
  ?>
  <hr />
<?php
  if (FEC_GIFT_WRAPPING_SWITCH == 'true') {
    $value = "ot_giftwrap_checkout.php"; 
    include_once(zen_get_file_directory(DIR_WS_LANGUAGES . $_SESSION['language'] .
          '/modules/order_total/', $value, 'false'));
    include_once(DIR_WS_MODULES . "order_total/" . $value);
    $wrap_mod = new ot_giftwrap_checkout(); 
    $use_gift_wrap = true;
    if ($wrap_mod->check()) {
?>
<h2 id="checkoutConfirmDefaultHeadingGiftWrap"><?php echo GIFT_WRAP_SUMMARY_HEADING; ?></h2>
<div class="buttonRow forward"><?php echo  '<a href="' . zen_href_link(FILENAME_CHECKOUT_SHIPPING, '', 'SSL') . '">' . zen_image_button(BUTTON_IMAGE_EDIT_SMALL, BUTTON_EDIT_SMALL_ALT) . '</a>'; ?></div>
<div>
<?php
       $wrap_count = 0;
       $wrapsettings = $_SESSION['wrapsettings'];
       for ($i=0, $n=sizeof($order->products); $i<$n; $i++) {
          $prid = $order->products[$i]['id'];
          for ($q = 1; $q <= $order->products[$i]['qty']; $q++) {
              if ( !isset($wrapsettings[$prid][$q]) || 
                   ($wrapsettings[$prid][$q] == 0) ) { 
                  continue;
              }
              if ($wrap_count == 0) {
                 echo '<table border="0" width="100%" cellspacing="0" cellpadding="0" id="cartContentsGiftWrapDisplay">'; 
                 echo '<tr class="cartTableHeading">';
                 echo '<th scope="col" id="ccProductsHeading">' . TABLE_HEADING_PRODUCTS .'</th>'; 
                 // echo '<th scope="col" id="ccWrapHeading">' . GIFT_WRAP_CHECKOFF . '</th>'; 
                 echo '</tr>';
              }
              $wrap_count++;
              if ($wrap_count%2 == 0) {
                 echo '<tr class="rowEven">';
              } else {
                 echo '<tr class="rowOdd">';
              }
              echo '<td class="cartProductDisplay">' . $order->products[$i]['name'];
              // if there are attributes, loop thru them and display one per line
              if (isset($order->products[$i]['attributes']) && sizeof($order->products[$i]['attributes']) > 0 ) {
                  echo '<ul class="cartAttribsList">';
                  for ($j=0, $n2=sizeof($order->products[$i]['attributes']); $j<$n2; $j++) {
                      echo '<li>' . $order->products[$i]['attributes'][$j]['option'] . ': ' . nl2br($order->products[$i]['attributes'][$j]['value']) . '</li>'; 
                  } // end loop
                  echo '</ul>';
              } // endif attribute-info
?>
              </td>
<?php
               // Indicate wrapping here.
?>
               </tr>
<?php
          }
       }
       if ($wrap_count == 0) {
          echo GIFT_WRAP_NO_TEXT;
       } else {
          echo "</table>"; 
       }
?>
</div>
<br class="clearBoth" />
<hr />
<?php
    }
  }
?>
  <h2 id="checkoutConfirmDefaultHeadingCart"><?php echo HEADING_PRODUCTS; ?></h2>

  <div class="buttonRow forward"><?php echo '<a href="' . zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL') . '">' . zen_image_button(BUTTON_IMAGE_EDIT_SMALL, BUTTON_EDIT_SMALL_ALT) . '</a>'; ?></div>
  <br class="clearBoth" />

  <?php  if ($flagAnyOutOfStock) { ?>
  <?php    if (STOCK_ALLOW_CHECKOUT == 'true') {  ?>
  <div class="messageStackError"><?php echo OUT_OF_STOCK_CAN_CHECKOUT; ?></div>
  <?php    } else { ?>
  <div class="messageStackError"><?php echo OUT_OF_STOCK_CANT_CHECKOUT; ?></div>
  <?php    } //endif STOCK_ALLOW_CHECKOUT ?>
  <?php  } //endif flagAnyOutOfStock ?>


        <table border="0" width="100%" cellspacing="0" cellpadding="0" id="cartContentsDisplay">
          <tr class="cartTableHeading">
          <th scope="col" id="ccQuantityHeading" width="30"><?php echo TABLE_HEADING_QUANTITY; ?></th>
          <th scope="col" id="ccProductsHeading"><?php echo TABLE_HEADING_PRODUCTS; ?></th>
  <?php
    // If there are tax groups, display the tax columns for price breakdown
    if (sizeof($order->info['tax_groups']) > 1) {
  ?>
            <th scope="col" id="ccTaxHeading"><?php echo HEADING_TAX; ?></th>
  <?php
    }
  ?>
            <th scope="col" id="ccTotalHeading"><?php echo TABLE_HEADING_TOTAL; ?></th>
          </tr>
  <?php // now loop thru all products to display quantity and price ?>
  <?php for ($i=0, $n=sizeof($order->products); $i<$n; $i++) { ?>
          <tr class="<?php echo $order->products[$i]['rowClass']; ?>">
            <td  class="cartQuantity"><?php echo $order->products[$i]['qty']; ?>&nbsp;x</td>
            <td class="cartProductDisplay"><?php echo $order->products[$i]['name']; ?>
            <?php  echo $stock_check[$i]; ?>

  <?php // if there are attributes, loop thru them and display one per line
      if (isset($order->products[$i]['attributes']) && sizeof($order->products[$i]['attributes']) > 0 ) {
      echo '<ul class="cartAttribsList">';
        for ($j=0, $n2=sizeof($order->products[$i]['attributes']); $j<$n2; $j++) {
  ?>
        <li><?php echo $order->products[$i]['attributes'][$j]['option'] . ': ' . nl2br(zen_output_string_protected($order->products[$i]['attributes'][$j]['value'])); ?></li>
  <?php
        } // end loop
        echo '</ul>';
      } // endif attribute-info
  ?>
          </td>

  <?php // display tax info if exists ?>
  <?php if (sizeof($order->info['tax_groups']) > 1)  { ?>
          <td class="cartTotalDisplay">
            <?php echo zen_display_tax_value($order->products[$i]['tax']); ?>%</td>
  <?php    }  // endif tax info display  ?>
          <td class="cartTotalDisplay">
            <?php echo $currencies->display_price($order->products[$i]['final_price'], $order->products[$i]['tax'], $order->products[$i]['qty']);
            if ($order->products[$i]['onetime_charges'] != 0 ) echo '<br /> ' . $currencies->display_price($order->products[$i]['onetime_charges'], $order->products[$i]['tax'], 1);
  ?>
          </td>
        </tr>
  <?php  }  // end for loopthru all products ?>
        </table>
        <hr />

  <?php
    if (MODULE_ORDER_TOTAL_INSTALLED) {
      $order_totals = $order_total_modules->process();
  ?>
  <div id="orderTotals"><?php $order_total_modules->output(); ?></div>
  <?php
    }
  ?>

  <div class="buttonRow"><?php echo zen_image_submit(BUTTON_IMAGE_CONFIRM_ORDER, BUTTON_CONFIRM_ORDER_ALT, 'name="btn_submit" id="btn_submit"') ;?></div>
</div>
<!-- end hidden form -->
    <noscript>
      <p>JavaScript not enabled, please click the confirmation button below to process your order.</p>
      <div class="buttonRow"><?php echo zen_image_submit(BUTTON_IMAGE_CONFIRM_ORDER, BUTTON_CONFIRM_ORDER_ALT, 'name="btn_submit" id="btn_submit"') ;?></div>
    </noscript>

<?php
    if (is_array($payment_modules->modules)) {
      echo $payment_modules->process_button();
    }
?>
  </form>
</div>