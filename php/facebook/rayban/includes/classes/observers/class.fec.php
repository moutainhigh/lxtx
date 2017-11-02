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
//  $Id: class.fec.php 109 2010-04-28 05:47:55Z numinix $
//
/**
 * Observer class used to redirect to the FEC page
 *
 */
class FECObserver extends base 
{
	function FECObserver()
	{
		global $zco_notifier;
		$zco_notifier->attach($this, array('NOTIFY_HEADER_START_CHECKOUT_SHIPPING'));
    $zco_notifier->attach($this, array('NOTIFY_HEADER_END_CHECKOUT_PAYMENT'));
	}
	
	function update(&$class, $eventID, $paramsArray) {
    global $messageStack; 
    if (FEC_STATUS == 'true') {
      $error = false;
      if ($_GET['main_page'] == FILENAME_CHECKOUT_PAYMENT and sizeof($messageStack->messages) > 0) {
        $error = true;
        for ($i=0, $n=sizeof($messageStack->messages); $i<$n; $i++) {
          if ($messageStack->messages[$i]['class'] == 'checkout_payment') {
            $checkout_payment_output[] = $messageStack->messages[$i];
          }
          if ($messageStack->messages[$i]['class'] == 'redemptions') {
            $redemptions_output[] = $messageStack->messages[$i];
          }
        }
        $messageStack->reset();
        if (sizeof($checkout_payment_output) > 0) {
          for ($i=0, $n=sizeof($checkout_payment_output); $i<$n; $i++) {
            $messageStack->add_session('checkout_payment', strip_tags($checkout_payment_output[$i]['text']), 'error');
          }
        }
        if (sizeof($redemptions_output) > 0) {
          for ($i=0, $n=sizeof($redemptions_output); $i<$n; $i++) {
            $messageStack->add_session('redemptions', strip_tags($redemptions_output[$i]['text']), 'caution');
          }
        }
      }  
      if ($_GET['credit_class_error']) {
        $error = true;
        $messageStack->add_session('checkout_payment', htmlspecialchars(urldecode($_GET['credit_class_error'])), 'error');
      }
      if ($error) {
        zen_redirect(zen_href_link(FILENAME_CHECKOUT, "fecaction=null", 'SSL'));
      } else {
        zen_redirect(zen_href_link(FILENAME_CHECKOUT, '', 'SSL'));
      }
    }
	}
}
// eof