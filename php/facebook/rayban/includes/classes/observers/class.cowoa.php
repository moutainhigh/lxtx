<?php
//
// +----------------------------------------------------------------------+
// |zen-cart Open Source E-commerce                                       |
// +----------------------------------------------------------------------+
// | Copyright (c) 2007-2008 Numinix Technology http://www.numinix.com    |
// |                                                                      |
// | Portions Copyright (c) 2003-2006 Zen Cart Development Team           |
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
//  $Id: class.cowoa.php 139 2011-05-24 04:17:04Z numinix $
//
/**
 * Observer class used to redirect to the COWOA page
 *
 */
class COWOAObserver extends base 
{
	function COWOAObserver()
	{
		global $zco_notifier;
		$zco_notifier->attach($this, array('NOTIFY_HEADER_START_CREATE_ACCOUNT'));
    $zco_notifier->attach($this, array('NOTIFY_HEADER_START_EASY_SIGNUP'));
	}
	
	function update(&$class, $eventID, $paramsArray) {
    // check if free/virtual products checkout enabled
    if (FEC_FREE_VIRTUAL_CHECKOUT == 'true') {
      // check if products are virtual
      if ($_SESSION['cart']->get_content_type() == 'virtual') {
        $cart_quantity = $_SESSION['cart']->count_contents();
        // check if products are free
        if ($_SESSION['cart']->in_cart_check('product_is_free','1') == $cart_quantity) {
          // check if COWOA is enabled
          if (FEC_NOACCOUNT_SWITCH == 'true') {
            // redirect to COWOA
            zen_redirect(zen_href_link(FILENAME_NO_ACCOUNT, 'type=free_virtual', 'SSL'));
          }
        }
      }
    }
    if (FEC_NOACCOUNT_ONLY_SWITCH == 'true') {
      $_SESSION['COWOA'] = true; // just set this now to be sure.
      // redirect to ESL
      zen_redirect(zen_href_link(FILENAME_NO_ACCOUNT, '', 'SSL'));
    }
	}
}
// eof