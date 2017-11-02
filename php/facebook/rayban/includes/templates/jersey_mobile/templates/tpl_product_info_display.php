<?php
/**
 * Page Template
 *
 * Loaded automatically by index.php?main_page=product_info.<br />
 * Displays details of a typical product
 *
 * @package templateSystem
 * @copyright Copyright 2003-2011 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_product_info_display.php 19690 2011-10-04 16:41:45Z drbyte $
 */
 //require(DIR_WS_MODULES . '/debug_blocks/product_info_prices.php');
?>
<div class="product-name"><h1><?php echo $products_name?></h1></div>

<script src="<?php echo DIR_WS_TEMPLATE; ?>jscript/unslider.js"></script>
<div class="banner-slide" id="b04">
<ul>
    <li>
    	<?php require($template->get_template_dir('/tpl_modules_main_product_image.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_main_product_image.php'); ?>
    </li>
     
    <?php /*?><li class="additionalImages centeredContent back"><?php */?>
      <?php
require($template->get_template_dir('tpl_modules_additional_images.php', DIR_WS_TEMPLATE, $current_page_base, 'templates').'/tpl_modules_additional_images.php');?>


    </ul>
</div>
<script type="text/javascript">
$(function() {
	$('.banner-slide').unslider({
	    speed: 2000,               //  滚动速度
	    delay: 500,              //  动画延迟
	    complete: function() {},  //  动画完成的回调函数
	    keys: true,               //  启动键盘导航
	    dots: true,               //  显示点导航
	    fluid: false              //  支持响应式设计
	});
});
</script>
<style type="text/css">
.banner-slide img{width: 100%; height: 100%;}
ul, ol { padding: 0;}
.banner-slide { position: relative; overflow: auto; text-align: center;}
.banner-slide li { list-style: none; }
.banner-slide ul li { float: left; }
#b04 { width: 100%;}
#b04 .dots { position: absolute; left: 0; right: 0; bottom: 00px;}
#b04 .dots li 
{ 
	display: inline-block; 
	width: 5px; 
	height: 5px; 
	margin: 0 4px; 
	text-indent: -999em; 
	border: 1px solid #222; 
	border-radius: 5px; 
	cursor: pointer; 
	opacity: .4; 
	-webkit-transition: background .5s, opacity .5s; 
	-moz-transition: background .5s, opacity .5s; 
	transition: background .5s, opacity .5s;
}
#b04 .dots li.active 
{
	background: #000;
	opacity: 1;
}
#b04 .arrow { position: absolute; top: 200px;}
#b04 #al { left: 15px;}
#b04 #ar { right: 15px;}
</style>




<div class="product-view">

<?php echo zen_draw_form('cart_quantity', zen_href_link(zen_get_info_page($_GET['products_id']), zen_get_all_get_params(array('action')) . 'action=add_product', $request_type), 'post', 'enctype="multipart/form-data"') . "\n"; ?>

<?php /*?><div class="web_pro_detail_cart_content">
        	<script language="javascript">
				function showTab(ID){
					 for(var i=1;i<7;i++){
					  if(ID==i){
					  document.getElementById('tab'+i).blur();
					  document.getElementById("tab"+i).className="selectTab2";
					  document.getElementById("tabContenta"+i).style.display="block";
					  }else{
					  document.getElementById("tab"+i).className="off";
					  document.getElementById("tabContenta"+i).style.display="none";
					  }
					 }
					 return false;
				}
			</script>
        	<ul id="tags2" class="tab">
				<li href="javascript:void(0)" id="tab1" onclick="showTab('1')" class="selectTab2"><a>Product</a></li>
				<li href="javascript:void(0)" id="tab2" class="off" onclick="showTab('2')"><a>Detail</a></li>
			</ul>
            <div id="tabContenta1" class="web_pro_detail_tab_content">
            	<div class="product-shop">
                	<div class="product-main-info">
                		<div class="product-name">
                        	<h1><?php echo $products_name?></h1>
                        </div>
                    </div>
                </div>
                <div class="product-img-box">

<?php
  if (zen_not_null($products_image)) {
  ?>
<?php require($template->get_template_dir('/tpl_modules_main_product_image.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_main_product_image.php'); ?>
<?php
  }
?>


<div style="clear:both;"></div>
                </div>
            </div>
            <div id="tabContenta2" class="web_pro_detail_tab_content web_tab_body_none">
						<?php echo stripslashes($products_description); ?>
				<?php
require($template->get_template_dir('tpl_modules_additional_images.php', DIR_WS_TEMPLATE, $current_page_base, 'templates').'/tpl_modules_additional_images.php');?>
				</div>
        </div><?php */?>
		
		
		       <div class="ware-inf fl">
        	<ul>
			<?php if ( (($flag_show_product_info_model == 1 and $products_model != '') or ($flag_show_product_info_weight == 1 and $products_weight !=0) or ($flag_show_product_info_quantity == 1) or ($flag_show_product_info_manufacturer == 1 and !empty($manufacturers_name))) ) { ?>
<!--<ul id="productDetailsList" class="floatingBox back">

  <?php //echo (($flag_show_product_info_model == 1 and $products_model !='') ? '<li><span class="product-info-label">' . TEXT_PRODUCT_MODEL . '</span>' . $products_model . '</li>' : '') . "\n"; ?>
  <?php //echo (($flag_show_product_info_weight == 1 and $products_weight !=0) ? '<li><span class="product-info-label">' . TEXT_PRODUCT_WEIGHT . '</span>' .  $products_weight . TEXT_PRODUCT_WEIGHT_UNIT . '</li>'  : '') . "\n"; ?>
  <?php //echo (($flag_show_product_info_quantity == 1) ? '<li><span class="product-info-label">'. TEXT_PRODUCT_QUANTITY . ': </span>'  . $products_quantity  . '</li>'  : '') . "\n"; ?>
  <?php //echo (($flag_show_product_info_manufacturer == 1 and !empty($manufacturers_name)) ? '<li><span class="product-info-label">' . TEXT_PRODUCT_MANUFACTURER . '</span>' . $manufacturers_name . '</li>' : '') . "\n"; ?>
  
</ul>-->

<?php
  }
?>



            </ul>
            <div class="view_price">






		
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
					
	
		</div>

</div>
		</div>
		<div class="span4" style="margin-left:0px; font-weight:700; background:#eee; border:1px solid #ccc; padding:4px 0px 4px 5px;float:left;width:98%;"> COLORS: </div>
          <style type="text/css">
.product-color .span1{ width: 20%; float: left;}
</style>
	<div style="margin-left:0px;  padding:4px 0px 4px 5px;float:left;width:98%;border-bottom:1px solid #ccc;" class="product-color span4">
  
   <?php
		$pro_all = $db->Execute(" select products_id,products_image from products");
	?>
	<?php
    while(!$pro_all->EOF){
    ?>
	<div style="margin-left:10px;" class="span1">
      <a href="<?php echo zen_href_link(zen_get_info_page($pro_all->fields['products_id']),'products_id=' . $pro_all->fields['products_id']); ?>">  
		<img <?php if($pro_all->fields['products_id'] == $_GET['products_id'] ) { echo  "style='border:2px solid #FF6827;width:100%;'"; }else{echo "style='border:2px solid #fff;width:100%;'";}?>  src="images/<?php echo $pro_all->fields['products_image'] ?>">
		</a>  
    </div>
    <?php
      $pro_all->MoveNext();
    }
    ?>
</div>
		<br style="clear:both">
		<div class="product-options">
		
		<?php
  if ($pr_attr->fields['total'] > 0) {
?>
<?php
/**
 * display the product atributes
 */
  require($template->get_template_dir('/tpl_modules_attributes.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_attributes.php'); ?>
<?php
  }
?>
		
		</div>
		
		
		<div class="view_qty">
		
		
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
    $the_button = '<span class="qtyText">' . PRODUCTS_ORDER_QTY_TEXT . '</span><input type="text" class="cart_quantity" name="cart_quantity" value="' . (zen_get_buy_now_qty($_GET['products_id'])) . '" maxlength="6" size="4" /><br /><br />' . zen_get_products_quantity_min_units_display((int)$_GET['products_id']) . zen_draw_hidden_field('products_id', (int)$_GET['products_id']) . '<input type="submit" value="Add to Cart" class="btn btn-large btn-danger cssButton button_in_cart">';
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

</form>
<div class="proshipping">
<div class="proshipping1">Estimated delivery time : 7 to 9 working days</div>
<div class="proshipping2">FREE SHIPPING ON ORDERS 2 PAIRS</div>
<div class="proshipping3">30 DAYS RETURN</div>
<div class="proshipping4">BEST PRICE GUARANTEE</div>
<div class="proshipping5">12 MONTH WARRANTY</div>
<div class="proshipping6">100% AUTHENTIC PRODUCTS</div>
</div>


<?php echo stripslashes($products_description); ?>

<!--<div class="mid-cent fl">
<?php //require($template->get_template_dir('tpl_product_info_similar.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_product_info_similar.php'); ?>
</div>-->



<?php if (PRODUCT_INFO_PREVIOUS_NEXT == 2 or PRODUCT_INFO_PREVIOUS_NEXT == 3) { ?>
<?php
/**
 * display the product previous/next helper
 */
 require($template->get_template_dir('/tpl_products_next_previous.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_products_next_previous.php'); ?>
<?php } ?>
<?php
  if ($flag_show_product_info_reviews == 1) {
    // if more than 0 reviews, then show reviews button; otherwise, show the "write review" button
    if ($reviews->fields['count'] > 0 ) { ?>
<div id="productReviewLink" class="buttonRow back"><?php echo '<a href="' . zen_href_link(FILENAME_PRODUCT_REVIEWS, zen_get_all_get_params()) . '">' . zen_image_button(BUTTON_IMAGE_REVIEWS, BUTTON_REVIEWS_ALT) . '</a>'; ?></div>
<br class="clearBoth" />
<p class="reviewCount"><?php echo ($flag_show_product_info_reviews_count == 1 ? TEXT_CURRENT_REVIEWS . ' ' . $reviews->fields['count'] : ''); ?></p>
<?php } else { ?>
<div id="productReviewLink" class="buttonRow back"><?php echo '<a href="' . zen_href_link(FILENAME_PRODUCT_REVIEWS_WRITE, zen_get_all_get_params(array())) . '">' . zen_image_button(BUTTON_IMAGE_WRITE_REVIEW, BUTTON_WRITE_REVIEW_ALT) . '</a>'; ?></div>
<br class="clearBoth" />
<?php
  }
}
?>
<?php
  if ($products_date_available > date('Y-m-d H:i:s')) {
    if ($flag_show_product_info_date_available == 1) {
?>
  <p id="productDateAvailable" class="productGeneral centeredContent"><?php echo sprintf(TEXT_DATE_AVAILABLE, zen_date_long($products_date_available)); ?></p>
<?php
    }
  } else {
    if ($flag_show_product_info_date_added == 1) {
?>
      <p id="productDateAdded" class="productGeneral centeredContent"><?php echo sprintf(TEXT_DATE_ADDED, zen_date_long($products_date_added)); ?></p>
<?php
    } // $flag_show_product_info_date_added
  }
?>
<?php
  if (zen_not_null($products_url)) {
    if ($flag_show_product_info_url == 1) {
?>
    <p id="productInfoLink" class="productGeneral centeredContent"><?php echo sprintf(TEXT_MORE_INFORMATION, zen_href_link(FILENAME_REDIRECT, 'action=url&goto=' . urlencode($products_url), 'NONSSL', true, false)); ?></p>
<?php
    } // $flag_show_product_info_url
  }
?>
<?php require($template->get_template_dir('tpl_modules_also_purchased_products.php', DIR_WS_TEMPLATE, $current_page_base,'templates'). '/' . 'tpl_modules_also_purchased_products.php');?>



