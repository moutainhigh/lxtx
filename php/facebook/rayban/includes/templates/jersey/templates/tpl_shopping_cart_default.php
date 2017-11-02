<?php
/**
* Page Template
*
* Loaded automatically by index.php?main_page=shopping_cart.<br />
* Displays shopping-cart contents
*
* @package templateSystem
* @copyright Copyright 2003-2007 Zen Cart Development Team
* @copyright Portions Copyright 2003 osCommerce
* @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
* @version $Id: tpl_shopping_cart_default.php 2007-12-02 00:00:00Z kuroi $
*/
?>
<div style="display:none">

<!-- Facebook Pixel Code -->
<script>
!function(f,b,e,v,n,t,s)
{if(f.fbq)return;n=f.fbq=function(){n.callMethod?
n.callMethod.apply(n,arguments):n.queue.push(arguments)};
if(!f._fbq)f._fbq=n;n.push=n;n.loaded=!0;n.version='2.0';
n.queue=[];t=b.createElement(e);t.async=!0;
t.src=v;s=b.getElementsByTagName(e)[0];
s.parentNode.insertBefore(t,s)}(window,document,'script',
'https://connect.facebook.net/en_US/fbevents.js');
 fbq('init', '386380038368649'); 
fbq('track', 'PageView');
fbq('track', 'AddToCart');
</script>
<noscript>
 <img height="1" width="1" 
src="https://www.facebook.com/tr?id=386380038368649&ev=PageView
&noscript=1"/>
</noscript>
<!-- End Facebook Pixel Code -->


