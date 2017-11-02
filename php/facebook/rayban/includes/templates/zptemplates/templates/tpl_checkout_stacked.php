  <?php
    // GOOGLE CHECKOUT
    foreach($payment_modules->modules as $pm_code => $pm) {
      if(substr($pm, 0, strrpos($pm, '.')) == 'googlecheckout') {
        unset($payment_modules->modules[$pm_code]);
      }
    }

    if ($numselection>0) {
      for ($i=0, $n=sizeof($selection); $i<$n; $i++) {
        if ($selectionStyle == 'split') {
          // $i starts at 0
          if ($i%2 == 0) $box = 'odd'; else $box = 'even';
        }
        if ($_GET['credit_class_error_code'] == $selection[$i]['id']) {
  ?>
  <div class="messageStackError"><?php echo zen_output_string_protected($_GET['credit_class_error']); ?></div>
  
  <?php
        }
        for ($j=0, $n2=sizeof($selection[$i]['fields']); $j<$n2; $j++) {
  ?>
  
  <?php   if(!($COWOA && $selection[$i]['module']==MODULE_ORDER_TOTAL_GV_TITLE)) { ?>

    <?php 
            $continue_discount = true;
            if ( (($selection[$i]['module']) == MODULE_ORDER_TOTAL_INSURANCE_TITLE) && ($order->content_type == 'virtual') ) { 
              $continue_discount = false;
              $_SESSION['insurance'] = $_SESSION['opt_insurance'] = '0';
            }
            if ($continue_discount == true) {
    ?>
  <div class="discountForm<?php echo $selectionStyle; ?> discount<?php echo $box; ?>">
    <fieldset class="discount">
    <legend><?php echo $selection[$i]['module']; ?></legend>
    <?php echo $selection[$i]['redeem_instructions']; ?>
    <div class="gvBal larger"><?php echo $selection[$i]['checkbox']; ?></div>
    <div class="gvBal">
      <label class="inputLabel"<?php echo ($selection[$i]['fields'][$j]['tag']) ? ' for="'.$selection[$i]['fields'][$j]['tag'].'"': ''; ?>><?php echo $selection[$i]['fields'][$j]['title']; ?></label>      
      <?php   echo $selection[$i]['fields'][$j]['field']; ?>
      <?php   if ( ($selection[$i]['module'] != MODULE_ORDER_TOTAL_INSURANCE_TITLE) && ($selection[$i]['module'] != MODULE_ORDER_TOTAL_SC_TITLE) ) { ?>
      <div class="buttonRow"><?php echo zen_image(zen_output_string($template->get_template_dir(BUTTON_IMAGE_UPDATE, DIR_WS_TEMPLATE, $current_page_base, 'buttons/' . $_SESSION['language'] . '/') . BUTTON_IMAGE_UPDATE), BUTTON_UPDATE_ALT, '', '', 'onclick="updateForm();"'); ?></div>
      <?php   } ?>  
    </div>
    </fieldset>
  <br class="clearBoth" />
  </div>
  <?php       if ($selectionStyle == 'split' && ($box == 'even')) { echo '<br class="clearBoth" />'; } ?>
    
  <?php     } ?>
    
    
  <?php   }
        }
      }
  ?>
  
  <?php
    }
  ?>
  
  <!--BOF SHIPPING-->
    
  <?php
    if ($order->content_type != 'virtual') {
  ?>
  <div id="checkoutShippingForm<?php echo $checkoutStyle; ?>">
    <h1 id="checkoutShippingHeading"><?php echo HEADING_TITLE_SHIPPING; ?></h1>
    
    <fieldset class="checkout" id="checkoutShippingMethods">
    <legend><?php echo TABLE_HEADING_SHIPPING_METHOD; ?></legend>
    
    <fieldset class="checkout" id="checkoutShipTo">
    <legend><?php echo TABLE_HEADING_SHIPPING_ADDRESS; ?></legend>
    <?php if ($messageStack->size('checkout_shipping') > 0) echo $messageStack->output('checkout_shipping'); ?>  
    <div class="addressContainer">
      <div id="checkoutShipto" class="floatingBox back">
        <address class="checkoutAddress"><?php echo html_entity_decode(zen_address_label($_SESSION['customer_id'], $_SESSION['sendto'], true, ' ', '<br />')); ?></address>
      </div> 
      <?php if ($displayAddressEdit) { ?>
      <?php 
              echo '<div class="buttonRow"><a id="linkCheckoutShippingAddr" href="' . zen_href_link(FILENAME_CHECKOUT_SHIPPING_ADDRESS, '', 'SSL') . '">' . zen_image_button(BUTTON_IMAGE_CHANGE_ADDRESS, BUTTON_CHANGE_ADDRESS_ALT) . '</a></div>';
      ?>
      <?php } ?>
      <br class="clearBoth" />
    </div>
    </fieldset>
    <?php
      //$addresses_count = zen_count_customer_address_book_entries();
      //require($template->get_template_dir('tpl_modules_fec_change_checkout_shipping_address.php', DIR_WS_TEMPLATE, $current_page_base,'templates/fec'). '/' . 'tpl_modules_fec_change_checkout_shipping_address.php');
    ?>
    <br class="clearBoth" />
    <?php
      if (zen_count_shipping_modules() > 0) {
    ?>
    
    <?php
        if (sizeof($quotes) > 1 && sizeof($quotes[0]) > 1) {
    ?>
    
    <div id="checkoutShippingContentChoose" class="important"><?php echo TEXT_CHOOSE_SHIPPING_METHOD; ?></div>
    
    <?php
        } elseif ($free_shipping == false) {
    ?>
    <div id="checkoutShippingContentChoose" class="important"><?php echo TEXT_ENTER_SHIPPING_INFORMATION; ?></div>
    
    <?php
        }
    ?>
    <?php
        if ($free_shipping == true) {
    ?>
    <div id="freeShip" class="important" ><?php echo FREE_SHIPPING_TITLE; ?>&nbsp;<?php echo $quotes[$i]['icon']; ?></div>
    <div id="defaultSelected"><?php echo sprintf(FREE_SHIPPING_DESCRIPTION, $currencies->format(MODULE_ORDER_TOTAL_SHIPPING_FREE_SHIPPING_OVER)) . zen_draw_hidden_field('shipping', 'free_free'); ?></div>
    
    <?php
        } else {
          $radio_buttons = 0;
          for ($i=0, $n=sizeof($quotes); $i<$n; $i++) {
    ?>
    <fieldset class="checkout">
    <legend><?php echo $quotes[$i]['module']; ?>&nbsp;<?php if (isset($quotes[$i]['icon']) && zen_not_null($quotes[$i]['icon'])) { echo $quotes[$i]['icon']; } ?></legend>
    <?php
            if (isset($quotes[$i]['error'])) {
    ?>
          <div><?php echo $quotes[$i]['error']; ?></div>
    <?php
            } else {
              for ($j=0, $n2=sizeof($quotes[$i]['methods']); $j<$n2; $j++) {
    // set the radio button to be checked if it is the method chosen
                $checked = (($quotes[$i]['id'] . '_' . $quotes[$i]['methods'][$j]['id'] == $_SESSION['shipping']['id']) ? true : false);
    
                if ( ($checked == true) || ($n == 1 && $n2 == 1) ) {
                  //echo '      <div id="defaultSelected" class="moduleRowSelected">' . "\n";
                //} else {
                  //echo '      <div class="moduleRow">' . "\n";
                }
    ?>
    <?php
                if ( ($n > 1) || ($n2 > 1) ) {
    ?>
    <div class="important forward"><?php echo $currencies->format(zen_add_tax($quotes[$i]['methods'][$j]['cost'], (isset($quotes[$i]['tax']) ? $quotes[$i]['tax'] : 0))); ?></div>
    <?php
                } else {
    ?>
    <div class="important forward"><?php echo $currencies->format(zen_add_tax($quotes[$i]['methods'][$j]['cost'], $quotes[$i]['tax'])) . zen_draw_hidden_field('shipping', $quotes[$i]['id'] . '_' . $quotes[$i]['methods'][$j]['id']); ?></div>
    <?php
                }
    ?>
    
    <?php echo zen_draw_radio_field('shipping', $quotes[$i]['id'] . '_' . $quotes[$i]['methods'][$j]['id'], $checked, 'onclick="updateForm();" id="ship-'.$quotes[$i]['id'] . '-' . $quotes[$i]['methods'][$j]['id'].'"'); ?>
    <label for="ship-<?php echo $quotes[$i]['id'] . '-' . $quotes[$i]['methods'][$j]['id']; ?>" class="checkboxLabel" ><?php echo $quotes[$i]['methods'][$j]['title']; ?></label>
    <!--</div>-->
    <br class="clearBoth" />
    <?php
                $radio_buttons++;
              }
              //bof tell a friend
              if($quotes[$i]["id"] == "tellafriend") {
                echo MODULE_SHIPPING_TELL_A_FRIEND_TEXT_CUSTOMER;
                echo $tell_a_friend_email_error != "" ? "<div class='messageStackWarning'>$tell_a_friend_email_error</div>" : "";
?>
    <div class="tellAFriendContent" style="width:50%;"><strong>Email</strong></div>
    <div class="tellAFriendContent" style="width:25%;"><strong>First Name</strong></div>
    <div class="tellAFriendContent" style="width:25%;"><strong>Last Name</strong></div>
<?php          
          for ($j = 0; $j < $quotes[$i]['email_no']; $j++) {
?>
    <div class="tellAFriendContent" style="width:50%;"><?php echo zen_draw_input_field('tell_a_friend_email[]', $_SESSION["tell_a_friend_email"][$j], 'size="28"'); ?></div>
    <div class="tellAFriendContent" style="width:25%;"><?php echo zen_draw_input_field('tell_a_friend_email_f_name[]', $_SESSION["tell_a_friend_email_f_name"][$j], 'size="15"'); ?></div>
    <div class="tellAFriendContent" style="width:25%;"><?php echo zen_draw_input_field('tell_a_friend_email_l_name[]', $_SESSION["tell_a_friend_email_l_name"][$j], 'size="15"'); ?></div>
    <br class="clearBoth" />
<?php
          }
// BOF Captcha
if(is_object($captcha)) {
?>
<?php echo $captcha->img(); ?>
<?php echo $captcha->redraw_button(BUTTON_IMAGE_CAPTCHA_REDRAW, BUTTON_IMAGE_CAPTCHA_REDRAW_ALT); ?>
    <br class="clearBoth" />
    <label for="captcha"><?php echo TITLE_CAPTCHA; ?></label>
<?php echo $captcha->input_field('captcha', 'id="captcha"') . '&nbsp;<span class="alert">' . TEXT_CAPTCHA . '</span>'; ?>
    <br class="clearBoth" />
<?php
}
// EOF Captcha          
              }
//eof tell a friend
            }
    ?>
    
    </fieldset>
    <?php
          }
        }
    ?>
    
    <?php
      } else {
    ?>
      <h2 id="checkoutShippingHeadingMethod"><?php echo TITLE_NO_SHIPPING_AVAILABLE; ?></h2>
      <div id="checkoutShippingContentChoose" class="important"><?php echo TEXT_NO_SHIPPING_AVAILABLE; ?></div>
    <?php
      }
    ?>
    </fieldset>
    <?php
      }
    ?>
    <!--EOF SHIPPING-->

