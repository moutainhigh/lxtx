  <fieldset class="loginFieldsetCenter">
    <legend><?php echo TABLE_HEADING_SHOPPING_CART; ?></legend>
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
              <td class="cartProductDisplay" align="center"><?php echo $order->products[$i]['name']; ?>
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
    
    <?php if (sizeof($order->info['tax_groups']) > 1)  { ?>
            <td class="cartTotalDisplay">
              <?php echo zen_display_tax_value($order->products[$i]['tax']); ?>%</td>
    <?php    }  // endif tax info display  
    ?>
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
  </fieldset>