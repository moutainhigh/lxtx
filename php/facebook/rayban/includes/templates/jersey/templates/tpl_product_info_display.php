<?php
/**
 * Page Template
 *
 * Loaded automatically by index.php?main_page=product_info.<br />
 * Displays details of a typical product
 *
 * @package templateSystem
 * @copyright Copyright 2003-2006 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_product_info_display.php 5369 2006-12-23 10:55:52Z drbyte $
 */
 //require(DIR_WS_MODULES . '/debug_blocks/product_info_prices.php');
?>
<div class="mid-cent fl">

<?php if ($messageStack->size('product_info') > 0) echo $messageStack->output('product_info'); ?>



<div class="content fl">

<div class="ware-bimg fl">

<?php if (zen_not_null($products_image)) { ?>
<?php
//display the main product image
   require($template->get_template_dir('/tpl_modules_main_product_image.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_main_product_image.php'); ?>
<?php } ?>
<br class="clearBoth" />

<?php if ($products_description != '') { ?>
<div id="productDescription">
    <div class="detail">    
    <?php echo stripslashes($products_description); ?>
    </div>
</div>
<?php } ?>

<br>
<p><br><b>When will my order ship?</b><br>
  <p>Usually all orders are processed and shipped within 3 working days.
<p>Most order ship within 24 hours. After execute an order, we will send tracking number by e-mail, and providing inquiry addresses.
<p><br><b>Can you ship to my country?</b><br>
<p>we can ship to your country.we can ship single item samples or large orders to more than 100 countries. 
<p><br><b>When will my order arrive?</b><br>
<p>This shipping method is the fastest available. Delivery times are between 7 - 10 days to all major destinations. Shipping costs vary with item, but all orders that use expedited shipping will receive a 36% discount on shipping costs.We also have Expedited  options available for most items, which means your order will arrive 3 or 4 business days after it leaves the warehouse. 
<p><br><b>Easy 365-Day Returns </b><br>
<p>We're committed to your total satisfaction. If, for any reason, you're not completely happy with your purchase, you can get a full refund of the product price and any associated tax, within 60 business days of receipt of the item(s). To receive either a credit toward an exchange or a credit on your charge account, please note that all returns and exchanges must be in new, unused or unworn condition with the original tags and stickers attached. Items deemed worn, used, dirty or missing tags will be returned to purchaser at their expense and no refund will be issued. Underwear, Customized and personalized items are not returnable. Since lost return shipments are the responsibility of the customer, be sure to obtain a tracking number from the courier for the return shipment.


</div>
<div class="ware-inf fl">
<div class="product-essential">
<div class="product_name"><?php echo $products_name; ?></div>
<p class="sku">Model: <?php echo  $products_model;?></p>
<!--<ul>-->
<?php
if((($flag_show_product_info_model == 1 and $products_model != '') or ($flag_show_product_info_weight == 1 and $products_weight !=0) or ($flag_show_product_info_quantity == 1) or ($flag_show_product_info_manufacturer == 1 and !empty($manufacturers_name))) ) {
	if($flag_show_product_info_model == 1 and $products_model !='') {
?>
	<!--<li><?php// echo '<span>'.TEXT_PRODUCT_MODEL . ' </span>' . $products_model;?></li>
	<li><span>Payment: </span><?php //echo zen_image(DIR_WS_TEMPLATE.'images/payment.png');?></li>
	<li><span>Shipping Type: </span>UPS DHL EMS USPS FEDEX</li>-->
<?php
	}
  if($flag_show_product_info_weight == 1 and $products_weight !=0) {
?>
    <li><?php echo TEXT_PRODUCT_WEIGHT .  $products_weight . TEXT_PRODUCT_WEIGHT_UNIT;?></li>
<?php
	}
  if($flag_show_product_info_manufacturer == 1 and !empty($manufacturers_name)) {
?>
   <li><?php echo TEXT_PRODUCT_MANUFACTURER . $manufacturers_name;?></li>
<?php
	}
}
$pid = (int)$_GET['products_id'];
$m1 = $pid % 6;
$m2 = $pid % 7;
switch ($m1) {
	case 1:
	case 3:
		$rate = 4.9;
		$reviews_count = 7 + ($m1 + 2) * ($m2 + 1);
		break;
	case 4:
	case 7:
		$rate = 4.8;
		$reviews_count = 8 + ($m1 + 2) * $m2;
		break;
	case 2:
	case 5:
		$rate = 4.6;
		$reviews_count = 4 + 2 * $m1 * $m2;
		break;
	default:
		$rate = 4.7;
		$reviews_count = 5 + $m1 + $m2;
 }
?>

<!--</ul>-->
<div class="fon">
<!--<span class="price-label">Our Price:</span>-->
<div class="price-box">


<?php
      // base price
      if ($show_onetime_charges_description == 'true') {
      $one_time = '<span >' . TEXT_ONETIME_CHARGE_SYMBOL . TEXT_ONETIME_CHARGE_DESCRIPTION . '</span><br />';
	  
      } else {
      $one_time = '';
      }
      echo $one_time . ((zen_has_product_attributes_values((int)$_GET['products_id']) and $flag_show_product_info_starting_at == 1) ? TEXT_BASE_PRICE : '') . zen_get_products_display_price((int)$_GET['products_id']);
      ?>
	
	
</div></div>
<p class="availability">Availability: In stock</p>
<div class="span4" style="margin-left:0px; font-weight:700; background:#eee; border:1px solid #ccc; padding:4px 0px 4px 5px;"> COLORS: </div>
<div style="margin-left:0px; border:1px solid #ccc; border-top:none; padding:4px 0px 4px 5px;height:165px;overflow: hidden;" class="span4">
  
   <?php
		$pro_all = $db->Execute(" select products_id,products_image from products");
	?>
	<?php
    while(!$pro_all->EOF){
    ?>
	<div style="" class="span1">
      <a href="<?php echo zen_href_link(zen_get_info_page($pro_all->fields['products_id']),'products_id=' . $pro_all->fields['products_id']); ?>">  
		<img <?php if($pro_all->fields['products_id'] == $_GET['products_id'] ) { echo  "style='border:2px solid #FF6827;'"; }?>  src="images/<?php echo $pro_all->fields['products_image'] ?>">
		</a>  
    </div>
    <?php
      $pro_all->MoveNext();
    }
    ?>
</div>
<br class="clearBoth">
<div class="pick-size">

<?php echo zen_draw_form('cart_quantity', zen_href_link(zen_get_info_page($_GET['products_id']), zen_get_all_get_params(array('action')) . 'action=add_product'), 'post', 'enctype="multipart/form-data"') . "\n"; ?>
<table border="0" cellpadding="0" cellspacing="0" class="AttAndCart">
<?php
  if ($pr_attr->fields['total'] > 0) {
?>
<tr><td valign="top">
<?php
//display the product atributes
  require($template->get_template_dir('/tpl_modules_attributes.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_attributes.php'); ?>
</td>
</tr>
<?php
  }
?>
<tr>
<td valign="top" colspan="2">
<?php
if (CUSTOMERS_APPROVAL == 3 and TEXT_LOGIN_FOR_PRICE_BUTTON_REPLACE_SHOWROOM == '') {
  // do nothing
} else {
?>
            <?php
    $display_qty = (($flag_show_product_info_in_cart_qty == 1 and $_SESSION['cart']->in_cart($_GET['products_id'])) ? '<p>' . PRODUCTS_ORDER_QTY_TEXT_IN_CART . $_SESSION['cart']->get_quantity($_GET['products_id']) . '</p>' : '');
            if ($products_qty_box_status == 0 or $products_quantity_order_max== 1) {
              // hide the quantity box and default to 1
              $the_button = '<input type="hidden" name="cart_quantity" value="1" />' . zen_draw_hidden_field('products_id', (int)$_GET['products_id']) . zen_image_submit(BUTTON_IMAGE_IN_CART, BUTTON_IN_CART_ALT);
            } else {
              // show the quantity box
    $the_button = '<span class="qtyText">' . PRODUCTS_ORDER_QTY_TEXT . '</span><input class="pro_quanlity" type="text" name="cart_quantity" value="' . (zen_get_buy_now_qty($_GET['products_id'])) . '" maxlength="6" size="4" /><br /><br />' . zen_get_products_quantity_min_units_display((int)$_GET['products_id']) . zen_draw_hidden_field('products_id', (int)$_GET['products_id']) . zen_image_submit(BUTTON_IMAGE_IN_CART, BUTTON_IN_CART_ALT);
            }
/****************************************************
/* Absolute-Solutions.co.uk Edit
/*
/* Attributes Grid format
/* 1 of 1
/****************************************************/
	if (isset($attrib_grid)) {
	  $the_button = zen_image_submit(BUTTON_IMAGE_IN_CART, BUTTON_IN_CART_ALT);
    }
/****************************************************
/* Absolute-Solutions.co.uk Edit
/*
/* Attributes Grid format
/* END OF 1 of 1
/****************************************************/
	$display_button = zen_get_buy_now_button($_GET['products_id'], $the_button);
  ?>
  <?php if ($display_qty != '' or $display_button != '') { ?>
    <div id="cartAdd">
    <?php
      echo $display_qty;
      echo $display_button;
			?>
    </div>
  <?php } // display qty and button ?>
<?php } // CUSTOMERS_APPROVAL == 3 ?>
</td></tr>
<?php
  if ($products_discount_type != 0) { 
?>
<tr>
<td>
<?php
//display the products quantity discount
 require($template->get_template_dir('/tpl_modules_products_quantity_discounts.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_products_quantity_discounts.php'); ?>
</td></tr>
<?php
  }
?>
</table>
<?php if(zen_get_product_is_always_free_shipping($products_id_current) && $flag_show_product_info_free_shipping) { ?>
<div id="freeShippingIcon"><?php echo TEXT_PRODUCT_FREE_SHIPPING_ICON; ?></div>
<?php } ?>
</form>
</div>	  
</div>

<br class="clearBoth">
<div class="proshipping">
<div class="proshipping1">Estimated delivery time : 7 to 9 working days</div>
<div class="proshipping2">FREE SHIPPING ON ORDER 2 PAIRS</div>
<div class="proshipping3">30 DAYS RETURN</div>
<div class="proshipping4">BEST PRICE GUARANTEE</div>
<div class="proshipping5">12 MONTH WARRANTY</div>
<div class="proshipping6">100% AUTHENTIC PRODUCTS</div>
</div>
</div>

</div></div>


<!--
<div class="mid-cent fl">
<div class="content-top fl">Describe</div>
<article>
 	
<?php //if ($products_description != '') { ?>
<div id="productDescription" class="productGeneral"><?php //echo stripslashes($products_description); ?></div>
<div class="clearBoth"></div>
<?php //} ?>
</article>
</div>
<div class="mid-cent fl">
<?php //require($template->get_template_dir('tpl_product_info_similar.php', DIR_WS_TEMPLATE, $current_page_base, 'templates').'/tpl_product_info_similar.php');?>

</div>-->
