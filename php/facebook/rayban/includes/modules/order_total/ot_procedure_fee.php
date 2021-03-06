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
  class ot_procedure_fee {
    var $title, $output;

    function ot_procedure_fee() {
      $this->code = 'ot_procedure_fee';
      $this->title = MODULE_ORDER_TOTAL_PROCEDURE_FEE_TITLE;
      $this->description = MODULE_ORDER_TOTAL_PROCEDURE_FEE_DESCRIPTION;
      $this->sort_order = MODULE_ORDER_TOTAL_PROCEDURE_FEE_SORT_ORDER;
      $this->output = array();
    }

    function process() {
      global $order, $currencies;
      if (!isset($_SESSION['payment'])) return;
      $pf  = 0;  // procedure fee
      $pfr = 0;  // procedure fee rate
      $pfr_name = strtoupper($_SESSION['payment']) . '_PROCEDURE_FEE';
      if (defined($pfr_name)) {
        $pfr = (float)constant($pfr_name);
        $pf = $order->info['total'] * $pfr;
	    $pf = (float)number_format($pf, 2);
        $order->info['total'] += $pf;
	  }
	  
      $this->output[] = array(
	    'title' => $this->title . ':',
        'text'  => $currencies->format($pf, true,  $order->info['currency'], $order->info['currency_value']),
        'value' => $pf
	  );
    }

    function check() {
      global $db;
      if (!isset($this->_check)) {
        $check_query = $db->Execute("select configuration_value from " . TABLE_CONFIGURATION . " where configuration_key = 'MODULE_ORDER_TOTAL_PROCEDURE_FEE_STATUS'");
        $this->_check = $check_query->RecordCount();
      }

      return $this->_check;
    }

    function keys() {
      return array('MODULE_ORDER_TOTAL_PROCEDURE_FEE_STATUS', 'MODULE_ORDER_TOTAL_PROCEDURE_FEE_SORT_ORDER');
    }

    function install() {
      global $db;
      $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('This module is installed', 'MODULE_ORDER_TOTAL_PROCEDURE_FEE_STATUS', 'true', '', '6', '998','zen_cfg_select_option(array(\'true\'), ', now())");
      $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('Sort Order', 'MODULE_ORDER_TOTAL_PROCEDURE_FEE_SORT_ORDER', '998', 'Sort order of display.', '6', '998', now())");
    }

    function remove() {
      global $db;
      $db->Execute("delete from " . TABLE_CONFIGURATION . " where configuration_key in ('" . implode("', '", $this->keys()) . "')");
    }
  }
?>