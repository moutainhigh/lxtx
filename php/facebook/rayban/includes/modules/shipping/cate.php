<?php
/**
 * @package shippingMethod
 * @copyright Copyright 2003-2009 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: table.php 14498 2009-10-01 20:16:16Z ajeh $
 */

class cate extends base {
  /**
   * @var unknown_type
   */
  var $code;
  /**
   * @var unknown_type
   */
  var $title;
  /**
   * @var unknown_type
   */
  var $description;
  /**
   * @var unknown_type
   */
  var $icon;
  /**
   * @var unknown_type
   */
  var $enabled;
  /**
   * @return table
   */
  
  function cate() {
    global $order, $db;

    $this->code = 'cate';
    $this->title = MODULE_SHIPPING_SORT_TABLE_TEXT_TITLE;
    $this->description = MODULE_SHIPPING_SORT_TABLE_TEXT_DESCRIPTION;
    $this->sort_order = MODULE_SHIPPING_SORT_TABLE_SORT_ORDER;
    $this->icon = '';
    $this->tax_class = MODULE_SHIPPING_SORT_TABLE_TAX_CLASS;
    $this->tax_basis = MODULE_SHIPPING_SORT_TABLE_TAX_BASIS;
    // disable only when entire cart is free shipping
    if (zen_get_shipping_enabled($this->code)) {
      $this->enabled = ((MODULE_SHIPPING_SORT_TABLE_STATUS == 'True') ? true : false);
    }

    if ( ($this->enabled == true) && ((int)MODULE_SHIPPING_SORT_TABLE_ZONE > 0) ) {
      $check_flag = false;
      $check = $db->Execute("select zone_id from " . TABLE_ZONES_TO_GEO_ZONES . " where geo_zone_id = '" . MODULE_SHIPPING_SORT_TABLE_ZONE . "' and zone_country_id = '" . $order->delivery['country']['id'] . "' order by zone_id");
      while (!$check->EOF) {
        if ($check->fields['zone_id'] < 1) {
          $check_flag = true;
          break;
        } elseif ($check->fields['zone_id'] == $order->delivery['zone_id']) {
          $check_flag = true;
          break;
        }
        $check->MoveNext();
      }

      if ($check_flag == false) {
        $this->enabled = false;
      }
    }
  }
  
  /**
   * @param unknown_type $method
   * @return unknown
   */
  function quote($method = '') {
    global $order, $shipping_weight, $shipping_num_boxes, $total_count;
	
	$items_ary = array();
	$qty_all = 0;
	foreach($_SESSION['cart']->contents as $key => $value) {
		$pid = (int)$key;
		$cpath = zen_get_product_path($pid);
		$cpath_ary = explode('_', $cpath);
		$items_ary[] = array('pid' => $pid, 'cpath' => $cpath_ary, 'qty' => $value['qty']);
		$qty_all += $value['qty'];
	}
	
	$shipping_fee = 0;
	if($qty_all < 10) {//10件以上全免运费
		if(MODULE_SHIPPING_SORT_TABLE_CATEGORY_ONE_IDS != '' && MODULE_SHIPPING_SORT_TABLE_CATEGORY_ONE_COST != '') {
			$table_shipping = $this->table_shipping($items_ary, MODULE_SHIPPING_SORT_TABLE_CATEGORY_ONE_COST, MODULE_SHIPPING_SORT_TABLE_CATEGORY_ONE_IDS);
			$items_ary = $table_shipping['items_ary'];
			$shipping_fee += $table_shipping['shipping'];
			
			if(MODULE_SHIPPING_SORT_TABLE_CATEGORY_TWO_IDS != '' && MODULE_SHIPPING_SORT_TABLE_CATEGORY_TWO_COST != ''){
				$table_shipping = $this->table_shipping($items_ary, MODULE_SHIPPING_SORT_TABLE_CATEGORY_TWO_COST, MODULE_SHIPPING_SORT_TABLE_CATEGORY_TWO_IDS);
				$items_ary = $table_shipping['items_ary'];
				$shipping_fee += $table_shipping['shipping'];
			}
			
				if(MODULE_SHIPPING_SORT_TABLE_CATEGORY_THREE_IDS != '' && MODULE_SHIPPING_SORT_TABLE_CATEGORY_THREE_COST != ''){
					$table_shipping = $this->table_shipping($items_ary, MODULE_SHIPPING_SORT_TABLE_CATEGORY_THREE_COST, MODULE_SHIPPING_SORT_TABLE_CATEGORY_THREE_IDS);
					$items_ary = $table_shipping['items_ary'];
					$shipping_fee += $table_shipping['shipping'];
				}
		}
	
		if($items_ary) {
			$shipping = $this->table_shipping($items_ary, MODULE_SHIPPING_SORT_TABLE_COST, false);
			$shipping_fee += $shipping;
		}
	}

    $this->quotes = array('id' => $this->code,
    'module' => MODULE_SHIPPING_SORT_TABLE_TEXT_TITLE,
    'methods' => array(array('id' => $this->code,
    'title' => MODULE_SHIPPING_SORT_TABLE_TEXT_WAY,
    'cost' => $shipping_fee) ));

    if ($this->tax_class > 0) {
      $this->quotes['tax'] = zen_get_tax_rate($this->tax_class, $order->delivery['country']['id'], $order->delivery['zone_id']);
    }

    if (zen_not_null($this->icon)) $this->quotes['icon'] = zen_image($this->icon, $this->title);

    return $this->quotes;
  }

