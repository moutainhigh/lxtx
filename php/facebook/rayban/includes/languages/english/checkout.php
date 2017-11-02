<?php
//
// +----------------------------------------------------------------------+
// |zen-cart Open Source E-commerce                                       |
// +----------------------------------------------------------------------+
// | Copyright (c) 2007 Numinix Technology http://www.numinix.com         |
// |                                                                      |
// | Portions Copyright (c) The Zen Cart Development Team                 |
// | http://www.zen-cart.com/index.php                                    |
// |                                                                      |
// | Portions Copyright (c) 2003 osCommerce                               |
// +----------------------------------------------------------------------+
// | This source file is subject to version 2.0 of the GPL license,       |
// | that is bundled with this package in the file LICENSE, and is        |
// | available through the world-wide-web at the following url:           |
// | http://www.zen-cart.com/license/2_0.txt.                             |
// | If you did not receive a copy of the zen-cart license and are unable |
// | to obtain it through the world-wide-web, please send a note to       |
// | license@zen-cart.com so we can mail you a copy immediately.          |
// +----------------------------------------------------------------------+

//BREADCRUMB
define('NAVBAR_TITLE', 'Checkout');

//SECTION HEADINGS
define('HEADING_TITLE_ORDER_TOTAL', 'Your Order Total:');
define('HEADING_TITLE_SHIPPING', 'Delivery Information'); 
define('HEADING_TITLE_PAYMENT', 'Payment Information'); 
define('HEADING_TITLE_PAYMENT_VIRTUAL', 'Payment Information');

//TABLE HEADINGS
define('TABLE_HEADING_PAYMENT_METHOD', 'Payment Method');
define('TABLE_HEADING_SHIPPING_METHOD', 'Shipping Method'); 
define('TABLE_HEADING_COMMENTS', 'Special Instructions or Order Comments'); 
define('TABLE_HEADING_SHIPPING_ADDRESS', 'Shipping Address');
define('TABLE_HEADING_SHOPPING_CART', 'Shopping Cart Contents');
define('TABLE_HEADING_BILLING_ADDRESS', 'Billing Address');
define('TABLE_HEADING_DROPDOWN', 'Drop Down Heading');
define('TABLE_HEADING_GIFT_MESSAGE', 'Gift Message');
define('TABLE_HEADING_FEC_CHECKBOX', 'Optional Checkbox');

//TITLES
define('TITLE_BILLING_ADDRESS', 'Billing Address:'); 
define('TITLE_CONTINUE_CHECKOUT_PROCEDURE', '<strong>Continue to Step 3</strong>'); 
define('TITLE_CONTINUE_CHECKOUT_PROCEDURE_VIRTUAL', '<strong>Continue to Step 2</strong>'); 
define('TITLE_CONFIRM_CHECKOUT', '<strong>Confirm Your Order</strong>');
define('TITLE_SHIPPING_ADDRESS', 'Shipping Information:'); 

//TEXT
define('TEXT_CHOOSE_SHIPPING_DESTINATION', 'Your order will be shipped to the address at the left or you may change the shipping address by clicking the <em>Change Address</em> button.'); 
define('TEXT_SELECTED_BILLING_DESTINATION', 'Your billing address is shown to the left. The billing address should match the address on your credit card statement. You can change the billing address by clicking the <em>Change Address</em> button.'); 
define('TEXT_YOUR_TOTAL','Your Total'); 
define('TEXT_SELECT_PAYMENT_METHOD', 'Please select a payment method for this order.'); 
define('TEXT_CONTINUE_CHECKOUT_PROCEDURE', "- by clicking 'continue checkout'"); 
define('TEXT_ENTER_SHIPPING_INFORMATION', 'This is currently the only shipping method available to use on this order.'); 
define('TEXT_CONFIRM_CHECKOUT', '- and proceed to processing');
define('TEXT_FIELD_REQUIRED', '<span class="fieldRequired">* Required</span>');
define('TEXT_DROP_DOWN', 'Select an option: ');
define('TEXT_FEC_CHECKBOX', 'Signature Option?');      

//ERROR MESSAGES DISPLAYED
define('ERROR_CONDITIONS_NOT_ACCEPTED', 'Please confirm the terms and conditions bound to this order by ticking the box below.'); 
define('ERROR_NO_PAYMENT_MODULE_SELECTED', 'Please select a payment method for your order.');
// eof