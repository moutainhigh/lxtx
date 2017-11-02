<?php
/**
 * quick_checkout class
 *
 * @package classes
 * @copyright Copyright 2003-2007 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: quick_checkout.php 88 2009-08-27 21:03:25Z numinix $
 */
 
class quick_checkout {
  
  function collect_posts($post) {
    foreach ($post as $key => $value) {
      $_SESSION[$key] = $value;
    }
  }
  
  function create_posts($session) {
    foreach ($session as $key => $value) {
      $_POST[$key] = $value;
    }
  }
  
}
// eof