<?php
  $selection =  $order_total_modules->credit_selection();
  if (sizeof($selection)>0) {
    for ($i=0, $n=sizeof($selection); $i<$n; $i++) {
      if ($_GET['credit_class_error_code'] == $selection[$i]['id']) {
?>
<div class="messageStackError"><?php echo zen_output_string_protected($_GET['credit_class_error']); ?></div>

<?php
      }
      for ($j=0, $n2=sizeof($selection[$i]['fields']); $j<$n2; $j++) {
?>
<?php if(!($COWOA && $selection[$i]['module']==MODULE_ORDER_TOTAL_GV_TITLE)) {?>
  <fieldset>
  <legend><?php echo $selection[$i]['module']; ?></legend>
  <?php echo $selection[$i]['redeem_instructions']; ?>
  <div class="gvBal larger"><?php echo $selection[$i]['checkbox']; ?></div>
  <label class="inputLabel"<?php echo ($selection[$i]['fields'][$j]['tag']) ? ' for="'.$selection[$i]['fields'][$j]['tag'].'"': ''; ?>><?php echo $selection[$i]['fields'][$j]['title']; ?></label>
  <?php echo $selection[$i]['fields'][$j]['field']; ?>
  </fieldset>
  <?php } ?>
<?php
      }
    }
  }