</div>
<div class="backgroundOuter contInner clear" id="shoppingCartDefault">
<?php
if ($flagHasCartContents) {
?>
<div class="clear">
  <h1 id="cartDefaultHeading" class="back"><?php echo HEADING_TITLE; ?></h1>
  <!--bof shopping cart buttons-->
  <div class="buttonRow forward"><?php echo '<a href="' . zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL') . '#quickbuy">' . zen_image_button(BUTTON_IMAGE_CHECKOUT, BUTTON_CHECKOUT_ALT) . '</a>'; ?></div>
  <div class="buttonRow forward"><?php echo zen_back_link() . zen_image_button(BUTTON_IMAGE_CONTINUE_SHOPPING, BUTTON_CONTINUE_SHOPPING_ALT) . '</a>'; ?></div>
</div>

<?php if ($messageStack->size('shopping_cart') > 0) echo $messageStack->output('shopping_cart'); ?>

<?php   //begin quick buy ?>
<?php if ($messageStack->size('no_account') > 0) echo $messageStack->output('no_account'); ?>
<?php if ($messageStack->size('quick_shopping') > 0) echo $messageStack->output('quick_shopping'); ?>
<?php if ($messageStack->size('redemptions') > 0) echo $messageStack->output('redemptions'); ?>
<?php   //end quick buy ?>

<?php echo zen_draw_form('cart_quantity', zen_href_link(FILENAME_SHOPPING_CART, 'action=update_product','','SSL')); ?>
<div id="cartInstructionsDisplay" class="content"><?php echo TEXT_INFORMATION; ?></div>

<?php if (!empty($totalsDisplay)) { ?>
<!--div class="cartTotalsDisplay important"><?php echo $totalsDisplay; ?></div-->
<?php } ?>

<?php if ($flagAnyOutOfStock) { ?>
<?php if (STOCK_ALLOW_CHECKOUT == 'true') { ?>
<div class="messageStackError"><?php echo OUT_OF_STOCK_CAN_CHECKOUT; ?></div>
<?php } else { ?>
<div class="messageStackError"><?php echo OUT_OF_STOCK_CANT_CHECKOUT; ?></div>
<?php } //endif STOCK_ALLOW_CHECKOUT ?>
<?php } //endif flagAnyOutOfStock ?>

<table  border="0" width="100%" cellspacing="0" cellpadding="2" id="cartContentsDisplay">
  <tr class="tableHeading">
    <th scope="col" id="scProductsImage"><?php echo TABLE_HEADING_PRODUCT_IMAGE;?></th>
    <th scope="col" id="scProductsHeading"><?php echo TABLE_HEADING_PRODUCTS; ?></th>
    <th scope="col" id="scQuantityHeading"><?php echo TABLE_HEADING_QUANTITY; ?></th>
    <th scope="col" id="scUnitHeading"><?php echo TABLE_HEADING_PRICE; ?></th>
    <th scope="col" id="scTotalHeading"><?php echo TABLE_HEADING_TOTAL; ?></th>
    <th scope="col" id="scUpdateQuantity"><?php echo TABLE_HEADING_UPDATE;?></th>
    <th scope="col" id="scRemoveHeading"><?php echo TABLE_HEADING_REMOVE;?></th>
  </tr>
<!-- Loop through all products /-->
<?php
$pn = 0;
foreach ($productArray as $product) {
	if($pn == 0) { $palso_id = (int)$product['id']; }
?>
  <tr class="<?php echo $product['rowClass']; ?>">
    <td class="cartProductImage">
      <a href="<?php echo $product['linkProductsName']; ?>" class="thickbox"><?php echo $product['productsImage']; ?></a>
    </td>
    <td class="cartProductName"><a href="<?php echo $product['linkProductsName']; ?>"><?php echo $product['productsName'] . '<span class="alert bold">' . $product['flagStockCheck'] . '</span>'; ?></a>
<?php
echo $product['attributeHiddenField'];
if (isset($product['attributes']) && is_array($product['attributes'])) {
	echo '<div class="cartAttribsList">';
	echo '<ul>';
	reset($product['attributes']);
	foreach ($product['attributes'] as $option => $value) {
?>
			<li><?php echo $value['products_options_name'] . TEXT_OPTION_DIVIDER . nl2br($value['products_options_values_name']); ?></li>
<?php
	}
	echo '</ul>';
	echo '</div>';
} ?>
<?php if ((STOCK_SHOW_LOW_IN_CART == 'true') && $product['flagStockCheck']) {
	echo '<span class="alert bold">';
	echo PWA_STOCK_AVAILABLE;
	echo ((isset($product['stockAvailable'])) ? $product['stockAvailable']: 0);
	echo '</span>';
} ?>
    </td>
    <td class="cartQuantity">
<?php
if ($product['flagShowFixedQuantity']) {
	echo $product['showFixedQuantityAmount'] . '<p class="alert bold">' . $product['flagStockCheck'] . '</p>' . $product['showMinUnits'];
} else {
	echo $product['quantityField'] . '<p class="alert bold">' . $product['flagStockCheck'] . '</p>' . $product['showMinUnits'];
}
?>
    </td>
    <td class="cartUnitDisplay"><?php echo $product['productsPriceDesc']; ?></td>
    <td class="cartTotalDisplay"><?php echo $product['productsFinalPrice']. '/' .'<span style="text-decoration:line-through;color:red;">'.$product['productsPrice'].'</span>'; ?></td>
    <td class="cartQuantityUpdate">
<?php
if ($product['buttonUpdate'] == '') {
echo '' ;
} else {
echo $product['buttonUpdate'];
}
?>
    </td>
    <td class="cartRemoveItemDisplay">
<?php if ($product['buttonDelete']) { ?>
      <a href="<?php echo zen_href_link(FILENAME_SHOPPING_CART, 'action=remove_product&product_id=' . $product['id'],'SSL'); ?>"><?php echo zen_image($template->get_template_dir(ICON_IMAGE_TRASH, DIR_WS_TEMPLATE, $current_page_base,'images/icons'). '/' . ICON_IMAGE_TRASH, ICON_TRASH_ALT); ?></a>
<?php } ?>
    </td>
  </tr>
<?php
$pn++;
} // end foreach ($productArray as $product)
?>
<!-- Finished loop through all products /-->
</table>

<div id="cartSubTotal"><?php echo SUB_TITLE_SUB_TOTAL; ?> <?php echo $cartShowTotal; ?>/<span style="text-decoration:line-through;color:red;"><?php echo $cartShowPre; ?><span></div>
<style>
.cart-charge{
    border-bottom: 1px solid #e2e2e2;
    color: #4d4d4d;
    font-size: 15px;
    font-weight: 600;
    line-height: 60px;
    padding-left: 30px;
}
.cart-charge b{	
   color: #ff3068;
   font-weight: 600;
}
</style>
<div class="cart-charge" style="line-height: 30px;">
FREE Shipping On Order Over <b> <?php echo $currencies->format(80).'</b>.'; ?>
<?php $cart_charge = 80 - $_SESSION['cart']->show_total();
if($cart_charge>0){echo '<br/>Spend <b>'.$currencies->format($cart_charge).'</b> More To Get It!';}else{
	echo 'Enjoy!';
}
 ?>
</div>
<?php
// show update cart button
if (SHOW_SHOPPING_CART_UPDATE == 2 or SHOW_SHOPPING_CART_UPDATE == 3) {
?>
<!--div class="buttonRow back removeChecked"><?php echo zen_image_submit(ICON_IMAGE_CLERABASKET, ICON_CLERABASKET_ALT); ?></div-->
<?php
} else { // don't show update button below cart
?>
<?php
} // show update button
?>
<!--eof shopping cart buttons-->
</form>

<br class="clearBoth" />
<?php
if (SHOW_SHIPPING_ESTIMATOR_BUTTON == '1') {
?>
<div class="buttonRow back"><?php echo '<a href="javascript:popupWindow(\'' . zen_href_link(FILENAME_POPUP_SHIPPING_ESTIMATOR) . '\')">' . zen_image_button(BUTTON_IMAGE_SHIPPING_ESTIMATOR, BUTTON_SHIPPING_ESTIMATOR_ALT) . '</a>'; ?></div>
<?php
}
?>

<?php  // the tpl_ec_button template only displays EC option if cart contents >0 and value >0
if (defined('MODULE_PAYMENT_PAYPALWPP_STATUS') && MODULE_PAYMENT_PAYPALWPP_STATUS == 'True') {
include(DIR_FS_CATALOG . DIR_WS_MODULES . 'payment/paypal/tpl_ec_button.php');
}
?>

<?php
if (SHOW_SHIPPING_ESTIMATOR_BUTTON == '2') {
// load the shipping estimator code if needed
	require(DIR_WS_MODULES . zen_get_module_directory('shipping_estimator.php'));
}
?>

<?php /*<div class="caWrapper">
<?php require($template->get_template_dir('/tpl_modules_cart_also.php', DIR_WS_TEMPLATE, $current_page_base, 'templates').'/tpl_modules_cart_also.php');?>
<div class="notein">
<b>Please Note That:</b><br>
Your order will be sent from China, the delivery days will be 7~10 days. Please make sure your shipping address is correct, especially the Postcode.
</div>
</div>*/ ?>

<?php // BEGIN QUICK BUY 1 OF 1 ?>
<fieldset id="quickbuy">
<h2 id="Secure_checkout"><?php echo TITLE_SECURE_CHECKOUT;?></h2>
<?php
if(!empty($_SESSION['customer_id'])){
	$account_query = "SELECT *
	FROM   " . TABLE_CUSTOMERS . "
	WHERE  customers_id = :customersID";
	
	$account_query = $db->bindVars($account_query, ':customersID', $_SESSION['customer_id'], 'integer');
	$account = $db->Execute($account_query);
	$firstname     = $account->fields['customers_firstname'];
	$lastname      = $account->fields['customers_lastname'];
	$email_address = $account->fields['customers_email_address'];
	$telephone = $account->fields['customers_telephone'];
	//$address_book_id = $account->fields['customers_default_address_id'];
	
	$sql = 'SELECT * ' .
	'FROM ' . TABLE_ADDRESS_BOOK . ' ' .
	'WHERE customers_id = :customersID';
	$sql = $db->bindVars($sql, ':customersID', $_SESSION['customer_id'], 'integer');
	$address_rs = $db->Execute($sql);
	$street_address  = $address_rs->fields['entry_street_address'];
	$city            = $address_rs->fields['entry_city'];
	$state           = $address_rs->fields['entry_state'];
	$postcode        = $address_rs->fields['entry_postcode'];
	$selected_country = $address_rs->fields['entry_country_id'];
	//echo '[' . $selected_country . ']'; exit;
	unset($sql);
}else{
	$selected_country = SHOW_CREATE_ACCOUNT_DEFAULT_COUNTRY;//STORE_COUNTRY;
}
?>

<?php if ($messageStack->size('create_account') > 0) echo $messageStack->output('create_account'); ?>
<?php echo zen_draw_form('quick_shopping', zen_href_link('quick_shopping', '', 'SSL'), 'post', 'onsubmit="return check_form(this.name);"'); ?>

<div class="box" id="delivery_info">
  <div class="checkHeading"><?php echo TITLE_DELIVERY_INFORMATION;?></div>
  <div class="content clear">
<?php if (!$_SESSION['customer_id']){ ?>
    <div id="signup_info">
      <p><?php echo SINGUP_NOTICE;?></p>
<?php require($template->get_template_dir('tpl_modules_customer_info_fields.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_customer_info_fields.php'); ?>
    </div> 
<?php //start without account ?>
    <div id="withOutAccount">
      <h2><?php echo TITLE_CHECKOUT_WITHOUT_AN_ACCOUNT;?></h2>
  <?php if (FEC_NOACCOUNT_ONLY_SWITCH != "true") { ?>
      <p><?php echo CWA_INFO;?></p>
      <p><?php echo CWA_NOTICE;?></p>
      <p class="forward"><a href="<?php echo zen_href_link(FILENAME_PASSWORD_FORGOTTEN, '', 'SSL');?>" rel="nofollow"><?php echo FORGOT_PASSWORD;?></a></p>
  <?php } ?>
      <p class="verisign clearBoth"><a href="https://www.truste.com/"  target="_blank"  rel="nofollow"><img src="<?php echo DIR_WS_TEMPLATE.'images/truste.gif';?>" alt="truste" /></a> &nbsp;&nbsp;<a href="https://www.verisign.com/" target="_blank" rel="nofollow"><img src="<?php echo DIR_WS_TEMPLATE.'images/verisign.gif';?>" alt="verisign" /></a></p>
    </div>
<?php //end without account?> 
<?php } else {?>
    <fieldset class="checkout" id="checkoutShipTo">
    <legend><?php echo TABLE_HEADING_SHIPPING_ADDRESS; ?></legend>
    <?php if ($messageStack->size('checkout_shipping') > 0) echo $messageStack->output('checkout_shipping'); ?>
    <div id="checkoutShipto" class="floatingBox back">
    <?php if (!isset($_SESSION['sendto'])) $_SESSION['sendto'] = $_SESSION['customer_default_address_id']; ?>
    <address class="checkoutAddress"><?php echo zen_address_label($_SESSION['customer_id'], $_SESSION['sendto'], true, ' ', '<br />'); ?></address>
    <?php if ($displayAddressEdit) { ?>
    <div class="buttonRow"><?php echo '<a href="' . $editShippingButtonLink . '">' . zen_image_button(BUTTON_IMAGE_CHANGE_ADDRESS, BUTTON_CHANGE_ADDRESS_ALT) . '</a>'; ?></div>
    <?php } ?>
    </div>
    </fieldset>
    
    <fieldset class="checkout" id="checkoutBillTo">
    <legend><?php echo TABLE_HEADING_BILLING_ADDRESS; ?></legend>
    <?php if ($messageStack->size('checkout_billing') > 0) echo $messageStack->output('checkout_billing'); ?>
    <div id="checkoutBillto" class="floatingBox back">
    <?php if (!isset($_SESSION['billto'])) $_SESSION['billto'] = $_SESSION['customer_default_address_id']; ?>
    <address class="checkoutAddress"><?php echo zen_address_label($_SESSION['customer_id'], $_SESSION['billto'], true, ' ', '<br />'); ?></address>
    <?php if ($displayAddressEdit) { ?>
    <div class="buttonRow"><?php echo '<a href="' . $editPaymentButtonLink . '">' . zen_image_button(BUTTON_IMAGE_CHANGE_ADDRESS, BUTTON_CHANGE_ADDRESS_ALT) . '</a>'; ?></div>
    <?php } ?>
    </div>
    </fieldset>
<?php }?> 
  </div>
</div>

<?php /*
<div id="discount" class="box">
<?php
$selection = $order_total_modules->credit_selection();
if (sizeof($selection)>0) {
	for ($i=0, $n=sizeof($selection); $i<$n; $i++) {
		if ($_GET['credit_class_error_code'] == $selection[$i]['id']) {
?>
  <div class="messageStackError"><?php echo zen_output_string_protected($_GET['credit_class_error']); ?></div>
<?php
		}
		for ($j=0, $n2=sizeof($selection[$i]['fields']); $j<$n2; $j++) {
?>
  <div class="checkHeading"><?php echo $selection[$i]['module']; ?></div>
  <div class="content clear"><?php echo $selection[$i]['redeem_instructions']; ?>
  <div class="gvBal larger"><?php echo $selection[$i]['checkbox']; ?></div>
  <div class="inputbox"><label class="inputLabel"<?php echo ($selection[$i]['fields'][$j]['tag']) ? ' for="'.$selection[$i]['fields'][$j]['tag'].'"': ''; ?>><b><?php echo $selection[$i]['fields'][$j]['title']; ?></b></label><?php echo $selection[$i]['fields'][$j]['field']; ?></div>
  </div>
<?php
		}
	}
}
?>
</div>
*/?>

<div class="box" id="shippingbox">
	<div class="checkHeading"><?php echo TITLE_SHIPPING_METHOD;?></div>
    <div class="content clear">
    <p><?php echo SHIPPING_METHOD;?></p>
    </div>
</div>

<div class="box payment_col" id="payment">
  <div class="checkHeading"><?php echo TITLE_SELECT_PAYMENT;?></div>
  <div class="content2 clear">
<?php
require(DIR_WS_CLASSES . 'payment.php');

$payment_modules = new payment;
$selection = $payment_modules->selection();
//echo '456';
//echo "123".json_encode($selection);

if (sizeof($selection) > 1) {
} elseif (sizeof($selection) == 0) {
?>
    <p class="important"><?php echo TEXT_NO_PAYMENT_OPTIONS_AVAILABLE; ?></p>
<?php
}
$radio_buttons = 0;
for ($i=0, $n=sizeof($selection); $i<$n; $i++) {
	//if (sizeof($selection) > 1) {
		if (empty($selection[$i]['noradio'])) {
			echo zen_draw_radio_field('payment', $selection[$i]['id'], ($selection[$i]['id'] == $_SESSION['payment'] ? true : false), 'id="pmt-'.$selection[$i]['id'].'"');
		}
	//} else {
	//	echo zen_draw_hidden_field('payment', $selection[$i]['id']);
	//}
?>
    <label for="pmt-<?php echo $selection[$i]['id']; ?>" class="radioButtonLabel"><?php echo $selection[$i]['module']; ?></label>
    
<?php
	if (defined('MODULE_ORDER_TOTAL_COD_STATUS') && MODULE_ORDER_TOTAL_COD_STATUS == 'true' and $selection[$i]['id'] == 'cod') {
?>
    <div class="alert"><?php echo TEXT_INFO_COD_FEES; ?></div>
<?php
	} else {
		//echo 'WRONG ' . $selection[$i]['id'];
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
  </div>
  <!--ÓÒ±ß¿ªÊ¼-->
  <div id="cos-cartRight">
           
           
<div class="cartInfoBlock">

    <div class="carline"></div>
    <div>
        <div class="con">
            
            <div class="con-text">
                <div class="left">
                    <img src="<?php echo HTTP_SERVER . DIR_WS_CATALOG . DIR_WS_TEMPLATE; ?>images/cartRightIcon_02.png">
                </div>
                <div class="left margin10 textCon">
                    <p>100% Secured</p>
                    The shopping is secure and safe. Guaranteed!                </div>
                <div class="clearfix"></div>
            </div>

            <div class="con-text">
                <div class="left">
                <img src="<?php echo HTTP_SERVER . DIR_WS_CATALOG . DIR_WS_TEMPLATE; ?>images/cartRightIcon_03.png">
                </div>
                <div class="left margin10 textCon">
                    <p>Fast Shipping</p>
                    We guarantee the fast shipping speed.                </div>
                <div class="clearfix"></div>
            </div>
            <div class="con-text">
                <div class="left">
                    <img src="<?php echo HTTP_SERVER . DIR_WS_CATALOG . DIR_WS_TEMPLATE; ?>images/cartRightIcon_04.png">
                </div>
                <div class="left margin10 textCon">
                    <p>100% Authentic</p>
                    We offer 100% authentic products and service.                </div>
                <div class="clearfix"></div>
            </div>
        </div>
    </div>
    <div class="con-help">
            
            <div class="left">
                <img src="<?php echo HTTP_SERVER . DIR_WS_CATALOG . DIR_WS_TEMPLATE; ?>images/cartRightIcon_05.png">
            </div>
            <div class="left contactUs">
            <h1>Need help?</h1>
            <span>We are here to help:</span>
            <span><a rel="nofollow" href="<?php echo zen_href_link(FILENAME_CONTACT_US);?>">Send Email</span></a></span>
            </div>
      
        <div class="clearfix"></div>
    </div>
</div>


<div style="display: none;" class="Head-preBlock shippingBlock">
    <div class="preBox">
        <span class="close-btn"></span>
        <div class="preContent">
            <h3>Free shipping</h3>
            <ul>
                <li>We offer free shipping for customers from United States,Canada, Australia, New Zealand.</li>
                <li>Apart from these countries, we charge $19 for shipping cost.</li>
            </ul>

            <h3>Free returns &amp; exchange</h3>
            <ul>
                <li>For orders containing only sunglasses or prescription glasses with free 1.56 single vision lenses, if for whatever reason you're not happy with your purchase, we provide 100% no hassle return within 30 days. You will get a full refund and we will even pay for return shipping for customers from US, Canada, Australia, New Zealand, France, Germany, and UK. Your returned glasses (either for refund or exchange) must be unworn, in the state you receive them and in the original packaging.</li>
                <li>For orders containing customized lenses, we provide one-time free exchange. For customers who return for a refund, we can only make a 50% refund because the lenses wonâ€™t fit anybody else. Our customized lenses include bifocal lenses, varifocal lenses, High Index 1.60 lenses, Super High Index 1.67 lenses, Ultra High Index 1.74 lenses, coatings, sunglasses tints or single vision lenses with strong prescriptions (SPH of +/- 3.00 and above).</li>


            </ul>
        </div>
    </div>
</div>

        </div>
<!--ÓÒ±ß½áÊø-->
</div>
<div class="clearBoth"></div>

<div id="checkout_bot" class="box">
  <div class="content clear">
    <div class="welcomMesg"><h1></h1></div>
    <div id="CustShowPrice" class="forward clear">
      <table cellpadding="0" cellspacing="2">
      <thead>
      <tr>
        <th colspan="2"><?php echo DETAILED_INFO;?></th>
      </tr>
      </thead>
      <tr>
        <th><?php echo SUB_TOTAL;?></th>
        <td class="loading"><?php echo 123; echo $cartShowTotal; echo $cartShowPre; ?></td>
      </tr>
      <tr >
        <th><?php echo TRANSPORT_CHARGES;?></th>
        <td class="loading"><?php echo $currencies->format($ship_fee);?></td>
      </tr>
      <tr>
        <th><?php echo INFO_INSURANCE_PREMIUM;?></th>
        <td class="loading"><?php echo $currencies->format($ship_fee);?></td>
      </tr>
      <tr>
        <th><?php echo PROCEDURE_FEE;?></th>
        <td class="loading"><?php echo $currencies->format($products_insurance_premium);?></td>
      </tr>
      <tr>
        <th class="totalText"><?php echo TITLE_TOTAL;?></th>
        <td class="totlaPriceText loading"><?php echo $cartShowTotal; ?></td>
      </tr>
      </table>
      <div id="errorMessage"></div>
    </div>
  </div>  
</div>

<div class="buttonRow confirmOrder">
<div class="buttonRow back"><?php echo zen_back_link() . zen_image_button(BUTTON_IMAGE_CONTINUE_SHOPPING, BUTTON_CONTINUE_SHOPPING_ALT) . '</a>'; ?></div>

<?php echo zen_image_submit(BUTTON_IMAGE_CONFIRM_ORDER, BUTTON_CONFIRM_ORDER_ALT, 'id="confirmOrderBtn" class="btnDisable" disabled="disabled"'); ?></div>
</form>

</fieldset>
<?php // // END QUICK BUY 1 OF 1 ?>

<?php
} else {
?>
<div id="emptyCartBody">
<h2 id="emptyMsg"><?php echo TEXT_CART_EMPTY; ?></h2>
</div>
<div class="cartEmptycontinue"><?php echo zen_back_link() . zen_image_button(BUTTON_IMAGE_CONTINUE_SHOPPING, BUTTON_CONTINUE_SHOPPING_ALT) . '</a>'; ?></div>
<?php
$show_display_shopping_cart_empty = $db->Execute(SQL_SHOW_SHOPPING_CART_EMPTY);

while (!$show_display_shopping_cart_empty->EOF) {
?>

<?php
if ($show_display_shopping_cart_empty->fields['configuration_key'] == 'SHOW_SHOPPING_CART_EMPTY_FEATURED_PRODUCTS') { ?>
<?php
/**
* display the Featured Products Center Box
*/
?>
<?php require($template->get_template_dir('tpl_modules_featured_products.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_featured_products.php'); ?>
<?php } ?>

<?php
if ($show_display_shopping_cart_empty->fields['configuration_key'] == 'SHOW_SHOPPING_CART_EMPTY_SPECIALS_PRODUCTS') { ?>
<?php
/**
* display the Special Products Center Box
*/
?>
<?php require($template->get_template_dir('tpl_modules_specials_default.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_specials_default.php'); ?>
<?php } ?>

<?php
if ($show_display_shopping_cart_empty->fields['configuration_key'] == 'SHOW_SHOPPING_CART_EMPTY_NEW_PRODUCTS') { ?>
<?php
/**
* display the New Products Center Box
*/
?>
<?php require($template->get_template_dir('tpl_modules_whats_new.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_whats_new.php'); ?>
<?php } ?>

<?php
if ($show_display_shopping_cart_empty->fields['configuration_key'] == 'SHOW_SHOPPING_CART_EMPTY_UPCOMING') {
	include(DIR_WS_MODULES . zen_get_module_directory(FILENAME_UPCOMING_PRODUCTS));
}
?>
<?php
$show_display_shopping_cart_empty->MoveNext();
} // !EOF
?>

<?php
}
?>
</div>
