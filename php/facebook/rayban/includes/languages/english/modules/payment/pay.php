<?php
//
// +----------------------------------------------------------------------+
// |zen-cart Open Source E-commerce                                       |
// +----------------------------------------------------------------------+
// | Copyright (c) 2003 The zen-cart developers                           |
// |                                                                      |
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
// | Simplified Chinese version   http://www.zen-cart.cn                  |
// +----------------------------------------------------------------------+
//  $Id: pay.php 002 $
//
  define('MODULE_PAYMENT_PAY_TEXT_ADMIN_TITLE', 'Credit Card');
  define('MODULE_PAYMENT_PAY_TEXT_CATALOG_TITLE', 'Credit Card');
  define('MODULE_PAYMENT_PAY_TEXT_DESCRIPTION', '');

  define('MODULE_PAYMENT_PAY_MARK_BUTTON_IMG', DIR_WS_MODULES . '/payment/pay/pay.png');
  define('MODULE_PAYMENT_PAY_MARK_BUTTON_ALT', 'Credit Card Payment');
  define('MODULE_PAYMENT_PAY_ACCEPTANCE_MARK_TEXT', 'Credit Card Payment');

  define('MODULE_PAYMENT_PAY_TEXT_CATALOG_LOGO', '<img src="' . MODULE_PAYMENT_PAY_MARK_BUTTON_IMG . '" alt="' . MODULE_PAYMENT_PAY_MARK_BUTTON_ALT . '" title="' . MODULE_PAYMENT_PAY_MARK_BUTTON_ALT . '" /> &nbsp;' .  '<span class="smallText">' . MODULE_PAYMENT_PAY_ACCEPTANCE_MARK_TEXT . '</span>');

  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_1_1', 'Enable Pay Module');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_1_2', 'Do you want to accept Pay payments?');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_2_1', 'pay ID');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_2_2', 'pay ID');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_3_1', 'pay key');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_3_2', 'pay key');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_4_1', 'Currency');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_4_2', 'Currency type'); 
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_5_1', 'Encodeing Type');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_5_2', 'Option: MD5 SHA1');
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_6_1', 'Payment Zone');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_6_2', 'If a zone is selected, only enable this payment method for that zone.');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_7_1', 'Set Pending Notification Status');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_7_2', 'Set the status of orders made with this payment module to this value<br />(Processing recommended)');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_8_1', 'Sort order of display');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_8_2', 'Sort order of display. Lowest is displayed first.');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_9_1', 'pay Transaction page URL<br />Default: <code>./pay.php</code><br />');  
  define('MODULE_PAYMENT_PAY_TEXT_CONFIG_9_2', 'pay Transaction page URL');  

?>