?>

  
    </div>
    <?php // ** BEGIN PAYPAL EXPRESS CHECKOUT **
    if (!$payment_modules->in_special_checkout()) {
      // ** END PAYPAL EXPRESS CHECKOUT ** ?>
    <?php
      if ($order->content_type != 'virtual') {
        $heading_title_payment = HEADING_TITLE_PAYMENT;
      } else {
        $heading_title_payment = HEADING_TITLE_PAYMENT_VIRTUAL;
      }
    ?>
  <?php if (FEC_SPLIT_CHECKOUT == 'false') { ?>
  <br class="clearBoth" />
  <?php } ?>
  <?php
    if ($credit_covers != true) { 
      if ($order->content_type == 'virtual') {
        $checkoutStyle .= "Full";
        echo '<br class="clearBoth" />';
      }
  ?>
    
  <!-- bof payment -->
  <div id="checkoutPaymentForm<?php echo $checkoutStyle; ?>">
    <h1 id="checkoutPaymentHeading"><?php echo $heading_title_payment; ?></h1>
                
    <fieldset class="checkout" id="checkoutPayment">
      <legend><?php echo TABLE_HEADING_PAYMENT_METHOD; ?></legend>
      
      <!--BILLING ADDRESS-->
      <?php // ** BEGIN PAYPAL EXPRESS CHECKOUT **
            if (!$payment_modules->in_special_checkout()) {
            // ** END PAYPAL EXPRESS CHECKOUT ** 
      ?>
      <fieldset class="checkout" id="checkoutBillTo">
        <legend><?php echo TABLE_HEADING_BILLING_ADDRESS; ?></legend>
        <div class="addressContainer">
          <div id="checkoutBillto" class="floatingBox back">
            <address><?php echo html_entity_decode(zen_address_label($_SESSION['customer_id'], $_SESSION['billto'], true, ' ', '<br />')); ?></address>
          </div>
          <?php if (MAX_ADDRESS_BOOK_ENTRIES >= 2) { 
                  echo '<div class="buttonRow"><a id="linkCheckoutPaymentAddr" href="' . zen_href_link(FILENAME_CHECKOUT_PAYMENT_ADDRESS, '', 'SSL') . '">' . zen_image_button(BUTTON_IMAGE_CHANGE_ADDRESS, BUTTON_CHANGE_ADDRESS_ALT) . '</a></div>';
          ?>
          <?php } ?>
          <br class="clearBoth" />
        </div>
      </fieldset>
      <?php
        //require($template->get_template_dir('tpl_modules_fec_change_checkout_payment_address.php', DIR_WS_TEMPLATE, $current_page_base,'templates/fec'). '/' . 'tpl_modules_fec_change_checkout_payment_address.php');
      ?>
      <?php // ** BEGIN PAYPAL EXPRESS CHECKOUT **
            }
            // ** END PAYPAL EXPRESS CHECKOUT ** 
      ?>
      
      <?php
        if (SHOW_ACCEPTED_CREDIT_CARDS != '0') {
      ?>
      
      <?php
          if (SHOW_ACCEPTED_CREDIT_CARDS == '1') {
            echo TEXT_ACCEPTED_CREDIT_CARDS . zen_get_cc_enabled();
          }
          if (SHOW_ACCEPTED_CREDIT_CARDS == '2') {
            echo TEXT_ACCEPTED_CREDIT_CARDS . zen_get_cc_enabled('IMAGE_');
          }
      ?>
      <br class="clearBoth" />
      <?php } ?>
      
      <?php
        foreach($payment_modules->modules as $pm_code => $pm) {
          if(substr($pm, 0, strrpos($pm, '.')) == 'googlecheckout') {
            unset($payment_modules->modules[$pm_code]);
          }
        }
        $selection = $payment_modules->selection();
      
        if (sizeof($selection) > 1) {
      ?>
      <p class="important"><?php echo TEXT_SELECT_PAYMENT_METHOD; ?></p>
      <?php
        } elseif (sizeof($selection) == 0) {
      ?>
      <p class="important"><?php echo TEXT_NO_PAYMENT_OPTIONS_AVAILABLE; ?></p>
      
      <?php
        }
      ?>
      
      <?php
        $radio_buttons = 0;
        for ($i=0, $n=sizeof($selection); $i<$n; $i++) {
      ?>
      <?php
          if (sizeof($selection) > 1) {
              if (empty($selection[$i]['noradio'])) {
       ?>
      <?php echo zen_draw_radio_field('payment', $selection[$i]['id'], ($selection[$i]['id'] == $_SESSION['payment'] ? true : false), 'id="pmt-'.$selection[$i]['id'].'" onclick="updateForm();"'); ?>
      <?php   } ?>
      <?php
          } else {
            
      ?>
      <?php echo zen_draw_hidden_field('payment', $selection[$i]['id']); ?>
      <?php
          }
      ?>
      <label for="pmt-<?php echo $selection[$i]['id']; ?>" class="radioButtonLabel"><?php echo $selection[$i]['module']; ?></label>
      
      <?php
          if (defined('MODULE_ORDER_TOTAL_COD_STATUS') && MODULE_ORDER_TOTAL_COD_STATUS == 'true' and $selection[$i]['id'] == 'cod') {
      ?>
      <div class="alert"><?php echo TEXT_INFO_COD_FEES; ?></div>
      <?php
          } else {
            // echo 'WRONG ' . $selection[$i]['id'];
      ?>
      <?php
          }
      ?>
      <br class="clearBoth" />
      
      <?php
          if (isset($selection[$i]['error'])) {
      ?>
          <div><?php echo $selection[$i]['error']; ?></div>
      
      <?php
          } elseif (isset($selection[$i]['fields']) && is_array($selection[$i]['fields'])) {
      ?>
      
      <div class="ccinfo">
      <?php
            for ($j=0, $n2=sizeof($selection[$i]['fields']); $j<$n2; $j++) {
      ?>
      <label <?php echo (isset($selection[$i]['fields'][$j]['tag']) ? 'for="'.$selection[$i]['fields'][$j]['tag'] . '" ' : ''); ?>class="inputLabelPayment"><?php echo $selection[$i]['fields'][$j]['title']; ?></label><?php echo $selection[$i]['fields'][$j]['field']; ?>
      <br class="clearBoth" />
      <?php
            }
      ?>
      </div>
      <br class="clearBoth" />
      <?php
          }
          $radio_buttons++;
      ?>
      <br class="clearBoth" />
      <?php
        }
      ?>
      <!-- bof Gift Wrap -->
      <?php 
        if (FEC_GIFT_WRAPPING_SWITCH == 'true') {
          if (!file_exists(DIR_WS_MODULES . "order_total/ot_giftwrap_checkout.php")) {
            echo '<font color="red"><strong>GIFTWRAP MODULE NOT INSTALLED, PLEASE DISABLE IN FEC CONFIGURATION</strong></font>';
          } else {     
      ?>
      <?php
         $value = "ot_giftwrap_checkout.php"; 
         include_once(zen_get_file_directory(DIR_WS_LANGUAGES . $_SESSION['language'] .
                '/modules/order_total/', $value, 'false'));
         include_once(DIR_WS_MODULES . "order_total/" . $value);
         $wrap_mod = new ot_giftwrap_checkout(); 
         $use_gift_wrap = true;
         if ($wrap_mod->check()) {
      ?>
            <br />
      <fieldset class="shipping" id="gift_wrap">
      <legend><?php echo GIFT_WRAP_HEADING; ?></legend>
      <?php
          echo '<div id="cartWrapExplain">'; 
          echo '<a href="javascript:alert(\'' . GIFT_WRAP_EXPLAIN_DETAILS . '\')">' . GIFT_WRAP_EXPLAIN_LINK . '</a>';
          echo '</div>'; 
      ?>
            <table border="0" width="100%" cellspacing="0" cellpadding="0">
              <tr class="cartTableHeading">
                <th scope="col" id="ccWrapProductsHeading"><?php echo TABLE_HEADING_PRODUCTS; ?></th>
                <th scope="col" id="ccWrapHeading"><?php echo GIFT_WRAP_CHECKOFF; ?></th>
              </tr>
      <?php  
             // now loop thru all products to display quantity and price
         $prod_count = 1; 
      // tsg_logger($order->products); 
         for ($i=0, $n=sizeof($order->products); $i<$n; $i++) {
           for ($q = 1; $q <= $order->products[$i]['qty']; $q++) {
              if ($prod_count%2 == 0) {
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
               // gift wrap setting
               echo '<td class="cartWrapCheckDisplay">'; 
               $prid = $order->products[$i]['id'];
               if (zen_get_products_virtual($order->products[$i]['id'])) {
                  echo GIFT_WRAP_NA;
               } else if (DOWNLOAD_ENABLED && product_attributes_downloads_status($order->products[$i]['id'], $order->products[$i]['attributes'])) {
                  echo GIFT_WRAP_NA; 
               } else if ($wrap_mod->exclude_product($prid)) {
                  echo GIFT_WRAP_NA; 
               } else if ($wrap_mod->exclude_category($prid)) {
                  echo GIFT_WRAP_NA; 
               } else { 
                  $gift_id = "wrap_prod_" . $prod_count;
                  if (isset($_SESSION[$gift_id]) && $_SESSION[$gift_id] != '') $giftChecked = true; else $giftChecked = false;
                  echo zen_draw_checkbox_field($gift_id,'',$giftChecked, 'id="'.$gift_id .'" onclick="updateForm();"');
               }
               echo "</td>"; 
      ?>
            </tr>
      <?php
                  $prod_count++; 
               }
             }  // end for loopthru all products 
      ?>
            </table>
      </fieldset>
            <br />
      <?php
         }  
      ?>
      <?php }} ?>
      <!-- eof Gift Wrap -->
      <!-- bof doublebox -->
      <?php
        if (MODULE_ORDER_TOTAL_DOUBLEBOX_STATUS == 'true') {
          $value = "ot_doublebox_checkout.php"; 
          include_once(zen_get_file_directory(DIR_WS_LANGUAGES . $_SESSION['language'] .
                '/modules/order_total/', $value, 'false'));
          include_once(DIR_WS_MODULES . "order_total/" . $value);
          $doublebox_mod = new ot_doublebox_checkout(); 
          $use_doublebox = true;
          if ($doublebox_mod->check() && $doublebox_mod->enabled) {
      ?>
            <br />
            <hr />
      <fieldset class="shipping" id="doublebox">
      <legend><?php echo DOUBLEBOX_HEADING; ?></legend>
      <?php
          echo '<div id="cartDoubleBoxExplain">'; 
          echo '<a href="javascript:alert(\'' . DOUBLEBOX_EXPLAIN_DETAILS . '\')">' . DOUBLEBOX_EXPLAIN_LINK . '</a>';
          echo '</div>'; 
      ?>
            <table border="0" width="100%" cellspacing="0" cellpadding="0" id="cartContentsDisplay">
              <tr class="cartTableHeading">
              <th scope="col" id="ccProductsHeading"><?php echo TABLE_HEADING_PRODUCTS; ?></th>
               <th scope="col" id="ccDoubleBoxHeading"><?php echo DOUBLEBOX_CHECKOFF; ?></th>
              </tr>
      <?php  
             // now loop thru all products to display quantity and price
         $prod_count = 1; 
      // tsg_logger($order->products); 
         for ($i=0, $n=sizeof($order->products); $i<$n; $i++) {
           for ($q = 1; $q <= $order->products[$i]['qty']; $q++) {
              if ($prod_count%2 == 0) {
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
               // doublebox setting
               echo '<td class="cartDoubleBoxCheckDisplay">'; 
               $prid = $order->products[$i]['id'];
               if (zen_get_products_virtual($order->products[$i]['id'])) {
                  echo DOUBLEBOX_NA;
               } else if (DOWNLOAD_ENABLED && product_attributes_downloads_status($order->products[$i]['id'], $order->products[$i]['attributes'])) {
                  echo DOUBLEBOX_NA; 
               } else if ($doublebox_mod->exclude_product($prid)) {
                  echo DOUBLEBOX_NA; 
               } else if ($doublebox_mod->exclude_category($prid)) {
                  echo DOUBLEBOX_NA; 
               } else { 
                  $doublebox_id = "doublebox_prod_" . $prod_count;
                  echo zen_draw_checkbox_field($doublebox_id,'1',$_SESSION[$doublebox_id], 'id="'.$doublebox_id .'"');
               }
               echo "</td>"; 
      ?>
            </tr>
      <?php
                  $prod_count++; 
               }
             }  // end for loopthru all products 
      ?>
            </table>
      </fieldset>
            <hr />
            <br />
      <?php
         }
        }  
      ?>
      <!-- eof doublebox -->     

    </fieldset>
  </div>
  <?php 
    }
  ?>
  <!-- eof payment -->
  <br class="clearBoth" />
  <?php // ** BEGIN PAYPAL EXPRESS CHECKOUT **
        } else {
          ?><input type="hidden" name="payment" value="<?php echo $_SESSION['payment']; ?>" /><?php
        }
        // ** END PAYPAL EXPRESS CHECKOUT ** 
  ?>
  <!-- EOF PAYMENT -->
  <!-- bog FEC v1.27 CHECKBOX -->
  <?php
  if (FEC_CHECKBOX == 'true') {
    $checkbox = ($_SESSION['fec_checkbox'] == '1' ? true : false);
  ?>
  <fieldset class="checkout" id="checkoutFECCheckbox">
  <legend><?php echo TABLE_HEADING_FEC_CHECKBOX; ?></legend>
  <label><?php echo TEXT_FEC_CHECKBOX; ?></label>
  <?php echo zen_draw_checkbox_field('fec_checkbox', '1', $checkbox, 'id="fec_checkbox"'); ?>
  </fieldset>
  <?php 
  } 
  ?>
  <!-- eof FEC v1.27 CHECKBOX -->
  <!-- bof FEC v1.24a DROP DOWN -->
  <?php 
    if (FEC_DROP_DOWN == 'true') {
  ?>
  <fieldset class="checkout" id="checkoutDropdown">
  <legend><?php echo TABLE_HEADING_DROPDOWN; ?></legend>
  <label><?php echo TEXT_DROP_DOWN; ?></label>
  <?php echo zen_draw_pull_down_menu('dropdown', $dropdown_list_array, $_SESSION['dropdown'], 'onchange="updateForm()"', true); ?>
  </fieldset>
  <?php
    }
    if (FEC_GIFT_MESSAGE == 'true') {
  ?>
  <fieldset class="checkout" id="giftMessage">
  <legend><?php echo TABLE_HEADING_GIFT_MESSAGE; ?></legend>
  <?php echo zen_draw_textarea_field('gift-message', '45', '3', $_SESSION['gift-message']); ?>
  </fieldset>
  <!-- eof DROP DOWN -->
<?php
    }
?>
  
  <?php /*<fieldset class="checkout" id="checkoutComments">
  <legend><?php echo TABLE_HEADING_COMMENTS; ?></legend>
  <?php echo zen_draw_textarea_field('comments', '45', '3'); ?>
  </fieldset>*/ ?>
  
  <?php
    if (DISPLAY_CONDITIONS_ON_CHECKOUT == 'true') {
  ?>
  <fieldset class="checkout" id="checkoutConditions">
  <legend><?php echo TABLE_HEADING_CONDITIONS; ?></legend>
  <div><?php echo TEXT_CONDITIONS_DESCRIPTION;?></div>
  <?php echo  zen_draw_checkbox_field('conditions', '1', false, 'id="conditions"');?>
  <label class="checkboxLabel" for="conditions"><?php echo TEXT_CONDITIONS_CONFIRM; ?></label>
  </fieldset>
  <?php
    }
  ?>
  <!-- include hidden payment attributes -->
  <?php 
    if (is_array($payment_modules->modules)) {
      echo $payment_modules->process_button();
    }
  ?>
	
  <br class="clearBoth" />
  <h1 id="checkoutOrderHeading"><?php echo HEADING_TITLE_ORDER_TOTAL; ?></h1>
  <!-- BOF SHOPPING CART -->
  <?php
  // following used for setting style type of order total and discount modules only
    $selection =  $order_total_modules->credit_selection();
    $numselection = sizeof($selection);
    if (FEC_SPLIT_CHECKOUT == 'true') {
      $selectionStyle = ($numselection%2 == 0 ? 'split' : '');
    }
  ?>
  
  <div id="checkoutOrderForm">
      <div class="buttonRow forward" id="editButton"><?php echo '<a href="' . zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL') . '">' . zen_image_button(BUTTON_IMAGE_EDIT_SMALL, BUTTON_EDIT_SMALL_ALT) . '</a>'; ?></div>
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
          <th scope="col" id="ccQuantityHeading"><?php echo TABLE_HEADING_QUANTITY; ?></th>
          <th scope="col" id="ccProductsHeading" colspan="3"><?php echo TABLE_HEADING_PRODUCTS; ?></th>
          <th scope="col" id="ccTotalHeading"><?php echo TABLE_HEADING_TOTAL; ?></th>
        </tr>
      <?php // now loop thru all products to display quantity and price ?>
      <?php for ($i=0, $n=sizeof($order->products); $i<$n; $i++) { ?>
      <?php $thumbnail = zen_get_products_image($order->products[$i]['id'], 40, 42); ?>
        <tr class="<?php echo $order->products[$i]['rowClass']; ?>">
          <td class="cartQuantity"><?php echo $order->products[$i]['qty']; ?>&nbsp;x</td>
          <td class="cartImage"><?php echo $thumbnail; ?></td>
          <td class="cartProductDisplay" colspan="2"><?php echo $order->products[$i]['name']; ?>
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
          <td class="cartTotalDisplay">
                <?php echo $currencies->display_price($order->products[$i]['final_price'], $order->products[$i]['tax'], $order->products[$i]['qty']);
                if ($order->products[$i]['onetime_charges'] != 0 ) echo '<br /> ' . $currencies->display_price($order->products[$i]['onetime_charges'], $order->products[$i]['tax'], 1);
      ?>
          </td>
        </tr>
      <?php  }  // end for loopthru all products 
      ?>
      </table>
            
      <?php
        if (MODULE_ORDER_TOTAL_INSTALLED) {
          $order_totals = $order_total_modules->process();
      ?>
      <div id="orderTotals"><?php $order_total_modules->output(); ?></div>
      <?php
        }
      ?>
    </div>
      <!-- EOF SHOPPING CART -->
