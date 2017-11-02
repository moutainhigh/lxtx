<?php
/**
 * @package languageDefines
 * @copyright Copyright 2003-2006 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: shopping_cart.php 3183 2006-03-14 07:58:59Z birdbrain $
 */

define('NAVBAR_TITLE', 'The Shopping Cart');
define('HEADING_TITLE', 'Your Shopping Cart Contents');
define('HEADING_TITLE_EMPTY', 'Your Shopping Cart');
define('TEXT_INFORMATION', '');
define('TABLE_HEADING_PRODUCT_IMAGE', 'Product Image');
define('TABLE_HEADING_UPDATE', 'Update');
define('TABLE_HEADING_REMOVE', 'Remove');
define('TABLE_HEADING_QUANTITY', 'Qty.');
define('TABLE_HEADING_MODEL', 'Model');
define('TABLE_HEADING_PRICE','Unit');
define('TEXT_CART_EMPTY', 'Your Shopping Cart is empty.');
define('SUB_TITLE_SUB_TOTAL', 'Sub-Total:');
define('SUB_TITLE_TOTAL', 'Total:');

define('OUT_OF_STOCK_CANT_CHECKOUT', 'Products marked with ' . STOCK_MARK_PRODUCT_OUT_OF_STOCK . ' are out of stock or there are not enough in stock to fill your order.<br />Please change the quantity of products marked with (' . STOCK_MARK_PRODUCT_OUT_OF_STOCK . '). Thank you');
define('OUT_OF_STOCK_CAN_CHECKOUT', 'Products marked with ' . STOCK_MARK_PRODUCT_OUT_OF_STOCK . ' are out of stock.<br />Items not in stock will be placed on backorder.');

define('TEXT_TOTAL_ITEMS', 'Total Items: ');
define('TEXT_TOTAL_WEIGHT', '&nbsp;&nbsp;Weight: ');
define('TEXT_TOTAL_AMOUNT', '&nbsp;&nbsp;Amount: ');
define('TEXT_VISITORS_CART', '<a href="javascript:session_win();">[help (?)]</a>');
define('TEXT_OPTION_DIVIDER', '&nbsp;-&nbsp;');

define('TITLE_SECURE_CHECKOUT', 'Secure Checkout');
define('TITLE_DELIVERY_INFORMATION', 'Delivery Information');
define('SINGUP_NOTICE', 'Please seriously fill below form, we will send order according to all the below information you provide. If there are any problems caused by incorrect information on the way, we will not be responsible for that!');
define('TITLE_CHECKOUT_WITHOUT_AN_ACCOUNT', 'Checkout Without An Account');
define('CWA_INFO', 'For a faster checkout experience, we offer the option to checkout without creating an account.');
define('CWA_NOTICE', 'If you want to check your order status,you should <a href="'.zen_href_link(FILENAME_CREATE_ACCOUNT, '', 'SSL').'" rel="nofollow">Create Account</a> or <a href="'.zen_href_link(FILENAME_LOGIN, '', 'SSL').'" rel="nofollow">LogIn</a> First.');
define('FORGOT_PASSWORD', 'Forgot Your Password?');

define('TABLE_HEADING_SHIPPING_ADDRESS', 'Shipping Address');
define('TEXT_CHOOSE_SHIPPING_DESTINATION', 'Your order will be shipped to the address at the left or you may change the shipping address by clicking the <em>Change Address</em> button.');
define('TABLE_HEADING_BILLING_ADDRESS', 'Billing Address');
define('ENTRY_PAYMENT_ERROR', 'Please Select a Payment.');
define('ENTRY_COPYBILLING', 'Same Address for Shipping/Billing');
define('ENTRY_COPYBILLING_TEXT', '');

define('TITLE_SHIPPING_METHOD', 'Shipping Method');
define('SHIPPING_METHOD', 'UPS / DHL / TNT / Fedex / USPS');
define('TITLE_SELECT_PAYMENT', 'Payment Method');
define('DETAILED_INFO', 'Detailed Info');
define('SUB_TOTAL', 'Sub-Total:');
define('TRANSPORT_CHARGES', 'Transport charges:');
define('INFO_INSURANCE_PREMIUM', 'Insurance Premium:');
define('PROCEDURE_FEE', 'Procedure fee:');
define('TITLE_TOTAL', 'Total:');

define('TITLE_CONFIRM_INFO', 'Confirm Your Information: ');
define('CONFIRM_EMAIL', 'Email');
define('CONFIRM_FIRST_NAME', 'First Name');
define('CONFIRM_LAST_NAME', 'Last Name');
define('CONFIRM_ADDRESS', 'Address');
define('CONFIRM_COUNTRY', 'Country');
define('CONFIRM_CITY', 'City');
define('CONFIRM_STATE', 'State');
define('CONFIRM_POST_CODE', 'Zip Code');
define('CONFIRM_TEL', 'Telephone');
?>