  /**
   * @get shipping fee
   */
  function table_shipping($items_ary, $cost_rule, $ids_rule) {
	$ids_count = 0;
	
	if($ids_rule) {
	  $ids = explode(',', $ids_rule);
	  foreach($items_ary as $key => $items) {
		$intersect = array_intersect($ids, $items['cpath']);
		if($intersect) {
		  $ids_count += $items['qty'];
		  unset($items_ary[$key]);
		}
	  }
	} else {
	  foreach($items_ary as $items) $ids_count += $items['qty'];
	  $items_ary = false;
	}
	
	$shipping = 0;
	/*if($ids_rule == '267' && $ids_count > 0) {
		if($ids_count <= 5) {
			$shipping = 28 + ($ids_count - 1)*8;
		} else {
			$shipping = 60 + ($ids_count - 5)*5;
		}
	} else {*/
		$cost = preg_split("/[:,]/", $cost_rule);
		$size = sizeof($cost);
		for($i=0; $i<$size; $i+=2) {
		  if($ids_count <= $cost[$i] && $ids_count > 0) { $shipping = $cost[$i+1]; break; }
		}
		if(MODULE_SHIPPING_SORT_TABLE_HANDLING_METHOD == 'Item') $shipping = $shipping * $ids_count;
	//}
	
	if($ids_rule) {
	  $table_shipping = array('items_ary' => $items_ary, 'shipping' => $shipping);
	  return $table_shipping;
	} else {
	  return $shipping;
	}
  }
  
  /**
   * @return unknown
   */
  function check() {
    global $db;
    if (!isset($this->_check)) {
      $check_query = $db->Execute("select configuration_value from " . TABLE_CONFIGURATION . " where configuration_key = 'MODULE_SHIPPING_SORT_TABLE_STATUS'");
      $this->_check = $check_query->RecordCount();
    }
    return $this->_check;
  }

  /**
   * @install function
   */
  function install() {
    global $db;
    $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) VALUES ('Enable Sort Table Method', 'MODULE_SHIPPING_SORT_TABLE_STATUS', 'True', 'Do you want to offer sort table rate shipping?', '6', '0', 'zen_cfg_select_option(array(\'True\', \'False\'), ', now())");
    $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('Shipping Table', 'MODULE_SHIPPING_SORT_TABLE_COST', '25:8.50,50:5.50,10000:0.00', 'The shipping cost is based on the count of the items. Example: 25:8.50,50:5.50,etc.. Up to 25 charge 8.50, from there to 50 charge 5.50, etc', '6', '1', 'zen_cfg_textarea(', now())");

    $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('Categories Id List Level 1', 'MODULE_SHIPPING_SORT_TABLE_CATEGORY_ONE_IDS', '', 'Example: 1,32,98 etc..', '6', '2', '', now())");
    $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('Shipping Table Level 1', 'MODULE_SHIPPING_SORT_TABLE_CATEGORY_ONE_COST', '25:8.50,50:5.50,10000:0.00', '', '6', '3', 'zen_cfg_textarea(', now())");

