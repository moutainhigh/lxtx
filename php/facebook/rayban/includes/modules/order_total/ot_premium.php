<?php
/**
 * ot_total order-total module
 *
 * @package orderTotal
 * @copyright Copyright 2003-2007 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: ot_subtotal.php 6101 2007-04-01 10:30:22Z wilt $
 */
  class ot_premium {
    var $title, $output;

    function ot_premium() {
      $this->code = 'ot_premium';
      $this->title = MODULE_ORDER_TOTAL_PREMIUM_TITLE;
      $this->description = MODULE_ORDER_TOTAL_PREMIUM_DESCRIPTION;
      $this->sort_order = MODULE_ORDER_TOTAL_PREMIUM_SORT_ORDER;

      $this->output = array();
    }

    function process() {
      global $order, $currencies;
      $insurance_premium = 0;
      $products = $_SESSION['cart']->get_products();
      for ($i=0; $i<count($products); $i++) {
        $insurance_premium += $products[$i]['quantity'] * INSURANCE_PREMIUM;
      }
      $order->info['total'] += $insurance_premium;
      $this->output[] = array(
        'title' => $this->title . ':',
        'text' => $currencies->format($insurance_premium, true, $order->info['currency'], $order->info['currency_value']),
        'value' => $insurance_premium
      );
    }

    function check() {
	  global $db;
      if (!isset($this->_check)) {
        $check_query = $db->Execute("select configuration_value from " . TABLE_CONFIGURATION . " where configuration_key = 'MODULE_ORDER_TOTAL_PREMIUM_STATUS'");
        $this->_check = $check_query->RecordCount();
      }

      return $this->_check;
    }

    function keys() {
      return array('MODULE_ORDER_TOTAL_PREMIUM_STATUS', 'MODULE_ORDER_TOTAL_PREMIUM_SORT_ORDER');
    }

    function install() {
	  global $db;
      $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('This module is installed', 'MODULE_ORDER_TOTAL_PREMIUM_STATUS', 'true', '', '6', '210','zen_cfg_select_option(array(\'true\'), ', now())");
      $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('Sort Order', 'MODULE_ORDER_TOTAL_PREMIUM_SORT_ORDER', '210', 'Sort order of display.', '6', '210', now())");
    }

    function remove() {
	  global $db;
      $db->Execute("delete from " . TABLE_CONFIGURATION . " where configuration_key in ('" . implode("', '", $this->keys()) . "')");
    }
  }
?>