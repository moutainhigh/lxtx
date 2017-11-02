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
//  $Id: class.esl.php 88 2009-08-27 21:03:25Z numinix $
//
/**
 * Observer class used to redirect to the Easy Sign-up page
 *
 */
class ESLObserver extends base 
{
	function ESLObserver()
	{
		global $zco_notifier;
		$zco_notifier->attach($this, array('NOTIFY_HEADER_START_CREATE_ACCOUNT'));
	}
	
	function update(&$class, $eventID, $paramsArray) {
    if (FEC_EASY_SIGNUP_STATUS == 'true') {
      // redirect to ESL
      zen_redirect(zen_href_link(FILENAME_LOGIN, '', 'SSL'));
    }
	}
}
// eof