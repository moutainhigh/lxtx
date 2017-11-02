<?php
/**
 * ot_shipping order-total module
 *
 * @package orderTotal
 * @copyright Copyright 2003-2013 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version GIT: $Id: Author: Ian Wilson  Mon Oct 28 17:54:33 2013 +0000 Modified in v1.5.2 $
 */

  define("SHIPFEE_GAP", 20); //the initial shipfee differs for different countries
  define("SHIPFEE_REMOTE_COUNTRIES", 35);
  define("SHIPFEE_FREE_ENTRY", 150); //the entry goods fee for ship-free orders for remote countries

  class ot_shipping {
    var $title, $output;

    function ot_shipping() {
      global $order, $currencies;

      $this->generate_log("order is :" . json_encode($order));
      $this->code = 'ot_shipping';
      $this->title = MODULE_ORDER_TOTAL_SHIPPING_TITLE;
      $this->description = MODULE_ORDER_TOTAL_SHIPPING_DESCRIPTION;
      $this->sort_order = MODULE_ORDER_TOTAL_SHIPPING_SORT_ORDER;
      unset($_SESSION['shipping_tax_description']);
      $this->output = array();
      if (MODULE_ORDER_TOTAL_SHIPPING_FREE_SHIPPING == 'true') {
        switch (MODULE_ORDER_TOTAL_SHIPPING_DESTINATION) {
          case 'national':
            if ($order->delivery['country_id'] == STORE_COUNTRY) $pass = true; break;
          case 'international':
            if ($order->delivery['country_id'] != STORE_COUNTRY) $pass = true; break;
          case 'both':
            $pass = true; break;
          default:
            $pass = false; break;
        }

        if ( ($pass == true) && ( ($order->info['total'] - $order->info['shipping_cost']) >= MODULE_ORDER_TOTAL_SHIPPING_FREE_SHIPPING_OVER) ) {
          $order->info['shipping_method'] = $this->title;
          $order->info['total'] -= $order->info['shipping_cost'];
          $order->info['shipping_cost'] = 0;
        }
      }
      $module = (isset($_SESSION['shipping']) && isset($_SESSION['shipping']['id'])) ? substr($_SESSION['shipping']['id'], 0, strpos($_SESSION['shipping']['id'], '_')) : '';
      if (is_object(($order)) && zen_not_null($order->info['shipping_method'])) {
        if ($GLOBALS[$module]->tax_class > 0) {
          if (!isset($GLOBALS[$module]->tax_basis)) {
            $shipping_tax_basis = STORE_SHIPPING_TAX_BASIS;
          } else {
            $shipping_tax_basis = $GLOBALS[$module]->tax_basis;
          }

          if ($shipping_tax_basis == 'Billing') {
            $shipping_tax = zen_get_tax_rate($GLOBALS[$module]->tax_class, $order->billing['country']['id'], $order->billing['zone_id']);
            $shipping_tax_description = zen_get_tax_description($GLOBALS[$module]->tax_class, $order->billing['country']['id'], $order->billing['zone_id']);
          } elseif ($shipping_tax_basis == 'Shipping') {
            $shipping_tax = zen_get_tax_rate($GLOBALS[$module]->tax_class, $order->delivery['country']['id'], $order->delivery['zone_id']);
            $shipping_tax_description = zen_get_tax_description($GLOBALS[$module]->tax_class, $order->delivery['country']['id'], $order->delivery['zone_id']);
          } else {
            if (STORE_ZONE == $order->billing['zone_id']) {
              $shipping_tax = zen_get_tax_rate($GLOBALS[$module]->tax_class, $order->billing['country']['id'], $order->billing['zone_id']);
              $shipping_tax_description = zen_get_tax_description($GLOBALS[$module]->tax_class, $order->billing['country']['id'], $order->billing['zone_id']);
            } elseif (STORE_ZONE == $order->delivery['zone_id']) {
              $shipping_tax = zen_get_tax_rate($GLOBALS[$module]->tax_class, $order->delivery['country']['id'], $order->delivery['zone_id']);
              $shipping_tax_description = zen_get_tax_description($GLOBALS[$module]->tax_class, $order->delivery['country']['id'], $order->delivery['zone_id']);
            } else {
              $shipping_tax = 0;
            }
          }
          $shipping_tax_amount = zen_calculate_tax($order->info['shipping_cost'], $shipping_tax);
          $order->info['shipping_tax'] += $shipping_tax_amount;
          $order->info['tax'] += $shipping_tax_amount;
          $order->info['tax_groups']["$shipping_tax_description"] += zen_calculate_tax($order->info['shipping_cost'], $shipping_tax);
          $order->info['total'] += zen_calculate_tax($order->info['shipping_cost'], $shipping_tax);
          $_SESSION['shipping_tax_description'] =  $shipping_tax_description;
          $_SESSION['shipping_tax_amount'] =  $shipping_tax_amount;
          if (DISPLAY_PRICE_WITH_TAX == 'true') $order->info['shipping_cost'] += zen_calculate_tax($order->info['shipping_cost'], $shipping_tax);
        }

        if ($_SESSION['shipping']['id'] == 'free_free') {
          $order->info['shipping_method'] = FREE_SHIPPING_TITLE;
          $order->info['shipping_cost'] = 0;
        }

      }
    }

    function process() {
      global $order, $currencies;

      $this->generate_log("order info is:".json_encode($order));
      //add some special operation here
      $this->specialOperationForShippingCost($order);
      $order->info['total'] = $order->info['shipping_cost'] + $order->info['subtotal'];
      $this->output[] = array('title' => $order->info['shipping_method'] . ':',
                              'text' => $currencies->format($order->info['shipping_cost'], true, $order->info['currency'], $order->info['currency_value']),
                              'value' => $order->info['shipping_cost']);  

      $this->generate_log("output is :" . json_encode($this->output));
    }

    function specialOperationForShippingCost(&$order) {
      $country_id = $order->delivery['country_id'];
      if (!$country_id || intval($country_id) == 0) {
        $country_id = $_SESSION['selected_country_id'];
        $this->generate_log("get selected country id from session:" . $country_id);
      }

      if (!$country_id) {
        return;
      }

      $countries_name = zen_get_country_name_var($country_id);
      //echo $countries_name;
      //$this->generate_log("special operations for order.".$country_id.$countries_name);
      if (in_array($countries_name, array("Thailand", "Qatar", "South Africa", "United Arab Emirates"))) {
        $shipping_cost = $order->info['shipping_cost'];
        if ($shipping_cost > 0) {
          $shipping_cost += SHIPFEE_GAP; //add 20 dollars for these countries 
        } else {
          //count the number of goods
          $policy_by_price = zen_ship_mode_by_price();
          if ($policy_by_price) {
            $total_price = 0;
            if ($order->info['subtotal'] < SHIPFEE_FREE_ENTRY) {
              $shipping_cost = SHIPFEE_REMOTE_COUNTRIES; 
            }
          } else {
            $product_count = 0;
            foreach ($order->products as $key => $prod) {
              $product_count += intval($prod['qty']);
            }
            if ($product_count < 3) {
              $shipping_cost = SHIPFEE_REMOTE_COUNTRIES;
            }            
          }
        }

        $order->info['shipping_cost'] = $shipping_cost;
      } 
    }

    function generate_log($msg) {
      file_put_contents("php_payment_log.txt", date("Y-m-d H:i:s")." ".$msg."\n", FILE_APPEND | LOCK_EX);
    }

    function check() {
      global $db;
      if (!isset($this->_check)) {
        $check_query = $db->Execute("select configuration_value from " . TABLE_CONFIGURATION . " where configuration_key = 'MODULE_ORDER_TOTAL_SHIPPING_STATUS'");
        $this->_check = $check_query->RecordCount();
      }
      return $this->_check;
    }

    function keys() {
      return array('MODULE_ORDER_TOTAL_SHIPPING_STATUS', 'MODULE_ORDER_TOTAL_SHIPPING_SORT_ORDER', 'MODULE_ORDER_TOTAL_SHIPPING_FREE_SHIPPING', 'MODULE_ORDER_TOTAL_SHIPPING_FREE_SHIPPING_OVER', 'MODULE_ORDER_TOTAL_SHIPPING_DESTINATION');
    }

    function install() {
      global $db;
      $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('This module is installed', 'MODULE_ORDER_TOTAL_SHIPPING_STATUS', 'true', '', '6', '1','zen_cfg_select_option(array(\'true\'), ', now())");
      $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('Sort Order', 'MODULE_ORDER_TOTAL_SHIPPING_SORT_ORDER', '200', 'Sort order of display.', '6', '2', now())");
      $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('Allow Free Shipping', 'MODULE_ORDER_TOTAL_SHIPPING_FREE_SHIPPING', 'false', 'Do you want to allow free shipping?', '6', '3', 'zen_cfg_select_option(array(\'true\', \'false\'), ', now())");
      $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, use_function, date_added) values ('Free Shipping For Orders Over', 'MODULE_ORDER_TOTAL_SHIPPING_FREE_SHIPPING_OVER', '50', 'Provide free shipping for orders over the set amount.', '6', '4', 'currencies->format', now())");
      $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('Provide Free Shipping For Orders Made', 'MODULE_ORDER_TOTAL_SHIPPING_DESTINATION', 'national', 'Provide free shipping for orders sent to the set destination.', '6', '5', 'zen_cfg_select_option(array(\'national\', \'international\', \'both\'), ', now())");
    }

    function remove() {
      global $db;
      $db->Execute("delete from " . TABLE_CONFIGURATION . " where configuration_key in ('" . implode("', '", $this->keys()) . "')");
    }
  }