    $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('Categories Id List Level 2', 'MODULE_SHIPPING_SORT_TABLE_CATEGORY_TWO_IDS', '', '', '6', '4', '', now())");
    $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('Shipping Table Level 2', 'MODULE_SHIPPING_SORT_TABLE_CATEGORY_TWO_COST', '25:8.50,50:5.50,10000:0.00', '', '6', '5', 'zen_cfg_textarea(', now())");

    $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('Categories Id List Level 3', 'MODULE_SHIPPING_SORT_TABLE_CATEGORY_THREE_IDS', '', '', '6', '6', '', now())");
    $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('Shipping Table Level 3', 'MODULE_SHIPPING_SORT_TABLE_CATEGORY_THREE_COST', '25:8.50,50:5.50,10000:0.00', '', '6', '7', 'zen_cfg_textarea(', now())");

    $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('Handling Per Order or Per Item', 'MODULE_SHIPPING_SORT_TABLE_HANDLING_METHOD', 'Order', 'Do you want to charge Handling Fee Per Order or Per Item?', '6', '8', 'zen_cfg_select_option(array(\'Order\', \'Item\'), ', now())");
	$db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, use_function, set_function, date_added) values ('Tax Class', 'MODULE_SHIPPING_SORT_TABLE_TAX_CLASS', '0', 'Use the following tax class on the shipping fee.', '6', '9', 'zen_get_tax_class_title', 'zen_cfg_pull_down_tax_classes(', now())");
    $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, set_function, date_added) values ('Tax Basis', 'MODULE_SHIPPING_SORT_TABLE_TAX_BASIS', 'Shipping', 'On what basis is Shipping Tax calculated. Options are<br />Shipping - Based on customers Shipping Address<br />Billing Based on customers Billing address<br />Store - Based on Store address if Billing/Shipping Zone equals Store zone', '6', '10', 'zen_cfg_select_option(array(\'Shipping\', \'Billing\', \'Store\'), ', now())");
    $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, use_function, set_function, date_added) values ('Shipping Zone', 'MODULE_SHIPPING_SORT_TABLE_ZONE', '0', 'If a zone is selected, only enable this shipping method for that zone.', '6', '11', 'zen_get_zone_class_title', 'zen_cfg_pull_down_zone_classes(', now())");
    $db->Execute("insert into " . TABLE_CONFIGURATION . " (configuration_title, configuration_key, configuration_value, configuration_description, configuration_group_id, sort_order, date_added) values ('Sort Order', 'MODULE_SHIPPING_SORT_TABLE_SORT_ORDER', '0', 'Sort order of display.', '6', '12', now())");
  }

  /**
   * @remove function
   */
    function remove() {
      global $db;
      $db->Execute("delete from " . TABLE_CONFIGURATION . " where configuration_key like 'MODULE\_SHIPPING\_SORT\_TABLE\_%'");
    }

  /**
   * @return unknown
   */
  function keys() {
    return array('MODULE_SHIPPING_SORT_TABLE_STATUS', 'MODULE_SHIPPING_SORT_TABLE_COST', 'MODULE_SHIPPING_SORT_TABLE_CATEGORY_ONE_IDS', 'MODULE_SHIPPING_SORT_TABLE_CATEGORY_ONE_COST', 'MODULE_SHIPPING_SORT_TABLE_CATEGORY_TWO_IDS', 'MODULE_SHIPPING_SORT_TABLE_CATEGORY_TWO_COST', 'MODULE_SHIPPING_SORT_TABLE_CATEGORY_THREE_IDS', 'MODULE_SHIPPING_SORT_TABLE_CATEGORY_THREE_COST', 'MODULE_SHIPPING_SORT_TABLE_HANDLING_METHOD', 'MODULE_SHIPPING_SORT_TABLE_TAX_CLASS', 'MODULE_SHIPPING_SORT_TABLE_TAX_BASIS', 'MODULE_SHIPPING_SORT_TABLE_ZONE', 'MODULE_SHIPPING_SORT_TABLE_SORT_ORDER');
  }
}
?>