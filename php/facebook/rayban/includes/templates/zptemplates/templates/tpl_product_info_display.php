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
<div id="produccenter">
<div class="mid-cent">

<?php if (DEFINE_BREADCRUMB_STATUS == '1' || (DEFINE_BREADCRUMB_STATUS == '2' && !$this_is_home_page) ) { ?>
	<div id="navBreadCrumb"><?php echo $breadcrumb->trail(BREAD_CRUMBS_SEPARATOR); ?></div>
<?php } ?>

<?php if ($messageStack->size('product_info') > 0) echo $messageStack->output('product_info'); ?>



<div class="content fl">
<br />
<div class="ware-bimg fl">

<?php if (zen_not_null($products_image)) { ?>
<?php
//display the main product image
   require($template->get_template_dir('/tpl_modules_main_product_image.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_main_product_image.php'); ?>
   
<?php } ?>

</div>

<div class="ware-inf">
<div class="product-essential">
<div class="product_name"><?php echo $products_name; ?></div>

<p class="sku"><strong>Model: </strong><?php echo  $products_model;?></p>
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
/*$pid = (int)$_GET['products_id'];
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
 }*/
?>


<?php /*?><div class="fon2">
<div>
<p style="font-weight:bold;" class="availability in-stock">Disponibilité: <span>En stock</span></p>
</div>
<div>
rang <img src="<?php echo DIR_WS_TEMPLATE; ?>images/nike/seller5.gif">
</div>
</div><?php */?>
<div class="clearBoth"></div>


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

<div class="info-qty">


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
    /*$the_button = '<span class="qtyText">' . PRODUCTS_ORDER_QTY_TEXT . '</span><input type="text" name="cart_quantity" value="' . (zen_get_buy_now_qty($_GET['products_id'])) . '" maxlength="6" size="4" /><br />' . zen_get_products_quantity_min_units_display((int)$_GET['products_id']) . zen_draw_hidden_field('products_id', (int)$_GET['products_id']) . zen_image_submit(BUTTON_IMAGE_IN_CART, BUTTON_IN_CART_ALT);
            }*/
			$the_button = '<input id="min" name="" type="button" value="-" /><input id="text_box" style="height:27px; text-align:center; border: 1px solid #ccc;" type="text" name="cart_quantity" value="' . (zen_get_buy_now_qty($_GET['products_id'])) . '" maxlength="6" size="4" /><input id="add" name="" type="button" value="+" /><br />' . zen_get_products_quantity_min_units_display((int)$_GET['products_id']) . '<br />' . zen_draw_hidden_field('products_id', (int)$_GET['products_id']) . zen_image_submit(BUTTON_IMAGE_IN_CART, BUTTON_IN_CART_ALT); 
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
</div>

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


<div class="clearBoth"></div>
 
</div>
</div>


</div>
<br class="clearBoth">

<?php if ($products_description != '') { ?>
<div id="productDescription">
    <div class="detail">
    <h3>Details</h3>
    <?php echo stripslashes($products_description); ?>
    </div>
</div>
<div class="clearBoth"></div>
<?php } ?>

<div class="clearBoth"></div>

<!--<div class="mid-cent fl">
<div class="content-top fl">Describe</div>
<article>
 	
<?php //if ($products_description != '') { ?>
<div id="productDescription" class="productGeneral"><?php //echo stripslashes($products_description); ?></div>
<div class="clearBoth"></div>
<?php //} ?>
</article>
</div>-->
<div class="mid-cent fl">
<?php require($template->get_template_dir('tpl_product_info_similar.php', DIR_WS_TEMPLATE, $current_page_base, 'templates').'/tpl_product_info_similar.php');?>

</div>
</div>

</div>

<link rel='stylesheet' type='text/css' href='includes/templates/jersey/css/jqzoom.css' />
<link rel='stylesheet' type='text/css' href='includes/templates/jersey/css/jqlightbox.css' />
<script type='text/javascript' src='includes/templates/jersey/jscript/jquery/jquery-1.4.2.min.js'></script>
<script type='text/javascript' src='includes/templates/jersey/jscript/jquery/jqzoom.pack.1.0.1.js'></script>
<script type='text/javascript' src='includes/templates/jersey/jscript/jquery/jquery.lightbox-0.5.min.js'></script>
<script language="javascript" type="text/javascript"><!--

$(document).ready(function(){

var options = {
	zoomType: "standard",
	zoomWidth: 350,
	zoomHeight: 350,
	xOffset: 10,
	yOffset: 0,
	position: "right",
	lens: true,
	imageOpacity: 0.2,
	title: false,
	showEffect: "show",
	hideEffect: "hide",
	fadeinSpeed: "fast",
	fadeoutSpeed: "slow",
	showPreload: true,
	preloadText: "Loading zoom",
	preloadPosition: "center"
};

$("#jqzoomMain").jqzoom(options);

$(".jqzoom, .jqzoomAdditional").click(function(){return false;});

$(".jqzoomAdditional").hover(function(){
	var $jqzoomMain = $("#jqzoomMain");
	var $jqzoomMainImg = $("#jqzoomMain").children('img:first');
	var $thisImag = $(this).children('img:first');

	var jqzoomMainHref = $jqzoomMain.attr('href');
	var jqzoomMainImgSrc = $jqzoomMainImg.attr('src');

	$jqzoomMain.attr('href', $(this).attr('href'));
	$jqzoomMainImg.attr('src', $thisImag.attr('src'));

	$(this).attr('href', jqzoomMainHref);
	$thisImag.attr('src', jqzoomMainImgSrc);

	return false;
}, function(){})
})
//--></script><script language="javascript" type="text/javascript"><!--

$(function() {
	$('a.jqlightbox').lightBox({
		overlayBgColor: '#000000',
		overlayOpacity: 0.8,
		containerBorderSize: 10,
		containerResizeSpeed: 400,
		fixedNavigation: false				
			
});

})
//--></script>

<script src="<?php echo DIR_WS_TEMPLATE; ?>jscript/cart.js" type="text/javascript"></script>